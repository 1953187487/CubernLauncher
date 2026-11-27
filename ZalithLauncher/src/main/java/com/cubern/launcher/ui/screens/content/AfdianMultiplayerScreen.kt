package com.cubern.launcher.ui.screens.content

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cubern.launcher.feature.afdian.AfdianApi
import com.cubern.launcher.ui.theme.cardColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 爱发电联机（Cubern 独有）：基于爱发电社交关系（私聊过 / 互相关注）的好友联机。
 * 必须登录爱发电后才能使用；未登录时显示登录引导。
 */
@Composable
fun AfdianMultiplayerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var loggedIn by remember { mutableStateOf(AfdianApi.isLoggedIn()) }
    var friends by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var invited by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loggedIn) {
        if (!loggedIn) return@LaunchedEffect
        loading = true
        friends = withContext(Dispatchers.IO) {
            try { AfdianApi.querySponsors().getOrNull()?.map { it.name } ?: emptyList() }
            catch (t: Throwable) { emptyList() }
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text("爱发电联机", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("与私聊过或互相关注的爱发电好友联机", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(12.dp))

        if (!loggedIn) {
            // 未登录：显示登录引导
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cardColor()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🔒", style = MaterialTheme.typography.displaySmall)
                Spacer(Modifier.height(8.dp))
                Text("请先登录爱发电", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(
                    "爱发电联机需要登录爱发电账号，用于同步私聊过或互相关注的好友列表。",
                    style = MaterialTheme.typography.bodyMedium, color = Color.Gray
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AfdianApi.LOGIN_URL)))
                            AfdianApi.markLoggedIn()
                            loggedIn = true
                        } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF25D8E))
                ) { Text("登录爱发电") }
                Spacer(Modifier.height(8.dp))
                Text("登录后将自动同步好友列表", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        } else if (loading) {
            CircularProgressIndicator()
        } else if (friends.isEmpty()) {
            Text("暂无可联机好友。请先在爱发电私聊或互相关注后重试。", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(friends) { friend ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor()).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👤", Modifier.size(28.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.1f)))
                        Text(friend, Modifier.weight(1f).padding(start = 10.dp))
                        Button(onClick = { invited = friend }) { Text("邀请") }
                    }
                }
            }
        }

        invited?.let { friend ->
            Spacer(Modifier.height(12.dp))
            Text("已向 $friend 发送联机邀请", color = Color(0xFF4CAF50))
        }
    }
}
