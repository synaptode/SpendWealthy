package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.WealthBlueprint
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun WealthBlueprintCard(
    blueprint: WealthBlueprint,
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
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountTree, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                    }
                    Text("Wealth Blueprint", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = if (blueprint.adherenceScore > 80) AppColors.Success.copy(alpha = 0.1f) else AppColors.Accent.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${blueprint.adherenceScore}% Adherence",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (blueprint.adherenceScore > 80) AppColors.Success else AppColors.Accent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                blueprint.allocations.forEach { allocation ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(allocation.category, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain, fontWeight = FontWeight.Bold)
                            Text("${(allocation.percentage * 100).toInt()}% Target", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                        }
                        
                        LinearProgressIndicator(
                            progress = { (allocation.currentAmount.amount.toFloat() / allocation.targetAmount.amount.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = AppColors.Primary,
                            trackColor = AppColors.Outline
                        )
                        
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(allocation.currentAmount.format(), style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                            Text("Goal: ${allocation.targetAmount.format()}", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                        }
                    }
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.Primary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Your structural adherence is ${if (blueprint.adherenceScore > 80) "Elite" else "improving"}. Keep following optimizations to reach 100%.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
