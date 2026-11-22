package com.movtery.zalithlauncher.ui.compose.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.compose.theme.CubernColors
import com.movtery.zalithlauncher.ui.compose.theme.CubernTheme
import com.movtery.zalithlauncher.ui.compose.theme.CubernTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

/**
 * Rust 版 Minecraft（Horizon Android）专区（1.0.8）。
 * 从 GitHub Releases 拉取 GoyDevv/Horizon-Android 的 APK 列表，提供下载入口。
 */
@Composable
fun RustMcScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var releases by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } // tag -> htmlUrl
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val list = withContext(Dispatchers.IO) {
            try {
                val text = URL("https://api.github.com/repos/GoyDevv/Horizon-Android/releases?per_page=5").readText()
                val arr = JSONArray(text)
                (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    o.optString("tag_name") to o.optString("html_url")
                }
            } catch (e: Exception) { emptyList() }
        }
        releases = list
        loading = false
    }

    CubernTheme {
        Column(Modifier.fillMaxSize().background(CubernTokens.BackgroundBrush)) {
            Row(Modifier.fillMaxWidth().background(CubernColors.SurfaceElevated).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("‹", style = MaterialTheme.typography.titleLarge, color = CubernColors.AccentSoft,
                    modifier = Modifier.clickable { onBack() }.padding(end = 12.dp))
                Text("Rust 版 MC（Horizon）", style = MaterialTheme.typography.titleLarge, color = CubernColors.TextPrimary)
            }
            HorizontalDivider(color = CubernColors.BorderDark)

            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard {
                    Text("Horizon —— 用 Rust 从零重写的 Minecraft Java 26.3", style = MaterialTheme.typography.titleMedium, color = CubernColors.AccentSoft)
                    Spacer(Modifier.height(6.dp))
                    Text("Android 移植版由社区开发，支持 Vulkan 渲染、可编辑触控与 .hmod 模组。", style = MaterialTheme.typography.bodyMedium, color = CubernColors.TextSecondary)
                    Text("注意：需自备正版 Minecraft Java 版游戏文件。", style = MaterialTheme.typography.bodySmall, color = CubernColors.Warning)
                }

                Button(onClick = { try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GoyDevv/Horizon-Android"))) } catch (_: Exception) {} },
                    Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CubernColors.Accent, contentColor = CubernColors.TextPrimary)) {
                    Text("打开 Horizon Android 项目页")
                }

                Text("可用版本", style = MaterialTheme.typography.titleMedium, color = CubernColors.AccentSoft)
                if (loading) {
                    CircularProgressIndicator(color = CubernColors.AccentSoft)
                } else if (releases.isEmpty()) {
                    Text("暂无版本信息，请前往项目页查看。", style = MaterialTheme.typography.bodyMedium, color = CubernColors.TextSecondary)
                } else releases.forEach { (tag, url) ->
                    AnimatedVisibility(visible = true, enter = fadeIn(tween(300))) {
                        OutlinedButton(onClick = { try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (_: Exception) {} },
                            Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.TextPrimary)) {
                            Text(tag)
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun InfoCard(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp))
        .background(CubernTokens.CardBrush).border(1.dp, CubernColors.BorderLight, RoundedCornerShape(14.dp)).padding(14.dp)) { content() }
}
