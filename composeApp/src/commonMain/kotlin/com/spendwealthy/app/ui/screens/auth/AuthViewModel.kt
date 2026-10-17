package com.spendwealthy.app.ui.screens.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.repository.PreferenceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedCurrency: String = "IDR",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = true,
    val agreeTerms: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) : UiState

class AuthViewModel(
    private val preferenceRepository: PreferenceRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email.trim(), errorMessage = null) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    fun onCurrencyChanged(currency: String) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleRememberMe() {
        _uiState.update { it.copy(rememberMe = !it.rememberMe) }
    }

    fun toggleAgreeTerms() {
        _uiState.update { it.copy(agreeTerms = !it.agreeTerms, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email atau username wajib diisi") }
            return
        }
        if (!state.email.contains("@") && !state.email.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Format email tidak valid") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Kata sandi wajib diisi") }
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(500) // Realistic responsive authentication latency

            preferenceRepository.setBoolean("is_logged_in", true)
            preferenceRepository.setBoolean("onboarding_completed", true)
            preferenceRepository.setString("user_email", state.email)
            val displayName = state.email.substringBefore("@").replaceFirstChar { it.uppercase() }
            preferenceRepository.setString("user_name", displayName)
            preferenceRepository.setString("auth_token", "sw_token_${state.email.hashCode()}")

            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            onSuccess()
        }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama lengkap wajib diisi") }
            return
        }
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email wajib diisi") }
            return
        }
        if (!state.email.contains("@") || !state.email.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Format email tidak valid") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Kata sandi minimal 6 karakter") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Konfirmasi kata sandi tidak cocok") }
            return
        }
        if (!state.agreeTerms) {
            _uiState.update { it.copy(errorMessage = "Harap setujui Syarat & Kebijakan Privasi") }
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(600)

            preferenceRepository.setBoolean("is_logged_in", true)
            preferenceRepository.setBoolean("onboarding_completed", true)
            preferenceRepository.setString("user_name", state.name)
            preferenceRepository.setString("user_email", state.email)
            preferenceRepository.setString("user_currency", state.selectedCurrency)
            preferenceRepository.setString("auth_token", "sw_token_${state.email.hashCode()}")

            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            onSuccess()
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(300)
            preferenceRepository.setBoolean("is_logged_in", false)
            preferenceRepository.setBoolean("onboarding_completed", true)
            preferenceRepository.setString("user_name", "Tamu Sovereign")
            preferenceRepository.setString("user_email", "guest@spendwealthy.local")
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            onSuccess()
        }
    }

    fun logout(onSuccess: () -> Unit) {
        screenModelScope.launch {
            preferenceRepository.setBoolean("is_logged_in", false)
            preferenceRepository.setString("user_email", "")
            preferenceRepository.setString("auth_token", "")
            onSuccess()
        }
    }
}
