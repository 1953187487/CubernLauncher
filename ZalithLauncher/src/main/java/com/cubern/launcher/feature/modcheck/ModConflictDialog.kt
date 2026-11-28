package com.cubern.launcher.feature.modcheck

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

/** 模组冲突检测结果提示：列出冲突，点击前往模组下载页搜索替代模组 */
@Composable
fun ModConflictDialog(conflicts: List<ModConflictChecker.Conflict>, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("模组冲突检测", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                if (conflicts.isEmpty()) {
                    Text("喵！没有发现冲突，模组状态很健康~")
                } else {
                    Text("发现 " + conflicts.size + " 处冲突喵：", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    conflicts.take(8).forEach { c ->
                        Text("• " + c.message, style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = {
                            val url = "https://modrinth.com/mods?q=" + java.net.URLEncoder.encode(c.suggestedQuery, "UTF-8")
                            try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (_: Exception) {}
                        }) { Text("前往下载替代模组喵") }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("知道喵") } },
    )
}
