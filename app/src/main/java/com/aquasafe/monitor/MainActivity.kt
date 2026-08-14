package com.aquasafe.monitor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aquasafe.monitor.notifications.NotificationHelper
import com.aquasafe.monitor.notifications.NotifyWorker
import com.aquasafe.monitor.ui.navigation.AppRoot
import com.aquasafe.monitor.ui.theme.WaterSafeTheme
import org.osmdroid.config.Configuration
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        enableEdgeToEdge()

        // Keep splash visible while loading (optional: add logic to wait for data)
        splashScreen.setKeepOnScreenCondition { false }

        NotificationHelper.ensureChannel(this)
        scheduleAlertWorker()
        requestNotificationPermissionIfNeeded()

        setContent {
            WaterSafeTheme {
                AppRoot()
            }
        }
    }

    /** Polling notifikasi BAHAYA tiap 15 menit (WorkManager, tahan reboot). */
    private fun scheduleAlertWorker() {
        val request = PeriodicWorkRequestBuilder<NotifyWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            NotifyWorker.UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}