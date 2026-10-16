package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.FinancialAnomaly
import com.spendwealthy.app.domain.service.Severity
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun FinancialGuardianCard(
    anomalies: List<FinancialAnomaly>,
    modifier: Modifier = Modifier
) {
    val highestSeverity = anomalies.maxByOrNull { it.severity.ordinal }?.severity ?: Severity.LOW
    val baseColor = when (highestSeverity) {
        Severity.CRITICAL, Severity.HIGH -> AppColors.Error
        Severity.MEDIUM -> AppColors.Accent
        Severity.LOW -> AppColors.Primary
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = baseColor.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, baseColor.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(baseColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.GppMaybe, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text("Financial Guardian", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                    Text("${anomalies.size} Attention Items", style = MaterialTheme.typography.labelSmall, color = baseColor)
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                anomalies.forEach { anomaly ->
                    AnomalyItem(anomaly)
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, null, tint = AppColors.TextMuted, modifier = Modifier.size(14.dp))
                    Text(
                        "Suggested: ${anomalies.first().suggestedAction}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMain,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AnomalyItem(anomaly: FinancialAnomaly) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(
            Icons.Default.Warning,
            null,
            tint = when(anomaly.severity) {
                Severity.CRITICAL, Severity.HIGH -> AppColors.Error
                Severity.MEDIUM -> AppColors.Accent
                Severity.LOW -> AppColors.Primary
            },
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Text(
            anomaly.description,
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextMain,
            lineHeight = 16.sp
        )
    }
}
