package com.aquasafe.monitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aquasafe.monitor.ui.navigation.AppRoot
import com.aquasafe.monitor.ui.theme.WaterSafeTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        enableEdgeToEdge()

        // Keep splash visible while loading (optional: add logic to wait for data)
        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            WaterSafeTheme {
                AppRoot()
            }
        }
    }
}
