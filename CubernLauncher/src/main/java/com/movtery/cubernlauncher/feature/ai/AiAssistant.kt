package com.movtery.cubernlauncher.feature.ai

import android.content.Context
import com.movtery.cubernlauncher.feature.log.Logging
import com.movtery.cubernlauncher.feature.version.VersionsManager
import com.movtery.cubernlauncher.utils.path.UrlManager
import com.movtery.cubernlauncher.utils.runtime.RuntimeManager
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

/**
 * AI 版本管理助手
 *
 * 设计思路参考：
 * - Amethyst-iOS：使用大模型进行版本配置的自然语言解析
 * - MyRemasteredAI：启动器内置 AI 助手，统一管理版本与环境
 *
 * 离线能力：
 * - 无需配置 API Key 也能完成版本体检、运行时匹配与清理建议
 * - 配置用户自有 API Key 后可进一步获得自然语言问答能力
 */
object AiAssistant {

    private const val TAG = "AiAssistant"

    /**
     * 用户自备的 API Key（由使用者自行提供，不写入代码）
     */
    private const val USER_LLM_API_KEY = "USER_LLM_API_KEY"

    /**
     * 用户自备的 API 地址
     */
    private const val USER_LLM_BASE_URL = "USER_LLM_BASE_URL"

    /**
     * 版本健康检查结果
     */
    data class VersionHealthReport(
        val versionName: String,
        val requiredJavaVersion: Int,
        val matchedRuntime: String?,
        val issues: List<String>,
        val suggestions: List<String>
    ) {
        val isHealthy: Boolean get() = issues.isEmpty()
    }

    /**
     * 对指定版本进行体检
     */
    fun inspectVersion(versionName: String): VersionHealthReport {
        val issues = mutableListOf<String>()
        val suggestions = mutableListOf<String>()

        val requiredJava = RuntimeManager.inferJavaVersion(versionName)
        val matched = RuntimeManager.findInstalledRuntime(requiredJava)

        if (matched == null) {
            issues.add("未找到兼容 Java $requiredJava 的运行时")
            suggestions.add("启动游戏时将自动准备所需运行时，也可手动在设置中指定")
        } else {
            val missing = RuntimeManager.verifyRuntimeIntegrity(matched)
            if (missing.isNotEmpty()) {
                issues.add("运行时文件不完整：${missing.joinToString(", ")}")
                suggestions.add("在设置中重装该运行时以修复缺失文件")
            }
        }

        if (RuntimeManager.requiredJavaVersion(versionName) != requiredJava) {
            suggestions.add("该版本的版本号与版本清单声明的 Java 版本存在差异，已按语义比较结果选择运行时")
        }

        return VersionHealthReport(
            versionName = versionName,
            requiredJavaVersion = requiredJava,
            matchedRuntime = matched,
            issues = issues,
            suggestions = suggestions
        )
    }

    /**
     * 批量体检所有已安装版本
     */
    fun inspectAllVersions(): List<VersionHealthReport> {
        return VersionsManager.getVersions().map { version ->
            inspectVersion(version.getVersionName())
        }
    }

    /**
     * 生成版本管理建议
     */
    fun buildManagementAdvice(): List<String> {
        val advice = mutableListOf<String>()
        val versions = VersionsManager.getVersions()

        if (versions.isEmpty()) {
            advice.add("当前没有已安装的游戏版本，可前往下载页安装一个正式版或快照。")
            return advice
        }

        val names = versions.map { it.getVersionName() }
        advice.add("已安装 ${names.size} 个版本：${names.joinToString(", ")}")

        val javaRequirements = names.groupBy { RuntimeManager.inferJavaVersion(it) }
        javaRequirements.forEach { (javaVersion, group) ->
            advice.add("需要 Java $javaVersion 的版本：${group.joinToString(", ")}")
        }

        val unused = javaRequirements.keys.filter { RuntimeManager.findInstalledRuntime(it) == null }
        if (unused.isNotEmpty()) {
            advice.add("以下 Java 版本尚未安装对应运行时：${unused.joinToString(", ")}，启动相关版本时会自动准备。")
        }

        return advice
    }

    /**
     * 检查用户是否配置了自有 API Key
     */
    fun hasUserApiKey(): Boolean {
        return !userApiKey().isNullOrBlank()
    }

    /**
     * 获取用户配置的 API Key
     *
     * 仅读取项目自有环境变量，不访问平台内部凭据。
     */
    private fun userApiKey(): String? {
        return try {
            val process = ProcessBuilder(
                "sh", "-c", "printf '%s' \"\$$USER_LLM_API_KEY\""
            ).redirectErrorStream(true).start()
            val value = process.inputStream.bufferedReader().use { it.readText() }.trim()
            process.waitFor()
            value.ifBlank { null }
        } catch (throwable: Throwable) {
            Logging.w(TAG, "Failed to read user API key", throwable)
            null
        }
    }

    /**
     * 提交一次自然语言请求（需要用户自备 API Key）
     */
    fun ask(context: Context, question: String, onResult: (String?) -> Unit) {
        val apiKey = userApiKey()
        if (apiKey.isNullOrBlank()) {
            onResult(null)
            return
        }

        val baseUrl = System.getenv(USER_LLM_BASE_URL) ?: "https://api.deepseek.com/v1"
        val payload = buildString {
            append("{\"model\":\"deepseek-chat\",\"messages\":[")
            append("{\"role\":\"system\",\"content\":\"你是 Minecraft Java 版启动器的版本管理助手，回答要简洁、可执行。\"},")
            append("{\"role\":\"user\",\"content\":\"")
            append(question.replace("\\", "\\\\").replace("\"", "\\\""))
            append("\"}]}")
        }

        Thread {
            var answer: String? = null
            try {
                val builder = UrlManager.createRequestBuilder(
                    "$baseUrl/chat/completions",
                    payload.toRequestBody("application/json; charset=utf-8".toMediaType())
                )
                builder.header("Authorization", "Bearer $apiKey")
                val request = builder.build()

                UrlManager.createOkHttpClient().newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        answer = response.body?.string()
                    }
                }
            } catch (exception: IOException) {
                Logging.w(TAG, "AI request failed", exception)
            }
            onResult(answer)
        }.start()
    }
}
