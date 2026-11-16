package com.movtery.cubernlauncher.terracotta

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import com.movtery.cubernlauncher.feature.log.Logging
import com.movtery.cubernlauncher.utils.path.UrlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.URISyntaxException

private const val TAG = "TerracottaNodeList"

/**
 * 陶瓦联机节点列表地址
 * 与 Zalith Launcher 2 保持一致
 */
private const val NODE_LIST_URL = "https://terracotta.glavo.site/nodes"

private class TerracottaNode(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("region")
    val region: String? = null
) {
    fun validate() {
        requireNotNull(url) { "TerracottaNode.url cannot be null" }
        try {
            URI(url)
        } catch (e: URISyntaxException) {
            throw JsonParseException("Invalid URL: $url", e)
        }
    }
}

@Volatile
private var nodeList: List<URI>? = null

private val fetchMutex = Mutex()

/**
 * 获取可用的 EasyTier 节点
 */
suspend fun fetchNodes(): List<URI> {
    nodeList?.let { return it }

    return fetchMutex.withLock {
        nodeList?.let { return it }

        if (TerracottaPrefs.enableCustomNodes) {
            val url = TerracottaPrefs.customNodes
            if (url.isNotBlank()) {
                val tempList = listOf(URI(url))
                nodeList = tempList
                return tempList
            }
        }

        withContext(Dispatchers.IO) {
            fetchNodesFromRemote()
        }.also { result ->
            nodeList = result
        }
    }
}

/**
 * 重置节点缓存，便于切换设置后重新拉取
 */
fun resetNodeCache() {
    nodeList = null
}

private suspend fun fetchNodesFromRemote(): List<URI> {
    return runCatching {
        val json = UrlManager.createOkHttpClient().newCall(
            UrlManager.createRequestBuilder(NODE_LIST_URL).build()
        ).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            response.body?.string() ?: return emptyList()
        }
        val type = object : com.google.gson.reflect.TypeToken<List<TerracottaNode?>>() {}.type
        val nodes: List<TerracottaNode?> = Gson().fromJson(json, type)

        if (nodes.isNullOrEmpty()) {
            Logging.i(TAG, "No available Terracotta nodes found")
            return emptyList()
        }

        nodes.asSequence()
            .mapNotNull { node -> if (node == null) null else parseNode(node) }
            .toList()
    }.onFailure {
        Logging.w(TAG, "Failed to fetch terracotta node list", it)
    }.getOrDefault(emptyList())
}

private fun parseNode(node: TerracottaNode): URI? {
    return try {
        node.validate()

        if (!shouldUseNode(node.region)) {
            return null
        }

        URI.create(node.url)
    } catch (e: Exception) {
        Logging.w(TAG, "Invalid terracotta node: $node", e)
        null
    }
}

private fun shouldUseNode(region: String?): Boolean {
    if (region.isNullOrBlank()) return true

    // 仅限中国大陆地区使用 CN 节点
    val isMainLand = isChinaMainland()
    return isMainLand && region.equals("CN", ignoreCase = true)
}

/**
 * 判断当前是否位于中国大陆
 */
private fun isChinaMainland(): Boolean {
    return try {
        val url = java.net.URL("https://www.gstatic.com/ipranges/country.json")
        val text = url.openStream().bufferedReader().use { it.readText() }
        val json = Gson().fromJson(text, com.google.gson.JsonObject::class.java)
        json?.get("country")?.asString?.uppercase()?.contains("CN") ?: false
    } catch (throwable: Throwable) {
        Logging.w(TAG, "Failed to resolve region", throwable)
        true
    }
}
