package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.OptimizerRecommendation
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun WealthOptimizerCard(
    recommendation: OptimizerRecommendation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.SurfaceRaised,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    AppColors.Primary.copy(alpha = pulseAlpha + 0.3f),
                    AppColors.Accent.copy(alpha = pulseAlpha + 0.1f)
                )
            )
        )
    ) {
        Box {
            // Animated Glow background
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AppColors.Primary.copy(alpha = pulseAlpha),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(0f, 0f)
                        )
                    )
            )
            
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(if (recommendation.id.contains("family")) AppColors.Accent else AppColors.Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (recommendation.id.contains("family")) Icons.Default.Groups else Icons.Default.AutoAwesome,
                                null,
                                tint = if (recommendation.id.contains("family")) AppColors.TextMain else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            if (recommendation.id.contains("family")) "Family Optimizer" else "Wealth Optimizer",
                            fontWeight = FontWeight.Black,
                            color = if (recommendation.id.contains("family")) AppColors.Accent else AppColors.Primary,
                            fontSize = 14.sp
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.Success.copy(alpha = 0.1f)
                        ) {
                            Text(
                                "Impact: +${recommendation.impactScore}%",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.Success,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        IconButton(
                            onClick = onClick,
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(AppColors.SurfaceMuted)
                        ) {
                            Icon(Icons.Default.Insights, null, tint = AppColors.Primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(recommendation.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text(recommendation.explanation, style = MaterialTheme.typography.bodySmall, color = AppColors.TextMuted, lineHeight = 16.sp)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("View Optimization Strategy", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                    Icon(Icons.Default.ChevronRight, null, tint = AppColors.Primary)
                }
            }
        }
    }
}
