package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.ResilienceReport
import com.spendwealthy.app.domain.service.ResilienceScenario
import com.spendwealthy.app.domain.service.ResilienceStatus
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun ResilienceRunwayCard(
    report: ResilienceReport,
    activeScenario: ResilienceScenario = ResilienceScenario.Standard,
    onScenarioChange: (ResilienceScenario) -> Unit = {},
    onOpenDetails: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val (statusColor, statusTitle) = when (report.status) {
        ResilienceStatus.CRITICAL -> Color(0xFFEF4444) to "Kritis"
        ResilienceStatus.VULNERABLE -> Color(0xFFF59E0B) to "Rentan"
        ResilienceStatus.STABLE -> Color(0xFF3B82F6) to "Stabil"
        ResilienceStatus.SECURE -> Color(0xFF10B981) to "Sangat Aman"
        ResilienceStatus.INVINCIBLE -> Color(0xFF8B5CF6) to "Invincible"
    }

    val animatedStatusColor by animateColorAsState(targetValue = statusColor, label = "StatusColorAnim")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .bouncyClickable(
                pressedScale = 0.985f,
                hapticType = HapticFeedbackType.LongPress
            ) {
                onOpenDetails()
            },
        shape = RoundedCornerShape(24.dp),
        color = AppColors.Surface,
        border = BorderStroke(1.dp, AppColors.Outline)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(animatedStatusColor.copy(alpha = 0.25f), animatedStatusColor.copy(alpha = 0.1f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = animatedStatusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Runway & Daya Tahan Krisis",
                            fontWeight = FontWeight.Black,
                            color = AppColors.TextMain,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Ketahanan Finansial Tanpa Pemasukan",
                            fontSize = 11.sp,
                            color = AppColors.TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = animatedStatusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, animatedStatusColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = statusTitle,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = animatedStatusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Metric & Gauge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "%.1f".format(report.runwayMonths),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "Bulan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMuted,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = if (activeScenario == ResilienceScenario.Standard) "Kondisi Normal" else "Simulasi Skenario",
                        fontSize = 11.sp,
                        color = AppColors.TextMuted
                    )
                }

                CircularProgressIndicator(
                    progress = { (report.resilienceScore / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.size(48.dp),
                    color = animatedStatusColor,
                    trackColor = animatedStatusColor.copy(alpha = 0.12f),
                    strokeWidth = 6.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            // Interactive Scenario Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Uji Skenario Krisis Nyata:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMuted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScenarioChip(
                        label = "🛡️ Normal",
                        isSelected = activeScenario == ResilienceScenario.Standard,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onScenarioChange(ResilienceScenario.Standard)
                        }
                    )
                    ScenarioChip(
                        label = "💼 Hilang Kerja",
                        isSelected = activeScenario == ResilienceScenario.JobLoss,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onScenarioChange(ResilienceScenario.JobLoss)
                        }
                    )
                    ScenarioChip(
                        label = "📈 Biaya +20%",
                        isSelected = activeScenario is ResilienceScenario.ExpenseSurge,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onScenarioChange(ResilienceScenario.ExpenseSurge(0.2))
                        }
                    )
                    ScenarioChip(
                        label = "🏥 Medis 10Jt",
                        isSelected = activeScenario is ResilienceScenario.Emergency,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onScenarioChange(ResilienceScenario.Emergency(10_000_000L))
                        }
                    )
                }
            }

            HorizontalDivider(color = AppColors.Outline)

            // Footer Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDetails() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Buka Stress Test Black Swan & Wealth Shield",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ScenarioChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .bouncyClickable(
                pressedScale = 0.92f,
                hapticType = HapticFeedbackType.TextHandleMove
            ) {
                onClick()
            },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
        border = if (isSelected) null else BorderStroke(1.dp, AppColors.Outline)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 11.sp,
            color = if (isSelected) Color.White else AppColors.TextMain,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
        )
    }
}
