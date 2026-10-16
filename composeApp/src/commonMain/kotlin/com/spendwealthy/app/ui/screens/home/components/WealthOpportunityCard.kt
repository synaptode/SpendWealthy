package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.WealthOpportunity
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun WealthOpportunityCard(
    opportunities: List<WealthOpportunity>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.Primary.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoGraph, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text("Wealth Opportunity", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                    Text("High Yield Detection", style = MaterialTheme.typography.labelSmall, color = AppColors.Primary)
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                opportunities.forEach { opportunity ->
                    OpportunityItem(opportunity)
                }
            }
        }
    }
}

@Composable
private fun OpportunityItem(opportunity: WealthOpportunity) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(opportunity.title, fontWeight = FontWeight.Black, fontSize = 15.sp, color = AppColors.TextMain)
                    Text("Potential over ${opportunity.impactYears} years", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                Text(opportunity.potentialGain.format(), fontWeight = FontWeight.Black, color = AppColors.Primary, fontSize = 16.sp)
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.Error.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.HourglassEmpty, null, tint = AppColors.Error, modifier = Modifier.size(14.dp))
                    Text(
                        "Cost of waiting 30 days: ${opportunity.costOfWaiting30Days.format()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.Error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Seize Opportunity", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
