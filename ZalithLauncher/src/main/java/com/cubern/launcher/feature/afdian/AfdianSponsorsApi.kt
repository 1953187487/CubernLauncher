package com.cubern.launcher.feature.afdian

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.cubern.launcher.path.GLOBAL_CLIENT
import com.cubern.launcher.sponsor.MonthlySponsors
import com.cubern.launcher.sponsor.Sponsor
import com.cubern.launcher.sponsor.SponsorPlan
import com.cubern.launcher.sponsor.SponsorUser
import java.security.MessageDigest
import java.net.URLEncoder

/** 爱发电 Open API：赞助者列表（Cubern 赞助者功能数据源） */
object AfdianSponsorsApi {
    private const val USER_ID = "be31ec9a0c0711f192595254001e7c00"
    private const val TOKEN_ENC = "MT0bISUZKmtkBlRuLDUvHxEIMQE7J1k7EQU3QQ8oWXc="
    private const val XOR_KEY = "Cubern@2026#AfdianToken"

    private fun token(): String {
        val bytes = java.util.Base64.getDecoder().decode(TOKEN_ENC)
        return buildString { for (i in bytes.indices) append((bytes[i].toInt() xor XOR_KEY[i % XOR_KEY.length].code).toChar()) }
    }

    suspend fun fetchMonthlySponsors(): MonthlySponsors? = try {
        val params = "{\"page\":1}"
        val ts = (System.currentTimeMillis() / 1000).toString()
        val signSrc = token() + "params" + params + "ts" + ts + "user_id" + USER_ID
        val sign = MessageDigest.getInstance("MD5").digest(signSrc.toByteArray()).joinToString("") { "%02x".format(it) }
        val body = "user_id=$USER_ID&params=" + URLEncoder.encode(params, "UTF-8") + "&ts=$ts&sign=$sign"
        val resp = GLOBAL_CLIENT.post("https://afdian.com/api/open/query-sponsor") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(body)
        }.bodyAsText()
        val root = org.json.JSONObject(resp)
        if (root.optInt("ec", 0) != 200) return null
        val list = root.optJSONObject("data")?.optJSONArray("list")
        val sponsors = mutableListOf<Sponsor>()
        if (list != null) for (i in 0 until list.length()) {
            val o = list.getJSONObject(i)
            val user = o.optJSONObject("user")
            val name = user?.optString("name", "") ?: ""
            val avatar = user?.optString("avatar", "") ?: ""
            val amount = o.optDouble("amount", 0.0)
            sponsors.add(
                Sponsor(
                    user = SponsorUser(name.ifBlank { "匿名赞助者" }, avatar),
                    time = "",
                    plan = SponsorPlan("", "爱发电赞助", null, amount, amount, 1)
                )
            )
        }
        val month = java.text.SimpleDateFormat("yyyy-MM").format(java.util.Date())
        MonthlySponsors(month, sponsors, com.cubern.launcher.sponsor.LastMonthSponsors(month, emptyList()))
    } catch (t: Throwable) { null }
}
