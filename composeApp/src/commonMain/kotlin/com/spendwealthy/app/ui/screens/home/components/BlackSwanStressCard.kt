package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.StressTestResult
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@Composable
fun BlackSwanStressCard(
    result: StressTestResult,
    modifier: Modifier = Modifier
) {
    val statusColor = when {
        result.resilienceScore > 80 -> AppColors.Success
        result.resilienceScore > 50 -> AppColors.Primary
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
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.Error.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Dangerous, null, tint = AppColors.Error, modifier = Modifier.size(18.dp))
                    }
                    Text("Black Swan Simulation", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${result.resilienceScore}% Resilient",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(result.scenario, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "${result.survivalMonths}",
                        style = AppTypography.displayNumeric(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = statusColor
                        )
                    )
                    Text("Months Survival", style = MaterialTheme.typography.titleMedium, color = AppColors.TextMuted, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.Error.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Security, null, tint = AppColors.Error, modifier = Modifier.size(16.dp))
                    Column {
                        Text("Est. Principal Loss", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                        Text(
                            text = result.principalLoss.formatWithSymbol(),
                            style = AppTypography.bodyNumeric(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.Error
                            )
                        )
                    }
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.Primary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    result.recoveryPlan,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
