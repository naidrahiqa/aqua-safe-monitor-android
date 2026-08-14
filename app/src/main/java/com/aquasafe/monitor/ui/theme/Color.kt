package com.aquasafe.monitor.ui.theme

import androidx.compose.ui.graphics.Color

// ===================================================================
// AquaSafe Neubrutalism Palette — v2 (TRUE neubrutalism)
// Light cream background, black borders, candy colors, black text
// Reference: neobrutalism.dev, Gumroad, Panda CSS
// ===================================================================

// Background layers — warm paper cream, not dark
val SurfaceDark = Color(0xFFFFF6E5)      // Main background (cream)
val SurfaceDeep = Color(0xFFFFF6E5)      // Flat — no gradient
val Panel = Color(0xFFFFFFFF)            // Card background (white)
val PanelLight = Color(0xFFF0E9D8)       // Chip / inset variant (dark cream)

// Borders & shadows — pure black
val Border = Color(0xFF000000)           // Default card border
val BorderLight = Color(0xFF000000)      // Emphasis border (also black)
val ShadowColor = Color(0xFF000000)      // Hard shadow

// Text hierarchy — black on cream
val TextPrimary = Color(0xFF0A0A0A)      // Headings, values
val TextSecondary = Color(0xFF3D3D3D)    // Body text
val TextMuted = Color(0xFF6E6E6E)        // Hints, labels

// Accent — candy cyan
val AccentCyan = Color(0xFF00C2FF)
val AccentCyanSoft = Color(0xFF7ADBFF)
val AccentCyanBg = Color(0xFF00C2FF)     // Badge bg

// Status colors — candy fills (text on them = black)
val Success = Color(0xFF1FC968)          // Safe / online (green)
val Warning = Color(0xFFFFDE00)          // Caution (candy yellow)
val Danger = Color(0xFFFF4D4D)           // Danger / error (bright red)

// Sensor accent colors — candy palette
val PhColor = Color(0xFFFFDE00)          // Yellow
val SuhuColor = Color(0xFFFF7A00)        // Orange
val TdsColor = Color(0xFF1FC968)         // Green
val TurbidityColor = Color(0xFFB06AFF)   // Purple
val WqiColor = Color(0xFF00C2FF)         // Cyan

// Special
val PinkAccent = Color(0xFFFF90E8)
val OnAccent = Color(0xFF000000)         // Text on accent bg = black