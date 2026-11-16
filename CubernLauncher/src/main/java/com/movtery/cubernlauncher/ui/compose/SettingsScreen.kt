package com.movtery.cubernlauncher.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.movtery.cubernlauncher.InfoDistributor
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.feature.render.RenderModeManager
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
 *
 * 原主界面的「关于启动器」与「管理控制」布局已合并进设置：
 * - 控制布局位于倒数第二项
 * - 关于启动器位于最后一项
 */
@Composable
fun SettingsScreen(onBack: () -> Unit, onOpenAbout: () -> Unit = {}) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = context.getString(R.string.main_setting),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.ArrowBack,
                            contentDescription = context.getString(R.string.main_setting)
                        )
                    }
                }
            )
        }
    ) { padding ->
        SettingsContent(padding = padding, onOpenAbout = onOpenAbout)
    }
}

@Composable
private fun SettingsContent(padding: PaddingValues, onOpenAbout: () -> Unit) {
    val context = LocalContext.current
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
                Text(text = context.getString(R.string.setting_category_video), modifier = Modifier.padding(bottom = 8.dp))
                RenderModeEntry(mode = renderMode, onModeChange = { renderMode = it })
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = context.getString(R.string.setting_category_game), modifier = Modifier.padding(bottom = 8.dp))
                SuperSwitch(
                    title = context.getString(R.string.terracotta_enable_custom_nodes),
                    summary = context.getString(R.string.terracotta_custom_nodes_hint),
                    checked = autoJava,
                    onCheckedChange = { autoJava = it }
                )
                SuperArrow(
                    title = context.getString(R.string.setting_java_select_runtime),
                    summary = context.getString(R.string.setting_java_select_runtime_default),
                    onClick = { }
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = context.getString(R.string.setting_category_control), modifier = Modifier.padding(bottom = 8.dp))
                SuperArrow(
                    title = context.getString(R.string.custom_control_title),
                    summary = context.getString(R.string.custom_control_title),
                    onClick = { }
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = context.getString(R.string.setting_category_launcher), modifier = Modifier.padding(bottom = 8.dp))
                SuperArrow(
                    title = context.getString(R.string.main_open_main_dir),
                    onClick = { openLauncherDirectory(context) }
                )
                SuperArrow(
                    title = context.getString(R.string.version_manager_shortcuts),
                    summary = context.getString(R.string.version_manager_shortcuts_version_path),
                    onClick = { }
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = context.getString(R.string.about_tab, InfoDistributor.APP_NAME), modifier = Modifier.padding(bottom = 8.dp))
                Text(text = context.getString(R.string.about_dec1, InfoDistributor.APP_NAME))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = context.getString(R.string.about_dec2, InfoDistributor.APP_NAME))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = context.getString(R.string.about_dec3, InfoDistributor.APP_NAME))
                Spacer(modifier = Modifier.height(12.dp))
                SuperArrow(
                    title = context.getString(R.string.about_author_current),
                    summary = context.getString(R.string.about_author_current_value),
                    onClick = { }
                )
                SuperArrow(
                    title = context.getString(R.string.about_author_original),
                    summary = context.getString(R.string.about_author_original_value),
                    onClick = { }
                )
                SuperArrow(
                    title = context.getString(R.string.about_version_name),
                    summary = context.getString(R.string.about_version_code),
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    text = context.getString(R.string.about_tab, InfoDistributor.APP_NAME),
                    onClick = onOpenAbout,
                    modifier = Modifier.fillMaxWidth()
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
    val context = LocalContext.current
    SuperArrow(
        title = context.getString(R.string.setting_video_render_mode),
        summary = mode.displayName,
        onClick = { onModeChange(mode.next()) }
    )
}

/**
 * 打开启动器主目录
 */
private fun openLauncherDirectory(context: android.content.Context) {
    runCatching {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            setDataAndType(
                android.net.Uri.parse("file://${com.movtery.cubernlauncher.utils.path.PathManager.DIR_GAME_HOME}/"),
                "resource/folder"
            )
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
