package com.aquasafe.monitor.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.WaterStatus

// Flat solid background — industrial theme has no gradients
val AppBackgroundGradient = Brush.verticalGradient(
    colors = listOf(SurfaceDark, SurfaceDeep),
)

private val LightScheme = lightColorScheme(
    primary = LightColors.AccentCyan,
    secondary = LightColors.Warning,
    tertiary = LightColors.Success,
    background = LightColors.SurfaceDark,
    surface = LightColors.Panel,
    surfaceVariant = LightColors.PanelLight,
    onBackground = LightColors.TextPrimary,
    onSurface = LightColors.TextPrimary,
    onSurfaceVariant = LightColors.TextSecondary,
    error = LightColors.Danger,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
)

private val DarkScheme = darkColorScheme(
    primary = DarkColors.AccentCyan,
    secondary = DarkColors.Warning,
    tertiary = DarkColors.Success,
    background = DarkColors.SurfaceDark,
    surface = DarkColors.Panel,
    surfaceVariant = DarkColors.PanelLight,
    onBackground = DarkColors.TextPrimary,
    onSurface = DarkColors.TextPrimary,
    onSurfaceVariant = DarkColors.TextSecondary,
    error = DarkColors.Danger,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
)

// Sharp corners — industrial panels
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(Radius.sm),
    small = RoundedCornerShape(Radius.sm),
    medium = RoundedCornerShape(Radius.md),
    large = RoundedCornerShape(Radius.lg),
    extraLarge = RoundedCornerShape(Radius.xl),
)

val CardBorderStroke: BorderStroke = BorderStroke(BorderWidth, Border)
val CardBorderHover: BorderStroke = BorderStroke(BorderWidth, BorderLight)

@Composable
fun WaterSafeTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    isDarkTheme = dark
    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
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

/** Safe accessor for theme shapes as RoundedCornerShape */
fun androidx.compose.material3.Shapes.roundedSmall(): RoundedCornerShape = small as RoundedCornerShape
fun androidx.compose.material3.Shapes.roundedMedium(): RoundedCornerShape = medium as RoundedCornerShape
fun androidx.compose.material3.Shapes.roundedLarge(): RoundedCornerShape = large as RoundedCornerShape