package com.spendwealthy.app

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.domain.usecase.RescheduleBillRemindersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.spendwealthy.app.domain.service.*

sealed class AppStartupState {
    object Loading : AppStartupState()
    object Onboarding : AppStartupState()
    object Dashboard : AppStartupState()
}

class AppViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val rescheduleBillRemindersUseCase: RescheduleBillRemindersUseCase,
    private val notificationService: NotificationService
) : ScreenModel {
    private val _notification = MutableStateFlow<AppNotification?>(null)
    val notification: StateFlow<AppNotification?> = _notification.asStateFlow()

    private val _startupState = MutableStateFlow<AppStartupState>(AppStartupState.Onboarding)
    val startupState: StateFlow<AppStartupState> = _startupState.asStateFlow()

    init {
        checkStartupState()
        rescheduleBillReminders()
        observeNotifications()
    }

    private fun checkStartupState() {
        screenModelScope.launch {
            val completed = preferenceRepository.getBoolean("onboarding_completed").firstOrNull() ?: false
            _startupState.value = if (completed) AppStartupState.Dashboard else AppStartupState.Onboarding
        }
    }

    private fun rescheduleBillReminders() {
        screenModelScope.launch {
            runCatching { rescheduleBillRemindersUseCase() }
        }
    }

    private fun observeNotifications() {
        screenModelScope.launch {
            notificationService.notifications.collectLatest { notification ->
                _notification.value = notification
                delay(4000)
                _notification.value = null
            }
        }
    }
}
