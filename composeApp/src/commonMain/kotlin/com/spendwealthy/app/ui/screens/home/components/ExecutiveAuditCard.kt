package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
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
import com.spendwealthy.app.domain.service.FinancialAuditReport
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun ExecutiveAuditCard(
    audit: FinancialAuditReport,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = AppColors.CardDark, // High-contrast premium look
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text("Executive Health Audit", fontWeight = FontWeight.Black, color = Color.White, fontSize = 14.sp)
                }
                
                Text(
                    audit.generatedAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${audit.overallHealthScore}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = when {
                            audit.overallHealthScore > 80 -> AppColors.Success
                            audit.overallHealthScore > 50 -> AppColors.Accent
                            else -> AppColors.Error
                        }
                    )
                    Text("Overall Health Score", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${(audit.vulnerabilityIndex * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text("Vulnerability Index", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                }
            }
            
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                audit.keyInsights.take(2).forEach { insight ->
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("•", color = AppColors.Accent, fontWeight = FontWeight.Bold)
                        Text(insight, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Top Recommendation: ", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                    Text(audit.recommendationSummary.replace("Priority: ", ""), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AppColors.Accent)
                }
            }
        }
    }
}
