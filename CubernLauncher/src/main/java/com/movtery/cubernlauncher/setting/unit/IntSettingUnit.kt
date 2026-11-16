package com.movtery.CubernLauncher.setting.unit

import com.movtery.CubernLauncher.setting.Settings.Manager

class IntSettingUnit(key: String, defaultValue: Int) : AbstractSettingUnit<Int>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it.toIntOrNull() }
}