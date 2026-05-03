package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.spendwealthy.app.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatPullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberPullToRefreshState()
    
    PullToRefreshBox(
        modifier = modifier,
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        indicator = {
            val scale by animateFloatAsState(
                targetValue = if (isRefreshing) 1.2f else state.distanceFraction.coerceIn(0f, 1f),
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
            )
            
            val infiniteTransition = rememberInfiniteTransition()
            val wiggle by infiniteTransition.animateFloat(
                initialValue = -15f,
                targetValue = 15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .size(48.dp)
                    .scale(scale)
                    .graphicsLayer { rotationZ = if (isRefreshing) wiggle else 0f },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) {
        content()
    }
}
