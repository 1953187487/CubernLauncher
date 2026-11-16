package com.movtery.cubernlauncher.ui.fragment.download.addon

import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.feature.mod.modloader.FabricLikeUtils

class DownloadQuiltFragment : DownloadFabricLikeFragment(FabricLikeUtils.QUILT_UTILS, R.drawable.ic_quilt) {
    companion object {
        const val TAG: String = "DownloadQuiltFragment"
    }
}