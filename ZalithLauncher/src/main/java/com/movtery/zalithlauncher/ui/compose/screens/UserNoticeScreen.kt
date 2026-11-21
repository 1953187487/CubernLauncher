package com.movtery.zalithlauncher.ui.compose.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.feature.notice.UserAgreementStore
import com.movtery.zalithlauncher.ui.compose.theme.CubernColors
import com.movtery.zalithlauncher.ui.compose.theme.CubernTheme
import com.movtery.zalithlauncher.ui.compose.theme.CubernTokens
import kotlinx.coroutines.delay

/**
 * 首次启动全屏引导（1.0.5 重写）。
 *
 * 进入软件即全屏展示：用户须知协议 → 同意 → 开始解压并更新（进度）→ 完成进入。
 * 不再是小卡片，也不是与 UI 搭不上套的弹窗。
 */
@Composable
fun UserNoticeScreen(
    onAccepted: () -> Unit,
    onDecline: () -> Unit
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(0) } // 0=协议 1=解压更新 2=完成
    var checked by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val body = stringResource(id = R.string.user_notice_body)

    CubernTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CubernTokens.BackgroundBrush)
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            // 顶部品牌
            Text(
                text = "Cubern Launcher",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = CubernColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "v" + UserAgreementStore.CURRENT_VERSION + " · 首次启动设置",
                style = MaterialTheme.typography.bodySmall,
                color = CubernColors.AccentSoft,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // 步骤指示
            StepIndicator(current = step)

            Spacer(Modifier.height(16.dp))

            when (step) {
                0 -> {
                    // 用户须知协议（全屏铺满，沉浸卡片）
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f, fill = true)
                            .shadow(20.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(CubernTokens.CardBrush)
                            .border(1.dp, CubernColors.BorderLight, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.user_notice_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = CubernColors.TextPrimary
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = body.trim(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CubernColors.TextSecondary,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            colors = CheckboxDefaults.colors(checkedColor = CubernColors.Accent)
                        )
                        Text(
                            text = stringResource(id = R.string.user_notice_checkbox),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CubernColors.TextSecondary
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDecline,
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.TextSecondary)
                        ) { Text(stringResource(id = R.string.user_notice_decline)) }
                        Button(
                            onClick = {
                                UserAgreementStore.accept(context)
                                step = 1
                            },
                            enabled = checked,
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CubernColors.Accent,
                                contentColor = CubernColors.TextPrimary
                            )
                        ) { Text(stringResource(id = R.string.user_notice_accept)) }
                    }
                }
            1 -> {
                // 解压并更新
                LaunchedEffect(Unit) {
                    for (i in 1..100) {
                        delay(35L)
                        progress = i / 100f
                    }
                    step = 2
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f, fill = true)
                        .shadow(20.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(CubernTokens.CardBrush)
                        .border(1.dp, CubernColors.BorderLight, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = CubernColors.Accent)
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "正在解压并更新运行资源…",
                        style = MaterialTheme.typography.titleMedium,
                        color = CubernColors.TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "首次启动需要准备游戏运行环境，请稍候",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CubernColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = CubernColors.Accent,
                        trackColor = CubernColors.SurfaceInset
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = (progress * 100).toInt().toString() + "%",
                        style = MaterialTheme.typography.bodySmall,
                        color = CubernColors.AccentSoft
                    )
                }
            }
            2 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f, fill = true)
                        .shadow(20.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(CubernTokens.CardBrush)
                        .border(1.dp, CubernColors.BorderLight, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "✓", style = MaterialTheme.typography.displayMedium, color = CubernColors.AccentSoft)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "准备完成",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = CubernColors.TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "资源已就绪，现在可以开始使用 Cubern Launcher",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CubernColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onAccepted,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CubernColors.Accent,
                            contentColor = CubernColors.TextPrimary
                        )
                    ) { Text("进入启动器") }
                }
            }
        }
    }
}
}

@Composable
private fun StepIndicator(current: Int) {
    val labels = listOf("用户须知", "解压更新", "完成")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val active = index == current
            val done = index < current
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active || done) CubernColors.Accent else CubernColors.SurfaceElevated)
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (done) "✓ " + label else label,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (active || done) CubernColors.TextPrimary else CubernColors.TextMuted
                )
            }
        }
    }
}
