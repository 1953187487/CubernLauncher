package com.movtery.zalithlauncher.ui.compose.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.feature.notice.UserAgreementStore
import com.movtery.zalithlauncher.ui.compose.theme.CubernColors
import com.movtery.zalithlauncher.ui.compose.theme.CubernTheme
import com.movtery.zalithlauncher.ui.compose.theme.CubernTokens

@Composable
fun UserNoticeScreen(
    onAccepted: () -> Unit,
    onDecline: () -> Unit
) {
    val context = LocalContext.current
    var checked by remember { mutableStateOf(false) }
    val body = stringResource(id = R.string.user_notice_body)

    CubernTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CubernTokens.BackgroundBrush)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .shadow(24.dp, MaterialTheme.shapes.extraLarge)
                    .background(CubernTokens.CardBrush, MaterialTheme.shapes.extraLarge)
                    .border(1.dp, Brush.verticalGradient(listOf(CubernColors.BorderLight, CubernColors.BorderDark)), MaterialTheme.shapes.extraLarge)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.user_notice_title),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = CubernColors.TextPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.user_notice_version_info, UserAgreementStore.APP_NAME, "${UserAgreementStore.CURRENT_VERSION}"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CubernColors.TextSecondary
                )
                Spacer(Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(360.dp),
                    color = CubernColors.SurfaceInset,
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = body.trim(),
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CubernColors.TextPrimary
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = checked, onCheckedChange = { checked = it })
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
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.TextSecondary)
                    ) {
                        Text(stringResource(id = R.string.user_notice_decline))
                    }
                    Button(
                        onClick = {
                            UserAgreementStore.accept(context)
                            onAccepted()
                        },
                        enabled = checked,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CubernColors.Accent, contentColor = CubernColors.TextPrimary)
                    ) {
                        Text(stringResource(id = R.string.user_notice_accept))
                    }
                }
            }
        }
    }
}
