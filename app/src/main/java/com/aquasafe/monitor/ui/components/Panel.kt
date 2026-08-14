package com.aquasafe.monitor.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.HardShadowSm
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.Radius
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary

/**
 * Industrial panel card — flat, 2px ink rule, no shadow
 */
@Composable
fun PanelCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(Radius.md),
    containerColor: Color = Panel,
    borderColor: Color = Border,
    shadowOffset: androidx.compose.ui.unit.DpOffset = HardShadowSm,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(BorderWidth, borderColor),
    ) {
        content()
    }
}

/**
 * Section header — uppercase bold number badge + title
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
            style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
        }
    }
}

/**
 * Indicator lamp + label — industrial status, no pill fill
 */
@Composable
fun StatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Box(
            Modifier
                .size(8.dp)
                .background(color),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
        )
    }
}

/**
 * Sticker badge — rotated neubrutalism tag
 */
@Composable
fun StickerBadge(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    rotation: Float = -3f,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .background(color, RoundedCornerShape(Radius.sm))
            .border(BorderWidth, Border, RoundedCornerShape(Radius.sm))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
        )
    }
}
