package com.spendwealthy.app.ui.screens.simulator

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.*
import com.spendwealthy.app.domain.repository.*
import com.spendwealthy.app.domain.service.PurchaseSimulationEngine
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.Clock as KtClock

data class PurchaseSimulatorState(
    val itemName: String = "",
    val priceInput: String = "",
    val priceAmount: Long = 0L,
    val selectedWallet: Wallet? = null,
    val wallets: List<Wallet> = emptyList(),
    val simulationResult: PurchaseSimulationResult? = null,
    val isCalculating: Boolean = false,
    val isLoading: Boolean = true
) : UiState

@OptIn(FlowPreview::class)
class PurchaseSimulatorViewModel(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val billRepository: BillRepository,
    private val budgetRepository: BudgetRepository,
    private val goalRepository: GoalRepository,
    private val simulationEngine: PurchaseSimulationEngine = PurchaseSimulationEngine()
) : ScreenModel {

    private val _uiState = MutableStateFlow(PurchaseSimulatorState())
    val uiState: StateFlow<PurchaseSimulatorState> = _uiState.asStateFlow()

    private var cachedWallets: List<Wallet> = emptyList()
    private var cachedTransactions: List<Transaction> = emptyList()
    private var cachedBills: List<BillReminder> = emptyList()
    private var cachedBudgets: List<Budget> = emptyList()
    private var cachedGoals: List<Goal> = emptyList()

    private val recalculateTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 64)

    init {
        loadData()
        observeRecalculations()
    }

    private fun loadData() {
        val now = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        screenModelScope.launch {
            combine(
                walletRepository.getWallets(),
                transactionRepository.getTransactions(),
                billRepository.getBillReminders(),
                budgetRepository.getBudgets(now.monthNumber, now.year),
                goalRepository.getGoals()
            ) { wallets, transactions, bills, budgets, goals ->
                cachedWallets = wallets.filterNot { it.isArchived }
                cachedTransactions = transactions
                cachedBills = bills
                cachedBudgets = budgets
                cachedGoals = goals

                val activeWallets = cachedWallets
                val defaultWallet = _uiState.value.selectedWallet ?: activeWallets.maxByOrNull { it.balance.amount }

                _uiState.update { current ->
                    current.copy(
                        wallets = activeWallets,
                        selectedWallet = defaultWallet,
                        isLoading = false
                    )
                }
                recalculate()
            }.collect()
        }
    }

    private fun observeRecalculations() {
        screenModelScope.launch {
            recalculateTrigger
                .debounce(150)
                .collectLatest {
                    executeSimulation()
                }
        }
    }

    fun onItemNameChanged(name: String) {
        _uiState.update { it.copy(itemName = name) }
    }

    fun onPriceInputChanged(rawInput: String) {
        val digitsOnly = rawInput.filter { it.isDigit() }
        val amount = digitsOnly.toLongOrNull() ?: 0L
        _uiState.update {
            it.copy(
                priceInput = digitsOnly,
                priceAmount = amount
            )
        }
        recalculate()
    }

    fun onPresetSelected(amount: Long) {
        _uiState.update {
            it.copy(
                priceInput = amount.toString(),
                priceAmount = amount
            )
        }
        recalculate()
    }

    fun onWalletSelected(wallet: Wallet) {
        _uiState.update { it.copy(selectedWallet = wallet) }
        recalculate()
    }

    private fun recalculate() {
        recalculateTrigger.tryEmit(Unit)
    }

    private fun executeSimulation() {
        val state = _uiState.value
        if (state.priceAmount <= 0L || state.wallets.isEmpty()) {
            _uiState.update { it.copy(simulationResult = null, isCalculating = false) }
            return
        }

        _uiState.update { it.copy(isCalculating = true) }

        val today = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val payday = LocalDate(
            year = today.year,
            month = today.month,
            dayOfMonth = today.month.length(isLeapYear(today.year))
        )

        val spentMap = cachedTransactions
            .filter { it.type == TransactionType.EXPENSE && it.date.month == today.month && it.date.year == today.year }
            .groupBy { it.categoryId }
            .mapValues { (_, txs) -> txs.sumOf { it.amount.amount } }

        val unpaidBills = cachedBills.filter { it.isEnabled && it.lastPaidAt == null }

        val request = PurchaseSimulationRequest(
            itemName = state.itemName.ifBlank { "Barang / Layanan" },
            price = Money(state.priceAmount, "IDR"),
            selectedWalletId = state.selectedWallet?.id
        )

        val result = simulationEngine.simulate(
            request = request,
            wallets = state.wallets,
            transactions = cachedTransactions,
            unpaidBills = unpaidBills,
            budgets = cachedBudgets,
            currentSpentInBudgets = spentMap,
            activeGoals = cachedGoals,
            today = today,
            payday = payday
        )

        _uiState.update {
            it.copy(
                simulationResult = result,
                isCalculating = false
            )
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun Month.length(isLeapYear: Boolean): Int {
        return when (this) {
            Month.FEBRUARY -> if (isLeapYear) 29 else 28
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            else -> 31
        }
    }
}
