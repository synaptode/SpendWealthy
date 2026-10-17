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

import com.spendwealthy.app.domain.model.ThemeMode

import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.usecase.ExportMonthlyTransactionsCsvUseCase
import com.spendwealthy.app.core.localization.AppLanguage
import com.spendwealthy.app.core.localization.LocalizationManager
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val currentLanguage: AppLanguage = AppLanguage.INDONESIAN,
    val privacySummary: String = "Data utama disimpan lokal di perangkat.",
    val exportStatus: String? = null
) : UiState

class SettingsViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val dataService: DataService,
    private val fileShareGateway: FileShareGateway? = null,
    private val exportMonthlyTransactionsCsvUseCase: ExportMonthlyTransactionsCsvUseCase? = null,
    private val localizationManager: LocalizationManager? = null
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
                preferenceRepository.getInt("user_avatar_id"),
                preferenceRepository.getString("theme_mode")
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
                val themeModeStr = args[10] as? String
                val themeMode = when (themeModeStr?.uppercase()) {
                    "LIGHT" -> ThemeMode.LIGHT
                    "DARK" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }

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
                    themeMode = themeMode,
                    currentLanguage = _uiState.value.currentLanguage,
                    privacySummary = "SpendWealthy beroperasi offline-first: enkripsi AES-256 lokal dengan sinkronisasi opsional."
                )
            }.collectLatest { state ->
                _uiState.update { current ->
                    state.copy(currentLanguage = current.currentLanguage)
                }
            }
        }

        screenModelScope.launch {
            preferenceRepository.getString("app_language").collectLatest { code ->
                val lang = AppLanguage.fromCode(code)
                _uiState.update { it.copy(currentLanguage = lang) }
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        screenModelScope.launch {
            if (localizationManager != null) {
                localizationManager.setLanguage(language)
            } else {
                preferenceRepository.setString("app_language", language.code)
            }
            _uiState.update { it.copy(currentLanguage = language) }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        screenModelScope.launch {
            preferenceRepository.setString("theme_mode", mode.name)
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
        backupLocalDataJson()
    }

    fun backupLocalDataJson() {
        screenModelScope.launch {
            _uiState.update { it.copy(exportStatus = "Menyiapkan berkas cadangan JSON...") }
            try {
                val wallets = walletRepository.getWallets().first()
                val transactions = transactionRepository.getTransactions().first()
                val json = dataService.exportToJson(wallets, transactions)
                val now = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val fileName = "spendwealthy-backup-${now.date}.json"

                if (fileShareGateway != null) {
                    when (val result = fileShareGateway.shareTextFile(fileName, "application/json", json)) {
                        is GatewayResult.Success -> {
                            _uiState.update { it.copy(exportStatus = "Cadangan JSON berhasil dibagikan.") }
                        }
                        is GatewayResult.Failure -> {
                            _uiState.update { it.copy(exportStatus = "Gagal membagikan cadangan: ${result.message}") }
                        }
                    }
                } else {
                    _uiState.update { it.copy(exportStatus = "Cadangan JSON siap diekspor.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(exportStatus = "Terjadi kesalahan: ${e.message}") }
            }
        }
    }

    fun exportTransactionsCsv() {
        screenModelScope.launch {
            _uiState.update { it.copy(exportStatus = "Menyiapkan ekspor CSV transaksi...") }
            try {
                val now = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                if (exportMonthlyTransactionsCsvUseCase != null) {
                    when (val result = exportMonthlyTransactionsCsvUseCase.invoke(now.year, now.monthNumber)) {
                        is GatewayResult.Success -> {
                            _uiState.update { it.copy(exportStatus = "Laporan transaksi CSV berhasil diekspor.") }
                        }
                        is GatewayResult.Failure -> {
                            _uiState.update { it.copy(exportStatus = "Gagal mengekspor CSV: ${result.message}") }
                        }
                    }
                } else {
                    _uiState.update { it.copy(exportStatus = "Ekspor CSV siap.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(exportStatus = "Terjadi kesalahan: ${e.message}") }
            }
        }
    }

    fun clearExportStatus() {
        _uiState.update { it.copy(exportStatus = null) }
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
