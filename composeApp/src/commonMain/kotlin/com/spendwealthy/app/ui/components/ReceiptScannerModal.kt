package com.spendwealthy.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ScannedReceipt(
    val merchantName: String,
    val dateText: String,
    val itemsSummary: String,
    val amount: Long,
    val suggestedCategoryName: String,
    val note: String,
    val icon: ImageVector
)

val SampleReceipts = listOf(
    ScannedReceipt(
        merchantName = "Indomaret Point",
        dateText = "Hari ini, 10:45",
        itemsSummary = "Ultra Milk 1L, Sari Roti, Chitato",
        amount = 147_500L,
        suggestedCategoryName = "Belanja",
        note = "Belanja Mingguan Indomaret",
        icon = Icons.Default.ShoppingCart
    ),
    ScannedReceipt(
        merchantName = "Kopi Kenangan",
        dateText = "Hari ini, 08:30",
        itemsSummary = "2x Kopi Kenangan Mantan Large",
        amount = 48_000L,
        suggestedCategoryName = "Makanan & Minuman",
        note = "Kopi pagi sebelum kerja",
        icon = Icons.Default.Restaurant
    ),
    ScannedReceipt(
        merchantName = "Starbucks Reserve",
        dateText = "Hari ini, 15:20",
        itemsSummary = "1x Caramel Macchiato, 1x Almond Croissant",
        amount = 115_000L,
        suggestedCategoryName = "Makanan & Minuman",
        note = "Meeting sore Starbucks",
        icon = Icons.Default.Restaurant
    ),
    ScannedReceipt(
        merchantName = "SPBU Pertamina 31.12",
        dateText = "Kemarin, 17:20",
        itemsSummary = "Pertamax Turbo 22.5 Liter",
        amount = 325_000L,
        suggestedCategoryName = "Transportasi",
        note = "Isi bensin full tank",
        icon = Icons.Default.LocalGasStation
    ),
    ScannedReceipt(
        merchantName = "Superindo Supermarket",
        dateText = "Kemarin, 19:15",
        itemsSummary = "Apel Fuji, Daging Sapi Segar, Sayur Organik",
        amount = 420_000L,
        suggestedCategoryName = "Belanja",
        note = "Belanja Bulanan Dapur",
        icon = Icons.Default.Storefront
    ),
    ScannedReceipt(
        merchantName = "Gojek / GoCar",
        dateText = "Kemarin, 09:10",
        itemsSummary = "Trip SCBD ke Menara BCA Sudirman",
        amount = 45_000L,
        suggestedCategoryName = "Transportasi",
        note = "Transport perjalanan dinas",
        icon = Icons.Default.DirectionsCar
    ),
    ScannedReceipt(
        merchantName = "Tokopedia Official",
        dateText = "16 Okt 2026",
        itemsSummary = "Keyboard Wireless Mechanical & Mousepad",
        amount = 265_000L,
        suggestedCategoryName = "Belanja",
        note = "Aksesoris kerja Tokopedia",
        icon = Icons.Default.ShoppingCart
    ),
    ScannedReceipt(
        merchantName = "Apotek Century",
        dateText = "15 Okt 2026",
        itemsSummary = "Panadol Flu & Batuk, Redoxon Vitamin C",
        amount = 89_000L,
        suggestedCategoryName = "Kesehatan",
        note = "Vitamin & obat flu keluarga",
        icon = Icons.Default.LocalPharmacy
    )
)

/**
 * Intelligent Local AI Parser for free text and banking SMS notifications.
 */
fun parseFreeTextReceipt(input: String): ScannedReceipt {
    val clean = input.trim()
    val lower = clean.lowercase()

    // 1. Detect Amount (handles "35rb", "35k", "Rp 35.000", "35000", etc.)
    var detectedAmount = 0L
    val rbKRegex = Regex("""(\d+[\d.,]*)\s*(?:rb|k)\b""", RegexOption.IGNORE_CASE)
    val rbKMatch = rbKRegex.find(lower)
    if (rbKMatch != null) {
        val numStr = rbKMatch.groupValues[1].replace(".", "").replace(",", "")
        detectedAmount = (numStr.toDoubleOrNull() ?: 0.0).toLong() * 1000L
    } else {
        val rpRegex = Regex("""(?:rp\.?|idr)\s*(\d+[\d.,]*)""", RegexOption.IGNORE_CASE)
        val rpMatch = rpRegex.find(lower)
        if (rpMatch != null) {
            val numStr = rpMatch.groupValues[1].replace(".", "").replace(",", "")
            detectedAmount = numStr.toLongOrNull() ?: 0L
        } else {
            val numRegex = Regex("""\b\d{4,9}\b""")
            val numMatch = numRegex.find(clean.replace(".", "").replace(",", ""))
            if (numMatch != null) {
                detectedAmount = numMatch.value.toLongOrNull() ?: 0L
            }
        }
    }
    if (detectedAmount <= 0L) detectedAmount = 50_000L

    // 2. Classify Merchant & Category
    val (merchant, category, icon) = when {
        lower.contains("kopi") || lower.contains("fore") || lower.contains("starbucks") || lower.contains("kenangan") || lower.contains("janji jiwa") || lower.contains("cafe") ->
            Triple("Coffee Shop", "Makanan & Minuman", Icons.Default.Restaurant)
        lower.contains("makan") || lower.contains("resto") || lower.contains("mcd") || lower.contains("kfc") || lower.contains("hokben") || lower.contains("bakso") || lower.contains("nasi") ->
            Triple("Restoran & Kuliner", "Makanan & Minuman", Icons.Default.Restaurant)
        lower.contains("indomaret") || lower.contains("alfamart") || lower.contains("superindo") || lower.contains("hypermart") || lower.contains("belanja") ->
            Triple("Minimarket & Belanja", "Belanja", Icons.Default.ShoppingCart)
        lower.contains("tokopedia") || lower.contains("shopee") || lower.contains("lazada") || lower.contains("tiktok") ->
            Triple("E-Commerce Online", "Belanja", Icons.Default.ShoppingCart)
        lower.contains("bensin") || lower.contains("pertamina") || lower.contains("shell") || lower.contains("pertamax") || lower.contains("solar") ->
            Triple("SPBU Bahan Bakar", "Transportasi", Icons.Default.LocalGasStation)
        lower.contains("gojek") || lower.contains("grab") || lower.contains("gocar") || lower.contains("goride") || lower.contains("taxi") || lower.contains("mrt") || lower.contains("krl") || lower.contains("tol") ->
            Triple("Transportasi & Ojek", "Transportasi", Icons.Default.DirectionsCar)
        lower.contains("apotek") || lower.contains("obat") || lower.contains("kimia farma") || lower.contains("century") || lower.contains("guardian") || lower.contains("rs") || lower.contains("dokter") ->
            Triple("Apotek & Medis", "Kesehatan", Icons.Default.LocalPharmacy)
        else -> Triple("Merchant Transaksi", "Lainnya", Icons.AutoMirrored.Filled.ReceiptLong)
    }

    return ScannedReceipt(
        merchantName = merchant,
        dateText = "Hari ini, AI Parsed",
        itemsSummary = if (clean.length > 40) clean.take(40) + "..." else clean.ifBlank { "Item terdeteksi dari teks" },
        amount = detectedAmount,
        suggestedCategoryName = category,
        note = clean.ifBlank { "Pengeluaran $merchant" },
        icon = icon
    )
}

/**
 * Interactive OCR Receipt Scanner Modal.
 * Supports both animated camera OCR scan simulation and live free-text / SMS banking parsing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScannerModal(
    onReceiptScanned: (ScannedReceipt) -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(0) } // 0 = Scanner Viewfinder, 1 = Teks / SMS Bank
    var selectedSample by remember { mutableStateOf(SampleReceipts.first()) }
    var isScanning by remember { mutableStateOf(false) }

    // Free text input state
    var freeTextInput by remember { mutableStateOf("Beli Kopi Kenangan Mantan 2x total 48rb") }
    val parsedReceipt = remember(freeTextInput) { parseFreeTextReceipt(freeTextInput) }

    // Laser animation
    val infiniteTransition = rememberInfiniteTransition()
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    ModalBottomSheet(
        onDismissRequest = { if (!isScanning) onDismiss() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = if (isDark) Color(0xFF090D16) else Color.White,
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
                .widthIn(max = 560.dp)
                .align(Alignment.CenterHorizontally)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFDE43)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Smart Receipt OCR",
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF111827)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "AI Powered",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                            }
                        }
                        Text(
                            text = "Ekstraksi otomatis nominal, toko & kategori",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextMuted
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onDismiss()
                        },
                    shape = CircleShape,
                    color = AppColors.SurfaceRaised,
                    border = BorderStroke(1.dp, AppColors.Outline)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = AppColors.TextMain,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Tab Switcher (Kamera OCR vs Teks SMS)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppColors.SurfaceRaised,
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tab 0: Kamera / Viewfinder
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                activeTab = 0
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeTab == 0) (if (isDark) Color(0xFF1E293B) else Color.White) else Color.Transparent,
                        border = if (activeTab == 0) BorderStroke(1.dp, AppColors.Outline) else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = if (activeTab == 0) AppColors.TextMain else AppColors.TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Kamera Struk",
                                fontSize = 12.sp,
                                fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 0) AppColors.TextMain else AppColors.TextMuted
                            )
                        }
                    }

                    // Tab 1: Input Bebas / SMS Bank
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                activeTab = 1
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeTab == 1) (if (isDark) Color(0xFF1E293B) else Color.White) else Color.Transparent,
                        border = if (activeTab == 1) BorderStroke(1.dp, AppColors.Outline) else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = if (activeTab == 1) AppColors.TextMain else AppColors.TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Teks / SMS Bank",
                                fontSize = 12.sp,
                                fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 1) AppColors.TextMain else AppColors.TextMuted
                            )
                        }
                    }
                }
            }

            if (activeTab == 0) {
                // Tab 0: Kamera / Viewfinder
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF131A29) else Color(0xFFF8F9FA),
                    border = BorderStroke(
                        1.5.dp,
                        if (isScanning) Color(0xFFFFDE43) else if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulated Receipt Card inside Viewfinder
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF1E293B) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = selectedSample.icon,
                                        contentDescription = null,
                                        tint = Color(0xFFFFDE43),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = selectedSample.merchantName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF111827)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = selectedSample.suggestedCategoryName,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = Color(0xFF059669),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = selectedSample.itemsSummary,
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextMuted,
                                maxLines = 1
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = selectedSample.dateText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = AppColors.TextMuted
                                )
                                Text(
                                    text = Money(selectedSample.amount, "IDR").formatWithSymbol(),
                                    style = AppTypography.headlineNumeric(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isDark) Color(0xFFFFDE43) else Color(0xFF111827)
                                    )
                                )
                            }
                        }

                        // Glowing Animated Laser Line
                        if (isScanning) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(laserOffsetY)
                                    .align(Alignment.TopCenter)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0x00FFDE43),
                                                    Color(0xFFFFDE43),
                                                    Color(0xFF10B981),
                                                    Color(0xFFFFDE43),
                                                    Color(0x00FFDE43)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }
                }

                // Sample Struk Carousel
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PILIH CONTOH STRUK UNTUK DIPINDAI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        ),
                        color = AppColors.TextMuted
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SampleReceipts) { sample ->
                            val isSelected = selectedSample == sample
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(enabled = !isScanning) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedSample = sample
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) {
                                    Color(0xFFFFDE43).copy(alpha = if (isDark) 0.2f else 0.3f)
                                } else {
                                    if (isDark) Color(0xFF131A29) else Color(0xFFF3F4F6)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFFFFDE43) else if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = sample.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color(0xFF854D0E) else AppColors.TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = sample.merchantName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            if (isDark) Color(0xFFFFDE43) else Color(0xFF111827)
                                        } else {
                                            AppColors.TextMuted
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Button for Tab 0
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isScanning) {
                            scope.launch {
                                isScanning = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                delay(1000)
                                isScanning = false
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                delay(150)
                                onReceiptScanned(selectedSample)
                                onDismiss()
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFDE43),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp,
                                color = Color(0xFF111827)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Mengekstrak data struk...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF111827)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = Color(0xFF111827),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Pindai Struk ${selectedSample.merchantName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF111827)
                            )
                        }
                    }
                }
            } else {
                // Tab 1: Teks / SMS Bank Parser
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "MASUKKAN ATAU TEMPEL TEKS NOTIFIKASI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        ),
                        color = AppColors.TextMuted
                    )

                    OutlinedTextField(
                        value = freeTextInput,
                        onValueChange = { freeTextInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 4,
                        placeholder = { Text("Contoh: Beli kopi fore 35rb atau Debit BCA 120.000 di Superindo") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AppColors.SurfaceRaised,
                            unfocusedContainerColor = AppColors.SurfaceRaised,
                            focusedBorderColor = AppColors.Primary,
                            unfocusedBorderColor = AppColors.Outline,
                            focusedTextColor = AppColors.TextMain,
                            unfocusedTextColor = AppColors.TextMain
                        )
                    )

                    // Quick prompt chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val prompts = listOf(
                            "Kopi Fore 38rb",
                            "Debit BCA 84.500 Alfamart",
                            "Gojek Ride 22rb",
                            "Makan siang Padang 45.000"
                        )
                        items(prompts) { prompt ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        freeTextInput = prompt
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = AppColors.SurfaceRaised,
                                border = BorderStroke(1.dp, AppColors.Outline)
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Live AI Result Preview Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Hasil Analisis AI:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = parsedReceipt.suggestedCategoryName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = parsedReceipt.merchantName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.TextMain
                                    )
                                    Text(
                                        text = parsedReceipt.itemsSummary,
                                        fontSize = 11.sp,
                                        color = AppColors.TextMuted,
                                        maxLines = 1
                                    )
                                }

                                Text(
                                    text = Money(parsedReceipt.amount, "IDR").formatWithSymbol(),
                                    style = AppTypography.bodyNumeric(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AppColors.TextMain
                                    )
                                )
                            }
                        }
                    }

                    // Action Button for Tab 1
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onReceiptScanned(parsedReceipt)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFDE43),
                            contentColor = Color(0xFF111827)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Catat ${Money(parsedReceipt.amount, "IDR").formatWithSymbol()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF111827)
                        )
                    }
                }
            }
        }
    }
}
