package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalContributionDialog(
    goal: Goal,
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onContribute: (Wallet, Long) -> Unit
) {
    var selectedWallet by remember { mutableStateOf(wallets.firstOrNull()) }
    var amount by remember { mutableStateOf("") }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = AppColors.Surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 540.dp)
                .align(Alignment.CenterHorizontally)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Nabung ke Impian",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppColors.SurfaceRaised)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Tutup",
                        tint = AppColors.TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Text(
                text = "Berapa nominal yang ingin kamu tabung untuk ${goal.name}?",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextMuted
            )
            
            AppAmountInput(
                value = amount,
                onValueChange = { amount = it },
                label = "Nominal Tabungan (Rp)"
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Sumber Dompet", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(wallets) { wallet ->
                        WalletChip(
                            wallet = wallet,
                            isSelected = selectedWallet?.id == wallet.id,
                            onClick = { selectedWallet = wallet }
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.TextMuted
                    )
                ) {
                    Text("Batal", fontWeight = FontWeight.SemiBold)
                }

                AppButton(
                    text = "Simpan",
                    onClick = { 
                        val amt = amount.toLongOrNull() ?: 0L
                        selectedWallet?.let { onContribute(it, amt) }
                    },
                    enabled = amount.isNotBlank() && selectedWallet != null,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                )
            }
        }
    }
}

@Composable
private fun WalletChip(wallet: Wallet, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet, 
                null, 
                tint = if (isSelected) AppColors.OnPrimary else AppColors.Primary,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = wallet.name,
                    color = if (isSelected) AppColors.OnPrimary else AppColors.TextMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = wallet.balance.formatWithSymbol(),
                    style = AppTypography.labelNumeric(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) AppColors.OnPrimary.copy(alpha = 0.8f) else AppColors.TextMuted
                    )
                )
            }
        }
    }
}
