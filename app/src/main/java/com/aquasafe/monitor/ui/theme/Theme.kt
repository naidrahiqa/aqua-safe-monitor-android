package com.aquasafe.monitor.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.WaterStatus

/** Latar gradien "night sky" — dipakai di root app, semua layar konsisten */
val AppBackgroundGradient = Brush.verticalGradient(
    colors = listOf(SurfaceDeep, SurfaceDark, Color(0xFF03050A)),
    endY = 2000f,
)

private val AppColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = Warning,
    tertiary = Success,
    background = SurfaceDark,
    surface = SurfaceDark,
    surfaceVariant = Panel,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = Danger,
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

val CardBorderStroke: BorderStroke = BorderStroke(1.dp, CardBorder)

@Composable
fun WaterSafeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = WaterSafeTypography,
        shapes = AppShapes,
        content = content,
    )
}

fun statusColor(status: WaterStatus): Color = when (status) {
    WaterStatus.SANGAT_LAYAK -> Success
    WaterStatus.LAYAK -> Warning
    WaterStatus.BAHAYA -> Danger
}
