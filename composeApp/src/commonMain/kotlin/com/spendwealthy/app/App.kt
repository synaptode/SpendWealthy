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

@Composable
fun App() {
    val viewModel = koinInject<AppViewModel>()
    val startupState = viewModel.startupState.collectAsState()
    val state = startupState.value
    val rootReady = remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        rootReady.value = true
    }

    SpendWealthyTheme {
        Surface(color = AppColors.Background, modifier = Modifier.fillMaxSize()) {
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
