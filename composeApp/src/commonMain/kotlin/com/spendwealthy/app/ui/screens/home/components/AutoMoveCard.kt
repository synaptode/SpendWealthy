package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.spendwealthy.app.domain.model.AutoMove
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun AutoMoveCard(
    autoMove: AutoMove,
    modifier: Modifier = Modifier
) {
    AppGlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = AppColors.SurfaceRaised
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppColors.Secondary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoMode,
                    null,
                    tint = AppColors.Secondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(
                    text = autoMove.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Next: ${autoMove.nextRunDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = autoMove.amount.format(),
                    fontWeight = FontWeight.Black,
                    color = AppColors.Primary
                )
                Text(
                    text = autoMove.frequency.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted
                )
            }
        }
    }
}
