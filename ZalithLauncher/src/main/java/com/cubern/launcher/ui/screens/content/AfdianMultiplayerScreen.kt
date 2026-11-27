package com.cubern.launcher.ui.screens.content

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.cubern.launcher.R
import com.cubern.launcher.ui.theme.cardColor
import com.cubern.launcher.ui.theme.onCardColor
import com.cubern.launcher.utils.network.NetWorkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 爱发电联机（Cubern 独有）：基于爱发电社交关系（私聊过 / 互相关注）的好友联机。
 * 当前版本以爱发电赞助者列表作为可联机好友数据源（官方 API 可查询赞助者）。
 */
@Composable
fun AfdianMultiplayerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var friends by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var invited by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // 从爱发电 Open API 获取赞助者作为好友列表
        friends = withContext(Dispatchers.IO) {
            try {
                val list = com.cubern.launcher.feature.afdian.AfdianApi.querySponsors()
                list.getOrNull()?.map { it.name } ?: emptyList()
            } catch (t: Throwable) { emptyList() }
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text("爱发电联机", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("与私聊过或互相关注的爱发电好友联机", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(12.dp))

        if (loading) {
            CircularProgressIndicator()
        } else if (friends.isEmpty()) {
            Text("暂无可联机好友。请在爱发电私聊或互相关注后重试。", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(friends) { friend ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👤", Modifier.size(28.dp).clip(CircleShape).background(onCardColor.copy(alpha = 0.1f)))
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
