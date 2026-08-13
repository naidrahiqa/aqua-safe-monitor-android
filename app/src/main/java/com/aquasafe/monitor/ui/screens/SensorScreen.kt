package com.aquasafe.monitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorConfigs
import com.aquasafe.monitor.model.valueOf
import com.aquasafe.monitor.ui.components.GaugeCard
import com.aquasafe.monitor.ui.components.SensorChart
import com.aquasafe.monitor.ui.components.StatusChip
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextSecondary
import com.aquasafe.monitor.ui.util.fmtDateTime
import com.aquasafe.monitor.viewmodel.DashboardUiState

/** Detail sensor: 4 tab (pH, Suhu, TDS, Turbidity) — gauge + riwayat chart */
@Composable
fun SensorScreen(
    state: DashboardUiState,
    onSetTimeRange: (Int?) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val config = SensorConfigs.ALL[selectedTab.coerceIn(0, SensorConfigs.ALL.lastIndex)]
    val latest = state.latestReading
    val series = state.readings.takeLast(60).map { it.valueOf(config) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            SensorConfigs.ALL.forEachIndexed { index, tag ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            tag.label.split(" ").first(),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            val ranges = listOf(null to "Semua", 1 to "1 Jam", 6 to "6 Jam", 24 to "24 Jam")
            ranges.forEach { (hours, label) ->
                FilterChip(
                    selected = state.timeRangeHours == hours,
                    onClick = { onSetTimeRange(hours) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }

        Column(Modifier.padding(16.dp)) {
            GaugeCard(
                title = config.label,
                value = latest?.valueOf(config),
                unit = config.unit,
                config = config,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(14.dp))
            if (latest != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(latest.waterStatus)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        fmtDateTime(latest.timestampMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Device: ${latest.deviceId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            } else {
                Text(
                    "Belum ada pembacaan dari sensor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            Spacer(Modifier.height(14.dp))
            SensorChart(values = series, config = config)
            Spacer(Modifier.height(24.dp))
        }
    }
}