package com.spendwealthy.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.ui.theme.AppColors

data class CategoryIconOption(
    val name: String,
    val icon: ImageVector,
    val label: String
)

val CATEGORY_ICON_OPTIONS = listOf(
    CategoryIconOption("restaurant", Icons.Default.Restaurant, "Makan"),
    CategoryIconOption("car", Icons.Default.DirectionsCar, "Transport"),
    CategoryIconOption("shopping", Icons.Default.ShoppingCart, "Belanja"),
    CategoryIconOption("subscriptions", Icons.Default.Subscriptions, "Tagihan"),
    CategoryIconOption("fitness", Icons.Default.FitnessCenter, "Olahraga"),
    CategoryIconOption("school", Icons.Default.School, "Edukasi"),
    CategoryIconOption("medical", Icons.Default.MedicalServices, "Kesehatan"),
    CategoryIconOption("savings", Icons.Default.Savings, "Investasi"),
    CategoryIconOption("gift", Icons.Default.CardGiftcard, "Hadiah"),
    CategoryIconOption("flight", Icons.Default.Flight, "Liburan"),
    CategoryIconOption("pets", Icons.Default.Pets, "Peliharaan"),
    CategoryIconOption("home", Icons.Default.Home, "Rumah"),
    CategoryIconOption("work", Icons.Default.Work, "Pekerjaan"),
    CategoryIconOption("coffee", Icons.Default.LocalCafe, "Kopi & Cafe"),
    CategoryIconOption("bolt", Icons.Default.Bolt, "Utilitas"),
    CategoryIconOption("game", Icons.Default.SportsEsports, "Hiburan")
)

val CATEGORY_COLOR_PALETTE = listOf(
    "#FFDE43" to "Canary Gold",
    "#10B981" to "Emerald Green",
    "#6366F1" to "Indigo",
    "#EF4444" to "Crimson Red",
    "#F59E0B" to "Amber",
    "#3B82F6" to "Royal Blue",
    "#A855F7" to "Purple",
    "#06B6D4" to "Cyan"
)

fun parseHexColor(hex: String, fallback: Color = Color(0xFFFFDE43)): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorLong = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or colorLong)
        } else if (clean.length == 8) {
            Color(colorLong)
        } else {
            fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

fun resolveCategoryIcon(iconName: String): ImageVector {
    return CATEGORY_ICON_OPTIONS.find { it.name.equals(iconName, ignoreCase = true) }?.icon
        ?: Icons.Default.Category
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryModal(
    onDismiss: () -> Unit,
    onCategoryCreated: (Category) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var categoryName by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("savings") }
    var selectedColorHex by remember { mutableStateOf("#FFDE43") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val activeColor = remember(selectedColorHex) { parseHexColor(selectedColorHex) }
    val activeIcon = remember(selectedIconName) { resolveCategoryIcon(selectedIconName) }

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
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tambah Kategori Baru",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = "Kustomisasi ikon dan warna sesuai kebutuhan",
                        fontSize = 12.sp,
                        color = AppColors.TextMuted
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = AppColors.SurfaceRaised,
                    border = BorderStroke(1.dp, AppColors.Outline),
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
                            tint = AppColors.TextMain,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Preview Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AppColors.SurfaceRaised,
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pratinjau Kategori:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = activeColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.5.dp, activeColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = activeIcon,
                                contentDescription = null,
                                tint = if (selectedColorHex == "#FFDE43") AppColors.TextMain else activeColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (categoryName.isBlank()) "Nama Kategori" else categoryName.trim(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedColorHex == "#FFDE43") AppColors.TextMain else activeColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            OutlinedTextField(
                value = categoryName,
                onValueChange = {
                    categoryName = it
                    if (errorMessage != null) errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nama Kategori (contoh: Investasi)") },
                singleLine = true,
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it, color = AppColors.Danger) } },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = AppColors.Outline,
                    focusedTextColor = AppColors.TextMain,
                    unfocusedTextColor = AppColors.TextMain,
                    focusedContainerColor = AppColors.SurfaceRaised,
                    unfocusedContainerColor = AppColors.SurfaceRaised
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Color Selector Row
            Text(
                text = "Pilih Aksen Warna",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CATEGORY_COLOR_PALETTE) { (hex, _) ->
                    val color = parseHexColor(hex)
                    val isSelected = selectedColorHex.equals(hex, ignoreCase = true)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) AppColors.TextMain else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedColorHex = hex
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Terpilih",
                                tint = if (hex == "#FFDE43") Color(0xFF111827) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Icon Selector Grid via Chunked Rows (100% native smooth scroll)
            Text(
                text = "Pilih Ikon Representatif",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val iconChunks = remember { CATEGORY_ICON_OPTIONS.chunked(4) }
                iconChunks.forEach { rowIcons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowIcons.forEach { opt ->
                            val isSelected = selectedIconName == opt.name
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AppColors.CharcoalBlack else AppColors.SurfaceRaised,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AppColors.CharcoalBlack else AppColors.Outline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedIconName = opt.name
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = opt.icon,
                                        contentDescription = opt.label,
                                        tint = if (isSelected) Color.White else AppColors.TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = opt.label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else AppColors.TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    if (categoryName.isBlank()) {
                        errorMessage = "Nama kategori tidak boleh kosong"
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        return@Button
                    }
                    val newCategory = Category(
                        id = "cat_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}",
                        name = categoryName.trim(),
                        iconName = selectedIconName,
                        colorHex = selectedColorHex,
                        isDefault = false
                    )
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCategoryCreated(newCategory)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.CharcoalBlack,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Simpan Kategori",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
