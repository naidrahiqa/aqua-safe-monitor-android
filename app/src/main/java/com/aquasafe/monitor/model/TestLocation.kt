package com.aquasafe.monitor.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Lokasi pengujian yang disimpan user — muncul sebagai pin di peta */
@Serializable
data class TestLocation(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val temperature: Double,
    val ph: Double,
    val tds: Double,
    val turbidity: Double,
    @SerialName("wqi_score") val wqiScore: Double,
    val status: String,
    val notes: String = "",
    @SerialName("created_at") val createdAt: String,
    @SerialName("synced_at") val syncedAt: String? = null,
) {
    val waterStatus: WaterStatus get() = WaterStatus.fromLabel(status)
    val isSynced: Boolean get() = syncedAt != null
}

/** Weighted-average WQI — matches Edge Function algorithm exactly */
fun computeWQI(pH: Double, tds: Double, turbidity: Double, temperature: Double): Double {
    // pH sub-index (ideal: 6.5-8.5)
    val phScore = when {
        pH in 6.5..8.5 -> 100.0
        pH in 5.0..<6.5 -> 60 + ((pH - 5.0) / 1.5) * 40
        pH in 8.5..10.0 -> 60 + ((10.0 - pH) / 1.5) * 40
        else -> (30 - Math.abs(pH - 7.0) * 5).coerceAtLeast(0.0)
    }

    // TDS sub-index (ideal: < 300 ppm, acceptable: < 500 ppm)
    val tdsScore = when {
        tds <= 300 -> 100.0
        tds <= 500 -> 100 - ((tds - 300) / 200) * 50
        else -> (50 - ((tds - 500) / 500) * 50).coerceAtLeast(0.0)
    }

    // Turbidity sub-index (ideal: < 1 NTU, acceptable: < 5 NTU)
    val turbScore = when {
        turbidity <= 1 -> 100.0
        turbidity <= 5 -> 100 - ((turbidity - 1) / 4) * 30
        else -> (70 - ((turbidity - 5) / 10) * 70).coerceAtLeast(0.0)
    }

    // Temperature sub-index (ideal: 20-30°C)
    val tempScore = when {
        temperature in 20.0..30.0 -> 100.0
        else -> (100 - Math.abs(temperature - 25) * 5).coerceAtLeast(0.0)
    }

    // Weighted average
    val wqi = phScore * 0.3 + tdsScore * 0.25 + turbScore * 0.25 + tempScore * 0.2
    return wqi.coerceIn(0.0, 100.0).let { Math.round(it * 10.0) / 10.0 }
}

fun wqiStatus(wqi: Double): WaterStatus = when {
    wqi >= 80 -> WaterStatus.SANGAT_LAYAK
    wqi >= 60 -> WaterStatus.LAYAK
    else -> WaterStatus.BAHAYA
}

/** Lokasi pengujian bawaan untuk demo pertama */
fun defaultTestLocations(): List<TestLocation> {
    val now = java.time.Instant.now().toString()
    return listOf(
        TestLocation(
            id = "TL-DEMO-1", name = "Sumur Warga RT 05",
            lat = -6.5833, lng = 110.6667,
            temperature = 26.4, ph = 7.2, tds = 185.0, turbidity = 2.1,
            wqiScore = 97.9, status = WaterStatus.SANGAT_LAYAK.label,
            notes = "Air sumur belakang rumah Pak Budi", createdAt = now,
        ),
        TestLocation(
            id = "TL-DEMO-2", name = "Kolam Ikan Hias",
            lat = -6.5850, lng = 110.6700,
            temperature = 27.5, ph = 6.5, tds = 245.0, turbidity = 5.2,
            wqiScore = 89.3, status = WaterStatus.SANGAT_LAYAK.label,
            notes = "Kolam depan sekolah", createdAt = now,
        ),
        TestLocation(
            id = "TL-DEMO-3", name = "Saluran Irigasi Sawah",
            lat = -6.5810, lng = 110.6640,
            temperature = 28.2, ph = 5.9, tds = 320.0, turbidity = 8.6,
            wqiScore = 72.1, status = WaterStatus.LAYAK.label,
            notes = "Saluran utama dari sungai", createdAt = now,
        ),
    )
}
