package com.spendwealthy.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors

import com.spendwealthy.app.ui.components.BrandLogo
import com.spendwealthy.app.ui.components.KnownBrand

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier.size(20.dp)) {
    BrandLogo(brand = KnownBrand.GOOGLE, modifier = modifier)
}

@Composable
fun AppleLogoIcon(
    modifier: Modifier = Modifier.size(20.dp),
    tint: Color = Color.White
) {
    BrandLogo(brand = KnownBrand.APPLE, modifier = modifier, tint = tint)
}

@Composable
fun GoogleSsoButton(
    text: String = "Lanjutkan dengan Google",
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = !isLoading) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = RoundedCornerShape(14.dp),
        color = AppColors.Surface,
        border = BorderStroke(1.dp, AppColors.Outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = AppColors.Primary,
                    strokeWidth = 2.dp
                )
            } else {
                GoogleLogoIcon(modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AppleSsoButton(
    text: String = "Lanjutkan dengan Apple",
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = !isLoading) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                AppleLogoIcon(modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SocialAuthDivider(
    label: String = "atau lanjutkan dengan",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = AppColors.Outline)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.TextMuted,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = AppColors.Outline)
    }
}
