package com.aquasafe.monitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.BuildConfig
import com.aquasafe.monitor.data.SupabaseConfig
import com.aquasafe.monitor.model.SensorConfigs
import com.aquasafe.monitor.ui.theme.AccentCyan
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.Danger
import com.aquasafe.monitor.ui.theme.HardShadowSm
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary
import com.aquasafe.monitor.ui.theme.TextSecondary
import com.aquasafe.monitor.viewmodel.DashboardUiState

@Composable
fun SettingsScreen(state: DashboardUiState) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            "PENGATURAN",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
        )
        Spacer(Modifier.height(16.dp))

        // Supabase status
        NeoPanel {
            Text(
                "KONEKSI SUPABASE",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(if (SupabaseConfig.isConfigured) Success else Danger, CircleShape),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (SupabaseConfig.isConfigured) "TERHUBUNG" else "BELUM DIKONFIGURASI",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (SupabaseConfig.isConfigured) Success else Danger,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                if (SupabaseConfig.isConfigured) "Data sensor akan dimuat dari Supabase."
                else "Isi SUPABASE_URL & SUPABASE_ANON_KEY di local.properties.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }

        Spacer(Modifier.height(14.dp))

        // Thresholds
        NeoPanel {
            Text(
                "AMBANG BATAS AIR",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            SensorConfigs.ALL.forEach { config ->
                Row(Modifier.padding(vertical = 6.dp)) {
                    Text(
                        config.label.uppercase(),
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

        Spacer(Modifier.height(14.dp))

        // About
        NeoPanel {
            Text(
                "TENTANG APLIKASI",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "WaterSafe Monitor v${BuildConfig.VERSION_NAME}",
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

        Spacer(Modifier.height(14.dp))
        Text(
            "TOTAL ${state.readings.size} DATA TERBACA DARI SENSOR",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
        )
        Spacer(Modifier.height(24.dp))
    }
}

/** Neubrutalism panel wrapper */
@Composable
private fun NeoPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .matchParentSize()
                .offset(HardShadowSm.x, HardShadowSm.y)
                .background(Color.Black.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                .border(BorderWidth, Border, MaterialTheme.shapes.medium)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Panel, MaterialTheme.shapes.medium)
                .border(BorderWidth, Border, MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            content()
        }
    }
}
