package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.SimulationComparison
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@Composable
fun WealthTimeTravelCard(
    comparison: SimulationComparison,
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
                        Icon(Icons.Default.History, null, tint = AppColors.Accent, modifier = Modifier.size(18.dp))
                    }
                    Text("Retrospective Insight", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = AppColors.Accent.copy(alpha = 0.1f),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        "Last 90 Days",
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
                        text = comparison.wealthGap.formatWithSymbol(),
                        style = AppTypography.headlineNumeric(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = AppColors.Accent
                        )
                    )
                    Text("Missed Wealth Potential", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                Icon(Icons.AutoMirrored.Filled.ShowChart, null, tint = AppColors.Accent.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.Surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    comparison.keyDifferenceReason,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    lineHeight = 16.sp
                )
            }
            
            LinearProgressIndicator(
                progress = { comparison.actualNetWorth.amount.toFloat() / comparison.simulatedNetWorth.amount.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = AppColors.Accent,
                trackColor = AppColors.Outline
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Actual: ${comparison.actualNetWorth.formatWithSymbol()}", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                Text("Optimized: ${comparison.simulatedNetWorth.formatWithSymbol()}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
            }
        }
    }
}
