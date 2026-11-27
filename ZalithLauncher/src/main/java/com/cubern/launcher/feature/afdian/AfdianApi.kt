package com.cubern.launcher.feature.afdian

import io.ktor.client.request.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.cubern.launcher.path.GLOBAL_CLIENT

/** 爱发电 Open API：查询赞助者（作为可联机好友列表） */
@Serializable
data class AfdianSponsor(val name: String = "", val amount: String = "")

object AfdianApi {
    const val LOGIN_URL = "https://afdian.com/login"
    private const val USER_ID = "be31ec9a0c0711f192595254001e7c00"
    private const val TOKEN_ENC = "JB0SOjEkDVBkVFkbFS4hFEYSAyAZXUYaJz8AGSgEGDohXAMCXGdhNQ=="
    private const val XOR_KEY = "Cubern@2026#SecureAnnounce"
    private val json = Json { ignoreUnknownKeys = true }

    private fun decrypt(): String {
        val bytes = java.util.Base64.getDecoder().decode(TOKEN_ENC)
        return buildString { for (i in bytes.indices) append((bytes[i].toInt() xor XOR_KEY[i % XOR_KEY.length].code).toChar()) }
    }

    /** 用户是否已登录爱发电（本地标记） */
    fun isLoggedIn(): Boolean = com.cubern.launcher.setting.launcherMMKV().getString("afdian_logged_in", null) == "true"

    fun markLoggedIn() = com.cubern.launcher.setting.launcherMMKV().putString("afdian_logged_in", "true")

    suspend fun querySponsors(): Result<List<AfdianSponsor>> = try {
        val page = 1
        val params = "{\"page\":$page}"
        val ts = (System.currentTimeMillis() / 1000).toString()
        val signSrc = decrypt() + "params" + params + "ts" + ts + "user_id" + USER_ID
        val sign = java.security.MessageDigest.getInstance("MD5").digest(signSrc.toByteArray()).joinToString("") { "%02x".format(it) }
        val body = "user_id=$USER_ID&params=" + java.net.URLEncoder.encode(params, "UTF-8") + "&ts=$ts&sign=$sign"
        val resp = GLOBAL_CLIENT.post("https://afdian.com/api/open/query-sponsor") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(body)
        }.bodyAsText()
        val arr = json.parseToJsonElement(resp).toString()
        val names = Regex("\"name\"\s*:\s*\"([^\"]+)\"").findAll(arr).map { it.groupValues[1] }.toList()
        Result.success(names.map { AfdianSponsor(it) })
    } catch (t: Throwable) { Result.failure(t) }
}
