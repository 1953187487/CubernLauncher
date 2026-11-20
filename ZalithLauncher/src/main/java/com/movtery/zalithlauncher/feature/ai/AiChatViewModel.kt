package com.movtery.zalithlauncher.feature.ai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movtery.zalithlauncher.feature.log.Logging
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * AI 助手 ViewModel：UI 层 → ViewModel → Repository。
 */
class AiChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<AiChatMessage>>(emptyList())
    val messages: StateFlow<List<AiChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var generateJob: Job? = null
    private var contextRef: Context? = null

    fun attach(context: Context, welcome: String) {
        contextRef = context.applicationContext
        if (_messages.value.isEmpty()) {
            _messages.value = listOf(AiChatMessage.assistant(welcome))
        } else if (AiConfigStore.isSaveHistory(context)) {
            _messages.value = AiHistoryStore.load(context).ifEmpty {
                listOf(AiChatMessage.assistant(welcome))
            }
        }
    }

    fun send(userText: String) {
        val context = contextRef ?: return
        val text = userText.trim()
        if (text.isEmpty() || _isGenerating.value) return

        if (!AiConfigStore.isEnabled(context)) {
            _error.value = "disabled"
            return
        }
        if (!AiConfigStore.hasApiKey(context)) {
            _error.value = "not_configured"
            return
        }
        if (AiSafety.isSensitive(text)) {
            _error.value = "sensitive"
            return
        }

        val userMessage = AiChatMessage.user(text)
        val assistantMessage = AiChatMessage.assistant("").copy(isStreaming = true)
        _messages.value = _messages.value + userMessage + assistantMessage
        _error.value = null
        _isGenerating.value = true

        val historyLimit = AiConfigStore.maxHistory(context)
        val history = _messages.value
            .filterNot { it.role == AiChatMessage.ROLE_SYSTEM }
            .takeLast(historyLimit * 2)
            .filterNot { it.isStreaming || it.isError }
            .map { AiChatMessage(it.role, it.content) }

        generateJob = viewModelScope.launch {
            try {
                if (AiConfigStore.isStreaming(context)) {
                    streamRequest(context, history)
                } else {
                    val answer = AiRepository.complete(context, history)
                    if (AiSafety.containsPhishingLink(answer)) {
                        appendAnswer("[REDACTED_PHISHING]")
                    } else {
                        appendAnswer(answer)
                    }
                }
            } catch (e: Exception) {
                Logging.w("AiChat", "AI request failed", e)
                markAssistantError()
            } finally {
                _isGenerating.value = false
                persistIfEnabled(context)
            }
        }
    }

    private suspend fun streamRequest(context: Context, history: List<AiChatMessage>) {
        var answer = StringBuilder()
        AiRepository.stream(context, history).collect { delta ->
            answer.append(delta)
            val idx = _messages.value.indexOfLast { it.isStreaming }
            if (idx >= 0) {
                val updated = _messages.value.toMutableList()
                updated[idx] = updated[idx].copy(content = answer.toString())
                _messages.value = updated
            }
        }
        if (AiSafety.containsPhishingLink(answer.toString())) {
            appendAnswer("[REDACTED_PHISHING]")
        }
    }

    private fun appendAnswer(answer: String) {
        val idx = _messages.value.indexOfLast { it.isStreaming }
        if (idx >= 0) {
            val updated = _messages.value.toMutableList()
            updated[idx] = updated[idx].copy(content = answer, isStreaming = false)
            _messages.value = updated
        }
    }

    private fun markAssistantError() {
        val idx = _messages.value.indexOfLast { it.isStreaming }
        if (idx >= 0) {
            val updated = _messages.value.toMutableList()
            updated[idx] = updated[idx].copy(content = "", isStreaming = false, isError = true)
            _messages.value = updated
        }
    }

    private fun persistIfEnabled(context: Context) {
        if (AiConfigStore.isSaveHistory(context)) {
            AiHistoryStore.save(context, _messages.value)
        }
    }

    fun stop() {
        generateJob?.cancel()
        _isGenerating.value = false
    }

    fun clearSession(context: Context) {
        stop()
        _messages.value = listOf(AiChatMessage.assistant(
            context.getString(com.movtery.zalithlauncher.R.string.ai_welcome)
        ))
        AiHistoryStore.clear(context)
        _error.value = null
    }

    fun retryLast(context: Context) {
        val msgs = _messages.value
        val lastUser = msgs.lastOrNull { it.role == AiChatMessage.ROLE_USER } ?: return
        // 移除失败的助手消息后重发
        _messages.value = msgs.filterNot { it.isError }.dropLastWhile { it.role == AiChatMessage.ROLE_ASSISTANT && it.content.isEmpty() }
        send(lastUser.content)
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * 需求 10.9：分析最近一次启动日志。
     * 调用前 UI 必须先向用户弹窗告知并取得确认。
     */
    fun analyzeLog(context: Context) {
        val result = AiLogAnalyzer.analyze()
        val summary = result.getOrElse {
            _error.value = "log_unavailable"
            return
        }
        val prompt = context.getString(
            com.movtery.zalithlauncher.R.string.ai_log_analysis_prompt,
            summary
        )
        send(prompt)
    }
}
