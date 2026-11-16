package com.movtery.cubernlauncher.feature.download.enums

import com.movtery.cubernlauncher.InfoDistributor
import com.movtery.cubernlauncher.feature.download.platform.AbstractPlatformHelper
import com.movtery.cubernlauncher.feature.download.platform.curseforge.CurseForgeHelper
import com.movtery.cubernlauncher.feature.download.platform.modrinth.ModrinthHelper

/**
 * 下载平台
 *
 * CurseForge 需要有效的 API Key 才能使用，未配置密钥时该平台会被自动隐藏，
 * 下载界面将只保留 Modrinth，避免搜索请求全部失败。
 */
enum class Platform(val pName: String, val helper: AbstractPlatformHelper) {
    MODRINTH("Modrinth", ModrinthHelper()),
    CURSEFORGE("CurseForge", CurseForgeHelper());

    companion object {
        /**
         * CurseForge 密钥是否可用
         */
        @JvmStatic
        fun isCurseForgeAvailable(): Boolean {
            val key = try {
                InfoDistributor.CURSEFORGE_API_KEY
            } catch (throwable: Throwable) {
                return false
            }
            return !key.isNullOrBlank() && key != "DUMMY"
        }

        /**
         * 当前可用的下载平台列表
         */
        @JvmStatic
        fun availablePlatforms(): List<Platform> {
            return if (isCurseForgeAvailable()) entries.toList() else listOf(MODRINTH)
        }

        /**
         * 默认推荐的下载平台
         */
        @JvmStatic
        fun recommendedPlatform(): Platform {
            return if (isCurseForgeAvailable()) CURSEFORGE else MODRINTH
        }
    }
}
