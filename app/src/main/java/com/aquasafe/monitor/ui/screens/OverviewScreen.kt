package com.aquasafe.monitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorConfigs
import com.aquasafe.monitor.model.valueOf
import com.aquasafe.monitor.ui.components.GaugeCard
import com.aquasafe.monitor.ui.components.PanelCard
import com.aquasafe.monitor.ui.components.SectionHeader
import com.aquasafe.monitor.ui.components.StatusPill
import com.aquasafe.monitor.ui.components.WqiHeroCard
import com.aquasafe.monitor.ui.theme.Danger
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.TextSecondary
import com.aquasafe.monitor.ui.util.fmtAgo
import com.aquasafe.monitor.viewmodel.DashboardUiState

/** Beranda: status online, WQI hero ring, 4 gauge sensor, ringkasan lokasi */
@Composable
fun OverviewScreen(
    state: DashboardUiState,
    onRefresh: () -> Unit,
    onNavigate: (String) -> Unit,
    onSetTimeRange: (Int?) -> Unit,
) {
    val latest = state.latestReading
    val onlineText = if (latest == null) "Menunggu data..." else "Online • ${fmtAgo(state.lastUpdatedAt)}"
    val onlineColor = if (latest == null) Danger else Success

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "WaterSafe Monitor",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(6.dp))
                StatusPill(
                    text = onlineText,
                    color = onlineColor,
                    pulsing = latest != null,
                )
            }
            IconButton(onClick = onRefresh) {
                if (state.loading) {
                    CircularProgressIndicator(
                        Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", tint = TextSecondary)
                }
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            PanelCard(
                shape = MaterialTheme.shapes.small as androidx.compose.foundation.shape.RoundedCornerShape,
            ) {
                Text(
                    state.error,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Danger,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        WqiHeroCard(
            wqi = latest?.wqiScore,
            statusLabel = latest?.waterStatus?.label ?: "BELUM ADA DATA",
            syncedText = latest?.let { "Terakhir update: ${fmtAgo(it.timestampMillis)}" },
        )

        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(Modifier.height(16.dp))
        SectionHeader(
            title = "Kualitas Air",
            subtitle = "Pembacaan sensor terbaru",
        )
        Spacer(Modifier.height(10.dp))

        SensorConfigs.ALL.chunked(2).forEach { rowConfigs ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowConfigs.forEach { config ->
                    GaugeCard(
                        title = config.label,
                        value = latest?.valueOf(config),
                        unit = config.unit,
                        config = config,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        SectionHeader(
            title = "Lokasi Pengujian",
            subtitle = "${state.locations.size} lokasi tersimpan",
        )
        Spacer(Modifier.height(10.dp))
        PanelCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate("lokasi") },
            shape = MaterialTheme.shapes.large as androidx.compose.foundation.shape.RoundedCornerShape,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Peta & Pin Lokasi",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Ketuk untuk buka peta pengujian",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                }
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}