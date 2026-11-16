package com.movtery.cubernlauncher.ui.fragment.download.addon

import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.feature.mod.modloader.FabricLikeUtils

class DownloadFabricFragment : DownloadFabricLikeFragment(FabricLikeUtils.FABRIC_UTILS, R.drawable.ic_fabric) {
    companion object {
        const val TAG: String = "DownloadFabricFragment"
    }
}