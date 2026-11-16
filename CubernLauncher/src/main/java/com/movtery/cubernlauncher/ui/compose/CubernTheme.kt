package com.movtery.cubernlauncher.ui.compose

import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Cubern Launcher 的 MIUIX 主题
 * 采用 MIUIX 提供的配色与字体规范，保证 HyperOS 风格一致性
 */
@Composable
fun CubernTheme(content: @Composable () -> Unit) {
    MiuixTheme(content = content)
}
