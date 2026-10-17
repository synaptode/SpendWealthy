package com.spendwealthy.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.RecurrenceType
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.theme.AppColors

class AutoMoveSetupScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<HomeViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        var name by remember { mutableStateOf("Monthly Savings") }
        var amount by remember { mutableStateOf("") }
        var selectedSource by remember { mutableStateOf(state.wallets.firstOrNull()) }
        var selectedTargetGoal by remember { mutableStateOf<Goal?>(null) }
        var selectedFrequency by remember { mutableStateOf(RecurrenceType.MONTHLY) }

        Scaffold(
            containerColor = AppColors.Background,
            topBar = {
                Row(
                    Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised).clickable { if (navigator.canPop) navigator.pop() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.TextMain)
                    }
                    Text("Otomasi Auto-Move", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    Box(Modifier.size(44.dp))
                }
            }
        ) { padding ->
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text("Otomatiskan alokasi kekayaan dengan memindahkan dana secara berkala antar rekening dan target impian.", style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMuted)
                }

                item {
                    AppAmountInput(value = amount, onValueChange = { amount = it }, label = "Nominal yang Dipindahkan (Rp)")
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Dari Sumber Dana", fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(state.wallets) { wallet ->
                                SelectionChip(
                                    label = wallet.name,
                                    isSelected = selectedSource?.id == wallet.id,
                                    onClick = { selectedSource = wallet }
                                )
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Ke Pos Target / Tabungan", fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(state.goals) { goal ->
                                SelectionChip(
                                    label = goal.name,
                                    isSelected = selectedTargetGoal?.id == goal.id,
                                    onClick = { selectedTargetGoal = goal }
                                )
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Frekuensi Eksekusi", fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                RecurrenceType.DAILY to "Harian",
                                RecurrenceType.WEEKLY to "Mingguan",
                                RecurrenceType.MONTHLY to "Bulanan"
                            ).forEach { (type, label) ->
                                SelectionChip(
                                    label = label,
                                    isSelected = selectedFrequency == type,
                                    onClick = { selectedFrequency = type }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                    AppButton(
                        text = "Aktifkan Otomatisasi Alokasi",
                        onClick = { if (navigator.canPop) navigator.pop() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = amount.isNotBlank() && selectedSource != null && selectedTargetGoal != null
                    )
                }
            }
        }
    }

    @Composable
    private fun SelectionChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Outline)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = if (isSelected) Color(0xFF090D16) else AppColors.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
