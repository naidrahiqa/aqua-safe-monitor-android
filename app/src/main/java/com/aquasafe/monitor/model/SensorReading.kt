package com.aquasafe.monitor.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Status kualitas air (Indonesia) */
enum class WaterStatus(val label: String) {
    SANGAT_LAYAK("SANGAT LAYAK"),
    LAYAK("LAYAK"),
    BAHAYA("BAHAYA");

    companion object {
        fun fromLabel(label: String?): WaterStatus =
            entries.firstOrNull { it.label.equals(label, ignoreCase = true) } ?: LAYAK
    }
}

/** Satu baris pembacaan sensor dari tabel `sensor_data` */
@Serializable
data class SensorReading(
    val id: String,
    @SerialName("device_id") val deviceId: String,
    val temperature: Double,
    val ph: Double,
    val tds: Double,
    val turbidity: Double,
    @SerialName("wqi_score") val wqiScore: Double,
    val status: String,
    @SerialName("created_at") val createdAt: String,
) {
    val waterStatus: WaterStatus get() = WaterStatus.fromLabel(status)
    val timestampMillis: Long
        get() = runCatching { java.time.Instant.parse(createdAt).toEpochMilli() }
            .getOrDefault(System.currentTimeMillis())
}

/** Konfigurasi tiap sensor — dipakai di gauge, chart, dan tab */
data class SensorConfig(
    val key: String,
    val label: String,
    val unit: String,
    val min: Double,
    val max: Double,
    val safeMin: Double,
    val safeMax: Double,
    val decimals: Int = 1,
    val color: Long,
    val safeNote: String,
)

object SensorConfigs {
    val PH = SensorConfig(
        key = "ph", label = "pH Level", unit = "pH",
        min = 0.0, max = 14.0, safeMin = 6.5, safeMax = 8.5,
        color = 0xFFF59E0B,
        safeNote = "Ideal 6.5 - 8.5 (WHO)",
    )
    val SUHU = SensorConfig(
        key = "suhu", label = "Suhu Air", unit = "°C",
        min = 0.0, max = 50.0, safeMin = 20.0, safeMax = 30.0,
        color = 0xFFF97316,
        safeNote = "Normal 20 - 30°C",
    )
    val TDS = SensorConfig(
        key = "tds", label = "TDS", unit = "ppm",
        min = 0.0, max = 2000.0, safeMin = 0.0, safeMax = 500.0,
        decimals = 0,
        color = 0xFF22C55E,
        safeNote = "Max aman 500 ppm (Permenkes)",
    )
    val TURBIDITY = SensorConfig(
        key = "turbidity", label = "Turbidity", unit = "NTU",
        min = 0.0, max = 200.0, safeMin = 0.0, safeMax = 5.0,
        color = 0xFF8B5CF6,
        safeNote = "Max aman 5 NTU (Permenkes 492/2010)",
    )
    val WQI = SensorConfig(
        key = "wqi", label = "Water Quality Index", unit = "/100",
        min = 0.0, max = 100.0, safeMin = 80.0, safeMax = 100.0,
        decimals = 0,
        color = 0xFF22D3EE,
        safeNote = "Skor 80-100 sangat layak",
    )

    val ALL = listOf(PH, SUHU, TDS, TURBIDITY)
    val byKey: Map<String, SensorConfig> = ALL.associateBy { it.key }
}

/** Ekstrak nilai sensor dari pembacaan */
fun SensorReading.valueOf(config: SensorConfig): Double = when (config.key) {
    "ph" -> ph
    "suhu" -> temperature
    "tds" -> tds
    "turbidity" -> turbidity
    "wqi" -> wqiScore
    else -> 0.0
}