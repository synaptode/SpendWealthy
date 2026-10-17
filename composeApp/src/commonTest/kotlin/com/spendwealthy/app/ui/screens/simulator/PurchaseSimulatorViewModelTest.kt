package com.spendwealthy.app.ui.screens.simulator

import com.spendwealthy.app.domain.model.*
import com.spendwealthy.app.domain.repository.*
import com.spendwealthy.app.domain.service.PurchaseSimulationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PurchaseSimulatorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val now = Instant.fromEpochMilliseconds(1700000000000L)
    private val fakeWallet = Wallet(
        id = "w1",
        name = "BCA Utama",
        balance = Money(25_000_000L, "IDR"),
        type = WalletType.BANK,
        createdAt = now,
        isArchived = false
    )

    private val fakeWalletRepo = object : WalletRepository {
        val walletsFlow = MutableStateFlow(listOf(fakeWallet))
        override fun getWallets(): Flow<List<Wallet>> = walletsFlow
        override fun getWalletById(id: String): Flow<Wallet?> = MutableStateFlow(fakeWallet)
        override suspend fun insertWallet(wallet: Wallet) {}
        override suspend fun deleteWallet(id: String) {}
        override suspend fun archiveWallet(id: String) {}
    }

    private val fakeTxRepo = object : TransactionRepository {
        override fun getTransactions(): Flow<List<Transaction>> = MutableStateFlow(emptyList())
        override fun getTransactionsByWallet(walletId: String): Flow<List<Transaction>> = MutableStateFlow(emptyList())
        override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> = MutableStateFlow(emptyList())
        override fun getTransactionById(id: String): Flow<Transaction?> = MutableStateFlow(null)
        override suspend fun insertTransaction(transaction: Transaction) {}
        override suspend fun deleteTransaction(id: String) {}
    }

    private val fakeBillRepo = object : BillRepository {
        override fun getBillReminders(): Flow<List<BillReminder>> = MutableStateFlow(emptyList())
        override fun getBillReminderById(id: String): Flow<BillReminder?> = MutableStateFlow(null)
        override suspend fun insertBillReminder(bill: BillReminder) {}
        override suspend fun updateBillReminder(bill: BillReminder) {}
        override suspend fun deleteBillReminder(id: String) {}
    }

    private val fakeBudgetRepo = object : BudgetRepository {
        override fun getBudgets(month: Int, year: Int): Flow<List<Budget>> = MutableStateFlow(emptyList())
        override suspend fun insertBudget(budget: Budget) {}
        override suspend fun deleteBudget(id: String) {}
    }

    private val fakeGoalRepo = object : GoalRepository {
        override fun getGoals(): Flow<List<Goal>> = MutableStateFlow(emptyList())
        override fun getGoalById(id: String): Flow<Goal?> = MutableStateFlow(null)
        override suspend fun insertGoal(goal: Goal) {}
        override suspend fun updateGoalProgress(id: String, currentAmount: Long) {}
        override suspend fun deleteGoal(id: String) {}
    }

    private lateinit var viewModel: PurchaseSimulatorViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PurchaseSimulatorViewModel(
            walletRepository = fakeWalletRepo,
            transactionRepository = fakeTxRepo,
            billRepository = fakeBillRepo,
            budgetRepository = fakeBudgetRepo,
            goalRepository = fakeGoalRepo,
            simulationEngine = PurchaseSimulationEngine()
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateLoadsWalletsAndSelectsDefault() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(1, state.wallets.size)
        assertEquals("w1", state.selectedWallet?.id)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun testPriceChangeTriggersDebouncedSimulation() = runTest {
        advanceUntilIdle()
        viewModel.onItemNameChanged("Sepatu Olahraga")
        viewModel.onPriceInputChanged("500000") // Rp 500.000

        assertEquals(500_000L, viewModel.uiState.value.priceAmount)

        // Advance debounce timer (150ms)
        advanceTimeBy(200)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.simulationResult)
        assertEquals(PurchaseVerdict.SAFE_TO_BUY, state.simulationResult?.verdict)
        assertTrue(state.simulationResult!!.canAffordDirectly)
    }

    @Test
    fun testPresetSelectionUpdatesPriceAndSimulates() = runTest {
        advanceUntilIdle()
        viewModel.onPresetSelected(2_000_000L)

        assertEquals(2_000_000L, viewModel.uiState.value.priceAmount)
        advanceTimeBy(200)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.simulationResult)
        assertEquals(2_000_000L, state.priceAmount)
    }
}
