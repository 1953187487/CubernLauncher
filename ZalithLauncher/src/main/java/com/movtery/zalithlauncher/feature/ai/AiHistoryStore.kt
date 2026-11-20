package com.movtery.zalithlauncher.feature.ai

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * AI 对话历史本地存储。
 *
 * 默认本地保存；用户清空历史后同步删除数据库记录。
 * 历史记录不上传到未经用户确认的第三方服务器。
 */
object AiHistoryStore {

    private const val PREFS = "cubern_ai_history"
    private const val KEY_SESSION = "session"

    fun load(context: Context): MutableList<AiChatMessage> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_SESSION, null) ?: return mutableListOf()
        return runCatching {
            val array = JSONArray(raw)
            val list = mutableListOf<AiChatMessage>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    AiChatMessage(
                        role = obj.optString("role", AiChatMessage.ROLE_ASSISTANT),
                        content = obj.optString("content", ""),
                        isStreaming = false,
                        isError = false
                    )
                )
            }
            list
        }.getOrDefault(mutableListOf())
    }

    fun save(context: Context, messages: List<AiChatMessage>) {
        val array = JSONArray()
        messages.filter { it.content.isNotBlank() && !it.isStreaming }.forEach { msg ->
            array.put(
                JSONObject()
                    .put("role", msg.role)
                    .put("content", msg.content)
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SESSION, array.toString())
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_SESSION)
            .apply()
    }
}
