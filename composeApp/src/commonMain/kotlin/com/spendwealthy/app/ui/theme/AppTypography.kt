package com.spendwealthy.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import spendwealthy.composeapp.generated.resources.Res
import spendwealthy.composeapp.generated.resources.plusjakartasans_bold
import spendwealthy.composeapp.generated.resources.plusjakartasans_extrabold
import spendwealthy.composeapp.generated.resources.plusjakartasans_medium
import spendwealthy.composeapp.generated.resources.plusjakartasans_regular
import spendwealthy.composeapp.generated.resources.plusjakartasans_semibold

@Composable
fun getPlusJakartaSansFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.plusjakartasans_regular, FontWeight.Normal),
        Font(Res.font.plusjakartasans_medium, FontWeight.Medium),
        Font(Res.font.plusjakartasans_semibold, FontWeight.SemiBold),
        Font(Res.font.plusjakartasans_bold, FontWeight.Bold),
        Font(Res.font.plusjakartasans_extrabold, FontWeight.ExtraBold)
    )
}

@Composable
fun getAppTypography(): Typography {
    val fontFamily = getPlusJakartaSansFamily()

    return Typography(
        // Large & Display Texts
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = (-0.5).sp
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.3).sp
        ),
        // Titles & Headlines
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.3).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.2).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),
        // Body & Labels
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        )
    )
}

object AppTypography {
    val Typography: Typography
        @Composable
        get() = getAppTypography()

    @Composable
    fun displayNumeric(
        fontWeight: FontWeight = FontWeight.ExtraBold,
        fontSize: androidx.compose.ui.unit.TextUnit = 32.sp,
        color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
    ): TextStyle = TextStyle(
        fontFamily = getPlusJakartaSansFamily(),
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = (fontSize.value * 1.2f).sp,
        letterSpacing = (-0.5).sp,
        color = color
    )

    @Composable
    fun headlineNumeric(
        fontWeight: FontWeight = FontWeight.Bold,
        fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
        color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
    ): TextStyle = TextStyle(
        fontFamily = getPlusJakartaSansFamily(),
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = (fontSize.value * 1.25f).sp,
        letterSpacing = (-0.3).sp,
        color = color
    )

    @Composable
    fun bodyNumeric(
        fontWeight: FontWeight = FontWeight.SemiBold,
        fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
        color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
    ): TextStyle = TextStyle(
        fontFamily = getPlusJakartaSansFamily(),
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = (fontSize.value * 1.35f).sp,
        letterSpacing = 0.sp,
        color = color
    )

    @Composable
    fun labelNumeric(
        fontWeight: FontWeight = FontWeight.Medium,
        fontSize: androidx.compose.ui.unit.TextUnit = 12.sp,
        color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
    ): TextStyle = TextStyle(
        fontFamily = getPlusJakartaSansFamily(),
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = (fontSize.value * 1.3f).sp,
        letterSpacing = 0.sp,
        color = color
    )
}
