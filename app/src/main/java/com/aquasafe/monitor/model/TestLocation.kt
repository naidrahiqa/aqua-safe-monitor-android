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

/** Perhitungan WQI sederhana — sama dengan versi web */
fun computeWQI(pH: Double, tds: Double, turbidity: Double, temperature: Double): Double {
    var score = 100.0
    if (pH < 6.5 || pH > 8.5) score -= 20
    if (pH < 5 || pH > 10) score -= 30
    if (tds > 500) score -= 15
    if (tds > 1000) score -= 20
    if (turbidity > 5) score -= 15
    if (turbidity > 25) score -= 25
    if (temperature < 20 || temperature > 30) score -= 10
    return score.coerceIn(0.0, 100.0)
}

fun wqiStatus(wqi: Double): WaterStatus = when {
    wqi >= 80 -> WaterStatus.SANGAT_LAYAK
    wqi >= 50 -> WaterStatus.LAYAK
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
            wqiScore = 92.0, status = WaterStatus.SANGAT_LAYAK.label,
            notes = "Air sumur belakang rumah Pak Budi", createdAt = now,
        ),
        TestLocation(
            id = "TL-DEMO-2", name = "Kolam Ikan Hias",
            lat = -6.5850, lng = 110.6700,
            temperature = 27.5, ph = 6.5, tds = 245.0, turbidity = 5.2,
            wqiScore = 65.0, status = WaterStatus.LAYAK.label,
            notes = "Kolam depan sekolah", createdAt = now,
        ),
        TestLocation(
            id = "TL-DEMO-3", name = "Saluran Irigasi Sawah",
            lat = -6.5810, lng = 110.6640,
            temperature = 28.2, ph = 5.9, tds = 320.0, turbidity = 8.6,
            wqiScore = 42.0, status = WaterStatus.BAHAYA.label,
            notes = "Saluran utama dari sungai", createdAt = now,
        ),
    )
}
