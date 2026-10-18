package com.spendwealthy.app.ui.screens.bill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.spendwealthy.app.domain.model.Wallet
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.BillStatus
import com.spendwealthy.app.domain.model.CurrencyConverter
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.RecurrenceType
import com.spendwealthy.app.ui.components.BrandLogo
import com.spendwealthy.app.ui.components.KnownBrand
import com.spendwealthy.app.ui.components.EmptyState
import com.spendwealthy.app.core.localization.LocalStrings
import com.spendwealthy.app.domain.service.VampireDrainEngine
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

enum class BillFilterTab {
    UNPAID, ALL, PAID
}

class BillListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BillListViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current
        val strings = LocalStrings.current

        var successMessage by remember { mutableStateOf<String?>(null) }
        var billToPay by remember { mutableStateOf<BillReminder?>(null) }
        var selectedFilter by remember { mutableStateOf(BillFilterTab.UNPAID) }
        var showVampireAuditModal by remember { mutableStateOf(false) }

        val vampireEngine = remember { VampireDrainEngine() }
        val vampireReport = remember(state.bills) { vampireEngine.auditSubscriptions(state.bills) }

        val unpaidBills = state.bills.filter { viewModel.statusOf(it) != BillStatus.PAID }
        val paidBills = state.bills.filter { viewModel.statusOf(it) == BillStatus.PAID }
        val totalUnpaidAmount = unpaidBills.sumOf { CurrencyConverter.toIdr(it.amount) }

        val displayedBills = when (selectedFilter) {
            BillFilterTab.UNPAID -> unpaidBills
            BillFilterTab.ALL -> state.bills
            BillFilterTab.PAID -> paidBills
        }

        Box(
            modifier = Modifier.fillMaxSize().background(AppColors.Background),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    BillsHeader(
                        onBack = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (navigator.canPop) navigator.pop()
                        },
                        onAdd = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            navigator.push(AddBillScreen())
                        }
                    )
                }

                // Vampire Drain Auditor Banner
                if (vampireReport.vampireSubscriptionsCount > 0) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showVampireAuditModal = true
                                },
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF1E1B4B),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF450A0A),
                                                Color(0xFF18181B)
                                            )
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "🧛‍♂️", fontSize = 18.sp)
                                        }

                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "Audit Kebocoran Gaib",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFFEF4444).copy(alpha = 0.25f)
                                                ) {
                                                    Text(
                                                        text = "${vampireReport.vampireSubscriptionsCount} Langganan",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFCA5A5),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Total ${Money(vampireReport.totalAnnualDrain, "IDR").formatWithSymbol()}/thn • Pelajari potensi kebocoran >",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Buka Audit",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (unpaidBills.isNotEmpty()) {
                    item {
                        BillSummaryBanner(
                            totalAmount = Money(totalUnpaidAmount, "IDR").formatWithSymbol(),
                            count = unpaidBills.size
                        )
                    }
                }

                // Filter Segmented Tabs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            BillFilterTab.UNPAID to "Belum Bayar (${unpaidBills.size})",
                            BillFilterTab.ALL to "Semua (${state.bills.size})",
                            BillFilterTab.PAID to "Lunas (${paidBills.size})"
                        ).forEach { (tab, label) ->
                            val isSelected = selectedFilter == tab
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
                                border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Outline),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedFilter = tab
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                        color = if (isSelected) AppColors.OnPrimary else AppColors.TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.bills.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Belum Ada Tagihan Terjadwal",
                            message = "Daftarkan tagihan bulanan atau cicilan paylater agar arus kas selalu terprediksi.",
                            icon = Icons.AutoMirrored.Filled.ReceiptLong
                        )
                    }
                } else if (displayedBills.isEmpty()) {
                    item {
                        EmptyState(
                            title = when (selectedFilter) {
                                BillFilterTab.UNPAID -> "Semua Tagihan Lunas! 🎉"
                                BillFilterTab.PAID -> "Belum Ada Tagihan yang Lunas"
                                else -> "Tidak Ada Tagihan"
                            },
                            message = when (selectedFilter) {
                                BillFilterTab.UNPAID -> "Luar biasa! Tidak ada tagihan tertunggak atau jatuh tempo saat ini."
                                BillFilterTab.PAID -> "Tagihan yang sudah dibayar bulan ini akan muncul di sini sebagai riwayat."
                                else -> "Daftarkan tagihan Anda agar arus kas selalu terpantau."
                            },
                            icon = if (selectedFilter == BillFilterTab.UNPAID) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ReceiptLong
                        )
                    }
                } else {
                    items(displayedBills, key = { it.id }) { bill ->
                        val status = viewModel.statusOf(bill)
                        BillCardItem(
                            bill = bill,
                            status = status,
                            onPaid = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (state.wallets.size > 1) {
                                    billToPay = bill
                                } else {
                                    val target = state.wallets.firstOrNull()
                                    viewModel.markPaid(bill.id, target?.id)
                                    successMessage = "${bill.name} berhasil ditandai lunas"
                                }
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
                    color = AppColors.SurfaceRaised,
                    border = BorderStroke(1.dp, AppColors.Primary),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = successMessage ?: "",
                            color = AppColors.TextMain,
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

            val currentBillToPay = billToPay
            if (currentBillToPay != null) {
                PaymentWalletPickerModal(
                    bill = currentBillToPay,
                    wallets = state.wallets,
                    onWalletSelected = { wallet ->
                        viewModel.markPaid(currentBillToPay.id, wallet.id)
                        successMessage = "${currentBillToPay.name} berhasil dibayar dengan ${wallet.name}"
                        billToPay = null
                    },
                    onDismiss = { billToPay = null }
                )
            }

            if (showVampireAuditModal) {
                VampireDrainAuditBottomSheet(
                    report = vampireReport,
                    onDismiss = { showVampireAuditModal = false }
                )
            }
        }
    }

    @Composable
    private fun BillsHeader(onBack: () -> Unit, onAdd: () -> Unit) {
        val strings = LocalStrings.current
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
                color = AppColors.SurfaceRaised,
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.back,
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tagihan & Langganan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Jadwal pembayaran & recurring",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                        tint = AppColors.OnPrimary,
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
            shape = RoundedCornerShape(22.dp),
            color = AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(
                                AppColors.SurfaceRaised,
                                AppColors.Surface
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Accent)
                            )
                            Text(
                                text = "Total Kewajiban Terjadwal",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMuted
                            )
                        }
                        Text(
                            text = totalAmount,
                            style = AppTypography.headlineNumeric(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.TextMain
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.Primary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "$count tagihan aktif",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppColors.Primary
                        )
                    }
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
        val (statusLabel, statusColor, statusIcon) = when (status) {
            BillStatus.OVERDUE -> Triple("Jatuh Tempo Terlewat", AppColors.Danger, Icons.Default.ErrorOutline)
            BillStatus.DUE_SOON -> Triple("Segera Jatuh Tempo", AppColors.Warning, Icons.Default.Schedule)
            BillStatus.PAID -> Triple("Sudah Lunas", AppColors.Success, Icons.Default.Check)
            else -> Triple("Terjadwal", AppColors.Primary, Icons.Default.CalendarMonth)
        }

        val recurrenceLabel = when (bill.recurrenceType) {
            RecurrenceType.NONE -> "Sekali"
            RecurrenceType.DAILY -> "Harian"
            RecurrenceType.WEEKLY -> "Mingguan"
            RecurrenceType.MONTHLY -> "Bulanan"
            RecurrenceType.YEARLY -> "Tahunan"
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = BorderStroke(
                1.dp,
                if (status == BillStatus.DUE_SOON) AppColors.Warning.copy(alpha = 0.4f)
                else if (status == BillStatus.OVERDUE) AppColors.Danger.copy(alpha = 0.4f)
                else AppColors.Outline
            )
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
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        val brand = KnownBrand.from(bill.name)
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(statusColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (brand != null) {
                                BrandLogo(
                                    brand = brand,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (brand.isMonochrome) AppColors.TextMain else null
                                )
                            } else {
                                Icon(
                                    imageVector = statusIcon,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = bill.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Jatuh tempo: ${bill.dueDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppColors.TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Box(
                                    modifier = Modifier
                                        .size(3.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.TextMuted)
                                )
                                Text(
                                    text = recurrenceLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppColors.TextMuted,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = bill.amount.formatWithSymbol(),
                            style = AppTypography.bodyNumeric(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (status == BillStatus.PAID) AppColors.Success else AppColors.TextMain
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (bill.amount.currency != "IDR") {
                            Text(
                                text = CurrencyConverter.formatEstimatedIdr(bill.amount),
                                style = AppTypography.labelNumeric(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextMuted
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (status != BillStatus.PAID) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onPaid),
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.Primary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AppColors.OnPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Tandai Lunas",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.OnPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PaymentWalletPickerModal(
        bill: BillReminder,
        wallets: List<Wallet>,
        onWalletSelected: (Wallet) -> Unit,
        onDismiss: () -> Unit
    ) {
        val haptic = LocalHapticFeedback.current
        val billIdr = CurrencyConverter.toIdr(bill.amount)

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
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Konfirmasi Pelunasan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain
                    )

                    Surface(
                        shape = CircleShape,
                        color = AppColors.SurfaceRaised,
                        border = BorderStroke(1.dp, AppColors.Outline),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDismiss()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = AppColors.TextMain,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Bill Snapshot Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.SurfaceRaised,
                    border = BorderStroke(1.dp, AppColors.Outline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = bill.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppColors.TextMain
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Jatuh Tempo: ${bill.dueDate}",
                                    style = AppTypography.labelNumeric(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Total Tagihan",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextMuted
                            )
                            Text(
                                text = bill.amount.formatWithSymbol(),
                                style = AppTypography.headlineNumeric(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AppColors.TextMain
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "Pilih Dompet Sumber Pembayaran",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMuted
                )

                // Wallets List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    wallets.forEach { wallet ->
                        val walletIdr = CurrencyConverter.toIdr(wallet.balance)
                        val isSufficient = walletIdr >= billIdr
                        val remainingAfter = walletIdr - billIdr

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(enabled = isSufficient) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onWalletSelected(wallet)
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSufficient) AppColors.SurfaceRaised else AppColors.SurfaceRaised.copy(alpha = 0.5f),
                            border = BorderStroke(
                                1.dp,
                                if (isSufficient) AppColors.Outline else AppColors.Danger.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSufficient) AppColors.Primary.copy(alpha = 0.15f)
                                                else AppColors.Danger.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = if (isSufficient) AppColors.Primary else AppColors.Danger,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = wallet.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = AppColors.TextMain
                                        )
                                        Text(
                                            text = "Saldo: ${wallet.balance.formatWithSymbol()}",
                                            style = AppTypography.labelNumeric(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = AppColors.TextMuted
                                            )
                                        )
                                        if (isSufficient) {
                                            Text(
                                                text = "Sisa setelah bayar: ${Money(remainingAfter, "IDR").formatWithSymbol()}",
                                                style = AppTypography.labelNumeric(
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF10B981)
                                                )
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSufficient) Color(0xFF10B981).copy(alpha = 0.12f) else AppColors.Danger.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = if (isSufficient) "Bayar ✓" else "Kurang ${Money(billIdr - walletIdr, "IDR").formatWithSymbol()}",
                                        style = AppTypography.labelNumeric(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSufficient) Color(0xFF10B981) else AppColors.Danger
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

