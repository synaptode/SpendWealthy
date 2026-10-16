package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.WealthProjection
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun WealthProjectionCard(
    projection: WealthProjection,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = AppColors.SurfaceRaised,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.Accent.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoGraph, null, tint = AppColors.Accent, modifier = Modifier.size(18.dp))
                    }
                    Text("12-Month Forecast", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.Success.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${projection.confidenceScore}% Confidence",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.Success,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // The Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(vertical = 8.dp)
            ) {
                ProjectionLineChart(projection)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Projected Net Worth", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                    Text(projection.projectedNetWorthAt12Months.format(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                }
                
                Text(
                    text = "Based on current savings velocity",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProjectionLineChart(projection: WealthProjection) {
    val points = projection.months
    val maxVal = points.maxOf { it.projectedBalance.amount }.toDouble()
    val minVal = points.minOf { it.projectedBalance.amount }.toDouble()
    val range = (maxVal - minVal).coerceAtLeast(1.0)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val spacing = width / (points.size - 1)

        val path = Path()
        val fillPath = Path()

        points.forEachIndexed { index, point ->
            val x = index * spacing
            val y = height - ((point.projectedBalance.amount - minVal) / range * height).toFloat()
            
            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                // Smoothing would be nice, but simple line for now
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
            
            if (index == points.size - 1) {
                fillPath.lineTo(x, height)
                fillPath.close()
            }
        }

        // Draw Area Fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(AppColors.Accent.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        // Draw Line
        drawPath(
            path = path,
            color = AppColors.Accent,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        
        // Draw End Point
        val endX = (points.size - 1) * spacing
        val endY = height - ((points.last().projectedBalance.amount - minVal) / range * height).toFloat()
        drawCircle(
            color = AppColors.Accent,
            radius = 6.dp.toPx(),
            center = Offset(endX, endY)
        )
        drawCircle(
            color = Color.White,
            radius = 3.dp.toPx(),
            center = Offset(endX, endY)
        )
    }
}
