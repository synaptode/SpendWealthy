package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.CatInsight
import com.spendwealthy.app.domain.service.CatMood
import com.spendwealthy.app.domain.service.InsightImpact
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun SpendingInsightsCard(
    insight: CatInsight,
    modifier: Modifier = Modifier
) {
    AppGlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = when (insight.impact) {
            InsightImpact.POSITIVE -> AppColors.Success.copy(alpha = 0.05f)
            InsightImpact.NEGATIVE -> AppColors.Danger.copy(alpha = 0.05f)
            InsightImpact.NEUTRAL -> AppColors.GlassBackground
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CatAvatar(insight.mood)
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = AppColors.TextMain
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = insight.message,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = AppColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun CatAvatar(mood: CatMood) {
    val emoji = when (mood) {
        CatMood.HAPPY -> "😺"
        CatMood.SLEEPY -> "🥱"
        CatMood.GRUMPY -> "😾"
        CatMood.ALERT -> "🙀"
        CatMood.PROUD -> "🦁"
        CatMood.CURIOUS -> "🧐"
        CatMood.NINJA -> "🥷"
    }
    
    val bgColor = when (mood) {
        CatMood.HAPPY -> Color(0xFFFFE082)
        CatMood.SLEEPY -> Color(0xFFB0BEC5)
        CatMood.GRUMPY -> Color(0xFFEF9A9A)
        CatMood.ALERT -> Color(0xFFFFCC80)
        CatMood.PROUD -> AppColors.Accent
        CatMood.CURIOUS -> Color(0xFFCE93D8)
        CatMood.NINJA -> Color(0xFF90CAF9)
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 28.sp)
    }
}
