package com.spendwealthy.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.model.CurrencyConverter
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailModal(
    transaction: Transaction,
    wallets: List<Wallet> = emptyList(),
    categories: List<Category> = emptyList(),
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val fileShareGateway = koinInject<FileShareGateway>()

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var copiedToClipboard by remember { mutableStateOf(false) }
    var isSharing by remember { mutableStateOf(false) }

    val isIncome = transaction.type == TransactionType.INCOME
    val isTransfer = transaction.type == TransactionType.TRANSFER

    val sourceWallet = remember(wallets, transaction.walletId) {
        wallets.find { it.id == transaction.walletId }
    }
    val targetWallet = remember(wallets, transaction.targetWalletId) {
        transaction.targetWalletId?.let { targetId -> wallets.find { it.id == targetId } }
    }
    val category = remember(categories, transaction.categoryId) {
        transaction.categoryId?.let { catId -> categories.find { it.id == catId } }
    }

    LaunchedEffect(copiedToClipboard) {
        if (copiedToClipboard) {
            delay(2500)
            copiedToClipboard = false
        }
    }

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

    val receiptText = remember(transaction, sourceWallet, targetWallet, category) {
        generateReceiptText(
            transaction = transaction,
            sourceWalletName = sourceWallet?.name,
            targetWalletName = targetWallet?.name,
            categoryName = category?.name
        )
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
                        style = AppTypography.displayNumeric(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when {
                                isIncome -> AppColors.EmeraldGreen
                                isTransfer -> Color(0xFF3B82F6)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    )

                    if (transaction.amount.currency != "IDR") {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyConverter.formatEstimatedIdr(transaction.amount),
                            style = AppTypography.bodyNumeric(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                                TransactionType.TRANSFER -> "Transfer Antar Dompet"
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
                    // Catatan
                    DetailRow(
                        label = "Catatan",
                        value = transaction.note.ifBlank { "Tanpa catatan" },
                        isPrimary = true
                    )

                    // Kategori (dengan badge warna)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kategori",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (category != null) {
                            val catColor = remember(category.colorHex) {
                                parseHexColor(category.colorHex, Color(0xFF10B981))
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = catColor.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, catColor.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(catColor)
                                    )
                                    Text(
                                        text = category.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = catColor
                                    )
                                }
                            }
                        } else if (isTransfer) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "Transfer Dana",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3B82F6),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "Umum",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Merchant / Toko (jika ada)
                    if (transaction.merchant.isNotBlank()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        )
                        DetailRow(
                            label = "Merchant / Toko",
                            value = transaction.merchant,
                            isPrimary = false
                        )
                    }

                    // Aliran Dana / Dompet
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    if (isTransfer) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Alur Transfer",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(7.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = sourceWallet?.name ?: "Dompet Asal",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(13.dp)
                                )
                                Surface(
                                    shape = RoundedCornerShape(7.dp),
                                    color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = targetWallet?.name ?: "Dompet Tujuan",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3B82F6),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        DetailRow(
                            label = if (isIncome) "Masuk Ke" else "Sumber Dana",
                            value = sourceWallet?.name ?: "Dompet Utama",
                            isPrimary = false
                        )
                    }

                    // Tanggal
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    DetailRow(
                        label = "Tanggal",
                        value = "${transaction.date}",
                        isPrimary = false
                    )

                    // ID Transaksi
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    DetailRow(
                        label = "ID Referensi",
                        value = "#${transaction.id.take(8).uppercase()}",
                        isPrimary = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Salin Resi & Bagikan Bukti
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Salin Resi (Clipboard)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            clipboardManager.setText(AnnotatedString(receiptText))
                            copiedToClipboard = true
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = if (copiedToClipboard) AppColors.EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (copiedToClipboard) AppColors.EmeraldGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = if (copiedToClipboard) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (copiedToClipboard) "Resi Tersalin!" else "Salin Resi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (copiedToClipboard) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Button 2: Bagikan Bukti (FileShareGateway)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(enabled = !isSharing) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            coroutineScope.launch {
                                isSharing = true
                                try {
                                    fileShareGateway.shareTextFile(
                                        fileName = "spendwealthy-resi-${transaction.id.take(8).lowercase()}.txt",
                                        mimeType = "text/plain",
                                        content = receiptText
                                    )
                                } finally {
                                    isSharing = false
                                }
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSharing) "Membagikan..." else "Bagikan Bukti",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions: Delete Button
            if (!showDeleteConfirm) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showDeleteConfirm = true
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = AppColors.Danger.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, AppColors.Danger.copy(alpha = 0.25f))
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
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hapus Catatan Ini",
                            fontSize = 13.sp,
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

private fun generateReceiptText(
    transaction: Transaction,
    sourceWalletName: String?,
    targetWalletName: String?,
    categoryName: String?
): String {
    val typeLabel = when (transaction.type) {
        TransactionType.INCOME -> "Pemasukan (+)"
        TransactionType.EXPENSE -> "Pengeluaran (-)"
        TransactionType.TRANSFER -> "Transfer Antar Dompet (⇄)"
    }
    val amountFormatted = (if (transaction.type == TransactionType.INCOME) "+ " else if (transaction.type == TransactionType.TRANSFER) "⇄ " else "- ") + transaction.amount.formatWithSymbol()
    val estIdr = if (transaction.amount.currency != "IDR") {
        " (~ ${CurrencyConverter.formatEstimatedIdr(transaction.amount)})"
    } else ""

    return buildString {
        appendLine("=================================")
        appendLine("🧾 BUKTI TRANSAKSI SPENDWEALTHY")
        appendLine("=================================")
        appendLine("Jenis Transaksi : $typeLabel")
        appendLine("Nominal         : $amountFormatted$estIdr")
        if (!categoryName.isNullOrBlank()) {
            appendLine("Kategori        : $categoryName")
        }
        if (transaction.note.isNotBlank()) {
            appendLine("Catatan         : ${transaction.note}")
        }
        if (transaction.merchant.isNotBlank()) {
            appendLine("Merchant / Toko : ${transaction.merchant}")
        }
        if (transaction.type == TransactionType.TRANSFER) {
            appendLine("Dompet Sumber   : ${sourceWalletName ?: "Dompet Sumber"}")
            appendLine("Dompet Tujuan   : ${targetWalletName ?: "Dompet Tujuan"}")
        } else {
            appendLine("Metode / Dompet : ${sourceWalletName ?: "Dompet Utama"}")
        }
        appendLine("Tanggal         : ${transaction.date}")
        appendLine("No. Referensi   : #${transaction.id.take(8).uppercase()}")
        appendLine("=================================")
        appendLine("Dicatat aman via SpendWealthy App")
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

