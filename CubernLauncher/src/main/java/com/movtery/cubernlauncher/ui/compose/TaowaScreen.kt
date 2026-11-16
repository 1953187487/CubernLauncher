package com.movtery.cubernlauncher.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.movtery.cubernlauncher.feature.taowa.TaowaMultiplayerManager
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
 * 淘瓦联机界面
 */
@Composable
fun TaowaScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = "淘瓦联机",
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
        val servers = remember {
            mutableStateListOf<TaowaMultiplayerManager.TaowaServer>().apply {
                addAll(TaowaMultiplayerManager.defaultServers())
            }
        }

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
                    Text(text = "联机服务器", modifier = Modifier.padding(bottom = 8.dp))
                    servers.forEach { server ->
                        SuperArrow(
                            title = server.name,
                            summary = if (server.isDefault) "默认服务器" else "自定义服务器",
                            onClick = { }
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    Text(text = "联机说明", modifier = Modifier.padding(bottom = 6.dp))
                    Text(
                        text = "淘瓦联机通过中转服务器实现玩家之间的联机游玩。加入服务器后，在游戏内即可看到同一房间的其他玩家。",
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(text = "该功能参考 Zalith Launcher2 的淘瓦联机实现。")
                }
            }
        }
    }
}
