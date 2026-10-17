package com.spendwealthy.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors

/**
 * VisualTransformation that inserts Indonesian thousand separators (dots)
 * e.g., "1500000" -> "1.500.000" while keeping original digits clean for state.
 */
class IndonesianThousandVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.filter { it.isDigit() }
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formatted = buildString {
            val length = originalText.length
            for (i in 0 until length) {
                append(originalText[i])
                val remaining = length - 1 - i
                if (remaining > 0 && remaining % 3 == 0) {
                    append('.')
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val clamped = offset.coerceAtMost(originalText.length)
                var trans = 0
                val length = originalText.length
                for (i in 0 until clamped) {
                    trans++
                    val remaining = length - 1 - i
                    if (remaining > 0 && remaining % 3 == 0) {
                        trans++
                    }
                }
                return trans.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val clamped = offset.coerceAtMost(formatted.length)
                var orig = 0
                for (i in 0 until clamped) {
                    if (formatted[i] != '.') {
                        orig++
                    }
                }
                return orig.coerceAtMost(originalText.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

/**
 * Premium Indonesian Fintech Currency Input with:
 * - Live thousand separator formatting (e.g. 1.500.000)
 * - Quick increment pills (+50rb, +100rb, +500rb, +1jt, Clear)
 * - Clear button
 * - Tactile haptic feedback
 */
@Composable
fun AppAmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Nominal",
    currency: String = "Rp",
    showQuickPills: Boolean = true,
    quickPresets: List<Pair<String, Long>> = listOf(
        "+50rb" to 50_000L,
        "+100rb" to 100_000L,
        "+500rb" to 500_000L,
        "+1jt" to 1_000_000L
    )
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val haptic = LocalHapticFeedback.current
    val visualTransformation = remember { IndonesianThousandVisualTransformation() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextField(
            value = value,
            onValueChange = { input: String ->
                val sanitized = input.filter { it.isDigit() }.take(14)
                onValueChange(sanitized)
            },
            label = label,
            placeholder = "0",
            leadingIcon = null,
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (value.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onValueChange("")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus",
                                tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B7280),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Currency Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFDE43).copy(alpha = if (isDark) 0.25f else 0.35f),
                        border = BorderStroke(1.dp, Color(0xFFFFDE43).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = currency,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) Color(0xFFFFDE43) else Color(0xFF854D0E)
                            )
                        )
                    }
                }
            },
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Quick Increment Pills Row
        if (showQuickPills) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickPresets) { (presetLabel, amountToAdd) ->
                    val pillBg = if (isDark) Color(0xFF131A29) else Color(0xFFF3F4F6)
                    val pillBorder = if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
                    val pillText = if (isDark) Color(0xFF10B981) else Color(0xFF059669)

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val current = value.filter { it.isDigit() }.toLongOrNull() ?: 0L
                                val updated = current + amountToAdd
                                onValueChange(updated.toString())
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = pillBg,
                        border = BorderStroke(1.dp, pillBorder)
                    ) {
                        Text(
                            text = presetLabel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = pillText
                            )
                        )
                    }
                }

                if (value.isNotEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onValueChange("")
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFFF43F5E).copy(alpha = 0.16f) else Color(0xFFFFF1F2),
                            border = BorderStroke(
                                1.dp,
                                if (isDark) Color(0xFFF43F5E).copy(alpha = 0.35f) else Color(0xFFFECDD3)
                            )
                        ) {
                            Text(
                                text = "Reset",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFF43F5E) else Color(0xFFE11D48)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
