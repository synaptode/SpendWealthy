package com.spendwealthy.app.ui.screens.wallet

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WalletDetailState(
    val wallet: Wallet? = null,
    val transactions: List<Transaction> = emptyList(),
    val filterByFamily: Boolean = false,
    val isLoading: Boolean = false
) : UiState

class WalletDetailViewModel(
    private val walletId: String,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(WalletDetailState())
    val uiState: StateFlow<WalletDetailState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            walletRepository.getWalletById(walletId).collectLatest { wallet ->
                _uiState.value = _uiState.value.copy(wallet = wallet)
            }
        }
        screenModelScope.launch {
            transactionRepository.getTransactionsByWallet(walletId).collectLatest { transactions ->
                _uiState.value = _uiState.value.copy(transactions = transactions)
            }
        }
    }

    fun toggleFamilyFilter() {
        _uiState.value = _uiState.value.copy(filterByFamily = !_uiState.value.filterByFamily)
    }

    val filteredTransactions: StateFlow<List<Transaction>> = _uiState.map { state ->
        if (state.filterByFamily) {
            state.transactions.filter { it.familyId != null }
        } else {
            state.transactions
        }
    }.stateIn<List<Transaction>>(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
