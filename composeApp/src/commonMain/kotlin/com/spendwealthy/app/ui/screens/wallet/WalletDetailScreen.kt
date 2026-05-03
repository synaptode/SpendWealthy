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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.ui.theme.AppColors
import org.koin.core.parameter.parametersOf

data class WalletDetailScreen(val walletId: String) : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<WalletDetailViewModel> { parametersOf(walletId) }
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { DetailHeader(title = state.wallet?.name ?: "Wallet Detail", onBack = { navigator.pop() }) }
                state.wallet?.let { wallet ->
                    item {
                        Surface(shape = RoundedCornerShape(24.dp), color = AppColors.CardBlue) {
                            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Current balance", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMain.copy(alpha = 0.7f))
                                Text(wallet.balance.format(), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                                Text(wallet.type.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelMedium, color = AppColors.TextMain.copy(alpha = 0.72f), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                item { SectionTitle("Recent activity") }
                if (state.transactions.isEmpty()) {
                    item {
                        Surface(shape = RoundedCornerShape(20.dp), color = AppColors.Surface) {
                            Text("No transactions recorded for this wallet yet.", modifier = Modifier.padding(16.dp), color = AppColors.TextMuted, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    items(state.transactions, key = { it.id }) { transaction ->
                        TransactionRow(transaction)
                    }
                }
            }
        }
    }

    @Composable
    private fun DetailHeader(title: String, onBack: () -> Unit) {
        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised).border(1.dp, AppColors.Outline, CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.TextMain, modifier = Modifier.size(20.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text("Balance and activity", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMuted)
            }
            Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AccountBalanceWallet, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
            }
        }
    }

    @Composable
    private fun SectionTitle(title: String) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain, modifier = Modifier.padding(top = 8.dp))
    }

    @Composable
    private fun TransactionRow(transaction: Transaction) {
        val isIncome = transaction.type == TransactionType.INCOME
        Surface(shape = RoundedCornerShape(20.dp), color = AppColors.Surface, shadowElevation = 4.dp) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(if (isIncome) AppColors.Primary.copy(alpha = 0.12f) else AppColors.CardDark), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ShoppingBag, null, tint = if (isIncome) AppColors.Primary else Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(transaction.note.ifBlank { transaction.merchant.ifBlank { transaction.type.name.lowercase().replaceFirstChar { it.uppercase() } } }, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                        Text(transaction.date.toString(), style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                    }
                }
                Text("${if (isIncome) "+" else "-"}${transaction.amount.format()}", fontWeight = FontWeight.Black, color = if (isIncome) AppColors.Success else AppColors.TextMain)
            }
        }
    }
}
