package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.AutoPilotStatus
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun WealthAutoPilotCard(
    status: AutoPilotStatus,
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
                        Icon(Icons.Default.RocketLaunch, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text("Wealth Auto-Pilot", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                        Text("Autonomous Growth Active", style = MaterialTheme.typography.labelSmall, color = AppColors.Success)
                    }
                }
                
                Switch(
                    checked = status.isActive,
                    onCheckedChange = { },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.Primary,
                        uncheckedThumbColor = AppColors.TextMuted,
                        uncheckedTrackColor = AppColors.Outline
                    )
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(status.totalWealthCreated.format(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    Text("Wealth Created", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("${status.totalOptimizationsExecuted}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    Text("Tasks Handled", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.Primary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = AppColors.Primary, modifier = Modifier.size(14.dp))
                    Text(
                        "Next Sweep: ${status.nextScheduledRun}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
