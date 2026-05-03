package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.ui.theme.AppColors
import kotlinx.coroutines.launch
import kotlin.math.atan2

data class PieChartData(
    val value: Float,
    val color: Color,
    val label: String
)

@Composable
fun CategoryPieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    val animationProgress = remember { Animatable(0f) }
    var selectedIndex by remember { mutableStateOf(-1) }
    
    LaunchedEffect(data) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Box(
        modifier = modifier
            .size(240.dp)
            .pointerInput(data) {
                detectTapGestures { offset ->
                    val x = offset.x - size.width / 2
                    val y = offset.y - size.height / 2
                    var angle = (atan2(y.toDouble(), x.toDouble()) * 180.0 / kotlin.math.PI).toFloat()
                    if (angle < 0) angle += 360f
                    
                    // Adjust to match chart start (-90deg)
                    angle = (angle + 90f) % 360f
                    
                    var currentAngle = 0f
                    data.forEachIndexed { index, item ->
                        val sweep = (item.value / total) * 360f
                        if (angle >= currentAngle && angle < currentAngle + sweep) {
                            selectedIndex = if (selectedIndex == index) -1 else index
                            return@detectTapGestures
                        }
                        currentAngle += sweep
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            var startAngle = -90f
            
            data.forEachIndexed { index, item ->
                val sweepAngle = (item.value / total) * 360f * animationProgress.value
                val isSelected = selectedIndex == index
                val extraStroke = if (isSelected) 8.dp.toPx() else 0f
                
                // Shadow/Glow
                drawArc(
                    color = item.color.copy(alpha = 0.2f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = (32.dp.toPx() + extraStroke), cap = StrokeCap.Round)
                )

                // Main Segment
                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = (24.dp.toPx() + extraStroke), cap = StrokeCap.Round)
                )
                startAngle += (item.value / total) * 360f
            }
        }
        
        // Living Cat Face Center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                val scale = 1f + (if (selectedIndex != -1) 0.1f else 0f)
                scaleX = scale
                scaleY = scale
            }
        ) {
            val mood = when {
                total > 1000000 -> "😲" // Heavy spending
                total > 500000 -> "🦁"  // Wild spending
                else -> "🐱"            // Calm
            }
            
            Text(
                text = mood,
                fontSize = 42.sp
            )
            
            if (selectedIndex != -1) {
                Text(
                    text = data[selectedIndex].label,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = data[selectedIndex].value.toInt().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = AppColors.DeepBlueText
                )
            } else {
                Text(
                    text = "Spent",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = total.toInt().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = AppColors.DeepBlueText
                )
            }
        }
    }
}
