package com.aquasafe.monitor.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquasafe.monitor.model.SensorConfig
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.Danger
import com.aquasafe.monitor.ui.theme.DataHero
import com.aquasafe.monitor.ui.theme.DataXLarge
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.Warning
import com.aquasafe.monitor.ui.theme.isDarkTheme
import java.util.Locale

/**
 * Industrial gauge card — flat panel, ink rule, semi-circle arc
 */
@Composable
fun GaugeCard(
    title: String,
    value: Double?,
    unit: String,
    config: SensorConfig,
    modifier: Modifier = Modifier,
) {
    val color = Color(if (isDarkTheme) config.colorDark else config.color)
    val pct = if (value != null) {
        ((value - config.min) / (config.max - config.min)).coerceIn(0.0, 1.0)
    } else 0.0
    val sweep by animateFloatAsState(
        targetValue = pct.toFloat(),
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "gaugeSweep",
    )
    val animValue by animateFloatAsState(
        targetValue = value?.toFloat() ?: 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "gaugeValue",
    )
    val valueText = when {
        value == null -> "--"
        config.decimals == 0 -> animValue.toInt().toString()
        else -> String.format(Locale.US, "%.${config.decimals}f", animValue)
    }
    val valueFontSize = when {
        valueText.length > 6 -> 18.sp
        valueText.length > 4 -> 22.sp
        else -> 26.sp
    }

    Column(
        modifier = modifier
            .background(Panel, MaterialTheme.shapes.medium)
            .border(BorderWidth, Border, MaterialTheme.shapes.medium)
            .padding(14.dp),
    ) {
            // Uppercase title badge
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.fillMaxWidth().height(100.dp)) {
                    val r = 42.dp.toPx()
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val topLeft = Offset(centerX - r, centerY - r)
                    val arcSize = Size(2 * r, 2 * r)

                    // Background arc
                    drawArc(
                        color = PanelLight,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
                    )
                    if (sweep > 0f) {
                        // Solid arc
                        drawArc(
                            color = color,
                            startAngle = 180f,
                            sweepAngle = 180f * sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    valueText,
                    style = DataXLarge.copy(fontSize = valueFontSize),
                    color = TextPrimary,
                )
                Text(
                    unit,
                    fontSize = 11.sp,
                    color = TextMuted,
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val isSafe = value != null && value >= config.safeMin && value <= config.safeMax
                val indicatorColor = when {
                    value == null -> PanelLight
                    isSafe -> Success
                    else -> Danger
                }
                val indicatorText = when {
                    value == null -> "Belum ada data"
                    isSafe -> "Aman"
                    value < config.safeMin -> "Di bawah minimum"
                    else -> "Di atas batas aman"
                }
                Box(
                    Modifier
                        .size(8.dp)
                        .background(indicatorColor, CircleShape),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    indicatorText,
                    style = MaterialTheme.typography.labelSmall,
                    color = indicatorColor,
                )
            }
        }
}

private val PanelLight = TextMuted

/**
 * Neubrutalism WQI hero card — full ring + big number
 */
@Composable
fun WqiHeroCard(
    wqi: Double?,
    statusLabel: String,
    syncedText: String?,
    modifier: Modifier = Modifier,
) {
    val wqiColor = when {
        wqi == null -> PanelLight
        wqi >= 80 -> Success
        wqi >= 60 -> Warning
        else -> Danger
    }
    val pct = (wqi ?: 0.0).coerceIn(0.0, 100.0) / 100.0
    val sweep by animateFloatAsState(
        targetValue = pct.toFloat(),
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "wqiSweep",
    )
    val animWqi by animateFloatAsState(
        targetValue = wqi?.toFloat() ?: 0f,
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "wqiValue",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .background(Panel, MaterialTheme.shapes.large)
            .border(BorderWidth, Border, MaterialTheme.shapes.large)
            .padding(20.dp),
    ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.fillMaxWidth().height(150.dp)) {
                    val r = 62.dp.toPx()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val topLeft = Offset(center.x - r, center.y - r)
                    val arcSize = Size(2 * r, 2 * r)
                    // Background ring
                    drawArc(
                        color = PanelLight,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
                    )
                    if (sweep > 0f) {
                        // Solid
                        drawArc(
                            color = wqiColor,
                            startAngle = -90f,
                            sweepAngle = 360f * sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "WQI",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            if (wqi == null) "--" else animWqi.toInt().toString(),
                            style = DataHero,
                            color = if (wqi == null) TextMuted else wqiColor,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "/100",
                            fontSize = 14.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            // Status — lamp + ink label, industrial style
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(10.dp)
                        .background(wqiColor),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    statusLabel.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                )
            }
            if (syncedText != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    syncedText,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                )
            }
    }
}
