package com.cubern.launcher.feature.modcheck

import java.io.File
import java.util.zip.ZipFile

/** 模组冲突检测（1.0.3）：重复 modid / 缺失前置。返回冲突描述，供 UI 提示并跳转下载页。 */
object ModConflictChecker {
    data class Conflict(val type: Type, val message: String, val suggestedQuery: String) {
        enum class Type { DUPLICATE_MODID, MISSING_DEPENDENCY, INCOMPATIBLE }
    }

    fun check(modsDir: File): List<Conflict> {
        val results = mutableListOf<Conflict>()
        if (!modsDir.exists()) return results
        val jars = modsDir.listFiles { f -> f.isFile && f.name.endsWith(".jar", true) } ?: return results
        val modIds = mutableMapOf<String, MutableList<String>>()
        val dependencies = mutableMapOf<String, MutableList<String>>()

        jars.forEach { jar ->
            try {
                ZipFile(jar).use { zip ->
                    val fabricEntry = zip.getEntry("fabric.mod.json")
                    if (fabricEntry != null) {
                        val text = zip.getInputStream(fabricEntry).bufferedReader().readText()
                        val id = Regex("\"id\"\s*:\s*\"([^\"]+)\"").find(text)?.groupValues?.get(1)
                        if (id != null) modIds.getOrPut(id) { mutableListOf() }.add(jar.name)
                        val depends = Regex("\"depends\"\s*:\s*\{([^}]*)\}").find(text)?.groupValues?.get(1)
                        if (depends != null) {
                            val deps = Regex("\"([a-z0-9_.-]+)\"\s*:").findAll(depends).map { it.groupValues[1] }.toList()
                            if (deps.isNotEmpty()) dependencies[jar.name] = deps.toMutableList()
                        }
                    }
                    val tomlEntry = zip.getEntry("META-INF/neoforge.mods.toml") ?: zip.getEntry("META-INF/mods.toml")
                    if (tomlEntry != null) {
                        val text = zip.getInputStream(tomlEntry).bufferedReader().readText()
                        Regex("modId\s*=\s*\"([^\"]+)\"").findAll(text).forEach { m ->
                            modIds.getOrPut(m.groupValues[1]) { mutableListOf() }.add(jar.name)
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        modIds.forEach { (id, files) ->
            if (files.size > 1) results.add(Conflict(Conflict.Type.DUPLICATE_MODID, "重复模组 ID [$id]: " + files.joinToString(", "), id))
        }
        dependencies.forEach { (file, deps) ->
            deps.forEach { dep ->
                if (!modIds.containsKey(dep)) results.add(Conflict(Conflict.Type.MISSING_DEPENDENCY, file + " 缺少前置模组 [" + dep + "]", dep))
            }
        }
        return results
    }
}
