package com.spendwealthy.app.ui.screens.bill

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.BillStatus
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.components.EmptyState
import com.spendwealthy.app.ui.theme.AppColors

class BillListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BillListViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        
        var showSuccess by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { BillsHeader(onBack = { navigator.pop() }, onAdd = { navigator.push(AddBillScreen()) }) }
                if (state.bills.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No bills yet",
                            message = "Add a bill to keep payment schedules visible.",
                            icon = Icons.AutoMirrored.Filled.ReceiptLong
                        )
                    }
                } else {
                    items(state.bills, key = { it.id }) { bill ->
                        BillItem(
                            bill = bill,
                            status = viewModel.statusOf(bill),
                            paymentWalletName = state.wallets.firstOrNull()?.name,
                            onPaid = {
                                viewModel.markPaid(bill.id)
                                showSuccess = true
                            }
                        )
                    }
                }
            }
            if (showSuccess) {
                Surface(
                    modifier = Modifier.align(Alignment.TopCenter).windowInsetsPadding(WindowInsets.statusBars).padding(top = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = AppColors.Primary
                ) {
                    Text("Bill marked as paid", modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp), color = Color.White, fontWeight = FontWeight.Bold)
                }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1400)
                    showSuccess = false
                }
            }
        }
    }

    @Composable
    private fun BillsHeader(onBack: () -> Unit, onAdd: () -> Unit) {
        Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised).border(1.dp, AppColors.Outline, CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.TextMain, modifier = Modifier.size(20.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Bills", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text("Upcoming payment reminders", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMuted)
            }
            Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.Primary).clickable(onClick = onAdd), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }

    @Composable
    private fun BillItem(
        bill: BillReminder,
        status: BillStatus,
        paymentWalletName: String?,
        onPaid: () -> Unit
    ) {
        val statusColor = when (status) {
            BillStatus.OVERDUE -> AppColors.Danger
            BillStatus.DUE_SOON -> AppColors.Warning
            BillStatus.PAID -> AppColors.Success
            else -> AppColors.Primary
        }

        Surface(shape = RoundedCornerShape(22.dp), color = AppColors.Surface, shadowElevation = 6.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, tint = statusColor, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = bill.name, 
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.Black,
                                    color = AppColors.TextMain
                            )
                            Text(
                                text = "Due: ${bill.dueDate}", 
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextMuted,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(end = 16.dp, top = 16.dp),
                        text = bill.amount.format(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (status == BillStatus.PAID) AppColors.Success else AppColors.DeepBlueText,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = status.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = statusColor
                        )
                    }

                    if (status != BillStatus.PAID) {
                        var isPressed by remember { mutableStateOf(false) }
                        val btnScale by animateFloatAsState(if (isPressed) 0.9f else 1f)
                        
                        AppButton(
                            text = "Mark Paid",
                            onClick = onPaid,
                            modifier = Modifier.width(132.dp).scale(btnScale).padding(end = 16.dp, bottom = 16.dp),
                            enabled = paymentWalletName != null
                        )
                    }
                }
            }
        }
    }
}
