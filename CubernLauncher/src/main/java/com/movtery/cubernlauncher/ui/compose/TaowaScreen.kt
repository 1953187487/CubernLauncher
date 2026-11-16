package com.movtery.cubernlauncher.ui.compose

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.terracotta.Terracotta
import com.movtery.cubernlauncher.terracotta.TerracottaState
import com.movtery.cubernlauncher.terracotta.TerracottaVPNService
import com.movtery.cubernlauncher.terracotta.fetchNodes
import net.burningtnt.terracotta.TerracottaAndroidAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack

/**
 * 陶瓦联机界面
 *
 * 移植自 Zalith Launcher 2 的陶瓦联机功能，界面使用 MIUIX 重写。
 */
@Composable
fun TaowaScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var state by remember { mutableStateOf<TerracottaState.Ready?>(null) }
    var roomCode by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }
    var showLog by remember { mutableStateOf(false) }
    var logContent by remember { mutableStateOf("") }
    var nodeText by remember { mutableStateOf("") }
    var initFailed by remember { mutableStateOf(false) }

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
            Terracotta.initialize(CoroutineScope(scope.coroutineContext + SupervisorJob()), listener)
        } catch (throwable: Throwable) {
            initFailed = true
        }

        onDispose { }
    }

    LaunchedEffect(Unit) {
        runCatching { fetchNodes() }
            .onSuccess { nodes -> nodeText = nodes.joinToString(", ") { it.toString() } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = context.getString(R.string.terracotta_menu),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.ArrowBack,
                            contentDescription = context.getString(R.string.terracotta_menu)
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
                        text = context.getString(R.string.terracotta_menu),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(text = context.getString(R.string.terracotta_menu_desc))
                }
            }

            if (initFailed) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        insideMargin = DpSize(16.dp, 16.dp)
                    ) {
                        Text(text = context.getString(R.string.terracotta_native_unavailable))
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.terracotta_waiting),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    StateContent(state = state)

                    Spacer(modifier = Modifier.height(12.dp))

                    SuperArrow(
                        title = context.getString(R.string.terracotta_host_mode),
                        summary = context.getString(R.string.terracotta_host_mode_desc),
                        onClick = {
                            scope.launch {
                                runCatching {
                                    Terracotta.setWaiting(true)
                                    val nodes = fetchNodes().map { it.toString() }
                                    Terracotta.setScanning(null, playerName.ifBlank { null }, nodes.ifEmpty { null })
                                }.onFailure {
                                    toast(context, context.getString(R.string.terracotta_init_failed))
                                }
                            }
                        }
                    )
                    SuperArrow(
                        title = context.getString(R.string.terracotta_guest_mode),
                        summary = context.getString(R.string.terracotta_guest_mode_desc),
                        onClick = {
                            scope.launch {
                                runCatching {
                                    if (roomCode.isBlank()) {
                                        toast(context, context.getString(R.string.terracotta_room_code_invalid))
                                        return@launch
                                    }
                                    Terracotta.setWaiting(true)
                                    val nodes = fetchNodes().map { it.toString() }
                                    Terracotta.setGuesting(roomCode, playerName.ifBlank { null }, nodes.ifEmpty { null })
                                }.onFailure {
                                    toast(context, context.getString(R.string.terracotta_room_code_invalid))
                                }
                            }
                        }
                    )
                    SuperArrow(
                        title = context.getString(R.string.terracotta_reset),
                        onClick = {
                            Terracotta.setWaiting(true)
                            state = null
                        }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.terracotta_room_code),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TextField(
                        value = roomCode,
                        onValueChange = { roomCode = it },
                        label = context.getString(R.string.terracotta_room_code_hint),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = context.getString(R.string.terracotta_player_name_hint),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.terracotta_nodes),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(text = nodeText.ifBlank { context.getString(R.string.terracotta_waiting) })
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = DpSize(16.dp, 16.dp)
                ) {
                    SuperArrow(
                        title = if (showLog) {
                            context.getString(R.string.terracotta_hide_log)
                        } else {
                            context.getString(R.string.terracotta_show_log)
                        },
                        onClick = {
                            showLog = !showLog
                            if (showLog) {
                                logContent = Terracotta.collectLogs().orEmpty()
                            }
                        }
                    )
                    if (showLog) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = logContent)
                    }
                }
            }
        }
    }
}

@Composable
private fun StateContent(state: TerracottaState.Ready?) {
    val context = LocalContext.current
    when (state) {
        null -> Text(text = context.getString(R.string.terracotta_waiting))
        is TerracottaState.Waiting -> Text(text = context.getString(R.string.terracotta_waiting))
        is TerracottaState.HostScanning -> {
            Text(text = context.getString(R.string.terracotta_status_host_scanning))
            Text(text = context.getString(R.string.terracotta_status_host_scanning_desc))
        }
        is TerracottaState.HostStarting -> Text(text = context.getString(R.string.terracotta_status_host_starting))
        is TerracottaState.HostOK -> {
            Text(text = context.getString(R.string.terracotta_status_host_ok))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${context.getString(R.string.terracotta_status_host_ok_code)}: ${state.code.orEmpty()}")
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Button(
                    text = context.getString(R.string.terracotta_status_host_ok_code_copy),
                    onClick = {
                        copyToClipboard(context, state.code.orEmpty())
                        toast(context, context.getString(R.string.terracotta_room_code_copied))
                    }
                )
            }
        }
        is TerracottaState.GuestConnecting -> Text(text = context.getString(R.string.terracotta_status_guest_starting))
        is TerracottaState.GuestStarting -> Text(text = context.getString(R.string.terracotta_status_guest_starting))
        is TerracottaState.GuestOK -> Text(text = context.getString(R.string.terracotta_status_guest_ok))
        is TerracottaState.Exception -> {
            Text(text = context.getString(R.string.terracotta_status_exception))
            Text(text = context.getString(state.getEnumType().textRes))
        }
        else -> Text(text = context.getString(R.string.terracotta_waiting))
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText("terracotta", text))
}

private fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
