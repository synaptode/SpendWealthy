package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
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
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalContributionDialog(
    goal: Goal,
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onContribute: (Wallet, Long) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedWallet by remember { mutableStateOf(wallets.firstOrNull()) }
    var amount by remember { mutableStateOf("") }

    val currentTarget = goal.targetAmount.amount.coerceAtLeast(1L)
    val currentAmount = goal.currentAmount.amount
    val remainingNeeded = (currentTarget - currentAmount).coerceAtLeast(0L)
    val parsedAmount = amount.toLongOrNull() ?: 0L
    val newTotal = currentAmount + parsedAmount
    val currentPct = ((currentAmount.toDouble() / currentTarget) * 100).toInt()
    val newPct = ((newTotal.toDouble() / currentTarget) * 100).toInt()
    val progressGain = (newPct - currentPct).coerceAtLeast(0)

    val selectedWalletBalance = selectedWallet?.balance?.amount ?: 0L
    val isInsufficientBalance = selectedWallet != null && parsedAmount > selectedWalletBalance
    val isTargetComplete = newTotal >= currentTarget

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
                .widthIn(max = 540.dp)
                .align(Alignment.CenterHorizontally)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AppColors.Primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Nabung ke Impian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = goal.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextMuted
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppColors.SurfaceRaised)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Tutup",
                        tint = AppColors.TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Target Status Snapshot
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AppColors.SurfaceRaised
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Terkumpul Saat Ini",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextMuted
                        )
                        Text(
                            text = goal.currentAmount.formatWithSymbol(),
                            style = AppTypography.bodyNumeric(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Sisa Menuju Target",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextMuted
                        )
                        Text(
                            text = Money(remainingNeeded, "IDR").formatWithSymbol(),
                            style = AppTypography.bodyNumeric(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        )
                    }
                }
            }

            // Input Nominal
            AppAmountInput(
                value = amount,
                onValueChange = { amount = it },
                label = "Nominal Setoran (Rp)"
            )

            // Live Currency & Progress Projection Card
            AnimatedVisibility(
                visible = parsedAmount > 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = AppColors.Primary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.25f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Jumlah Setoran",
                                fontSize = 12.sp,
                                color = AppColors.TextMuted
                            )
                            Text(
                                text = Money(parsedAmount, "IDR").formatWithSymbol(),
                                style = AppTypography.headlineNumeric(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AppColors.TextMain
                                )
                            )
                        }

                        // Progress indicator bar
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progres: $currentPct% ➔ $newPct%",
                                    style = AppTypography.labelNumeric(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTargetComplete) Color(0xFF10B981) else AppColors.Primary
                                    )
                                )
                                Text(
                                    text = if (isTargetComplete) "Target Tercapai! 🏆" else "+$progressGain% lonjakan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isTargetComplete) Color(0xFF10B981) else AppColors.TextMuted
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(AppColors.SurfaceRaised)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((newPct.toFloat() / 100f).coerceIn(0f, 1f))
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF10B981), Color(0xFF34D399), Color(0xFFDEFF37))
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Quick Nominal Pills
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Pilih Cepat",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMuted
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (remainingNeeded > 0L) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AppColors.Primary.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        amount = remainingNeeded.toString()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AppColors.Primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Lunasi (${Money(remainingNeeded, "IDR").formatWithSymbol()})",
                                        style = AppTypography.labelNumeric(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.Primary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    listOf(50_000L, 100_000L, 250_000L, 500_000L, 1_000_000L).forEach { quickAmt ->
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AppColors.SurfaceRaised,
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val current = amount.toLongOrNull() ?: 0L
                                        amount = (current + quickAmt).toString()
                                    }
                            ) {
                                Text(
                                    text = "+${Money(quickAmt, "IDR").formatWithSymbol()}",
                                    style = AppTypography.labelNumeric(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.TextMain
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Wallet Selection
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Sumber Dompet",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(wallets) { wallet ->
                        WalletChip(
                            wallet = wallet,
                            isSelected = selectedWallet?.id == wallet.id,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedWallet = wallet
                            }
                        )
                    }
                }
            }

            // Insufficient Balance Warning
            AnimatedVisibility(
                visible = isInsufficientBalance,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppColors.Danger.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Danger.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AppColors.Danger,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Saldo dompet tidak mencukupi (Tersedia: ${selectedWallet?.balance?.formatWithSymbol() ?: "Rp0"})",
                            style = AppTypography.labelNumeric(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.Danger
                            )
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.TextMuted
                    )
                ) {
                    Text("Batal", fontWeight = FontWeight.SemiBold)
                }

                AppButton(
                    text = "Setor Tabungan",
                    onClick = {
                        val amt = amount.toLongOrNull() ?: 0L
                        selectedWallet?.let { onContribute(it, amt) }
                    },
                    enabled = parsedAmount > 0L && selectedWallet != null && !isInsufficientBalance,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                )
            }
        }
    }
}

@Composable
private fun WalletChip(wallet: Wallet, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                null,
                tint = if (isSelected) AppColors.OnPrimary else AppColors.Primary,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = wallet.name,
                    color = if (isSelected) AppColors.OnPrimary else AppColors.TextMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = wallet.balance.formatWithSymbol(),
                    style = AppTypography.labelNumeric(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) AppColors.OnPrimary.copy(alpha = 0.8f) else AppColors.TextMuted
                    )
                )
            }
        }
    }
}
