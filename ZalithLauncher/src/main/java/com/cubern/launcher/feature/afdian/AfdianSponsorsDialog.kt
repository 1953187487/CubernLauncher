package com.cubern.launcher.feature.afdian

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cubern.launcher.sponsor.Sponsor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 本启动器（Cubern）赞助者对话框 —— 数据来自爱发电 Open API */
@Composable
fun AfdianSponsorsDialog(onDismiss: () -> Unit) {
    var sponsors by remember { mutableStateOf<List<Sponsor>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        sponsors = withContext(Dispatchers.IO) {
            AfdianSponsorsApi.fetchMonthlySponsors()?.sponsors ?: emptyList()
        }
        loading = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("本启动器的赞助者", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                if (loading) {
                    Text("正在加载爱发电赞助者…", color = Color.Gray)
                } else if (sponsors.isEmpty()) {
                    Text("暂无赞助者，感谢您的支持！", color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text("支持作者：https://afdian.com/@KZjewnk", style = MaterialTheme.typography.bodySmall)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sponsors) { s ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text("👤 " + s.user.name, Modifier.weight(1f))
                                Text("¥" + String.format("%.2f", s.plan.totalAmount))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("关闭") } },
    )
}
