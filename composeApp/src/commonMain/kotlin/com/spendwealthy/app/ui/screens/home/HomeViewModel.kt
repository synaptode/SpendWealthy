package com.spendwealthy.app.ui.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiEffect
import com.spendwealthy.app.core.architecture.UiEvent
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.core.util.monthRange
import com.spendwealthy.app.domain.model.*
import com.spendwealthy.app.domain.repository.*
import com.spendwealthy.app.domain.service.CatInsight
import com.spendwealthy.app.domain.service.CatInsightService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HomeState(
    val userName: String = "User",
    val userAvatarId: Int = 0,
    val totalBalance: Money = Money.zero(),
    val totalIncome: Money = Money.zero(),
    val totalExpense: Money = Money.zero(),
    val safeToSpend: Money = Money.zero(),
    val budgetUsage: Double = 0.0,
    val upcomingBills: List<BillReminder> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val daysUntilPayday: Int = 0,
    val dailyInsight: CatInsight? = null,
    val isLoading: Boolean = false
) : UiState

sealed class HomeEvent : UiEvent {
    object Refresh : HomeEvent()
}

sealed class HomeEffect : UiEffect {
    data class ShowError(val message: String) : HomeEffect()
}

class HomeViewModel(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val preferenceRepository: PreferenceRepository,
    private val budgetRepository: BudgetRepository,
    private val billRepository: BillRepository,
    private val insightService: CatInsightService
) : ScreenModel {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        screenModelScope.launch {
            preferenceRepository.getString("user_name").collectLatest { name ->
                _uiState.value = _uiState.value.copy(userName = name ?: "User")
            }
        }
        screenModelScope.launch {
            preferenceRepository.getInt("user_avatar_id").collectLatest { avatarId ->
                _uiState.value = _uiState.value.copy(userAvatarId = avatarId)
            }
        }
        screenModelScope.launch {
            val now = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val range = monthRange(now.year, now.monthNumber)
            combine(
                walletRepository.getWallets(),
                transactionRepository.getTransactionsByDateRange(range.start, range.endInclusive),
                budgetRepository.getBudgets(now.monthNumber, now.year),
                billRepository.getBillReminders()
            ) { wallets, transactions, budgets, bills ->
                val totalBalance = wallets.sumOf { it.balance.amount }
                val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.amount }
                val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.amount }
                val unpaidBills = bills
                    .filter { it.isEnabled && it.dueDate >= range.start && it.dueDate <= range.endInclusive }
                    .sortedBy { it.dueDate }
                val totalBudget = budgets.firstOrNull { it.categoryId == null } ?: budgets.combineAsTotal()
                val budgetUsage = if (totalBudget?.limit?.amount != null && totalBudget.limit.amount > 0L) {
                    expense.toDouble() / totalBudget.limit.amount.toDouble()
                } else {
                    0.0
                }
                val safe = totalBalance - unpaidBills.sumOf { it.amount.amount }
                
                val insight = insightService.getDailyInsight(transactions, budgets)
                
                _uiState.value.copy(
                    totalBalance = Money(totalBalance, "IDR"),
                    totalIncome = Money(income, "IDR"),
                    totalExpense = Money(expense, "IDR"),
                    safeToSpend = Money(safe, "IDR"),
                    budgetUsage = budgetUsage,
                    upcomingBills = unpaidBills.take(3),
                    recentTransactions = transactions.take(5),
                    daysUntilPayday = range.endInclusive.dayOfMonth - now.dayOfMonth,
                    dailyInsight = insight
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh() {
        observeData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> { /* Refresh logic */ }
        }
    }

    private fun List<Budget>.combineAsTotal(): Budget? {
        if (isEmpty()) return null
        val first = first()
        return first.copy(
            categoryId = null,
            limit = Money(sumOf { it.limit.amount }, first.limit.currency)
        )
    }
}
