package com.spendwealthy.app.ui.theme

import androidx.compose.ui.unit.dp

object AppSpacing {
    val None = 0.dp
    val Tiny = 2.dp
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 24.dp
    val DoubleLarge = 32.dp
    val TripleLarge = 48.dp
    
    // Semantic aliases
    val ScreenPadding = Large
    val CardPadding = Large
    val SectionSpacing = ExtraLarge
    val ComponentSpacing = Medium
}

object AppRadius {
    val None = 0.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 24.dp
    val Full = 999.dp
    
    // Semantic aliases
    val CardRadius = Large
    val ButtonRadius = Medium
    val InputRadius = Medium
}
