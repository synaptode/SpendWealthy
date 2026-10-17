package com.spendwealthy.app.ui.screens.report

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.components.tactilePress
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
import androidx.compose.ui.graphics.luminance
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
import com.spendwealthy.app.ui.components.CategoryPieChart
import com.spendwealthy.app.ui.components.MonthPickerModal
import com.spendwealthy.app.ui.components.PieChartData
import com.spendwealthy.app.ui.theme.AppColors

class ReportScreen(
    private val onBackClick: (() -> Unit)? = null
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<ReportViewModel>()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val haptic = LocalHapticFeedback.current
        var selectedTab by remember { mutableStateOf(1) } // 0: Income, 1: Expense (Default Expense to match Reference 4)
        var showMonthPicker by remember { mutableStateOf(false) }
        var showWalletPicker by remember { mutableStateOf(false) }
        val navigator = LocalNavigator.currentOrThrow
        val canGoBack = onBackClick != null || navigator.canPop

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ReportEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        if (showMonthPicker) {
            MonthPickerModal(
                selectedYear = state.selectedYear,
                selectedMonth = state.selectedMonth,
                onMonthSelected = { year, month ->
                    if (year != null && month != null) {
                        viewModel.setMonth(year, month)
                    }
                },
                onDismiss = { showMonthPicker = false },
                allowAll = false
            )
        }

        if (showWalletPicker) {
            WalletPickerBottomSheet(
                wallets = state.wallets,
                selectedWalletId = state.selectedWalletId,
                onSelectWallet = { walletId ->
                    viewModel.selectWallet(walletId)
                    showWalletPicker = false
                },
                onDismiss = { showWalletPicker = false }
            )
        }

        Scaffold(
            containerColor = AppColors.Background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppColors.Background)
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = if (canGoBack) 40.dp else 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Header (Statistics + Wallet Filter + Month Selector + CSV Export)
                item {
                    ReportHeader(
                        canGoBack = canGoBack,
                        onBack = {
                            if (onBackClick != null) {
                                onBackClick.invoke()
                            } else if (navigator.canPop) {
                                navigator.pop()
                            }
                        },
                        selectedMonthName = state.selectedMonthName,
                        selectedWalletName = state.selectedWalletName,
                        showMonthPicker = state.selectedPeriodPreset == PeriodPreset.THIS_MONTH || state.selectedPeriodPreset == PeriodPreset.LAST_MONTH,
                        onSelectMonth = { showMonthPicker = true },
                        onSelectWallet = { showWalletPicker = true },
                        onExport = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.exportCsv()
                        },
                        loading = state.exportInProgress
                    )
                }

                // 1.5. Period Preset Toggle (Bulan Ini, Bulan Lalu, 3 Bulan, Tahun Ini)
                item {
                    PeriodPresetToggle(
                        selectedPreset = state.selectedPeriodPreset,
                        onPresetSelected = { preset ->
                            viewModel.setPeriodPreset(preset)
                        }
                    )
                }

                // 1.8. Key Financial KPI Summary Card
                item {
                    KeyMetricsCard(state = state)
                }

                // 2. Income / Expense Pill Tab Toggle (Matching Reference 4)
                item {
                    IncomeExpenseToggle(
                        selectedTab = selectedTab,
                        onTabSelected = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedTab = it
                        }
                    )
                }

                val isIncomeSelected = selectedTab == 0
                val activeSummaries = if (isIncomeSelected) state.incomeCategorySummaries else state.categorySummaries
                val centerTitle = if (isIncomeSelected) "Total Income" else "Total Expense"

                // 3. Segmented Donut Chart Card (Matching Reference 4: Total Expense/Income + Donut)
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = AppColors.Surface,
                        border = BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (activeSummaries.isNotEmpty()) {
                                val subtitle = if (isIncomeSelected) {
                                    "${activeSummaries.size} Sumber"
                                } else {
                                    if (state.totalIncome.amount > 0) {
                                        val remainingPercent = kotlin.math.max(0, 100 - (state.totalExpense.amount * 100 / state.totalIncome.amount).toInt())
                                        "$remainingPercent% Sisa Tabungan"
                                    } else {
                                        "${activeSummaries.size} Kategori"
                                    }
                                }
                                CategoryPieChart(
                                    data = activeSummaries.map {
                                        PieChartData(it.amount.amount.toFloat(), it.color, it.categoryName)
                                    },
                                    centerTitle = centerTitle,
                                    subtitleBadge = subtitle
                                )
                            } else {
                                EmptyReportState(isIncome = isIncomeSelected)
                            }
                        }
                    }
                }

                // 4. Cashflow Overview Card (Clean Light Minimalist Card)
                item { CashflowOverviewCard(state) }

                // 5. Financial Insight Banner
                item { FinancialInsightBanner(state.insight) }

                // 6. Category Breakdown List
                if (activeSummaries.isNotEmpty()) {
                    item {
                        SectionTitle(if (isIncomeSelected) "Sumber Pemasukan" else "Rincian per Kategori")
                    }
                    items(activeSummaries, key = { it.categoryId ?: it.categoryName }) { summary ->
                        CategorySpendingRow(summary, isIncome = isIncomeSelected)
                    }
                }
            }
        }
    }

    @Composable
    private fun ReportHeader(
        canGoBack: Boolean,
        onBack: () -> Unit,
        selectedMonthName: String,
        selectedWalletName: String,
        showMonthPicker: Boolean,
        onSelectMonth: () -> Unit,
        onSelectWallet: () -> Unit,
        onExport: () -> Unit,
        loading: Boolean
    ) {
        val haptic = LocalHapticFeedback.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)
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

                Text(
                    text = "Statistik",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Wallet Selector Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Outline),
                    modifier = Modifier.clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSelectWallet()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = selectedWalletName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Pilih Dompet",
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Month Selector Pill (Only shown for monthly presets)
                if (showMonthPicker) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AppColors.Surface,
                        border = BorderStroke(1.dp, AppColors.Outline),
                        modifier = Modifier.clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelectMonth()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = selectedMonthName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.TextMain
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Pilih Bulan",
                                tint = AppColors.TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // CSV Export Action
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !loading, onClick = onExport),
                    shape = CircleShape,
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Outline)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AppColors.Primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "Ekspor CSV",
                                tint = AppColors.TextMain,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Income / Expense Pill Tab Toggle matching Reference 4.
     */
    @Composable
    private fun IncomeExpenseToggle(
        selectedTab: Int,
        onTabSelected: (Int) -> Unit
    ) {
        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
        val containerBg = if (isDark) AppColors.SurfaceRaised else Color(0xFFF3F4F6)
        val activeBg = if (isDark) AppColors.Primary else Color(0xFF111827)
        val activeTextColor = if (isDark) Color(0xFF10140D) else Color.White

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = containerBg
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Income Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == 0) activeBg else Color.Transparent)
                        .clickable { onTabSelected(0) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Income",
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 0) activeTextColor else AppColors.TextMuted
                    )
                }

                // Expense Tab (Active in Ref 4)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == 1) activeBg else Color.Transparent)
                        .clickable { onTabSelected(1) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Expense",
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 1) activeTextColor else AppColors.TextMuted
                    )
                }
            }
        }
    }

    @Composable
    private fun CashflowOverviewCard(state: ReportState) {
        val savingRatePercent = (state.savingRate * 100).toInt()
        val savingRateColor = if (savingRatePercent >= 20) AppColors.Success else AppColors.Warning

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AppColors.Accent)
                        )
                        Text(
                            text = "RINGKASAN ARUS KAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMuted,
                            letterSpacing = 0.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = savingRateColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, savingRateColor.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "Rasio Tabungan: $savingRatePercent%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = savingRateColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CashflowStatBox(
                        title = "Pemasukan",
                        amount = state.totalIncome.format(),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        tint = AppColors.Success,
                        modifier = Modifier.weight(1f)
                    )
                    CashflowStatBox(
                        title = "Pengeluaran",
                        amount = state.totalExpense.format(),
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        tint = AppColors.Danger,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    @Composable
    private fun CashflowStatBox(
        title: String,
        amount: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        tint: Color,
        modifier: Modifier = Modifier
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            color = AppColors.SurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                }
                Text(
                    text = amount,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    @Composable
    private fun FinancialInsightBanner(insight: String) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = AppColors.Primary.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = insight,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMain,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    private fun SectionTitle(title: String) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextMuted,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    @Composable
    private fun CategorySpendingRow(summary: CategorySummary, isIncome: Boolean = false) {
        val percentageInt = (summary.percentage * 100).toInt()
        val animatedProgress by animateFloatAsState(
            targetValue = summary.percentage.toFloat().coerceIn(0f, 1f),
            animationSpec = tween(500)
        )

        // Dynamic vector icon matching Reference 4 categories
        val icon = when {
            summary.categoryName.contains("bisnis", ignoreCase = true) ||
            summary.categoryName.contains("business", ignoreCase = true) ||
            summary.categoryName.contains("kerja", ignoreCase = true) -> Icons.Default.Business

            summary.categoryName.contains("makan", ignoreCase = true) ||
            summary.categoryName.contains("food", ignoreCase = true) ||
            summary.categoryName.contains("resto", ignoreCase = true) -> Icons.Default.Restaurant

            summary.categoryName.contains("edukasi", ignoreCase = true) ||
            summary.categoryName.contains("pendidikan", ignoreCase = true) ||
            summary.categoryName.contains("education", ignoreCase = true) ||
            summary.categoryName.contains("kursus", ignoreCase = true) -> Icons.Default.School

            summary.categoryName.contains("belanja", ignoreCase = true) ||
            summary.categoryName.contains("shop", ignoreCase = true) -> Icons.Default.ShoppingCart

            else -> Icons.Default.Category
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(summary.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = summary.color,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = summary.categoryName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "$percentageInt% of total ${if (isIncome) "income" else "expense"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Text(
                        text = summary.amount.format(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AppColors.TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                val trackBg = if (isDark) AppColors.Dark.Surface else Color(0xFFF3F4F6)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(trackBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(summary.color)
                    )
                }
            }
        }
    }

    @Composable
    private fun EmptyReportState(isIncome: Boolean = false) {
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
                    imageVector = Icons.Default.PieChart,
                    contentDescription = null,
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = if (isIncome) "Belum Ada Pemasukan" else "Belum Ada Pengeluaran",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMain
                )
                Text(
                    text = if (isIncome)
                        "Transaksi pemasukan bulan ini akan otomatis dikelompokkan dalam grafik dan persentase di sini."
                    else
                        "Transaksi pengeluaran bulan ini akan otomatis dikelompokkan dalam grafik dan persentase di sini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun PeriodPresetToggle(
        selectedPreset: PeriodPreset,
        onPresetSelected: (PeriodPreset) -> Unit
    ) {
        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
        val haptic = LocalHapticFeedback.current

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodPreset.entries.forEach { preset ->
                val isSelected = preset == selectedPreset
                val activeBg = if (isDark) AppColors.Primary else Color(0xFF111827)
                val activeTextColor = if (isDark) Color(0xFF10140D) else Color.White
                val inactiveBg = if (isDark) AppColors.SurfaceRaised else Color(0xFFF3F4F6)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) activeBg else inactiveBg)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onPresetSelected(preset)
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = preset.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) activeTextColor else AppColors.TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    @Composable
    private fun KeyMetricsCard(state: ReportState) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Metric 1: Avg Daily Expense
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Rata-rata / Hari",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.averageDailyExpense.formatWithSymbol(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(AppColors.Outline)
                )

                // Metric 2: Top Expense Category
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kategori Terbesar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.topExpenseCategoryName ?: "-",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(AppColors.Outline)
                )

                // Metric 3: Total Transactions Count
                Column(
                    modifier = Modifier.weight(0.9f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Volume",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${state.totalTransactionsCount} Trx",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun WalletPickerBottomSheet(
        wallets: List<com.spendwealthy.app.domain.model.Wallet>,
        selectedWalletId: String?,
        onSelectWallet: (String?) -> Unit,
        onDismiss: () -> Unit
    ) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = AppColors.Surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(AppColors.Outline)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Pilih Dompet / Akun",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )

                // Option: "Semua Dompet"
                val isAllSelected = selectedWalletId == null
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectWallet(null) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isAllSelected) AppColors.Primary.copy(alpha = 0.12f) else AppColors.SurfaceRaised,
                    border = BorderStroke(1.dp, if (isAllSelected) AppColors.Primary else AppColors.Outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Semua Dompet",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.TextMain
                                )
                                Text(
                                    text = "Akumulasi seluruh akun keuangan",
                                    fontSize = 11.sp,
                                    color = AppColors.TextMuted
                                )
                            }
                        }
                        if (isAllSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Terpilih",
                                tint = AppColors.Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Individual Wallets
                wallets.forEach { wallet ->
                    val isWalletSelected = wallet.id == selectedWalletId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectWallet(wallet.id) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isWalletSelected) AppColors.Primary.copy(alpha = 0.12f) else AppColors.SurfaceRaised,
                        border = BorderStroke(1.dp, if (isWalletSelected) AppColors.Primary else AppColors.Outline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.Outline.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = AppColors.TextMain,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = wallet.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.TextMain
                                    )
                                    Text(
                                        text = wallet.balance.formatWithSymbol(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.TextMuted
                                    )
                                }
                            }
                            if (isWalletSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Terpilih",
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
