package com.spendwealthy.app.ui.screens.transaction

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiEffect
import com.spendwealthy.app.core.architecture.UiEvent
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.core.util.monthRange
import com.spendwealthy.app.domain.model.*
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.SubscriptionRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.SubscriptionPolicy
import com.spendwealthy.app.domain.usecase.AddTransactionUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AddTransactionState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedWallet: Wallet? = null,
    val selectedTargetWallet: Wallet? = null,
    val selectedCategory: Category? = null,
    val wallets: List<Wallet> = emptyList(),
    val categories: List<Category> = emptyList(),
    val note: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) : UiState

sealed class AddTransactionEvent : UiEvent {
    data class AmountChanged(val amount: String) : AddTransactionEvent()
    data class TypeChanged(val type: TransactionType) : AddTransactionEvent()
    data class WalletSelected(val wallet: Wallet) : AddTransactionEvent()
    data class TargetWalletSelected(val wallet: Wallet) : AddTransactionEvent()
    data class CategorySelected(val category: Category) : AddTransactionEvent()
    data class NoteChanged(val note: String) : AddTransactionEvent()
    object SaveClicked : AddTransactionEvent()
}

sealed class AddTransactionEffect : UiEffect {
    object NavigateBack : AddTransactionEffect()
    data class ShowError(val message: String) : AddTransactionEffect()
}

class AddTransactionViewModel(
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val transactionRepository: TransactionRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionPolicy: SubscriptionPolicy
) : ScreenModel {

    private val _uiState = MutableStateFlow(AddTransactionState())
    val uiState: StateFlow<AddTransactionState> = _uiState.asStateFlow()
    private val _effect = Channel<AddTransactionEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            walletRepository.getWallets().collectLatest { wallets ->
                _uiState.value = _uiState.value.copy(
                    wallets = wallets,
                    selectedWallet = _uiState.value.selectedWallet ?: wallets.firstOrNull(),
                    selectedTargetWallet = _uiState.value.selectedTargetWallet
                        ?: wallets.drop(1).firstOrNull()
                        ?: wallets.firstOrNull()
                )
            }
        }
        screenModelScope.launch {
            categoryRepository.getCategories().collectLatest { categories ->
                if (categories.isEmpty()) {
                    categoryRepository.seedDefaultCategories()
                }
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    selectedCategory = _uiState.value.selectedCategory ?: categories.firstOrNull()
                )
            }
        }
    }

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.AmountChanged -> _uiState.value = _uiState.value.copy(amount = event.amount, errorMessage = null)
            is AddTransactionEvent.TypeChanged -> _uiState.value = _uiState.value.copy(type = event.type, errorMessage = null)
            is AddTransactionEvent.WalletSelected -> _uiState.value = _uiState.value.copy(
                selectedWallet = event.wallet,
                selectedTargetWallet = _uiState.value.selectedTargetWallet.takeUnless { it?.id == event.wallet.id }
                    ?: _uiState.value.wallets.firstOrNull { it.id != event.wallet.id },
                errorMessage = null
            )
            is AddTransactionEvent.TargetWalletSelected -> _uiState.value = _uiState.value.copy(selectedTargetWallet = event.wallet, errorMessage = null)
            is AddTransactionEvent.CategorySelected -> _uiState.value = _uiState.value.copy(selectedCategory = event.category, errorMessage = null)
            is AddTransactionEvent.NoteChanged -> _uiState.value = _uiState.value.copy(note = event.note)
            AddTransactionEvent.SaveClicked -> saveTransaction()
        }
    }

    private fun saveTransaction() {
        val state = _uiState.value
        val amountLong = state.amount.toLongOrNull() ?: 0L
        val wallet = state.selectedWallet ?: return
        val category = state.selectedCategory

        if (amountLong <= 0) {
            val message = "Nominal harus lebih dari 0."
            _uiState.value = state.copy(errorMessage = message)
            screenModelScope.launch {
                _effect.send(AddTransactionEffect.ShowError(message))
            }
            return
        }
        if (state.type == TransactionType.TRANSFER && state.selectedTargetWallet == null) {
            val message = "Pilih dompet tujuan transfer."
            _uiState.value = state.copy(errorMessage = message)
            screenModelScope.launch {
                _effect.send(AddTransactionEffect.ShowError(message))
            }
            return
        }

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val now = KtClock.System.now()
                val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
                val range = monthRange(today.year, today.monthNumber)
                val transactionCount = transactionRepository.getTransactionsByDateRange(range.start, range.endInclusive)
                    .firstOrNull()
                    .orEmpty()
                    .size
                val entitlement = subscriptionRepository.getEntitlement().firstOrNull()
                    ?: SubscriptionEntitlement(SubscriptionPlan.FREE, true)
                val access = subscriptionPolicy.canAddTransaction(transactionCount, entitlement)
                if (!access.allowed) {
                    val message = access.reason ?: "Limit transaksi tercapai."
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                    _effect.send(AddTransactionEffect.ShowError(message))
                    return@launch
                }
                
                val transaction = Transaction(
                    id = "t_${now.toEpochMilliseconds()}",
                    amount = Money(amountLong, wallet.balance.currency),
                    type = state.type,
                    categoryId = category?.id.takeUnless { state.type == TransactionType.TRANSFER },
                    walletId = wallet.id,
                    targetWalletId = state.selectedTargetWallet?.id.takeIf { state.type == TransactionType.TRANSFER },
                    date = today,
                    note = state.note,
                    merchant = "",
                    createdAt = now,
                    updatedAt = now
                )
                
                addTransactionUseCase(transaction)
                _effect.send(AddTransactionEffect.NavigateBack)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Transaksi gagal disimpan.")
                _effect.send(AddTransactionEffect.ShowError(e.message ?: "Transaksi gagal disimpan."))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
