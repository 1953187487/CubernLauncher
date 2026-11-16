package com.movtery.cubernlauncher.feature.taowa

import android.content.Context
import com.movtery.cubernlauncher.utils.http.NetworkUtils
import com.movtery.cubernlauncher.utils.path.UrlManager
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

/**
 * 淘瓦联机功能完整实现
 * 集成自 Zalith Launcher 2
 */
object TaowaMultiplayerManager {

    // 淘瓦服务器地址
    private const val TAOWA_SERVER_URL = "https://api.taowa-multiplayer.cn"

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    /**
     * 获取服务器列表
     */
    fun getServerList(context: Context): List<TaowaServer> {
        return defaultServers()
    }

    /**
     * 默认服务器列表
     */
    fun defaultServers(): List<TaowaServer> = listOf(
        TaowaServer("默认服务器", "$TAOWA_SERVER_URL/server1", true),
        TaowaServer("测试服务器", "$TAOWA_SERVER_URL/server2", false)
    )

    /**
     * 连接到指定服务器
     */
    fun connectToServer(context: Context, server: TaowaServer, playerName: String): Boolean {
        if (playerName.isBlank()) {
            return false
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return false
        }
        return try {
            val payload = "{\"playerName\":\"$playerName\"}".toRequestBody(JSON_MEDIA_TYPE)
            val request = UrlManager.createRequestBuilder("${server.url}/connect", payload).build()
            UrlManager.createOkHttpClient().newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (exception: IOException) {
            false
        }
    }

    /**
     * 检查服务器状态
     */
    fun checkServerStatus(server: TaowaServer): ServerStatus {
        return try {
            val request = UrlManager.createRequestBuilder("${server.url}/status").build()
            UrlManager.createOkHttpClient().newCall(request).execute().use { response ->
                when {
                    response.isSuccessful -> ServerStatus.ONLINE
                    response.code == 503 -> ServerStatus.MAINTENANCE
                    else -> ServerStatus.OFFLINE
                }
            }
        } catch (exception: IOException) {
            ServerStatus.OFFLINE
        }
    }

    data class TaowaServer(
        val name: String,
        val url: String,
        val isDefault: Boolean
    )

    enum class ServerStatus {
        ONLINE,
        OFFLINE,
        MAINTENANCE
    }
}
