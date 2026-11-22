package com.movtery.zalithlauncher.ui.compose

import android.content.Context
import android.view.View
import androidx.compose.ui.platform.ComposeView
import com.movtery.zalithlauncher.ui.compose.screens.AboutLauncherScreen
import com.movtery.zalithlauncher.ui.compose.screens.AiAssistantScreen
import com.movtery.zalithlauncher.ui.compose.screens.AiSettingsScreen
import com.movtery.zalithlauncher.ui.compose.screens.TerracottaScreen
import com.movtery.zalithlauncher.ui.compose.screens.UserNoticeScreen
import com.movtery.zalithlauncher.ui.compose.screens.SkinPreviewScreen
import com.movtery.zalithlauncher.ui.compose.screens.RustMcScreen

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

    fun createAboutLauncherView(
        context: Context,
        onBack: Runnable
    ): View = createView(context) {
        AboutLauncherScreen(onBack = onBack::run)
    }

    fun createAiSettingsView(
        context: Context,
        onBack: Runnable
    ): View = createView(context) {
        AiSettingsScreen(onBack = onBack::run)
    }

    /** 1.0.8：Rust 版 MC（Horizon Android）专区 */
    @JvmStatic
    fun createRustMcView(context: Context, onBack: Runnable): View = createView(context) {
        RustMcScreen(onBack = onBack::run)
    }

    /** 1.0.6：皮肤 3D 预览（含旋转、动作、跳舞） */
    @JvmStatic
    fun createSkinPreviewView(
        context: Context,
        skinName: String,
        onBack: Runnable
    ): View = createView(context) {
        SkinPreviewScreen(skinName = skinName, onBack = onBack::run)
    }

    private fun createView(context: Context, content: @androidx.compose.runtime.Composable () -> Unit): View {
        val view = ComposeView(context)
        view.setContent(content)
        return view
    }
}
