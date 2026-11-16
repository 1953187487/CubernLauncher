package com.movtery.cubernlauncher.utils.dependency

/**
 * 依赖库去重机制
 * 按 groupId:artifactId:version:classifier:abi 进行去重
 */
object DependencyDeduplicator {

    /**
     * 依赖库标识
     */
    data class DependencyKey(
        val groupId: String,
        val artifactId: String,
        val version: String,
        val classifier: String?,
        val abi: String?
    )

    /**
     * 去重依赖库列表
     */
    fun deduplicate(dependencies: List<DependencyKey>): List<DependencyKey> {
        val seen = mutableSetOf<DependencyKey>()
        val result = mutableListOf<DependencyKey>()
        
        for (dep in dependencies) {
            if (seen.add(dep)) {
                result.add(dep)
            }
        }
        
        return result
    }

    /**
     * 生成依赖库唯一标识
     */
    fun createKey(groupId: String, artifactId: String, version: String, 
                  classifier: String? = null, abi: String? = null): DependencyKey {
        return DependencyKey(groupId, artifactId, version, classifier, abi)
    }
}
