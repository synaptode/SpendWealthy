package com.spendwealthy.app.ui.screens.wallet

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { WalletHeader(onAdd = { navigator.push(AddWalletScreen()) }) }
                if (state.wallets.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No wallets yet",
                            message = "Create a wallet to track cash, bank, cards, and savings.",
                            icon = Icons.Default.AccountBalanceWallet
                        )
                    }
                } else {
                    items(state.wallets, key = { it.id }) { wallet ->
                        WalletItem(wallet) { navigator.push(WalletDetailScreen(wallet.id)) }
                    }
                }
            }
        }
    }

    @Composable
    private fun WalletHeader(onAdd: () -> Unit) {
        Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Wallets", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text("All balances and payment sources", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMuted)
            }
            Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.Primary).clickable(onClick = onAdd), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }

    @Composable
    private fun WalletItem(wallet: Wallet, onClick: () -> Unit) {
        val isDebt = wallet.type == WalletType.PAYLATER || wallet.type == WalletType.CREDIT_CARD
        val accent = when {
            isDebt && wallet.balance.amount < 0 -> AppColors.Danger
            wallet.type == WalletType.SAVINGS -> AppColors.Secondary
            else -> AppColors.Primary
        }
        val icon = when (wallet.type) {
            WalletType.CREDIT_CARD, WalletType.PAYLATER -> Icons.Default.CreditCard
            WalletType.SAVINGS -> Icons.Default.Savings
            else -> Icons.Default.AccountBalanceWallet
        }

        Surface(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.Surface,
            shadowElevation = 6.dp
        ) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(Modifier.size(48.dp).clip(CircleShape).background(accent.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(wallet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                        Text(wallet.type.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                    }
                }
                Text(wallet.balance.format(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = accent)
            }
        }
    }
}
