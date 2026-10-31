package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// StockFlow Base Dark Theme Palette
val DarkBg = Color(0xFF0A0E1A)            // Navy presque noir profond
val DarkSurface = Color(0xFF111827)       // Surface principale glassmorphism
val DarkSurfaceElevated = Color(0xFF1A2234) // Cartes KPI & conteneurs
val DarkSurfaceCard = Color(0xFF161F30)   // Cartes produits & bento grid
val DarkBorder = Color(0xFF26334D)        // Bordures subtiles
val DarkBorderLight = Color(0x335EEAD4)   // Bordures accentuées

val TextPrimary = Color(0xFFF8FAFC)       // Titres & chiffres nets
val TextSecondary = Color(0xFF94A3B8)     // Labels & sous-titres
val TextMuted = Color(0xFF64748B)         // Placeholders & hints

// Semantic Accents
val AmberAlert = Color(0xFFF59E0B)        // Alertes stock bas
val RedDanger = Color(0xFFEF4444)         // Rupture & suppression
val GreenSuccess = Color(0xFF10B981)      // Entrées & succès
val VioletBrand = Color(0xFFA78BFA)       // Dégradé secondaire

// 8 Curated Brand Palettes
data class BrandPalette(
    val name: String,
    val hex: String,
    val color: Color,
    val gradientEnd: Color = VioletBrand
)

val CuratedBrandPalettes = listOf(
    BrandPalette("Électrique Mint", "#5EEAD4", Color(0xFF5EEAD4), Color(0xFFA78BFA)),
    BrandPalette("Violet Cosmique", "#A78BFA", Color(0xFFA78BFA), Color(0xFF818CF8)),
    BrandPalette("Corail Radiant", "#FB7185", Color(0xFFFB7185), Color(0xFFF472B6)),
    BrandPalette("Ciel Azur", "#38BDF8", Color(0xFF38BDF8), Color(0xFF818CF8)),
    BrandPalette("Lime Vif", "#A3E635", Color(0xFFA3E635), Color(0xFF5EEAD4)),
    BrandPalette("Ambre Doré", "#FBBF24", Color(0xFFFBBF24), Color(0xFFFB7185)),
    BrandPalette("Rose Néo", "#F472B6", Color(0xFFF472B6), Color(0xFFA78BFA)),
    BrandPalette("Indigo Royal", "#818CF8", Color(0xFF818CF8), Color(0xFF38BDF8))
)

fun parseHexColor(hex: String, fallback: Color = Color(0xFF5EEAD4)): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color(0xFF000000 or colorInt)
        } else if (cleanHex.length == 8) {
            Color(colorInt)
        } else {
            fallback
        }
    } catch (_: Exception) {
        fallback
    }
}
