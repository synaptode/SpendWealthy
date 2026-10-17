package com.spendwealthy.app.ui.screens.transaction.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.usecase.DeleteTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

data class CategoryFilterItem(
    val id: String?,
    val name: String,
    val iconName: String = "category"
)

data class TransactionListState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val categoryFilterItems: List<CategoryFilterItem> = listOf(CategoryFilterItem(null, "Semua")),
    val selectedCategoryId: String? = null,
    val selectedYear: Int? = null,
    val selectedMonth: Int? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
) : UiState {
    // Backward compatibility helper
    val categories: List<String>
        get() = categoryFilterItems.map { it.name }
    val selectedCategory: String
        get() = categoryFilterItems.find { it.id == selectedCategoryId }?.name ?: "Semua"
}

class TransactionListViewModel(
    private val transactionRepository: TransactionRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val categoryRepository: CategoryRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(TransactionListState())
    val uiState: StateFlow<TransactionListState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        val now = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                transactionRepository.getTransactionsByDateRange(
                    now.date.minus(DatePeriod(months = 24)),
                    now.date
                ),
                categoryRepository.getCategories()
            ) { transactions, dbCategories ->
                val categoryItems = listOf(CategoryFilterItem(null, "Semua")) + dbCategories.map {
                    CategoryFilterItem(id = it.id, name = it.name, iconName = it.iconName)
                }
                Pair(transactions, categoryItems)
            }.collectLatest { (transactions, categoryItems) ->
                val current = _uiState.value
                _uiState.value = current.copy(
                    transactions = transactions,
                    categoryFilterItems = categoryItems,
                    filteredTransactions = filterTransactions(
                        transactions = transactions,
                        query = current.searchQuery,
                        categoryId = current.selectedCategoryId,
                        categoryItems = categoryItems,
                        year = current.selectedYear,
                        month = current.selectedMonth
                    ),
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            searchQuery = query,
            filteredTransactions = filterTransactions(
                transactions = current.transactions,
                query = query,
                categoryId = current.selectedCategoryId,
                categoryItems = current.categoryFilterItems,
                year = current.selectedYear,
                month = current.selectedMonth
            )
        )
    }

    fun onCategorySelected(categoryId: String?) {
        val current = _uiState.value
        _uiState.value = current.copy(
            selectedCategoryId = categoryId,
            filteredTransactions = filterTransactions(
                transactions = current.transactions,
                query = current.searchQuery,
                categoryId = categoryId,
                categoryItems = current.categoryFilterItems,
                year = current.selectedYear,
                month = current.selectedMonth
            )
        )
    }

    fun onCategorySelectedByName(categoryName: String) {
        val current = _uiState.value
        val item = current.categoryFilterItems.find { it.name == categoryName }
        onCategorySelected(item?.id)
    }

    fun onPeriodSelected(year: Int?, month: Int?) {
        val current = _uiState.value
        _uiState.value = current.copy(
            selectedYear = year,
            selectedMonth = month,
            filteredTransactions = filterTransactions(
                transactions = current.transactions,
                query = current.searchQuery,
                categoryId = current.selectedCategoryId,
                categoryItems = current.categoryFilterItems,
                year = year,
                month = month
            )
        )
    }

    fun resetPeriod() {
        onPeriodSelected(null, null)
    }

    private fun filterTransactions(
        transactions: List<Transaction>,
        query: String,
        categoryId: String?,
        categoryItems: List<CategoryFilterItem>,
        year: Int?,
        month: Int?
    ): List<Transaction> {
        val selectedCategoryName = categoryItems.find { it.id == categoryId }?.name
        return transactions.filter { transaction ->
            val matchQuery = query.isBlank() ||
                    transaction.note.contains(query, ignoreCase = true) ||
                    transaction.merchant.contains(query, ignoreCase = true)

            val matchCategory = categoryId == null ||
                    transaction.categoryId == categoryId ||
                    (selectedCategoryName != null && transaction.note.contains(selectedCategoryName, ignoreCase = true))

            val matchPeriod = (year == null || transaction.date.year == year) &&
                    (month == null || transaction.date.monthNumber == month)

            matchQuery && matchCategory && matchPeriod
        }
    }

    fun deleteTransaction(id: String) {
        screenModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }
}
