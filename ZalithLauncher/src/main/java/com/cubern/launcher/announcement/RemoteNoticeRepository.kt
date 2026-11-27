package com.cubern.launcher.announcement

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.cubern.launcher.path.GLOBAL_CLIENT

/** 远程公告（1.0）：来自 Cubern-Announcements 仓库 */
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
    private const val BASE = "https://raw.githubusercontent.com/1953187487/Cubern-Announcements/main/"

    suspend fun fetchNotice(): RemoteNotice? = try {
        val text = GLOBAL_CLIENT.get(BASE + "announcement.json").bodyAsText()
        kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString<RemoteNotice>(text)
    } catch (t: Throwable) { null }

    suspend fun fetchUserNotice(): String? = fetchText("user_notice.md")
    suspend fun fetchPrivacyPolicy(): String? = fetchText("privacy_policy.md")

    private suspend fun fetchText(name: String): String? = try {
        GLOBAL_CLIENT.get(BASE + name).bodyAsText()
    } catch (t: Throwable) { null }
}
