package com.spendwealthy.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.spendwealthy.app.ui.main.MainScreen
import com.spendwealthy.app.ui.theme.SpendWealthyTheme
import cafe.adriel.voyager.transitions.SlideTransition
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject
import com.spendwealthy.app.ui.screens.onboarding.OnboardingScreen

@Composable
fun App() {
    val viewModel = koinInject<AppViewModel>()
    val startupState = viewModel.startupState.collectAsState()
    val state = startupState.value

    SpendWealthyTheme {
        when (state) {
            AppStartupState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            AppStartupState.Onboarding -> {
                Navigator(OnboardingScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
            AppStartupState.Dashboard -> {
                Navigator(MainScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}