package com.movtery.zalithlauncher.ui.compose.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.compose.theme.CubernColors
import com.movtery.zalithlauncher.ui.compose.theme.CubernTheme
import com.movtery.zalithlauncher.ui.compose.theme.CubernTokens
import kotlinx.coroutines.delay

/**
 * 皮肤 3D 预览（1.0.6）。
 * 使用 Compose Canvas 绘制 Minecraft 风格角色，支持旋转、动作切换（站立/行走/挥手/跳舞）。
 * 无外部 3D 引擎依赖，避免体积与构建风险。
 */
@Composable
fun SkinPreviewScreen(skinName: String, onBack: () -> Unit) {
    var angle by remember { mutableStateOf(0f) }
    var action by remember { mutableStateOf(SkinAction.IDLE) }
    var dancing by remember { mutableStateOf(false) }

    LaunchedEffect(dancing) {
        while (dancing) {
            delay(50L)
            angle = (angle + 6f) % 360f
        }
    }
    val animatedAngle by animateFloatAsState(angle, tween(80))

    CubernTheme {
        Column(Modifier.fillMaxSize().background(CubernTokens.BackgroundBrush)) {
            Row(Modifier.fillMaxWidth().background(CubernColors.SurfaceElevated).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("‹", style = MaterialTheme.typography.titleLarge, color = CubernColors.AccentSoft,
                    modifier = Modifier.clickable { onBack() }.padding(end = 12.dp))
                Text("角色皮肤预览", style = MaterialTheme.typography.titleLarge, color = CubernColors.TextPrimary)
            }

            Box(Modifier.weight(1f).fillMaxWidth().padding(16.dp).shadow(16.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp)).background(CubernTokens.CardBrush),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Canvas(modifier = Modifier.size(240.dp)) {
                        drawSkinFigure(animatedAngle, action, dancing)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(skinName, style = MaterialTheme.typography.titleMedium, color = CubernColors.TextPrimary)
                    Text("动作: " + action.label, style = MaterialTheme.typography.bodySmall, color = CubernColors.TextMuted)
                }
            }

            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkinAction.entries.forEach { act ->
                        OutlinedButton(onClick = { action = act; dancing = false }, modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (action == act) CubernColors.AccentSoft else CubernColors.TextSecondary)) {
                            Text(act.label, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { angle = (angle + 45f) % 360f }, modifier = Modifier.weight(1f)) { Text("旋转 ↻") }
                    Button(onClick = { dancing = !dancing; if (dancing) action = SkinAction.DANCE }, modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CubernColors.Accent)) {
                        Text(if (dancing) "停止跳舞" else "跳舞 💃")
                    }
                }
            }
        }
    }
}

private enum class SkinAction(val label: String) {
    IDLE("站立"), WALK("行走"), WAVE("挥手"), DANCE("跳舞")
}

/** 简化的 Minecraft 风格角色绘制：随角度产生宽度透视变化，随动作改变手臂/腿部姿态。 */
private fun DrawScope.drawSkinFigure(angle: Float, action: SkinAction, dancing: Boolean) {
    val rad = Math.toRadians(angle.toDouble())
    val perspective = (Math.cos(rad).toFloat() + 1f) / 2f * 0.4f + 0.6f
    val cx = size.width / 2
    val scale = size.height / 220f
    val w = 44f * scale * perspective
    val bodyH = 70f * scale
    val headS = 46f * scale
    val bob = if (dancing || action == SkinAction.WALK) (Math.sin(angle.toDouble() * Math.PI / 180 * 2).toFloat() * 8f * scale) else 0f

    // 影子
    drawOval(Color(0x33000000), topLeft = Offset(cx - w * 1.1f, size.height - 18f), size = androidx.compose.ui.geometry.Size(w * 2.2f, 12f))
    val top = 20f * scale + bob

    // 头
    drawRect(CubernColors.AccentSoft, topLeft = Offset(cx - headS / 2, top), size = androidx.compose.ui.geometry.Size(headS, headS))
    // 眼睛
    val eyeY = top + headS * 0.35f
    drawRect(Color.White, topLeft = Offset(cx - headS * 0.28f, eyeY), size = androidx.compose.ui.geometry.Size(headS * 0.2f, headS * 0.16f))
    drawRect(Color.White, topLeft = Offset(cx + headS * 0.08f, eyeY), size = androidx.compose.ui.geometry.Size(headS * 0.2f, headS * 0.16f))
    // 身体
    val bodyY = top + headS + 4f * scale
    drawRect(CubernColors.Accent, topLeft = Offset(cx - w / 2, bodyY), size = androidx.compose.ui.geometry.Size(w, bodyH))
    // 手臂（挥手/跳舞时上举）
    val armLen = 56f * scale
    val armRise = if (action == SkinAction.WAVE || dancing) -30f * scale else 0f
    drawRect(CubernColors.TextSecondary, topLeft = Offset(cx - w / 2 - 14f * scale, bodyY + armRise), size = androidx.compose.ui.geometry.Size(14f * scale, armLen))
    drawRect(CubernColors.TextSecondary, topLeft = Offset(cx + w / 2, bodyY - armRise), size = androidx.compose.ui.geometry.Size(14f * scale, armLen))
    // 腿（行走/跳舞时交替）
    val legY = bodyY + bodyH
    val legSwing = if (action == SkinAction.WALK || dancing) Math.sin(angle.toDouble() * Math.PI / 180).toFloat() * 12f * scale else 0f
    drawRect(CubernColors.SurfaceElevated, topLeft = Offset(cx - w / 2 + legSwing, legY), size = androidx.compose.ui.geometry.Size(w * 0.42f, 52f * scale))
    drawRect(CubernColors.SurfaceElevated, topLeft = Offset(cx + w * 0.08f - legSwing, legY), size = androidx.compose.ui.geometry.Size(w * 0.42f, 52f * scale))
}
