package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun CatBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(modifier = modifier.scale(scale)) {
        // Subtle Ears
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-6).dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp, 12.dp)
                    .clip(GenericShape { size, _ ->
                        moveTo(0f, size.height)
                        quadraticTo(size.width * 0.5f, 0f, size.width, size.height)
                        close()
                    })
                    .background(AppColors.Accent)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f)),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.Accent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💡", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMain,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun AnimatedCatEar(
    modifier: Modifier = Modifier,
    color: Color = AppColors.Primary
) {
    val infiniteTransition = rememberInfiniteTransition()
    val earWiggle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val earShape = GenericShape { size, _ ->
        moveTo(0f, size.height)
        quadraticTo(size.width * 0.5f, -size.height * 0.2f, size.width, size.height)
        close()
    }

    Box(
        modifier = modifier
            .size(40.dp, 30.dp)
            .graphicsLayer { rotationZ = earWiggle }
            .clip(earShape)
            .background(color)
    )
}
