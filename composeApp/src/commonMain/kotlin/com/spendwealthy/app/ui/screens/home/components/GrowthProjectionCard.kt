package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun GrowthProjectionCard(
    annualGrowth: Money,
    modifier: Modifier = Modifier
) {
    AppGlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = AppColors.SurfaceRaised
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text("12-Month Projection", fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text("Based on current auto-saves", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
            }
            
            Column {
                Text(
                    text = annualGrowth.format(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = AppColors.Primary
                )
                Text(
                    text = "Potential wealth growth by May 2027",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Progress Bar (Visual decorative element)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(AppColors.Outline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(AppColors.Primary, AppColors.Accent)
                            )
                        )
                )
            }
            
            Text(
                "You're on track to reach your goals 3 months faster!",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.Success,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
