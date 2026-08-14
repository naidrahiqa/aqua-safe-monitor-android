package com.aquasafe.monitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorConfig
import com.aquasafe.monitor.model.SensorConfigs
import com.aquasafe.monitor.model.valueOf
import com.aquasafe.monitor.ui.components.GaugeCard
import com.aquasafe.monitor.ui.components.SensorChart
import com.aquasafe.monitor.ui.components.StatusChip
import com.aquasafe.monitor.ui.theme.AccentCyan
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.Radius
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.TextSecondary
import com.aquasafe.monitor.viewmodel.DashboardUiState

@Composable
fun SensorScreen(
    state: DashboardUiState,
    onSetTimeRange: (Int?) -> Unit,
) {
    var selectedTab = remember { androidx.compose.runtime.mutableIntStateOf(0) }
    val config = SensorConfigs.ALL[selectedTab.intValue.coerceIn(0, SensorConfigs.ALL.lastIndex)]

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            "SENSOR DETAIL",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Detail pembacaan per sensor",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
        )

        Spacer(Modifier.height(16.dp))
        TimeRangeChips(selected = state.timeRangeHours, onSelect = onSetTimeRange)

        Spacer(Modifier.height(16.dp))
        // Tab bar — neubrutalism style
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            SensorConfigs.ALL.forEachIndexed { index, cfg ->
                val isSelected = index == selectedTab.intValue
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(Radius.md))
                        .then(
                            if (isSelected) Modifier.background(AccentCyan).border(BorderWidth, AccentCyan, RoundedCornerShape(Radius.md))
                            else Modifier.background(Panel).border(BorderWidth, Border, RoundedCornerShape(Radius.md))
                        )
                        .clickable { selectedTab.intValue = index }
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        cfg.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) com.aquasafe.monitor.ui.theme.OnAccent else TextSecondary,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        // Gauge
        GaugeCard(
            title = config.label,
            value = state.latestReading?.valueOf(config),
            unit = config.unit,
            config = config,
            modifier = Modifier.fillMaxWidth(),
        )

        // Status chip
        val latest = state.latestReading
        if (latest != null) {
            Spacer(Modifier.height(12.dp))
            StatusChip(latest.waterStatus)
        }

        // Chart
        Spacer(Modifier.height(16.dp))
        val chartValues = remember(state.readings, config) {
            state.readings.map { it.timestampMillis to it.valueOf(config) }
                .sortedBy { it.first }
                .takeLast(50)
        }
        SensorChart(
            label = "Riwayat ${config.label}",
            values = chartValues,
            config = config,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
