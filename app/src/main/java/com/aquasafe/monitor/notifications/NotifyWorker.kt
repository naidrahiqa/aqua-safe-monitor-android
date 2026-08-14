package com.aquasafe.monitor.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aquasafe.monitor.data.SensorRepository
import com.aquasafe.monitor.data.SupabaseConfig
import com.aquasafe.monitor.model.WaterStatus

/**
 * Worker periodik yang mengecek data sensor terbaru dari Supabase dan
 * memunculkan notifikasi saat status kualitas air berubah menjadi BAHAYA.
 *
 * Dedup: hanya mengirim notifikasi jika status terakhir yang sudah
 * dilaporkan (tersimpan di SharedPreferences) belum BAHAYA.
 */
class NotifyWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!SupabaseConfig.isConfigured) return Result.retry()

        val latest = runCatching { SensorRepository().fetchLatest() }.getOrNull()
        if (latest == null || latest.waterStatus != WaterStatus.BAHAYA) {
            return Result.success()
        }

        val prefs = applicationContext.getSharedPreferences("water_alert", Context.MODE_PRIVATE)
        val lastNotifiedStatus = prefs.getString(KEY_LAST_STATUS, WaterStatus.LAYAK.name)

        if (lastNotifiedStatus != WaterStatus.BAHAYA.name) {
            NotificationHelper.notifyDanger(
                applicationContext,
                latest.wqiScore,
                "pH ${latest.ph}, TDS ${latest.tds.toInt()} ppm, " +
                    "Turbidity ${latest.turbidity} NTU, ${latest.temperature}°C",
            )
        }

        prefs.edit().putString(KEY_LAST_STATUS, WaterStatus.BAHAYA.name).apply()
        return Result.success()
    }

    companion object {
        private const val KEY_LAST_STATUS = "last_notified_status"
        const val UNIQUE_NAME = "water-safety-alert-worker"
    }
}