package com.aquasafe.monitor.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.aquasafe.monitor.MainActivity
import com.aquasafe.monitor.R
import com.aquasafe.monitor.model.WaterStatus

/** Membuat channel & menampilkan notifikasi peringatan kualitas air. */
object NotificationHelper {

    const val CHANNEL_ID = "water_safety_alerts"
    private const val NOTIFICATION_ID = 1001

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Peringatan Kualitas Air",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Notifikasi saat kualitas air berada di ambang berbahaya"
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /** Notifikasi BAHAYA — hanya dipanggil saat status berubah menjadi BAHAYA. */
    fun notifyDanger(context: Context, wqi: Double, detail: String) {
        ensureChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return // izin belum diberikan
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_alert)
            .setContentTitle("⚠️ Kualitas Air BAHAYA")
            .setContentText("WQI $wqi/100 — $detail")
            .setStyle(NotificationCompat.BigTextStyle().bigText("WQI $wqi/100 — $detail"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}