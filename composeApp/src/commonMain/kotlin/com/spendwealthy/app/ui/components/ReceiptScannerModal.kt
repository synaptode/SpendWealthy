package com.spendwealthy.app.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
        merchantName = "SPBU Pertamina 31.12",
        dateText = "Kemarin, 17:20",
        itemsSummary = "Pertamax Turbo 22.5 Liter",
        amount = 325_000L,
        suggestedCategoryName = "Transportasi",
        note = "Isi bensin full tank",
        icon = Icons.Default.LocalGasStation
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
 * Interactive OCR Receipt Scanner Modal.
 * Features an animated laser scanner viewfinder, sample receipt selectors,
 * and automatic extraction of merchant, nominal, and category.
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

    var selectedSample by remember { mutableStateOf(SampleReceipts.first()) }
    var isScanning by remember { mutableStateOf(false) }
    var scanCompleted by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                    // Top Handle Bar & Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
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
                                Text(
                                    text = "Smart Receipt OCR",
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF111827)
                                )
                                Text(
                                    text = "Pindai struk & catat instan tanpa ketik",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppColors.TextMuted
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .size(36.dp)
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
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Viewfinder / Scanner Simulation Window
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp),
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
                                        text = Money(selectedSample.amount, "IDR").format(),
                                        fontWeight = FontWeight.Black,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isDark) Color(0xFFFFDE43) else Color(0xFF111827)
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

                    // Action Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !isScanning) {
                                scope.launch {
                                    isScanning = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    delay(1200) // Realistic AI extraction delay
                                    isScanning = false
                                    scanCompleted = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    delay(200)
                                    onReceiptScanned(selectedSample)
                                    onDismiss()
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFDE43),
                        shadowElevation = 4.dp
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
        }
    }
}
