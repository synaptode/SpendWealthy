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
                preferenceRepository.getInt("user_avatar_id")
            ) { args: Array<Any?> ->
                val biometric = args[0] as Boolean
                val notifications = args[1] as Boolean
                val privacyMode = args[2] as Boolean
                val pin = args[3] as? String
                val entitlement = args[4] as SubscriptionEntitlement
                val name = args[5] as? String
                val avatarId = args[6] as Int

                SettingsState(
                    biometricEnabled = biometric,
                    notificationsEnabled = notifications,
                    privacyModeEnabled = privacyMode,
                    hasCustomPin = !pin.isNullOrBlank(),
                    entitlement = entitlement,
                    userName = name ?: "User",
                    avatarId = avatarId,
                    privacySummary = "MVP ini local-first: transaksi, budget, dan tagihan berada di database lokal."
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
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
