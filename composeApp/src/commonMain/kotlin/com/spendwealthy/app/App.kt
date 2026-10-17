package com.spendwealthy.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.spendwealthy.app.ui.main.MainScreen
import com.spendwealthy.app.ui.theme.SpendWealthyTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.koin.compose.koinInject
import com.spendwealthy.app.ui.screens.onboarding.OnboardingScreen
import com.spendwealthy.app.ui.components.InAppNotificationToast
import com.spendwealthy.app.ui.theme.AppColors
import androidx.compose.ui.unit.dp

import com.spendwealthy.app.core.localization.LocalizationManager
import com.spendwealthy.app.core.localization.LocalAppLanguage
import com.spendwealthy.app.core.localization.LocalStrings
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun App() {
    val viewModel = koinInject<AppViewModel>()
    val localizationManager = koinInject<LocalizationManager>()
    val startupState = viewModel.startupState.collectAsState()
    val currentLanguage by localizationManager.currentLanguage.collectAsState()
    val currentStrings by localizationManager.currentStrings.collectAsState()
    val state = startupState.value
    val rootReady = remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        rootReady.value = true
    }

    CompositionLocalProvider(
        LocalAppLanguage provides currentLanguage,
        LocalStrings provides currentStrings
    ) {
        SpendWealthyTheme(
            darkTheme = when (viewModel.themeMode.collectAsState().value) {
                com.spendwealthy.app.domain.model.ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
                com.spendwealthy.app.domain.model.ThemeMode.DARK -> true
                com.spendwealthy.app.domain.model.ThemeMode.LIGHT -> false
            }
        ) {
            Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize()) {
                when {
                    !rootReady.value || state == AppStartupState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Starting SpendWealthy...",
                                modifier = Modifier.padding(top = 16.dp),
                                color = AppColors.TextMuted
                            )
                        }
                    }
                    state == AppStartupState.Onboarding -> {
                        Navigator(OnboardingScreen())
                    }
                    state == AppStartupState.Locked -> {
                        com.spendwealthy.app.ui.screens.security.SecurityLockScreen(
                            onUnlocked = { viewModel.unlock() }
                        )
                    }
                    state == AppStartupState.Dashboard -> {
                        Navigator(MainScreen())
                    }
                }

                val notification by viewModel.notification.collectAsState()
                InAppNotificationToast(notification = notification)
            }
        }
    }
}
}
