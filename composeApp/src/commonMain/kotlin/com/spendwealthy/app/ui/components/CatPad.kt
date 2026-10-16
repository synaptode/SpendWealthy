package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors

/**
 * High-performance, tactile Quick Entry Keypad tailored for Indonesian Fintech.
 * Features triple-zero (000), quick increment pills (+10k, +50k, +100k), and haptic feedback.
 */
@Composable
fun CatPad(
    onValueChange: (String) -> Unit,
    currentValue: String,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val quickPills = listOf(
        "+10k" to 10_000L,
        "+20k" to 20_000L,
        "+50k" to 50_000L,
        "+100k" to 100_000L
    )

    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("000", "0", "DEL")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(AppColors.BackgroundSoftBlue)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Increment Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPills.forEach { (label, amount) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, AppColors.Outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val current = currentValue.filter { it.isDigit() }.toLongOrNull() ?: 0L
                            val updated = current + amount
                            onValueChange(updated.toString())
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                }
            }
        }

        // Numeric Keypad Grid
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { key ->
                    CatPadKey(
                        key = key,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(
                                if (key == "DEL") HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove
                            )
                            when (key) {
                                "DEL" -> {
                                    if (currentValue.isNotEmpty()) {
                                        onValueChange(currentValue.dropLast(1))
                                    }
                                }
                                "000" -> {
                                    if (currentValue.isNotEmpty() && currentValue != "0" && currentValue.length <= 11) {
                                        onValueChange(currentValue + "000")
                                    }
                                }
                                else -> {
                                    if (currentValue == "0") {
                                        onValueChange(key)
                                    } else if (currentValue.length < 14) {
                                        onValueChange(currentValue + key)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CatPadKey(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
    )

    val backgroundColor = when (key) {
        "DEL" -> AppColors.Danger.copy(alpha = 0.12f)
        "000" -> AppColors.PrimaryContainer.copy(alpha = 0.45f)
        else -> Color.White
    }

    val textColor = when (key) {
        "DEL" -> AppColors.Danger
        "000" -> AppColors.Primary
        else -> AppColors.DeepBlueText
    }

    Box(
        modifier = modifier
            .aspectRatio(1.6f)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (key == "DEL") {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Hapus",
                tint = textColor,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                text = key,
                fontSize = if (key == "000") 20.sp else 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}
