package com.cubern.launcher.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cubern.launcher.setting.launcherMMKV

/**
 * 主页横屏左侧公告栏（1.0.2）。
 * 显示从私有公告仓库拉取的最新公告，点击可展开/收起。
 */
@Composable
fun AnnouncementBar(modifier: Modifier = Modifier) {
    val title = launcherMMKV().getString("cubern_announcement_title", null)
    val content = launcherMMKV().getString("cubern_announcement_content", null)
    if (title.isNullOrBlank() && content.isNullOrBlank()) return

    Column(
        modifier = modifier
            .width(180.dp)
            .fillMaxHeight()
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📢", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text("公告", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        if (!title.isNullOrBlank()) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        if (!content.isNullOrBlank()) {
            Text(content, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
