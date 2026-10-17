package com.spendwealthy.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.CurrencyConverter
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailModal(
    transaction: Transaction,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val isIncome = transaction.type == TransactionType.INCOME
    val isTransfer = transaction.type == TransactionType.TRANSFER

    val brand = if (!isIncome && !isTransfer) {
        KnownBrand.from(transaction.merchant) ?: KnownBrand.from(transaction.note)
    } else null

    val vectorIcon: ImageVector = when {
        isTransfer -> Icons.Default.SwapHoriz
        isIncome -> Icons.Default.Paid
        transaction.note.contains("makan", ignoreCase = true) ||
        transaction.note.contains("food", ignoreCase = true) ||
        transaction.merchant.contains("restaurant", ignoreCase = true) ||
        transaction.merchant.contains("kopi", ignoreCase = true) -> Icons.Default.Restaurant
        transaction.note.contains("transport", ignoreCase = true) ||
        transaction.merchant.contains("uber", ignoreCase = true) ||
        transaction.merchant.contains("gojek", ignoreCase = true) ||
        transaction.merchant.contains("grab", ignoreCase = true) -> Icons.Default.DirectionsCar
        transaction.note.contains("belanja", ignoreCase = true) ||
        transaction.merchant.contains("market", ignoreCase = true) -> Icons.Default.ShoppingCart
        transaction.note.contains("langganan", ignoreCase = true) ||
        transaction.merchant.contains("netflix", ignoreCase = true) -> Icons.Default.Subscriptions
        transaction.merchant.contains("workspace", ignoreCase = true) -> Icons.Default.Business
        else -> Icons.AutoMirrored.Filled.ReceiptLong
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header: Title & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detail Transaksi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onDismiss()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Highlight Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isIncome -> AppColors.EmeraldGreen.copy(alpha = 0.15f)
                                    isTransfer -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                                    brand != null -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (brand != null) {
                            BrandLogo(
                                brand = brand,
                                modifier = Modifier.size(28.dp),
                                tint = if (brand.isMonochrome) MaterialTheme.colorScheme.onSurface else null
                            )
                        } else {
                            Icon(
                                imageVector = vectorIcon,
                                contentDescription = null,
                                tint = when {
                                    isIncome -> AppColors.EmeraldGreen
                                    isTransfer -> Color(0xFF3B82F6)
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = (if (isIncome) "+ " else if (isTransfer) "⇄ " else "- ") + transaction.amount.formatWithSymbol(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            isIncome -> AppColors.EmeraldGreen
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        letterSpacing = (-0.5).sp
                    )

                    if (transaction.amount.currency != "IDR") {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyConverter.formatEstimatedIdr(transaction.amount),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isIncome -> AppColors.EmeraldGreen.copy(alpha = 0.12f)
                            isTransfer -> Color(0xFF3B82F6).copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        }
                    ) {
                        Text(
                            text = when (transaction.type) {
                                TransactionType.INCOME -> "Pemasukan"
                                TransactionType.EXPENSE -> "Pengeluaran"
                                TransactionType.TRANSFER -> "Transfer"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isIncome -> AppColors.EmeraldGreen
                                isTransfer -> Color(0xFF3B82F6)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Breakdown Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(
                        label = "Catatan",
                        value = transaction.note.ifBlank { "Tanpa catatan" },
                        isPrimary = true
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    DetailRow(
                        label = "Merchant / Toko",
                        value = transaction.merchant.ifBlank { "-" },
                        isPrimary = false
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    DetailRow(
                        label = "Tanggal",
                        value = "${transaction.date}",
                        isPrimary = false
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    DetailRow(
                        label = "ID Transaksi",
                        value = transaction.id.take(8).uppercase(),
                        isPrimary = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions: Delete Button
            if (!showDeleteConfirm) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showDeleteConfirm = true
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.Danger.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, AppColors.Danger.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = AppColors.Danger,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hapus Transaksi Ini",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Danger
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Yakin ingin menghapus catatan transaksi ini?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Danger,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showDeleteConfirm = false
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Batal",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onDelete(transaction.id)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = AppColors.Danger
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Ya, Hapus",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isPrimary: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
