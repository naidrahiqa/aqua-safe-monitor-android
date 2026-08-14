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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.TestLocation
import com.aquasafe.monitor.model.WaterStatus
import com.aquasafe.monitor.ui.components.StatusChip
import com.aquasafe.monitor.ui.components.WaterMap
import com.aquasafe.monitor.ui.theme.AccentCyan
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.Danger
import com.aquasafe.monitor.ui.theme.DataMedium
import com.aquasafe.monitor.ui.theme.HardShadowSm
import com.aquasafe.monitor.ui.theme.OnAccent
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.PanelLight
import com.aquasafe.monitor.ui.theme.Radius
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.Warning
import com.aquasafe.monitor.ui.util.fmtAgo
import com.aquasafe.monitor.viewmodel.DashboardUiState

@Composable
fun MapScreen(
    state: DashboardUiState,
    onAdd: (name: String, lat: Double, lng: Double, temp: Double, ph: Double, tds: Double, turb: Double, notes: String) -> Unit,
    onRemove: (String) -> Unit,
    onSync: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<TestLocation?>(null) }
    var pickLat by remember { mutableStateOf(-6.5833) }
    var pickLng by remember { mutableStateOf(110.6667) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            "LOKASI UJI",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "Pin = lokasi pengujian dengan data sensor terkini",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
        )

        Spacer(Modifier.height(16.dp))
        // Map — OpenStreetMap with location pins
        Box {
            Box(
                Modifier
                    .matchParentSize()
                    .offset(HardShadowSm.x, HardShadowSm.y)
                    .background(Color.Black.copy(alpha = 0.3f), MaterialTheme.shapes.large)
                    .border(BorderWidth, Border, MaterialTheme.shapes.large)
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(MaterialTheme.shapes.large)
                    .border(BorderWidth, Border, MaterialTheme.shapes.large),
            ) {
                WaterMap(
                    locations = state.locations,
                    modifier = Modifier.fillMaxSize(),
                    onLongPress = { point ->
                        pickLat = point.latitude
                        pickLng = point.longitude
                        showAddDialog = true
                    },
                )
                Text(
                    "TEKAN LAMA UNTUK TAMBAH PIN",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                        .background(Panel.copy(alpha = 0.9f), RoundedCornerShape(Radius.pill))
                        .border(BorderWidth, Border, RoundedCornerShape(Radius.pill))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        // Locations list
        if (state.locations.isEmpty()) {
            Text(
                "Belum ada lokasi. Ketuk + untuk menambah.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
            )
        } else {
            state.locations.forEach { location ->
                LocationCard(
                    location = location,
                    onSync = { onSync(location.id) },
                    onDelete = { deleteTarget = location },
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    // Add dialog
    if (showAddDialog) {
        AddLocationDialog(
            initialLat = pickLat,
            initialLng = pickLng,
            onDismiss = { showAddDialog = false },
            onAdd = { name, lat, lng, temp, ph, tds, turb, notes ->
                onAdd(name, lat, lng, temp, ph, tds, turb, notes)
                showAddDialog = false
            },
        )
    }

    // Delete confirmation
    deleteTarget?.let { loc ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Hapus Lokasi", style = MaterialTheme.typography.titleMedium) },
            text = { Text("Hapus \"${loc.name}\"? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    onRemove(loc.id)
                    deleteTarget = null
                }) {
                    Text("HAPUS", color = Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("BATAL")
                }
            },
        )
    }
}

@Composable
private fun LocationCard(
    location: TestLocation,
    onSync: () -> Unit,
    onDelete: () -> Unit,
) {
    Box {
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
                        location.name.uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                    )
                    Text(
                        "${String.format(java.util.Locale.US, "%.4f", location.lat)}, ${String.format(java.util.Locale.US, "%.4f", location.lng)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
                StatusChip(location.waterStatus)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                MiniValue("SUHU", "${location.temperature}°C")
                MiniValue("PH", location.ph.toString())
                MiniValue("TDS", "${location.tds.toInt()} PPM")
                MiniValue("NTU", location.turbidity.toString())
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "WQI: ${location.wqiScore.toInt()}/100",
                    style = DataMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.weight(1f))
                if (location.isSynced) {
                    Text(
                        "Synced ${fmtAgo(location.syncedAt?.let { com.aquasafe.monitor.ui.util.fmtAgoIso(it) }?.let { 0L } ?: 0L)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
                // Sync button
                IconButton(onClick = onSync, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Rounded.Refresh,
                        contentDescription = "Sinkron",
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp),
                    )
                }
                // Delete button
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Hapus",
                        tint = Danger,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniValue(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(value, style = DataMedium, color = TextPrimary)
    }
}

@Composable
private fun AddLocationDialog(
    initialLat: Double,
    initialLng: Double,
    onDismiss: () -> Unit,
    onAdd: (name: String, lat: Double, lng: Double, temp: Double, ph: Double, tds: Double, turb: Double, notes: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf(String.format(java.util.Locale.US, "%.4f", initialLat)) }
    var lng by remember { mutableStateOf(String.format(java.util.Locale.US, "%.4f", initialLng)) }
    var temp by remember { mutableStateOf("25.0") }
    var ph by remember { mutableStateOf("7.0") }
    var tds by remember { mutableStateOf("200") }
    var turb by remember { mutableStateOf("2.0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("TAMBAH LOKASI UJI", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DialogField("Nama", name, { name = it })
                DialogField("Latitude", lat, { lat = it })
                DialogField("Longitude", lng, { lng = it })
                DialogField("Suhu (°C)", temp, { temp = it })
                DialogField("pH", ph, { ph = it })
                DialogField("TDS (ppm)", tds, { tds = it })
                DialogField("Turbidity (NTU)", turb, { turb = it })
                DialogField("Catatan", notes, { notes = it })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onAdd(
                    name.ifBlank { "Lokasi Baru" },
                    lat.toDoubleOrNull() ?: -6.5833,
                    lng.toDoubleOrNull() ?: 110.6667,
                    temp.toDoubleOrNull() ?: 25.0,
                    ph.toDoubleOrNull() ?: 7.0,
                    tds.toDoubleOrNull() ?: 200.0,
                    turb.toDoubleOrNull() ?: 2.0,
                    notes,
                )
            }) {
                Text("TAMBAH", color = AccentCyan)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("BATAL")
            }
        },
    )
}

@Composable
private fun DialogField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
        )
    }
}
