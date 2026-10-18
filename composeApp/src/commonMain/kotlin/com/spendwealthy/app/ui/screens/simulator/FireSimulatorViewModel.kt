package com.spendwealthy.app.ui.screens.simulator

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.domain.repository.GoalRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.FireCalculatorEngine
import com.spendwealthy.app.domain.service.FireInput
import com.spendwealthy.app.domain.service.FireResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

data class FireSimulatorState(
    val currentAge: Int = 28,
    val targetRetirementAge: Int = 55,
    val currentNetWorth: Long = 30_000_000L,
    val monthlySavings: Long = 3_000_000L,
    val desiredMonthlyExpense: Long = 8_000_000L,
    val annualReturnRate: Double = 0.08, // 8% default balanced return
    val result: FireResult? = null,
    val isConvertingGoal: Boolean = false,
    val goalConvertedSuccess: Boolean = false,
    val isLoading: Boolean = true
) : UiState

sealed interface FireSimulatorEvent {
    data class CurrentAgeChanged(val age: Int) : FireSimulatorEvent
    data class TargetRetirementAgeChanged(val age: Int) : FireSimulatorEvent
    data class CurrentNetWorthChanged(val amount: Long) : FireSimulatorEvent
    data class MonthlySavingsChanged(val amount: Long) : FireSimulatorEvent
    data class DesiredMonthlyExpenseChanged(val amount: Long) : FireSimulatorEvent
    data class AnnualReturnRateChanged(val rate: Double) : FireSimulatorEvent
    data class ConvertToGoal(val targetName: String, val amount: Long) : FireSimulatorEvent
    data object ResetGoalConvertedStatus : FireSimulatorEvent
}

class FireSimulatorViewModel(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val fireEngine: FireCalculatorEngine = FireCalculatorEngine()
) : ScreenModel {

    private val _uiState = MutableStateFlow(FireSimulatorState())
    val uiState: StateFlow<FireSimulatorState> = _uiState.asStateFlow()

    init {
        initializeFromUserFinances()
    }

    private fun initializeFromUserFinances() {
        screenModelScope.launch {
            try {
                val wallets = walletRepository.getWallets().first()
                val liquidNetWorth = wallets
                    .filter { !it.isArchived && it.type != WalletType.CREDIT_CARD && it.type != WalletType.PAYLATER }
                    .sumOf { it.balance.amount }
                    .coerceAtLeast(1_000_000L)

                val transactions = transactionRepository.getTransactions().first()
                // Simple surplus estimate: income - expense in latest records
                val income = transactions.filter { it.type == com.spendwealthy.app.domain.model.TransactionType.INCOME }.sumOf { it.amount.amount }
                val expense = transactions.filter { it.type == com.spendwealthy.app.domain.model.TransactionType.EXPENSE }.sumOf { it.amount.amount }
                val estimatedSurplus = (income - expense).coerceIn(1_000_000L, 20_000_000L)

                val initialSavings = if (estimatedSurplus > 0L) estimatedSurplus else 3_000_000L

                _uiState.update { current ->
                    current.copy(
                        currentNetWorth = liquidNetWorth,
                        monthlySavings = initialSavings,
                        isLoading = false
                    )
                }
                recalculate()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                recalculate()
            }
        }
    }

    fun onEvent(event: FireSimulatorEvent) {
        when (event) {
            is FireSimulatorEvent.CurrentAgeChanged -> {
                _uiState.update { it.copy(currentAge = event.age.coerceIn(18, 70)) }
                recalculate()
            }
            is FireSimulatorEvent.TargetRetirementAgeChanged -> {
                _uiState.update { it.copy(targetRetirementAge = event.age.coerceIn(30, 80)) }
                recalculate()
            }
            is FireSimulatorEvent.CurrentNetWorthChanged -> {
                _uiState.update { it.copy(currentNetWorth = event.amount.coerceAtLeast(0L)) }
                recalculate()
            }
            is FireSimulatorEvent.MonthlySavingsChanged -> {
                _uiState.update { it.copy(monthlySavings = event.amount.coerceAtLeast(0L)) }
                recalculate()
            }
            is FireSimulatorEvent.DesiredMonthlyExpenseChanged -> {
                _uiState.update { it.copy(desiredMonthlyExpense = event.amount.coerceAtLeast(1_000_000L)) }
                recalculate()
            }
            is FireSimulatorEvent.AnnualReturnRateChanged -> {
                _uiState.update { it.copy(annualReturnRate = event.rate) }
                recalculate()
            }
            is FireSimulatorEvent.ConvertToGoal -> {
                convertTargetToGoal(event.targetName, event.amount)
            }
            FireSimulatorEvent.ResetGoalConvertedStatus -> {
                _uiState.update { it.copy(goalConvertedSuccess = false) }
            }
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        val input = FireInput(
            currentAge = state.currentAge,
            targetRetirementAge = state.targetRetirementAge,
            currentNetWorth = state.currentNetWorth,
            monthlySavings = state.monthlySavings,
            desiredMonthlyExpenseAtRetirement = state.desiredMonthlyExpense,
            annualReturnRate = state.annualReturnRate
        )
        val result = fireEngine.calculate(input)
        _uiState.update { it.copy(result = result) }
    }

    private fun convertTargetToGoal(name: String, amount: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isConvertingGoal = true) }
            try {
                val nowInstant = Clock.System.now()
                val targetYear = 2026 + (_uiState.value.result?.yearsToFire?.toInt() ?: 10)
                val targetDate = LocalDate(targetYear.coerceIn(2027, 2070), 12, 31)

                val goal = Goal(
                    id = "goal_fire_${nowInstant.toEpochMilliseconds()}",
                    name = name,
                    targetAmount = Money(amount, "IDR"),
                    currentAmount = Money(_uiState.value.currentNetWorth.coerceAtMost(amount), "IDR"),
                    type = com.spendwealthy.app.domain.model.GoalType.SAVINGS,
                    deadline = targetDate,
                    createdAt = nowInstant
                )
                goalRepository.insertGoal(goal)
                _uiState.update { it.copy(isConvertingGoal = false, goalConvertedSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isConvertingGoal = false) }
            }
        }
    }
}
