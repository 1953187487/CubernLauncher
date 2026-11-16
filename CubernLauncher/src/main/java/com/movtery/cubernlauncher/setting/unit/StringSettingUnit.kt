package com.movtery.cubernlauncher.setting.unit

import com.movtery.cubernlauncher.setting.Settings.Manager

class StringSettingUnit(key: String, defaultValue: String) : AbstractSettingUnit<String>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it }
}