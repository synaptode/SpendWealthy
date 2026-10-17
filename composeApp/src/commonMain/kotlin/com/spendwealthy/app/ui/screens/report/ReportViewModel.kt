package com.spendwealthy.app.ui.screens.report

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiEffect
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.core.util.monthRange
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.usecase.ExportMonthlyTransactionsCsvUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import com.spendwealthy.app.core.util.pastMonthsRange
import com.spendwealthy.app.core.util.yearRange
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.repository.WalletRepository

enum class PeriodPreset(val label: String) {
    THIS_MONTH("Bulan Ini"),
    LAST_MONTH("Bulan Lalu"),
    LAST_3_MONTHS("3 Bulan"),
    THIS_YEAR("Tahun Ini")
}

data class ReportState(
    val categorySummaries: List<CategorySummary> = emptyList(),
    val incomeCategorySummaries: List<CategorySummary> = emptyList(),
    val totalIncome: Money = Money.zero(),
    val totalExpense: Money = Money.zero(),
    val savingRate: Double = 0.0,
    val averageDailyExpense: Money = Money.zero(),
    val topExpenseCategoryName: String? = null,
    val totalTransactionsCount: Int = 0,
    val insight: String = "Belum ada transaksi bulan ini.",
    val exportInProgress: Boolean = false,
    val isLoading: Boolean = false,
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 10,
    val selectedPeriodPreset: PeriodPreset = PeriodPreset.THIS_MONTH,
    val wallets: List<Wallet> = emptyList(),
    val selectedWalletId: String? = null
) : UiState {
    val selectedMonthName: String
        get() = when (selectedMonth) {
            1 -> "Januari"
            2 -> "Februari"
            3 -> "Maret"
            4 -> "April"
            5 -> "Mei"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "Agustus"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Desember"
            else -> "Bulan Ini"
        }

    val selectedWalletName: String
        get() = wallets.firstOrNull { it.id == selectedWalletId }?.name ?: "Semua Dompet"
}

sealed class ReportEffect : UiEffect {
    data class ShowMessage(val message: String) : ReportEffect()
}

data class CategorySummary(
    val categoryId: String?,
    val categoryName: String,
    val amount: Money,
    val percentage: Double,
    val color: androidx.compose.ui.graphics.Color
)

class ReportViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val walletRepository: WalletRepository,
    private val exportMonthlyTransactionsCsvUseCase: ExportMonthlyTransactionsCsvUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(ReportState())
    val uiState: StateFlow<ReportState> = _uiState.asStateFlow()
    private val _effect = Channel<ReportEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val currentMonth = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    private val chartColors = listOf(
        androidx.compose.ui.graphics.Color(0xFF818CF8), // Indigo
        androidx.compose.ui.graphics.Color(0xFF34D399), // Emerald
        androidx.compose.ui.graphics.Color(0xFFFBBF24), // Amber
        androidx.compose.ui.graphics.Color(0xFFF87171), // Red
        androidx.compose.ui.graphics.Color(0xFF60A5FA), // Blue
        androidx.compose.ui.graphics.Color(0xFFA78BFA), // Violet
        androidx.compose.ui.graphics.Color(0xFF4ADE80), // Green
        androidx.compose.ui.graphics.Color(0xFFF472B6)  // Pink
    )

    private var selectedYear = currentMonth.year
    private var selectedMonth = currentMonth.monthNumber
    private var selectedPeriod: PeriodPreset = PeriodPreset.THIS_MONTH
    private var selectedWalletId: String? = null

    init {
        loadReport()
    }

    fun setMonth(year: Int, month: Int) {
        selectedYear = year
        selectedMonth = month
        selectedPeriod = PeriodPreset.THIS_MONTH
        loadReport()
    }

    fun setPeriodPreset(preset: PeriodPreset) {
        selectedPeriod = preset
        val now = currentMonth
        when (preset) {
            PeriodPreset.THIS_MONTH -> {
                selectedYear = now.year
                selectedMonth = now.monthNumber
            }
            PeriodPreset.LAST_MONTH -> {
                if (now.monthNumber == 1) {
                    selectedYear = now.year - 1
                    selectedMonth = 12
                } else {
                    selectedYear = now.year
                    selectedMonth = now.monthNumber - 1
                }
            }
            PeriodPreset.LAST_3_MONTHS -> {
                selectedYear = now.year
                selectedMonth = now.monthNumber
            }
            PeriodPreset.THIS_YEAR -> {
                selectedYear = now.year
                selectedMonth = now.monthNumber
            }
        }
        loadReport()
    }

    fun selectWallet(walletId: String?) {
        selectedWalletId = walletId
        loadReport()
    }

    private fun loadReport() {
        val range = when (selectedPeriod) {
            PeriodPreset.THIS_MONTH -> monthRange(selectedYear, selectedMonth)
            PeriodPreset.LAST_MONTH -> monthRange(selectedYear, selectedMonth)
            PeriodPreset.LAST_3_MONTHS -> pastMonthsRange(selectedYear, selectedMonth, 3)
            PeriodPreset.THIS_YEAR -> yearRange(selectedYear)
        }
        val daysInPeriod = (range.endInclusive.toEpochDays() - range.start.toEpochDays() + 1).coerceAtLeast(1)

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                selectedPeriodPreset = selectedPeriod,
                selectedWalletId = selectedWalletId
            )

            combine(
                transactionRepository.getTransactionsByDateRange(range.start, range.endInclusive),
                categoryRepository.getCategories(),
                walletRepository.getWallets()
            ) { allTransactions, categories, wallets ->
                val transactions = if (selectedWalletId != null) {
                    allTransactions.filter { it.walletId == selectedWalletId }
                } else {
                    allTransactions
                }
                val categoryMap = categories.associateBy { it.id }
                val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
                val incomes = transactions.filter { it.type == TransactionType.INCOME }
                val totalExpense = expenses.sumOf { it.amount.amount }
                val totalIncome = incomes.sumOf { it.amount.amount }
                val savingRate = if (totalIncome > 0L) {
                    (totalIncome - totalExpense).toDouble() / totalIncome.toDouble()
                } else {
                    0.0
                }
                val avgDaily = totalExpense / daysInPeriod

                val summaries = expenses.groupBy { it.categoryId }.entries.mapIndexed { index, (catId, trans) ->
                    val sum = trans.sumOf { it.amount.amount }
                    val category = categoryMap[catId]
                    CategorySummary(
                        categoryId = catId,
                        categoryName = category?.name ?: "Lainnya",
                        amount = Money(sum, "IDR"),
                        percentage = if (totalExpense > 0) sum.toDouble() / totalExpense.toDouble() else 0.0,
                        color = chartColors[index % chartColors.size]
                    )
                }.sortedByDescending { it.amount.amount }

                val incomeSummaries = incomes.groupBy { it.categoryId }.entries.mapIndexed { index, (catId, trans) ->
                    val sum = trans.sumOf { it.amount.amount }
                    val category = categoryMap[catId]
                    CategorySummary(
                        categoryId = catId,
                        categoryName = category?.name ?: "Pemasukan",
                        amount = Money(sum, "IDR"),
                        percentage = if (totalIncome > 0) sum.toDouble() / totalIncome.toDouble() else 0.0,
                        color = chartColors[index % chartColors.size]
                    )
                }.sortedByDescending { it.amount.amount }

                val topExpense = summaries.firstOrNull()?.categoryName

                val periodLabel = when (selectedPeriod) {
                    PeriodPreset.THIS_MONTH -> "bulan ini"
                    PeriodPreset.LAST_MONTH -> "bulan lalu"
                    PeriodPreset.LAST_3_MONTHS -> "3 bulan terakhir"
                    PeriodPreset.THIS_YEAR -> "tahun ini"
                }

                val insight = when {
                    transactions.isEmpty() -> "Belum ada transaksi pada $periodLabel."
                    totalIncome <= 0L -> "Pengeluaran tercatat ${Money(totalExpense, "IDR").formatWithSymbol()} pada $periodLabel."
                    savingRate >= 0.2 -> "Bagus, saving rate kamu ${(savingRate * 100).toInt()}% pada $periodLabel."
                    savingRate >= 0.0 -> "Saving rate kamu ${(savingRate * 100).toInt()}%. Masih aman, pertahankan!"
                    else -> "Pengeluaran melebihi pemasukan pada $periodLabel. Cek kategori $topExpense untuk evaluasi."
                }

                ReportState(
                    categorySummaries = summaries,
                    incomeCategorySummaries = incomeSummaries,
                    totalIncome = Money(totalIncome, "IDR"),
                    totalExpense = Money(totalExpense, "IDR"),
                    savingRate = savingRate,
                    averageDailyExpense = Money(avgDaily, "IDR"),
                    topExpenseCategoryName = topExpense,
                    totalTransactionsCount = transactions.size,
                    insight = insight,
                    exportInProgress = _uiState.value.exportInProgress,
                    isLoading = false,
                    selectedYear = selectedYear,
                    selectedMonth = selectedMonth,
                    selectedPeriodPreset = selectedPeriod,
                    wallets = wallets,
                    selectedWalletId = selectedWalletId
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }

    fun exportCsv() {
        val range = when (selectedPeriod) {
            PeriodPreset.THIS_MONTH -> monthRange(selectedYear, selectedMonth)
            PeriodPreset.LAST_MONTH -> monthRange(selectedYear, selectedMonth)
            PeriodPreset.LAST_3_MONTHS -> pastMonthsRange(selectedYear, selectedMonth, 3)
            PeriodPreset.THIS_YEAR -> yearRange(selectedYear)
        }
        val label = "laporan-${selectedPeriod.name.lowercase()}-$selectedYear"

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(exportInProgress = true)
            when (val result = exportMonthlyTransactionsCsvUseCase.exportRange(
                startDate = range.start,
                endDate = range.endInclusive,
                label = label,
                walletId = selectedWalletId
            )) {
                is GatewayResult.Failure -> _effect.send(ReportEffect.ShowMessage(result.message))
                is GatewayResult.Success -> _effect.send(ReportEffect.ShowMessage("Laporan CSV siap dibagikan."))
            }
            _uiState.value = _uiState.value.copy(exportInProgress = false)
        }
    }

    fun exportJson() {
        screenModelScope.launch {
            _effect.send(ReportEffect.ShowMessage("JSON export is not available in MVP yet. CSV export is ready."))
        }
    }
}
