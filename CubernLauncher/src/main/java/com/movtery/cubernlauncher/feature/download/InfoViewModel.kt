package com.movtery.cubernlauncher.feature.download

import androidx.lifecycle.ViewModel
import com.movtery.cubernlauncher.feature.download.item.InfoItem
import com.movtery.cubernlauncher.feature.download.platform.AbstractPlatformHelper

class InfoViewModel : ViewModel() {
    var platformHelper: AbstractPlatformHelper? = null
    var infoItem: InfoItem? = null
}