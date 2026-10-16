package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.EstateReport
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun GenerationalLegacyCard(
    report: EstateReport,
    modifier: Modifier = Modifier
) {
    val statusColor = when (report.legacyPillarStatus) {
        "Sovereign Legacy" -> AppColors.Success
        "Developing Legacy" -> AppColors.Primary
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
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.Accent.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalance, null, tint = AppColors.Accent, modifier = Modifier.size(18.dp))
                    }
                    Text("Generational Legacy", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        report.legacyPillarStatus.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegacyMetric("Transfer Eff.", "${report.wealthTransferEfficiency}%", statusColor)
                VerticalDivider(modifier = Modifier.height(30.dp), color = AppColors.Outline)
                LegacyMetric("Tax Shield", "${100 - report.taxExposureRisk}%", AppColors.Primary)
                VerticalDivider(modifier = Modifier.height(30.dp), color = AppColors.Outline)
                LegacyMetric("Coverage", "${report.beneficiaryCoverage}%", AppColors.Accent)
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Preservation Recommendations", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted, fontWeight = FontWeight.Bold)
                report.recommendations.forEach { rec ->
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AutoAwesome, null, tint = AppColors.Accent, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                        Text(rec, style = MaterialTheme.typography.bodySmall, color = AppColors.TextMuted, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LegacyMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, color = color, fontSize = 16.sp)
        Text(label, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
    }
}
