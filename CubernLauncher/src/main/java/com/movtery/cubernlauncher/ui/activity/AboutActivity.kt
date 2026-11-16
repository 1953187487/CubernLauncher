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

/**
 * 关于启动器页面
 */
class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuiXTheme {
                AboutScreen()
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于启动器") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Cubern Launcher",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("版本: v1.0.0-26.2+")
            Text("作者: Cubern Team & Contributors")
            Text("协议: GNU General Public License v3.0")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Cubern Launcher 是一款基于 Zalith Launcher 1 重构开发的 Minecraft Java版启动器。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
