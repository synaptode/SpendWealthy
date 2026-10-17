package com.spendwealthy.app.ui.screens.splitter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.PaycheckSplitBreakdown
import com.spendwealthy.app.domain.model.SplitStrategy
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaycheckSplitterBottomSheet(
    viewModel: PaycheckSplitterViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .animateContentSize()
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(AppColors.Primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AppColors.PrimaryDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Bagi Gaji Otomatis",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "Smart Paycheck Splitter",
                            fontSize = 12.sp,
                            color = AppColors.TextMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = AppColors.TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Success View
            if (state.executionResult != null) {
                SuccessSplitView(
                    result = state.executionResult!!,
                    onFinish = {
                        viewModel.dismissResult()
                        onDismiss()
                    }
                )
            } else {
                // Main Form View
                SplitterFormContent(
                    state = state,
                    viewModel = viewModel,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun SplitterFormContent(
    state: PaycheckSplitterUiState,
    viewModel: PaycheckSplitterViewModel,
    onDismiss: () -> Unit
) {
    // 1. Gaji Input
    Text(
        text = "Nominal Gaji Diterima",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.TextMuted
    )
    Spacer(modifier = Modifier.height(6.dp))

    val currency = state.wallets.firstOrNull()?.balance?.currency ?: "IDR"
    val formattedDisplay = if (state.salaryAmount > 0L) {
        Money(state.salaryAmount, currency).format()
    } else {
        "Rp 0"
    }

    OutlinedTextField(
        value = state.salaryInput,
        onValueChange = { viewModel.onSalaryChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Contoh: 15000000") },
        prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = AppColors.TextMain) },
        supportingText = {
            Text(
                text = "Terbaca: $formattedDisplay",
                fontSize = 11.sp,
                color = AppColors.PrimaryDark
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.PrimaryDark,
            unfocusedBorderColor = AppColors.Outline,
            focusedContainerColor = AppColors.SurfaceRaised,
            unfocusedContainerColor = AppColors.SurfaceRaised
        )
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Preset Chips
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val presets = listOf(
            5_000_000L to "5 Juta",
            10_000_000L to "10 Juta",
            15_000_000L to "15 Juta",
            25_000_000L to "25 Juta",
            50_000_000L to "50 Juta"
        )
        presets.forEach { (amount, label) ->
            val isSelected = state.salaryAmount == amount
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { viewModel.onPresetSalarySelected(amount) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
                border = if (isSelected) null else BorderStroke(1.dp, AppColors.Outline)
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.Black else AppColors.TextMain,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 2. Rekening Payroll Selector
    Text(
        text = "Masuk ke Rekening Payroll",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.TextMuted
    )
    Spacer(modifier = Modifier.height(6.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.wallets.forEach { wallet ->
            val isSelected = wallet.id == state.selectedPayrollWalletId
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.onPayrollWalletSelected(wallet.id) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) AppColors.Primary.copy(alpha = 0.12f) else AppColors.SurfaceRaised,
                border = BorderStroke(
                    1.dp,
                    if (isSelected) AppColors.PrimaryDark else AppColors.Outline
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = if (isSelected) AppColors.PrimaryDark else AppColors.TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = wallet.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = wallet.balance.format(),
                            fontSize = 10.sp,
                            color = AppColors.TextMuted
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 3. Strategy Tabs
    Text(
        text = "Pilih Strategi Pembagian",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.TextMuted
    )
    Spacer(modifier = Modifier.height(6.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SplitStrategy.entries.forEach { strategy ->
            val isSelected = state.selectedStrategy == strategy
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { viewModel.onStrategySelected(strategy) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
                border = if (isSelected) null else BorderStroke(1.dp, AppColors.Outline)
            ) {
                Text(
                    text = strategy.title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.Black else AppColors.TextMain,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 4. Interactive Visual Proportional Bar
    state.breakdown?.let { breakdown ->
        ProportionBar(breakdown = breakdown)

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Three Breakdown Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Pos 1: Needs
            BucketDetailCard(
                icon = Icons.Default.Home,
                iconTint = Color(0xFF10B981),
                title = "Kebutuhan & Tagihan Wajib",
                ratioText = "${(breakdown.needsRatio * 100).toInt()}%",
                amount = breakdown.needsAmount,
                subtitle = if (breakdown.isNeedsSufficientForBills) {
                    "Tagihan bulan ini (${breakdown.upcomingBillsTotal.format()}) tercover aman."
                } else {
                    "⚠️ Kurang ${breakdown.billsDeficit.format()} untuk tagihan bulan ini!"
                },
                isWarning = !breakdown.isNeedsSufficientForBills
            )

            // Pos 2: Wants
            BucketDetailCard(
                icon = Icons.Default.Coffee,
                iconTint = Color(0xFFF59E0B),
                title = "Lifestyle & Pengeluaran Harian",
                ratioText = "${(breakdown.wantsRatio * 100).toInt()}%",
                amount = breakdown.wantsAmount,
                subtitle = "Proyeksi jatah harian aman: ${breakdown.projectedDailyAllowance.format()} / hari.",
                isWarning = false
            )

            // Pos 3: Savings
            BucketDetailCard(
                icon = Icons.Default.Savings,
                iconTint = Color(0xFF8B5CF6),
                title = "Tabungan & Investasi",
                ratioText = "${(breakdown.savingsRatio * 100).toInt()}%",
                amount = breakdown.savingsAmount,
                subtitle = if (breakdown.goalAllocations.isNotEmpty()) {
                    "Disalurkan ke ${breakdown.goalAllocations.size} target tabungan aktif."
                } else {
                    "Belum ada target tabungan aktif terdaftar."
                },
                isWarning = false
            ) {
                // Goal Mini breakdown
                if (breakdown.goalAllocations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        breakdown.goalAllocations.forEach { alloc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AppColors.SurfaceMuted, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = null,
                                        tint = Color(0xFF8B5CF6),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = alloc.goal.name,
                                        fontSize = 11.sp,
                                        color = AppColors.TextMain,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "+${alloc.allocatedAmount.format()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B5CF6)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Error Alert
    if (state.errorMessage != null) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = AppColors.Error.copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AppColors.Error,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state.errorMessage,
                    fontSize = 12.sp,
                    color = AppColors.Error
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // Action CTA Button
    Button(
        onClick = { viewModel.onExecuteSplit() },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .bouncyClickable {},
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Primary,
            contentColor = Color.Black
        ),
        enabled = !state.isExecuting && state.salaryAmount > 0L
    ) {
        if (state.isExecuting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.Black,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mengalokasikan...")
        } else {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Alokasikan Gaji Sekarang",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedButton(
        onClick = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, AppColors.Outline)
    ) {
        Text("Tutup", color = AppColors.TextMuted)
    }
}

@Composable
private fun ProportionBar(breakdown: PaycheckSplitBreakdown) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Komposisi Alokasi",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMuted
            )
            Text(
                text = "${(breakdown.needsRatio * 100).toInt()}% • ${(breakdown.wantsRatio * 100).toInt()}% • ${(breakdown.savingsRatio * 100).toInt()}%",
                fontSize = 11.sp,
                color = AppColors.TextMuted
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            val needsWeight = breakdown.needsRatio.coerceAtLeast(0.05f)
            val wantsWeight = breakdown.wantsRatio.coerceAtLeast(0.05f)
            val savingsWeight = breakdown.savingsRatio.coerceAtLeast(0.05f)

            Box(
                modifier = Modifier
                    .weight(needsWeight)
                    .background(Color(0xFF10B981))
            )
            Box(
                modifier = Modifier
                    .weight(wantsWeight)
                    .background(Color(0xFFF59E0B))
            )
            Box(
                modifier = Modifier
                    .weight(savingsWeight)
                    .background(Color(0xFF8B5CF6))
            )
        }
    }
}

@Composable
private fun BucketDetailCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    ratioText: String,
    amount: Money,
    subtitle: String,
    isWarning: Boolean,
    extraContent: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AppColors.SurfaceRaised,
        border = BorderStroke(
            1.dp,
            if (isWarning) AppColors.Error else AppColors.Outline
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(iconTint.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextMain
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconTint.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = ratioText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconTint,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = amount.format(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = if (isWarning) AppColors.Error else AppColors.TextMuted,
                lineHeight = 15.sp
            )

            extraContent?.invoke()
        }
    }
}

@Composable
private fun SuccessSplitView(
    result: com.spendwealthy.app.domain.model.ExecuteSplitResult,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF10B981).copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Gaji Berhasil Dibagi!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextMain
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = result.message,
            fontSize = 13.sp,
            color = AppColors.TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Selesai",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
