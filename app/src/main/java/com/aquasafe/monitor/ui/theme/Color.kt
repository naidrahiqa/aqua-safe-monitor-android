package com.aquasafe.monitor.ui.theme

import androidx.compose.ui.graphics.Color

// ===================================================================
// AquaSafe Industrial Palette — v3 (LIGHT + DARK)
// Light = paper + ink. Dark = control room charcoal + light text.
// Tokens are getters reading the active theme flag (set by WaterSafeTheme)
// so every component switches automatically.
// ===================================================================

interface Palette {
    val SurfaceDark: Color
    val SurfaceDeep: Color
    val Panel: Color
    val PanelLight: Color
    val Border: Color
    val BorderLight: Color
    val TextPrimary: Color
    val TextSecondary: Color
    val TextMuted: Color
    val AccentCyan: Color
    val AccentCyanSoft: Color
    val Success: Color
    val Warning: Color
    val Danger: Color
    val PhColor: Color
    val SuhuColor: Color
    val TdsColor: Color
    val TurbidityColor: Color
    val WqiColor: Color
    val PinkAccent: Color
    val OnAccent: Color
}

object LightColors : Palette {
    // Background layers — warm paper, flat
    override val SurfaceDark = Color(0xFFF2EFE9)
    override val SurfaceDeep = Color(0xFFF2EFE9)
    override val Panel = Color(0xFFFAF8F4)
    override val PanelLight = Color(0xFFE4E0D6)

    // Lines & rules — ink
    override val Border = Color(0xFF1A1A1A)
    override val BorderLight = Color(0xFF1A1A1A)

    // Text — ink hierarchy
    override val TextPrimary = Color(0xFF1A1A1A)
    override val TextSecondary = Color(0xFF3F3F3F)
    override val TextMuted = Color(0xFF6B675E)

    // Accent — instrument blue
    override val AccentCyan = Color(0xFF1D4ED8)
    override val AccentCyanSoft = Color(0xFF3B6AD9)

    // Status — indicator lamps
    override val Success = Color(0xFF2E7D32)
    override val Warning = Color(0xFFB26A00)
    override val Danger = Color(0xFFC62828)

    // Sensor accents
    override val PhColor = Color(0xFFB26A00)
    override val SuhuColor = Color(0xFFD84315)
    override val TdsColor = Color(0xFF2E7D32)
    override val TurbidityColor = Color(0xFF6A3D9A)
    override val WqiColor = Color(0xFF1D4ED8)

    override val PinkAccent = Color(0xFFC2185B)
    override val OnAccent = Color(0xFFFFFFFF)
}

object DarkColors : Palette {
    // Background — control room charcoal
    override val SurfaceDark = Color(0xFF14161A)
    override val SurfaceDeep = Color(0xFF14161A)
    override val Panel = Color(0xFF1E2229)
    override val PanelLight = Color(0xFF2A303B)

    // Lines — lighter ink on dark
    override val Border = Color(0xFF4A5160)
    override val BorderLight = Color(0xFF5A6273)

    // Text
    override val TextPrimary = Color(0xFFEAE8E2)
    override val TextSecondary = Color(0xFFB7BBC4)
    override val TextMuted = Color(0xFF878D99)

    // Accent — brighter instrument blue
    override val AccentCyan = Color(0xFF6EA8FF)
    override val AccentCyanSoft = Color(0xFF4C9AFF)

    // Status — lit lamps
    override val Success = Color(0xFF57C77E)
    override val Warning = Color(0xFFE0A94E)
    override val Danger = Color(0xFFE4646B)

    // Sensor accents
    override val PhColor = Color(0xFFE0A94E)
    override val SuhuColor = Color(0xFFF2803F)
    override val TdsColor = Color(0xFF57C77E)
    override val TurbidityColor = Color(0xFFB48AE0)
    override val WqiColor = Color(0xFF6EA8FF)

    override val PinkAccent = Color(0xFFF060A8)
    override val OnAccent = Color(0xFFFFFFFF)
}

/** Active palette flag — set at the start of WaterSafeTheme composition. */
var isDarkTheme: Boolean = false

private val ActivePalette: Palette
    get() = if (isDarkTheme) DarkColors else LightColors

// ================= Public tokens (theme-aware getters) =================
val SurfaceDark: Color get() = ActivePalette.SurfaceDark
val SurfaceDeep: Color get() = ActivePalette.SurfaceDeep
val Panel: Color get() = ActivePalette.Panel
val PanelLight: Color get() = ActivePalette.PanelLight
val Border: Color get() = ActivePalette.Border
val BorderLight: Color get() = ActivePalette.BorderLight
val ShadowColor: Color get() = ActivePalette.Border
val TextPrimary: Color get() = ActivePalette.TextPrimary
val TextSecondary: Color get() = ActivePalette.TextSecondary
val TextMuted: Color get() = ActivePalette.TextMuted
val AccentCyan: Color get() = ActivePalette.AccentCyan
val AccentCyanSoft: Color get() = ActivePalette.AccentCyanSoft
val AccentCyanBg: Color get() = ActivePalette.AccentCyan
val Success: Color get() = ActivePalette.Success
val Warning: Color get() = ActivePalette.Warning
val Danger: Color get() = ActivePalette.Danger
val PhColor: Color get() = ActivePalette.PhColor
val SuhuColor: Color get() = ActivePalette.SuhuColor
val TdsColor: Color get() = ActivePalette.TdsColor
val TurbidityColor: Color get() = ActivePalette.TurbidityColor
val WqiColor: Color get() = ActivePalette.WqiColor
val PinkAccent: Color get() = ActivePalette.PinkAccent
val OnAccent: Color get() = ActivePalette.OnAccent