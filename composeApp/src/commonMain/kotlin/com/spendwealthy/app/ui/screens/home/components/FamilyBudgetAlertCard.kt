package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.CatInsight
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun FamilyBudgetAlertCard(
    insight: CatInsight,
    modifier: Modifier = Modifier
) {
    AppGlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFEF2F2).copy(alpha = 0.9f),
                Color(0xFFFEE2E2).copy(alpha = 0.9f)
            )
        ),
        padding = 16.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF991B1B)
                )
                Text(
                    text = insight.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFB91C1C).copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
