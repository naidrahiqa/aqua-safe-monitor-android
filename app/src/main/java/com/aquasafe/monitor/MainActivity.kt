package com.aquasafe.monitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aquasafe.monitor.ui.navigation.AppRoot
import com.aquasafe.monitor.ui.theme.WaterSafeTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        enableEdgeToEdge()
        setContent {
            WaterSafeTheme {
                AppRoot()
            }
        }
    }
}