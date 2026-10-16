package com.spendwealthy.app.ui.screens.onboarding

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.core.util.daysInMonth
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.RecurrenceType
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.domain.repository.BillRepository
import com.spendwealthy.app.domain.repository.BudgetRepository
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class OnboardingState(
    val step: Int = 1,
    val mode: String = "PERSONAL",
    val name: String = "",
    val walletName: String = "Kas Utama",
    val walletType: WalletType = WalletType.CASH,
    val initialBalance: String = "0",
    val monthlyBudget: String = "",
    val billName: String = "",
    val billAmount: String = "",
    val billDueDay: String = "25",
    val avatarId: Int = 0,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false
) : UiState

class OnboardingViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val billRepository: BillRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onModeChanged(mode: String) {
        _uiState.value = _uiState.value.copy(mode = mode)
    }

    fun onAvatarChanged(id: Int) {
        _uiState.value = _uiState.value.copy(avatarId = id)
    }

    fun onWalletNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(walletName = name)
    }

    fun onWalletTypeChanged(type: WalletType) {
        _uiState.value = _uiState.value.copy(walletType = type)
    }

    fun onInitialBalanceChanged(balance: String) {
        _uiState.value = _uiState.value.copy(initialBalance = balance.filter { it.isDigit() })
    }

    fun onMonthlyBudgetChanged(amount: String) {
        _uiState.value = _uiState.value.copy(monthlyBudget = amount.filter { it.isDigit() })
    }

    fun onBillNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(billName = name)
    }

    fun onBillAmountChanged(amount: String) {
        _uiState.value = _uiState.value.copy(billAmount = amount.filter { it.isDigit() })
    }

    fun onBillDueDayChanged(day: String) {
        _uiState.value = _uiState.value.copy(billDueDay = day.filter { it.isDigit() }.take(2))
    }

    fun nextStep() {
        if (_uiState.value.step < 4) {
            _uiState.value = _uiState.value.copy(step = _uiState.value.step + 1)
        } else {
            completeOnboarding()
        }
    }

    private fun completeOnboarding() {
        val state = _uiState.value
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            preferenceRepository.setString("user_name", state.name.ifBlank { "Teman" })
            preferenceRepository.setString("user_mode", state.mode)
            preferenceRepository.setInt("user_avatar_id", state.avatarId)
            
            val now = KtClock.System.now()
            val localDate = now.toLocalDateTime(TimeZone.currentSystemDefault())
            val wallet = Wallet(
                id = "w_initial",
                name = state.walletName.ifBlank { "Kas Utama" },
                balance = Money(state.initialBalance.toLongOrNull() ?: 0L, "IDR"),
                type = state.walletType,
                createdAt = now,
                isArchived = false
            )
            walletRepository.insertWallet(wallet)

            if (categoryRepository.getCategories().firstOrNull().orEmpty().isEmpty()) {
                categoryRepository.seedDefaultCategories()
            }

            val monthlyBudget = state.monthlyBudget.toLongOrNull() ?: 0L
            if (monthlyBudget > 0L) {
                budgetRepository.insertBudget(
                    Budget(
                        id = "b_initial_${now.toEpochMilliseconds()}",
                        categoryId = null,
                        limit = Money(monthlyBudget, "IDR"),
                        month = localDate.monthNumber,
                        year = localDate.year,
                        createdAt = now
                    )
                )
            }

            val billAmount = state.billAmount.toLongOrNull() ?: 0L
            if (state.billName.isNotBlank() && billAmount > 0L) {
                val dueDay = state.billDueDay.toIntOrNull() ?: localDate.dayOfMonth
                val safeDueDay = dueDay.coerceIn(1, daysInMonth(localDate.year, localDate.monthNumber))
                billRepository.insertBillReminder(
                    BillReminder(
                        id = "bill_initial_${now.toEpochMilliseconds()}",
                        name = state.billName,
                        amount = Money(billAmount, "IDR"),
                        categoryId = null,
                        dueDate = LocalDate(localDate.year, localDate.monthNumber, safeDueDay),
                        recurrenceType = RecurrenceType.MONTHLY,
                        isEnabled = true,
                        lastPaidAt = null
                    )
                )
            }

            preferenceRepository.setBoolean("onboarding_completed", true)
            
            _uiState.value = _uiState.value.copy(isLoading = false, isCompleted = true)
        }
    }

    fun skipToComplete() {
        completeOnboarding()
    }
}
