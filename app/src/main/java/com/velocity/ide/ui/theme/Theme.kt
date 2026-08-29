package com.velocity.ide.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = AccentOrange,
    onPrimary = Color.White,
    primaryContainer = AccentSoft,
    onPrimaryContainer = AccentPressed,
    secondary = BgElevated,
    onSecondary = TextSecondary,
    secondaryContainer = BgSurface,
    onSecondaryContainer = TextSecondary,
    tertiary = StatusInfo,
    onTertiary = Color.White,
    tertiaryContainer = StatusInfo.copy(alpha = 0.16f),
    onTertiaryContainer = StatusInfo,
    background = BgPrimary,
    onBackground = TextPrimary,
    surface = BgElevated,
    onSurface = TextPrimary,
    surfaceVariant = BgSurface,
    onSurfaceVariant = TextSecondary,
    inverseSurface = TextPrimary,
    inverseOnSurface = BgPrimary,
    inversePrimary = AccentPressed,
    surfaceTint = AccentOrange,
    error = StatusError,
    onError = Color.White,
    errorContainer = StatusErrorSoft,
    onErrorContainer = StatusError,
    outline = BorderDefault,
    outlineVariant = BorderSubtle,
    scrim = Color(0xCC0E0F11)
)

private fun colorSchemeWithAccent(primary: Color, glow: Color): androidx.compose.material3.ColorScheme =
    DarkColorScheme.copy(
        primary = primary,
        primaryContainer = glow.copy(alpha = 0.14f),
        onPrimary = Color.White,
        inversePrimary = glow,
        surfaceTint = primary
    )

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.1).sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.1).sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp
    )
)

private val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(RadiusControl),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(RadiusCard),
    large = androidx.compose.foundation.shape.RoundedCornerShape(RadiusSheet),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
)

@Composable
fun VelocityTheme(
    accentName: String = "orange",
    content: @Composable () -> Unit
) {
    val accent = AccentPresets[accentName] ?: AccentPresets.getValue("orange")
    MaterialTheme(
        colorScheme = colorSchemeWithAccent(accent.primary, accent.glow),
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}