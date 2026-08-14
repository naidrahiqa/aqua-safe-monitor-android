package com.aquasafe.monitor.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===================================================================
// Design tokens — neubrutalism spacing, radius, shadows
// ===================================================================

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val page = 16.dp
}

object Radius {
    val sm = 2.dp
    val md = 2.dp
    val lg = 4.dp
    val xl = 6.dp
    val pill = 50.dp
}

object IconSize {
    val sm = 16.dp
    val md = 20.dp
    val lg = 24.dp
    val xl = 32.dp
}

object FontSize {
    val xs = 10.sp
    val sm = 12.sp
    val md = 14.sp
    val lg = 16.sp
    val xl = 20.sp
    val xxl = 28.sp
    val hero = 44.sp
}

// Neubrutalism shadow — hard offset, no blur
val HardShadow = androidx.compose.ui.unit.DpOffset(4.dp, 4.dp)
val HardShadowSm = androidx.compose.ui.unit.DpOffset(3.dp, 3.dp)
val BorderWidth = 2.dp
