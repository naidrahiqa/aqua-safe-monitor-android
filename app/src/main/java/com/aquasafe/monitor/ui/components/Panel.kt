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
import androidx.compose.foundation.layout.offset
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
import com.aquasafe.monitor.ui.theme.OnAccent
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.Radius
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextPrimary

/**
 * Neubrutalism card — bold 2px border + hard offset shadow
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
    Box(modifier) {
        // Hard shadow layer
        Box(
            Modifier
                .matchParentSize()
                .offset(shadowOffset.x, shadowOffset.y)
                .border(BorderWidth, borderColor, shape)
                .background(Color.Black.copy(alpha = 0.4f), shape)
        )
        // Main card
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(BorderWidth, borderColor),
        ) {
            content()
        }
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
 * Status pill — solid candy badge, static (no pulsing, no glow)
 */
@Composable
fun StatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color, RoundedCornerShape(Radius.sm))
            .border(BorderWidth, Border, RoundedCornerShape(Radius.sm))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Box(
            Modifier
                .size(7.dp)
                .background(OnAccent, CircleShape),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = OnAccent,
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
