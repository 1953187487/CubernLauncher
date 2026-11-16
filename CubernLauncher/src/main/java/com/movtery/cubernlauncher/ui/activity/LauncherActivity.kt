package com.movtery.cubernlauncher.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.movtery.cubernlauncher.ui.compose.CubernApp
import com.movtery.cubernlauncher.ui.compose.CubernTheme
import net.kdt.pojavlaunch.LauncherActivity as LegacyLauncherActivity

/**
 * Cubern Launcher 的 MUIX 主界面
 * 右上角提供淘瓦联机入口，设置中整合关于启动器与管理控制
 */
class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CubernTheme {
                var showSettings by remember { mutableStateOf(false) }
                var showTaowa by remember { mutableStateOf(false) }

                CubernApp(
                    onOpenSettings = { showSettings = true },
                    onOpenTaowa = { showTaowa = true },
                    showSettings = showSettings,
                    showTaowa = showTaowa,
                    onCloseSettings = { showSettings = false },
                    onCloseTaowa = { showTaowa = false },
                    onOpenLegacyLauncher = { openLegacyLauncher() }
                )
            }
        }
    }

    private fun openLegacyLauncher() {
        startActivity(Intent(this, LegacyLauncherActivity::class.java))
    }
}
