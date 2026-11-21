package com.movtery.cubernlauncher.feature.afdian

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest

/** 爱发电 Open API 对接（1.0.5）。凭据从远程配置拉取，token 仅本地签名。 */
object AfdianApi {
    const val CONFIG_URL = "https://raw.githubusercontent.com/1953187487/CubernLauncher/main/afdian_config.json"
    const val FALLBACK_URL = "https://afdian.com/@KZjewnk"

    data class Credentials(val userId: String, val token: String, val url: String = FALLBACK_URL)
    data class Sponsor(val name: String, val avatar: String, val amount: String, val month: Int, val remark: String)

    @Volatile private var cached: Credentials? = null

    suspend fun getCredentials(): Credentials? = withContext(Dispatchers.IO) {
        cached?.let { return@withContext it }
        try {
            val c = URL(CONFIG_URL).openConnection() as HttpURLConnection
            c.connectTimeout = 8000; c.readTimeout = 8000
            val json = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
            val cred = Credentials(json.optString("user_id",""), json.optString("token",""), json.optString("url", FALLBACK_URL))
            if (cred.userId.isNotBlank() && cred.token.isNotBlank()) { cached = cred; cred } else null
        } catch (e: Exception) { null }
    }

    private fun md5(s: String): String =
        MessageDigest.getInstance("MD5").digest(s.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

    suspend fun querySponsors(page: Int = 1): Result<List<Sponsor>> = withContext(Dispatchers.IO) {
        try {
            val c = getCredentials() ?: return@withContext Result.failure(Exception("未获取到爱发电配置"))
            val params = JSONObject().put("page", page).toString()
            val ts = (System.currentTimeMillis() / 1000).toString()
            val sign = md5(c.token + "params" + params + "ts" + ts + "user_id" + c.userId)
            val body = "user_id=" + c.userId + "&params=" + URLEncoder.encode(params,"UTF-8") + "&ts=" + ts + "&sign=" + sign
            val conn = URL("https://afdian.com/api/open/query-sponsor").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"; conn.connectTimeout = 8000; conn.readTimeout = 8000
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded")
            conn.setRequestProperty("User-Agent","CubernLauncher/1.0.5"); conn.doOutput = true
            OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(body) }
            val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
            if (json.optInt("ec",-1) != 200) return@withContext Result.failure(Exception(json.optString("em","查询失败")))
            val arr = json.getJSONObject("data").optJSONArray("list")
            val out = mutableListOf<Sponsor>()
            if (arr != null) for (i in 0 until arr.length()) {
                val it = arr.getJSONObject(i)
                val user = it.optJSONObject("user")
                val name = it.optString("name", user?.optString("name", "匿名赞助者") ?: "匿名赞助者")
                val avatar = it.optString("avatar", user?.optString("avatar", "") ?: "")
                out.add(Sponsor(name, avatar, it.optString("show_amount", it.optString("total_amount","")), it.optInt("month",0), it.optString("remark","")))
            }
            Result.success(out)
        } catch (e: Exception) { Result.failure(e) }
    }
}
