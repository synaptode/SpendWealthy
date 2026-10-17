package com.spendwealthy.app.ui.screens.transaction

import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.LedgerRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.usecase.DeleteTransactionUseCase
import com.spendwealthy.app.ui.screens.transaction.list.TransactionListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeTransactionRepo = FakeTransactionRepository()
    private val fakeCategoryRepo = FakeCategoryRepository()
    private val fakeLedgerRepo = FakeLedgerRepository()
    private val deleteUseCase = DeleteTransactionUseCase(fakeLedgerRepo)

    private val nowInstant = Clock.System.now()
    private val today = LocalDate(2026, 10, 17)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeTransactionRepo.setSampleTransactions(
            listOf(
                Transaction(
                    id = "tx_1",
                    amount = Money(50_000L, "IDR"),
                    type = TransactionType.EXPENSE,
                    categoryId = "cat_food",
                    walletId = "w1",
                    targetWalletId = null,
                    date = today,
                    note = "Makan Siang",
                    merchant = "Warung Padang",
                    createdAt = nowInstant,
                    updatedAt = nowInstant
                ),
                Transaction(
                    id = "tx_2",
                    amount = Money(2_000_000L, "IDR"),
                    type = TransactionType.INCOME,
                    categoryId = "cat_salary",
                    walletId = "w1",
                    targetWalletId = null,
                    date = today,
                    note = "Gaji Freelance",
                    merchant = "Klien A",
                    createdAt = nowInstant,
                    updatedAt = nowInstant
                ),
                Transaction(
                    id = "tx_3",
                    amount = Money(300_000L, "IDR"),
                    type = TransactionType.TRANSFER,
                    categoryId = null,
                    walletId = "w1",
                    targetWalletId = "w2",
                    date = today,
                    note = "Pindah ke Tabungan",
                    merchant = "BCA to Jago",
                    createdAt = nowInstant,
                    updatedAt = nowInstant
                )
            )
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoadReturnsAllTransactions() = runTest(testDispatcher) {
        val viewModel = TransactionListViewModel(fakeTransactionRepo, deleteUseCase, fakeCategoryRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.transactions.size)
        assertEquals(3, state.filteredTransactions.size)
        assertNull(state.selectedType)
    }

    @Test
    fun testFilterByTypeExpenseOnly() = runTest(testDispatcher) {
        val viewModel = TransactionListViewModel(fakeTransactionRepo, deleteUseCase, fakeCategoryRepo)
        advanceUntilIdle()

        viewModel.onTypeSelected(TransactionType.EXPENSE)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TransactionType.EXPENSE, state.selectedType)
        assertEquals(1, state.filteredTransactions.size)
        assertEquals("tx_1", state.filteredTransactions.first().id)
    }

    @Test
    fun testFilterByTypeIncomeOnly() = runTest(testDispatcher) {
        val viewModel = TransactionListViewModel(fakeTransactionRepo, deleteUseCase, fakeCategoryRepo)
        advanceUntilIdle()

        viewModel.onTypeSelected(TransactionType.INCOME)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TransactionType.INCOME, state.selectedType)
        assertEquals(1, state.filteredTransactions.size)
        assertEquals("tx_2", state.filteredTransactions.first().id)
    }

    @Test
    fun testFilterByTypeTransferOnly() = runTest(testDispatcher) {
        val viewModel = TransactionListViewModel(fakeTransactionRepo, deleteUseCase, fakeCategoryRepo)
        advanceUntilIdle()

        viewModel.onTypeSelected(TransactionType.TRANSFER)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TransactionType.TRANSFER, state.selectedType)
        assertEquals(1, state.filteredTransactions.size)
        assertEquals("tx_3", state.filteredTransactions.first().id)
    }

    @Test
    fun testCombinedSearchQueryAndTypeFilter() = runTest(testDispatcher) {
        val viewModel = TransactionListViewModel(fakeTransactionRepo, deleteUseCase, fakeCategoryRepo)
        advanceUntilIdle()

        viewModel.onTypeSelected(TransactionType.EXPENSE)
        viewModel.onSearchQueryChanged("Makan")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredTransactions.size)

        // Cari query yang tidak ada di expense
        viewModel.onSearchQueryChanged("Gaji")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.filteredTransactions.isEmpty())

        // Reset tipe kembali ke semua
        viewModel.onTypeSelected(null)
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.filteredTransactions.size)
        assertEquals("tx_2", viewModel.uiState.value.filteredTransactions.first().id)
    }

    @Test
    fun testCategoryEntitiesLoadedInState() = runTest(testDispatcher) {
        val viewModel = TransactionListViewModel(fakeTransactionRepo, deleteUseCase, fakeCategoryRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.categoryEntities.size)
        assertEquals("cat_food", state.categoryEntities[0].id)
        assertEquals("cat_salary", state.categoryEntities[1].id)
    }
}

private class FakeTransactionRepository : TransactionRepository {
    private val txFlow = MutableStateFlow<List<Transaction>>(emptyList())

    fun setSampleTransactions(list: List<Transaction>) {
        txFlow.value = list
    }

    override fun getTransactions(): Flow<List<Transaction>> = txFlow

    override fun getTransactionsByWallet(walletId: String): Flow<List<Transaction>> = txFlow

    override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> = txFlow

    override fun getTransactionById(id: String): Flow<Transaction?> = flowOf(txFlow.value.find { it.id == id })

    override suspend fun insertTransaction(transaction: Transaction) {}

    override suspend fun deleteTransaction(id: String) {}
}

private class FakeCategoryRepository : CategoryRepository {
    override fun getCategories(): Flow<List<Category>> = flowOf(
        listOf(
            Category("cat_food", "Makanan & Minuman", "utensils", "#F43F5E", true),
            Category("cat_salary", "Gaji & Pemasukan", "wallet", "#10B981", true)
        )
    )

    override suspend fun insertCategory(category: Category) {}

    override suspend fun seedDefaultCategories() {}
}

private class FakeLedgerRepository : LedgerRepository {
    override suspend fun insertTransactionAndApplyBalance(transaction: Transaction) {}
    override suspend fun deleteTransactionAndRollbackBalance(transactionId: String) {}
}
