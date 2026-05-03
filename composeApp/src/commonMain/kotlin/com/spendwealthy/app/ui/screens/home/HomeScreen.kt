package com.spendwealthy.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.ui.screens.report.ReportScreen
import com.spendwealthy.app.ui.screens.transaction.AddTransactionScreen
import com.spendwealthy.app.ui.screens.transaction.list.TransactionListScreen
import com.spendwealthy.app.ui.screens.wallet.AddWalletScreen
import com.spendwealthy.app.ui.theme.AppColors

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<HomeViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(bottom = 156.dp)
            ) {
                item { HomeHeader(state.userName, state.userAvatarId) }
                item { BalanceBlock(state) }
                item { WalletCarousel(state, onClick = { navigator.parent?.push(AddWalletScreen()) }) }
                item {
                    QuickActions(
                        onTopUp = { navigator.parent?.push(AddTransactionScreen(TransactionType.INCOME)) },
                        onSend = { navigator.parent?.push(AddTransactionScreen(TransactionType.TRANSFER)) },
                        onRequest = { navigator.parent?.push(AddTransactionScreen(TransactionType.INCOME)) }
                    )
                }
                item { ConnectCardBanner(onClick = { navigator.parent?.push(AddWalletScreen()) }) }
                item { QuickSendStrip() }
                item { SectionHeader("Today, Dec 25", "All transactions", onClick = { navigator.parent?.push(TransactionListScreen()) }) }
                items(
                    items = state.recentTransactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionRow(transaction)
                }
            }
        }
    }

    @Composable
    private fun HomeHeader(name: String, avatarId: Int) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(listOf(AppColors.Primary, AppColors.CardBlue, AppColors.Accent, AppColors.Secondary)[avatarId % 4]),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).ifBlank { "A" }.uppercase(),
                    color = if (avatarId % 4 == 2) AppColors.TextMain else Color.White,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = name.ifBlank { "Alex Piter" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMain
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppColors.SurfaceRaised)
                    .border(1.dp, AppColors.Outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NotificationsNone, null, tint = AppColors.TextMain, modifier = Modifier.size(20.dp))
            }
        }
    }

    @Composable
    private fun BalanceBlock(state: HomeState) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text("Total Balance", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMain)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.totalBalance.format(),
                    fontSize = 34.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = AppColors.TextMain
                )
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Default.VisibilityOff, null, tint = AppColors.TextMuted, modifier = Modifier.size(18.dp))
            }
        }
    }

    @Composable
    private fun WalletCarousel(state: HomeState, onClick: () -> Unit) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                WalletCard(
                    modifier = Modifier.width(276.dp).height(158.dp),
                    color = AppColors.CardBlue,
                    title = "Debit",
                    amount = state.safeToSpend.format(),
                    number = "**** **** **** 5690",
                    valid = "07/30",
                    onClick = onClick
                )
            }
            item {
                WalletCard(
                    modifier = Modifier.width(276.dp).height(158.dp),
                    color = AppColors.Accent,
                    title = "Savings",
                    amount = state.totalBalance.format(),
                    number = "**** **** **** 2488",
                    valid = "11/29",
                    onClick = onClick
                )
            }
        }
    }

    @Composable
    private fun WalletCard(
        modifier: Modifier,
        color: Color,
        title: String,
        amount: String,
        number: String,
        valid: String,
        onClick: () -> Unit
    ) {
        Box(
            modifier = modifier.clip(RoundedCornerShape(16.dp)).background(color).clickable(onClick = onClick).padding(16.dp)
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(Color.White.copy(alpha = 0.18f), radius = size.width * 0.35f, center = Offset(size.width * 0.82f, size.height * 0.12f))
                drawCircle(Color.White.copy(alpha = 0.12f), radius = size.width * 0.28f, center = Offset(size.width * 0.66f, size.height * 0.58f))
            }
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, color = AppColors.TextMain, fontSize = 18.sp)
                        Spacer(Modifier.height(7.dp))
                        Icon(Icons.Default.CreditCard, null, tint = AppColors.TextMain, modifier = Modifier.size(22.dp))
                    }
                    Text("VISA", fontWeight = FontWeight.Black, fontSize = 27.sp, color = AppColors.TextMain)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text("Card Number", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.66f))
                        Text(number, fontWeight = FontWeight.SemiBold, color = AppColors.TextMain, fontSize = 15.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(amount, fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 15.sp)
                        Spacer(Modifier.height(3.dp))
                        Text("Valid", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.66f))
                        Text(valid, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    }
                }
            }
        }
    }

    @Composable
    private fun QuickActions(onTopUp: () -> Unit, onSend: () -> Unit, onRequest: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoundAction(Icons.Default.CreditCard, "Top Up", onTopUp)
            RoundAction(Icons.AutoMirrored.Filled.Send, "Send", onSend)
            RoundAction(Icons.Default.Add, "Request", onRequest)
        }
    }

    @Composable
    private fun RoundAction(icon: ImageVector, label: String, onClick: () -> Unit) {
        Column(
            modifier = Modifier.clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(shape = CircleShape, color = AppColors.Surface, border = BorderStroke(1.dp, AppColors.Outline), shadowElevation = 6.dp) {
                Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = AppColors.TextMain, modifier = Modifier.size(21.dp))
                }
            }
            Text(label, style = MaterialTheme.typography.labelMedium, color = AppColors.TextMain)
        }
    }

    @Composable
    private fun ConnectCardBanner(onClick: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.PrimaryContainer
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(AppColors.CardDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Link, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Connect Your Card", fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text("The smarter way to pay your money", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.68f))
                }
                Text(">", color = AppColors.TextMain, fontSize = 24.sp, fontWeight = FontWeight.Light)
            }
        }
    }

    @Composable
    private fun QuickSendStrip() {
        val people = listOf("Add", "Jane", "Kristin", "Hawkins", "Darlene")
        Column(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Text("Quick Send", modifier = Modifier.padding(horizontal = 24.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(people) { name ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (name == "Add") AppColors.Surface else listOf(AppColors.CardBlue, AppColors.Accent, AppColors.PrimaryContainer, AppColors.SecondaryContainer)[name.length % 4])
                                .border(1.dp, AppColors.Outline, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (name == "Add") {
                                Icon(Icons.Default.Add, null, tint = AppColors.Secondary)
                            } else {
                                Text(name.take(1), fontWeight = FontWeight.Black, color = AppColors.TextMain)
                            }
                        }
                        Text(name, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun SectionHeader(title: String, action: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
            Text(
                "$action  >",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMain,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
    }

    @Composable
    private fun TransactionRow(transaction: Transaction) {
        val isIncome = transaction.type == TransactionType.INCOME
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(if (isIncome) AppColors.Primary else AppColors.CardDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(if (isIncome) Icons.Default.AccountBalance else Icons.Default.ShoppingBag, null, tint = if (isIncome) AppColors.Accent else Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(transaction.merchant.ifBlank { transaction.note.ifBlank { "Transaction" } }, fontWeight = FontWeight.SemiBold, color = AppColors.TextMain)
                Text(transaction.date.toString(), style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isIncome) "+" else "-"}${transaction.amount.format()}",
                    fontWeight = FontWeight.Black,
                    color = if (isIncome) AppColors.Success else AppColors.TextMain
                )
                Text(if (isIncome) "Income" else "Payment", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
            }
        }
    }
}
