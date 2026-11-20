package com.movtery.zalithlauncher.feature.ai

import java.util.regex.Pattern

/**
 * 内容安全过滤与敏感信息屏蔽。
 *
 * - 请求发送前过滤明显敏感内容
 * - 回复中检测钓鱼链接
 * - 日志分析时自动隐藏 Access Token / Player Token / API Key / UUID / 路径用户名
 */
object AiSafety {

    private val SENSITIVE_CONTENT_PATTERNS = listOf(
        Pattern.compile("(?i).*(密码|password|passwd).*(找回|reset|recover).*"),
        Pattern.compile("(?i).*(正版验证|盗版|破解|绕过.*验证).*"),
        Pattern.compile("(?i).*(账号找回|密码操作).*"),
        Pattern.compile("(?i).*(钓鱼|phishing).*"),
        Pattern.compile("(?i).*(银行卡|信用卡|支付密码|转账).*")
    )

    private val PHISHING_PATTERNS = listOf(
        Pattern.compile("(?i)https?://(?!.*(minecraft\\.net|mojang\\.com|microsoft\\.com|github\\.com|deepseek\\.com))[^\\s]+\\.(xyz|top|tk|ml|ga|cf|gq|work|club|online|site|icu|monster|click)(/[^\\s]*)?")
    )

    private val ACCESS_TOKEN = Pattern.compile("(?i)(access[_-]?token|bearer)\\s*[:=]\\s*[A-Za-z0-9._~+/=-]{8,}")
    private val PLAYER_TOKEN = Pattern.compile("(?i)(player[_-]?token|playertoken)\\s*[:=]\\s*[A-Za-z0-9._~+/=-]{8,}")
    private val API_KEY = Pattern.compile("(?i)(api[_-]?key|apikey|authorization)\\s*[:=]\\s*[A-Za-z0-9._~+/=-]{8,}")
    private val UUID = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
    private val AUTH_TOKEN = Pattern.compile("(?i)(token|auth)\\s*[:=]\\s*[A-Za-z0-9._~+/=-]{16,}")

    private val PATH_USERNAME = Pattern.compile("(?i)(/home/|/users/|/data/user/\\d+/|C:\\\\Users\\\\)[^\\\\/\\s]+")

    /**
     * 是否包含明显敏感内容，应拒绝发送。
     */
    fun isSensitive(text: String): Boolean =
        SENSITIVE_CONTENT_PATTERNS.any { it.matcher(text).matches() }

    /**
     * 回复中是否包含可疑钓鱼链接。
     */
    fun containsPhishingLink(text: String): Boolean =
        PHISHING_PATTERNS.any { it.matcher(text).find() }

    /**
     * 日志上传前的敏感信息屏蔽。
     */
    fun maskSensitive(text: String): String {
        var result = text
        result = ACCESS_TOKEN.matcher(result).replaceAll("[REDACTED_ACCESS_TOKEN]")
        result = PLAYER_TOKEN.matcher(result).replaceAll("[REDACTED_PLAYER_TOKEN]")
        result = API_KEY.matcher(result).replaceAll("[REDACTED_API_KEY]")
        result = AUTH_TOKEN.matcher(result).replaceAll("[REDACTED_TOKEN]")
        result = UUID.matcher(result).replaceAll("[REDACTED_UUID]")
        result = PATH_USERNAME.matcher(result).replaceAll("$1[REDACTED_USER]")
        return result
    }
}
