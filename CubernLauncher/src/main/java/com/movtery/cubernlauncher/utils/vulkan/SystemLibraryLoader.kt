package com.movtery.cubernlauncher.utils.vulkan

import android.util.Log

/**
 * 系统原生库加载器
 *
 * 仅在必要时按优先级搜索系统原生库，避免无差别加载。
 * 搜索顺序：
 * 1. 应用自身 nativeLibraryDir（与当前 ABI 匹配）
 * 2. Android 系统库目录（受限路径）
 */
object SystemLibraryLoader {

    private const val TAG = "CubernNativeLib"

    /** Android 系统原生库目录 */
    private val SYSTEM_LIBRARY_PATHS = listOf("/system/lib64", "/system/lib")

    /** 应用自身的原生库目录缓存 */
    private var applicationLibraryDirectory: String? = null

    /**
     * 设置应用原生库目录，由 Application 初始化时注入
     */
    fun setApplicationLibraryDirectory(path: String?) {
        applicationLibraryDirectory = path
    }

    /**
     * 检查指定原生库是否存在于受控搜索路径中
     */
    fun exists(libraryName: String): Boolean = resolve(libraryName) != null

    /**
     * 按优先级解析原生库路径
     */
    fun resolve(libraryName: String): String? {
        val fileName = if (libraryName.startsWith("lib")) libraryName else "lib$libraryName.so"

        applicationLibraryDirectory?.let { directory ->
            val candidate = "$directory/$fileName"
            if (java.io.File(candidate).exists()) {
                return candidate
            }
        }

        for (directory in SYSTEM_LIBRARY_PATHS) {
            val candidate = "$directory/$fileName"
            if (java.io.File(candidate).exists()) {
                return candidate
            }
        }

        Log.d(TAG, "native library not found in constrained paths: $fileName")
        return null
    }
}
