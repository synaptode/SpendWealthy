package com.spendwealthy.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val strokeWidth = diameter * 0.22f
        val radius = (diameter - strokeWidth) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val rect = androidx.compose.ui.geometry.Rect(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )

        // 4-Color Google Ring Arcs
        // Blue (Right / Crossbar side)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        // Green (Bottom side)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        // Yellow (Left side)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        // Red (Top side)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Blue Horizontal Inward Crossbar for the "G"
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(center.x, center.y),
            end = Offset(center.x + radius + (strokeWidth / 2f), center.y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Square
        )
    }
}

@Composable
fun AppleLogoIcon(
    modifier: Modifier = Modifier.size(20.dp),
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Apple body and leaf silhouette path
        val path = Path().apply {
            // Leaf at top right
            moveTo(w * 0.52f, h * 0.18f)
            cubicTo(w * 0.52f, h * 0.08f, w * 0.60f, h * 0.02f, w * 0.68f, h * 0.02f)
            cubicTo(w * 0.68f, h * 0.12f, w * 0.60f, h * 0.18f, w * 0.52f, h * 0.18f)
            close()

            // Main Apple body
            moveTo(w * 0.74f, h * 0.56f)
            // Right bite cutout curve
            cubicTo(w * 0.74f, h * 0.44f, w * 0.84f, h * 0.38f, w * 0.85f, h * 0.37f)
            cubicTo(w * 0.79f, h * 0.28f, w * 0.70f, h * 0.27f, w * 0.67f, h * 0.27f)
            cubicTo(w * 0.59f, h * 0.26f, w * 0.51f, h * 0.31f, w * 0.47f, h * 0.31f)
            cubicTo(w * 0.43f, h * 0.31f, w * 0.36f, h * 0.26f, w * 0.29f, h * 0.26f)
            cubicTo(w * 0.19f, h * 0.26f, w * 0.10f, h * 0.32f, w * 0.05f, h * 0.41f)
            cubicTo(w * -0.05f, h * 0.58f, w * 0.02f, h * 0.83f, w * 0.12f, h * 0.97f)
            cubicTo(w * 0.17f, h * 1.04f, w * 0.23f, h * 1.11f, w * 0.30f, h * 1.11f)
            cubicTo(w * 0.37f, h * 1.11f, w * 0.40f, h * 1.07f, w * 0.48f, h * 1.07f)
            cubicTo(w * 0.56f, h * 1.07f, w * 0.58f, h * 1.11f, w * 0.66f, h * 1.11f)
            cubicTo(w * 0.74f, h * 1.11f, w * 0.78f, h * 1.04f, w * 0.83f, h * 0.97f)
            cubicTo(w * 0.89f, h * 0.89f, w * 0.91f, h * 0.81f, w * 0.92f, h * 0.80f)
            cubicTo(w * 0.91f, h * 0.80f, w * 0.74f, h * 0.73f, w * 0.74f, h * 0.56f)
            close()
        }

        drawPath(path = path, color = tint)
    }
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
