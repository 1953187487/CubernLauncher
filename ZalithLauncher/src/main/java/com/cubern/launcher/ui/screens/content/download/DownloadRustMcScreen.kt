package com.cubern.launcher.ui.screens.content.download

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cubern.launcher.ui.base.BaseScreen
import com.cubern.launcher.ui.screens.NestedNavKey
import com.cubern.launcher.ui.screens.TitledNavKey
import com.cubern.launcher.ui.screens.content.download.game.SelectGameVersionHost
import com.cubern.launcher.ui.screens.content.download.game.SelectGameVersionScreen
import com.cubern.launcher.viewmodel.EventViewModel

/**
 * Rust 版 Minecraft 专区 —— 直接在启动器内下载真正的游戏版本。
 * 复用 ZL2 的版本下载流程，从 Mojang 官方源 / BMCLAPI 镜像下载 Minecraft Java 26.3。
 * 无需打开任何外部软件。
 */
@Composable
fun DownloadRustMcScreen(
    key: NestedNavKey.DownloadRustMc,
    mainScreenKey: TitledNavKey?,
    downloadScreenKey: TitledNavKey?,
    eventViewModel: EventViewModel,
    backScreenViewModel: com.cubern.launcher.viewmodel.ScreenBackStackViewModel
) {
    BaseScreen(
        screenKey = key,
        currentKey = mainScreenKey,
        useClassEquality = true
    ) { isVisible: Boolean ->
        Column(Modifier.fillMaxSize()) {
            // 游戏版本下载（真正的游戏本体，非 APK）
            SelectGameVersionScreen(
                host = SelectGameVersionHost.Download(
                    mainScreenKey = mainScreenKey,
                    downloadScreenKey = downloadScreenKey,
                    downloadGameScreenKey = backScreenViewModel.downloadGameScreen
                ),
                eventViewModel = eventViewModel
            ) { version ->
                // 选择版本后，走标准安装流程
                backScreenViewModel.downloadGameScreen.navigateOnce(
                    com.cubern.launcher.ui.screens.NormalNavKey.DownloadGame.Addons(
                        gameVersion = version.version.id,
                        type = version.type
                    )
                )
            }
        }
    }
}
