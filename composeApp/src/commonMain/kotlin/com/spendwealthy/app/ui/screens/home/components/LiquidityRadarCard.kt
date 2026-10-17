package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.LiquidityReport
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@Composable
fun LiquidityRadarCard(
    report: LiquidityReport,
    modifier: Modifier = Modifier
) {
    val statusColor = when (report.riskLevel) {
        "Fortress Liquidity" -> AppColors.Success
        "Stable" -> AppColors.Primary
        "Caution" -> AppColors.Accent
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
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Radar, null, tint = statusColor, modifier = Modifier.size(18.dp))
                    }
                    Text("Liquidity Radar", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        report.riskLevel.uppercase(),
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
                        "${report.safetyScore}%",
                        style = AppTypography.headlineNumeric(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = statusColor
                        )
                    )
                    Text("Safety Score", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = report.currentBuffer.formatWithSymbol(),
                        style = AppTypography.bodyNumeric(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = AppColors.TextMain
                        )
                    )
                    Text("Available Buffer", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
            }
            
            LinearProgressIndicator(
                progress = { report.safetyScore / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = statusColor,
                trackColor = AppColors.Outline
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Required: ${report.requiredBuffer.formatWithSymbol()}", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                report.criticalDate?.let {
                    Text("Gap detected: $it", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AppColors.Error)
                }
            }
        }
    }
}
