package com.spendwealthy.app.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // Premium money-management palette inspired by the supplied mobile reference.
    val Primary = Color(0xFF34784E)
    val PrimaryDark = Color(0xFF102337)
    val PrimaryContainer = Color(0xFFDDEBC9)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFF0E1B15)

    val Secondary = Color(0xFF007E87)
    val SecondaryContainer = Color(0xFFBAE4EA)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFF052226)

    val Accent = Color(0xFFDEFF37)
    val Background = Color(0xFFF7F6EC)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFF1F4E8)
    val SurfaceRaised = Color(0xFFFFFEF4)
    val GlassBackground = Color(0xEEFFFFFF)
    val Glass = Color(0xCCFFFFFF)
    
    val TextMain = Color(0xFF161A23)
    val TextMuted = Color(0xFF858B94)
    val TextContrast = Color(0xFFFFFFFF)
    
    val CardBlue = Color(0xFFAEE0E7)
    val CardDark = Color(0xFF151C2F)
    
    val Success = Color(0xFF22795C)
    val Danger = Color(0xFFD45757)
    val Warning = Color(0xFFF3A53B)
    val Outline = Color(0xFFE5E8DD)

    // Gradients
    val PrimaryGradient = listOf(Primary, Color(0xFF265D40))
    val CardBlueGradient = listOf(CardBlue, Color(0xFF8FCAD4))
    val AccentGradient = listOf(Accent, Color(0xFFC9ED24))

    // Dark Mode Palette
    object Dark {
        val Background = Color(0xFF0E1118)
        val Surface = Color(0xFF171C26)
        val Card = Color(0xFF202838)
        val Text = Color(0xFFFFFFFF)
        val TextMuted = Color(0xFFAAB1BD)
        val Primary = Color(0xFFDEFF37)
        val Outline = Color(0xFF2D3545)
        val Glass = Color(0xCC171C26)
    }

    // Compatibility Aliases
    val BackgroundSoftBlue = Background
    val DeepBlueText = TextMain
    val PrimaryVariant = Primary
    val CardBackground = Surface
}
