package com.movtery.cubernlauncher.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack

/**
 * 设置界面
 * 将原主界面的「关于启动器」与「管理控制」布局整合进设置
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenLegacyLauncher: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = "设置",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        SettingsContent(padding = padding, onOpenLegacyLauncher = onOpenLegacyLauncher)
    }
}

@Composable
private fun SettingsContent(padding: PaddingValues, onOpenLegacyLauncher: () -> Unit) {
    var renderMode by remember { mutableStateOf(RenderModeOption.AUTO) }
    var autoJava by remember { mutableStateOf(true) }

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
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = "渲染", modifier = Modifier.padding(bottom = 8.dp))
                RenderModeEntry(mode = renderMode, onModeChange = { renderMode = it })
                SuperArrow(
                    title = "Vulkan 支持检测",
                    summary = "核验 Vulkan API、物理设备、Vulkan 1.2 能力、图形队列与表面扩展",
                    onClick = onOpenLegacyLauncher
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = "Java 运行时", modifier = Modifier.padding(bottom = 8.dp))
                SuperSwitch(
                    title = "自动管理 Java 运行时",
                    summary = "自动识别、下载、校验并注入兼容的 Java 运行时",
                    checked = autoJava,
                    onCheckedChange = { autoJava = it }
                )
                SuperArrow(
                    title = "选择运行时",
                    summary = "查看已安装运行时并检查架构与完整性",
                    onClick = onOpenLegacyLauncher
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = "管理控制", modifier = Modifier.padding(bottom = 8.dp))
                SuperArrow(
                    title = "版本管理",
                    summary = "管理正式版、快照、Forge/Fabric 等自定义环境",
                    onClick = onOpenLegacyLauncher
                )
                SuperArrow(
                    title = "模组与资源包",
                    summary = "安装、启用与删除模组和资源包",
                    onClick = onOpenLegacyLauncher
                )
                SuperArrow(
                    title = "控制布局",
                    summary = "编辑触屏控制按键布局",
                    onClick = onOpenLegacyLauncher
                )
                SuperArrow(
                    title = "文件管理",
                    summary = "浏览与管理游戏目录文件",
                    onClick = onOpenLegacyLauncher
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = "关于启动器", modifier = Modifier.padding(bottom = 8.dp))
                SuperArrow(
                    title = "Cubern Launcher",
                    summary = "版本 1.0.0",
                    onClick = onOpenLegacyLauncher
                )
                SuperArrow(
                    title = "当前启动器作者",
                    summary = "万古沧海",
                    onClick = onOpenLegacyLauncher
                )
                SuperArrow(
                    title = "原作者",
                    summary = "Zalith Launcher1 启动器作者",
                    onClick = onOpenLegacyLauncher
                )
                SuperArrow(
                    title = "开源协议",
                    summary = "GNU General Public License v3.0",
                    onClick = onOpenLegacyLauncher
                )
                Button(
                    text = "打开完整启动器界面",
                    onClick = onOpenLegacyLauncher,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

private enum class RenderModeOption(val displayName: String) {
    FOLLOW_GAME("跟随游戏"),
    OPENGL("OpenGL"),
    VULKAN("Vulkan"),
    AUTO("自动选择");

    fun next(): RenderModeOption = entries[(ordinal + 1) % entries.size]
}

@Composable
private fun RenderModeEntry(mode: RenderModeOption, onModeChange: (RenderModeOption) -> Unit) {
    SuperArrow(
        title = "渲染模式",
        summary = mode.displayName,
        onClick = { onModeChange(mode.next()) }
    )
}
