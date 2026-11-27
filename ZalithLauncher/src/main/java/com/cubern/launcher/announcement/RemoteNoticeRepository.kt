package com.cubern.launcher.announcement

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.cubern.launcher.path.GLOBAL_CLIENT

/** 远程公告（1.0）：来自私有仓库 Cubern-Announcements，Token 经 XOR+Base64 混淆存储 */
@Serializable
data class RemoteNotice(
    @SerialName("version") val version: Int = 0,
    @SerialName("enabled") val enabled: Boolean = false,
    @SerialName("title") val title: String = "",
    @SerialName("content") val content: String = "",
    @SerialName("date") val date: String = "",
    @SerialName("url") val url: String = ""
)

object RemoteNoticeRepository {
    private const val REPO = "1953187487/Cubern-Announcements"
    // XOR + Base64 混淆的只读 Token（运行时解密，仅 contents:read 权限）
    private const val TOKEN_ENC = "JB0SOjEkDVBkVFkbFS4hFEYSAyAZXUYaJz8AGSgEGDohXAMCXGdhNQ=="
    private const val XOR_KEY = "Cubern@2026#SecureAnnounce"
    private const val API = "https://api.github.com/repos/$REPO/contents/"
    private val json = Json { ignoreUnknownKeys = true }

    private fun decryptToken(): String {
        val bytes = java.util.Base64.getDecoder().decode(TOKEN_ENC)
        return buildString {
            for (i in bytes.indices) append((bytes[i].toInt() xor XOR_KEY[i % XOR_KEY.length].code).toChar())
        }
    }

    private suspend fun fetchFile(name: String): String? = try {
        val resp = GLOBAL_CLIENT.get(API + name) {
            header("Authorization", "Bearer " + decryptToken())
            header("Accept", "application/vnd.github+json")
        }
        val obj = org.json.JSONObject(resp.bodyAsText())
        val content = obj.optString("content")
        if (content.isBlank()) return null
        java.util.Base64.getDecoder().decode(content).toString(Charsets.UTF_8)
    } catch (t: Throwable) { null }

    suspend fun fetchNotice(): RemoteNotice? = fetchFile("announcement.json")?.let {
        try { json.decodeFromString<RemoteNotice>(it) } catch (t: Throwable) { null }
    }

    suspend fun fetchUserNotice(): String? = fetchFile("user_notice.md")
    suspend fun fetchPrivacyPolicy(): String? = fetchFile("privacy_policy.md")
}
