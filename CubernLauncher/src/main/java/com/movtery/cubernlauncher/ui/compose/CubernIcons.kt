package com.movtery.cubernlauncher.ui.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Cubern Launcher 自绘图标
 * 使用内置矢量路径，避免额外图标库依赖
 */
object CubernIcons {

    /**
     * 联机组图标
     */
    val Multiplayer: ImageVector by lazy {
        ImageVector.Builder(
            name = "CubernMultiplayer",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9f, 11f)
                curveTo(6.24f, 11f, 4f, 13.24f, 4f, 16f)
                curveTo(4f, 18.76f, 6.24f, 21f, 9f, 21f)
                curveTo(11.76f, 21f, 14f, 18.76f, 14f, 16f)
                curveTo(14f, 13.24f, 11.76f, 11f, 9f, 11f)
                close()
                moveTo(9f, 13f)
                curveTo(10.66f, 13f, 12f, 14.34f, 12f, 16f)
                curveTo(12f, 17.66f, 10.66f, 19f, 9f, 19f)
                curveTo(7.34f, 19f, 6f, 17.66f, 6f, 16f)
                curveTo(6f, 14.34f, 7.34f, 13f, 9f, 13f)
                close()
                moveTo(17.5f, 3f)
                curveTo(14.74f, 3f, 12.5f, 5.24f, 12.5f, 8f)
                curveTo(12.5f, 10.76f, 14.74f, 13f, 17.5f, 13f)
                curveTo(20.26f, 13f, 22.5f, 10.76f, 22.5f, 8f)
                curveTo(22.5f, 5.24f, 20.26f, 3f, 17.5f, 3f)
                close()
                moveTo(17.5f, 5f)
                curveTo(19.16f, 5f, 20.5f, 6.34f, 20.5f, 8f)
                curveTo(20.5f, 9.66f, 19.16f, 11f, 17.5f, 11f)
                curveTo(15.84f, 11f, 14.5f, 9.66f, 14.5f, 8f)
                curveTo(14.5f, 6.34f, 15.84f, 5f, 17.5f, 5f)
                close()
            }
        }.build()
    }

    /**
     * 文件夹图标
     */
    val Folder: ImageVector by lazy {
        ImageVector.Builder(
            name = "CubernFolder",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 4f)
                lineTo(12f, 4f)
                lineTo(14f, 6f)
                lineTo(20f, 6f)
                curveTo(20.55f, 6f, 21f, 6.45f, 21f, 7f)
                lineTo(21f, 19f)
                curveTo(21f, 19.55f, 20.55f, 20f, 20f, 20f)
                lineTo(4f, 20f)
                curveTo(3.45f, 20f, 3f, 19.55f, 3f, 19f)
                lineTo(3f, 5f)
                curveTo(3f, 4.45f, 3.45f, 4f, 4f, 4f)
                close()
            }
        }.build()
    }

    /**
     * 设置图标
     */
    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "CubernSettings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.43f, 12.98f)
                curveTo(19.47f, 12.66f, 19.5f, 12.34f, 19.5f, 12f)
                curveTo(19.5f, 11.66f, 19.47f, 11.34f, 19.43f, 11.02f)
                lineTo(21.54f, 9.37f)
                curveTo(21.73f, 9.22f, 21.78f, 8.95f, 21.66f, 8.75f)
                lineTo(19.66f, 5.29f)
                curveTo(19.54f, 5.08f, 19.27f, 5f, 19.05f, 5.05f)
                lineTo(16.56f, 5.63f)
                curveTo(16.04f, 4.98f, 15.48f, 4.41f, 14.87f, 3.9f)
                lineTo(14.49f, 1.34f)
                curveTo(14.46f, 1.09f, 14.25f, 0.9f, 14f, 0.9f)
                lineTo(10f, 0.9f)
                curveTo(9.75f, 0.9f, 9.54f, 1.09f, 9.51f, 1.34f)
                lineTo(9.13f, 3.9f)
                curveTo(8.52f, 4.41f, 7.96f, 4.98f, 7.44f, 5.63f)
                lineTo(4.95f, 5.05f)
                curveTo(4.73f, 5f, 4.46f, 5.08f, 4.34f, 5.29f)
                lineTo(2.34f, 8.75f)
                curveTo(2.21f, 8.95f, 2.27f, 9.22f, 2.46f, 9.37f)
                lineTo(4.57f, 11.02f)
                curveTo(4.53f, 11.34f, 4.5f, 11.67f, 4.5f, 12f)
                curveTo(4.5f, 12.33f, 4.53f, 12.66f, 4.57f, 12.98f)
                lineTo(2.46f, 14.63f)
                curveTo(2.27f, 14.78f, 2.21f, 15.05f, 2.34f, 15.25f)
                lineTo(4.34f, 18.71f)
                curveTo(4.46f, 18.92f, 4.73f, 19f, 4.95f, 18.95f)
                lineTo(7.44f, 18.37f)
                curveTo(7.96f, 19.02f, 8.52f, 19.59f, 9.13f, 20.1f)
                lineTo(9.51f, 22.66f)
                curveTo(9.54f, 22.91f, 9.75f, 23.1f, 10f, 23.1f)
                lineTo(14f, 23.1f)
                curveTo(14.25f, 23.1f, 14.46f, 22.91f, 14.49f, 22.66f)
                lineTo(14.87f, 20.1f)
                curveTo(15.48f, 19.59f, 16.04f, 19.02f, 16.56f, 18.37f)
                lineTo(19.05f, 18.95f)
                curveTo(19.27f, 19f, 19.54f, 18.92f, 19.66f, 18.71f)
                lineTo(21.66f, 15.25f)
                curveTo(21.78f, 15.05f, 21.73f, 14.78f, 21.54f, 14.63f)
                close()
                moveTo(12f, 15.6f)
                curveTo(9.66f, 15.6f, 7.8f, 13.74f, 7.8f, 11.4f)
                curveTo(7.8f, 9.06f, 9.66f, 7.2f, 12f, 7.2f)
                curveTo(14.34f, 7.2f, 16.2f, 9.06f, 16.2f, 11.4f)
                curveTo(16.2f, 13.74f, 14.34f, 15.6f, 12f, 15.6f)
                close()
            }
        }.build()
    }
}
