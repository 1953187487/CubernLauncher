package com.movtery.cubernlauncher.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.feature.ai.AiAssistant
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack

/**
 * AI 版本管理助手界面
 *
 * 设计思路参考 Amethyst-iOS 与 MyRemasteredAI：
 * - 离线即可完成版本体检与运行时匹配
 * - 配置用户自有 API Key 后可进行自然语言问答
 */
@Composable
fun AiAssistantScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var reports by remember { mutableStateOf(emptyList<AiAssistant.VersionHealthReport>()) }
    var advice by remember { mutableStateOf(emptyList<String>()) }
    var answer by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        reports = AiAssistant.inspectAllVersions()
        advice = AiAssistant.buildManagementAdvice()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = context.getString(R.string.ai_feature_title),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.ArrowBack,
                            contentDescription = context.getString(R.string.ai_feature_title)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.ai_feature_title),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(text = context.getString(R.string.ai_feature_desc))
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.ai_feature_advice),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    advice.forEach { line ->
                        Text(text = "· $line", modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }

            if (reports.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        insideMargin = DpSize(16.dp, 16.dp)
                    ) {
                        Text(
                            text = context.getString(R.string.ai_feature_health),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                items(reports) { report ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        insideMargin = DpSize(16.dp, 8.dp)
                    ) {
                        Text(text = report.versionName)
                        Text(
                            text = context.getString(
                                R.string.ai_feature_java_requirement,
                                report.requiredJavaVersion
                            )
                        )
                        Text(
                            text = context.getString(
                                R.string.ai_feature_runtime_matched,
                                report.matchedRuntime ?: context.getString(R.string.ai_feature_runtime_missing)
                            )
                        )
                        report.issues.forEach { issue ->
                            Text(text = "· $issue")
                        }
                        report.suggestions.forEach { suggestion ->
                            Text(text = "→ $suggestion")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.ai_feature_ask),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = context.getString(
                            if (AiAssistant.hasUserApiKey()) {
                                R.string.ai_feature_api_ready
                            } else {
                                R.string.ai_feature_api_missing
                            }
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    if (answer.isNotBlank()) {
                        Text(text = answer, modifier = Modifier.padding(bottom = 10.dp))
                    }
                    Button(
                        text = context.getString(R.string.ai_feature_analyze),
                        onClick = {
                            reports = AiAssistant.inspectAllVersions()
                            advice = AiAssistant.buildManagementAdvice()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        text = context.getString(R.string.ai_feature_ask_button),
                        onClick = {
                            AiAssistant.ask(context, context.getString(R.string.ai_feature_ask_prompt)) { result ->
                                answer = result ?: context.getString(R.string.ai_feature_api_missing)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
