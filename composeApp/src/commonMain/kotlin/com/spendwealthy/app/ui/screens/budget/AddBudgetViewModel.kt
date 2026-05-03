package com.spendwealthy.app.ui.screens.budget

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiEffect
import com.spendwealthy.app.core.architecture.UiEvent
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.repository.BudgetRepository
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.SubscriptionRepository
import com.spendwealthy.app.domain.service.SubscriptionPolicy
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AddBudgetState(
    val amount: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) : UiState

sealed class AddBudgetEvent : UiEvent {
    data class AmountChanged(val amount: String) : AddBudgetEvent()
    data class CategorySelected(val category: Category) : AddBudgetEvent()
    object SaveClicked : AddBudgetEvent()
}

sealed class AddBudgetEffect : UiEffect {
    object NavigateBack : AddBudgetEffect()
    data class ShowError(val message: String) : AddBudgetEffect()
}

class AddBudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionPolicy: SubscriptionPolicy
) : ScreenModel {

    private val _uiState = MutableStateFlow(AddBudgetState())
    val uiState: StateFlow<AddBudgetState> = _uiState.asStateFlow()
    private val _effect = Channel<AddBudgetEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        screenModelScope.launch {
            categoryRepository.getCategories().collectLatest { categories ->
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    selectedCategory = _uiState.value.selectedCategory ?: categories.firstOrNull()
                )
            }
        }
    }

    fun onEvent(event: AddBudgetEvent) {
        when (event) {
            is AddBudgetEvent.AmountChanged -> _uiState.value = _uiState.value.copy(amount = event.amount, errorMessage = null)
            is AddBudgetEvent.CategorySelected -> _uiState.value = _uiState.value.copy(selectedCategory = event.category, errorMessage = null)
            AddBudgetEvent.SaveClicked -> saveBudget()
        }
    }

    private fun saveBudget() {
        val state = _uiState.value
        val amountLong = state.amount.toLongOrNull() ?: 0L
        if (amountLong <= 0) {
            val message = "Budget harus lebih dari 0."
            _uiState.value = state.copy(errorMessage = message)
            screenModelScope.launch { _effect.send(AddBudgetEffect.ShowError(message)) }
            return
        }

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val now = KtClock.System.now()
                val localDate = now.toLocalDateTime(TimeZone.currentSystemDefault())
                val budgetCount = budgetRepository.getBudgets(localDate.monthNumber, localDate.year).firstOrNull()
                    .orEmpty()
                    .count { it.categoryId != null }
                val entitlement = subscriptionRepository.getEntitlement().firstOrNull()
                    ?: com.spendwealthy.app.domain.model.SubscriptionEntitlement(com.spendwealthy.app.domain.model.SubscriptionPlan.FREE, true)
                val access = subscriptionPolicy.canAddBudgetCategory(budgetCount, entitlement)
                if (!access.allowed && state.selectedCategory != null) {
                    val message = access.reason ?: "Limit budget tercapai."
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                    _effect.send(AddBudgetEffect.ShowError(message))
                    return@launch
                }
                
                val budget = Budget(
                    id = "b_${now.toEpochMilliseconds()}",
                    categoryId = state.selectedCategory?.id,
                    limit = Money(amountLong, "IDR"),
                    month = localDate.monthNumber,
                    year = localDate.year,
                    createdAt = now
                )
                budgetRepository.insertBudget(budget)
                _effect.send(AddBudgetEffect.NavigateBack)
            } catch (e: Exception) {
                val message = e.message ?: "Budget gagal disimpan."
                _uiState.value = _uiState.value.copy(errorMessage = message)
                _effect.send(AddBudgetEffect.ShowError(message))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
