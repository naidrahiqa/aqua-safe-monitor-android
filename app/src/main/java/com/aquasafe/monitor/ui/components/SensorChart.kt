package com.aquasafe.monitor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorConfig
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.DataMedium
import com.aquasafe.monitor.ui.theme.HardShadowSm
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.PanelLight
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.util.fmtAgo

@Composable
fun SensorChart(
    label: String,
    values: List<Pair<Long, Double>>,
    config: SensorConfig,
    modifier: Modifier = Modifier,
) {
    val color = Color(config.color)
    Box(modifier) {
        // Hard shadow
        Box(
            Modifier
                .matchParentSize()
                .offset(HardShadowSm.x, HardShadowSm.y)
                .background(Color.Black.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                .border(BorderWidth, Border, MaterialTheme.shapes.medium)
        )
        Column(
            modifier = Modifier
                .background(Panel, MaterialTheme.shapes.medium)
                .border(BorderWidth, Border, MaterialTheme.shapes.medium)
                .padding(14.dp)
        ) {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
            if (values.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center,
                ) {
                    Text(
                        "Belum ada data sensor",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                }
            } else {
                val minVal = values.minOf { it.second }
                val maxVal = values.maxOf { it.second }
                val safeMin = config.safeMin
                val safeMax = config.safeMax

                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(top = 8.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val range = (maxVal - minVal).coerceAtLeast(0.001)

                    // Safe zone band
                    val safeTop = ((maxVal - safeMax) / range).toFloat().coerceIn(0f, 1f)
                    val safeBottom = ((maxVal - safeMin) / range).toFloat().coerceIn(0f, 1f)
                    drawRect(
                        color = Success.copy(alpha = 0.08f),
                        topLeft = Offset(0f, safeTop * h),
                        size = androidx.compose.ui.geometry.Size(w, (safeBottom - safeTop).coerceAtLeast(0f) * h),
                    )

                    // Line path
                    val path = Path()
                    values.forEachIndexed { i, (_, v) ->
                        val x = i * w / (values.size - 1).coerceAtLeast(1)
                        val y = ((maxVal - v) / range).toFloat() * h
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, color, style = Stroke(width = 2.5.dp.toPx()))
                }

                // Stats row
                val latest = values.last().second
                val first = values.first().second
                val trend = latest - first
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        "Latest: ${fmtVal(latest, config)} ${config.unit}  •  Trend: ${if (trend >= 0) "+" else ""}${fmtVal(trend, config)}",
                        style = DataMedium,
                        color = TextPrimary,
                    )
                    Text(
                        "Min: ${fmtVal(minVal, config)}  •  Max: ${fmtVal(maxVal, config)}  •  ${values.size} readings",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
            }
        }
    }
}

private fun fmtVal(v: Double, config: SensorConfig): String = if (config.decimals == 0) {
    v.toInt().toString()
} else {
    String.format(java.util.Locale.US, "%.${config.decimals}f", v)
}
