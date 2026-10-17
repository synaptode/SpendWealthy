package com.spendwealthy.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.ui.main.MainScreen
import com.spendwealthy.app.ui.screens.onboarding.OnboardingScreen
import com.spendwealthy.app.ui.theme.AppColors

class RegisterScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AuthViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current
        val focusManager = LocalFocusManager.current

        val currencies = listOf("IDR", "USD", "EUR", "SGD")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 540.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (navigator.canPop) navigator.pop() else navigator.push(LoginScreen())
                            },
                        shape = CircleShape,
                        color = AppColors.Surface,
                        border = BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = AppColors.TextMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = "Daftar Akun",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )

                    Spacer(Modifier.size(42.dp))
                }

                Spacer(Modifier.height(8.dp))

                // Brand Emblem
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AppColors.Primary.copy(alpha = 0.2f))
                        .border(1.5.dp, AppColors.Primary, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = AppColors.PrimaryDark,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Buat Akun SpendWealthy",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = AppColors.TextMain,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Mulai perjalanan finansial mandiri dengan kontrol total atas privasi data Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(Modifier.height(20.dp))

                // Error Banner
                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = AppColors.Danger.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, AppColors.Danger.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = AppColors.Danger,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = state.errorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Danger,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Full Name Input
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nama Lengkap") },
                    placeholder = { Text("Alex Pratama") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppColors.Surface,
                        unfocusedContainerColor = AppColors.Surface,
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = AppColors.Outline,
                        focusedLabelColor = AppColors.TextMain,
                        unfocusedLabelColor = AppColors.TextMuted
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Email Input
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Alamat Email") },
                    placeholder = { Text("alex@spendwealthy.com") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppColors.Surface,
                        unfocusedContainerColor = AppColors.Surface,
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = AppColors.Outline,
                        focusedLabelColor = AppColors.TextMain,
                        unfocusedLabelColor = AppColors.TextMuted
                    )
                )

                Spacer(Modifier.height(14.dp))

                // Currency Preference Selector
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Mata Uang Utama",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMuted
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currencies.forEach { curr ->
                            val isSelected = state.selectedCurrency == curr
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.onCurrencyChanged(curr)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AppColors.Primary else AppColors.Surface,
                                border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Outline)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = curr,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) AppColors.OnPrimary else AppColors.TextMain
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Password Input
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kata Sandi") },
                    placeholder = { Text("Minimal 6 karakter") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Tampilkan Sandi",
                                tint = AppColors.TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppColors.Surface,
                        unfocusedContainerColor = AppColors.Surface,
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = AppColors.Outline,
                        focusedLabelColor = AppColors.TextMain,
                        unfocusedLabelColor = AppColors.TextMuted
                    )
                )

                // Password Strength Indicator Bar
                if (state.password.isNotEmpty()) {
                    val strengthLabel = when {
                        state.password.length >= 10 -> "Kuat"
                        state.password.length >= 6 -> "Sedang"
                        else -> "Lemah"
                    }
                    val strengthColor = when {
                        state.password.length >= 10 -> AppColors.Success
                        state.password.length >= 6 -> AppColors.Warning
                        else -> AppColors.Danger
                    }
                    val strengthProgress = when {
                        state.password.length >= 10 -> 1f
                        state.password.length >= 6 -> 0.6f
                        else -> 0.3f
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, start = 4.dp, end = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { strengthProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = strengthColor,
                            trackColor = AppColors.Outline
                        )
                        Text(
                            text = "Kekuatan Sandi: $strengthLabel",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = strengthColor
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Confirm Password Input
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Konfirmasi Kata Sandi") },
                    placeholder = { Text("Ulangi kata sandi") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.register {
                                navigator.replaceAll(OnboardingScreen())
                            }
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppColors.Surface,
                        unfocusedContainerColor = AppColors.Surface,
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = AppColors.Outline,
                        focusedLabelColor = AppColors.TextMain,
                        unfocusedLabelColor = AppColors.TextMuted
                    )
                )

                Spacer(Modifier.height(14.dp))

                // Terms and Conditions Agreement
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewModel.toggleAgreeTerms() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.agreeTerms,
                        onCheckedChange = { viewModel.toggleAgreeTerms() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AppColors.Primary,
                            checkmarkColor = AppColors.OnPrimary
                        )
                    )
                    Text(
                        text = "Saya menyetujui Ketentuan Layanan & Kebijakan Privasi SpendWealthy Sovereign Vault.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextMain,
                        lineHeight = 16.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Register Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !state.isLoading) {
                            focusManager.clearFocus()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.register {
                                navigator.replaceAll(OnboardingScreen())
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.Primary,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = AppColors.OnPrimary,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Buat Akun Baru",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = AppColors.OnPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AppColors.OnPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Link to Login
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sudah memiliki akun?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMuted
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Masuk di Sini",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (navigator.canPop) navigator.pop() else navigator.push(LoginScreen())
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
