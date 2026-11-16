package com.movtery.cubernlauncher.ui.compose

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.movtery.cubernlauncher.InfoDistributor
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.event.single.LaunchGameEvent
import com.movtery.cubernlauncher.feature.version.VersionsManager
import com.movtery.cubernlauncher.ui.fragment.AccountFragment
import com.movtery.cubernlauncher.ui.fragment.FilesFragment
import com.movtery.cubernlauncher.ui.fragment.ModsFragment
import com.movtery.cubernlauncher.ui.fragment.VersionManagerFragment
import org.greenrobot.eventbus.EventBus
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow

/**
 * Cubern Launcher 的 MIUIX 主界面
 *
 * 布局完全还原 Zalith Launcher1 的双栏结构：
 * - 顶栏右侧四个图标：打开主目录、陶瓦联机、下载、设置
 * - 左侧菜单栏（宽 6.5 份）：版本管理、模组管理、文件管理、执行 JAR
 * - 右侧启动面板（宽 3 份）：账号、当前版本、启动游戏
 */
@Composable
fun LauncherHomeScreen(
    activity: FragmentActivity,
    onOpenSettings: () -> Unit,
    onOpenTaowa: () -> Unit,
    onOpenDownload: () -> Unit,
    onOpenVersionSelector: () -> Unit,
    onOpenAi: () -> Unit
) {
    val context = LocalContext.current
    var versionName by remember { mutableStateOf(context.getString(R.string.version_no_versions)) }
    var versionInfo by remember { mutableStateOf("") }

    val jarInstaller = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            runCatching {
                val launchIntent = Intent(activity.intent).apply {
                    data = uri
                    action = Intent.ACTION_VIEW
                }
                activity.startActivity(launchIntent)
            }.onFailure {
                Toast.makeText(context, R.string.main_install_jar_file, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        val version = VersionsManager.getCurrentVersion()
        versionName = version?.getVersionName() ?: context.getString(R.string.version_no_versions)
        versionInfo = version?.getVersionInfo()?.getInfoString().orEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = InfoDistributor.APP_NAME,
                actions = {
                    IconButton(onClick = { openMainDirectory(context) }) {
                        Icon(
                            imageVector = CubernIcons.Folder,
                            contentDescription = context.getString(R.string.main_open_main_dir)
                        )
                    }
                    IconButton(onClick = onOpenTaowa) {
                        Icon(
                            imageVector = CubernIcons.Multiplayer,
                            contentDescription = context.getString(R.string.terracotta_menu)
                        )
                    }
                    IconButton(onClick = onOpenDownload) {
                        Icon(
                            imageVector = CubernIcons.Download,
                            contentDescription = context.getString(R.string.download)
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = CubernIcons.Settings,
                            contentDescription = context.getString(R.string.main_setting)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LauncherMenuPane(
                modifier = Modifier
                    .weight(6.5f)
                    .fillMaxHeight(),
                onOpenVersionManager = { openFragment(activity, VersionManagerFragment::class.java, VersionManagerFragment.TAG) },
                onOpenMods = { openFragment(activity, ModsFragment::class.java, ModsFragment.TAG) },
                onOpenFiles = { openFragment(activity, FilesFragment::class.java, FilesFragment.TAG) },
                onExecuteJar = { jarInstaller.launch("application/java-archive") }
            )

            // 启动器动画：位于右侧面板的最左边
            LauncherAnimation(
                modifier = Modifier.fillMaxHeight()
            )

            PlayPanel(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight(),
                versionName = versionName,
                versionInfo = versionInfo,
                onOpenAccount = { openFragment(activity, AccountFragment::class.java, AccountFragment.TAG) },
                onOpenVersionSelector = onOpenVersionSelector,
                onOpenAi = onOpenAi,
                onLaunchGame = {
                    if (VersionsManager.getCurrentVersion() == null) {
                        Toast.makeText(context, R.string.version_no_versions, Toast.LENGTH_SHORT).show()
                    } else {
                        EventBus.getDefault().post(LaunchGameEvent())
                    }
                }
            )
        }
    }
}

/**
 * 左侧菜单栏
 */
@Composable
private fun LauncherMenuPane(
    modifier: Modifier,
    onOpenVersionManager: () -> Unit,
    onOpenMods: () -> Unit,
    onOpenFiles: () -> Unit,
    onExecuteJar: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Text(text = InfoDistributor.APP_NAME)
                Text(
                    text = context.getString(R.string.about_dec1, InfoDistributor.APP_NAME),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = DpSize(16.dp, 4.dp)
            ) {
                SuperArrow(
                    title = context.getString(R.string.version_manager_title),
                    summary = context.getString(R.string.version_manager_shortcuts),
                    onClick = onOpenVersionManager
                )
                SuperArrow(
                    title = context.getString(R.string.version_manager_shortcuts_mods),
                    onClick = onOpenMods
                )
                SuperArrow(
                    title = context.getString(R.string.main_files),
                    onClick = onOpenFiles
                )
                SuperArrow(
                    title = context.getString(R.string.main_install_jar_file),
                    summary = context.getString(R.string.main_install_jar_file_desc),
                    onClick = onExecuteJar
                )
            }
        }
    }
}

/**
 * 右侧启动面板
 */
@Composable
private fun PlayPanel(
    modifier: Modifier,
    versionName: String,
    versionInfo: String,
    onOpenAccount: () -> Unit,
    onOpenVersionSelector: () -> Unit,
    onOpenAi: () -> Unit,
    onLaunchGame: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = DpSize(16.dp, 16.dp)
        ) {
            SuperArrow(
                title = context.getString(R.string.account_title),
                summary = context.getString(R.string.account_no_saved_accounts),
                onClick = onOpenAccount
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = DpSize(16.dp, 16.dp)
        ) {
            Text(
                text = context.getString(R.string.version_current),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = versionName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (versionInfo.isNotBlank()) {
                Text(
                    text = versionInfo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            SuperArrow(
                title = context.getString(R.string.version_title),
                onClick = onOpenVersionSelector
            )
        }

        // AI 功能：第五个功能入口
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = DpSize(16.dp, 16.dp)
        ) {
            SuperArrow(
                title = context.getString(R.string.ai_feature_title),
                summary = context.getString(R.string.ai_feature_desc),
                onClick = onOpenAi
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            text = context.getString(R.string.main_launch),
            onClick = onLaunchGame,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 打开游戏主目录
 */
private fun openMainDirectory(context: android.content.Context) {
    runCatching {
        val directory = com.movtery.cubernlauncher.utils.path.PathManager.DIR_GAME_HOME
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse("file://$directory/"), "resource/folder")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, R.string.main_open_main_dir, Toast.LENGTH_SHORT).show()
    }
}

/**
 * 切换到指定的功能页面
 */
private fun openFragment(
    activity: FragmentActivity,
    fragmentClass: Class<out androidx.fragment.app.Fragment>,
    tag: String
) {
    val fragment = activity.supportFragmentManager.findFragmentById(R.id.container_fragment)
    if (fragment != null) {
        com.movtery.cubernlauncher.utils.ZHTools.swapFragmentWithAnim(fragment, fragmentClass, tag, null)
    }
}
