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

data class ReportState(
    val categorySummaries: List<CategorySummary> = emptyList(),
    val totalIncome: Money = Money.zero(),
    val totalExpense: Money = Money.zero(),
    val savingRate: Double = 0.0,
    val insight: String = "Belum ada transaksi bulan ini.",
    val exportInProgress: Boolean = false,
    val isLoading: Boolean = false
) : UiState

sealed class ReportEffect : UiEffect {
    data class ShowMessage(val message: String) : ReportEffect()
}

data class CategorySummary(
    val categoryId: String?,
    val categoryName: String,
    val amount: Money,
    val percentage: Double,
    val color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Gray
)

class ReportViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val exportMonthlyTransactionsCsvUseCase: ExportMonthlyTransactionsCsvUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(ReportState())
    val uiState: StateFlow<ReportState> = _uiState.asStateFlow()
    private val _effect = Channel<ReportEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val currentMonth = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    init {
        loadReport()
    }

    private fun loadReport() {
        val range = monthRange(currentMonth.year, currentMonth.monthNumber)
        
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            combine(
                transactionRepository.getTransactionsByDateRange(range.start, range.endInclusive),
                categoryRepository.getCategories()
            ) { transactions, categories ->
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
                
                val summaries = expenses.groupBy { it.categoryId }.map { (catId, trans) ->
                    val sum = trans.sumOf { it.amount.amount }
                    val category = categoryMap[catId]
                    CategorySummary(
                        categoryId = catId,
                        categoryName = category?.name ?: "Lainnya",
                        amount = Money(sum, "IDR"),
                        percentage = if (totalExpense > 0) sum.toDouble() / totalExpense.toDouble() else 0.0
                    )
                }.sortedByDescending { it.amount.amount }

                val insight = when {
                    transactions.isEmpty() -> "Belum ada transaksi bulan ini. Mulai dari catatan kecil dulu."
                    totalIncome <= 0L -> "Pengeluaran tercatat ${Money(totalExpense, "IDR").format()} bulan ini. Tambahkan income agar saving rate bisa dihitung."
                    savingRate >= 0.2 -> "Bagus, saving rate kamu ${(savingRate * 100).toInt()}% bulan ini."
                    savingRate >= 0.0 -> "Saving rate kamu ${(savingRate * 100).toInt()}%. Masih aman, tapi ruang menabung tipis."
                    else -> "Pengeluaran melebihi income bulan ini. Cek kategori terbesar untuk prioritas evaluasi."
                }
                
                ReportState(
                    categorySummaries = summaries,
                    totalIncome = Money(totalIncome, "IDR"),
                    totalExpense = Money(totalExpense, "IDR"),
                    savingRate = savingRate,
                    insight = insight,
                    exportInProgress = _uiState.value.exportInProgress,
                    isLoading = false
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }

    fun exportCsv() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(exportInProgress = true)
            when (val result = exportMonthlyTransactionsCsvUseCase(currentMonth.year, currentMonth.monthNumber)) {
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
