package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
        
        // Donut Center - Minimalist Luxury Typography (Matching Reference 4)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                val scale = 1f + (if (selectedIndex != -1) 0.05f else 0f)
                scaleX = scale
                scaleY = scale
            }
        ) {
            val label = if (selectedIndex != -1) data[selectedIndex].label else "Total Expense"
            val displayValue = if (selectedIndex != -1) data[selectedIndex].value else total
            val percentage = if (total > 0 && selectedIndex != -1) {
                ((data[selectedIndex].value / total) * 100).toInt()
            } else 0

            Text(
                text = label,
                fontSize = 12.sp,
                color = AppColors.TextMuted,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "Rp " + kotlin.math.round(displayValue).toLong().toString().reversed().chunked(3).joinToString(".").reversed(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.TextMain,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            if (selectedIndex != -1) {
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    color = AppColors.Success.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "$percentage% of total",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Success,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else {
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    color = AppColors.Accent.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "42% remaining",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
