package com.movtery.cubernlauncher.utils.versioning

/**
 * 版本语义比较工具
 * 采用语义化版本号比较，禁止伪造、跳过或修改版本号
 */
object VersionComparator {

    /**
     * 解析版本字符串为版本号数组
     * 支持格式: major.minor.patch[-classifier]
     * 例如: "1.20.1", "26.2.0-snapshot", "1.19.4+build.5"
     */
    fun parseVersion(versionStr: String): IntArray {
        if (versionStr.isNullOrEmpty()) {
            return intArrayOf(0, 0, 0)
        }
        
        // 移除classifier部分（如-snapshot, -rc1等）
        val cleanVersion = versionStr.split(Regex("[/-+]")).first()
        
        return cleanVersion.split("\\.".toRegex())
            .filter { it.isNotEmpty() }
            .map { it.toIntOrNull() ?: 0 }
            .toIntArray()
    }

    /**
     * 比较两个版本号
     * @return negative if v1 < v2, 0 if v1 == v2, positive if v1 > v2
     */
    fun compareVersions(v1: String, v2: String): Int {
        val arr1 = parseVersion(v1)
        val arr2 = parseVersion(v2)
        
        val maxLen = maxOf(arr1.size, arr2.size)
        for (i in 0 until maxLen) {
            val elem1 = arr1.getOrElse(i) { 0 }
            val elem2 = arr2.getOrElse(i) { 0 }
            if (elem1 != elem2) {
                return elem1 - elem2
            }
        }
        return 0
    }

    /**
     * 验证版本号合法性
     * 禁止伪造、跳过或修改版本号
     */
    fun isValidVersion(versionStr: String): Boolean {
        if (versionStr.isNullOrEmpty()) return false
        
        // 检查是否包含非法字符
        if (versionStr.contains(Regex("[^0-9.a-zA-Z-+]"))) {
            return false
        }
        
        // 版本号不能为空或仅包含特殊字符
        val cleanVersion = versionStr.split(Regex("[/-+]")).first()
        return cleanVersion.isNotEmpty() && cleanVersion.contains(Regex("\\d"))
    }
}
