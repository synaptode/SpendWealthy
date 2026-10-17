package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
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
import com.spendwealthy.app.domain.service.WealthNarrative
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@Composable
fun WealthNarrativeBanner(
    narrative: WealthNarrative,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = AppColors.SurfaceRaised,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(AppColors.Primary.copy(alpha = 0.5f), Color.Transparent)
            )
        )
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
                }
                Text(narrative.headline, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
            }
            
            Text(
                narrative.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextMuted,
                lineHeight = 22.sp
            )
            
            HorizontalDivider(color = AppColors.Outline)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Flag, null, tint = AppColors.Accent, modifier = Modifier.size(18.dp))
                Column {
                    Text("Next Big Milestone", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                    Text(narrative.nextBigMilestone, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                }
            }
            
            if (narrative.shadowImpact.amount > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.Success.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Potential Wealth Gain: ", style = MaterialTheme.typography.labelMedium, color = AppColors.Success)
                        Text(
                            "+${narrative.shadowImpact.formatWithSymbol()}",
                            style = AppTypography.labelNumeric(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.Success
                            )
                        )
                    }
                }
            }
        }
    }
}
