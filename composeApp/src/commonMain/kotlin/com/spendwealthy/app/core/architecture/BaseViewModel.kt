package com.spendwealthy.app.core.architecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Marker interface for UI State
 */
interface UiState

/**
 * Marker interface for UI Events (Intents)
 */
interface UiEvent

/**
 * Marker interface for UI Side Effects (One-time actions)
 */
interface UiEffect

/**
 * Base ViewModel for MVI architecture
 */
abstract class BaseViewModel<State : UiState, Event : UiEvent, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected var currentState: State
        get() = _uiState.value
        set(value) {
            _uiState.value = value
        }

    abstract fun onEvent(event: Event)

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    protected fun updateState(reducer: State.() -> State) {
        _uiState.value = currentState.reducer()
    }
}
