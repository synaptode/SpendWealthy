package com.spendwealthy.app

import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.domain.usecase.RescheduleBillRemindersUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.spendwealthy.app.domain.service.*

import kotlinx.coroutines.flow.combine

sealed class AppStartupState {
    object Loading : AppStartupState()
    object Onboarding : AppStartupState()
    object Locked : AppStartupState()
    object Dashboard : AppStartupState()
}

class AppViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val rescheduleBillRemindersUseCase: RescheduleBillRemindersUseCase,
    private val notificationService: NotificationService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val _notification = MutableStateFlow<AppNotification?>(null)
    val notification: StateFlow<AppNotification?> = _notification.asStateFlow()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _startupState = MutableStateFlow<AppStartupState>(AppStartupState.Loading)
    val startupState: StateFlow<AppStartupState> = _startupState.asStateFlow()

    private val _themeMode = MutableStateFlow(com.spendwealthy.app.domain.model.ThemeMode.SYSTEM)
    val themeMode: StateFlow<com.spendwealthy.app.domain.model.ThemeMode> = _themeMode.asStateFlow()

    init {
        checkStartupState()
        rescheduleBillReminders()
        observeNotifications()
        observeThemeMode()
    }

    private fun checkStartupState() {
        scope.launch {
            combine(
                preferenceRepository.getBoolean("onboarding_completed"),
                preferenceRepository.getBoolean("biometric_enabled"),
                _isUnlocked
            ) { completed, biometricEnabled, unlocked ->
                when {
                    !completed -> AppStartupState.Onboarding
                    biometricEnabled && !unlocked -> AppStartupState.Locked
                    else -> AppStartupState.Dashboard
                }
            }.collectLatest { state ->
                _startupState.value = state
            }
        }
    }

    fun unlock() {
        _isUnlocked.value = true
    }

    fun lock() {
        _isUnlocked.value = false
    }

    private fun rescheduleBillReminders() {
        scope.launch {
            runCatching { rescheduleBillRemindersUseCase() }
        }
    }

    private fun observeNotifications() {
        scope.launch {
            notificationService.notifications.collectLatest { notification ->
                _notification.value = notification
                delay(4000)
                _notification.value = null
            }
        }
    }

    private fun observeThemeMode() {
        scope.launch {
            preferenceRepository.getString("theme_mode").collectLatest { modeString ->
                val mode = when (modeString?.uppercase()) {
                    "LIGHT" -> com.spendwealthy.app.domain.model.ThemeMode.LIGHT
                    "DARK" -> com.spendwealthy.app.domain.model.ThemeMode.DARK
                    else -> com.spendwealthy.app.domain.model.ThemeMode.SYSTEM
                }
                _themeMode.value = mode
            }
        }
    }

    fun setThemeMode(mode: com.spendwealthy.app.domain.model.ThemeMode) {
        scope.launch {
            preferenceRepository.setString("theme_mode", mode.name)
        }
    }
}
