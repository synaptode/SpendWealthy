@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.spendwealthy.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.ui.main.MainScreen
import com.spendwealthy.app.ui.screens.auth.LoginScreen

class OnboardingScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<OnboardingViewModel>()
        val state by viewModel.uiState.collectAsState()
        val haptic = LocalHapticFeedback.current
        val navigator = LocalNavigator.currentOrThrow

        // Menjamin navigasi instan saat status selesai aktif
        LaunchedEffect(state.isCompleted) {
            if (state.isCompleted) {
                navigator.replaceAll(MainScreen())
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF090D16),
                            Color(0xFF0F172A),
                            Color(0xFF050811)
                        )
                    )
                )
        ) {
            // Ambient subtle background light
            Box(
                modifier = Modifier
                    .size(360.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF10B981).copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Header Bar (Brand + Stepper + Skip + Login)
                TopHeaderBar(
                    step = state.step,
                    onSkip = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.skipToComplete()
                    },
                    onLogin = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        navigator.push(LoginScreen())
                    }
                )

                // Scrollable Content Container (Never overflows or gets cut off)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(
                        targetState = state.step,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                        label = "onboarding_step"
                    ) { targetStep ->
                        when (targetStep) {
                            1 -> Step1Experience()
                            2 -> Step2Profile(state, viewModel)
                            3 -> Step3Wallet(state, viewModel)
                            4 -> Step4Activation(state)
                            else -> Step4Activation(state)
                        }
                    }
                }

                // Bottom Fixed Action Dock (Always visible, prominent & accessible)
                BottomActionDock(
                    step = state.step,
                    isLoading = state.isLoading,
                    onNext = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.nextStep()
                    },
                    onLogin = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        navigator.push(LoginScreen())
                    }
                )
            }
        }
    }

    // --------------------------------------------------------------------------
    // Header & Stepper
    // --------------------------------------------------------------------------
    @Composable
    private fun TopHeaderBar(step: Int, onSkip: () -> Unit, onLogin: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp)
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
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "SpendWealthy",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.3).sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (step == 1) {
                        TextButton(onClick = onLogin) {
                            Text(
                                text = "Masuk",
                                color = Color(0xFFFFDE43),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (step < 4) {
                        TextButton(onClick = onSkip) {
                            Text(
                                text = "Lewati",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Stepper Bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 1..4) {
                    val isPastOrCurrent = i <= step
                    val width by animateDpAsState(if (i == step) 32.dp else 14.dp)
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .weight(if (i == step) 2f else 1f)
                            .clip(CircleShape)
                            .background(
                                if (isPastOrCurrent) Color(0xFF10B981) else Color(0xFF334155)
                            )
                    )
                }
            }
        }
    }

    // --------------------------------------------------------------------------
    // Step 1: Wealth Experience
    // --------------------------------------------------------------------------
    @Composable
    private fun Step1Experience() {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Glassmorphic Titanium Card Preview
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF161F30).copy(alpha = 0.9f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TITANIUM WORKSPACE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            letterSpacing = 1.2.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "100% OFFLINE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Total Likuiditas",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "Rp 35.000.000",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Safe-to-Spend Harian",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        Text(
                            text = "Rp 450.000 / hari",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }

            // Headline Text
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Privasi Finansial Penuh.\nTanpa Kompromi.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )
                Text(
                    text = "Kelola arus kas, alokasi anggaran, dan brankas dokumen secara offline-first dan terenkripsi di perangkat.",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Feature Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeaturePill(Icons.Default.Lock, "Enkripsi Lokal", Modifier.weight(1f))
                FeaturePill(Icons.Default.Payments, "QuickPad 000", Modifier.weight(1f))
                FeaturePill(Icons.Default.Security, "Brankas Dokumen", Modifier.weight(1f))
            }
        }
    }

    @Composable
    private fun FeaturePill(icon: ImageVector, label: String, modifier: Modifier) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                .padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFCBD5E1)
            )
        }
    }

    // --------------------------------------------------------------------------
    // Step 2: Mode & Profile Setup
    // --------------------------------------------------------------------------
    @Composable
    private fun Step2Profile(state: OnboardingState, viewModel: OnboardingViewModel) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Karakter Profil Anda",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Pilih mode pengelolaan dan lengkapi nama pengguna Anda.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModeCard(
                    title = "Personal",
                    description = "Arus kas & dompet mandiri",
                    icon = Icons.Default.Person,
                    isSelected = state.mode == "PERSONAL",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onModeChanged("PERSONAL") }
                )
                ModeCard(
                    title = "Keluarga",
                    description = "Konsensus & anggaran bersama",
                    icon = Icons.Default.People,
                    isSelected = state.mode == "FAMILY",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onModeChanged("FAMILY") }
                )
            }

            // Name Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Nama Anda",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1)
                )
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChanged,
                    placeholder = { Text("Contoh: Alex Pratama", color = Color(0xFF64748B)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )
            }

            // Avatar Monogram Badges
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Pilih Inisial Avatar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val initials = listOf("A", "M", "R", "S", "W")
                    initials.forEachIndexed { index, letter ->
                        val isSelected = state.avatarId == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFF10B981) else Color(0xFF1E293B))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF34D399) else Color(0xFF334155),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.onAvatarChanged(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ModeCard(
        title: String,
        description: String,
        icon: ImageVector,
        isSelected: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = modifier.clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF1E293B),
            border = BorderStroke(1.dp, if (isSelected) Color(0xFF10B981) else Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF10B981) else Color(0xFF94A3B8),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 14.sp
                )
            }
        }
    }

    // --------------------------------------------------------------------------
    // Step 3: Wallet Setup
    // --------------------------------------------------------------------------
    @Composable
    private fun Step3Wallet(state: OnboardingState, viewModel: OnboardingViewModel) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Siapkan Kas Utama",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tentukan wadah dana dan saldo awal untuk kalkulasi Safe-to-Spend.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Wallet Types
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WalletTypePill(
                    label = "Tunai",
                    icon = Icons.Default.Payments,
                    isSelected = state.walletType == WalletType.CASH,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onWalletTypeChanged(WalletType.CASH) }
                )
                WalletTypePill(
                    label = "Bank",
                    icon = Icons.Default.AccountBalance,
                    isSelected = state.walletType == WalletType.BANK,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onWalletTypeChanged(WalletType.BANK) }
                )
                WalletTypePill(
                    label = "E-Wallet",
                    icon = Icons.Default.Smartphone,
                    isSelected = state.walletType == WalletType.EWALLET,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onWalletTypeChanged(WalletType.EWALLET) }
                )
            }

            // Wallet Name
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Nama Dompet",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1)
                )
                OutlinedTextField(
                    value = state.walletName,
                    onValueChange = viewModel::onWalletNameChanged,
                    placeholder = { Text("Contoh: Kas Utama / BCA", color = Color(0xFF64748B)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )
            }

            // Initial Balance
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Saldo Awal (Rp)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1)
                )
                OutlinedTextField(
                    value = state.initialBalance,
                    onValueChange = viewModel::onInitialBalanceChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0", color = Color(0xFF64748B)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )

                // Quick Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("100000" to "+100k", "500000" to "+500k", "1000000" to "+1jt", "5000000" to "+5jt").forEach { (amount, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF334155))
                                .clickable {
                                    val current = state.initialBalance.toLongOrNull() ?: 0L
                                    viewModel.onInitialBalanceChanged((current + amount.toLong()).toString())
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WalletTypePill(
        label: String,
        icon: ImageVector,
        isSelected: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Color(0xFF10B981) else Color(0xFF1E293B))
                .border(1.dp, if (isSelected) Color(0xFF34D399) else Color(0xFF334155), RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else Color.White
            )
        }
    }

    // --------------------------------------------------------------------------
    // Step 4: Wealth Seal & Activation (NO CARTOON PAW!)
    // --------------------------------------------------------------------------
    @Composable
    private fun Step4Activation(state: OnboardingState) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Glowing Wealth Emblem
            Box(contentAlignment = Alignment.Center) {
                // Pulsing Outer Aura
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.12f))
                )
                // Middle Ring
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.22f))
                        .border(1.5.dp, Color(0xFF10B981).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Workspace Finansial Siap",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Enkripsi hardware aktif. Data finansial tersimpan aman di perangkat lokal Anda.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Summary Configuration Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF161F30),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryItem("Pemilik Akun", state.name.ifBlank { "Personal" })
                    SummaryItem("Mode", if (state.mode == "FAMILY") "Family Consensus" else "Personal Finance")
                    SummaryItem(
                        "Dompet Awal",
                        "${state.walletName} (Rp ${state.initialBalance.ifBlank { "0" }})"
                    )
                    SummaryItem("Penyimpanan", "Offline-First (Sandbox Terenkripsi)")
                }
            }
        }
    }

    @Composable
    private fun SummaryItem(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }

    // --------------------------------------------------------------------------
    // Bottom Action Dock
    // --------------------------------------------------------------------------
    @Composable
    private fun BottomActionDock(
        step: Int,
        isLoading: Boolean,
        onNext: () -> Unit,
        onLogin: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF090D16).copy(alpha = 0.95f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNext,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (step < 4) "Lanjutkan" else "Mulai Kelola Keuangan ✦",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = if (step < 4) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (step == 1) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sudah memiliki akun? ",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "Masuk",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFDE43),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(onClick = onLogin)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
