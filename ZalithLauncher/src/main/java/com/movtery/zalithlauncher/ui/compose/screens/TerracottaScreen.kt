package com.movtery.zalithlauncher.ui.compose.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.terracotta.Terracotta
import com.movtery.zalithlauncher.terracotta.TerracottaPrefs
import com.movtery.zalithlauncher.terracotta.TerracottaState
import com.movtery.zalithlauncher.terracotta.TerracottaVPNService
import com.movtery.zalithlauncher.terracotta.fetchNodes
import com.movtery.zalithlauncher.ui.compose.theme.CubernColors
import com.movtery.zalithlauncher.ui.compose.theme.CubernTokens
import net.burningtnt.terracotta.TerracottaAndroidAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Composable
fun TerracottaScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var state by remember { mutableStateOf<TerracottaState.Ready?>(null) }
    var roomCode by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }
    var showLog by remember { mutableStateOf(false) }
    var logContent by remember { mutableStateOf("") }
    var nodeText by remember { mutableStateOf("") }
    var initFailed by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val noticeRead = TerracottaPrefs.noticeVersion >= Terracotta.TERRACOTTA_USER_NOTICE_VERSION
    var enableMultiplayer by remember { mutableStateOf(true) }
    var enableCustomNodes by remember { mutableStateOf(TerracottaPrefs.enableCustomNodes) }

    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val vpnIntent = Intent(context, TerracottaVPNService::class.java).apply {
                action = TerracottaVPNService.ACTION_START
            }
            ContextCompat.startForegroundService(context, vpnIntent)
        } else {
            try {
                TerracottaAndroidAPI.getPendingVpnServiceRequest().reject()
            } catch (ignored: IllegalStateException) {
            }
            Terracotta.setWaiting(true)
            toast(context, context.getString(R.string.terracotta_permission_vpn_denied))
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Terracotta.Listener {
            override fun onRequestVpnPermission() {
                val intent = VpnService.prepare(context)
                if (intent != null) {
                    vpnLauncher.launch(intent)
                } else {
                    val vpnIntent = Intent(context, TerracottaVPNService::class.java).apply {
                        action = TerracottaVPNService.ACTION_START
                    }
                    ContextCompat.startForegroundService(context, vpnIntent)
                }
            }

            override fun onStateChanged(newState: TerracottaState.Ready) {
                state = newState
            }
        }

        try {
            if (enableMultiplayer) {
                Terracotta.initialize(CoroutineScope(scope.coroutineContext + SupervisorJob()), listener)
            }
        } catch (throwable: Throwable) {
            initFailed = true
        }
        onDispose { }
    }

    LaunchedEffect(Unit) {
        runCatching { fetchNodes() }
            .onSuccess { nodes -> nodeText = nodes.joinToString(", ") { it.toString() } }
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
                text = context.getString(R.string.terracotta_menu),
                style = MaterialTheme.typography.titleLarge,
                color = CubernColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = CubernColors.BorderDark)

        // 顶部标签：用户须知 / 房主教程 / 房客教程
        val tabTitles = listOf(
            context.getString(R.string.terracotta_tab_notice),
            context.getString(R.string.terracotta_tab_host_tutorial),
            context.getString(R.string.terracotta_tab_guest_tutorial)
        )
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CubernColors.SurfaceElevated,
            contentColor = CubernColors.Accent
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { if (index == 0 || noticeRead) selectedTab = index },
                    text = { Text(title, color = if (selectedTab == index) CubernColors.Accent else CubernColors.TextMuted) }
                )
            }
        }

        when (selectedTab) {
            0 -> NoticeTab(
                onUnderstand = {
                    TerracottaPrefs.noticeVersion = Terracotta.TERRACOTTA_USER_NOTICE_VERSION
                    selectedTab = 1
                },
                enableMultiplayer = enableMultiplayer,
                onEnableMultiplayerChanged = {
                    enableMultiplayer = it
                    if (it) {
                        runCatching {
                            Terracotta.initialize(CoroutineScope(scope.coroutineContext + SupervisorJob()), object : Terracotta.Listener {
                                override fun onRequestVpnPermission() {
                                    val intent = VpnService.prepare(context)
                                    if (intent != null) vpnLauncher.launch(intent)
                                }
                                override fun onStateChanged(newState: TerracottaState.Ready) { state = newState }
                            })
                        }
                    }
                },
                enableCustomNodes = enableCustomNodes,
                onEnableCustomNodesChanged = {
                    enableCustomNodes = it
                    TerracottaPrefs.enableCustomNodes = it
                },
                onShareLogs = {
                    Terracotta.collectLogs()?.let { logs ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, logs)
                        }
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.terracotta_share_logs)))
                    }
                },
                state = state,
                initFailed = initFailed,
                roomCode = roomCode,
                playerName = playerName,
                onRoomCodeChange = { roomCode = it },
                onPlayerNameChange = { playerName = it },
                onHost = {
                    scope.launch {
                        runCatching {
                            Terracotta.setWaiting(true)
                            val nodes = fetchNodes().map { it.toString() }
                            Terracotta.setScanning(null, playerName.ifBlank { null }, nodes.ifEmpty { null })
                        }.onFailure { toast(context, context.getString(R.string.terracotta_init_failed)) }
                    }
                },
                onGuest = {
                    scope.launch {
                        runCatching {
                            if (roomCode.isBlank()) {
                                toast(context, context.getString(R.string.terracotta_room_code_invalid))
                                return@launch
                            }
                            Terracotta.setWaiting(true)
                            val nodes = fetchNodes().map { it.toString() }
                            Terracotta.setGuesting(roomCode, playerName.ifBlank { null }, nodes.ifEmpty { null })
                        }.onFailure { toast(context, context.getString(R.string.terracotta_room_code_invalid)) }
                    }
                },
                onReset = { Terracotta.setWaiting(true); state = null },
                showLog = showLog,
                logContent = logContent,
                onToggleLog = {
                    showLog = !showLog
                    if (showLog) logContent = Terracotta.collectLogs().orEmpty()
                },
                nodeText = nodeText
            )
            1 -> TutorialTab(context.getString(R.string.terracotta_host_tutorial_steps))
            2 -> TutorialTab(context.getString(R.string.terracotta_guest_tutorial_steps))
        }
    }
}

@Composable
private fun NoticeTab(
    onUnderstand: () -> Unit,
    enableMultiplayer: Boolean,
    onEnableMultiplayerChanged: (Boolean) -> Unit,
    enableCustomNodes: Boolean,
    onEnableCustomNodesChanged: (Boolean) -> Unit,
    onShareLogs: () -> Unit,
    state: TerracottaState.Ready?,
    initFailed: Boolean,
    roomCode: String,
    playerName: String,
    onRoomCodeChange: (String) -> Unit,
    onPlayerNameChange: (String) -> Unit,
    onHost: () -> Unit,
    onGuest: () -> Unit,
    onReset: () -> Unit,
    showLog: Boolean,
    logContent: String,
    onToggleLog: () -> Unit,
    nodeText: String
) {
    val context = LocalContext.current
    val noticeRead = TerracottaPrefs.noticeVersion >= Terracotta.TERRACOTTA_USER_NOTICE_VERSION

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 用户须知文本（可滚动，不截断）
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CubernColors.SurfaceInset,
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = context.getString(R.string.terracotta_notice_body),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CubernColors.TextPrimary
                )
            }

            Spacer(Modifier.height(16.dp))

            SwitchRow(
                title = context.getString(R.string.terracotta_enable_switch),
                checked = enableMultiplayer,
                onCheckedChange = onEnableMultiplayerChanged
            )
            SwitchRow(
                title = context.getString(R.string.terracotta_custom_nodes_switch),
                checked = enableCustomNodes,
                onCheckedChange = onEnableCustomNodesChanged
            )

            Spacer(Modifier.height(8.dp))

            if (initFailed) {
                Text(
                    text = context.getString(R.string.terracotta_native_unavailable),
                    color = CubernColors.Danger,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
            }

            SectionTitle(context.getString(R.string.terracotta_nodes))
            Text(
                text = nodeText.ifBlank { context.getString(R.string.terracotta_waiting) },
                color = CubernColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))

            if (enableMultiplayer && !initFailed) {
                SectionTitle(context.getString(R.string.terracotta_connection_mode))
                StateContent(state)
                Spacer(Modifier.height(8.dp))
                OutlinedAction(context.getString(R.string.terracotta_host_mode), onHost)
                OutlinedAction(context.getString(R.string.terracotta_guest_mode), onGuest)
                OutlinedAction(context.getString(R.string.terracotta_reset), onReset)
            }

            Spacer(Modifier.height(8.dp))
            OutlinedAction(
                if (showLog) context.getString(R.string.terracotta_hide_log) else context.getString(R.string.terracotta_show_log),
                onToggleLog
            )
            if (showLog && logContent.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = logContent,
                    color = CubernColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(CubernColors.SurfaceInset)
                        .padding(8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            OutlinedAction(context.getString(R.string.terracotta_share_logs), onShareLogs)
            OutlinedAction(context.getString(R.string.terracotta_about_easytier), {
                // 关于 EasyTier 提示
                toast(context, context.getString(R.string.terracotta_about_easytier_desc))
            })
        }

        // 底部固定「我已了解」按钮
        Surface(color = CubernColors.SurfaceElevated) {
            Button(
                onClick = onUnderstand,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CubernColors.Accent)
            ) {
                Text(context.getString(R.string.terracotta_notice_understand))
            }
        }
    }
}

@Composable
private fun TutorialTab(steps: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CubernColors.SurfaceInset,
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = steps,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = CubernColors.TextPrimary
            )
        }
    }
}

@Composable
private fun SwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = CubernColors.TextPrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = CubernColors.Accent,
                checkedThumbColor = CubernColors.TextPrimary
            )
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = CubernColors.AccentSoft,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun OutlinedAction(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = CubernColors.Surface,
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, CubernColors.BorderDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, color = CubernColors.TextPrimary, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun StateContent(state: TerracottaState.Ready?) {
    val context = LocalContext.current
    when (state) {
        null, is TerracottaState.Waiting -> Text(context.getString(R.string.terracotta_waiting), color = CubernColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        is TerracottaState.HostScanning -> Text(context.getString(R.string.terracotta_status_host_scanning), color = CubernColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        is TerracottaState.HostStarting -> Text(context.getString(R.string.terracotta_status_host_starting), color = CubernColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        is TerracottaState.HostOK -> {
            Text(context.getString(R.string.terracotta_status_host_ok), color = CubernColors.AccentSoft, style = MaterialTheme.typography.bodyLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${context.getString(R.string.terracotta_status_host_ok_code)}: ${state.code.orEmpty()}", color = CubernColors.TextPrimary)
                Spacer(Modifier.padding(horizontal = 6.dp))
                TextButton(onClick = {
                    copyToClipboard(context, state.code.orEmpty())
                    toast(context, context.getString(R.string.terracotta_room_code_copied))
                }) { Text(context.getString(R.string.terracotta_status_host_ok_code_copy), color = CubernColors.AccentSoft) }
            }
        }
        is TerracottaState.GuestConnecting, is TerracottaState.GuestStarting -> Text(context.getString(R.string.terracotta_status_guest_starting), color = CubernColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        is TerracottaState.GuestOK -> Text(context.getString(R.string.terracotta_status_guest_ok), color = CubernColors.AccentSoft, style = MaterialTheme.typography.bodyLarge)
        is TerracottaState.Exception -> {
            Text(context.getString(R.string.terracotta_status_exception), color = CubernColors.Danger, style = MaterialTheme.typography.bodyLarge)
            Text(context.getString(state.getEnumType().textRes), color = CubernColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        else -> Text(context.getString(R.string.terracotta_waiting), color = CubernColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText("terracotta", text))
}

private fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
