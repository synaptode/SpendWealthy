package com.spendwealthy.app.ui.screens.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.SubscriptionPlan
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.theme.AppColors

/**
 * Modern, high-converting subscription paywall.
 * Avoids aggressive friction by emphasizing user empowerment, local-first privacy, and tangible value.
 */
class PaywallScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current
        var selectedPlan by remember { mutableStateOf(SubscriptionPlan.PREMIUM_PERSONAL) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    PaywallHeader(onClose = { navigator.pop() })
                }

                item {
                    PlanSelector(
                        selectedPlan = selectedPlan,
                        onPlanSelected = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedPlan = it
                        }
                    )
                }

                item {
                    FeatureList()
                }

                item {
                    Spacer(Modifier.height(28.dp))
                    Box(Modifier.padding(horizontal = 24.dp)) {
                        AppButton(
                            text = "Mulai Akses Penuh (${if (selectedPlan == SubscriptionPlan.PREMIUM_PERSONAL) "Personal" else "Keluarga"})",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                navigator.pop()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Text(
                        text = "Langganan dapat dibatalkan kapan saja. Data keuanganmu tetap tersimpan privat di perangkat.",
                        fontSize = 12.sp,
                        color = AppColors.TextMuted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun PaywallHeader(onClose: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AppColors.CardDark,
                            Color(0xFF0F1520)
                        )
                    )
                )
                .statusBarsPadding()
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = AppColors.Accent,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = AppColors.CardDark,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "SpendWealthy Pro 🐾",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Text(
                    text = "Bebaskan kendali uangmu dengan fitur premium tanpa batas kuota",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    @Composable
    private fun PlanSelector(
        selectedPlan: SubscriptionPlan,
        onPlanSelected: (SubscriptionPlan) -> Unit
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Text(
                text = "Pilih Paket Kemitraan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain
            )
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PlanCard(
                    modifier = Modifier.weight(1f),
                    title = "Personal Pro",
                    badge = "Populer",
                    price = "Rp 19rb",
                    period = "/bulan",
                    description = "Ideal untuk individu",
                    isSelected = selectedPlan == SubscriptionPlan.PREMIUM_PERSONAL,
                    onClick = { onPlanSelected(SubscriptionPlan.PREMIUM_PERSONAL) }
                )

                PlanCard(
                    modifier = Modifier.weight(1f),
                    title = "Family Plan",
                    badge = "Hemat",
                    price = "Rp 39rb",
                    period = "/bulan",
                    description = "Hingga 5 anggota",
                    isSelected = selectedPlan == SubscriptionPlan.PREMIUM_FAMILY,
                    onClick = { onPlanSelected(SubscriptionPlan.PREMIUM_FAMILY) }
                )
            }
        }
    }

    @Composable
    private fun PlanCard(
        modifier: Modifier,
        title: String,
        badge: String,
        price: String,
        period: String,
        description: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(22.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(22.dp),
            color = if (isSelected) AppColors.Surface else AppColors.Surface.copy(alpha = 0.6f),
            shadowElevation = if (isSelected) 6.dp else 0.dp,
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AppColors.Primary else AppColors.Outline.copy(alpha = 0.6f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) AppColors.Primary else AppColors.TextMain
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) AppColors.Accent else AppColors.Outline.copy(alpha = 0.4f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.CardDark
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = price,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = period,
                        fontSize = 11.sp,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                }

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    @Composable
    private fun FeatureList() {
        val features = listOf(
            Triple("Brankas Dokumen Terenkripsi", "Simpan polis, faktur & kwitansi offline tanpa batas", Icons.Default.Lock),
            Triple("Deteksi Bocor Halus Otomatis", "Peringatan dini untuk langganan yang tidak lagi terpakai", Icons.Default.Shield),
            Triple("Budget Bersama Pasangan & Keluarga", "Sinkronisasi pengeluaran rumah tangga dalam satu pandangan", Icons.Default.FamilyRestroom),
            Triple("Ekspor Laporan Pajak & Finansial (CSV)", "Cetak laporan pengeluaran siap pakai kapan pun dibutuhkan", Icons.Default.Savings)
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Keuntungan Keanggotaan Pro",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            features.forEach { (title, subtitle, icon) ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppColors.PrimaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                color = AppColors.TextMuted,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AppColors.Success,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
