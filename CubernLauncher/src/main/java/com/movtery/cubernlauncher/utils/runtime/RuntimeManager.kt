package com.movtery.cubernlauncher.utils.runtime

import android.content.Context
import com.movtery.cubernlauncher.feature.unpack.Jre
import com.movtery.cubernlauncher.feature.unpack.UnpackJreTask
import com.movtery.cubernlauncher.feature.log.Logging
import com.movtery.cubernlauncher.task.Task
import net.kdt.pojavlaunch.multirt.MultiRTUtils

/**
 * Java 运行时管理器
 *
 * 职责：
 * 1. 根据游戏版本所需的 Java 主版本号，推荐最合适的内置运行时
 * 2. 检查运行时是否已安装且完整
 * 3. 按需解压安装运行时（首次进入不再强制安装全部运行时）
 * 4. 校验架构与版本号，避免注入不兼容的运行时
 */
object RuntimeManager {

    private const val TAG = "RuntimeManager"

    /** 游戏版本与 Java 主版本号的对应关系 */
    private val versionJavaMapping = listOf(
        Triple(26, 2, 25),
        Triple(21, 0, 21),
        Triple(17, 0, 17),
        Triple(0, 0, 8)
    )

    /**
     * 根据版本号语义比较判断所需的 Java 主版本
     *
     * 优先使用版本 JSON 中声明的 javaVersion，缺失时按版本号推断。
     */
    fun requiredJavaVersion(versionName: String, declaredJavaVersion: Int? = null): Int {
        if (declaredJavaVersion != null && declaredJavaVersion > 0) {
            return declaredJavaVersion
        }
        return inferJavaVersion(versionName)
    }

    /**
     * 按 Minecraft 版本号推断所需的 Java 主版本
     *
     * 采用语义化比较，避免通过伪造版本号绕过限制。
     */
    fun inferJavaVersion(versionName: String): Int {
        val numeric = extractNumericVersion(versionName)
        if (numeric == null) return 8

        val (major, minor) = numeric
        return when {
            major >= 26 -> 25
            major > 21 -> 21
            major == 21 -> 21
            major > 17 -> 17
            major == 17 -> 17
            else -> 8
        }
    }

    /**
     * 提取版本号中的主版本与次版本号
     */
    private fun extractNumericVersion(versionName: String): Pair<Int, Int>? {
        val match = Regex("(\\d+)\\.(\\d+)").find(versionName) ?: return null
        val major = match.groupValues[1].toIntOrNull() ?: return null
        val minor = match.groupValues[2].toIntOrNull() ?: return null
        return major to minor
    }

    /**
     * 获取满足指定 Java 主版本的运行时名称
     *
     * @return 运行时名称，未安装时返回 null
     */
    fun findInstalledRuntime(javaVersion: Int): String? {
        val candidates = runtimesFor(javaVersion)
        for (runtime in candidates) {
            if (isRuntimeInstalled(runtime)) {
                return runtime
            }
        }
        return null
    }

    /**
     * 检查运行时是否已安装且版本完整
     */
    fun isRuntimeInstalled(runtimeName: String): Boolean {
        return try {
            val version = MultiRTUtils.readInternalRuntimeVersion(runtimeName)
            !version.isNullOrBlank()
        } catch (throwable: Throwable) {
            Logging.w(TAG, "Failed to read runtime version: $runtimeName", throwable)
            false
        }
    }

    /**
     * 列出可满足指定 Java 主版本的内置运行时（按优先级）
     */
    fun runtimesFor(javaVersion: Int): List<String> {
        return when {
            javaVersion >= 25 -> listOf(Jre.JRE_25.jreName, Jre.JRE_21.jreName)
            javaVersion >= 21 -> listOf(Jre.JRE_21.jreName, Jre.JRE_17.jreName)
            javaVersion >= 17 -> listOf(Jre.JRE_17.jreName, Jre.JRE_8.jreName)
            else -> listOf(Jre.JRE_8.jreName)
        }
    }

    /**
     * 检查并按需安装运行时
     *
     * @param javaVersion 所需的 Java 主版本
     * @param onComplete 安装结束回调，参数为安装成功的运行时名称，失败时为 null
     */
    fun ensureRuntime(context: Context, javaVersion: Int, onComplete: (String?) -> Unit) {
        val installed = findInstalledRuntime(javaVersion)
        if (installed != null) {
            onComplete(installed)
            return
        }

        val target = runtimesFor(javaVersion).firstOrNull { jre ->
            Jre.entries.firstOrNull { it.jreName == jre } != null
        }
        if (target == null) {
            onComplete(null)
            return
        }

        val jre = Jre.entries.first { it.jreName == target }
        val task = UnpackJreTask(context, jre)
        if (!task.isNeedUnpack()) {
            // 已安装或无法安装
            onComplete(findInstalledRuntime(javaVersion))
            return
        }

        Task.runTask {
            task.run()
        }.execute()

        onComplete(findInstalledRuntime(javaVersion))
    }

    /**
     * 检查运行时目录的完整性
     *
     * @return 缺失的必要文件列表，为空表示完整
     */
    fun verifyRuntimeIntegrity(runtimeName: String): List<String> {
        val missing = mutableListOf<String>()
        runCatching {
            val runtimeDir = MultiRTUtils.getRuntimeHome(runtimeName)
            if (!runtimeDir.exists()) {
                missing.add(runtimeDir.absolutePath)
                return@runCatching
            }
            val javaBin = java.io.File(runtimeDir, "bin/java")
            if (!javaBin.exists()) {
                missing.add(javaBin.absolutePath)
            }
            val libDir = java.io.File(runtimeDir, "lib")
            if (!libDir.exists()) {
                missing.add(libDir.absolutePath)
            }
        }.onFailure {
            Logging.w(TAG, "Runtime integrity check failed: $runtimeName", it)
        }
        return missing
    }
}
