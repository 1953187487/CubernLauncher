package com.movtery.cubernlauncher.launch

import com.movtery.cubernlauncher.utils.versioning.VersionComparator

/**
 * 启动参数生成器
 * 重构版本JSON解析、启动参数生成流程
 */
object LaunchArgGenerator {

    /**
     * 根据版本信息生成启动参数
     */
    fun generateLaunchArgs(versionInfo: VersionInfo): LaunchArgs {
        val args = LaunchArgs()
        
        // 解析版本JSON
        val versionData = parseVersionJson(versionInfo.jsonPath)
        
        // 生成Java参数
        args.javaArgs = buildJavaArgs(versionData)
        
        // 生成游戏参数
        args.gameArgs = buildGameArgs(versionData)
        
        // 生成库路径
        args.classPath = buildClassPath(versionData.libraries)
        
        return args
    }

    /**
     * 解析版本JSON
     */
    private fun parseVersionJson(jsonPath: String): VersionData {
        // TODO: 实现JSON解析
        return VersionData()
    }

    /**
     * 构建Java参数
     */
    private fun buildJavaArgs(data: VersionData): List<String> {
        val args = mutableListOf<String>()
        args.add("-Xmx${data.javaMemory}M")
        args.add("-Dfile.encoding=UTF-8")
        return args
    }

    /**
     * 构建游戏参数
     */
    private fun buildGameArgs(data: VersionData): List<String> {
        val args = mutableListOf<String>()
        args.add("--version")
        args.add(data.versionId)
        return args
    }

    /**
     * 构建类路径
     */
    private fun buildClassPath(libraries: List<Library>): String {
        return libraries.map { lib -> lib.path }.joinToString(":")
    }
}

data class VersionInfo(
    val versionId: String,
    val jsonPath: String
)

data class VersionData(
    val versionId: String,
    val javaMemory: String = "2048",
    val libraries: List<Library> = emptyList()
)

data class Library(
    val path: String,
    val groupId: String,
    val artifactId: String,
    val version: String
)

data class LaunchArgs(
    var javaArgs: List<String> = emptyList(),
    var gameArgs: List<String> = emptyList(),
    var classPath: String = ""
)
