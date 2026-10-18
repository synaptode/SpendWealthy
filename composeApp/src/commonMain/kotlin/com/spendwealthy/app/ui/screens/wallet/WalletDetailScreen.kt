package com.spendwealthy.app.ui.screens.wallet

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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.spendwealthy.app.ui.screens.transaction.AddTransactionScreen
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.CurrencyConverter
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.ui.components.BrandLogo
import com.spendwealthy.app.ui.components.KnownBrand
import com.spendwealthy.app.ui.components.TransactionDetailModal
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.components.tactilePress
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography
import com.spendwealthy.app.core.localization.LocalStrings
import org.koin.core.parameter.parametersOf

data class WalletDetailScreen(val walletId: String) : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<WalletDetailViewModel> { parametersOf(walletId) }
        val state by viewModel.uiState.collectAsState()
        val transactions by viewModel.filteredTransactions.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current
        val strings = LocalStrings.current
        var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

        Box(
            modifier = Modifier.fillMaxSize().background(AppColors.Background),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    DetailHeader(
                        title = state.wallet?.name ?: strings.wallet,
                        wallet = state.wallet,
                        onBack = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (navigator.canPop) navigator.pop()
                        }
                    )
                }

                state.wallet?.let { wallet ->
                    item {
                        WalletHeroCard(
                            wallet = wallet,
                            onAction = { type ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                (navigator.parent ?: navigator).push(AddTransactionScreen(type, wallet.id))
                            }
                        )
                    }
                }

                state.wallet?.familyId?.let {
                    item {
                        SpaceToggle(
                            isFamily = state.filterByFamily,
                            onToggle = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleFamilyFilter()
                            }
                        )
                    }
                }

                item {
                    Text(
                        text = strings.recentTransactions,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (transactions.isEmpty()) {
                    item {
                        EmptyWalletActivityState()
                    }
                } else {
                    items(transactions, key = { it.id }) { transaction ->
                        TransactionRowItem(
                            transaction = transaction,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedTransaction = transaction
                            }
                        )
                    }
                }
            }

            // Transaction Detail Modal
            val currentSelected = selectedTransaction
            if (currentSelected != null) {
                TransactionDetailModal(
                    transaction = currentSelected,
                    wallets = listOfNotNull(state.wallet),
                    onDelete = { id -> viewModel.deleteTransaction(id) },
                    onDismiss = { selectedTransaction = null }
                )
            }
        }
    }

    @Composable
    private fun DetailHeader(
        title: String,
        wallet: com.spendwealthy.app.domain.model.Wallet? = null,
        onBack: () -> Unit
    ) {
        val strings = LocalStrings.current
        val brand = wallet?.name?.let { KnownBrand.from(it) }

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
                    .bouncyClickable(
                        pressedScale = 0.90f,
                        hapticType = HapticFeedbackType.LongPress,
                        onClick = onBack
                    ),
                shape = CircleShape,
                color = AppColors.Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
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
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Informasi saldo & riwayat",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = if (brand != null) Color(0xFFF9FAFB) else AppColors.Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (brand != null) {
                        BrandLogo(brand = brand, modifier = Modifier.size(22.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WalletHeroCard(
        wallet: com.spendwealthy.app.domain.model.Wallet,
        onAction: (TransactionType) -> Unit
    ) {
        val isDebt = wallet.type == WalletType.PAYLATER || wallet.type == WalletType.CREDIT_CARD
        val brand = KnownBrand.from(wallet.name)
        val typeLabel = when (wallet.type) {
            WalletType.BANK -> "Rekening Bank"
            WalletType.EWALLET -> "Dompet Digital"
            WalletType.CREDIT_CARD -> "Kartu Kredit"
            WalletType.PAYLATER -> "Paylater"
            WalletType.SAVINGS -> "Tabungan Terkunci"
            WalletType.CASH -> "Uang Tunai"
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF111827),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F2937))
        ) {
            Box(
                modifier = Modifier
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp)
                        ) {
                            if (brand != null) {
                                Surface(
                                    modifier = Modifier.size(24.dp),
                                    shape = CircleShape,
                                    color = Color.White
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        BrandLogo(brand = brand, modifier = Modifier.size(16.dp))
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.Primary)
                                )
                            }
                            Text(
                                text = "SALDO SAAT INI",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AppColors.Primary
                        ) {
                            Text(
                                text = typeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppColors.OnPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    val balanceStr = wallet.balance.formatWithSymbol()
                    val balanceFontSize = when {
                        balanceStr.length > 15 -> 22.sp
                        balanceStr.length > 12 -> 25.sp
                        balanceStr.length > 9 -> 28.sp
                        else -> 32.sp
                    }

                    Text(
                        text = balanceStr,
                        style = AppTypography.displayNumeric(
                            fontSize = balanceFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDebt && wallet.balance.amount < 0) AppColors.Danger else Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (wallet.balance.currency != "IDR") {
                        Text(
                            text = CurrencyConverter.formatEstimatedIdr(wallet.balance),
                            style = AppTypography.bodyNumeric(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.75f)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Quick Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // + Masuk
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .bouncyClickable(
                                    pressedScale = 0.93f,
                                    hapticType = HapticFeedbackType.LongPress,
                                    onClick = { onAction(TransactionType.INCOME) }
                                ),
                            shape = RoundedCornerShape(14.dp),
                            color = AppColors.Primary
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = AppColors.OnPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Masuk",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.OnPrimary
                                )
                            }
                        }

                        // ↑ Keluar
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .bouncyClickable(
                                    pressedScale = 0.93f,
                                    hapticType = HapticFeedbackType.LongPress,
                                    onClick = { onAction(TransactionType.EXPENSE) }
                                ),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Keluar",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // ⇄ Transfer
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .bouncyClickable(
                                    pressedScale = 0.93f,
                                    hapticType = HapticFeedbackType.LongPress,
                                    onClick = { onAction(TransactionType.TRANSFER) }
                                ),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Transfer",
                                    style = MaterialTheme.typography.labelSmall,
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

    @Composable
    private fun SpaceToggle(isFamily: Boolean, onToggle: () -> Unit) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .bouncyClickable(
                    pressedScale = 0.98f,
                    hapticType = HapticFeedbackType.LongPress,
                    onClick = onToggle
                ),
            color = AppColors.SurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isFamily) AppColors.Primary else Color.Transparent)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pribadi",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (!isFamily) AppColors.OnPrimary else AppColors.TextMuted,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isFamily) AppColors.Primary else Color.Transparent)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ruang Keluarga",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isFamily) AppColors.OnPrimary else AppColors.TextMuted,
                        maxLines = 1
                    )
                }
            }
        }
    }

    @Composable
    private fun TransactionRowItem(
        transaction: Transaction,
        onClick: () -> Unit = {}
    ) {
        val isIncome = transaction.type == TransactionType.INCOME
        val isTransfer = transaction.type == TransactionType.TRANSFER
        val brand = KnownBrand.from(transaction.merchant.ifBlank { transaction.note })

        val (icon, tint) = when {
            isIncome -> Icons.AutoMirrored.Filled.TrendingUp to AppColors.Success
            isTransfer -> Icons.Default.Receipt to AppColors.Primary
            else -> Icons.AutoMirrored.Filled.TrendingDown to AppColors.TextMain
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .bouncyClickable(
                    pressedScale = 0.97f,
                    hapticType = HapticFeedbackType.LongPress,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(18.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (brand != null) Color(0xFFF9FAFB)
                                else tint.copy(alpha = 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (brand != null) {
                            BrandLogo(
                                brand = brand,
                                modifier = Modifier.size(22.dp),
                                tint = if (brand.isMonochrome) AppColors.TextMain else null
                            )
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = transaction.note.ifBlank { transaction.merchant.ifBlank { transaction.type.name } },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = transaction.date.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (isIncome) "+" else "-"}${transaction.amount.formatWithSymbol()}",
                        style = AppTypography.bodyNumeric(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isIncome) AppColors.Success else AppColors.TextMain
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (transaction.amount.currency != "IDR") {
                        Text(
                            text = CurrencyConverter.formatEstimatedIdr(transaction.amount),
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
        }
    }

    @Composable
    private fun EmptyWalletActivityState() {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Belum Ada Transaksi",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Aktivitas pembayaran atau transfer dari dompet ini akan muncul di sini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
