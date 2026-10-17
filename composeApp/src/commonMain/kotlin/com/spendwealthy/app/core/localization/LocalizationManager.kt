package com.spendwealthy.app.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.spendwealthy.app.domain.repository.PreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Centralized manager for application localization and dynamic runtime language switching.
 */
class LocalizationManager(
    private val preferenceRepository: PreferenceRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val _currentLanguage = MutableStateFlow(AppLanguage.INDONESIAN)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentStrings = MutableStateFlow(IdStrings)
    val currentStrings: StateFlow<AppStrings> = _currentStrings.asStateFlow()

    init {
        observeLanguage()
    }

    private fun observeLanguage() {
        scope.launch {
            preferenceRepository.getString(KEY_APP_LANGUAGE).collectLatest { code ->
                if (!code.isNullOrBlank()) {
                    val language = AppLanguage.fromCode(code)
                    _currentLanguage.value = language
                    _currentStrings.value = getStringsForLanguage(language)
                }
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        _currentStrings.value = getStringsForLanguage(language)
        scope.launch {
            preferenceRepository.setString(KEY_APP_LANGUAGE, language.code)
        }
    }

    companion object {
        const val KEY_APP_LANGUAGE = "app_language"

        fun getStringsForLanguage(language: AppLanguage): AppStrings {
            return when (language) {
                AppLanguage.INDONESIAN -> IdStrings
                AppLanguage.ENGLISH -> EnStrings
                AppLanguage.MALAY -> MsStrings
                AppLanguage.THAI -> ThStrings
                AppLanguage.CHINESE -> ZhStrings
                AppLanguage.JAPANESE -> JaStrings
                AppLanguage.VIETNAMESE -> EnStrings
                AppLanguage.ARABIC -> EnStrings
            }
        }
    }
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.INDONESIAN }
val LocalStrings = staticCompositionLocalOf { IdStrings }

/**
 * Convenient accessor for localized strings in any Composable.
 */
object AppStringsProvider {
    val current: AppStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalStrings.current

    val language: AppLanguage
        @Composable
        @ReadOnlyComposable
        get() = LocalAppLanguage.current
}
