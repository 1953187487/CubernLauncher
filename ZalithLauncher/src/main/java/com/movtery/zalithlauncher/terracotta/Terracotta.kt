package com.movtery.zalithlauncher.terracotta

import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.movtery.zalithlauncher.feature.log.Logging
import com.movtery.zalithlauncher.utils.path.PathManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.burningtnt.terracotta.TerracottaAndroidAPI
import java.io.IOException
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicReference

private const val TAG = "Terracotta"

/**
 * 陶瓦联机核心控制器
 *
 * 移植自 Zalith Launcher 2 的陶瓦联机模块，
 * 参考实现：https://github.com/FCL-Team/FoldCraftLauncher
 */
object Terracotta {
    const val TERRACOTTA_USER_NOTICE_VERSION = 1

    enum class Mode {
        /** 房主模式 */
        Host,

        /** 房客模式 */
        Guest
    }

    /** 联机事件回调，由 UI 层实现 */
    interface Listener {
        /** 需要申请 VPN 权限 */
        fun onRequestVpnPermission()

        /** 联机状态变化 */
        fun onStateChanged(state: TerracottaState.Ready)
    }

    var initialized = false
        private set

    private var listener: Listener? = null

    private var notificationJob: Job? = null
    private var metadata: TerracottaAndroidAPI.Metadata? = null

    var mode: Mode? = null
        private set

    private val _state = MutableStateFlow<TerracottaState.Ready?>(null)
    val state: StateFlow<TerracottaState.Ready?> = _state.asStateFlow()

    private val stateRef = AtomicReference<TerracottaState.Ready?>(null)

    /**
     * 初始化陶瓦联机
     *
     * @param lifecycleScope 用于运行状态轮询协程
     * @param listener UI 事件回调
     */
    fun initialize(
        lifecycleScope: CoroutineScope,
        listener: Listener
    ) {
        if (initialized) return
        this.listener = listener

        metadata = TerracottaAndroidAPI.initialize(
            PathManager.DIR_TERRACOTTA.absolutePath,
            PathManager.FILE_TERRACOTTA_LOG.absolutePath
        ) {
            this.listener?.onRequestVpnPermission()
            lifecycleScope.startNotificationJob()
        }

        lifecycleScope.launch(Dispatchers.Default) {
            while (true) {
                try {
                    val current = stateRef.get()
                    val index = current?.index ?: -1

                    val stateJson = TerracottaAndroidAPI.getState()
                    val obj = TerracottaState.TerracottaStateGson.fromJson<TerracottaState.Ready>(
                        stateJson,
                        object : TypeToken<TerracottaState.Ready>() {}.type
                    ) ?: throw JsonParseException("Json object cannot be null.")

                    if (obj.index > index) {
                        compareAndSet(current, obj)
                    }
                } catch (throwable: Throwable) {
                    Logging.w(TAG, "Failed to poll terracotta state", throwable)
                }

                delay(100L)
            }
        }

        initialized = true
    }

    fun setWaiting(manual: Boolean) {
        if (!initialized) return

        stopNotificationJob()
        TerracottaAndroidAPI.setWaiting()
        if (manual) {
            Logging.i(TAG, "Terracotta reset to waiting state")
        }
    }

    fun setScanning(room: String?, player: String?, extraNodes: List<String>?) {
        checkInitialized()
        if (_state.value !is TerracottaState.Waiting) {
            throw Exception("reset state to waiting first!")
        }

        mode = Mode.Host
        TerracottaAndroidAPI.setScanning(room, player, extraNodes)
    }

    fun setGuesting(room: String, player: String?, extraNodes: List<String>?): Boolean {
        checkInitialized()
        if (_state.value !is TerracottaState.Waiting) {
            throw Exception("reset state to waiting first!")
        }

        mode = Mode.Guest
        return TerracottaAndroidAPI.setGuesting(room, player, extraNodes)
    }

    fun parseRoomCode(room: String?): TerracottaAndroidAPI.RoomType? {
        if (!initialized || room == null) return null
        return TerracottaAndroidAPI.parseRoomCode(room)
    }

    fun getMetadata(): TerracottaAndroidAPI.Metadata =
        metadata ?: TerracottaAndroidAPI.Metadata("unknown", 0, "unknown")

    fun collectLogs(): String? {
        if (!initialized) return null
        return try {
            TerracottaAndroidAPI.collectLogs().use { reader ->
                val writer = StringWriter()
                val buf = CharArray(4096)
                var n: Int
                while (reader.read(buf).also { n = it } != -1) {
                    writer.write(buf, 0, n)
                }
                writer.toString()
            }
        } catch (e: IOException) {
            Logging.w(TAG, "Failed to collect terracotta logs", e)
            "Failed to collect logs: ${e.message}"
        }
    }

    @Deprecated("This API is exposed for debug purpose.")
    fun testNativePanic() {
        if (!initialized) return
        TerracottaAndroidAPI.panic()
    }

    private fun checkInitialized() {
        if (!initialized) throw Exception("initialize Terracotta first!")
    }

    private fun compareAndSet(previous: TerracottaState.Ready?, next: TerracottaState.Ready) {
        if (stateRef.compareAndSet(previous, next)) {
            _state.value = next
        }
    }

    private fun CoroutineScope.startNotificationJob() {
        notificationJob?.cancel()
        notificationJob = launch(Dispatchers.Default) {
            state.collect { current ->
                if (current != null && current !is TerracottaState.Waiting) {
                    listener?.onStateChanged(current)
                }
            }
        }
    }

    private fun stopNotificationJob() {
        notificationJob?.cancel()
        notificationJob = null
    }
}
