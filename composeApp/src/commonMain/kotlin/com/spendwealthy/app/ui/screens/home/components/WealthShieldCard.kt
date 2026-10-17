package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.WealthShieldStatus
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@Composable
fun WealthShieldCard(
    shield: WealthShieldStatus,
    modifier: Modifier = Modifier
) {
    val statusColor = if (shield.isActive) AppColors.Success else AppColors.Primary

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.SurfaceRaised,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (shield.isActive) AppColors.Success.copy(alpha = 0.3f) else AppColors.Outline)
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
                    Text("Adaptive Wealth Shield", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        if (shield.isActive) "ACTIVE PROTECTION" else "MONITORING",
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
                        "${shield.protectionLevel}%",
                        style = AppTypography.headlineNumeric(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = statusColor
                        )
                    )
                    Text("Defense Integrity", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                if (shield.isActive) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = shield.shieldedAssets.formatWithSymbol(),
                            style = AppTypography.bodyNumeric(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.TextMain
                            )
                        )
                        Text("Shielded Assets", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                    }
                }
            }
            
            shield.triggerReason?.let {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.Error.copy(alpha = 0.05f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        it,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Active Protocols", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    shield.activeProtocols.take(3).forEach { protocol ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusColor.copy(alpha = 0.05f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.1f))
                        ) {
                            Text(
                                protocol,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor
                            )
                        }
                    }
                }
            }
        }
    }
}
