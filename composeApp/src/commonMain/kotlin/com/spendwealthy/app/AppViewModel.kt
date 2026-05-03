package com.spendwealthy.app

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.domain.usecase.RescheduleBillRemindersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class AppStartupState {
    object Loading : AppStartupState()
    object Onboarding : AppStartupState()
    object Dashboard : AppStartupState()
}

class AppViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val rescheduleBillRemindersUseCase: RescheduleBillRemindersUseCase
) : ScreenModel {

    private val _startupState = MutableStateFlow<AppStartupState>(AppStartupState.Loading)
    val startupState: StateFlow<AppStartupState> = _startupState.asStateFlow()

    init {
        checkStartupState()
        rescheduleBillReminders()
    }

    private fun checkStartupState() {
        screenModelScope.launch {
            preferenceRepository.getBoolean("onboarding_completed").collectLatest { completed ->
                _startupState.value = if (completed) AppStartupState.Dashboard else AppStartupState.Onboarding
            }
        }
    }

    private fun rescheduleBillReminders() {
        screenModelScope.launch {
            runCatching { rescheduleBillRemindersUseCase() }
        }
    }
}
