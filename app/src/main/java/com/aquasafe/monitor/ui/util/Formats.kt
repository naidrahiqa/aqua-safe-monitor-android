package com.aquasafe.monitor.ui.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val dateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale("id", "ID"))

/** epoch millis → "5 Agu 2026, 14:30" */
fun fmtDateTime(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(dateFormatter)

/** ISO string → "5 Agu 2026, 14:30" (fallback: input mentah) */
fun fmtDateTimeIso(iso: String): String =
    runCatching { fmtDateTime(Instant.parse(iso).toEpochMilli()) }.getOrDefault(iso)

/** "5 menit lalu" / "baru saja" */
fun fmtAgo(millis: Long): String {
    val minutes = ChronoUnit.MINUTES.between(Instant.ofEpochMilli(millis), Instant.now())
    return when {
        minutes < 1 -> "baru saja"
        minutes < 60 -> "$minutes mnt lalu"
        else -> "${minutes / 60} jam lalu"
    }
}

/** ISO string → "baru saja" */
fun fmtAgoIso(iso: String?): String? =
    iso?.let { runCatching { fmtAgo(Instant.parse(it).toEpochMilli()) }.getOrNull() }