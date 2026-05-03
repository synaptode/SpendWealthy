package com.spendwealthy.app.ui.screens.budget

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.ui.screens.settings.SettingsScreen
import com.spendwealthy.app.ui.screens.wallet.AddWalletScreen
import com.spendwealthy.app.ui.theme.AppColors

class BudgetListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BudgetListViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(bottom = 156.dp)
            ) {
                item { CardsHeader(onAdd = { navigator.parent?.push(AddWalletScreen()) }) }
                item { CardCarousel(state, viewModel, onClick = { navigator.parent?.push(AddWalletScreen()) }) }
                item {
                    CardControls(
                        onAddCard = { navigator.parent?.push(AddWalletScreen()) },
                        onFreeze = { navigator.parent?.push(SettingsScreen()) },
                        onSecurity = { navigator.parent?.push(SettingsScreen()) }
                    )
                }
                item { SectionTitle("Spending controls", "Add", onClick = { navigator.parent?.push(AddBudgetScreen()) }) }
                if (state.budgets.isEmpty()) {
                    item { EmptyCardState(onClick = { navigator.parent?.push(AddBudgetScreen()) }) }
                } else {
                    items(state.budgets, key = { it.id }) { budget ->
                        BudgetLimitRow(
                            title = viewModel.getBudgetName(budget),
                            budget = budget,
                            usage = viewModel.getUsage(budget),
                            remaining = viewModel.getRemaining(budget).format()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CardsHeader(onAdd: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Cards", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text("Manage wallets, limits, and card security", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMuted)
            }
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(AppColors.Primary).clickable(onClick = onAdd),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }

    @Composable
    private fun CardCarousel(state: BudgetListState, viewModel: BudgetListViewModel, onClick: () -> Unit) {
        val primaryBudget = state.budgets.firstOrNull()
        val balanceLabel = primaryBudget?.limit?.format() ?: "Rp0"
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                PaymentCard(
                    color = AppColors.CardBlue,
                    label = "Debit",
                    number = "**** **** **** 5690",
                    amount = balanceLabel,
                    footer = primaryBudget?.let { "Left ${viewModel.getRemaining(it).format()}" } ?: "Ready to use",
                    onClick = onClick
                )
            }
            item {
                PaymentCard(
                    color = AppColors.Accent,
                    label = "Savings",
                    number = "**** **** **** 2488",
                    amount = state.budgets.drop(1).firstOrNull()?.limit?.format() ?: "Rp0",
                    footer = "Protected balance",
                    onClick = onClick
                )
            }
        }
    }

    @Composable
    private fun PaymentCard(color: Color, label: String, number: String, amount: String, footer: String, onClick: () -> Unit) {
        Box(
            modifier = Modifier.width(276.dp).height(158.dp).clip(RoundedCornerShape(16.dp)).background(color).clickable(onClick = onClick).padding(16.dp)
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(Color.White.copy(alpha = 0.18f), radius = size.width * 0.34f, center = Offset(size.width * 0.82f, size.height * 0.1f))
                drawCircle(Color.White.copy(alpha = 0.1f), radius = size.width * 0.28f, center = Offset(size.width * 0.62f, size.height * 0.62f))
            }
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                        Spacer(Modifier.height(7.dp))
                        Icon(Icons.Default.CreditCard, null, tint = AppColors.TextMain, modifier = Modifier.size(22.dp))
                    }
                    Text("VISA", fontSize = 27.sp, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text("Card Number", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.62f))
                        Text(number, fontWeight = FontWeight.SemiBold, color = AppColors.TextMain, fontSize = 15.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(amount, fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 15.sp)
                        Text(footer, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.62f))
                    }
                }
            }
        }
    }

    @Composable
    private fun CardControls(onAddCard: () -> Unit, onFreeze: () -> Unit, onSecurity: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ControlAction(Icons.Default.Add, "Add Card", onAddCard)
            ControlAction(Icons.Default.Lock, "Freeze", onFreeze)
            ControlAction(Icons.Default.Shield, "Security", onSecurity)
        }
    }

    @Composable
    private fun ControlAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
        Column(modifier = Modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = CircleShape, color = AppColors.Surface, shadowElevation = 6.dp, border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)) {
                Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = AppColors.TextMain, modifier = Modifier.size(21.dp))
                }
            }
            Text(label, style = MaterialTheme.typography.labelMedium, color = AppColors.TextMain)
        }
    }

    @Composable
    private fun SectionTitle(title: String, action: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
            Text(action, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = AppColors.Secondary, modifier = Modifier.clickable(onClick = onClick))
        }
    }

    @Composable
    private fun BudgetLimitRow(title: String, budget: Budget, usage: Double, remaining: String) {
        val progress = usage.toFloat().coerceIn(0f, 1f)
        val accent = when {
            usage >= 0.9 -> AppColors.Danger
            usage >= 0.7 -> AppColors.Warning
            else -> AppColors.Primary
        }

        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 7.dp),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(CircleShape).background(accent.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Payments, null, tint = accent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(title, fontWeight = FontWeight.SemiBold, color = AppColors.TextMain)
                            Text("Remaining $remaining", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                        }
                    }
                    Text(budget.limit.format(), fontWeight = FontWeight.Black, color = AppColors.TextMain)
                }
                Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(AppColors.SurfaceMuted)) {
                    Box(Modifier.fillMaxWidth(progress).height(10.dp).clip(CircleShape).background(accent))
                }
            }
        }
    }

    @Composable
    private fun EmptyCardState(onClick: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(24.dp),
            color = AppColors.PrimaryContainer
        ) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(AppColors.CardDark), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Savings, null, tint = AppColors.Accent)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("No spending controls yet", fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text("Create a monthly budget to connect it with card limits.", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.68f))
                }
                Icon(Icons.Default.MoreHoriz, null, tint = AppColors.TextMain)
            }
        }
    }
}
