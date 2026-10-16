package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.MomentumAnalysis
import com.spendwealthy.app.domain.service.MomentumDirection
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun FinancialMomentumCard(
    momentum: MomentumAnalysis,
    modifier: Modifier = Modifier
) {
    val statusColor = when (momentum.direction) {
        MomentumDirection.ACCELERATING -> AppColors.Success
        MomentumDirection.STABLE -> AppColors.Primary
        MomentumDirection.DECELERATING -> AppColors.Error
    }

    val icon = when (momentum.direction) {
        MomentumDirection.ACCELERATING -> Icons.AutoMirrored.Filled.TrendingUp
        MomentumDirection.STABLE -> Icons.AutoMirrored.Filled.TrendingFlat
        MomentumDirection.DECELERATING -> Icons.AutoMirrored.Filled.TrendingDown
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.SurfaceRaised,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Speed, null, tint = statusColor, modifier = Modifier.size(18.dp))
                    }
                    Text("Financial Momentum", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.1f),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(icon, null, tint = statusColor, modifier = Modifier.size(14.dp))
                        Text(
                            momentum.direction.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "${if (momentum.momentumScore >= 0) "+" else ""}${momentum.momentumScore}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = statusColor
                    )
                    Text("Velocity Score", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    null,
                    tint = statusColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Primary Driver", style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
                    Text(
                        momentum.primaryDriver,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextMain,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
