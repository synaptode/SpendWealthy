package com.spendwealthy.app.ui.screens.bill

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiEffect
import com.spendwealthy.app.core.architecture.UiEvent
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.core.util.daysInMonth
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.model.*
import com.spendwealthy.app.domain.repository.BillRepository
import com.spendwealthy.app.domain.repository.SubscriptionRepository
import com.spendwealthy.app.domain.service.SubscriptionPolicy
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AddBillState(
    val name: String = "",
    val amount: String = "",
    val dueDay: String = "25",
    val recurrenceType: RecurrenceType = RecurrenceType.MONTHLY,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) : UiState

sealed class AddBillEvent : UiEvent {
    data class NameChanged(val name: String) : AddBillEvent()
    data class AmountChanged(val amount: String) : AddBillEvent()
    data class DueDateChanged(val day: String) : AddBillEvent()
    data class RecurrenceChanged(val type: RecurrenceType) : AddBillEvent()
    object SaveClicked : AddBillEvent()
}

sealed class AddBillEffect : UiEffect {
    object NavigateBack : AddBillEffect()
    data class ShowError(val message: String) : AddBillEffect()
}

class AddBillViewModel(
    private val billRepository: BillRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionPolicy: SubscriptionPolicy,
    private val notificationScheduler: NotificationScheduler
) : ScreenModel {

    private val _uiState = MutableStateFlow(AddBillState())
    val uiState: StateFlow<AddBillState> = _uiState.asStateFlow()
    private val _effect = Channel<AddBillEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: AddBillEvent) {
        when (event) {
            is AddBillEvent.NameChanged -> _uiState.value = _uiState.value.copy(name = event.name, errorMessage = null)
            is AddBillEvent.AmountChanged -> _uiState.value = _uiState.value.copy(amount = event.amount, errorMessage = null)
            is AddBillEvent.DueDateChanged -> _uiState.value = _uiState.value.copy(dueDay = event.day.filter { it.isDigit() }.take(2), errorMessage = null)
            is AddBillEvent.RecurrenceChanged -> _uiState.value = _uiState.value.copy(recurrenceType = event.type)
            AddBillEvent.SaveClicked -> saveBill()
        }
    }

    private fun saveBill() {
        val state = _uiState.value
        val amountLong = state.amount.toLongOrNull() ?: 0L
        if (state.name.isBlank() || amountLong <= 0) {
            _uiState.value = state.copy(errorMessage = "Nama dan nominal tagihan wajib diisi.")
            return
        }

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val reminderCount = billRepository.getBillReminders().firstOrNull().orEmpty().size
                val entitlement = subscriptionRepository.getEntitlement().firstOrNull()
                    ?: SubscriptionEntitlement(plan = SubscriptionPlan.FREE, isActive = true)
                val access = subscriptionPolicy.canAddBillReminder(reminderCount, entitlement)
                if (!access.allowed) {
                    val message = access.reason ?: "Limit reminder tercapai."
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                    _effect.send(AddBillEffect.ShowError(message))
                    return@launch
                }

                val now = Clock.System.now()
                val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
                val dueDay = (state.dueDay.toIntOrNull() ?: today.dayOfMonth)
                    .coerceIn(1, daysInMonth(today.year, today.monthNumber))
                
                val bill = BillReminder(
                    id = "bill_${now.toEpochMilliseconds()}",
                    name = state.name,
                    amount = Money(amountLong, "IDR"),
                    categoryId = null,
                    dueDate = LocalDate(today.year, today.monthNumber, dueDay),
                    recurrenceType = state.recurrenceType,
                    isEnabled = true,
                    lastPaidAt = null
                )
                billRepository.insertBillReminder(bill)
                when (val result = notificationScheduler.scheduleBillReminder(bill)) {
                    is GatewayResult.Failure -> _effect.send(AddBillEffect.ShowError(result.message))
                    is GatewayResult.Success -> Unit
                }
                _effect.send(AddBillEffect.NavigateBack)
            } catch (e: Exception) {
                val message = e.message ?: "Tagihan gagal disimpan."
                _uiState.value = _uiState.value.copy(errorMessage = message)
                _effect.send(AddBillEffect.ShowError(message))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
