package com.spendwealthy.app.ui.screens.transaction.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.usecase.DeleteTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.DatePeriod

data class TransactionListState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val categories: List<String> = listOf("Semua", "Makan", "Transport", "Hiburan", "Belanja", "Tagihan"),
    val selectedCategory: String = "Semua",
    val searchQuery: String = "",
    val isLoading: Boolean = false
) : UiState

class TransactionListViewModel(
    private val transactionRepository: TransactionRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
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
            transactionRepository.getTransactionsByDateRange(
                now.date.minus(DatePeriod(months = 12)),
                now.date
            ).collectLatest { transactions ->
                _uiState.value = _uiState.value.copy(
                    transactions = transactions,
                    filteredTransactions = filterTransactions(transactions, _uiState.value.searchQuery, _uiState.value.selectedCategory),
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredTransactions = filterTransactions(_uiState.value.transactions, query, _uiState.value.selectedCategory)
        )
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredTransactions = filterTransactions(_uiState.value.transactions, _uiState.value.searchQuery, category)
        )
    }

    private fun filterTransactions(transactions: List<Transaction>, query: String, category: String): List<Transaction> {
        return transactions.filter { transaction ->
            val matchQuery = query.isBlank() || 
                    transaction.note.contains(query, ignoreCase = true) || 
                    transaction.merchant.contains(query, ignoreCase = true)
            
            val matchCategory = category == "Semua" || 
                    transaction.note.contains(category, ignoreCase = true) // Temporary mapping
            
            matchQuery && matchCategory
        }
    }

    fun deleteTransaction(id: String) {
        screenModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }
}
