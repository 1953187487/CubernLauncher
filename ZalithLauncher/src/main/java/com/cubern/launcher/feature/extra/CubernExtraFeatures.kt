package com.cubern.launcher.feature.extra

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cubern.launcher.ui.theme.cardColor

/**
 * Cubern 独有：音乐播放器（酷狗音乐概念版）
 * 搜索/播放跳转酷狗音乐概念版，3D 动态唱片为本启动器独有视觉特效。
 */
@Composable
fun MusicPlayerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var keyword by remember { mutableStateOf("") }
    var playing by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("音乐播放器", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("酷狗音乐概念版 · 边放音乐边玩游戏", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(16.dp))

        // 3D 动态唱片
        val infinite = rememberInfiniteTransition()
        val angle by infinite.animateFloat(0f, 360f, infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart))
        Box(Modifier.size(180.dp).clip(CircleShape).background(Color(0xFF1DB954).copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(160.dp)) {
                val cx = size.width / 2f; val cy = size.height / 2f
                for (i in 0..36) {
                    val a = Math.toRadians((angle + i * 10.0).toDouble())
                    val r = 40f + (i % 4) * 12f
                    drawCircle(Color(0xFF1DB954).copy(alpha = 0.3f + (i % 4) * 0.15f), radius = 3f + (i % 3), center = Offset(cx + Math.cos(a).toFloat() * r, cy + Math.sin(a).toFloat() * r))
                }
                drawCircle(Color(0xFF1DB954), radius = 30f, center = Offset(cx, cy))
                drawCircle(Color.Black, radius = 8f, center = Offset(cx, cy))
            }
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = keyword, onValueChange = { keyword = it }, label = { Text("搜索音乐") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val url = if (keyword.isBlank()) "https://www.kugou.com/" else "https://www.kugou.com/yy/html/search.html#searchType=song&searchKeyWord=" + java.net.URLEncoder.encode(keyword, "UTF-8")
                try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (_: Exception) {}
                playing = true
            }) { Text("搜索并播放") }
            Button(onClick = {
                try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("kugou://"))) } catch (_: Exception) {}
            }) { Text("打开酷狗概念版") }
        }
        Spacer(Modifier.height(8.dp))
        Text(if (playing) "正在播放（酷狗音乐概念版）" else "未播放", color = if (playing) Color(0xFF4CAF50) else Color.Gray)
    }
}

/** Verse-App：Rust 版 Minecraft 下载与运行入口 */
@Composable
fun VerseRustMcScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text("Rust 版 Minecraft", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("通过 Verse-App 下载并运行 Rust 版 Minecraft（Horizon）。", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/doujie081231/Verse-App/releases/latest"))) } catch (_: Exception) {}
        }, Modifier.fillMaxWidth()) { Text("下载 Verse-App") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            try {
                val i = context.packageManager.getLaunchIntentForPackage("com.doujie.verse")
                if (i != null) context.startActivity(i) else context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/doujie081231/Verse-App")))
            } catch (_: Exception) {}
        }, Modifier.fillMaxWidth()) { Text("打开 Verse-App 运行 Rust MC") }
    }
}
