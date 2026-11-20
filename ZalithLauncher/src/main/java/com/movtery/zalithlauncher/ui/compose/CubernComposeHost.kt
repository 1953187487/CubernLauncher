package com.movtery.zalithlauncher.ui.compose

import android.content.Context
import android.view.View
import androidx.compose.ui.platform.ComposeView
import com.movtery.zalithlauncher.ui.compose.screens.AiAssistantScreen
import com.movtery.zalithlauncher.ui.compose.screens.AiSettingsScreen
import com.movtery.zalithlauncher.ui.compose.screens.TerracottaScreen
import com.movtery.zalithlauncher.ui.compose.screens.UserNoticeScreen

/**
 * Compose 承载桥。
 *
 * Java 侧的 Activity 通过本对象获取 Compose 视图，
 * 回调使用 Runnable 以避免 Java 与 @Composable 参数之间的互操作问题。
 */
object CubernComposeHost {

    fun createUserNoticeView(
        context: Context,
        onAccepted: Runnable,
        onDecline: Runnable
    ): View = createView(context) {
        UserNoticeScreen(onAccepted = onAccepted::run, onDecline = onDecline::run)
    }

    fun createAiAssistantView(
        context: Context,
        onBack: Runnable,
        onOpenSettings: Runnable
    ): View = createView(context) {
        AiAssistantScreen(onBack = onBack::run, onOpenSettings = onOpenSettings::run)
    }

    fun createTerracottaView(
        context: Context,
        onBack: Runnable
    ): View = createView(context) {
        TerracottaScreen(onBack = onBack::run)
    }

    fun createAiSettingsView(
        context: Context,
        onBack: Runnable
    ): View = createView(context) {
        AiSettingsScreen(onBack = onBack::run)
    }

    private fun createView(context: Context, content: @androidx.compose.runtime.Composable () -> Unit): View {
        val view = ComposeView(context)
        view.setContent(content)
        return view
    }
}
