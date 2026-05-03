package com.spendwealthy.app.ui.screens.transaction.list

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Store
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
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.ui.theme.AppColors

class TransactionListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<TransactionListViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val income = state.filteredTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.amount }
        val spending = state.filteredTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.amount }

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 116.dp)
            ) {
                item { TransactionTopBar(onBack = { if (navigator.canPop) navigator.pop() }) }
                item {
                    IncomeSummaryCard(
                        totalIncome = Money(income, "IDR").format(),
                        totalSpending = Money(spending, "IDR").format()
                    )
                }
                item { TransactionsHeader() }
                items(
                    items = state.filteredTransactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }

    @Composable
    private fun TransactionTopBar(onBack: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleIcon(Icons.AutoMirrored.Filled.ArrowBack, onBack)
            Text(
                text = "Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMain
            )
            CircleIcon(Icons.Default.NotificationsNone)
        }
    }

    @Composable
    private fun CircleIcon(icon: ImageVector, onClick: (() -> Unit)? = null) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AppColors.SurfaceRaised)
                .border(1.dp, AppColors.Outline, CircleShape)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = AppColors.TextMain, modifier = Modifier.size(21.dp))
        }
    }

    @Composable
    private fun IncomeSummaryCard(totalIncome: String, totalSpending: String) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            shape = RoundedCornerShape(14.dp),
            color = AppColors.Accent
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("Income by", fontWeight = FontWeight.SemiBold, fontSize = 23.sp, color = AppColors.TextMain)
                        Text("Viewing last 6 months chart", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMain.copy(alpha = 0.62f))
                    }
                    Surface(shape = RoundedCornerShape(22.dp), color = Color.White.copy(alpha = 0.72f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monthly", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain)
                            Icon(Icons.Default.ExpandMore, null, tint = AppColors.TextMain, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.64f)).padding(vertical = 12.dp)) {
                    SummaryStat(Modifier.weight(1f), "Total Income", totalIncome)
                    SummaryStat(Modifier.weight(1f), "Total Spendings", totalSpending)
                }

                Spacer(Modifier.height(22.dp))

                SimpleBarChart(
                    data = listOf(0.62f, 0.32f, 0.86f, 0.56f, 0.58f, 0.68f),
                    modifier = Modifier.fillMaxWidth().height(112.dp)
                )
            }
        }
    }

    @Composable
    private fun SimpleBarChart(data: List<Float>, modifier: Modifier) {
        val labels = listOf("Jan", "Feb", "Mar", "April", "May", "June")
        Canvas(modifier = modifier) {
            val chartHeight = size.height - 24.dp.toPx()
            val barWidth = 34.dp.toPx()
            val spacing = (size.width - (data.size * barWidth)) / (data.size + 1)
            repeat(4) { index ->
                val y = chartHeight * (index + 1) / 5f
                drawLine(
                    color = AppColors.Primary.copy(alpha = 0.16f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
            data.forEachIndexed { index, value ->
                val barHeight = chartHeight * value.coerceIn(0.12f, 1f)
                val x = spacing + index * (barWidth + spacing)
                drawRoundRect(
                    color = AppColors.Primary,
                    topLeft = Offset(x, chartHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(label, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.7f))
            }
        }
    }

    @Composable
    private fun SummaryStat(modifier: Modifier, label: String, amount: String) {
        Column(modifier = modifier.padding(horizontal = 14.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.58f))
            Text(amount, fontWeight = FontWeight.Black, fontSize = 22.sp, color = AppColors.TextMain)
        }
    }

    @Composable
    private fun TransactionsHeader() {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = AppColors.TextMain)
            Text("See All  >", style = MaterialTheme.typography.labelMedium, color = AppColors.Secondary, fontWeight = FontWeight.SemiBold)
        }
    }

    @Composable
    private fun TransactionItem(transaction: Transaction) {
        val isIncome = transaction.type == TransactionType.INCOME
        val icon = when {
            isIncome -> Icons.Default.AccountBalance
            transaction.merchant.contains("netflix", ignoreCase = true) -> Icons.Default.Subscriptions
            transaction.merchant.contains("mcdonald", ignoreCase = true) -> Icons.Default.Fastfood
            else -> Icons.Default.Store
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(54.dp).clip(CircleShape).background(if (isIncome) AppColors.Primary else AppColors.CardDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (isIncome) AppColors.Accent else Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchant.ifBlank { transaction.note.ifBlank { "Transaction" } },
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain
                )
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
