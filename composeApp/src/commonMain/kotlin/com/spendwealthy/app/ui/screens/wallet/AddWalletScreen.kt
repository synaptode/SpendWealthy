package com.spendwealthy.app.ui.screens.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.components.AppTextField
import com.spendwealthy.app.ui.theme.AppColors

class AddWalletScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AddWalletViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val haptic = LocalHapticFeedback.current

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    AddWalletEffect.NavigateBack -> navigator.pop()
                    is AddWalletEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            containerColor = AppColors.Background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppColors.Background)
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                item {
                    FormHeader(
                        title = "Tambah Dompet",
                        subtitle = "Catat sumber saldo baru",
                        onClose = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            navigator.pop()
                        }
                    )
                }

                item {
                    FormSection(title = "Nama Dompet / Rekening", icon = Icons.Default.AccountBalanceWallet) {
                        AppTextField(
                            value = state.name,
                            onValueChange = { viewModel.onEvent(AddWalletEvent.NameChanged(it)) },
                            label = "Nama",
                            placeholder = "BCA, Mandiri, GoPay, Tunai Dompet"
                        )
                    }
                }

                item {
                    FormSection(title = "Tipe Sumber Dana", icon = Icons.Default.AccountBalanceWallet) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(WalletType.entries.toList()) { type ->
                                val label = when (type) {
                                    WalletType.BANK -> "Bank"
                                    WalletType.EWALLET -> "E-Wallet"
                                    WalletType.CASH -> "Tunai"
                                    WalletType.CREDIT_CARD -> "Kartu Kredit"
                                    WalletType.PAYLATER -> "Paylater"
                                    WalletType.SAVINGS -> "Tabungan"
                                }
                                ChoiceChip(
                                    text = label,
                                    selected = state.type == type,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.onEvent(AddWalletEvent.TypeChanged(type))
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    FormSection(title = "Saldo Awal", icon = Icons.Default.Payments) {
                        AppAmountInput(
                            value = state.initialBalance,
                            onValueChange = { viewModel.onEvent(AddWalletEvent.BalanceChanged(it)) },
                            label = "Nominal Saldo (Rp)"
                        )
                    }
                }

                item {
                    state.errorMessage?.let {
                        Text(
                            text = it,
                            color = AppColors.Danger,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    AppButton(
                        text = "Simpan Dompet",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onEvent(AddWalletEvent.SaveClicked)
                        },
                        isLoading = state.isLoading,
                        enabled = state.name.isNotBlank() && !state.isLoading
                    )
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }

    @Composable
    private fun FormHeader(title: String, subtitle: String, onClose: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClose),
                shape = CircleShape,
                color = AppColors.Surface,
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted
                )
            }

            Spacer(Modifier.size(42.dp))
        }
    }

    @Composable
    private fun FormSection(
        title: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        content: @Composable () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AppColors.Surface,
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    content()
                }
            }
        }
    }

    @Composable
    private fun ChoiceChip(text: String, selected: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            color = if (selected) AppColors.Primary else AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, if (selected) AppColors.Primary else AppColors.Outline)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                color = if (selected) Color.White else AppColors.TextMain,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
