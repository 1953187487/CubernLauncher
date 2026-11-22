package com.movtery.zalithlauncher.ui.compose.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.feature.afdian.AfdianApi
import com.movtery.zalithlauncher.feature.update.UpdateUtils
import com.movtery.zalithlauncher.ui.compose.theme.CubernColors
import com.movtery.zalithlauncher.ui.compose.theme.CubernTheme
import com.movtery.zalithlauncher.ui.compose.theme.CubernTokens
import kotlinx.coroutines.delay

/**
 * 关于启动器（1.0.9）：恢复 1.0.1 时期 UI —— 图标 + 标题 + 描述 + 按钮的卡片列表，
 * 保留爱发电、赞助者、检查更新、账号等功能。
 */
@Composable
fun AboutLauncherScreen(onBack: () -> Unit, onCheckUpdate: (() -> Unit)? = null) {
    val context = LocalContext.current
    var sponsors by remember { mutableStateOf<List<AfdianApi.Sponsor>>(emptyList()) }
    var visible by remember { mutableStateOf(false) }
    var checking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        sponsors = AfdianApi.getCachedSponsors(context)
        AfdianApi.querySponsors().onSuccess { list -> sponsors = list; AfdianApi.saveSponsors(context, list) }
        delay(200)
    }

    CubernTheme {
        Column(Modifier.fillMaxSize().background(CubernTokens.BackgroundBrush)) {
            // 顶栏
            Row(Modifier.fillMaxWidth().background(CubernColors.SurfaceElevated).padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("‹", style = MaterialTheme.typography.titleLarge, color = CubernColors.AccentSoft,
                    modifier = Modifier.clickable { onBack() }.padding(end = 12.dp))
                Text("关于启动器", style = MaterialTheme.typography.titleLarge, color = CubernColors.TextPrimary)
            }
            HorizontalDivider(color = CubernColors.BorderDark)

            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // 1.0.1 风格列表项
                AboutItemView("ℹ", "Cubern Launcher", "v" + BuildConfig.VERSION_NAME + " · 作者：鹙鸟（B站同名）") {}
                AboutItemView("⚙", "开发基础", "基于 Zalith Launcher 1 二改，内核 PojavLauncher，Compose + Material 3 拟物沉浸风") {}

                // 账号入口
                AboutItemView("👤", "账号", "登录 Microsoft 账号游玩正版 Minecraft", "前往登录") { onBack() }

                // 检查更新（带刷新动画）
                AboutItemView("↻", "检查更新", "当前版本 v" + BuildConfig.VERSION_NAME, if (checking) "检查中…" else "检查") {
                    checking = true
                    UpdateUtils.checkDownloadedPackage(context, true, false)
                }

                // 爱发电赞助
                AboutItemView("❤", "赞助作者", "在爱发电支持作者 鹙鸟", "打开") {
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AfdianApi.FALLBACK_URL))) } catch (_: Exception) {}
                }

                // 赞助者列表
                AnimatedVisibility(visible = visible, enter = fadeIn(tween(300))) {
                    AboutItemView("★", "赞助者", if (sponsors.isEmpty()) "暂无赞助者" else sponsors.take(10).joinToString("、") { it.name }) {}
                }

                AboutItemView("🤝", "贡献者与鸣谢", "ZalithLauncher 1 · PojavLauncherTeam · burningtnt · EasyTier · FCL-Team · bs-community 等") {}

                Text("Cubern Launcher · 非官方社区开源项目，与 Mojang / Microsoft 无关",
                    style = MaterialTheme.typography.bodySmall, color = CubernColors.TextMuted, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
            }
        }
    }
}

/** 1.0.1 风格的关于列表项：左侧图标 + 标题/描述 + 右侧按钮 */
@Composable
private fun AboutItemView(
    icon: String,
    title: String,
    desc: String,
    buttonText: String? = null,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(12.dp)).clip(RoundedCornerShape(12.dp))
            .background(CubernTokens.CardBrush).border(1.dp, CubernColors.BorderLight, RoundedCornerShape(12.dp))
            .clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 圆形图标（对应 1.0.1 的 ShapeableImageView）
        androidx.compose.foundation.layout.Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(CubernTokens.AccentBrush),
            contentAlignment = Alignment.Center) {
            Text(icon, style = MaterialTheme.typography.titleMedium, color = CubernColors.TextPrimary)
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = CubernColors.TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = CubernColors.TextSecondary)
        }
        if (buttonText != null) {
            OutlinedButton(onClick = onClick, colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.AccentSoft)) {
                Text(buttonText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
