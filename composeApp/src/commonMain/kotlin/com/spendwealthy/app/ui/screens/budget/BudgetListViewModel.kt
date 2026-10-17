package com.spendwealthy.app.ui.screens.budget

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.core.util.monthRange
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.model.Money
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
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.repository.GoalRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class BudgetListState(
    val budgets: List<Budget> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val categoriesById: Map<String, Category> = emptyMap(),
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false
) : UiState

class BudgetListViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetEngine: BudgetEngine,
    private val categoryRepository: CategoryRepository,
    private val goalRepository: GoalRepository
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
                categoryRepository.getCategories(),
                goalRepository.getGoals()
            ) { budgets, transactions, categories, goals ->
                _uiState.value.copy(
                    budgets = budgets,
                    transactions = transactions,
                    categoriesById = categories.associateBy { it.id },
                    goals = goals,
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

    fun getSpent(budget: Budget) = budgetEngine.calculateSpentAmount(budget, _uiState.value.transactions)

    fun getExceeded(budget: Budget): Money {
        val spent = getSpent(budget)
        val diff = spent.amount - budget.limit.amount
        return if (diff > 0) Money(diff, budget.limit.currency) else Money.zero(budget.limit.currency)
    }

    val totalAllocated: Money
        get() = Money(_uiState.value.budgets.sumOf { it.limit.amount }, "IDR")

    val totalSpent: Money
        get() = Money(_uiState.value.budgets.sumOf { getSpent(it).amount }, "IDR")

    val overallUsagePercentage: Double
        get() {
            val total = totalAllocated.amount
            if (total == 0L) return 0.0
            return totalSpent.amount.toDouble() / total.toDouble()
        }

    val criticalBudgetsCount: Int
        get() = _uiState.value.budgets.count { getUsage(it) >= 1.0 }

    val warningBudgetsCount: Int
        get() = _uiState.value.budgets.count { getUsage(it) >= 0.8 && getUsage(it) < 1.0 }

    fun getBudgetName(budget: Budget): String {
        return budget.categoryId?.let { _uiState.value.categoriesById[it]?.name } ?: "Total bulan ini"
    }
}
