package com.movtery.cubernlauncher.utils.vulkan

import android.content.Context
import android.util.Log

/**
 * Vulkan 支持检测
 *
 * 检测链路：
 * 1. 确认系统提供 Vulkan API（android.graphics.vulkan.VulkanHardwareBuffer / libvulkan.so）
 * 2. 确认存在物理设备
 * 3. 确认 Vulkan 1.2 能力
 * 4. 确认图形队列与表面扩展
 * 5. 确认启动器所需的 Vulkan 原生库存在
 *
 * 任一环节失败都会记录明确原因，供上层提示与安全回退使用。
 */
object VulkanDetector {

    private const val TAG = "CubernVulkan"

    /** Vulkan 1.2 版本号 */
    const val VULKAN_1_2_VERSION: Int = 0x00001002

    /** 启动器运行所需的 Vulkan 原生库 */
    private val REQUIRED_NATIVE_LIBS = listOf("libvulkan.so")

    /** Vulkan 表面扩展，Android 平台必须支持 */
    private val REQUIRED_EXTENSIONS = listOf(
        "VK_KHR_surface",
        "VK_KHR_android_surface"
    )

    /**
     * 完整检测 Vulkan 支持情况
     */
    fun detect(context: Context): VulkanResult {
        val apiAvailable = checkVulkanApiAvailable()
        if (!apiAvailable) {
            return VulkanResult.failure(
                VulkanFailureReason.API_UNAVAILABLE,
                "系统未提供 Vulkan API"
            )
        }

        val nativeLibraries = checkRequiredNativeLibraries()
        if (nativeLibraries.isNotEmpty()) {
            return VulkanResult.failure(
                VulkanFailureReason.NATIVE_LIBRARY_MISSING,
                "缺少 Vulkan 原生库: ${nativeLibraries.joinToString(", ")}"
            )
        }

        val runtime = VulkanRuntime.query()
        if (!runtime.hasPhysicalDevice) {
            return VulkanResult.failure(
                VulkanFailureReason.NO_PHYSICAL_DEVICE,
                "未找到可用的 Vulkan 物理设备"
            )
        }

        if (runtime.apiVersion < VULKAN_1_2_VERSION) {
            return VulkanResult.failure(
                VulkanFailureReason.VERSION_TOO_LOW,
                "Vulkan 版本 ${formatVersion(runtime.apiVersion)} 低于所需的 1.2"
            )
        }

        if (runtime.graphicsQueueFamilyIndex < 0) {
            return VulkanResult.failure(
                VulkanFailureReason.NO_GRAPHICS_QUEUE,
                "物理设备未提供图形队列"
            )
        }

        val missingExtensions = REQUIRED_EXTENSIONS.filterNot { it in runtime.extensions }
        if (missingExtensions.isNotEmpty()) {
            return VulkanResult.failure(
                VulkanFailureReason.MISSING_SURFACE_EXTENSION,
                "缺少表面扩展: ${missingExtensions.joinToString(", ")}"
            )
        }

        return VulkanResult.success(runtime)
    }

    /**
     * 快速判断 Vulkan 是否完全可用
     */
    fun isVulkanSupported(context: Context): Boolean = detect(context).supported

    /**
     * 检查系统是否暴露 Vulkan API
     */
    private fun checkVulkanApiAvailable(): Boolean {
        return try {
            Class.forName("android.graphics.vulkan.Vulkan")
            true
        } catch (throwable: Throwable) {
            Log.d(TAG, "android.graphics.vulkan.Vulkan unavailable", throwable)
            false
        }
    }

    /**
     * 检查启动器所需的 Vulkan 原生库
     */
    private fun checkRequiredNativeLibraries(): List<String> {
        val missing = mutableListOf<String>()
        for (library in REQUIRED_NATIVE_LIBS) {
            if (!SystemLibraryLoader.exists(library)) {
                missing.add(library)
            }
        }
        return missing
    }

    private fun formatVersion(version: Int): String {
        val major = (version shr 22) and 0x7F
        val minor = (version shr 12) and 0x3FF
        val patch = version and 0xFFF
        return "$major.$minor.$patch"
    }
}

/**
 * Vulkan 检测失败原因
 */
enum class VulkanFailureReason {
    API_UNAVAILABLE,
    NATIVE_LIBRARY_MISSING,
    NO_PHYSICAL_DEVICE,
    VERSION_TOO_LOW,
    NO_GRAPHICS_QUEUE,
    MISSING_SURFACE_EXTENSION,
    QUERY_FAILED
}

/**
 * Vulkan 检测结果
 */
data class VulkanResult(
    val supported: Boolean,
    val runtime: VulkanRuntimeInfo?,
    val reason: VulkanFailureReason?,
    val message: String
) {
    companion object {
        fun success(runtime: VulkanRuntimeInfo) = VulkanResult(
            supported = true,
            runtime = runtime,
            reason = null,
            message = "Vulkan ${format(runtime.apiVersion)} 检测通过"
        )

        fun failure(reason: VulkanFailureReason, message: String) = VulkanResult(
            supported = false,
            runtime = null,
            reason = reason,
            message = message
        )

        private fun format(version: Int): String {
            val major = (version shr 22) and 0x7F
            val minor = (version shr 12) and 0x3FF
            val patch = version and 0xFFF
            return "$major.$minor.$patch"
        }
    }
}
