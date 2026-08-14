package com.aquasafe.monitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.model.WaterStatus
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.Danger
import com.aquasafe.monitor.ui.theme.OnAccent
import com.aquasafe.monitor.ui.theme.Radius
import com.aquasafe.monitor.ui.theme.Success
import com.aquasafe.monitor.ui.theme.Warning

@Composable
fun StatusChip(status: WaterStatus, modifier: Modifier = Modifier) {
    val (color, label) = when (status) {
        WaterStatus.SANGAT_LAYAK -> Success to "SANGAT LAYAK"
        WaterStatus.LAYAK -> Warning to "LAYAK"
        WaterStatus.BAHAYA -> Danger to "BAHAYA"
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color, RoundedCornerShape(Radius.sm))
            .border(BorderWidth, Border, RoundedCornerShape(Radius.sm))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Spacer(
            Modifier
                .size(6.dp)
                .background(OnAccent, CircleShape),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = OnAccent,
        )
    }
}