package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun CatLoader(
    modifier: Modifier = Modifier,
    color: Color = AppColors.Primary
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Staggered paws "walking"
    val paw1Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val paw2Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(paw1Scale)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Pets, null, tint = color, modifier = Modifier.size(20.dp))
        }
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(paw2Scale)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Pets, null, tint = color, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun FullScreenCatLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BackgroundSoftBlue.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CatLoader()
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.Text(
                text = "Pouncing on data...",
                color = AppColors.DeepBlueText,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black
            )
        }
    }
}
