package com.spendwealthy.app.ui.screens.wallet

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class WalletListState(
    val wallets: List<Wallet> = emptyList(),
    val isLoading: Boolean = false
) : UiState

class WalletListViewModel(
    private val walletRepository: WalletRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(WalletListState())
    val uiState: StateFlow<WalletListState> = _uiState.asStateFlow()

    init {
        loadWallets()
    }

    private fun loadWallets() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            walletRepository.getWallets().collectLatest { wallets ->
                _uiState.value = _uiState.value.copy(
                    wallets = wallets,
                    isLoading = false
                )
            }
        }
    }
}
