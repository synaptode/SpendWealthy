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
import com.spendwealthy.app.ui.screens.auth.LoginScreen
import com.spendwealthy.app.ui.screens.auth.RegisterScreen
import com.spendwealthy.app.ui.screens.onboarding.OnboardingScreen
import com.spendwealthy.app.ui.theme.AppColors

import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import com.spendwealthy.app.domain.model.ThemeMode
import com.spendwealthy.app.core.localization.AppLanguage
import com.spendwealthy.app.core.localization.LocalStrings
import com.spendwealthy.app.ui.components.LanguageSelectionBottomSheet
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.spendwealthy.app.ui.screens.security.SetPinModal

class SettingsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current
        val strings = LocalStrings.current
        var showSetPinModal by remember { mutableStateOf(false) }
        var showEditProfileModal by remember { mutableStateOf(false) }
        var showLogoutDialog by remember { mutableStateOf(false) }
        var showLanguageModal by remember { mutableStateOf(false) }

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
                            text = strings.settingsTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppColors.TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = strings.settingsSubtitle,
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
                                if (navigator.canPop) navigator.pop()
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
                        },
                        onEditProfile = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showEditProfileModal = true
                        }
                    )

                    // Akun & Autentikasi
                    SettingSection(title = "Akun & Keamanan Cloud") {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.Surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (state.isLoggedIn) {
                                    // Status Badge Terverifikasi
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AppColors.Success.copy(alpha = 0.12f))
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = AppColors.Success,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = state.userEmail ?: "Akun Terverifikasi",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.TextMain
                                            )
                                            Text(
                                                text = "Sovereign Cloud Terenkripsi Aktif",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AppColors.Success
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    SettingsRowItem(
                                        title = "Edit Profil & Mata Uang",
                                        subtitle = "Nama: ${state.userName} • Mata Uang: ${state.userCurrency}",
                                        icon = Icons.Default.Edit,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            showEditProfileModal = true
                                        }
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = AppColors.Outline
                                    )

                                    SettingsRowItem(
                                        title = "Ganti Akun Lain",
                                        subtitle = "Masuk menggunakan alamat email lain",
                                        icon = Icons.Default.Lock,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            navigator.push(LoginScreen())
                                        }
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = AppColors.Outline
                                    )

                                    SettingsRowItem(
                                        title = "Keluar dari Akun",
                                        subtitle = "Kembali ke mode penyimpanan lokal offline",
                                        icon = Icons.AutoMirrored.Filled.Logout,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            showLogoutDialog = true
                                        }
                                    )
                                } else {
                                    // Status Badge Tamu
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AppColors.Primary.copy(alpha = 0.12f))
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = AppColors.PrimaryDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Mode Tamu / Offline-First",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.TextMain
                                            )
                                            Text(
                                                text = "Data disimpan di database lokal perangkat",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AppColors.TextMuted
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    SettingsRowItem(
                                        title = "Masuk ke Akun Cloud",
                                        subtitle = "Sinkronisasi antar perangkat & backup otomatis",
                                        icon = Icons.Default.Lock,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            navigator.push(LoginScreen())
                                        }
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = AppColors.Outline
                                    )

                                    SettingsRowItem(
                                        title = "Daftar Akun Baru",
                                        subtitle = "Buat akun sovereign vault untuk sinkronisasi antar perangkat",
                                        icon = Icons.Default.Security,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            navigator.push(RegisterScreen())
                                        }
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = AppColors.Outline
                                    )

                                    SettingsRowItem(
                                        title = "Ubah Nama & Mata Uang",
                                        subtitle = "Nama: ${state.userName} • Mata Uang: ${state.userCurrency}",
                                        icon = Icons.Default.Edit,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            showEditProfileModal = true
                                        }
                                    )
                                }
                            }
                        }
                    }

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
                                        color = AppColors.Primary.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = status,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AppColors.Primary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Tutup status",
                                                tint = AppColors.Primary,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        viewModel.clearExportStatus()
                                                    }
                                            )
                                        }
                                    }
                                }

                                SettingsRowItem(
                                    title = "Ekspor Riwayat Transaksi (CSV)",
                                    subtitle = "Unduh seluruh riwayat transaksi bulanan dalam format spreadsheet CSV",
                                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.exportTransactionsCsv()
                                    }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = AppColors.Outline
                                )

                                SettingsRowItem(
                                    title = "Cadangkan Data Lengkap (JSON)",
                                    subtitle = "Ekspor cadangan utuh seluruh akun dompet & transaksi lokal offline",
                                    icon = Icons.Default.Download,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.backupLocalDataJson()
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

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = AppColors.Outline
                                )

                                SettingsRowItem(
                                    title = "Panduan Onboarding Aplikasi",
                                    subtitle = "Lihat kembali pengenalan fitur, mode akun & safe-to-spend",
                                    icon = Icons.Default.Info,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigator.push(OnboardingScreen())
                                    }
                                )
                            }
                        }
                    }

                    // Tampilan & Tema Section
                    SettingSection(title = "Tampilan & Tema") {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.Surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Mode Tampilan Aplikasi",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.TextMain
                                )
                                Text(
                                    text = "Sesuaikan kontras dan tema visual sesuai preferensi Anda",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppColors.TextMuted
                                )

                                Spacer(Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val modes = listOf(
                                        Triple(ThemeMode.SYSTEM, "Sistem", Icons.Default.BrightnessAuto),
                                        Triple(ThemeMode.LIGHT, "Terang", Icons.Default.LightMode),
                                        Triple(ThemeMode.DARK, "Gelap", Icons.Default.DarkMode)
                                    )

                                    modes.forEach { (mode, title, icon) ->
                                        val isSelected = state.themeMode == mode
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    viewModel.setThemeMode(mode)
                                                },
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isSelected) AppColors.Primary.copy(alpha = 0.18f) else AppColors.SurfaceRaised,
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) AppColors.Primary else AppColors.Outline
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = title,
                                                    tint = if (isSelected) AppColors.TextMain else AppColors.TextMuted,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(Modifier.height(6.dp))
                                                Text(
                                                    text = title,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                    color = if (isSelected) AppColors.TextMain else AppColors.TextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bahasa & Wilayah (Language & Region) Section
                    SettingSection(title = strings.languageSectionTitle) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.Surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                        Text(
                                            text = strings.selectLanguage,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppColors.TextMain
                                        )
                                        Text(
                                            text = strings.languageSectionSubtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AppColors.TextMuted
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = AppColors.Primary.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = state.currentLanguage.flagEmoji,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = state.currentLanguage.nativeName,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.TextMain
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(14.dp))

                                // Quick Language Selector Grid (Top 4 Languages)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        AppLanguage.INDONESIAN,
                                        AppLanguage.ENGLISH,
                                        AppLanguage.MALAY,
                                        AppLanguage.THAI
                                    ).forEach { lang ->
                                        val isSelected = state.currentLanguage == lang
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    viewModel.setLanguage(lang)
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) AppColors.Primary.copy(alpha = 0.18f) else AppColors.SurfaceRaised,
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) AppColors.Primary else AppColors.Outline
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(text = lang.flagEmoji, fontSize = 18.sp)
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    text = lang.displayName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) AppColors.TextMain else AppColors.TextMuted,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                // Quick Language Selector Grid (Next 4 Languages: Mandarin, Japanese, Vietnamese, Arabic)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        AppLanguage.CHINESE,
                                        AppLanguage.JAPANESE,
                                        AppLanguage.VIETNAMESE,
                                        AppLanguage.ARABIC
                                    ).forEach { lang ->
                                        val isSelected = state.currentLanguage == lang
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    viewModel.setLanguage(lang)
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) AppColors.Primary.copy(alpha = 0.18f) else AppColors.SurfaceRaised,
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) AppColors.Primary else AppColors.Outline
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(text = lang.flagEmoji, fontSize = 18.sp)
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    text = lang.displayName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) AppColors.TextMain else AppColors.TextMuted,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                HorizontalDivider(color = AppColors.Outline)

                                Spacer(Modifier.height(8.dp))

                                SettingsRowItem(
                                    title = strings.selectLanguage,
                                    subtitle = "${state.currentLanguage.flagEmoji} ${state.currentLanguage.nativeName} (${state.currentLanguage.region})",
                                    icon = Icons.Default.Language,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        showLanguageModal = true
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

            if (showEditProfileModal) {
                EditProfileModal(
                    currentName = state.userName,
                    currentCurrency = state.userCurrency,
                    onDismiss = { showEditProfileModal = false },
                    onSave = { name, currency ->
                        viewModel.updateProfile(name, currency)
                    }
                )
            }

            if (showLanguageModal) {
                LanguageSelectionBottomSheet(
                    currentLanguage = state.currentLanguage,
                    onLanguageSelected = { lang ->
                        viewModel.setLanguage(lang)
                    },
                    onDismiss = { showLanguageModal = false }
                )
            }

            if (showLogoutDialog) {
                ModalBottomSheet(
                    onDismissRequest = { showLogoutDialog = false },
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
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 36.dp)
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = AppColors.Danger.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = AppColors.Danger,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Konfirmasi Keluar",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = "Apakah Anda yakin ingin keluar dari akun? Sesi cloud akan diakhiri dan aplikasi akan kembali ke mode penyimpanan lokal offline.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showLogoutDialog = false
                                viewModel.logout {}
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Danger,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Keluar dari Akun", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showLogoutDialog = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Batal", color = AppColors.TextMuted, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ProfileSectionCard(
        userName: String,
        plan: SubscriptionPlan,
        onUpgrade: () -> Unit,
        onEditProfile: () -> Unit
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onEditProfile)
                            .padding(end = 8.dp)
                    ) {
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
                        Column(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userName,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profil",
                                    tint = AppColors.Primary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
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
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
                modifier = Modifier.weight(1f).padding(end = 12.dp)
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
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
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
