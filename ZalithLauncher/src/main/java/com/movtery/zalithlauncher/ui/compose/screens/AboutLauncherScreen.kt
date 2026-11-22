package com.movtery.zalithlauncher.ui.compose.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.alpha
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
 * 关于启动器（1.0.8）：重写为 Zalith Launcher 1 原版列表风格 + 拟物 UI 与入场动画。
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
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(400))

    CubernTheme {
        Column(Modifier.fillMaxSize().background(CubernTokens.BackgroundBrush)) {
            // 原版风格顶栏
            Row(Modifier.fillMaxWidth().background(CubernColors.SurfaceElevated).padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("‹", style = MaterialTheme.typography.titleLarge, color = CubernColors.AccentSoft,
                    modifier = Modifier.clickable { onBack() }.padding(end = 12.dp))
                Text("关于启动器", style = MaterialTheme.typography.titleLarge, color = CubernColors.TextPrimary)
            }
            HorizontalDivider(color = CubernColors.BorderDark)

            Column(Modifier.fillMaxSize().alpha(alpha).verticalScroll(rememberScrollState()).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // 版本信息卡（原版信息样式）
                InfoCard {
                    AboutLine("应用版本：" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")
                    AboutLine("当前作者：鹙鸟（B站同名）")
                    AboutLine("版本状态：Beta / Pre-release")
                    AboutLine("开源协议：GNU General Public License v3.0")
                }

                SectionTitle("开发基础")
                InfoCard {
                    AboutLine("基于 Zalith Launcher 1 原作代码深度二改")
                    AboutLine("底层内核：PojavLauncher（Android Minecraft Java 版启动核心）")
                    AboutLine("界面引擎：Jetpack Compose + Material 3")
                    AboutLine("视觉风格：拟物沉浸风（Cubern Design）")
                }

                SectionTitle("账号与更新")
                InfoCard {
                    AboutLine("登录 Microsoft 账号游玩正版 Minecraft")
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(onClick = {}, Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.AccentSoft)) { Text("前往账号登录") }
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(onClick = { checking = true; UpdateUtils.checkDownloadedPackage(context, true, false) },
                        Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.AccentSoft)) {
                        if (checking) CircularProgressIndicator(Modifier.size(16.dp), color = CubernColors.AccentSoft, strokeWidth = 2.dp)
                        else Text("检查更新")
                    }
                }

                SectionTitle("赞助作者")
                InfoCard {
                    AboutLine("在爱发电支持作者：鹙鸟")
                    Spacer(Modifier.height(6.dp))
                    Button(onClick = { try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AfdianApi.FALLBACK_URL))) } catch (_: Exception) {} },
                        Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CubernColors.Accent, contentColor = CubernColors.TextPrimary)) {
                        Text("打开爱发电赞助页面")
                    }
                    if (sponsors.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("赞助者名单", style = MaterialTheme.typography.titleSmall, color = CubernColors.AccentSoft)
                        sponsors.take(20).forEachIndexed { index, sp ->
                            AnimatedVisibility(visible = visible, enter = fadeIn(tween(300 + index * 40)) + slideInVertically(initialOffsetY = { it / 2 })) {
                                AboutLine(sp.name + (if (sp.amount.isNotBlank()) " · ￥" + sp.amount else ""))
                            }
                        }
                    }
                }

                SectionTitle("贡献者与鸣谢")
                InfoCard {
                    AboutLine("Zalith Launcher 1 · ZalithLauncher —— github.com/ZalithLauncher/ZalithLauncher")
                    AboutLine("PojavLauncher · PojavLauncherTeam —— github.com/PojavLauncherTeam/PojavLauncher")
                    AboutLine("Terracotta · burningtnt —— github.com/burningtnt/terracotta")
                    AboutLine("EasyTier · EasyTier 团队 —— github.com/EasyTier/EasyTier")
                    AboutLine("FoldCraftLauncher · FCL-Team —— github.com/FCL-Team/FoldCraftLauncher")
                    AboutLine("Zalith Launcher 2 · ZalithLauncher —— github.com/ZalithLauncher/ZalithLauncher2")
                    AboutLine("skinview3d · bs-community —— github.com/bs-community/skinview3d")
                    AboutLine("MC 百科 —— mcmod.cn（Mod 与整合包中文翻译）")
                    AboutLine("所有反馈问题与支持的玩家")
                }

                Text("Cubern Launcher · 为 Android 上的 Minecraft Java 版而生",
                    style = MaterialTheme.typography.bodySmall, color = CubernColors.TextMuted, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
            }
        }
    }
}

@Composable private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = CubernColors.AccentSoft, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
}

@Composable private fun InfoCard(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp))
        .background(CubernTokens.CardBrush).border(1.dp, CubernColors.BorderLight, RoundedCornerShape(14.dp)).padding(14.dp)) { content() }
}

@Composable private fun AboutLine(text: String) {
    Text("· " + text, style = MaterialTheme.typography.bodyMedium, color = CubernColors.TextSecondary, modifier = Modifier.padding(vertical = 3.dp))
}
