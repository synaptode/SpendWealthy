package com.spendwealthy.app.ui.screens.splitter

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.ExecuteSplitRequest
import com.spendwealthy.app.domain.model.ExecuteSplitResult
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.PaycheckSplitBreakdown
import com.spendwealthy.app.domain.model.SplitStrategy
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.repository.BillRepository
import com.spendwealthy.app.domain.repository.GoalRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.PaycheckSplitterEngine
import com.spendwealthy.app.domain.usecase.ExecutePaycheckSplitUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaycheckSplitterUiState(
    val salaryInput: String = "15000000",
    val salaryAmount: Long = 15_000_000L,
    val selectedStrategy: SplitStrategy = SplitStrategy.BALANCED_50_30_20,
    val customNeedsPercent: Int = 50,
    val customWantsPercent: Int = 30,
    val customSavingsPercent: Int = 20,
    val wallets: List<Wallet> = emptyList(),
    val selectedPayrollWalletId: String? = null,
    val selectedSavingsWalletId: String? = null,
    val upcomingBills: List<BillReminder> = emptyList(),
    val activeGoals: List<Goal> = emptyList(),
    val breakdown: PaycheckSplitBreakdown? = null,
    val isExecuting: Boolean = false,
    val executionResult: ExecuteSplitResult? = null,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class PaycheckSplitterViewModel(
    private val walletRepository: WalletRepository,
    private val billRepository: BillRepository,
    private val goalRepository: GoalRepository,
    private val splitterEngine: PaycheckSplitterEngine,
    private val executePaycheckSplitUseCase: ExecutePaycheckSplitUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(PaycheckSplitterUiState())
    val uiState: StateFlow<PaycheckSplitterUiState> = _uiState.asStateFlow()

    private val recalculateTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 64)

    init {
        loadInitialData()

        recalculateTrigger
            .debounce(150)
            .onEach { recalculate() }
            .launchIn(screenModelScope)
    }

    private fun loadInitialData() {
        screenModelScope.launch {
            combine(
                walletRepository.getWallets(),
                billRepository.getBillReminders(),
                goalRepository.getGoals()
            ) { wallets: List<Wallet>, bills: List<BillReminder>, goals: List<Goal> ->
                Triple(wallets, bills, goals)
            }.collect { (wallets, bills, goals) ->
                val activeWallets = wallets.filterNot { it.isArchived }
                val defaultPayroll = _uiState.value.selectedPayrollWalletId
                    ?: activeWallets.firstOrNull { it.name.contains("BCA", ignoreCase = true) }?.id
                    ?: activeWallets.firstOrNull()?.id

                val defaultSavings = _uiState.value.selectedSavingsWalletId
                    ?: activeWallets.firstOrNull {
                        it.id != defaultPayroll && (it.name.contains("Tabungan", ignoreCase = true) || it.name.contains("Jago", ignoreCase = true))
                    }?.id

                _uiState.update { current ->
                    current.copy(
                        wallets = activeWallets,
                        selectedPayrollWalletId = defaultPayroll,
                        selectedSavingsWalletId = defaultSavings,
                        upcomingBills = bills.filter { it.isEnabled },
                        activeGoals = goals
                    )
                }
                recalculate()
            }
        }
    }

    fun onSalaryChanged(input: String) {
        val sanitized = input.filter { it.isDigit() }
        val amount = sanitized.toLongOrNull() ?: 0L
        _uiState.update { it.copy(salaryInput = sanitized, salaryAmount = amount) }
        recalculateTrigger.tryEmit(Unit)
    }

    fun onPresetSalarySelected(amount: Long) {
        _uiState.update { it.copy(salaryInput = amount.toString(), salaryAmount = amount) }
        recalculateTrigger.tryEmit(Unit)
    }

    fun onStrategySelected(strategy: SplitStrategy) {
        _uiState.update { it.copy(selectedStrategy = strategy) }
        recalculate()
    }

    fun onCustomRatiosChanged(needs: Int, wants: Int, savings: Int) {
        _uiState.update {
            it.copy(
                customNeedsPercent = needs,
                customWantsPercent = wants,
                customSavingsPercent = savings,
                selectedStrategy = SplitStrategy.CUSTOM
            )
        }
        recalculate()
    }

    fun onPayrollWalletSelected(walletId: String) {
        _uiState.update { it.copy(selectedPayrollWalletId = walletId) }
    }

    fun onSavingsWalletSelected(walletId: String?) {
        _uiState.update { it.copy(selectedSavingsWalletId = walletId) }
    }

    fun recalculate() {
        val state = _uiState.value
        val currency = state.wallets.firstOrNull()?.balance?.currency ?: "IDR"
        val incomeMoney = Money(state.salaryAmount, currency)

        val breakdown = splitterEngine.calculateSplit(
            income = incomeMoney,
            strategy = state.selectedStrategy,
            customNeedsRatio = state.customNeedsPercent / 100f,
            customWantsRatio = state.customWantsPercent / 100f,
            customSavingsRatio = state.customSavingsPercent / 100f,
            upcomingBills = state.upcomingBills,
            activeGoals = state.activeGoals,
            daysRemainingInMonth = 30
        )

        _uiState.update { it.copy(breakdown = breakdown) }
    }

    fun onExecuteSplit() {
        val state = _uiState.value
        val breakdown = state.breakdown ?: return
        val payrollId = state.selectedPayrollWalletId ?: return

        if (breakdown.totalIncome.amount <= 0L) {
            _uiState.update { it.copy(errorMessage = "Nominal gaji harus lebih dari 0.") }
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isExecuting = true, errorMessage = null) }
            try {
                val request = ExecuteSplitRequest(
                    totalIncome = breakdown.totalIncome,
                    payrollWalletId = payrollId,
                    savingsWalletId = state.selectedSavingsWalletId,
                    note = "Gaji Masuk & Alokasi Otomatis (${breakdown.strategy.title})",
                    breakdown = breakdown
                )
                val result = executePaycheckSplitUseCase(request)
                _uiState.update { it.copy(isExecuting = false, executionResult = result) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isExecuting = false, errorMessage = e.message ?: "Gagal membagi gaji.") }
            }
        }
    }

    fun dismissResult() {
        _uiState.update { it.copy(executionResult = null, errorMessage = null) }
    }
}
