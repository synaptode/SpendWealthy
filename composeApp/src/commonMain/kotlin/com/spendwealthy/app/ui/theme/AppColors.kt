package com.spendwealthy.app.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // Ultra-Clean Modern Fintech Luxury Palette (Matching User Reference)
    val Primary = Color(0xFFFFDE43)              // Canary Gold / Warm Yellow Primary
    val PrimaryDark = Color(0xFFE5C420)
    val PrimaryContainer = Color(0xFFFFF9DB)
    val OnPrimary = Color(0xFF111827)
    val OnPrimaryContainer = Color(0xFF111827)

    val Secondary = Color(0xFF111827)            // Pitch Charcoal Black
    val SecondaryContainer = Color(0xFFF3F4F6)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFF111827)

    val Accent = Color(0xFFFFDE43)               // Canary Accent
    val CanaryYellow = Color(0xFFFFDE43)
    val CharcoalBlack = Color(0xFF111827)
    val CloudGray = Color(0xFFF3F4F6)
    val EmeraldGreen = Color(0xFF10B981)

    val Background = Color(0xFFF7F8FA)           // Crisp Minimalist Canvas
    val Surface = Color(0xFFFFFFFF)              // Pure White Card
    val SurfaceMuted = Color(0xFFF1F3F5)         // Soft Neutral Slate
    val SurfaceRaised = Color(0xFFF3F4F6)        // Subtle Pill Gray
    val GlassBackground = Color(0xEEFFFFFF)
    val Glass = Color(0xE6FFFFFF)
    val BorderGlass = Color(0x14000000)          // 8% Black Hairline
    
    val TextMain = Color(0xFF111827)             // Deep Slate Black
    val TextMuted = Color(0xFF6B7280)            // Neutral Cool Gray
    val TextContrast = Color(0xFF000000)
    
    val CardBlue = Color(0xFFFFFFFF)
    val CardDark = Color(0xFF111827)
    
    val Success = Color(0xFF10B981)              // Emerald Green
    val Danger = Color(0xFFEF4444)               // Crimson Red
    val Error = Danger
    val Warning = Color(0xFFF59E0B)              // Warm Amber
    val Outline = Color(0xFFE5E7EB)              // Elegant Hairline Border

    // Gradients
    val PrimaryGradient = listOf(Color(0xFFFFDE43), Color(0xFFFACC15))
    val CardBlueGradient = listOf(Color(0xFFFFFFFF), Color(0xFFF8F9FA))
    val AccentGradient = listOf(Color(0xFFFFDE43), Color(0xFFFFE566))
    val ObsidianGradient = listOf(Color(0xFF111827), Color(0xFF1F2937), Color(0xFF111827))

    // Dark Mode Palette
    object Dark {
        val Background = Color(0xFF090D16)
        val Surface = Color(0xFF0F172A)
        val Card = Color(0xFF1E293B)
        val Text = Color(0xFFF8FAFC)
        val TextMuted = Color(0xFF94A3B8)
        val Primary = Color(0xFFFFDE43)
        val Outline = Color(0xFF1E293B)
        val Glass = Color(0xCC0F172A)
    }

    // Compatibility Aliases
    val BackgroundSoftBlue = Background
    val DeepBlueText = TextMain
    val PrimaryVariant = Primary
    val CardBackground = Surface
}
