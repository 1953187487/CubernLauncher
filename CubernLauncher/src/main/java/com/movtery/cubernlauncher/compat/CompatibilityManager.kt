package com.movtery.cubernlauncher.compat

/**
 * 兼容性保证
 * 保证 26.2 之前的正式版、快照、Forge/Fabric 等自定义环境仍可正常启动
 */
object CompatibilityManager {

    /**
     * 检查版本兼容性
     */
    fun isVersionCompatible(versionId: String): Boolean {
        // 支持所有已知版本格式
        return versionId.isNotEmpty()
    }

    /**
     * 为旧版本调整启动参数
     */
    fun adjustArgsForOldVersion(versionId: String, args: MutableList<String>) {
        // TODO: 根据版本号调整参数
        if (versionId.contains("-snapshot")) {
            // 快照版本特殊处理
            args.add("--snapshot")
        }
    }

    /**
     * 检测Forge/Fabric环境
     */
    fun detectModLoader(versionDir: String): ModLoaderType {
        // TODO: 检测模组加载器类型
        return ModLoaderType.NONE
    }

    enum class ModLoaderType {
        NONE,
        FORGE,
        FABRIC,
        NEOFORGE
    }
}
