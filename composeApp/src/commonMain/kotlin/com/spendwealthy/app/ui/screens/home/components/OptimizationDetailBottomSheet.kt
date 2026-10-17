package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.core.localization.LocalStrings
import com.spendwealthy.app.domain.model.OptimizationAction
import com.spendwealthy.app.domain.model.OptimizationType
import com.spendwealthy.app.domain.model.OptimizerRecommendation
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizationDetailBottomSheet(
    recommendation: OptimizerRecommendation,
    onExecute: (String) -> Unit,
    onViewHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val haptic = LocalHapticFeedback.current
    var isExecuting by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Background,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        AppColors.Primary.copy(alpha = 0.2f),
                                        AppColors.Accent.copy(alpha = 0.2f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (recommendation.id.contains("family")) Icons.Default.Groups else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (recommendation.id.contains("family")) AppColors.Accent else AppColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (recommendation.id.contains("family")) "Family Wealth Optimizer" else "Autonomous Wealth Optimizer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (recommendation.id.contains("family")) AppColors.Accent else AppColors.Primary,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = recommendation.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = AppColors.TextMain
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppColors.TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Impact Score Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = AppColors.SurfaceRaised,
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROJECTED EFFICIENCY IMPACT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981),
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = recommendation.explanation,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = AppColors.TextMuted
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "+${recommendation.impactScore}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Actions Breakdown Section
            Text(
                text = "Rencana Eksekusi Algoritmik",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                recommendation.actions.forEachIndexed { index, action ->
                    ActionItemCard(index = index + 1, action = action)
                }
            }

            // Autonomous Safety Assurance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Aman & Reversibel: Langkah ini dicatat ke riwayat eksekusi kekayaan dan dapat ditinjau kapan saja.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = AppColors.TextMuted
                )
            }

            // Primary Execute Button
            Button(
                onClick = {
                    if (!isExecuting) {
                        isExecuting = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onExecute(recommendation.id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .bouncyClickable {},
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    contentColor = Color.Black
                ),
                enabled = !isExecuting
            ) {
                if (isExecuting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Mengoptimalkan...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Terapkan Optimalisasi Sekarang ✦",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Secondary History Button
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onViewHistory()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppColors.TextMain
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = AppColors.Primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Lihat Riwayat Optimalisasi",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ActionItemCard(
    index: Int,
    action: OptimizationAction
) {
    val (icon, typeLabel, typeColor) = when (action.type) {
        OptimizationType.GOAL_BOOST -> Triple(
            Icons.AutoMirrored.Filled.TrendingUp,
            "Alokasi Target",
            Color(0xFF10B981)
        )
        OptimizationType.BILL_SHIELD -> Triple(
            Icons.Default.Shield,
            "Cadangan Tagihan",
            Color(0xFF38BDF8)
        )
        OptimizationType.IDLE_CASH_SWEEP -> Triple(
            Icons.Default.ElectricBolt,
            "Sapuan Dana Mengendap",
            Color(0xFFF59E0B)
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AppColors.SurfaceRaised,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Langkah $index",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMuted
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = typeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = typeLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = typeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${action.sourceId} ➔ ${action.targetId}",
                    fontSize = 12.sp,
                    color = AppColors.TextMuted
                )
            }

            Text(
                text = action.amount.formatWithSymbol(),
                style = AppTypography.headlineNumeric(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = AppColors.TextMain
                )
            )
        }
    }
}
