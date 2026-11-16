package com.movtery.cubernlauncher.setting.unit

import com.movtery.cubernlauncher.setting.Settings.Manager

class DoubleSettingUnit(key: String, defaultValue: Double) : AbstractSettingUnit<Double>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it.toDoubleOrNull() }
}