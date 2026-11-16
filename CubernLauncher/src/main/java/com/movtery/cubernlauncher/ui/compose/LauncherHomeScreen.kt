package com.movtery.cubernlauncher.ui.compose

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.movtery.cubernlauncher.InfoDistributor
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.event.single.LaunchGameEvent
import com.movtery.cubernlauncher.feature.version.VersionsManager
import com.movtery.cubernlauncher.ui.fragment.VersionSelectorFragment
import com.movtery.cubernlauncher.utils.ZHTools
import com.movtery.cubernlauncher.utils.path.PathManager
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
 * 布局与原 Zalith Launcher1 主界面保持一致：
 * - 右上角：打开主目录、陶瓦联机
 * - 主体：当前游戏版本、启动游戏、版本选择、执行 JAR、设置入口
 * - 关于启动器与控制布局已整合进设置界面
 */
@Composable
fun LauncherHomeScreen(
    activity: FragmentActivity,
    onOpenSettings: () -> Unit,
    onOpenTaowa: () -> Unit,
    onOpenVersionSelector: () -> Unit
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
                }
            )
        }
    ) { padding ->
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
                    Text(
                        text = InfoDistributor.APP_NAME,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(text = context.getString(R.string.about_dec1, InfoDistributor.APP_NAME))
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.version_current),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(text = versionName)
                    if (versionInfo.isNotBlank()) {
                        Text(text = versionInfo)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        text = context.getString(R.string.main_launch),
                        onClick = {
                            if (VersionsManager.getCurrentVersion() == null) {
                                Toast.makeText(context, R.string.version_no_versions, Toast.LENGTH_SHORT).show()
                            } else {
                                EventBus.getDefault().post(LaunchGameEvent())
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    SuperArrow(
                        title = context.getString(R.string.version_title),
                        summary = versionName,
                        onClick = onOpenVersionSelector
                    )
                    SuperArrow(
                        title = context.getString(R.string.main_install_jar_file),
                        summary = context.getString(R.string.main_install_jar_file_desc),
                        onClick = { jarInstaller.launch("application/java-archive") }
                    )
                    SuperArrow(
                        title = context.getString(R.string.main_setting),
                        summary = context.getString(R.string.main_setting_desc),
                        onClick = onOpenSettings
                    )
                }
            }
        }
    }
}

/**
 * 打开游戏主目录
 */
private fun openMainDirectory(context: android.content.Context) {
    runCatching {
        val directory = PathManager.DIR_GAME_HOME
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse("file://$directory/"), "resource/folder")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, R.string.main_open_main_dir, Toast.LENGTH_SHORT).show()
    }
}
