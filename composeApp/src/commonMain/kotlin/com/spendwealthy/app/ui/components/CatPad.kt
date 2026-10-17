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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance
import com.spendwealthy.app.ui.theme.AppColors

/**
 * High-performance, tactile Quick Entry Keypad tailored for Indonesian Fintech.
 * Features triple-zero (000), quick increment pills (+10k, +50k, +100k), and haptic feedback.
 * Dynamically adapts to Light and Dark mode appearances.
 */
@Composable
fun QuickEntryKeypad(
    onValueChange: (String) -> Unit,
    currentValue: String,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val haptic = LocalHapticFeedback.current

    val quickPills = listOf(
        "+10k" to 10_000L,
        "+20k" to 20_000L,
        "+50k" to 50_000L,
        "+100k" to 100_000L,
        "+500k" to 500_000L
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
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp, top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Increment Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPills.forEach { (label, amount) ->
                val pillBg = if (isDark) Color(0xFF131A29) else Color(0xFFF3F4F6)
                val pillBorder = if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
                val pillText = if (isDark) Color(0xFF10B981) else Color(0xFF059669)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(pillBg)
                        .border(1.dp, pillBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val current = currentValue.filter { it.isDigit() }.toLongOrNull() ?: 0L
                            val updated = current + amount
                            onValueChange(updated.toString())
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = pillText
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
                    QuickEntryKey(
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

/** Backwards-compatible alias for legacy calls */
@Composable
fun CatPad(
    onValueChange: (String) -> Unit,
    currentValue: String,
    modifier: Modifier = Modifier
) = QuickEntryKeypad(onValueChange, currentValue, modifier)

@Composable
private fun QuickEntryKey(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
    )

    val backgroundColor = when (key) {
        "DEL" -> if (isDark) Color(0xFFF43F5E).copy(alpha = 0.16f) else Color(0xFFFFF1F2)
        "000" -> if (isDark) Color(0xFF10B981).copy(alpha = 0.16f) else Color(0xFFECFDF5)
        else -> if (isDark) Color(0xFF131A29) else Color(0xFFF8F9FA)
    }

    val borderColor = when (key) {
        "DEL" -> if (isDark) Color(0xFFF43F5E).copy(alpha = 0.35f) else Color(0xFFFECDD3)
        "000" -> if (isDark) Color(0xFF10B981).copy(alpha = 0.35f) else Color(0xFFA7F3D0)
        else -> if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
    }

    val textColor = when (key) {
        "DEL" -> if (isDark) Color(0xFFF43F5E) else Color(0xFFE11D48)
        "000" -> if (isDark) Color(0xFF10B981) else Color(0xFF059669)
        else -> if (isDark) Color(0xFFF8FAFC) else Color(0xFF111827)
    }

    Box(
        modifier = modifier
            .aspectRatio(1.6f)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
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
