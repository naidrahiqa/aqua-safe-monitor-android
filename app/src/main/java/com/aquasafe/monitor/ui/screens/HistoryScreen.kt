package com.aquasafe.monitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorReading
import com.aquasafe.monitor.ui.components.PanelCard
import com.aquasafe.monitor.ui.components.SectionHeader
import com.aquasafe.monitor.ui.components.StatusChip
import com.aquasafe.monitor.ui.theme.DataMedium
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.roundedMedium
import com.aquasafe.monitor.ui.util.fmtDateTime
import com.aquasafe.monitor.viewmodel.DashboardUiState

/** Riwayat: seluruh pembacaan sensor, terbaru di atas */
@Composable
fun HistoryScreen(state: DashboardUiState) {
    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                SectionHeader(
                    title = "Riwayat Pembacaan",
                    subtitle = "${state.readings.size} data dari sensor",
                )
                Spacer(Modifier.height(12.dp))
            }
            if (state.readings.isEmpty()) {
                item {
                    Text(
                        "Belum ada data. Pastikan SupabaseConfig.kt sudah di-set.",
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
    PanelCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.roundedMedium(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        fmtDateTime(reading.timestampMillis),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                    )
                    Text(
                        "Device ${reading.deviceId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
                StatusChip(reading.waterStatus)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                MiniValue("Suhu", "${reading.temperature}°C")
                MiniValue("pH", reading.ph.toString())
                MiniValue("TDS", "${reading.tds.toInt()} ppm")
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