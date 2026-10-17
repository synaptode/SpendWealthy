package com.spendwealthy.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.SubscriptionPlan
import com.spendwealthy.app.ui.screens.bill.BillListScreen
import com.spendwealthy.app.ui.screens.family.FamilyScreen
import com.spendwealthy.app.ui.screens.insight.PrideStatusScreen
import com.spendwealthy.app.ui.screens.premium.PaywallScreen
import com.spendwealthy.app.ui.theme.AppColors

import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.spendwealthy.app.ui.screens.security.SetPinModal

class SettingsScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current
        var showSetPinModal by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Top Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Pengaturan & Keamanan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppColors.TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Kelola akun, privasi data & preferensi",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                navigator.pop()
                            },
                        shape = CircleShape,
                        color = AppColors.Surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Profile Card
                    ProfileSectionCard(
                        userName = state.userName,
                        plan = state.entitlement.plan,
                        onUpgrade = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            navigator.push(PaywallScreen())
                        }
                    )

                    // Export & Sync Section
                    SettingSection(title = "Data & Sinkronisasi") {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.Surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                state.exportStatus?.let { status ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = AppColors.Primary.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = status,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AppColors.Primary,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                SettingsRowItem(
                                    title = "Ekspor Laporan CSV",
                                    subtitle = "Unduh seluruh riwayat transaksi & dompet",
                                    icon = Icons.Default.Download,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.exportFamilyData()
                                    }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = AppColors.Outline
                                )

                                SettingsRowItem(
                                    title = "Tabungan & Ruang Keluarga",
                                    subtitle = "Menabung bersama keluarga, target impian & kelola anggota",
                                    icon = Icons.Default.Groups,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigator.push(FamilyScreen())
                                    }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = AppColors.Outline
                                )

                                SettingsRowItem(
                                    title = "Tagihan & Langganan Rutin",
                                    subtitle = "Kelola tagihan bulanan, cicilan & notifikasi recurring",
                                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigator.push(BillListScreen())
                                    }
                                )
                            }
                        }
                    }

                    // Wealth Analysis & Health Lab
                    SettingSection(title = "Analisis & Keuangan") {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.Surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SettingsRowItem(
                                    title = "Sovereign Wealth Lab",
                                    subtitle = "Metrik kesehatan finansial, gauge score & rekomendasi",
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigator.push(PrideStatusScreen())
                                    }
                                )
                            }
                        }
                    }

                    // Security Section
                    SettingSection(title = "Keamanan & Pengingat") {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.Surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                SettingsSwitchItem(
                                    title = "Mode Privasi (Sensor Saldo)",
                                    subtitle = "Sembunyikan nominal saldo dan transaksi di layar utama",
                                    icon = Icons.Default.VisibilityOff,
                                    checked = state.privacyModeEnabled,
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.setPrivacyModeEnabled(it)
                                    }
                                )

                                HorizontalDivider(color = AppColors.Outline)

                                SettingsSwitchItem(
                                    title = "Kunci Aplikasi (Biometrik & PIN)",
                                    subtitle = "Wajibkan Face ID / Touch ID / PIN saat membuka aplikasi",
                                    icon = Icons.Default.Fingerprint,
                                    checked = state.biometricEnabled,
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.setBiometricEnabled(it)
                                    }
                                )

                                HorizontalDivider(color = AppColors.Outline)

                                SettingsRowItem(
                                    title = "Atur PIN Keamanan (6 Digit)",
                                    subtitle = if (state.hasCustomPin) "PIN kustom aktif (Ketuk untuk ubah)" else "Gunakan PIN demo 123456 (Ketuk untuk atur PIN baru)",
                                    icon = Icons.Default.Lock,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        showSetPinModal = true
                                    }
                                )

                                HorizontalDivider(color = AppColors.Outline)

                                SettingsSwitchItem(
                                    title = "Pengingat Jatuh Tempo Tagihan",
                                    subtitle = "Notifikasi otomatis sebelum batas waktu pembayaran",
                                    icon = Icons.Default.Notifications,
                                    checked = state.notificationsEnabled,
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.setNotificationsEnabled(it)
                                    }
                                )
                            }
                        }
                    }

                    // Privacy Note
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AppColors.SurfaceRaised,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "SpendWealthy adalah aplikasi offline-first: semua saldo, transaksi, dan brankas dokumen tersimpan aman dan terenkripsi di perangkat Anda.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextMuted,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(80.dp))
                }
            }

            if (showSetPinModal) {
                SetPinModal(
                    onDismiss = { showSetPinModal = false },
                    onPinSaved = { pin ->
                        viewModel.setSecurityPin(pin)
                    }
                )
            }
        }
    }

    @Composable
    private fun ProfileSectionCard(
        userName: String,
        plan: SubscriptionPlan,
        onUpgrade: () -> Unit
    ) {
        val planName = when (plan) {
            SubscriptionPlan.FREE -> "Paket Free"
            SubscriptionPlan.PREMIUM_PERSONAL -> "Personal Pro"
            SubscriptionPlan.PREMIUM_FAMILY -> "Family Pro"
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.SurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(AppColors.Primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppColors.Primary
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = userName,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppColors.TextMain
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (plan == SubscriptionPlan.FREE) AppColors.SurfaceRaised else AppColors.Primary.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (plan == SubscriptionPlan.FREE) AppColors.Outline else AppColors.Primary.copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = planName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (plan == SubscriptionPlan.FREE) AppColors.TextMuted else AppColors.Primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (plan == SubscriptionPlan.FREE) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onUpgrade),
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.Primary
                        ) {
                            Text(
                                text = "Upgrade",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF090D16),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SettingSection(
        title: String,
        content: @Composable () -> Unit
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMuted
            )
            content()
        }
    }

    @Composable
    private fun SettingsRowItem(
        title: String,
        subtitle: String,
        icon: ImageVector,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.SurfaceRaised),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMain
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AppColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    @Composable
    private fun SettingsSwitchItem(
        title: String,
        subtitle: String,
        icon: ImageVector,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.SurfaceRaised),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppColors.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = AppColors.SurfaceRaised
                )
            )
        }
    }
}
