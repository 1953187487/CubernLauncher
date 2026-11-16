package com.movtery.cubernlauncher.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack

/**
 * Cubern Launcher 的 MIUIX 应用骨架
 */
@Composable
fun CubernApp(
    onOpenSettings: () -> Unit,
    onOpenTaowa: () -> Unit,
    showSettings: Boolean,
    showTaowa: Boolean,
    onCloseSettings: () -> Unit,
    onCloseTaowa: () -> Unit,
    onOpenLegacyLauncher: () -> Unit
) {
    when {
        showSettings -> SettingsScreen(onBack = onCloseSettings, onOpenLegacyLauncher = onOpenLegacyLauncher)
        showTaowa -> TaowaScreen(onBack = onCloseTaowa)
        else -> LauncherHomeScreen(
            onOpenSettings = onOpenSettings,
            onOpenTaowa = onOpenTaowa,
            onOpenLegacyLauncher = onOpenLegacyLauncher
        )
    }
}

@Composable
private fun LauncherHomeScreen(
    onOpenSettings: () -> Unit,
    onOpenTaowa: () -> Unit,
    onOpenLegacyLauncher: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = "Cubern Launcher",
                actions = {
                    IconButton(onClick = onOpenTaowa) {
                        Icon(
                            imageVector = CubernIcons.Multiplayer,
                            contentDescription = "淘瓦联机"
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = CubernIcons.Settings,
                            contentDescription = "设置"
                        )
                    }
                }
            )
        }
    ) { padding ->
        HomeContent(
            padding = padding,
            onOpenSettings = onOpenSettings,
            onOpenTaowa = onOpenTaowa,
            onOpenLegacyLauncher = onOpenLegacyLauncher
        )
    }
}

@Composable
private fun HomeContent(
    padding: PaddingValues,
    onOpenSettings: () -> Unit,
    onOpenTaowa: () -> Unit,
    onOpenLegacyLauncher: () -> Unit
) {
    val renderMode = remember { mutableStateOf("自动选择") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp)
            ) {
                Text(
                    text = "Cubern Launcher",
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "基于 Zalith Launcher1 二改开发，引入 26.2+ 运行环境、Java 运行时自动管理、淘瓦联机与 Vulkan 渲染支持。",
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    text = "进入游戏库",
                    onClick = onOpenLegacyLauncher,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp)
            ) {
                Text(
                    text = "功能入口",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SuperArrow(
                    title = "淘瓦联机",
                    summary = "与好友联机游玩，右上角快速进入",
                    onClick = onOpenTaowa
                )
                SuperArrow(
                    title = "设置",
                    summary = "渲染模式、Java 运行时、关于启动器与管理控制",
                    onClick = onOpenSettings
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp)
            ) {
                Text(
                    text = "运行环境",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SuperArrow(
                    title = "渲染模式",
                    summary = renderMode.value,
                    onClick = { renderMode.value = nextRenderMode(renderMode.value) }
                )
                SuperArrow(
                    title = "Java 运行时",
                    summary = "自动识别、下载、校验并注入兼容运行时",
                    onClick = onOpenSettings
                )
            }
        }
    }
}

private fun nextRenderMode(current: String): String = when (current) {
    "跟随游戏" -> "OpenGL"
    "OpenGL" -> "Vulkan"
    "Vulkan" -> "自动选择"
    else -> "跟随游戏"
}
