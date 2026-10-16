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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.SubscriptionPlan
import com.spendwealthy.app.ui.screens.premium.PaywallScreen
import com.spendwealthy.app.ui.theme.AppColors

class SettingsScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                    Column {
                        Text(
                            text = "Pengaturan & Keamanan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "Kelola akun, privasi data & preferensi",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextMuted
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
                                    title = "Sinkronisasi Ruang Keluarga",
                                    subtitle = "Bagikan catatan dengan pasangan atau keluarga",
                                    icon = Icons.Default.Groups,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.exportFamilyData()
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
                                    title = "Kunci Biometrik (FaceID / Fingerprint)",
                                    subtitle = "Wajibkan autentikasi saat membuka aplikasi",
                                    icon = Icons.Default.Fingerprint,
                                    checked = state.biometricEnabled,
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.setBiometricEnabled(it)
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
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AppColors.Primary.copy(alpha = 0.12f)),
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
                    Column {
                        Text(
                            text = userName,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.TextMain
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (plan == SubscriptionPlan.FREE) AppColors.SurfaceRaised else AppColors.Primary.copy(alpha = 0.14f),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = planName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (plan == SubscriptionPlan.FREE) AppColors.TextMuted else AppColors.Primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
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
