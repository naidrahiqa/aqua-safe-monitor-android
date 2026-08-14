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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.SensorConfigs
import com.aquasafe.monitor.model.valueOf
import com.aquasafe.monitor.ui.components.GaugeCard
import com.aquasafe.monitor.ui.components.PanelCard
import com.aquasafe.monitor.ui.components.SectionHeader
import com.aquasafe.monitor.ui.components.StatusPill
import com.aquasafe.monitor.ui.components.WqiHeroCard
import com.aquasafe.monitor.ui.theme.AccentCyan
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.Danger
import com.aquasafe.monitor.ui.theme.HardShadowSm
import com.aquasafe.monitor.ui.theme.OnAccent
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.Radius
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.TextSecondary
import com.aquasafe.monitor.ui.util.fmtAgo
import com.aquasafe.monitor.viewmodel.DashboardUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    var isRefreshing by remember { mutableStateOf(false) }
    val refreshScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            refreshScope.launch {
                isRefreshing = true
                onRefresh()
                delay(500)
                isRefreshing = false
            }
        },
        state = refreshState,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "WATERSAFE",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary,
                    )
                    Text(
                        "MONITOR",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AccentCyan,
                    )
                    Spacer(Modifier.height(6.dp))
                    StatusPill(
                        text = onlineText,
                        color = onlineColor,
                    )
                }
                IconButton(onClick = onRefresh) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = AccentCyan,
                        )
                    } else {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", tint = TextSecondary)
                    }
                }
            }

            // Error
            if (state.error != null) {
                Spacer(Modifier.height(12.dp))
                PanelCard(
                    borderColor = Danger,
                ) {
                    Text(
                        state.error,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Danger,
                    )
                }
            }

            // BAHAYA banner — muncul saat status terbaru berbahaya
            val latestStatus = latest?.waterStatus
            if (latestStatus == com.aquasafe.monitor.model.WaterStatus.BAHAYA) {
                Spacer(Modifier.height(12.dp))
                PanelCard(
                    containerColor = Danger,
                    borderColor = Danger,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = OnAccent,
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "BAHAYA! Air tidak layak",
                                style = MaterialTheme.typography.titleSmall,
                                color = OnAccent,
                            )
                            Text(
                                "WQI ${latest?.wqiScore?.toInt() ?: 0}/100 — hindari penggunaan air ini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnAccent,
                            )
                        }
                    }
                }
            }

            // WQI Hero
            Spacer(Modifier.height(16.dp))
            WqiHeroCard(
                wqi = latest?.wqiScore,
                statusLabel = latest?.waterStatus?.label ?: "BELUM ADA DATA",
                syncedText = latest?.let { "Terakhir update: ${fmtAgo(it.timestampMillis)}" },
            )

            // Time filter chips
            Spacer(Modifier.height(16.dp))
            TimeRangeChips(
                selected = state.timeRangeHours,
                onSelect = onSetTimeRange,
            )

            // Sensor gauges
            Spacer(Modifier.height(16.dp))
            SectionHeader(
                title = "SENSOR DATA",
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

            // Location shortcut
            SectionHeader(
                title = "LOKASI UJI",
                subtitle = "${state.locations.size} lokasi tersimpan",
            )
            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate("lokasi") }
            ) {
                // Hard shadow
                Box(
                    Modifier
                        .matchParentSize()
                        .offset(HardShadowSm.x, HardShadowSm.y)
                        .background(Color.Black.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                        .border(BorderWidth, Border, MaterialTheme.shapes.medium)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Panel, MaterialTheme.shapes.medium)
                        .border(BorderWidth, Border, MaterialTheme.shapes.medium)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "PETA & PIN LOKASI",
                            style = MaterialTheme.typography.titleSmall,
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
                        tint = AccentCyan,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun TimeRangeChips(
    selected: Int?,
    onSelect: (Int?) -> Unit,
) {
    val ranges = listOf(null to "Semua", 1 to "1 JAM", 6 to "6 JAM", 24 to "24 JAM")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        ranges.forEach { (hours, label) ->
            val isSelected = selected == hours
            Box(
                Modifier
                    .clip(RoundedCornerShape(Radius.md))
                    .then(
                        if (isSelected) Modifier.background(AccentCyan).border(BorderWidth, AccentCyan, RoundedCornerShape(Radius.md))
                        else Modifier.background(Panel).border(BorderWidth, Border, RoundedCornerShape(Radius.md))
                    )
                    .clickable { onSelect(hours) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) OnAccent else TextSecondary,
                )
            }
        }
    }
}
