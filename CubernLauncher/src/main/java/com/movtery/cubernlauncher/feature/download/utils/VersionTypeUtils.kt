package com.movtery.cubernlauncher.feature.download.utils

import com.movtery.cubernlauncher.feature.download.enums.VersionType

class VersionTypeUtils {
    companion object {
        fun getVersionType(type: String): VersionType {
            return when (type) {
                "beta", "2" -> VersionType.BETA
                "alpha", "3" -> VersionType.ALPHA
                "release", "1" -> VersionType.RELEASE
                else -> VersionType.RELEASE
            }
        }
    }
}