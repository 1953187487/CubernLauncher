package com.movtery.cubernlauncher.utils.vulkan

/**
 * Vulkan 运行时信息
 */
data class VulkanRuntimeInfo(
    val apiVersion: Int,
    val hasPhysicalDevice: Boolean,
    val physicalDeviceName: String,
    val graphicsQueueFamilyIndex: Int,
    val extensions: List<String>
)
