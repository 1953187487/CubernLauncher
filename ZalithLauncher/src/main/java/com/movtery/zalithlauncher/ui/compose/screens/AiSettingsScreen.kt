package com.movtery.zalithlauncher.ui.compose.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.feature.ai.AiConfigStore
import com.movtery.zalithlauncher.ui.compose.theme.CubernColors
import com.movtery.zalithlauncher.ui.compose.theme.CubernTokens

@Composable
fun AiSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    var enabled by remember { mutableStateOf(AiConfigStore.isEnabled(context)) }
    var baseUrl by remember { mutableStateOf(AiConfigStore.baseUrl(context)) }
    var model by remember { mutableStateOf(AiConfigStore.model(context)) }
    var apiKey by remember { mutableStateOf(AiConfigStore.apiKey(context).orEmpty()) }
    var stream by remember { mutableStateOf(AiConfigStore.isStreaming(context)) }
    var saveHistory by remember { mutableStateOf(AiConfigStore.isSaveHistory(context)) }
    var maxHistory by remember { mutableIntStateOf(AiConfigStore.maxHistory(context)) }
    var temperature by remember { mutableFloatStateOf(AiConfigStore.temperature(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CubernTokens.BackgroundBrush)
    ) {
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
                text = context.getString(R.string.ai_settings_title),
                style = MaterialTheme.typography.titleLarge,
                color = CubernColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = CubernColors.BorderDark)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionHeader(context.getString(R.string.ai_settings_general))
            SwitchRow(context.getString(R.string.ai_settings_enabled), enabled) {
                enabled = it
                AiConfigStore.setEnabled(context, it)
            }

            Spacer(Modifier.height(16.dp))

            SectionHeader(context.getString(R.string.ai_settings_service))
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text(context.getString(R.string.ai_settings_base_url), color = CubernColors.TextMuted) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text(context.getString(R.string.ai_settings_model), color = CubernColors.TextMuted) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text(context.getString(R.string.ai_settings_api_key), color = CubernColors.TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text(
                text = context.getString(R.string.ai_settings_api_key_hint),
                color = CubernColors.TextMuted,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(16.dp))

            SectionHeader(context.getString(R.string.ai_settings_behavior))
            SwitchRow(context.getString(R.string.ai_settings_stream), stream) {
                stream = it
                AiConfigStore.setStreaming(context, it)
            }
            SwitchRow(context.getString(R.string.ai_settings_save_history), saveHistory) {
                saveHistory = it
                AiConfigStore.setSaveHistory(context, it)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = context.getString(R.string.ai_settings_max_history, maxHistory),
                color = CubernColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = maxHistory.toFloat(),
                onValueChange = { maxHistory = it.toInt().coerceIn(1, 100) },
                valueRange = 1f..100f,
                steps = 19
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = context.getString(R.string.ai_settings_temperature, "%.1f".format(temperature)),
                color = CubernColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = temperature,
                onValueChange = { temperature = it },
                valueRange = 0f..2f,
                steps = 19
            )

            Spacer(Modifier.height(16.dp))

            SectionHeader(context.getString(R.string.ai_settings_actions))
            OutlinedButton(
                onClick = {
                    AiConfigStore.setBaseUrl(context, baseUrl)
                    AiConfigStore.setModel(context, model)
                    AiConfigStore.setMaxHistory(context, maxHistory)
                    AiConfigStore.setTemperature(context, temperature)
                    if (apiKey.isNotBlank()) {
                        AiConfigStore.setApiKey(context, apiKey)
                    } else {
                        AiConfigStore.clearApiKey(context)
                    }
                    Toast.makeText(context, R.string.ai_settings_saved, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.AccentSoft)
            ) {
                Text(context.getString(R.string.ai_settings_save))
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    AiHistoryStoreRef.clear(context)
                    Toast.makeText(context, R.string.ai_settings_cleared, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CubernColors.Danger)
            ) {
                Text(context.getString(R.string.ai_settings_clear_history))
            }

            Spacer(Modifier.height(16.dp))

            SectionHeader(context.getString(R.string.ai_settings_notice))
            Text(
                text = context.getString(R.string.user_notice_ai_excerpt),
                color = CubernColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private object AiHistoryStoreRef {
    fun clear(context: Context) {
        com.movtery.zalithlauncher.feature.ai.AiHistoryStore.clear(context)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = CubernColors.AccentSoft,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun SwitchRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = CubernColors.TextPrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = CubernColors.Accent,
                checkedThumbColor = CubernColors.TextPrimary
            )
        )
    }
}
