package com.aquasafe.monitor.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.data.SupabaseConfig
import com.aquasafe.monitor.model.SensorConfigs
import com.aquasafe.monitor.ui.components.PanelCard
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.TextSecondary
import com.aquasafe.monitor.viewmodel.DashboardUiState

/** Pengaturan: konfigurasi supabase, ambang batas, tentang aplikasi */
@Composable
fun SettingsScreen(state: DashboardUiState) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            "Pengaturan",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )
        Spacer(Modifier.height(16.dp))

        PanelCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium as RoundedCornerShape,
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Konfigurasi Supabase",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(
                                if (SupabaseConfig.isConfigured) Success
                                else MaterialTheme.colorScheme.error,
                                CircleShape,
                            ),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (SupabaseConfig.isConfigured) "Terkonfigurasi"
                        else "Belum dikonfigurasi",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (SupabaseConfig.isConfigured) Success
                        else MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    if (SupabaseConfig.isConfigured) "Terkonfigurasi — data sensor akan termuat."
                    else "Belum di-set. Edit file SupabaseConfig.kt dengan URL & anon key project kamu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (SupabaseConfig.isConfigured) Success else TextSecondary,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    SupabaseConfig.SUPABASE_URL,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        PanelCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium as RoundedCornerShape,
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Ambang Batas Kualitas Air",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(8.dp))
                SensorConfigs.ALL.forEach { config ->
                    Row(Modifier.padding(vertical = 6.dp)) {
                        Text(
                            config.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            config.safeNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        PanelCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium as RoundedCornerShape,
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Tentang Aplikasi",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "WaterSafe Monitor v1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                Text(
                    "Alur data: ESP32 → Supabase → Aplikasi Android. " +
                            "Lokasi pengujian tersimpan lokal di device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            "Total ${state.readings.size} data terbaca dari sensor.",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
        )
        Spacer(Modifier.height(24.dp))
    }
}