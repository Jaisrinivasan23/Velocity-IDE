package com.velocity.ide.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Velocity-IDE Design System Tokens.
 *
 * Premium minimal-dark surfaces with the retained brand orange accent.
 * All screens must consume these tokens only — never inline hex values.
 */

// ── Surfaces ────────────────────────────────────────────────────────────────
/** Primary page background (darkest). */
val BgPrimary = Color(0xFF0E0F11)

/** Elevated surfaces: headers, top bars, cards on primary background. */
val BgElevated = Color(0xFF141519)

/** Interactive/elevated containers: inputs, dialogs, active tabs, code panels. */
val BgSurface = Color(0xFF1C1E24)

/** Highest contrast containers: dropdowns, floating elements. */
val BgOverlay = Color(0xFF23262D)

/** Terminal-style surfaces (pure black). */
val BgTerminal = Color(0xFF0A0B0C)

// ── Borders ─────────────────────────────────────────────────────────────────
/** Standard hairline border. */
val BorderDefault = Color(0xFF2A2D35)

/** Very subtle divider border (white ~8%). */
val BorderSubtle = Color(0x14FFFFFF)

// ── Text ────────────────────────────────────────────────────────────────────
/** Primary text. */
val TextPrimary = Color(0xFFF2F3F5)

/** Secondary text / labels. */
val TextSecondary = Color(0xFF9AA1AC)

/** Muted / disabled / placeholders. */
val TextMuted = Color(0xFF6B7280)

// ── Accent (retained brand orange) ──────────────────────────────────────────
/** Brand accent. */
val AccentOrange = Color(0xFFFD5E02)

/** Accent in pressed / highlighted states. */
val AccentPressed = Color(0xFFFF7A2A)

/** Accent at ~14% alpha for soft container tints. */
val AccentSoft = Color(0x24FD5E02)

// ── Status ──────────────────────────────────────────────────────────────────
val StatusSuccess = Color(0xFF35D07F)
val StatusWarning = Color(0xFFF5B94C)
val StatusError = Color(0xFFFF5C68)
val StatusInfo = Color(0xFF5BA7FF)

val StatusSuccessSoft = Color(0x2235D07F)
val StatusWarningSoft = Color(0x22F5B94C)
val StatusErrorSoft = Color(0x22FF5C68)

// ── Radii ───────────────────────────────────────────────────────────────────
val RadiusControl = 8.dp
val RadiusCard = 12.dp
val RadiusSheet = 16.dp
val RadiusPill = 999.dp

// ── Motion (Material defaults are 150–250ms already; kept for reference) ───
const val MotionFastMs = 150

// ── Accent presets (Appearance settings) ────────────────────────────────────
data class AccentPreset(
    val name: String,
    val display: String,
    val primary: Color,
    val glow: Color
)

val AccentPresets: Map<String, AccentPreset> = linkedMapOf(
    "orange" to AccentPreset("orange", "Velocity Orange", AccentOrange, AccentOrange),
    "violet" to AccentPreset("violet", "Electric Violet", Color(0xFF7C5CFF), Color(0xFF7C5CFF)),
    "teal" to AccentPreset("teal", "Mint Teal", Color(0xFF2DD4BF), Color(0xFF2DD4BF)),
    "blue" to AccentPreset("blue", "Signal Blue", Color(0xFF3B82F6), Color(0xFF3B82F6)),
    "pink" to AccentPreset("pink", "Rose Pink", Color(0xFFEC4899), Color(0xFFEC4899)),
    "lime" to AccentPreset("lime", "Acid Lime", Color(0xFFA3E635), Color(0xFFA3E635))
)