package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.ResilienceReport
import com.spendwealthy.app.domain.service.ResilienceStatus
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun ResilienceRunwayCard(
    report: ResilienceReport,
    onScenarioChange: (com.spendwealthy.app.domain.service.ResilienceScenario) -> Unit = {},
    activeScenario: com.spendwealthy.app.domain.service.ResilienceScenario = com.spendwealthy.app.domain.service.ResilienceScenario.Standard,
    modifier: Modifier = Modifier
) {
    val statusColor = when (report.status) {
        ResilienceStatus.CRITICAL -> AppColors.Error
        ResilienceStatus.VULNERABLE -> AppColors.Accent
        ResilienceStatus.STABLE -> AppColors.Success
        ResilienceStatus.SECURE -> AppColors.Primary
        ResilienceStatus.INVINCIBLE -> AppColors.CardBlue
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
                        Icon(Icons.Default.Shield, null, tint = statusColor, modifier = Modifier.size(18.dp))
                    }
                    Text("Financial Resilience", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        report.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                    Text("${report.runwayMonths.toInt()} Months", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    Text(if (activeScenario == com.spendwealthy.app.domain.service.ResilienceScenario.Standard) "Current Runway" else "Simulated Runway", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                CircularProgressIndicator(
                    progress = { (report.resilienceScore / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.size(48.dp),
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.1f),
                    strokeWidth = 6.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            // Scenario Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Stress Test:", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted, fontWeight = FontWeight.Bold)
                
                ScenarioChip(
                    label = "Standard",
                    isSelected = activeScenario == com.spendwealthy.app.domain.service.ResilienceScenario.Standard,
                    onClick = { onScenarioChange(com.spendwealthy.app.domain.service.ResilienceScenario.Standard) }
                )
                ScenarioChip(
                    label = "Job Loss",
                    isSelected = activeScenario == com.spendwealthy.app.domain.service.ResilienceScenario.JobLoss,
                    onClick = { onScenarioChange(com.spendwealthy.app.domain.service.ResilienceScenario.JobLoss) }
                )
                ScenarioChip(
                    label = "+20% Cost",
                    isSelected = activeScenario is com.spendwealthy.app.domain.service.ResilienceScenario.ExpenseSurge,
                    onClick = { onScenarioChange(com.spendwealthy.app.domain.service.ResilienceScenario.ExpenseSurge(0.2)) }
                )
            }
            
            HorizontalDivider(color = AppColors.Outline)
            
            Text(
                text = when(report.status) {
                    ResilienceStatus.CRITICAL -> "Urgent: Your liquidity covers less than 1 month of expenses. Recommend reducing non-essential spending immediately."
                    ResilienceStatus.VULNERABLE -> "You have a small buffer. Aim to increase your runway to 3 months for better peace of mind."
                    ResilienceStatus.STABLE -> "Solid progress! You can survive for ${report.runwayMonths.toInt()} months without income. Keep building toward 6 months."
                    ResilienceStatus.SECURE -> "Excellent resilience. You are well-protected against most financial shocks."
                    ResilienceStatus.INVINCIBLE -> "You have over a year of runway. You are in the top 1% of financial security!"
                },
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextMuted,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun ScenarioChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) AppColors.Primary else AppColors.Surface,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else AppColors.TextMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
