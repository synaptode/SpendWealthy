package com.spendwealthy.app.ui.screens.simulator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.core.localization.LocalStrings
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FireSimulatorBottomSheet(
    viewModel: FireSimulatorViewModel,
    onDismiss: () -> Unit,
    onGoalCreated: (() -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val strings = LocalStrings.current

    LaunchedEffect(state.goalConvertedSuccess) {
        if (state.goalConvertedSuccess) {
            onGoalCreated?.invoke()
            viewModel.onEvent(FireSimulatorEvent.ResetGoalConvertedStatus)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = AppColors.Surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(scrollState)
                .animateContentSize()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "SUPERPOWER FIRE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Simulasi Bebas Finansial",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.TextMain,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Proyeksi kemandirian finansial & snowball compound interest",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextMuted
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() },
                    shape = CircleShape,
                    color = AppColors.SurfaceRaised,
                    border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = AppColors.TextMain,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val result = state.result
            if (result != null) {
                // 1. Hero Freedom Projection Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E1B4B),
                    border = BorderStroke(1.dp, Color(0xFF4338CA).copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF312E81),
                                        Color(0xFF1E1B4B),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (result.isAlreadyFire) Color(0xFF10B981) else Color(0xFFFFDE43))
                                    )
                                    Text(
                                        text = if (result.isAlreadyFire) "BEBAS FINANSIAL TERCAPAI" else "TARGET USIA BEBAS FINANSIAL",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.8f),
                                        letterSpacing = 0.8.sp
                                    )
                                }

                                if (result.isCoastFire) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "🛡️ Coast FIRE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF34D399),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Usia ${result.projectedFireAge}",
                                    style = AppTypography.headlineNumeric(
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = if (result.isAlreadyFire) "Sekarang" else "(${result.yearsToFire.toInt()} tahun lagi)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFFDE43),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Target Numbers Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Target Aset Bebas",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = Money(result.standardFireTarget, "IDR").formatWithSymbol(),
                                        style = AppTypography.labelNumeric(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Passive Income Bulanan",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = Money(result.monthlyPassiveIncomeAtFire, "IDR").formatWithSymbol() + "/bln",
                                        style = AppTypography.labelNumeric(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF34D399)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Progress Bar
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Progres Aset Terkumpul",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${(result.currentFireProgressPercent * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFDE43)
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { result.currentFireProgressPercent },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape),
                                    color = Color(0xFFFFDE43),
                                    trackColor = Color.White.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Interactive Controls
                Text(
                    text = "Parameter Simulasi Finansial",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AppColors.SurfaceRaised,
                    border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Current Age Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Usia Saat Ini",
                                    fontSize = 13.sp,
                                    color = AppColors.TextMuted
                                )
                                Text(
                                    text = "${state.currentAge} Tahun",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextMain
                                )
                            }
                            Slider(
                                value = state.currentAge.toFloat(),
                                onValueChange = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onEvent(FireSimulatorEvent.CurrentAgeChanged(it.toInt()))
                                },
                                valueRange = 18f..65f,
                                steps = 46,
                                colors = SliderDefaults.colors(
                                    thumbColor = AppColors.Primary,
                                    activeTrackColor = AppColors.Primary
                                )
                            )
                        }

                        // Target Monthly Expenses in Retirement
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Biaya Hidup Bulanan Saat Pensiun",
                                    fontSize = 13.sp,
                                    color = AppColors.TextMuted
                                )
                                Text(
                                    text = Money(state.desiredMonthlyExpense, "IDR").formatWithSymbol(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextMain
                                )
                            }
                            Slider(
                                value = state.desiredMonthlyExpense.toFloat(),
                                onValueChange = {
                                    val rounded = (it / 500_000).toLong() * 500_000L
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onEvent(FireSimulatorEvent.DesiredMonthlyExpenseChanged(rounded))
                                },
                                valueRange = 3_000_000f..30_000_000f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFF59E0B),
                                    activeTrackColor = Color(0xFFF59E0B)
                                )
                            )
                        }

                        // Monthly Savings Contribution
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Setoran Tabungan / Investasi / Bulan",
                                    fontSize = 13.sp,
                                    color = AppColors.TextMuted
                                )
                                Text(
                                    text = Money(state.monthlySavings, "IDR").formatWithSymbol(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                            Slider(
                                value = state.monthlySavings.toFloat(),
                                onValueChange = {
                                    val rounded = (it / 250_000).toLong() * 250_000L
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onEvent(FireSimulatorEvent.MonthlySavingsChanged(rounded))
                                },
                                valueRange = 500_000f..20_000_000f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF10B981),
                                    activeTrackColor = Color(0xFF10B981)
                                )
                            )
                        }

                        // Expected Investment Return Profile
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Ekspektasi Return Investasi Tahunan",
                                fontSize = 13.sp,
                                color = AppColors.TextMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val options = listOf(
                                    Triple(0.05, "5%", "Konservatif"),
                                    Triple(0.08, "8%", "Seimbang"),
                                    Triple(0.12, "12%", "Agresif")
                                )
                                options.forEach { (rate, label, desc) ->
                                    val isSelected = (state.annualReturnRate == rate)
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .bouncyClickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                viewModel.onEvent(FireSimulatorEvent.AnnualReturnRateChanged(rate))
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) Color(0xFF8B5CF6).copy(alpha = 0.2f) else AppColors.Surface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFF8B5CF6) else AppColors.Outline.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isSelected) Color(0xFF7C3AED) else AppColors.TextMain
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 10.sp,
                                                color = if (isSelected) Color(0xFF7C3AED) else AppColors.TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Milestones Spectrum
                Text(
                    text = "Spektrum Target Bebas Finansial",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Lean FIRE
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = AppColors.SurfaceRaised,
                        border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🌱 Lean FIRE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Money(result.leanFireTarget, "IDR").formatWithSymbol(),
                                style = AppTypography.labelNumeric(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            )
                            Text(
                                text = "Kebutuhan dasar",
                                fontSize = 10.sp,
                                color = AppColors.TextMuted
                            )
                        }
                    }

                    // Standard FIRE
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF8B5CF6).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "👑 Standard FIRE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Money(result.standardFireTarget, "IDR").formatWithSymbol(),
                                style = AppTypography.labelNumeric(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED)
                                )
                            )
                            Text(
                                text = "Gaya hidup nyaman",
                                fontSize = 10.sp,
                                color = AppColors.TextMuted
                            )
                        }
                    }

                    // Fat FIRE
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = AppColors.SurfaceRaised,
                        border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "💎 Fat FIRE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Money(result.fatFireTarget, "IDR").formatWithSymbol(),
                                style = AppTypography.labelNumeric(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            )
                            Text(
                                text = "Mewah & traveling",
                                fontSize = 10.sp,
                                color = AppColors.TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. Autonomous Acceleration Insight
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFDE43).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFFFFDE43).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFDE43).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = result.insightMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF92400E),
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Actions: Convert to Goal
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.onEvent(
                            FireSimulatorEvent.ConvertToGoal(
                                targetName = "Kebebasan Finansial (FIRE Usia ${result.projectedFireAge})",
                                amount = result.standardFireTarget
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    enabled = !state.isConvertingGoal
                ) {
                    if (state.isConvertingGoal) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Kunci Sebagai Target Finansial (Goal)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
