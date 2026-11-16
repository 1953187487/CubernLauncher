package com.movtery.cubernlauncher.terracotta

import com.movtery.cubernlauncher.setting.Settings
import com.movtery.cubernlauncher.setting.unit.BooleanSettingUnit
import com.movtery.cubernlauncher.setting.unit.IntSettingUnit
import com.movtery.cubernlauncher.setting.unit.StringSettingUnit

/**
 * 陶瓦联机设置项
 * 接入 Cubern Launcher 的 JSON 设置体系
 */
object TerracottaPrefs {

    private val noticeVersionUnit = IntSettingUnit("terracotta_notice_version", -1)
    private val enableCustomNodesUnit = BooleanSettingUnit("terracotta_enable_custom_nodes", false)
    private val customNodesUnit = StringSettingUnit("terracotta_custom_nodes", "")

    /**
     * 玩家已阅读的联机公告版本号
     */
    var noticeVersion: Int
        get() = noticeVersionUnit.getValue()
        set(value) {
            noticeVersionUnit.put(value).save()
        }

    /**
     * 是否启用自定义 EasyTier 节点
     */
    var enableCustomNodes: Boolean
        get() = enableCustomNodesUnit.getValue()
        set(value) {
            enableCustomNodesUnit.put(value).save()
        }

    /**
     * 自定义 EasyTier 节点地址
     */
    var customNodes: String
        get() = customNodesUnit.getValue()
        set(value) {
            customNodesUnit.put(value).save()
        }
}
