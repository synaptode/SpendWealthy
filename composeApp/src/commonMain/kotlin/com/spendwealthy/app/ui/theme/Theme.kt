package com.spendwealthy.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimaryContainer = AppColors.OnPrimaryContainer,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryContainer,
    onSecondaryContainer = AppColors.OnSecondaryContainer,
    background = AppColors.Background,
    onBackground = AppColors.TextMain,
    surface = AppColors.Surface,
    onSurface = AppColors.TextMain,
    surfaceVariant = AppColors.SurfaceMuted,
    onSurfaceVariant = AppColors.TextMuted,
    outline = AppColors.Outline,
    error = AppColors.Danger
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Dark.Primary,
    onPrimary = Color(0xFF10140D),
    primaryContainer = AppColors.Dark.Surface,
    onPrimaryContainer = AppColors.Dark.Text,
    secondary = AppColors.Secondary,
    onSecondary = Color.White,
    background = AppColors.Dark.Background,
    onBackground = AppColors.Dark.Text,
    surface = AppColors.Dark.Surface,
    onSurface = AppColors.Dark.Text,
    surfaceVariant = AppColors.Dark.Card,
    onSurfaceVariant = AppColors.Dark.TextMuted,
    outline = AppColors.Dark.Outline,
    error = AppColors.Danger
)

val LocalDarkTheme = androidx.compose.runtime.compositionLocalOf { false }

@Composable
fun SpendWealthyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    androidx.compose.runtime.CompositionLocalProvider(
        LocalDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getAppTypography(),
            content = content
        )
    }
}
