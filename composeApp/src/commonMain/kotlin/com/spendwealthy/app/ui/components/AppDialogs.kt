package com.spendwealthy.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun CatDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Pounce! 🐾",
    dismissText: String = "Not now 🐈",
    onConfirm: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var isVisible by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0.8f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )

        LaunchedEffect(Unit) {
            isVisible = true
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .scale(scale)
        ) {
            AppCard(
                hasCatEars = true,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (icon != null) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(AppColors.Primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(32.dp))
                        }
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = AppColors.DeepBlueText
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMuted,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, AppColors.BackgroundSoftBlue)
                        ) {
                            Text(dismissText, color = AppColors.TextMuted, fontWeight = FontWeight.Black)
                        }

                        AppButton(
                            text = confirmText,
                            onClick = onConfirm,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
            }
        }
    }
}
