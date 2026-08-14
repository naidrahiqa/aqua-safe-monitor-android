package com.aquasafe.monitor.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorConfig
import com.aquasafe.monitor.ui.theme.AccentCyan
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.Danger
import com.aquasafe.monitor.ui.theme.DataMedium
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.PanelLight
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.Warning
import com.aquasafe.monitor.ui.util.fmtAgo

@Composable
fun SensorChart(
    label: String,
    values: List<Pair<Long, Double>>,
    config: SensorConfig,
    modifier: Modifier = Modifier,
) {
    // Line always instrument blue — must stand apart from green/yellow/red zone bands
    val color = AccentCyan
    Column(
        modifier = modifier
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
                val margin = ((safeMax - safeMin) * 0.5).coerceAtLeast(1.0)

                // Auto-fit Y ke data + padding — chart nggak datar lagi.
                // Zona ijo/kuning/merah tetap digambar dan ke-clip natural.
                val spread = (maxVal - minVal).coerceAtLeast(1e-9)
                val pad = (spread * 0.25).coerceAtLeast(0.5)
                val lo = minVal - pad
                val hi = maxVal + pad

                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(top = 8.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val range = (hi - lo).coerceAtLeast(0.001)

                    fun yOf(v: Double): Float = ((hi - v) / range).toFloat() * h

                    // ===== Zona latar: MERAH → KUNING → HIJAU → KUNING → MERAH =====
                    // Band di-clip ke rentang terlihat (intersect dulu, baru gambar)
                    fun drawBand(topV: Double, bottomV: Double, color: Color) {
                        val vLo = minOf(topV, bottomV)
                        val vHi = maxOf(topV, bottomV)
                        val ovLo = maxOf(vLo, lo)
                        val ovHi = minOf(vHi, hi)
                        if (ovHi <= ovLo) return
                        val yTop = yOf(ovHi)
                        val yBottom = yOf(ovLo)
                        if (yBottom - yTop < 0.5f) return
                        drawRect(
                            color = color.copy(alpha = 0.14f),
                            topLeft = Offset(0f, yTop),
                            size = androidx.compose.ui.geometry.Size(w, yBottom - yTop),
                        )
                    }

                    // Bahaya (merah)
                    drawBand(lo, safeMin - margin, Danger)
                    drawBand(safeMax + margin, hi, Danger)
                    // Waspada (kuning)
                    drawBand(safeMin - margin, safeMin, Warning)
                    drawBand(safeMax, safeMax + margin, Warning)
                    // Aman (hijau)
                    drawBand(safeMin, safeMax, Success)

                    // Garis batas aman
                    drawLine(
                        color = Success.copy(alpha = 0.35f),
                        start = Offset(0f, yOf(safeMin)),
                        end = Offset(w, yOf(safeMin)),
                        strokeWidth = 1.dp.toPx(),
                    )
                    drawLine(
                        color = Success.copy(alpha = 0.35f),
                        start = Offset(0f, yOf(safeMax)),
                        end = Offset(w, yOf(safeMax)),
                        strokeWidth = 1.dp.toPx(),
                    )

                    // Line path
                    val path = Path()
                    values.forEachIndexed { i, (_, v) ->
                        val x = i * w / (values.size - 1).coerceAtLeast(1)
                        val y = yOf(v)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, color, style = Stroke(width = 2.5.dp.toPx()))
                }

                // Legend ijo/kuning/merah
                Row(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    LegendDot(Success, "Aman")
                    LegendDot(Warning, "Waspada")
                    LegendDot(Danger, "Bahaya")
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
private fun fmtVal(v: Double, config: SensorConfig): String = if (config.decimals == 0) {
    v.toInt().toString()
} else {
    String.format(java.util.Locale.US, "%.${config.decimals}f", v)
}

@Composable
private fun LegendDot(color: Color, text: String) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
    }
}
