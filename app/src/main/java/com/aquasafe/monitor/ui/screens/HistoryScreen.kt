package com.aquasafe.monitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorReading
import com.aquasafe.monitor.ui.components.SectionHeader
import com.aquasafe.monitor.ui.components.StatusChip
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.DataMedium
import com.aquasafe.monitor.ui.theme.HardShadowSm
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.util.fmtDateTime
import com.aquasafe.monitor.viewmodel.DashboardUiState

@Composable
fun HistoryScreen(state: DashboardUiState) {
    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Text(
                    "RIWAYAT",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${state.readings.size} data dari sensor",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
                Spacer(Modifier.height(12.dp))
            }
            if (state.readings.isEmpty()) {
                item {
                    Text(
                        "Belum ada data sensor. Pastikan ESP32 terhubung ke Supabase.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                    )
                }
            }
            items(state.readings) { reading ->
                ReadingCard(reading)
            }
        }
    }
}

@Composable
private fun ReadingCard(reading: SensorReading) {
    Box {
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
                .fillMaxWidth()
                .background(Panel, MaterialTheme.shapes.medium)
                .border(BorderWidth, Border, MaterialTheme.shapes.medium)
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        fmtDateTime(reading.timestampMillis),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                    )
                    Text(
                        "Device ${reading.deviceId.take(8)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
                StatusChip(reading.waterStatus)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                MiniValue("SUHU", "${reading.temperature}°C")
                MiniValue("PH", reading.ph.toString())
                MiniValue("TDS", "${reading.tds.toInt()} PPM")
                MiniValue("NTU", reading.turbidity.toString())
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "WQI: ${reading.wqiScore.toInt()}/100",
                style = DataMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun MiniValue(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
        Text(
            value,
            style = DataMedium,
            color = TextPrimary,
        )
    }
}

private object Color {
    val Black = androidx.compose.ui.graphics.Color.Black
}
