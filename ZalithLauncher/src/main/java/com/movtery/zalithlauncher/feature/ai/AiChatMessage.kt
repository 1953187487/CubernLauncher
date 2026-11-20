package com.movtery.zalithlauncher.feature.ai

/**
 * AI 对话消息。
 */
data class AiChatMessage(
    val role: String,
    val content: String,
    val isStreaming: Boolean = false,
    val isError: Boolean = false
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
        const val ROLE_SYSTEM = "system"

        fun user(content: String) = AiChatMessage(ROLE_USER, content)
        fun assistant(content: String) = AiChatMessage(ROLE_ASSISTANT, content)
    }
}
