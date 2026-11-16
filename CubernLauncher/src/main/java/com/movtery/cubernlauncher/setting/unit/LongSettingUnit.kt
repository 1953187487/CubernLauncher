package com.movtery.cubernlauncher.setting.unit

import com.movtery.cubernlauncher.setting.Settings.Manager

class LongSettingUnit(key: String, defaultValue: Long) : AbstractSettingUnit<Long>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it.toLongOrNull() }
}