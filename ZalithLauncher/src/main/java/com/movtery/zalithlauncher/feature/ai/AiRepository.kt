package com.movtery.zalithlauncher.feature.ai

import android.content.Context
import com.movtery.zalithlauncher.feature.log.Logging
import com.movtery.zalithlauncher.utils.path.UrlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSource
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * AI 服务接口层：OpenAI Chat Completions 兼容接口。
 *
 * 提供统一接口，不硬编码单一厂商 SDK，便于用户切换合规服务。
 */
object AiRepository {

    private const val TAG = "AiRepository"
    private val JSON = "application/json; charset=utf-8".toMediaType()

    class AiException(message: String, cause: Throwable? = null) : IOException(message, cause)

    /**
     * 发送一次非流式请求。
     */
    suspend fun complete(
        context: Context,
        messages: List<AiChatMessage>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = AiConfigStore.apiKey(context)
            ?: throw AiException("No API key configured")
        val baseUrl = AiConfigStore.baseUrl(context).trimEnd('/')
        val model = AiConfigStore.model(context)
        val temperature = AiConfigStore.temperature(context)

        val payload = buildPayload(model, messages, temperature, stream = false)
        val request = UrlManager.createRequestBuilder("$baseUrl/chat/completions", payload.toRequestBody(JSON))
            .header("Authorization", "Bearer $apiKey")
            .build()

        UrlManager.createOkHttpClient().newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw AiException("HTTP ${response.code}: ${response.body?.string()?.take(200)}")
            }
            val body = response.body?.string() ?: throw AiException("Empty response body")
            parseContent(body)
        }
    }

    /**
     * 发送流式请求，逐段产出增量文本。
     */
    fun stream(
        context: Context,
        messages: List<AiChatMessage>
    ): Flow<String> = flow {
        val apiKey = AiConfigStore.apiKey(context)
            ?: throw AiException("No API key configured")
        val baseUrl = AiConfigStore.baseUrl(context).trimEnd('/')
        val model = AiConfigStore.model(context)
        val temperature = AiConfigStore.temperature(context)

        val payload = buildPayload(model, messages, temperature, stream = true)
        val request = UrlManager.createRequestBuilder("$baseUrl/chat/completions", payload.toRequestBody(JSON))
            .header("Authorization", "Bearer $apiKey")
            .header("Accept", "text/event-stream")
            .build()

        UrlManager.createOkHttpClient().newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw AiException("HTTP ${response.code}: ${response.body?.string()?.take(200)}")
            }
            val source: BufferedSource = response.body?.source() ?: throw AiException("Empty response body")
            var buffer = StringBuilder()
            while (!source.exhausted()) {
                val line = source.readUtf8Line() ?: break
                buffer.append(line)
                if (line.isBlank()) {
                    val event = buffer.toString().trim()
                    buffer = StringBuilder()
                    if (event.startsWith("data:")) {
                        val data = event.removePrefix("data:").trim()
                        if (data == "[DONE]") break
                        val delta = parseDelta(data)
                        if (delta.isNotEmpty()) emit(delta)
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun buildPayload(
        model: String,
        messages: List<AiChatMessage>,
        temperature: Float,
        stream: Boolean
    ): String {
        val messagesArray = JSONArray()
        messages.forEach { msg ->
            messagesArray.put(JSONObject().put("role", msg.role).put("content", msg.content))
        }
        return JSONObject()
            .put("model", model)
            .put("messages", messagesArray)
            .put("temperature", temperature.toDouble())
            .put("stream", stream)
            .toString()
    }

    private fun parseContent(body: String): String {
        return runCatching {
            val json = JSONObject(body)
            json.getJSONArray("choices").getJSONObject(0)
                .getJSONObject("message")
                .optString("content", "")
        }.getOrElse {
            Logging.w(TAG, "Failed to parse AI response", it)
            throw AiException("Failed to parse response", it)
        }
    }

    private fun parseDelta(data: String): String {
        return runCatching {
            val json = JSONObject(data)
            json.getJSONArray("choices").getJSONObject(0)
                .getJSONObject("delta")
                .optString("content", "")
        }.getOrDefault("")
    }
}
