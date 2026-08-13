package com.aquasafe.monitor.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aquasafe.monitor.model.TestLocation
import com.aquasafe.monitor.ui.components.StatusChip
import com.aquasafe.monitor.ui.theme.AccentCyan
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.TextSecondary
import com.aquasafe.monitor.ui.theme.statusColor
import com.aquasafe.monitor.ui.util.fmtAgoIso
import com.aquasafe.monitor.viewmodel.DashboardUiState
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val DEFAULT_LAT = -6.5833
private const val DEFAULT_LNG = 110.6667

/** Lokasi pengujian: peta OSM (gratis, tanpa API key) + daftar lokasi + sinkron dari sensor */
@Composable
fun MapScreen(
    state: DashboardUiState,
    onAdd: (name: String, lat: Double, lng: Double, temperature: Double, ph: Double, tds: Double, turbidity: Double, notes: String) -> Unit,
    onRemove: (String) -> Unit,
    onSync: (String) -> Unit,
) {
    val context = LocalContext.current
    val locations = state.locations
    var pendingPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                "Lokasi Pengujian",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Pin = lokasi pengujian. Ketuk peta lalu tombol + untuk menambah.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
            Spacer(Modifier.height(12.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp)),
            ) {
                val markerIcons = remember {
                    MarkerIcons(context)
                }
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setMultiTouchControls(true)
                            controller.setZoom(12.0)
                            controller.setCenter(GeoPoint(DEFAULT_LAT, DEFAULT_LNG))
                            val detector = GestureDetector(
                                ctx,
                                object : GestureDetector.SimpleOnGestureListener() {
                                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                                        val p = projection.fromPixels(e.x.toInt(), e.y.toInt())
                                        pendingPoint = GeoPoint(p.latitude, p.longitude)
                                        return true
                                    }
                                },
                            )
                            setOnTouchListener { _, event ->
                                detector.onTouchEvent(event)
                                onTouchEvent(event)
                            }
                        }
                    },
                    update = { mapView ->
                        mapView.overlays.clear()
                        locations.forEachIndexed { index, location ->
                            mapView.overlays.add(
                                Marker(mapView).apply {
                                    position = GeoPoint(location.lat, location.lng)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                    icon = markerIcons.forStatus(location.waterStatus)
                                    title = "${index + 1}. ${location.name}"
                                    snippet = "${location.waterStatus.label} • WQI ${location.wqiScore.toInt()}"
                                },
                            )
                        }
                        pendingPoint?.let { point ->
                            mapView.overlays.add(
                                Marker(mapView).apply {
                                    position = point
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                    icon = markerIcons.pending
                                    title = "Titik baru"
                                    snippet = "Ketuk + untuk menyimpan"
                                },
                            )
                        }
                        mapView.invalidate()
                    },
                )
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Panel.copy(alpha = 0.88f))
                        .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        "Ketuk peta untuk menandai titik",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))

            if (locations.isEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Belum ada lokasi. Tekan + untuk menambah lokasi pengujian.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }

            Spacer(Modifier.height(16.dp))
            locations.forEachIndexed { index, location ->
                LocationCard(
                    index = index,
                    location = location,
                    syncing = state.syncingLocationId == location.id,
                    onSync = { onSync(location.id) },
                    onRemove = { onRemove(location.id) },
                )
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(80.dp))
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surface,
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Tambah lokasi")
        }
    }

    if (showAddDialog) {
        AddLocationDialog(
            point = pendingPoint ?: GeoPoint(DEFAULT_LAT, DEFAULT_LNG),
            onConfirm = { name, lat, lng, temperature, ph, tds, turbidity, notes ->
                onAdd(name, lat, lng, temperature, ph, tds, turbidity, notes)
                showAddDialog = false
                pendingPoint = null
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

/** Ikon pin peta yang digambar manual — warna mengikuti status kualitas air */
private class MarkerIcons(private val context: Context) {
    val pending: BitmapDrawable by lazy { build(Color.White.copy(alpha = 0.9f), AccentCyan) }

    fun forStatus(status: com.aquasafe.monitor.model.WaterStatus): BitmapDrawable {
        val color = statusColor(status)
        return build(color.copy(alpha = 0.95f), color)
    }

    private fun build(fill: Color, ring: Color): BitmapDrawable {
        val density = context.resources.displayMetrics.density
        val size = (30 * density).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val stroke = 3f * density
        val radius = size / 2f - stroke
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = fill.toArgb()
            style = Paint.Style.FILL
        }
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ring.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = stroke
        }
        val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 1.5f * density
        }
        canvas.drawCircle(size / 2f, size / 2f, radius, fillPaint)
        canvas.drawCircle(size / 2f, size / 2f, radius, ringPaint)
        canvas.drawCircle(size / 2f, size / 2f, radius - stroke, whitePaint)
        return BitmapDrawable(context.resources, bitmap)
    }
}

@Composable
private fun LocationCard(
    index: Int,
    location: TestLocation,
    syncing: Boolean,
    onSync: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${index + 1}. ${location.name}",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                    )
                    if (location.notes.isNotBlank()) {
                        Text(
                            location.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                    }
                }
                StatusChip(location.waterStatus)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Suhu ${location.temperature}°C • pH ${location.ph} • TDS ${location.tds.toInt()} ppm • NTU ${location.turbidity}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Spacer(Modifier.height(6.dp))
            if (location.isSynced) {
                Text(
                    "Data dari ESP32 sensor • ${fmtAgoIso(location.syncedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row {
                TextButton(onClick = onSync, enabled = !syncing) {
                    if (syncing) {
                        CircularProgressIndicator(Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            Icons.Rounded.Sync,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(if (syncing) "Sinkron..." else "Sinkron dari Sensor")
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Hapus lokasi",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddLocationDialog(
    point: GeoPoint,
    onConfirm: (name: String, lat: Double, lng: Double, temperature: Double, ph: Double, tds: Double, turbidity: Double, notes: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("25") }
    var ph by remember { mutableStateOf("") }
    var tds by remember { mutableStateOf("") }
    var turbidity by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Lokasi Pengujian") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama lokasi *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = temperature,
                        onValueChange = { temperature = it },
                        label = { Text("Suhu °C") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = ph,
                        onValueChange = { ph = it },
                        label = { Text("pH") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = tds,
                        onValueChange = { tds = it },
                        label = { Text("TDS ppm") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = turbidity,
                        onValueChange = { turbidity = it },
                        label = { Text("NTU") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Titik peta: ${String.format("%.4f, %.4f", point.latitude, point.longitude)} (ketuk peta untuk pindah)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onConfirm(
                        name.trim(),
                        point.latitude,
                        point.longitude,
                        temperature.toDoubleOrNull() ?: 0.0,
                        ph.toDoubleOrNull() ?: 0.0,
                        tds.toDoubleOrNull() ?: 0.0,
                        turbidity.toDoubleOrNull() ?: 0.0,
                        notes.trim(),
                    )
                },
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
    )
}