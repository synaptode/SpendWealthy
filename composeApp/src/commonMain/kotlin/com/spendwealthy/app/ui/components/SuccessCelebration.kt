package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun SuccessCelebration(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val shockwaveScale = remember { Animatable(0f) }
    val shockwaveAlpha = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            shockwaveScale.animateTo(
                targetValue = 1.5f,
                animationSpec = tween(800, easing = LinearOutSlowInEasing)
            )
            shockwaveAlpha.animateTo(0f, tween(800))
        }
        launch {
            alpha.animateTo(1f, tween(1000))
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Emerald Luxury Shockwave
            Canvas(modifier = Modifier.size(240.dp)) {
                drawCircle(
                    color = AppColors.Primary.copy(alpha = shockwaveAlpha.value * 0.3f),
                    radius = (size.minDimension / 2) * shockwaveScale.value,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Radial Glow
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    color = AppColors.Primary.copy(alpha = 0.15f),
                    radius = (size.minDimension / 2) * scale.value
                )
            }
            
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale.value)
            )
        }
        
        Spacer(modifier = Modifier.height(28.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextMain,
            modifier = Modifier.graphicsLayer { this.alpha = alpha.value }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.graphicsLayer { this.alpha = alpha.value }
        )
    }
}
