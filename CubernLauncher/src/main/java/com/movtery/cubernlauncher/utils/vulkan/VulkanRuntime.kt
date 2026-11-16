package com.movtery.cubernlauncher.utils.vulkan

/**
 * Vulkan 运行时查询
 *
 * 通过反射调用 Android 提供的 Vulkan 入口，收集物理设备、队列与扩展信息。
 * 查询失败时返回保守结果，由调用方给出明确失败原因。
 */
object VulkanRuntime {

    /**
     * 查询 Vulkan 运行时信息
     */
    fun query(): VulkanRuntimeInfo {
        return try {
            queryInternal()
        } catch (throwable: Throwable) {
            VulkanRuntimeInfo(
                apiVersion = 0,
                hasPhysicalDevice = false,
                physicalDeviceName = "",
                graphicsQueueFamilyIndex = -1,
                extensions = emptyList()
            )
        }
    }

    private fun queryInternal(): VulkanRuntimeInfo {
        val vulkanClass = Class.forName("android.graphics.vulkan.Vulkan")
        val getInstance = vulkanClass.getDeclaredMethod("getInstance")
        val instance = getInstance.invoke(null) ?: return emptyRuntime()

        val instanceClass = instance.javaClass
        val getPhysicalDevices = instanceClass.getDeclaredMethod("getPhysicalDevices")
        @Suppress("UNCHECKED_CAST")
        val devices = getPhysicalDevices.invoke(instance) as? List<Any> ?: emptyList()

        val physicalDevice = devices.firstOrNull() ?: return emptyRuntime()

        val physicalDeviceClass = physicalDevice.javaClass
        val apiVersion = readInt(physicalDeviceClass, physicalDevice, "getApiVersion")
        val properties = readObject(physicalDeviceClass, physicalDevice, "getProperties")
        val deviceName = if (properties != null) readString(properties.javaClass, properties, "deviceName") else ""
        val queueFamilies = readObject(physicalDeviceClass, physicalDevice, "getQueueFamilyProperties")
        val graphicsQueue = findGraphicsQueue(queueFamilies)
        val extensions = readDeviceExtensions(physicalDeviceClass, physicalDevice)

        return VulkanRuntimeInfo(
            apiVersion = apiVersion,
            hasPhysicalDevice = true,
            physicalDeviceName = deviceName,
            graphicsQueueFamilyIndex = graphicsQueue,
            extensions = extensions
        )
    }

    private fun emptyRuntime() = VulkanRuntimeInfo(
        apiVersion = 0,
        hasPhysicalDevice = false,
        physicalDeviceName = "",
        graphicsQueueFamilyIndex = -1,
        extensions = emptyList()
    )

    private fun readInt(targetClass: Class<*>, target: Any, methodName: String): Int {
        return try {
            val method = targetClass.getDeclaredMethod(methodName)
            (method.invoke(target) as? Number)?.toInt() ?: 0
        } catch (throwable: Throwable) {
            0
        }
    }

    private fun readObject(targetClass: Class<*>, target: Any, methodName: String): Any? {
        return try {
            val method = targetClass.getDeclaredMethod(methodName)
            method.invoke(target)
        } catch (throwable: Throwable) {
            null
        }
    }

    private fun readString(targetClass: Class<*>, target: Any, methodName: String): String {
        return try {
            val method = targetClass.getDeclaredMethod(methodName)
            method.invoke(target) as? String ?: ""
        } catch (throwable: Throwable) {
            ""
        }
    }

    private fun readDeviceExtensions(targetClass: Class<*>, target: Any): List<String> {
        return try {
            val method = targetClass.getDeclaredMethod("getDeviceExtensions")
            when (val result = method.invoke(target)) {
                is Array<*> -> result.filterIsInstance<String>()
                is List<*> -> result.filterIsInstance<String>()
                else -> emptyList()
            }
        } catch (throwable: Throwable) {
            emptyList()
        }
    }

    /**
     * 在队列族属性中查找图形队列
     */
    private fun findGraphicsQueue(queueFamilies: Any?): Int {
        if (queueFamilies !is Array<*>) return -1
        queueFamilies.forEachIndexed { index, family ->
            val flags = if (family != null) readInt(family.javaClass, family, "queueFlags") else 0
            if (flags and 0x1 == 0x1) {
                return index
            }
        }
        return -1
    }
}
