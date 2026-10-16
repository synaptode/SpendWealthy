package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.LifecycleReport
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun LifetimeTrajectoryCard(
    report: LifecycleReport,
    modifier: Modifier = Modifier
) {
    val statusColor = when {
        report.probabilityOfSuccess > 80 -> AppColors.Success
        report.probabilityOfSuccess > 50 -> AppColors.Primary
        else -> AppColors.Error
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
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Timeline, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                    }
                    Text("Lifetime Wealth Trajectory", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${report.probabilityOfSuccess}% Success",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        report.monthlyRetirementIncome.format(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain
                    )
                    Text("Est. Monthly Retirement Income", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("Age ${report.retirementAge}", fontWeight = FontWeight.Black, color = AppColors.Primary, fontSize = 18.sp)
                    Text("Retirement Target", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.Primary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    report.recommendation,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    lineHeight = 16.sp
                )
            }
            
            LinearProgressIndicator(
                progress = { report.probabilityOfSuccess / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = statusColor,
                trackColor = AppColors.Outline
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Current Assets: Analyzed", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                Text("Trajectory: Sustainable", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
            }
        }
    }
}
