package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.FinancialChallenge
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun DailyChallengeCard(
    challenge: FinancialChallenge,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.Secondary.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Secondary.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.Secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lightbulb, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text("Daily Wealth Drill", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = CircleShape,
                    color = AppColors.Secondary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, null, tint = AppColors.Secondary, modifier = Modifier.size(14.dp))
                        Text(
                            "+${challenge.rewardPoints} pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(challenge.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text(challenge.description, style = MaterialTheme.typography.bodySmall, color = AppColors.TextMuted, lineHeight = 16.sp)
            }
            
            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Secondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Accept Challenge", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
