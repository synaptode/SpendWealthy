package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun GoalContributionDialog(
    goal: Goal,
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onContribute: (Wallet, Long) -> Unit
) {
    var selectedWallet by remember { mutableStateOf(wallets.firstOrNull()) }
    var amount by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        AppGlassCard(backgroundColor = AppColors.Surface) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Add to Goal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Icon(Icons.Default.Close, null, modifier = Modifier.clickable(onClick = onDismiss))
                }
                
                Text(
                    text = "How much would you like to contribute to ${goal.name}?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMuted
                )
                
                AppAmountInput(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Amount"
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("From Wallet", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
                
                AppButton(
                    text = "Contribute",
                    onClick = { 
                        val amt = amount.toLongOrNull() ?: 0L
                        selectedWallet?.let { onContribute(it, amt) }
                    },
                    enabled = amount.isNotBlank() && selectedWallet != null,
                    modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet, 
                null, 
                tint = if (isSelected) Color.White else AppColors.Primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = wallet.name,
                color = if (isSelected) Color.White else AppColors.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
