package com.spendwealthy.app.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // Titanium Obsidian Quiet-Luxury Fintech Palette
    val Primary = Color(0xFF10B981)              // Vibrant Emerald
    val PrimaryDark = Color(0xFF064E3B)          // Deep Emerald Forest
    val PrimaryContainer = Color(0xFF064E3B)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFF6EE7B7)

    val Secondary = Color(0xFF38BDF8)            // Sky Accent
    val SecondaryContainer = Color(0xFF0369A1)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFFBAE6FD)

    val Accent = Color(0xFFDEFF37)               // Electric Lime (Safe-to-Spend highlight)
    val Background = Color(0xFF090D16)           // Deep Obsidian
    val Surface = Color(0xFF0F172A)              // Titanium Slate
    val SurfaceMuted = Color(0xFF1E293B)         // Card Slate
    val SurfaceRaised = Color(0xFF1E293B)
    val GlassBackground = Color(0xEE0F172A)
    val Glass = Color(0xCC0F172A)
    val BorderGlass = Color(0x1FFFFFFF)          // 12% White
    
    val TextMain = Color(0xFFF8FAFC)             // Crisp Platinum
    val TextMuted = Color(0xFF94A3B8)            // Slate Muted
    val TextContrast = Color(0xFFFFFFFF)
    
    val CardBlue = Color(0xFF1E293B)
    val CardDark = Color(0xFF0F172A)
    
    val Success = Color(0xFF10B981)              // Emerald
    val Danger = Color(0xFFF43F5E)               // Coral Rose
    val Error = Danger
    val Warning = Color(0xFFF59E0B)              // Amber
    val Outline = Color(0xFF1E293B)              // Subtle Card Border

    // Gradients
    val PrimaryGradient = listOf(Primary, Color(0xFF059669))
    val CardBlueGradient = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    val AccentGradient = listOf(Accent, Color(0xFFC9ED24))
    val ObsidianGradient = listOf(Color(0xFF090D16), Color(0xFF0F172A), Color(0xFF090D16))

    // Dark Mode Palette
    object Dark {
        val Background = Color(0xFF090D16)
        val Surface = Color(0xFF0F172A)
        val Card = Color(0xFF1E293B)
        val Text = Color(0xFFF8FAFC)
        val TextMuted = Color(0xFF94A3B8)
        val Primary = Color(0xFF10B981)
        val Outline = Color(0xFF1E293B)
        val Glass = Color(0xCC0F172A)
    }

    // Compatibility Aliases
    val BackgroundSoftBlue = Background
    val DeepBlueText = TextMain
    val PrimaryVariant = Primary
    val CardBackground = Surface
}
