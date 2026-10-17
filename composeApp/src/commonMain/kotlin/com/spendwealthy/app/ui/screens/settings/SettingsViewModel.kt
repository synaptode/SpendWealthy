package com.spendwealthy.app.ui.screens.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.SubscriptionEntitlement
import com.spendwealthy.app.domain.model.SubscriptionPlan
import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.domain.repository.SubscriptionRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.DataService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsState(
    val biometricEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val privacyModeEnabled: Boolean = false,
    val hasCustomPin: Boolean = false,
    val entitlement: SubscriptionEntitlement = SubscriptionEntitlement(SubscriptionPlan.FREE, true),
    val userName: String = "User",
    val userEmail: String? = null,
    val isLoggedIn: Boolean = false,
    val userCurrency: String = "IDR",
    val avatarId: Int = 0,
    val privacySummary: String = "Data utama disimpan lokal di perangkat.",
    val exportStatus: String? = null
) : UiState

class SettingsViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val dataService: DataService
) : ScreenModel {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        screenModelScope.launch {
            combine(
                preferenceRepository.getBoolean(KEY_BIOMETRIC),
                preferenceRepository.getBoolean(KEY_NOTIFICATIONS),
                preferenceRepository.getBoolean(KEY_PRIVACY_MODE),
                preferenceRepository.getString(KEY_SECURITY_PIN),
                subscriptionRepository.getEntitlement(),
                preferenceRepository.getString("user_name"),
                preferenceRepository.getString("user_email"),
                preferenceRepository.getBoolean("is_logged_in"),
                preferenceRepository.getString("user_currency"),
                preferenceRepository.getInt("user_avatar_id")
            ) { args: Array<Any?> ->
                val biometric = args[0] as Boolean
                val notifications = args[1] as Boolean
                val privacyMode = args[2] as Boolean
                val pin = args[3] as? String
                val entitlement = args[4] as SubscriptionEntitlement
                val name = args[5] as? String
                val email = args[6] as? String
                val loggedIn = args[7] as Boolean
                val currency = args[8] as? String ?: "IDR"
                val avatarId = args[9] as Int

                SettingsState(
                    biometricEnabled = biometric,
                    notificationsEnabled = notifications,
                    privacyModeEnabled = privacyMode,
                    hasCustomPin = !pin.isNullOrBlank(),
                    entitlement = entitlement,
                    userName = name ?: "User",
                    userEmail = email,
                    isLoggedIn = loggedIn,
                    userCurrency = currency,
                    avatarId = avatarId,
                    privacySummary = "SpendWealthy beroperasi offline-first: enkripsi AES-256 lokal dengan sinkronisasi opsional."
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }

    fun updateProfile(name: String, currency: String) {
        screenModelScope.launch {
            if (name.isNotBlank()) {
                preferenceRepository.setString("user_name", name.trim())
            }
            if (currency.isNotBlank()) {
                preferenceRepository.setString("user_currency", currency.trim())
                preferenceRepository.setString("primary_currency", currency.trim())
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        screenModelScope.launch {
            preferenceRepository.setBoolean("is_logged_in", false)
            preferenceRepository.setString("user_name", "Tamu Sovereign")
            preferenceRepository.setString("user_email", "")
            preferenceRepository.setString("auth_token", "")
            onLoggedOut()
        }
    }

    fun exportFamilyData() {
        screenModelScope.launch {
            _uiState.update { it.copy(exportStatus = "Preparing sync data...") }
            val wallets = walletRepository.getWallets().first()
            val transactions = transactionRepository.getTransactions().first()
            val json = dataService.exportToJson(wallets, transactions)
            // In a real app, we would share this via Intent or Clipboard
            _uiState.update { it.copy(exportStatus = "Data is ready for export.") }
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        screenModelScope.launch {
            preferenceRepository.setBoolean(KEY_BIOMETRIC, enabled)
        }
    }

    fun setPrivacyModeEnabled(enabled: Boolean) {
        screenModelScope.launch {
            preferenceRepository.setBoolean(KEY_PRIVACY_MODE, enabled)
        }
    }

    fun setSecurityPin(pin: String) {
        screenModelScope.launch {
            preferenceRepository.setString(KEY_SECURITY_PIN, pin)
            // Auto enable biometric/pin lock when user explicitly configures a PIN
            preferenceRepository.setBoolean(KEY_BIOMETRIC, true)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        screenModelScope.launch {
            preferenceRepository.setBoolean(KEY_NOTIFICATIONS, enabled)
        }
    }

    fun setPlan(plan: SubscriptionPlan) {
        screenModelScope.launch {
            subscriptionRepository.setLocalPlan(plan)
        }
    }

    private companion object {
        const val KEY_BIOMETRIC = "biometric_enabled"
        const val KEY_NOTIFICATIONS = "notifications_enabled"
        const val KEY_PRIVACY_MODE = "privacy_mode_enabled"
        const val KEY_SECURITY_PIN = "user_security_pin"
    }
}
