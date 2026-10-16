package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.FinancialHealthTelemetry
import com.spendwealthy.app.domain.service.HealthTrend
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.core.util.format

@Composable
fun FinancialPerformanceCard(
    telemetry: FinancialHealthTelemetry,
    modifier: Modifier = Modifier
) {
    val trendIcon = when (telemetry.healthTrend) {
        HealthTrend.IMPROVING -> Icons.AutoMirrored.Filled.TrendingUp
        HealthTrend.DECLINING -> Icons.AutoMirrored.Filled.TrendingDown
        HealthTrend.STABLE -> Icons.AutoMirrored.Filled.TrendingFlat
    }
    
    val trendColor = when (telemetry.healthTrend) {
        HealthTrend.IMPROVING -> AppColors.Success
        HealthTrend.DECLINING -> AppColors.Error
        HealthTrend.STABLE -> AppColors.Primary
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
                        Icon(Icons.Default.Assessment, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                    }
                    Text("Financial Performance", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(trendIcon, null, tint = trendColor, modifier = Modifier.size(16.dp))
                    Text(
                        telemetry.healthTrend.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = trendColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PerformanceMetric(
                    label = "Savings Rate",
                    value = "${(telemetry.savingsRate * 100).toInt()}%",
                    color = AppColors.Primary
                )
                
                VerticalDivider(modifier = Modifier.height(40.dp).padding(horizontal = 16.dp), color = AppColors.Outline)
                
                PerformanceMetric(
                    label = "Efficiency",
                    value = "${telemetry.optimizationEfficiency}%",
                    color = AppColors.Accent
                )
                
                VerticalDivider(modifier = Modifier.height(40.dp).padding(horizontal = 16.dp), color = AppColors.Outline)

                PerformanceMetric(
                    label = "LCR",
                    value = "x${telemetry.liquidityCoverageRatio.format(1)}",
                    color = AppColors.Success
                )
            }
            
            Text(
                text = when(telemetry.healthTrend) {
                    HealthTrend.IMPROVING -> "Great job! Your savings rate and optimization efficiency are trending upward. You're building wealth faster than average."
                    HealthTrend.DECLINING -> "Heads up: your expenses are rising relative to income. Consider reviewing your active optimizations."
                    HealthTrend.STABLE -> "You're maintaining a steady financial pace. To improve, try executing one more optimization this month."
                },
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextMuted,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun PerformanceMetric(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
    }
}
