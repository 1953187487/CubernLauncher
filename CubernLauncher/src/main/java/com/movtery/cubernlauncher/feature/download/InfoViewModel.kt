package com.movtery.CubernLauncher.feature.download

import androidx.lifecycle.ViewModel
import com.movtery.CubernLauncher.feature.download.item.InfoItem
import com.movtery.CubernLauncher.feature.download.platform.AbstractPlatformHelper

class InfoViewModel : ViewModel() {
    var platformHelper: AbstractPlatformHelper? = null
    var infoItem: InfoItem? = null
}