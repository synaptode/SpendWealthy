package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun EmptyState(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.Inbox,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Bouncy scale
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Playful hover (Float)
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            // Wiggling Ears
            Row(
                modifier = Modifier.width(140.dp).offset(y = (-40).dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedCatEar(color = AppColors.Primary.copy(alpha = 0.2f))
                AnimatedCatEar(color = AppColors.Primary.copy(alpha = 0.2f))
            }
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.Primary.copy(alpha = 0.15f),
                modifier = Modifier
                    .size(130.dp)
                    .graphicsLayer { 
                        translationY = floatY
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        CatBubble(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextMuted,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black
        )
    }
}
