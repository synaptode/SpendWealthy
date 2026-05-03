package com.spendwealthy.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    avatarId: Int = 0,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val avatars = listOf("🐈", "🦁", "🐯", "🐱")
    val avatar = avatars.getOrElse(avatarId) { "🐈" }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(AppColors.PrimaryDark)
    ) {
        // Cat Ear Geometry Background
        Canvas(modifier = Modifier.matchParentSize()) {
            val earWidth = 40.dp.toPx()
            val earHeight = 20.dp.toPx()
            
            val path = Path().apply {
                // Left Ear
                moveTo(24.dp.toPx(), 0f)
                lineTo(24.dp.toPx() + earWidth / 2, -earHeight)
                lineTo(24.dp.toPx() + earWidth, 0f)
                
                // Right Ear
                moveTo(size.width - 24.dp.toPx() - earWidth, 0f)
                lineTo(size.width - 24.dp.toPx() - earWidth / 2, -earHeight)
                lineTo(size.width - 24.dp.toPx(), 0f)
            }
            drawPath(path, AppColors.PrimaryDark)
        }

        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Animated Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(avatar, fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 26.sp
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    actions()
                }
            }
        }
    }
}
