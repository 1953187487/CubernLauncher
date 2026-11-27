package com.cubern.launcher.ui.screens.content

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cubern.launcher.feature.afdian.AfdianApi
import com.cubern.launcher.terracotta.Terracotta
import com.cubern.launcher.terracotta.fetchNodes
import com.cubern.launcher.terracotta.TerracottaState
import com.cubern.launcher.ui.theme.cardColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 爱发电联机（Cubern 独有，1.0.1 重写）：
 * 基于爱发电社交关系（私聊过/互相关注）的联机系统。
 * 房主创建房间生成房间码，通过爱发电好友邀请；房客输入房间码加入。
 * 联机底层使用 Terracotta（EasyTier P2P）。
 */
@Composable
fun AfdianMultiplayerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loggedIn by remember { mutableStateOf(AfdianApi.isLoggedIn()) }
    var friends by remember { mutableStateOf<List<String>>(emptyList()) }
    var roomCode by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("未开始") }
    val terracottaState by Terracotta.state.collectAsState()

    LaunchedEffect(terracottaState) {
        when (val s = terracottaState) {
            is TerracottaState.HostOK -> {
                status = "房主已就绪，房间码：${s.code}"
                roomCode = s.code.orEmpty()
            }
            is TerracottaState.GuestOK -> status = "已加入房间"
            is TerracottaState.Exception -> status = "联机异常：${s.type}"
            is TerracottaState.HostScanning -> status = "正在创建房间…"
            is TerracottaState.GuestConnecting -> status = "正在连接房间…"
            else -> {}
        }
    }

    LaunchedEffect(loggedIn) {
        if (!loggedIn) return@LaunchedEffect
        friends = withContext(Dispatchers.IO) {
            try { AfdianApi.querySponsors().getOrNull()?.map { it.name } ?: emptyList() }
            catch (t: Throwable) { emptyList() }
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text("爱发电联机", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("与私聊过或互相关注的爱发电好友联机（P2P）", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(12.dp))

        if (!loggedIn) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cardColor()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🔒", style = MaterialTheme.typography.displaySmall)
                Spacer(Modifier.height(8.dp))
                Text("请先登录爱发电", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("爱发电联机需登录账号，用于同步私聊过或互相关注的好友。", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AfdianApi.LOGIN_URL)))
                        AfdianApi.markLoggedIn()
                        loggedIn = true
                    } catch (_: Exception) {}
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF25D8E))) { Text("登录爱发电") }
            }
        } else {
            // 状态
            Text("状态：$status", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF4CAF50))
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(value = playerName, onValueChange = { playerName = it }, label = { Text("玩家名（可选）") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch {
                        try {
                            Terracotta.setWaiting(true)
                            val nodes = fetchNodes().map { it.toString() }
                            Terracotta.setScanning(null, playerName.ifBlank { null }, nodes)
                            status = "正在创建房间…"
                        } catch (t: Throwable) { status = "创建失败：${t.message}" }
                    }
                }, modifier = Modifier.weight(1f)) { Text("创建房间") }

                Button(onClick = {
                    if (roomCode.isNotBlank()) {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("room", roomCode))
                        Toast.makeText(context, "房间码已复制，可发给爱发电好友", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.weight(1f)) { Text("复制房间码邀请") }
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = roomCode, onValueChange = { roomCode = it }, label = { Text("输入好友的房间码") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                if (roomCode.isBlank()) { Toast.makeText(context, "请输入房间码", Toast.LENGTH_SHORT).show(); return@Button }
                scope.launch {
                    try {
                        Terracotta.setWaiting(true)
                        val nodes = fetchNodes().map { it.toString() }
                        Terracotta.setGuesting(roomCode.trim(), playerName.ifBlank { null }, nodes)
                        status = "正在连接房间…"
                    } catch (t: Throwable) { status = "加入失败：${t.message}" }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("加入好友房间") }

            Spacer(Modifier.height(16.dp))
            Text("好友列表（可联机）", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            if (friends.isEmpty()) {
                Text("暂无可联机好友，请先在爱发电私聊或互相关注。", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(friends) { friend ->
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor()).padding(12.dp)) {
                            Text("👤  $friend")
                            Spacer(Modifier.weight(1f))
                            Button(onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("invite", "Cubern 联机邀请：$roomCode"))
                                Toast.makeText(context, "邀请信息已复制，可发给 $friend", Toast.LENGTH_SHORT).show()
                            }) { Text("邀请") }
                        }
                    }
                }
            }
        }
    }
}
