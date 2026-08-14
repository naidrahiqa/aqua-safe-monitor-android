package com.aquasafe.monitor.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.WaterStatus

// Flat solid background — neubrutalism has no gradients
val AppBackgroundGradient = Brush.verticalGradient(
    colors = listOf(SurfaceDark, SurfaceDark),
)

private val AppColorScheme = lightColorScheme(
    primary = AccentCyan,
    secondary = Warning,
    tertiary = Success,
    background = SurfaceDark,
    surface = Panel,
    surfaceVariant = PanelLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = Danger,
    onPrimary = OnAccent,
    onSecondary = OnAccent,
    onTertiary = OnAccent,
    onError = Color.White,
)

// Sharp corners — neubrutalism prefers square shapes
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

/** Safe accessor for theme shapes as RoundedCornerShape */
fun androidx.compose.material3.Shapes.roundedSmall(): RoundedCornerShape = small as RoundedCornerShape
fun androidx.compose.material3.Shapes.roundedMedium(): RoundedCornerShape = medium as RoundedCornerShape
fun androidx.compose.material3.Shapes.roundedLarge(): RoundedCornerShape = large as RoundedCornerShape