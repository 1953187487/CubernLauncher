package com.movtery.cubernlauncher.utils.error

import android.content.Context

/**
 * 错误处理与提示
 * 当设备不支持、库缺失、Java不匹配或参数错误时，明确提示具体原因
 */
object ErrorHandler {

    enum class ErrorCode {
        VULKAN_NOT_SUPPORTED,
        JAVA_VERSION_MISMATCH,
        NATIVE_LIB_MISSING,
        INVALID_ARGS,
        NETWORK_ERROR
    }

    /**
     * 获取错误提示信息
     */
    fun getErrorMessage(context: Context, errorCode: ErrorCode): String {
        return when (errorCode) {
            ErrorCode.VULKAN_NOT_SUPPORTED -> "设备不支持Vulkan渲染，已自动切换到OpenGL模式"
            ErrorCode.JAVA_VERSION_MISMATCH -> "Java运行时版本不匹配，请下载并安装合适的Java版本"
            ErrorCode.NATIVE_LIB_MISSING -> "缺少必要的原生库文件，请检查安装完整性"
            ErrorCode.INVALID_ARGS -> "启动参数错误，请检查游戏版本配置"
            ErrorCode.NETWORK_ERROR -> "网络连接失败，请检查网络设置"
        }
    }

    /**
     * 安全回退到兼容渲染后端
     */
    fun fallbackToCompatibleRenderer(context: Context, errorCode: ErrorCode): String {
        // TODO: 实现自动回退逻辑
        return "OPENGL"
    }
}
