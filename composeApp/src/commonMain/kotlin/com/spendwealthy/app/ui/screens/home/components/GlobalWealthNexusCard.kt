package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.PortfolioAnalysis
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.core.util.format

@Composable
fun GlobalWealthNexusCard(
    analysis: PortfolioAnalysis,
    modifier: Modifier = Modifier
) {
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
                        Icon(Icons.Default.PieChart, null, tint = AppColors.Accent, modifier = Modifier.size(18.dp))
                    }
                    Text("Global Wealth Nexus", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = AppColors.Accent.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${analysis.diversificationScore}% Diverse",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.Accent,
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
                        analysis.totalValue.format(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain
                    )
                    Text("Consolidated Portfolio Value", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("${(analysis.weightedYield * 100).format(1)}%", fontWeight = FontWeight.Black, color = AppColors.Success, fontSize = 18.sp)
                    Text("Avg Yield", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
            }
            
            if (analysis.rebalanceActions.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.Accent.copy(alpha = 0.05f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Rebalance Recommendations", style = MaterialTheme.typography.labelSmall, color = AppColors.Accent, fontWeight = FontWeight.Bold)
                        analysis.rebalanceActions.forEach { action ->
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("•", color = AppColors.Accent, fontWeight = FontWeight.Bold)
                                Text(action, style = MaterialTheme.typography.bodySmall, color = AppColors.TextMuted, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
            
            LinearProgressIndicator(
                progress = { analysis.diversificationScore / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = AppColors.Accent,
                trackColor = AppColors.Outline
            )
        }
    }
}
