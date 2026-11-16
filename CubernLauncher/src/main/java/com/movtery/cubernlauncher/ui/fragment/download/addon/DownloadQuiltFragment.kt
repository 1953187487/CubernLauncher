package com.movtery.CubernLauncher.ui.fragment.download.addon

import com.movtery.CubernLauncher.R
import com.movtery.CubernLauncher.feature.mod.modloader.FabricLikeUtils

class DownloadQuiltFragment : DownloadFabricLikeFragment(FabricLikeUtils.QUILT_UTILS, R.drawable.ic_quilt) {
    companion object {
        const val TAG: String = "DownloadQuiltFragment"
    }
}