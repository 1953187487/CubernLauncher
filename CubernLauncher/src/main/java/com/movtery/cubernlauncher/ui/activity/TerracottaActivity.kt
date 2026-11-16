package com.movtery.cubernlauncher.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.movtery.cubernlauncher.ui.compose.CubernTheme
import com.movtery.cubernlauncher.ui.compose.TaowaScreen

/**
 * 陶瓦联机独立界面
 *
 * 与 Zalith Launcher 2 保持一致：进入游戏后通过启动器菜单打开联机功能。
 * 该界面与设置页面相互独立。
 */
class TerracottaActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CubernTheme {
                var showTerracotta by remember { mutableStateOf(true) }

                if (showTerracotta) {
                    TaowaScreen(onBack = { finish() })
                }
            }
        }
    }
}
