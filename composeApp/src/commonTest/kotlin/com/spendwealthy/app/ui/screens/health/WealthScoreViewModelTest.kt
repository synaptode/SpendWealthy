package com.spendwealthy.app.ui.screens.health

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.domain.repository.BillRepository
import com.spendwealthy.app.domain.repository.GoalRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.FinancialGamificationService
import com.spendwealthy.app.domain.service.WealthScoreEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WealthScoreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val now = Instant.fromEpochMilliseconds(1773000000000)

    private val fakeWallet = Wallet("w1", "BCA", Money(30_000_000L, "IDR"), WalletType.BANK, now, false)

    private val fakeWalletRepo = object : WalletRepository {
        override fun getWallets(): Flow<List<Wallet>> = flowOf(listOf(fakeWallet))
        override fun getWalletById(id: String): Flow<Wallet?> = flowOf(fakeWallet)
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
        override fun getGoals(): Flow<List<Goal>> = flowOf(emptyList())
        override fun getGoalById(id: String): Flow<Goal?> = flowOf(null)
        override suspend fun insertGoal(goal: Goal) {}
        override suspend fun updateGoalProgress(id: String, currentAmount: Long) {}
        override suspend fun deleteGoal(id: String) {}
    }

    private val fakeTxRepo = object : TransactionRepository {
        override fun getTransactions(): Flow<List<Transaction>> = flowOf(emptyList())
        override fun getTransactionsByWallet(walletId: String): Flow<List<Transaction>> = flowOf(emptyList())
        override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> = flowOf(emptyList())
        override fun getTransactionById(id: String): Flow<Transaction?> = flowOf(null)
        override suspend fun insertTransaction(transaction: Transaction) {}
        override suspend fun deleteTransaction(id: String) {}
    }

    private val fakeClock = object : Clock {
        override fun now(): Instant = now
    }

    private lateinit var viewModel: WealthScoreViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WealthScoreViewModel(
            walletRepository = fakeWalletRepo,
            billRepository = fakeBillRepo,
            goalRepository = fakeGoalRepo,
            transactionRepository = fakeTxRepo,
            scoreEngine = WealthScoreEngine(),
            gamificationService = FinancialGamificationService(),
            clock = fakeClock
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState_loadsScoreAndMissions() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.score.score in 300..850)
        assertNotNull(state.score.breakdown)
        assertTrue(state.missions.isNotEmpty())
        assertTrue(state.badges.isNotEmpty())
    }

    @Test
    fun testTabSelection_updatesSelectedTab() = runTest {
        advanceUntilIdle()
        assertEquals(HealthTab.BREAKDOWN, viewModel.uiState.value.selectedTab)

        viewModel.onTabSelected(HealthTab.MISSIONS)
        assertEquals(HealthTab.MISSIONS, viewModel.uiState.value.selectedTab)

        viewModel.onTabSelected(HealthTab.BADGES)
        assertEquals(HealthTab.BADGES, viewModel.uiState.value.selectedTab)
    }
}
