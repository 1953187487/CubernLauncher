package com.movtery.CubernLauncher.feature.download.enums

import com.movtery.CubernLauncher.feature.download.platform.AbstractPlatformHelper
import com.movtery.CubernLauncher.feature.download.platform.curseforge.CurseForgeHelper
import com.movtery.CubernLauncher.feature.download.platform.modrinth.ModrinthHelper

enum class Platform(val pName: String, val helper: AbstractPlatformHelper) {
    MODRINTH("Modrinth", ModrinthHelper()),
    CURSEFORGE("CurseForge", CurseForgeHelper())
}