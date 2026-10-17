package com.spendwealthy.app.ui.screens.splitter

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.ExecuteSplitRequest
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.SplitStrategy
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.domain.repository.BillRepository
import com.spendwealthy.app.domain.repository.GoalRepository
import com.spendwealthy.app.domain.repository.LedgerRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.PaycheckSplitterEngine
import com.spendwealthy.app.domain.usecase.ExecutePaycheckSplitUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PaycheckSplitterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val now = Instant.fromEpochMilliseconds(1773000000000L)

    private val fakeBcaWallet = Wallet(
        id = "w_bca",
        name = "BCA Payroll",
        balance = Money(10_000_000L, "IDR"),
        type = WalletType.BANK,
        createdAt = now,
        isArchived = false
    )

    private val fakeWalletRepo = object : WalletRepository {
        val walletsFlow = MutableStateFlow(listOf(fakeBcaWallet))
        override fun getWallets(): Flow<List<Wallet>> = walletsFlow
        override fun getWalletById(id: String): Flow<Wallet?> = MutableStateFlow(fakeBcaWallet)
        override suspend fun insertWallet(wallet: Wallet) {}
        override suspend fun deleteWallet(id: String) {}
        override suspend fun archiveWallet(id: String) {}
    }

    private val fakeBillRepo = object : BillRepository {
        override fun getBillReminders(): Flow<List<BillReminder>> = flowOf(emptyList())
        override fun getBillReminderById(id: String): Flow<BillReminder?> = flowOf(null)
        override suspend fun insertBillReminder(bill: BillReminder) {}
        override suspend fun updateBillReminder(bill: BillReminder) {}
        override suspend fun deleteBillReminder(id: String) {}
    }

    private val fakeGoalRepo = object : GoalRepository {
        val updatedGoals = mutableMapOf<String, Long>()
        override fun getGoals(): Flow<List<Goal>> = flowOf(emptyList())
        override fun getGoalById(id: String): Flow<Goal?> = flowOf(null)
        override suspend fun insertGoal(goal: Goal) {}
        override suspend fun updateGoalProgress(id: String, currentAmount: Long) {
            updatedGoals[id] = currentAmount
        }
        override suspend fun deleteGoal(id: String) {}
    }

    private val fakeLedgerRepo = object : LedgerRepository {
        override suspend fun insertTransactionAndApplyBalance(transaction: com.spendwealthy.app.domain.model.Transaction) {}
        override suspend fun deleteTransactionAndRollbackBalance(transactionId: String) {}
    }

    private val fakeClock = object : Clock {
        override fun now(): Instant = now
    }

    private lateinit var viewModel: PaycheckSplitterViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val engine = PaycheckSplitterEngine()
        val useCase = ExecutePaycheckSplitUseCase(fakeLedgerRepo, fakeGoalRepo, fakeClock)
        viewModel = PaycheckSplitterViewModel(
            walletRepository = fakeWalletRepo,
            billRepository = fakeBillRepo,
            goalRepository = fakeGoalRepo,
            splitterEngine = engine,
            executePaycheckSplitUseCase = useCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateLoadsWalletsAndCalculatesInitialBreakdown() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(1, state.wallets.size)
        assertEquals("w_bca", state.selectedPayrollWalletId)
        assertNotNull(state.breakdown)
        assertEquals(15_000_000L, state.breakdown?.totalIncome?.amount)
        assertEquals(7_500_000L, state.breakdown?.needsAmount?.amount)
        assertEquals(4_500_000L, state.breakdown?.wantsAmount?.amount)
        assertEquals(3_000_000L, state.breakdown?.savingsAmount?.amount)
    }

    @Test
    fun testSalaryChangedTriggersRecalculation() = runTest {
        advanceUntilIdle()
        viewModel.onSalaryChanged("20000000") // 20 Juta

        assertEquals(20_000_000L, viewModel.uiState.value.salaryAmount)

        advanceTimeBy(200)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.breakdown)
        assertEquals(20_000_000L, state.breakdown?.totalIncome?.amount)
        assertEquals(10_000_000L, state.breakdown?.needsAmount?.amount)
        assertEquals(6_000_000L, state.breakdown?.wantsAmount?.amount)
        assertEquals(4_000_000L, state.breakdown?.savingsAmount?.amount)
    }

    @Test
    fun testStrategyChangeRecalculatesImmediately() = runTest {
        advanceUntilIdle()
        viewModel.onStrategySelected(SplitStrategy.AGGRESSIVE_SAVER_40_20_40)

        val state = viewModel.uiState.value
        assertEquals(SplitStrategy.AGGRESSIVE_SAVER_40_20_40, state.selectedStrategy)
        assertEquals(6_000_000L, state.breakdown?.needsAmount?.amount) // 40% of 15jt
        assertEquals(3_000_000L, state.breakdown?.wantsAmount?.amount) // 20% of 15jt
        assertEquals(6_000_000L, state.breakdown?.savingsAmount?.amount) // 40% of 15jt
    }

    @Test
    fun testExecuteSplitCallsUseCaseSuccessfully() = runTest {
        advanceUntilIdle()
        viewModel.onExecuteSplit()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.executionResult)
        assertTrue(state.executionResult!!.isSuccess)
    }
}
