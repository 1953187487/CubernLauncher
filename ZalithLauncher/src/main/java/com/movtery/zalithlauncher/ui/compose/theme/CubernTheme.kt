package com.movtery.zalithlauncher.ui.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = CubernColors.Accent,
    onPrimary = CubernColors.TextPrimary,
    secondary = CubernColors.AccentCyan,
    background = CubernColors.BackgroundTop,
    surface = CubernColors.Surface,
    surfaceVariant = CubernColors.SurfaceElevated,
    onSurface = CubernColors.TextPrimary,
    onSurfaceVariant = CubernColors.TextSecondary,
    error = CubernColors.Danger,
    outline = CubernColors.BorderLight
)

val CubernShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val CubernTypography = Typography(
    headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = CubernColors.TextPrimary),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = CubernColors.TextPrimary),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = CubernColors.TextPrimary),
    bodyLarge = TextStyle(fontSize = 15.sp, color = CubernColors.TextPrimary),
    bodyMedium = TextStyle(fontSize = 14.sp, color = CubernColors.TextSecondary),
    bodySmall = TextStyle(fontSize = 12.sp, color = CubernColors.TextMuted),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CubernColors.TextPrimary)
)

object CubernTokens {
    val BackgroundBrush = Brush.verticalGradient(listOf(CubernColors.BackgroundTop, CubernColors.BackgroundBottom))
    val CardBrush = Brush.verticalGradient(listOf(CubernColors.SurfaceElevated, CubernColors.Surface))
    val AccentBrush = Brush.horizontalGradient(listOf(CubernColors.Accent, CubernColors.AccentCyan))
}

@Composable
fun CubernTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = CubernTypography,
        shapes = CubernShapes,
        content = content
    )
}
