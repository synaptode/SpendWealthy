package com.spendwealthy.app.ui.screens.budget

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.core.util.monthRange
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.repository.BudgetRepository
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.service.BudgetEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class BudgetListState(
    val budgets: List<Budget> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val categoriesById: Map<String, Category> = emptyMap(),
    val isLoading: Boolean = false
) : UiState

class BudgetListViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetEngine: BudgetEngine,
    private val categoryRepository: CategoryRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(BudgetListState())
    val uiState: StateFlow<BudgetListState> = _uiState.asStateFlow()

    init {
        loadBudgets()
    }

    private fun loadBudgets() {
        val now = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val range = monthRange(now.year, now.monthNumber)
        screenModelScope.launch {
            combine(
                budgetRepository.getBudgets(now.monthNumber, now.year),
                transactionRepository.getTransactionsByDateRange(range.start, range.endInclusive),
                categoryRepository.getCategories()
            ) { budgets, transactions, categories ->
                _uiState.value.copy(
                    budgets = budgets,
                    transactions = transactions,
                    categoriesById = categories.associateBy { it.id },
                    isLoading = false
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }
    
    fun getUsage(budget: Budget): Double {
        return budgetEngine.calculateUsagePercentage(budget, _uiState.value.transactions)
    }

    fun getRemaining(budget: Budget) = budgetEngine.calculateRemainingAmount(budget, _uiState.value.transactions)

    fun getBudgetName(budget: Budget): String {
        return budget.categoryId?.let { _uiState.value.categoriesById[it]?.name } ?: "Total bulan ini"
    }
}
