package com.cubern.launcher.ui.screens.content.download

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cubern.launcher.ui.base.BaseScreen
import com.cubern.launcher.ui.screens.NestedNavKey
import com.cubern.launcher.ui.screens.TitledNavKey
import com.cubern.launcher.ui.screens.navigateOnce
import com.cubern.launcher.ui.screens.NormalNavKey

/**
 * Rust 版 Minecraft（Horizon）专区 —— 下载真正的游戏版本，而非 Android APK。
 * Horizon 不含游戏本体，需自备正版 Minecraft Java 26.3 的游戏文件，
 * 本页引导通过启动器安装官方 26.3 版本，并提供 Android 移植版运行库入口。
 */
@Composable
fun DownloadRustMcScreen(
    key: NestedNavKey.DownloadRustMc,
    mainScreenKey: TitledNavKey?,
    downloadScreenKey: TitledNavKey?,
    backScreenViewModel: com.cubern.launcher.viewmodel.ScreenBackStackViewModel
) {
    val context = LocalContext.current
    BaseScreen(
        screenKey = key,
        currentKey = mainScreenKey,
        useClassEquality = true
    ) { isVisible: Boolean ->
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("Rust 版 Minecraft", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Horus 版 Minecraft Java 26.3（Horizon Android）由 Rust + wgpu 编写。安装 APK 后在 Horizon 内选择「Download Minecraft 26.3」，它会从 Mojang 官方源下载约 500MB 游戏资源（含校验），无需电脑准备。", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(16.dp))

            Text("第一步：安装游戏版本", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text("通过启动器下载并安装官方 Minecraft Java 26.3 版本（游戏本体），这是 Rust 版运行的前提。", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                // 跳转到下载游戏版本页，用户选择 26.3
                backScreenViewModel.downloadScreen.currentKey = backScreenViewModel.downloadGameScreen
                backScreenViewModel.downloadGameScreen.navigateOnce(NormalNavKey.DownloadGame.SelectGameVersion)
            }, Modifier.fillMaxWidth()) { Text("前往下载游戏版本（选择 26.3）") }
            Spacer(Modifier.height(16.dp))

            Text("第二步：运行库（Android 移植版）", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text("Horizon Android 移植版由社区开发，内置运行库支持 Vulkan 渲染与触控。", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {
                try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GoyDevv/Horizon-Android/releases"))) } catch (_: Exception) {}
            }, Modifier.fillMaxWidth()) { Text("打开 Horizon Android 运行库下载") }
            Spacer(Modifier.height(16.dp))

            Text("注意：需 Android 9+ 与 Vulkan 1.0+。多人服务器需正版账号。本项目与 Mojang / Microsoft / Horizon 无隶属关系。", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF9800))
        }
    }
}
