package com.spendwealthy.app.ui.screens.simulator

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.PurchaseVerdict
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseSimulatorBottomSheet(
    viewModel: PurchaseSimulatorViewModel,
    onDismiss: () -> Unit,
    onProceedToTransaction: (itemName: String, amount: Long, walletId: String?) -> Unit,
    onConvertToGoal: (itemName: String, amount: Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

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
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    val strings = com.spendwealthy.app.core.localization.LocalStrings.current
                    Text(
                        text = strings.simulatorTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.TextMain,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = strings.simulatorSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextMuted
                    )
                }

                val strings = com.spendwealthy.app.core.localization.LocalStrings.current
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

            // Item Name Input
            Text(
                text = "Nama Barang / Rencana Belanja",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.itemName,
                onValueChange = { viewModel.onItemNameChanged(it) },
                placeholder = {
                    Text(
                        text = "Misal: Sepatu Lari, Gadget Baru, Tiket Konser",
                        color = AppColors.TextMuted.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = AppColors.Outline,
                    focusedContainerColor = AppColors.Background,
                    unfocusedContainerColor = AppColors.Background
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price Input
            Text(
                text = "Estimasi Harga",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = if (state.priceAmount > 0) Money(state.priceAmount, "IDR").formatWithSymbol() else "",
                onValueChange = { viewModel.onPriceInputChanged(it) },
                placeholder = {
                    Text(
                        text = "Rp 0",
                        color = AppColors.TextMuted.copy(alpha = 0.6f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = AppColors.Outline,
                    focusedContainerColor = AppColors.Background,
                    unfocusedContainerColor = AppColors.Background
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Preset Chips
            val presets = listOf(
                100_000L to "100rb",
                350_000L to "350rb",
                1_000_000L to "1 Juta",
                2_500_000L to "2.5 Juta",
                10_000_000L to "10 Juta"
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets) { (amount, label) ->
                    val isSelected = state.priceAmount == amount
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.onPresetSelected(amount)
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AppColors.CharcoalBlack else AppColors.Outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AppColors.CharcoalBlack else AppColors.TextMain
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Source Wallet Picker
            Text(
                text = "Sumber Dana / Dompet",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.wallets) { wallet ->
                    val isSelected = state.selectedWallet?.id == wallet.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.onWalletSelected(wallet)
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) AppColors.Primary.copy(alpha = 0.15f) else AppColors.Background,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) AppColors.Primary else AppColors.Outline.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.SurfaceRaised),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = AppColors.TextMain,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = wallet.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextMain
                                )
                                Text(
                                    text = wallet.balance.formatWithSymbol(),
                                    fontSize = 11.sp,
                                    color = AppColors.TextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Reactive Simulation Results Card
            val result = state.simulationResult
            AnimatedVisibility(
                visible = result != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (result != null) {
                    val verdictContainerColor = when (result.verdict) {
                        PurchaseVerdict.SAFE_TO_BUY -> Color(0xFF10B981).copy(alpha = 0.12f)
                        PurchaseVerdict.PROCEED_WITH_CAUTION -> Color(0xFFF59E0B).copy(alpha = 0.14f)
                        PurchaseVerdict.HIGH_RISK_DEFICIT -> Color(0xFFEF4444).copy(alpha = 0.12f)
                    }
                    val verdictBorderColor = when (result.verdict) {
                        PurchaseVerdict.SAFE_TO_BUY -> Color(0xFF10B981)
                        PurchaseVerdict.PROCEED_WITH_CAUTION -> Color(0xFFF59E0B)
                        PurchaseVerdict.HIGH_RISK_DEFICIT -> Color(0xFFEF4444)
                    }
                    val verdictIcon = when (result.verdict) {
                        PurchaseVerdict.SAFE_TO_BUY -> Icons.Default.CheckCircle
                        PurchaseVerdict.PROCEED_WITH_CAUTION -> Icons.Default.Info
                        PurchaseVerdict.HIGH_RISK_DEFICIT -> Icons.Default.Warning
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = AppColors.Background,
                        border = BorderStroke(1.dp, verdictBorderColor.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Verdict Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(verdictContainerColor)
                                    .border(1.dp, verdictBorderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = verdictIcon,
                                    contentDescription = null,
                                    tint = verdictBorderColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = result.verdictTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = verdictBorderColor
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = result.verdictMessage,
                                fontSize = 12.sp,
                                color = AppColors.TextMain,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = AppColors.Outline.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(14.dp))

                            // 3-Column Metrics Breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Column 1: Jatah Harian
                                MetricImpactColumn(
                                    title = "Jatah Harian",
                                    currentValue = result.currentDailyAllowance.formatWithSymbol(),
                                    simulatedValue = result.simulatedDailyAllowance.formatWithSymbol(),
                                    isNegativeImpact = result.simulatedDailyAllowance.amount < result.currentDailyAllowance.amount
                                )

                                // Column 2: Dana Darurat
                                MetricImpactColumn(
                                    title = "Dana Darurat",
                                    currentValue = "${result.emergencyRunwayMonthsCurrent} bln",
                                    simulatedValue = "${result.emergencyRunwayMonthsSimulated} bln",
                                    isNegativeImpact = result.emergencyRunwayMonthsSimulated < result.emergencyRunwayMonthsCurrent
                                )

                                // Column 3: Tabungan Keluarga
                                val goalImpactText = if (result.impactOnFamilyGoalDaysDelayed > 0) {
                                    "+${result.impactOnFamilyGoalDaysDelayed} hr"
                                } else {
                                    "Aman"
                                }
                                MetricImpactColumn(
                                    title = "Target Keluarga",
                                    currentValue = "On Track",
                                    simulatedValue = goalImpactText,
                                    isNegativeImpact = result.impactOnFamilyGoalDaysDelayed > 0
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Recommendation Note
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AppColors.SurfaceRaised.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AppColors.TextMuted,
                                        modifier = Modifier.size(15.dp).padding(top = 1.dp)
                                    )
                                    Text(
                                        text = result.recommendation,
                                        fontSize = 11.sp,
                                        color = AppColors.TextMuted,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onProceedToTransaction(
                            state.itemName.ifBlank { "Pembelian Barang" },
                            state.priceAmount,
                            state.selectedWallet?.id
                        )
                    },
                    enabled = state.priceAmount > 0L,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        contentColor = AppColors.CharcoalBlack
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lanjut Catat Transaksi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onConvertToGoal(
                            state.itemName.ifBlank { "Rencana Pembelian" },
                            state.priceAmount
                        )
                    },
                    enabled = state.priceAmount > 0L,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AppColors.Outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.TextMain
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Jadikan Target Tabungan Saja",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricImpactColumn(
    title: String,
    currentValue: String,
    simulatedValue: String,
    isNegativeImpact: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = 90.dp)
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = currentValue,
            fontSize = 11.sp,
            color = AppColors.TextMuted,
            fontWeight = FontWeight.Normal
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (isNegativeImpact) Color(0xFFEF4444) else Color(0xFF10B981),
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = simulatedValue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isNegativeImpact) Color(0xFFEF4444) else AppColors.TextMain
            )
        }
    }
}
