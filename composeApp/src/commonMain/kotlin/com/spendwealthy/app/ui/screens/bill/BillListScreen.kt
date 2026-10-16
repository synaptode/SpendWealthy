package com.spendwealthy.app.ui.screens.bill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.BillStatus
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.ui.components.EmptyState
import com.spendwealthy.app.ui.theme.AppColors

class BillListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BillListViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current

        var successMessage by remember { mutableStateOf<String?>(null) }

        val unpaidBills = state.bills.filter { viewModel.statusOf(it) != BillStatus.PAID }
        val totalUnpaidAmount = unpaidBills.sumOf { it.amount.amount }

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    BillsHeader(
                        onBack = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            navigator.pop()
                        },
                        onAdd = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            navigator.push(AddBillScreen())
                        }
                    )
                }

                if (unpaidBills.isNotEmpty()) {
                    item {
                        BillSummaryBanner(
                            totalAmount = Money(totalUnpaidAmount, "IDR").format(),
                            count = unpaidBills.size
                        )
                    }
                }

                if (state.bills.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Tidak ada tagihan aktif",
                            message = "Tambahkan tagihan bulanan atau paylater agar selalu ingat tanggal jatuh tempo.",
                            icon = Icons.AutoMirrored.Filled.ReceiptLong
                        )
                    }
                } else {
                    items(state.bills, key = { it.id }) { bill ->
                        val status = viewModel.statusOf(bill)
                        BillCardItem(
                            bill = bill,
                            status = status,
                            onPaid = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.markPaid(bill.id)
                                successMessage = "${bill.name} berhasil ditandai lunas"
                            }
                        )
                    }
                }
            }

            // Sleek floating toast notification
            AnimatedVisibility(
                visible = successMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.Primary,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = successMessage ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                LaunchedEffect(successMessage) {
                    if (successMessage != null) {
                        kotlinx.coroutines.delay(2000)
                        successMessage = null
                    }
                }
            }
        }
    }

    @Composable
    private fun BillsHeader(onBack: () -> Unit, onAdd: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                shape = CircleShape,
                color = AppColors.Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Tagihan & Paylater",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Jadwal pembayaran & kewajiban",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted
                )
            }

            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAdd),
                shape = CircleShape,
                color = AppColors.Primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Tagihan",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun BillSummaryBanner(totalAmount: String, count: Int) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.SurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Kewajiban Terjadwal",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = totalAmount,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.Primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "$count tagihan",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                }
            }
        }
    }

    @Composable
    private fun BillCardItem(
        bill: BillReminder,
        status: BillStatus,
        onPaid: () -> Unit
    ) {
        val (statusLabel, statusColor) = when (status) {
            BillStatus.OVERDUE -> "Terlewat" to AppColors.Danger
            BillStatus.DUE_SOON -> "Segera Jatuh Tempo" to AppColors.Warning
            BillStatus.PAID -> "Lunas" to AppColors.Success
            else -> "Terjadwal" to AppColors.Primary
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(statusColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (status == BillStatus.PAID) Icons.Default.Check else Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = bill.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = "Tempo: ${bill.dueDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextMuted
                            )
                        }
                    }

                    Text(
                        text = bill.amount.format(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (status == BillStatus.PAID) AppColors.Success else AppColors.TextMain
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = statusLabel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }

                    if (status != BillStatus.PAID) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onPaid),
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.Primary
                        ) {
                            Text(
                                text = "Tandai Lunas",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium,
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
