package com.spendwealthy.app.ui.screens.insight

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.ui.components.*
import com.spendwealthy.app.ui.theme.AppColors
import kotlinx.coroutines.delay

class PrideStatusScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()
        
        Box(modifier = Modifier.fillMaxSize().background(AppColors.BackgroundSoftBlue)) {
            // Grid Background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSize = 32.dp.toPx()
                val color = AppColors.Primary.copy(alpha = 0.1f)
                for (x in 0..size.width.toInt() step gridSize.toInt()) {
                    drawLine(color, androidx.compose.ui.geometry.Offset(x.toFloat(), 0f), androidx.compose.ui.geometry.Offset(x.toFloat(), size.height))
                }
                for (y in 0..size.height.toInt() step gridSize.toInt()) {
                    drawLine(color, androidx.compose.ui.geometry.Offset(0f, y.toFloat()), androidx.compose.ui.geometry.Offset(size.width, y.toFloat()))
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                AppTopBar(
                    title = "Pride Status 🦁",
                    subtitle = "Measuring your financial dominance",
                    actions = {
                        IconButton(
                            onClick = { navigator.pop() },
                            modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        // The Health Meter (Gauge)
                        PrideHealthGauge(score = 85f)

                        // Insights Section
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Pride Secrets 🐾",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = AppColors.DeepBlueText
                            )
                            
                            InsightCard(
                                title = "Hunting Streak!",
                                message = "You've stayed under budget for 5 days. Your pantry is growing! 🥩",
                                icon = Icons.Default.FlashOn,
                                color = AppColors.Success
                            )
                            
                            InsightCard(
                                title = "Coffee Pounce Detected",
                                message = "You pounced on coffee 4 times this week. Maybe catch a smaller fish next time? ☕️",
                                icon = Icons.Default.Coffee,
                                color = AppColors.Warning
                            )
                            
                            InsightCard(
                                title = "Savings Forecast",
                                message = "If you keep this pace, you'll reach your 'Cat Tree' goal in 12 days! 🏰",
                                icon = Icons.Default.Timeline,
                                color = AppColors.Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun PrideHealthGauge(score: Float) {
        val animationProgress = remember { Animatable(0f) }
        LaunchedEffect(score) {
            animationProgress.animateTo(
                targetValue = score / 100f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
            Canvas(modifier = Modifier.size(200.dp)) {
                // Background Track
                drawArc(
                    color = AppColors.BackgroundSoftBlue,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Progress Gauge
                drawArc(
                    brush = Brush.sweepGradient(
                        0f to AppColors.Danger,
                        0.5f to AppColors.Warning,
                        1f to AppColors.Success
                    ),
                    startAngle = 135f,
                    sweepAngle = 270f * animationProgress.value,
                    useCenter = false,
                    style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(animationProgress.value * 100).toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = AppColors.DeepBlueText
                )
                Text(
                    text = "Alpha Pride",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = AppColors.Primary
                )
            }
        }
    }

    @Composable
    private fun InsightCard(title: String, message: String, icon: ImageVector, color: Color) {
        AppCard(hasCatEars = true) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Black, color = AppColors.DeepBlueText)
                    Text(message, style = MaterialTheme.typography.bodySmall, color = AppColors.TextMuted, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
