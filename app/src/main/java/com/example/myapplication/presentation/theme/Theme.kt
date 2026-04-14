package com.example.myapplication.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

/**
 * Farbpalette mit hohem Kontrast fuer dunkle Wear-OS-Displays.
 *
 * Kontrastverhaeltnisse zum schwarzen Hintergrund (WCAG AA ≥ 4.5:1):
 * - onBackground (Weiss):        21:1  – Hauptinhalte
 * - primary (Helles Blau):       ~8:1  – Akzent / interaktive Elemente
 * - secondary (Warmes Gelb):     ~12:1 – Sekundaerer Akzent
 * - onSurfaceVariant (Hellgrau): ~11:1 – Labels, Beschreibungen
 * - error (Rot):                 ~4.6:1 – Fehlermeldungen
 */
private val WearColorPalette = Colors(
    primary = Color(0xFFAECBFA),
    primaryVariant = Color(0xFF8AB4F8),
    secondary = Color(0xFFFDE293),
    secondaryVariant = Color(0xFFE8DEF8),
    background = Color.Black,
    surface = Color(0xFF303133),
    error = Color(0xFFEE675C),
    onPrimary = Color(0xFF303133),
    onSecondary = Color(0xFF303133),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFDADCE0),
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    /**
     * Theme fuer die GalaxyWatch Health App.
     * Verwendet eine kontrastreiche Farbpalette und die Standard-Wear-OS-Typografie,
     * die fuer kleine runde Displays (1,47 Zoll) optimiert ist.
     */
    MaterialTheme(
        colors = WearColorPalette,
        content = content
    )
}