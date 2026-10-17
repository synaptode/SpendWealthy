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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.ui.components.EmptyState
import com.spendwealthy.app.ui.theme.AppColors

class WalletListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<WalletListViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current

        val totalLiquidity = state.wallets
            .filter { it.type != WalletType.PAYLATER && it.type != WalletType.CREDIT_CARD }
            .sumOf { it.balance.amount }

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    WalletHeader(
                        onBack = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (navigator.canPop) navigator.pop()
                        },
                        onAdd = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            navigator.push(AddWalletScreen())
                        }
                    )
                }

                if (state.wallets.isNotEmpty()) {
                    item {
                        LiquiditySummaryCard(
                            totalFormatted = Money(totalLiquidity, "IDR").format(),
                            walletCount = state.wallets.size
                        )
                    }
                    item {
                        Text(
                            text = "Semua Rekening & Dompet",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (state.wallets.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Belum Ada Dompet",
                            message = "Hubungkan rekening bank, e-wallet, atau catat saldo tunai untuk mulai mengelola keuangan.",
                            icon = Icons.Default.AccountBalanceWallet
                        )
                    }
                } else {
                    items(state.wallets, key = { it.id }) { wallet ->
                        WalletListItem(
                            wallet = wallet,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                navigator.push(WalletDetailScreen(wallet.id))
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WalletHeader(onBack: () -> Unit, onAdd: () -> Unit) {
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

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Daftar Dompet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Kelola rekening & sumber dana",
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
                        contentDescription = "Tambah Dompet",
                        tint = Color(0xFF090D16),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun LiquiditySummaryCard(totalFormatted: String, walletCount: Int) {
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
                                    .background(Color(0xFFFFDE43))
                            )
                            Text(
                                text = "TOTAL SALDO LIKUID",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = totalFormatted,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFDE43)
                    ) {
                        Text(
                            text = "$walletCount akun aktif",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF111827),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WalletListItem(wallet: Wallet, onClick: () -> Unit) {
        val isDebt = wallet.type == WalletType.PAYLATER || wallet.type == WalletType.CREDIT_CARD
        val (icon, typeLabel) = when (wallet.type) {
            WalletType.BANK -> Icons.Default.AccountBalance to "Rekening Bank"
            WalletType.EWALLET -> Icons.Default.AccountBalanceWallet to "Dompet Digital"
            WalletType.CREDIT_CARD -> Icons.Default.CreditCard to "Kartu Kredit"
            WalletType.PAYLATER -> Icons.Default.CreditCard to "Paylater"
            WalletType.SAVINGS -> Icons.Default.Savings to "Tabungan Terkunci"
            WalletType.CASH -> Icons.Default.AccountBalanceWallet to "Uang Tunai"
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(20.dp),
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isDebt) AppColors.Danger.copy(alpha = 0.12f)
                                else AppColors.Primary.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isDebt) AppColors.Danger else AppColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = wallet.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = typeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = wallet.balance.format(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDebt && wallet.balance.amount < 0) AppColors.Danger else AppColors.TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AppColors.TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
