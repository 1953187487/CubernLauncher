package com.movtery.cubernlauncher.feature.download.enums

import com.movtery.cubernlauncher.feature.download.platform.AbstractPlatformHelper
import com.movtery.cubernlauncher.feature.download.platform.curseforge.CurseForgeHelper
import com.movtery.cubernlauncher.feature.download.platform.modrinth.ModrinthHelper

enum class Platform(val pName: String, val helper: AbstractPlatformHelper) {
    MODRINTH("Modrinth", ModrinthHelper()),
    CURSEFORGE("CurseForge", CurseForgeHelper())
}