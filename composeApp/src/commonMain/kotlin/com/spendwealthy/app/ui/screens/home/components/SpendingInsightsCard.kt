package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun SpendingInsightsCard(
    insight: CatInsight,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExecutiveInsightIcon(insight.mood)
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = insight.message,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = AppColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun ExecutiveInsightIcon(mood: CatMood) {
    val (icon, tintColor, bgColor) = when (mood) {
        CatMood.HAPPY, CatMood.PROUD -> Triple(
            Icons.AutoMirrored.Filled.TrendingUp,
            Color(0xFF10B981),
            Color(0xFF10B981).copy(alpha = 0.15f)
        )
        CatMood.ALERT, CatMood.GRUMPY -> Triple(
            Icons.Default.Warning,
            Color(0xFFF43F5E),
            Color(0xFFF43F5E).copy(alpha = 0.15f)
        )
        CatMood.CURIOUS -> Triple(
            Icons.Default.Insights,
            Color(0xFF38BDF8),
            Color(0xFF38BDF8).copy(alpha = 0.15f)
        )
        else -> Triple(
            Icons.Default.AutoAwesome,
            Color(0xFFDEFF37),
            Color(0xFFDEFF37).copy(alpha = 0.15f)
        )
    }

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, tintColor.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(22.dp)
        )
    }
}
