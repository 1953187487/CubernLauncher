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
 * 管理控制页面
 */
class ManagementControlActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuiXTheme {
                ManagementControlScreen()
            }
        }
    }
}

@Composable
fun ManagementControlScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("管理控制") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "管理控制",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // TODO: 实现管理控制功能
            // 例如：模组管理、资源包管理、版本管理等
        }
    }
}
