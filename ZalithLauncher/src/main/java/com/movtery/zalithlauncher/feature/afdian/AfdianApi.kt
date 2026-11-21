package com.movtery.zalithlauncher.feature.afdian

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest

/**
 * 爱发电（Afdian）Open API 对接（1.0.5）。
 * 凭据从远程配置拉取，不硬编码在 APK 中；token 仅参与本地签名，不明文发送。
 * 签名：MD5(token + "params" + paramsJson + "ts" + ts + "user_id" + user_id)
 */
object AfdianApi {
    const val CONFIG_URL = "https://raw.githubusercontent.com/1953187487/CubernLauncher/main/afdian_config.json"
    const val FALLBACK_URL = "https://afdian.com/@KZjewnk"

    data class Credentials(val userId: String, val token: String, val url: String = FALLBACK_URL)
    data class Sponsor(val name: String, val avatar: String, val amount: String, val month: Int, val remark: String)

    @Volatile private var cachedCredentials: Credentials? = null

    suspend fun getCredentials(): Credentials? = withContext(Dispatchers.IO) {
        cachedCredentials?.let { return@withContext it }
        try {
            val conn = URL(CONFIG_URL).openConnection() as HttpURLConnection
            conn.connectTimeout = 8000; conn.readTimeout = 8000
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(text)
            val c = Credentials(json.optString("user_id",""), json.optString("token",""), json.optString("url", FALLBACK_URL))
            if (c.userId.isNotBlank() && c.token.isNotBlank()) { cachedCredentials = c; c } else null
        } catch (e: Exception) { null }
    }

    private fun md5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

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
            conn.setRequestProperty("User-Agent","CubernLauncher/1.0.5")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(body) }
            val resp = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(resp)
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

    // ===== 1.0.7：赞助者每日 0 点自动刷新 + 本地持久化缓存 =====
    private const val PREF = "cubern_afdian"
    private const val KEY_SPONSORS = "sponsors_json"
    private const val KEY_REFRESH_TIME = "refresh_time"

    fun getCachedSponsors(context: android.content.Context): List<Sponsor> {
        val sp = context.getSharedPreferences(PREF, android.content.Context.MODE_PRIVATE)
        val json = sp.getString(KEY_SPONSORS, "[]") ?: "[]"
        val out = mutableListOf<Sponsor>()
        try {
            val arr = org.json.JSONArray(json)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(Sponsor(o.optString("name",""), o.optString("avatar",""), o.optString("amount",""), o.optInt("month",0), o.optString("remark","")))
            }
        } catch (_: Exception) {}
        return out
    }

    fun saveSponsors(context: android.content.Context, list: List<Sponsor>) {
        val arr = org.json.JSONArray()
        list.forEach { s -> arr.put(org.json.JSONObject().put("name", s.name).put("avatar", s.avatar).put("amount", s.amount).put("month", s.month).put("remark", s.remark)) }
        val sp = context.getSharedPreferences(PREF, android.content.Context.MODE_PRIVATE)
        sp.edit().putString(KEY_SPONSORS, arr.toString()).putLong(KEY_REFRESH_TIME, System.currentTimeMillis()).apply()
    }

    fun getLastRefreshTime(context: android.content.Context): Long =
        context.getSharedPreferences(PREF, android.content.Context.MODE_PRIVATE).getLong(KEY_REFRESH_TIME, 0L)
}
