package com.spendwealthy.app.ui.screens.wallet

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiEffect
import com.spendwealthy.app.core.architecture.UiEvent
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.domain.repository.SubscriptionRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.SubscriptionPolicy
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock

data class AddWalletState(
    val name: String = "",
    val type: WalletType = WalletType.CASH,
    val initialBalance: String = "0",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) : UiState

sealed class AddWalletEvent : UiEvent {
    data class NameChanged(val name: String) : AddWalletEvent()
    data class TypeChanged(val type: WalletType) : AddWalletEvent()
    data class BalanceChanged(val balance: String) : AddWalletEvent()
    object SaveClicked : AddWalletEvent()
}

sealed class AddWalletEffect : UiEffect {
    object NavigateBack : AddWalletEffect()
    data class ShowError(val message: String) : AddWalletEffect()
}

class AddWalletViewModel(
    private val walletRepository: WalletRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionPolicy: SubscriptionPolicy
) : ScreenModel {

    private val _uiState = MutableStateFlow(AddWalletState())
    val uiState: StateFlow<AddWalletState> = _uiState.asStateFlow()
    private val _effect = Channel<AddWalletEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: AddWalletEvent) {
        when (event) {
            is AddWalletEvent.NameChanged -> _uiState.value = _uiState.value.copy(name = event.name, errorMessage = null)
            is AddWalletEvent.TypeChanged -> _uiState.value = _uiState.value.copy(type = event.type, errorMessage = null)
            is AddWalletEvent.BalanceChanged -> _uiState.value = _uiState.value.copy(initialBalance = event.balance, errorMessage = null)
            AddWalletEvent.SaveClicked -> saveWallet()
        }
    }

    private fun saveWallet() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            val message = "Nama wallet wajib diisi."
            _uiState.value = state.copy(errorMessage = message)
            screenModelScope.launch { _effect.send(AddWalletEffect.ShowError(message)) }
            return
        }

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val walletCount = walletRepository.getWallets().firstOrNull().orEmpty().size
                val entitlement = subscriptionRepository.getEntitlement().firstOrNull()
                    ?: com.spendwealthy.app.domain.model.SubscriptionEntitlement(com.spendwealthy.app.domain.model.SubscriptionPlan.FREE, true)
                val access = subscriptionPolicy.canAddWallet(walletCount, entitlement)
                if (!access.allowed) {
                    val message = access.reason ?: "Limit wallet tercapai."
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                    _effect.send(AddWalletEffect.ShowError(message))
                    return@launch
                }

                val now = KtClock.System.now()
                val wallet = Wallet(
                    id = "w_${now.toEpochMilliseconds()}",
                    name = state.name,
                    balance = Money(state.initialBalance.toLongOrNull() ?: 0L, "IDR"),
                    type = state.type,
                    createdAt = now,
                    isArchived = false
                )
                walletRepository.insertWallet(wallet)
                _effect.send(AddWalletEffect.NavigateBack)
            } catch (e: Exception) {
                val message = e.message ?: "Wallet gagal disimpan."
                _uiState.value = _uiState.value.copy(errorMessage = message)
                _effect.send(AddWalletEffect.ShowError(message))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
