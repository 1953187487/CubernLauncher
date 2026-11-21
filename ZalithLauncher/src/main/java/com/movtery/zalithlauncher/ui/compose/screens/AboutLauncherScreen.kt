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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Brush
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
 * 关于启动器页面（1.0.6 重写排版 + 更新动画 UI）。
 * 单一滚动页：品牌头 / 开发基础 / 作者与爱发电 / 赞助者 / 检查更新 / 贡献者与鸣谢。
 */
@Composable
fun AboutLauncherScreen(onBack: () -> Unit, onCheckUpdate: (() -> Unit)? = null) {
    val context = LocalContext.current
    var sponsors by remember { mutableStateOf<List<AfdianApi.Sponsor>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var visible by remember { mutableStateOf(false) }
    var checking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        AfdianApi.querySponsors().onSuccess { sponsors = it }
        delay(300)
        loading = false
    }
    val headerAlpha by animateFloatAsState(if (visible) 1f else 0f, tween(500))

    CubernTheme {
        Column(Modifier.fillMaxSize().background(CubernTokens.BackgroundBrush)) {
            // 顶部栏（返回 + 标题 + 检查更新动画按钮）
            Row(
                Modifier.fillMaxWidth().background(CubernColors.SurfaceElevated).padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("‹", style = MaterialTheme.typography.titleLarge, color = CubernColors.AccentSoft,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onBack() }.padding(end = 12.dp))
                Text("关于启动器", style = MaterialTheme.typography.titleLarge, color = CubernColors.TextPrimary,
                    modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = { checking = true; UpdateUtils.checkDownloadedPackage(context, true, false) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.AccentSoft),
                ) {
                    if (checking) CircularProgressIndicator(Modifier.size(16.dp), color = CubernColors.AccentSoft, strokeWidth = 2.dp)
                    else Text("检查更新")
                }
            }

            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // 品牌头（淡入动画）
                Column(
                    Modifier.fillMaxWidth().alpha(headerAlpha).shadow(16.dp, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))
                        .background(CubernTokens.CardBrush).border(1.dp, CubernColors.BorderLight, RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.size(64.dp).clip(CircleShape).background(CubernTokens.AccentBrush))
                    Spacer(Modifier.height(10.dp))
                    Text("Cubern Launcher", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = CubernColors.TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text("v" + BuildConfig.VERSION_NAME + " · Beta", style = MaterialTheme.typography.bodyMedium, color = CubernColors.AccentSoft)
                }

                // 开发基础
                AboutCard("开发基础") {
                    AboutLine("基于 Zalith Launcher 1 原作代码深度二改")
                    AboutLine("底层内核：PojavLauncher（Android Minecraft Java 版启动核心）")
                    AboutLine("界面引擎：Jetpack Compose + Material 3")
                    AboutLine("视觉风格：拟物沉浸风（Cubern Design）")
                    AboutLine("开源协议：GNU General Public License v3.0")
                }

                // 作者与支持（爱发电）
                AboutCard("作者与支持") {
                    AboutLine("当前作者与维护者：鹙鸟（B站同名）")
                    AboutLine("非官方社区开源项目，与 Mojang / Microsoft / 网易无关")
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AfdianApi.FALLBACK_URL))) } catch (_: Exception) {} },
                        Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CubernColors.Accent, contentColor = CubernColors.TextPrimary),
                    ) { Text("在爱发电支持作者") }
                }

                // 赞助者（动态 + 逐个入场动画）
                AboutCard("赞助者") {
                    if (loading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(16.dp), color = CubernColors.AccentSoft, strokeWidth = 2.dp)
                            Spacer(Modifier.padding(horizontal = 8.dp))
                            Text("正在加载赞助者…", style = MaterialTheme.typography.bodySmall, color = CubernColors.TextMuted)
                        }
                    } else if (sponsors.isEmpty()) {
                        AboutLine("还没有赞助者，成为第一个支持作者的人吧！")
                    } else sponsors.take(20).forEachIndexed { index, sp ->
                        AnimatedVisibility(visible = visible, enter = fadeIn(tween(300 + index * 50)) + slideInVertically(initialOffsetY = { it / 2 })) {
                            AboutLine(sp.name + (if (sp.amount.isNotBlank()) " · ￥" + sp.amount else ""))
                        }
                    }
                }

                // 检查更新
                AboutCard("检查更新") {
                    AboutLine("当前版本：v" + BuildConfig.VERSION_NAME)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { checking = true; UpdateUtils.checkDownloadedPackage(context, true, false) },
                        Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.AccentSoft)) { Text("检查更新") }
                }

                // 贡献者与鸣谢（重写）
                AboutCard("贡献者与鸣谢") {
                    AboutLine("Zalith Launcher 1 —— 原作启动器，奠定本项目底层架构")
                    AboutLine("PojavLauncher 开发团队 —— 开源 Android Minecraft 启动核心")
                    AboutLine("Terracotta / EasyTier —— P2P 联机方案")
                    AboutLine("MC 百科（mcmod.cn）—— Mod 与整合包中文翻译")
                    AboutLine("FoldCraftLauncher / Zalith Launcher 2 —— UI 与功能参考")
                    AboutLine("所有提交 Issue、反馈问题与支持的玩家")
                }

                Text("Cubern Launcher · 为 Android 上的 Minecraft Java 版而生",
                    style = MaterialTheme.typography.bodySmall, color = CubernColors.TextMuted, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
            }
        }
    }
}

@Composable private fun AboutCard(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(20.dp)).clip(RoundedCornerShape(20.dp))
        .background(CubernTokens.CardBrush).border(1.dp, CubernColors.BorderLight, RoundedCornerShape(20.dp)).padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = CubernColors.AccentSoft)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable private fun AboutLine(text: String) {
    Text("· " + text, style = MaterialTheme.typography.bodyMedium, color = CubernColors.TextSecondary, modifier = Modifier.padding(vertical = 3.dp))
}
