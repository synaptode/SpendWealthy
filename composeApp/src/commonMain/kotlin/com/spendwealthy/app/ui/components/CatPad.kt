package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun CatPad(
    onValueChange: (String) -> Unit,
    currentValue: String,
    modifier: Modifier = Modifier
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "DEL")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(AppColors.BackgroundSoftBlue)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { key ->
                    CatPadKey(
                        key = key,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (key) {
                                "DEL" -> {
                                    if (currentValue.isNotEmpty()) {
                                        onValueChange(currentValue.dropLast(1))
                                    }
                                }
                                "." -> {
                                    if (!currentValue.contains(".")) {
                                        onValueChange(currentValue + key)
                                    }
                                }
                                else -> {
                                    if (currentValue.length < 12) {
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
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
    )

    val backgroundColor = when (key) {
        "DEL" -> AppColors.Danger.copy(alpha = 0.1f)
        else -> Color.White
    }
    
    val textColor = when (key) {
        "DEL" -> AppColors.Danger
        else -> AppColors.DeepBlueText
    }

    Box(
        modifier = modifier
            .aspectRatio(1.5f)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
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
                contentDescription = "Delete",
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = key,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )
        }
    }
}
