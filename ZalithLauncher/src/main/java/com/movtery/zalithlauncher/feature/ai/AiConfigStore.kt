package com.movtery.zalithlauncher.feature.ai

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * AI 服务配置存储。
 *
 * API Key 使用 Android Keystore 加密后保存在 SharedPreferences 中，
 * 不写入 strings.xml，不打印到日志。服务地址与模型名允许用户自由修改。
 */
object AiConfigStore {

    private const val PREFS = "cubern_ai_config"
    private const val KEY_BASE_URL = "base_url"
    private const val KEY_MODEL = "model"
    private const val KEY_API_KEY_CIPHER = "api_key_cipher"
    private const val KEY_API_KEY_IV = "api_key_iv"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_STREAM = "stream"
    private const val KEY_MAX_HISTORY = "max_history"
    private const val KEY_SAVE_HISTORY = "save_history"
    private const val KEY_TEMPERATURE = "temperature"

    private const val KEYSTORE_ALIAS = "cubern_ai_api_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    const val DEFAULT_BASE_URL = "https://api.deepseek.com/v1"
    const val DEFAULT_MODEL = "deepseek-chat"
    const val DEFAULT_MAX_HISTORY = 20
    const val DEFAULT_TEMPERATURE = 0.7f

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, value).apply()
    }

    fun baseUrl(context: Context): String =
        prefs(context).getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL

    fun setBaseUrl(context: Context, value: String) {
        prefs(context).edit().putString(KEY_BASE_URL, value.trim()).apply()
    }

    fun model(context: Context): String =
        prefs(context).getString(KEY_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL

    fun setModel(context: Context, value: String) {
        prefs(context).edit().putString(KEY_MODEL, value.trim()).apply()
    }

    fun isStreaming(context: Context): Boolean = prefs(context).getBoolean(KEY_STREAM, true)

    fun setStreaming(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_STREAM, value).apply()
    }

    fun maxHistory(context: Context): Int =
        prefs(context).getInt(KEY_MAX_HISTORY, DEFAULT_MAX_HISTORY).coerceIn(1, 100)

    fun setMaxHistory(context: Context, value: Int) {
        prefs(context).edit().putInt(KEY_MAX_HISTORY, value.coerceIn(1, 100)).apply()
    }

    fun isSaveHistory(context: Context): Boolean = prefs(context).getBoolean(KEY_SAVE_HISTORY, true)

    fun setSaveHistory(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_SAVE_HISTORY, value).apply()
    }

    fun temperature(context: Context): Float =
        prefs(context).getFloat(KEY_TEMPERATURE, DEFAULT_TEMPERATURE).coerceIn(0f, 2f)

    fun setTemperature(context: Context, value: Float) {
        prefs(context).edit().putFloat(KEY_TEMPERATURE, value.coerceIn(0f, 2f)).apply()
    }

    fun hasApiKey(context: Context): Boolean = apiKey(context) != null

    /**
     * 读取解密后的 API Key。失败时返回 null。
     */
    fun apiKey(context: Context): String? {
        val prefs = prefs(context)
        val cipherText = prefs.getString(KEY_API_KEY_CIPHER, null) ?: return null
        val iv = prefs.getString(KEY_API_KEY_IV, null) ?: return null
        return runCatching {
            val key = getOrCreateKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP)))
            String(cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP)))
        }.getOrNull()
    }

    /**
     * 使用 Android Keystore 加密保存 API Key。
     */
    fun setApiKey(context: Context, value: String) {
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(value.trim().toByteArray(Charsets.UTF_8))
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val cipherText = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        prefs(context).edit()
            .putString(KEY_API_KEY_CIPHER, cipherText)
            .putString(KEY_API_KEY_IV, iv)
            .apply()
    }

    fun clearApiKey(context: Context) {
        prefs(context).edit()
            .remove(KEY_API_KEY_CIPHER)
            .remove(KEY_API_KEY_IV)
            .apply()
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEYSTORE_ALIAS, null) as? SecretKey)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance("AES", ANDROID_KEYSTORE)
        keyGenerator.init(android.security.keystore.KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                android.security.keystore.KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
            .build())
        return keyGenerator.generateKey()
    }
}
