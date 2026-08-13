package com.aquasafe.monitor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorConfig
import com.aquasafe.monitor.ui.theme.DataMedium
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.PanelLight
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextSecondary

/**
 * Chart garis — konsep sama dengan chart di versi web:
 * zona aman digambar sebagai band hijau transparan,
 * garis nilai dengan auto-scale terhadap batas aman.
 */
@Composable
fun SensorChart(
    values: List<Double>,
    config: SensorConfig,
    modifier: Modifier = Modifier,
) {
    val color = Color(config.color)
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Riwayat ${config.label}",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary,
            )
            Spacer(Modifier.height(10.dp))
            if (values.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Belum ada data sensor",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                    )
                }
            } else {
                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                ) {
                    val minV = values.min()
                    val maxV = values.max()
                    val low = minOf(minV, config.safeMin)
                    val high = maxOf(maxV, config.safeMax)
                    val span = maxOf(high - low, 1.0)
                    fun y(v: Double): Float =
                        size.height - ((v - low) / span * size.height).toFloat()

                    val bandTop = y(config.safeMax)
                    val bandBottom = y(config.safeMin)
                    drawRect(
                        color = Success.copy(alpha = 0.10f),
                        topLeft = Offset(0f, bandTop),
                        size = Size(size.width, bandBottom - bandTop),
                    )
                    drawLine(
                        color = PanelLight,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )

                    if (values.size == 1) {
                        drawCircle(
                            color = color,
                            radius = 3.dp.toPx(),
                            center = Offset(size.width / 2f, y(values[0])),
                        )
                    } else {
                        val step = size.width / (values.size - 1)
                        val linePath = Path()
                        values.forEachIndexed { i, v ->
                            val x = i * step
                            if (i == 0) linePath.moveTo(x, y(v)) else linePath.lineTo(x, y(v))
                        }
                        val fillPath = Path().apply {
                            addPath(linePath)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    color.copy(alpha = 0.30f),
                                    color.copy(alpha = 0.02f),
                                ),
                                startY = 0f,
                                endY = size.height,
                            ),
                        )
                        drawPath(
                            path = linePath,
                            brush = Brush.verticalGradient(
                                colors = listOf(color, color.copy(alpha = 0.35f)),
                            ),
                            style = Stroke(
                                width = 2.5.dp.toPx(),
                                cap = StrokeCap.Round,
                            ),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Min ${fmt(values.min(), config)}  •  Max ${fmt(values.max(), config)}",
                        style = DataMedium,
                        color = TextMuted,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        config.safeNote,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
            }
        }
    }
}

private fun fmt(v: Double, config: SensorConfig): String =
    if (config.decimals == 0) v.toInt().toString()
    else String.format(java.util.Locale.US, "%.${config.decimals}f", v)