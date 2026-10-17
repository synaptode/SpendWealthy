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
        containerColor = Color.White,
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
                    color = Color(0xFF111827)
                )

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF3F4F6),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Highlight Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFF9FAFB),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
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
                                    isIncome -> Color(0xFFECFDF5)
                                    isTransfer -> Color(0xFFEFF6FF)
                                    else -> Color(0xFFF3F4F6)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vectorIcon,
                            contentDescription = null,
                            tint = when {
                                isIncome -> Color(0xFF059669)
                                isTransfer -> Color(0xFF2563EB)
                                else -> Color(0xFF111827)
                            },
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = (if (isIncome) "+ " else if (isTransfer) "⇄ " else "- ") + transaction.amount.format(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            isIncome -> Color(0xFF059669)
                            else -> Color(0xFF111827)
                        },
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isIncome -> Color(0xFFECFDF5)
                            isTransfer -> Color(0xFFEFF6FF)
                            else -> Color(0xFFF3F4F6)
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
                                isIncome -> Color(0xFF059669)
                                isTransfer -> Color(0xFF2563EB)
                                else -> Color(0xFF374151)
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
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(
                        label = "Catatan",
                        value = transaction.note.ifBlank { "Tanpa catatan" },
                        isPrimary = true
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF3F4F6))
                    DetailRow(
                        label = "Merchant / Toko",
                        value = transaction.merchant.ifBlank { "-" },
                        isPrimary = false
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF3F4F6))
                    DetailRow(
                        label = "Tanggal",
                        value = "${transaction.date}",
                        isPrimary = false
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF3F4F6))
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
                    color = Color(0xFFFEE2E2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hapus Transaksi Ini",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
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
                        color = Color(0xFFDC2626),
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
                            color = Color(0xFFF3F4F6)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Batal",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4B5563)
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
                            color = Color(0xFFDC2626)
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
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.SemiBold,
            color = Color(0xFF111827)
        )
    }
}
