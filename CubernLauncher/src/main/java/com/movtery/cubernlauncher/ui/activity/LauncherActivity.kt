package com.movtery.cubernlauncher.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.miuix.*

import com.movtery.cubernlauncher.R

/**
 * Launcher 主界面 - 使用 MUIX Compose UI
 * 移除了日志分享功能，关于启动器和管理控制已移至设置中
 */
class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuiXTheme {
                LauncherScreen()
            }
        }
    }
}

@Composable
fun LauncherScreen() {
    var showSettings by remember { mutableStateOf(false) }
    
    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cubern Launcher") },
                    actions = {
                        // 右上角淘瓦联机按钮
                        IconButton(onClick = { /* TODO: 打开淘瓦联机 */ }) {
                            Icon(
                                imageVector = Icons.Default.ConnectWithoutContact,
                                contentDescription = "淘瓦联机"
                            )
                        }
                        // 设置按钮（合并了关于启动器和管理控制）
                        IconButton(onClick = { showSettings = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "设置"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // 主内容区域
                Text(
                    text = "Cubern Launcher",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 游戏列表区域
                GameListSection()
            }
        }
    }
}

@Composable
fun GameListSection() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("游戏列表", style = MaterialTheme.typography.titleLarge)
            // TODO: 实现游戏列表展示
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // 关于启动器
            AboutLauncherSection()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 管理控制
            ManagementControlSection()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 其他设置项
            OtherSettingsSection()
        }
    }
}

@Composable
fun AboutLauncherSection() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("关于启动器", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cubern Launcher v1.0.0")
            Text("基于 Zalith Launcher 1 重构开发")
            Text("作者: Cubern Team & Contributors")
        }
    }
}

@Composable
fun ManagementControlSection() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("管理控制", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            // TODO: 实现管理控制功能
        }
    }
}

@Composable
fun OtherSettingsSection() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("其他设置", style = MaterialTheme.typography.titleMedium)
            // TODO: 实现其他设置项
        }
    }
}
