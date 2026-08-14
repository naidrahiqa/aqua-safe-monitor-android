package com.aquasafe.monitor.ui.theme

import androidx.compose.ui.graphics.Color

// ===================================================================
// AquaSafe Industrial Panel Palette — v3
// Real instrument feel: paper background, ink text, 2px rules,
// indicator lamps (deep saturated), NO shadows, NO candy colors.
// Reference: industrial HMI panels, dataloggers, old test equipment.
// ===================================================================

// Background layers — warm paper, flat
val SurfaceDark = Color(0xFFF2EFE9)      // Main background (paper)
val SurfaceDeep = Color(0xFFF2EFE9)      // Flat — no gradient
val Panel = Color(0xFFFAF8F4)            // Card background (lighter paper)
val PanelLight = Color(0xFFE4E0D6)       // Inset / track (slightly darker paper)

// Lines & rules — ink
val Border = Color(0xFF1A1A1A)           // 2px ink rules
val BorderLight = Color(0xFF1A1A1A)      // Emphasis (same ink)
val ShadowColor = Color(0xFF1A1A1A)      // (unused — industrial has no shadows)

// Text — ink hierarchy
val TextPrimary = Color(0xFF1A1A1A)      // Headings, values
val TextSecondary = Color(0xFF3F3F3F)    // Body text
val TextMuted = Color(0xFF6B675E)        // Labels, hints

// Accent — instrument blue
val AccentCyan = Color(0xFF1D4ED8)       // Deep instrument blue
val AccentCyanSoft = Color(0xFF3B6AD9)   // Lighter variant
val AccentCyanBg = Color(0xFF1D4ED8)     // Badge bg

// Status — indicator lamps (deep, not candy)
val Success = Color(0xFF2E7D32)          // Lamp green
val Warning = Color(0xFFB26A00)          // Lamp amber
val Danger = Color(0xFFC62828)           // Lamp red

// Sensor accents — instrument series
val PhColor = Color(0xFFB26A00)          // Amber
val SuhuColor = Color(0xFFD84315)        // Burnt orange
val TdsColor = Color(0xFF2E7D32)         // Green
val TurbidityColor = Color(0xFF6A3D9A)   // Violet
val WqiColor = Color(0xFF1D4ED8)         // Blue

// Special
val PinkAccent = Color(0xFFC2185B)
val OnAccent = Color(0xFFFFFFFF)         // Text on filled/ink = white