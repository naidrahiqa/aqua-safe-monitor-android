package com.aquasafe.monitor.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aquasafe.monitor.ui.theme.Border
import com.aquasafe.monitor.ui.theme.BorderWidth
import com.aquasafe.monitor.ui.theme.HardShadow
import com.aquasafe.monitor.ui.theme.HardShadowSm
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
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
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
                .background(Color.Black.copy(alpha = 0.3f), shape)
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
 * Status pill — colored badge with optional pulsing dot
 */
@Composable
fun StatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    pulsing: Boolean = false,
) {
    val transition = rememberInfiniteTransition(label = "statusPulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseAlpha",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(Radius.pill))
            .border(BorderWidth, color.copy(alpha = 0.4f), RoundedCornerShape(Radius.pill))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Box(
            Modifier
                .size(7.dp)
                .scale(if (pulsing) pulse else 1f)
                .background(color.copy(alpha = if (pulsing) pulse else 1f), CircleShape),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
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
