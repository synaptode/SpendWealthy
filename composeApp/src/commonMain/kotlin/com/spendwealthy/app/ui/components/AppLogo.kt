package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    showWhiskers: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Wiggling whiskers animation
    val whiskerWiggle by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showWhiskers) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val whiskerColor = AppColors.Primary.copy(alpha = 0.3f)
                val whiskerStroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                
                // Left whiskers
                drawLine(
                    color = whiskerColor,
                    start = Offset(20.dp.toPx(), 60.dp.toPx()),
                    end = Offset(0.dp.toPx(), 50.dp.toPx() + whiskerWiggle.dp.toPx()),
                    strokeWidth = whiskerStroke.width,
                    cap = whiskerStroke.cap
                )
                drawLine(
                    color = whiskerColor,
                    start = Offset(20.dp.toPx(), 65.dp.toPx()),
                    end = Offset(2.dp.toPx(), 65.dp.toPx() + (whiskerWiggle * 0.5f).dp.toPx()),
                    strokeWidth = whiskerStroke.width,
                    cap = whiskerStroke.cap
                )
                
                // Right whiskers
                drawLine(
                    color = whiskerColor,
                    start = Offset(100.dp.toPx(), 60.dp.toPx()),
                    end = Offset(120.dp.toPx(), 50.dp.toPx() - whiskerWiggle.dp.toPx()),
                    strokeWidth = whiskerStroke.width,
                    cap = whiskerStroke.cap
                )
                drawLine(
                    color = whiskerColor,
                    start = Offset(100.dp.toPx(), 65.dp.toPx()),
                    end = Offset(118.dp.toPx(), 65.dp.toPx() - (whiskerWiggle * 0.5f).dp.toPx()),
                    strokeWidth = whiskerStroke.width,
                    cap = whiskerStroke.cap
                )
            }
        }

        // Central Paw with Gradient Glow
        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(1.1f)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AppColors.Primary.copy(alpha = 0.2f), Color.Transparent),
                        center = center,
                        radius = size.minDimension / 1.5f
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = "SpendWealthy Logo",
                tint = AppColors.Primary,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}
