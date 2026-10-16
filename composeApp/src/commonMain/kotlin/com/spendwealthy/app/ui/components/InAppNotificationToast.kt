package com.spendwealthy.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.spendwealthy.app.domain.service.AppNotification
import com.spendwealthy.app.domain.service.NotificationType
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun InAppNotificationToast(
    notification: AppNotification?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 50.dp) // Below status bar
    ) {
        notification?.let { 
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = AppColors.Surface,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val icon = when (it.type) {
                        NotificationType.SUCCESS -> Icons.Default.CheckCircle
                        NotificationType.WARNING -> Icons.Default.Warning
                        NotificationType.ERROR -> Icons.Default.Warning
                        NotificationType.FAMILY -> Icons.Default.Groups
                        NotificationType.INFO -> Icons.Default.Info
                    }
                    val iconColor = when (it.type) {
                        NotificationType.SUCCESS -> AppColors.Success
                        NotificationType.WARNING -> Color(0xFFF59E0B)
                        NotificationType.ERROR -> Color(0xFFEF4444)
                        NotificationType.FAMILY -> AppColors.Accent
                        NotificationType.INFO -> AppColors.Primary
                    }

                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(iconColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
                    }

                    Column(Modifier.weight(1f)) {
                        Text(it.title, fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                        Text(it.message, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                    }
                }
            }
        }
    }
}
