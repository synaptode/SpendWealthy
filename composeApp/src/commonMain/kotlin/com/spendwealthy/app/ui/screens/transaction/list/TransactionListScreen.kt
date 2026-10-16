package com.spendwealthy.app.ui.screens.transaction.list

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.ui.theme.AppColors

/**
 * Modern, minimalist transaction ledger ("Buku Kas & Riwayat").
 * Features real-time search, category filtering, monthly cashflow summary, and clean typography.
 */
class TransactionListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<TransactionListViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current

        val totalIncome = state.filteredTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.amount }
        val totalExpense = state.filteredTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.amount }
        val netCashflow = totalIncome - totalExpense

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // 1. Header
                item {
                    LedgerHeader(
                        canGoBack = navigator.canPop,
                        onBack = { if (navigator.canPop) navigator.pop() }
                    )
                }

                // 2. Search Bar
                item {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                // 3. Cashflow Summary Card
                item {
                    SummaryCard(
                        income = Money(totalIncome, "IDR").format(),
                        expense = Money(totalExpense, "IDR").format(),
                        net = Money(netCashflow, "IDR").format(),
                        isSurplus = netCashflow >= 0,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                // 4. Category Filter Chips
                item {
                    CategoryFilterRow(
                        categories = state.categories,
                        selected = state.selectedCategory,
                        onSelect = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.onCategorySelected(it)
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // 5. Transaction Count Indicator
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Catatan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "${state.filteredTransactions.size} Transaksi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextMuted
                        )
                    }
                }

                // 6. Transactions List
                if (state.filteredTransactions.isEmpty()) {
                    item {
                        EmptyLedgerState(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                        )
                    }
                } else {
                    items(
                        items = state.filteredTransactions,
                        key = { it.id }
                    ) { transaction ->
                        LedgerItem(
                            transaction = transaction,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun LedgerHeader(canGoBack: Boolean, onBack: () -> Unit) {
        val haptic = LocalHapticFeedback.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canGoBack) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppColors.Surface)
                        .border(1.dp, AppColors.Outline.copy(alpha = 0.6f), CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onBack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = "Buku Kas & Riwayat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Pantau setiap rupiah yang keluar & masuk",
                    fontSize = 12.sp,
                    color = AppColors.TextMuted
                )
            }
        }
    }

    @Composable
    private fun SearchBar(
        query: String,
        onQueryChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(20.dp)
                )

                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = "Cari merchant, kategori, atau catatan...",
                            fontSize = 13.sp,
                            color = AppColors.TextMuted
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                if (query.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Hapus pencarian",
                        tint = AppColors.TextMuted,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onQueryChange("") }
                    )
                }
            }
        }
    }

    @Composable
    private fun SummaryCard(
        income: String,
        expense: String,
        net: String,
        isSurplus: Boolean,
        modifier: Modifier = Modifier
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, AppColors.Outline)
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
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSurplus) AppColors.Accent else AppColors.Danger)
                            )
                            Text(
                                text = "ARUS KAS BERSIH (NET)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMuted,
                                letterSpacing = 1.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = (if (isSurplus) AppColors.Success else AppColors.Danger).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, (if (isSurplus) AppColors.Success else AppColors.Danger).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (isSurplus) "Surplus Arus Kas" else "Defisit Arus Kas",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSurplus) AppColors.Success else AppColors.Danger,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = (if (isSurplus) "+ " else "") + net,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSurplus) AppColors.Success else AppColors.Danger
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.06f))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = AppColors.Success,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text("Pemasukan", fontSize = 12.sp, color = AppColors.TextMuted)
                            }
                            Text(
                                text = income,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = AppColors.Danger,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text("Pengeluaran", fontSize = 12.sp, color = AppColors.TextMuted)
                            }
                            Text(
                                text = expense,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CategoryFilterRow(
        categories: List<String>,
        selected: String,
        onSelect: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selected
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) AppColors.Primary else AppColors.Surface,
                    border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Outline.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onSelect(category) }
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else AppColors.TextMain,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun LedgerItem(
        transaction: Transaction,
        modifier: Modifier = Modifier
    ) {
        val isIncome = transaction.type == TransactionType.INCOME
        val isTransfer = transaction.type == TransactionType.TRANSFER

        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.45f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isIncome -> AppColors.Success.copy(alpha = 0.15f)
                                    isTransfer -> AppColors.Secondary.copy(alpha = 0.15f)
                                    else -> AppColors.Outline.copy(alpha = 0.45f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isIncome -> Icons.AutoMirrored.Filled.TrendingUp
                                isTransfer -> Icons.Default.SwapHoriz
                                else -> Icons.AutoMirrored.Filled.ReceiptLong
                            },
                            contentDescription = null,
                            tint = when {
                                isIncome -> AppColors.Success
                                isTransfer -> AppColors.Secondary
                                else -> AppColors.TextMain
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = transaction.note.ifBlank {
                                transaction.merchant.ifBlank {
                                    "Transaksi ${transaction.type.name.lowercase().replaceFirstChar { it.uppercase() }}"
                                }
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${transaction.date}",
                            fontSize = 11.sp,
                            color = AppColors.TextMuted
                        )
                    }
                }

                Text(
                    text = (if (isIncome) "+ " else if (isTransfer) "" else "- ") + transaction.amount.format(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isIncome -> AppColors.Success
                        isTransfer -> AppColors.Secondary
                        else -> AppColors.TextMain
                    }
                )
            }
        }
    }

    @Composable
    private fun EmptyLedgerState(modifier: Modifier = Modifier) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AppColors.PrimaryContainer.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Tidak Ada Transaksi Ditemukan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )

                Text(
                    text = "Coba ubah kata kunci pencarian atau pilih kategori lain",
                    fontSize = 12.sp,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
