package com.movtery.zalithlauncher.ui.compose.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.feature.ai.AiChatMessage
import com.movtery.zalithlauncher.feature.ai.AiChatViewModel
import com.movtery.zalithlauncher.feature.ai.AiConfigStore
import com.movtery.zalithlauncher.ui.compose.theme.CubernColors
import com.movtery.zalithlauncher.ui.compose.theme.CubernTokens

@Composable
fun AiAssistantScreen(
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val vm: AiChatViewModel = viewModel()
    val messages by vm.messages.collectAsState()
    val isGenerating by vm.isGenerating.collectAsState()
    val error by vm.error.collectAsState()
    var input by remember { mutableStateOf("") }
    var showLogConfirm by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        vm.attach(context, context.getString(R.string.ai_welcome))
    }

    LaunchedEffect(messages.size, messages.lastOrNull()?.content) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CubernTokens.BackgroundBrush)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CubernColors.SurfaceElevated)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(context.getString(R.string.generic_return), color = CubernColors.AccentSoft)
            }
            Text(
                text = context.getString(R.string.ai_feature_title),
                style = MaterialTheme.typography.titleLarge,
                color = CubernColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showLogConfirm = true }) {
                Text(context.getString(R.string.ai_analyze_log), color = CubernColors.AccentSoft, style = MaterialTheme.typography.labelLarge)
            }
            IconButton(onClick = {
                vm.clearSession(context)
            }) {
                Text(context.getString(R.string.ai_clear_session), color = CubernColors.TextSecondary, style = MaterialTheme.typography.labelLarge)
            }
        }
        HorizontalDivider(color = CubernColors.BorderDark)

        // 消息列表
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.hashCode() }) { msg ->
                MessageBubble(msg)
            }

            if (error != null) {
                item {
                    ErrorNotice(error!!, onRetry = { vm.retryLast(context) }, onOpenSettings = onOpenSettings, onDismiss = { vm.clearError() })
                }
            }
        }

        // 上传日志前的隐私告知（需求 10.9）
        if (showLogConfirm) {
            AlertDialog(
                onDismissRequest = { showLogConfirm = false },
                title = { Text(context.getString(R.string.ai_log_dialog_title)) },
                text = { Text(context.getString(R.string.ai_log_dialog_body)) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogConfirm = false
                        vm.analyzeLog(context)
                    }) { Text(context.getString(R.string.ai_log_dialog_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { showLogConfirm = false }) { Text(context.getString(R.string.generic_close)) }
                }
            )
        }

        // 输入区
        Surface(color = CubernColors.SurfaceElevated) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(context.getString(R.string.ai_input_hint), color = CubernColors.TextMuted) },
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp)
                    )
                    if (isGenerating) {
                        Button(
                            onClick = { vm.stop() },
                            colors = ButtonDefaults.buttonColors(containerColor = CubernColors.Danger)
                        ) {
                            Text(context.getString(R.string.ai_stop))
                        }
                    } else {
                        Button(
                            onClick = {
                                if (input.isNotBlank()) {
                                    vm.send(input)
                                    input = ""
                                }
                            },
                            enabled = input.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = CubernColors.Accent)
                        ) {
                            Text(context.getString(R.string.ai_send))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: AiChatMessage) {
    val context = LocalContext.current
    val isUser = msg.role == AiChatMessage.ROLE_USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isUser) Brush.horizontalGradient(listOf(CubernColors.Accent, CubernColors.AccentCyan))
                    else Brush.verticalGradient(listOf(CubernColors.SurfaceElevated, CubernColors.Surface))
                )
                .border(1.dp, if (isUser) Color.Transparent else CubernColors.BorderDark, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (msg.isStreaming) {
                Text(
                    text = msg.content.ifEmpty { context.getString(R.string.ai_welcome).take(0) },
                    color = CubernColors.TextPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (msg.content.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp).padding(1.dp),
                        color = CubernColors.AccentSoft,
                        strokeWidth = 2.dp
                    )
                }
            } else if (msg.isError) {
                Text(
                    text = context.getString(R.string.ai_network_error),
                    color = CubernColors.Danger,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = msg.content.ifBlank { context.getString(R.string.ai_empty_reply) },
                    color = if (isUser) CubernColors.TextPrimary else CubernColors.TextPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (!isUser && !msg.isStreaming && msg.content.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = context.getString(R.string.ai_copy),
                    color = CubernColors.AccentSoft,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable { copy(context, msg.content) }
                )
            }
        }
    }
}

@Composable
private fun ErrorNotice(
    message: String,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Surface(
        color = CubernColors.SurfaceInset,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CubernColors.Warning, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = when (message) {
                    "not_configured" -> context.getString(R.string.ai_not_configured)
                    "sensitive" -> "该内容涉及敏感信息，已阻止发送。"
                    "disabled" -> "AI 助手已停用。"
                    else -> context.getString(R.string.ai_network_error)
                },
                color = CubernColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenSettings) {
                    Text(context.getString(R.string.main_setting), color = CubernColors.AccentSoft)
                }
                OutlinedButton(onClick = onRetry) {
                    Text(context.getString(R.string.ai_retry), color = CubernColors.AccentSoft)
                }
                TextButton(onClick = onDismiss) {
                    Text(context.getString(R.string.generic_close), color = CubernColors.TextMuted)
                }
            }
        }
    }
}

private fun copy(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText("ai_reply", text))
    Toast.makeText(context, context.getString(R.string.ai_copied), Toast.LENGTH_SHORT).show()
}
