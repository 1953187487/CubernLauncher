package com.movtery.CubernLauncher.ui.fragment.download.addon

import com.movtery.CubernLauncher.R
import com.movtery.CubernLauncher.feature.mod.modloader.FabricLikeUtils

class DownloadFabricFragment : DownloadFabricLikeFragment(FabricLikeUtils.FABRIC_UTILS, R.drawable.ic_fabric) {
    companion object {
        const val TAG: String = "DownloadFabricFragment"
    }
}