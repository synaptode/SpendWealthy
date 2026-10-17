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
import com.spendwealthy.app.ui.theme.AppColors

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AuthViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current
        val focusManager = LocalFocusManager.current

        var showForgotPasswordDialog by remember { mutableStateOf(false) }

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
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (navigator.canPop) {
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    navigator.pop()
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
                    } else {
                        Spacer(Modifier.size(42.dp))
                    }

                    Text(
                        text = "Masuk",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )

                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.loginAsGuest {
                                navigator.replaceAll(MainScreen())
                            }
                        }
                    ) {
                        Text(
                            text = "Tamu",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextMuted
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Brand Emblem & Welcome Title
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.Primary.copy(alpha = 0.2f))
                        .border(1.5.dp, AppColors.Primary, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = AppColors.PrimaryDark,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Selamat Datang Kembali",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = AppColors.TextMain,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Kelola anggaran, tagihan, dan kekayaan Anda secara cerdas & terenkripsi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Spacer(Modifier.height(24.dp))

                // Error Banner
                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
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

                // Email Input Field
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email atau Username") },
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

                // Password Input Field
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kata Sandi") },
                    placeholder = { Text("••••••••") },
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
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.login {
                                navigator.replaceAll(MainScreen())
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

                Spacer(Modifier.height(10.dp))

                // Remember Me & Forgot Password Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.toggleRememberMe() }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Checkbox(
                            checked = state.rememberMe,
                            onCheckedChange = { viewModel.toggleRememberMe() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AppColors.Primary,
                                checkmarkColor = AppColors.OnPrimary
                            )
                        )
                        Text(
                            text = "Ingat Saya",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextMain
                        )
                    }

                    TextButton(onClick = { showForgotPasswordDialog = true }) {
                        Text(
                            text = "Lupa Sandi?",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMuted
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Primary Login Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !state.isLoading) {
                            focusManager.clearFocus()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.login {
                                navigator.replaceAll(MainScreen())
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
                                    text = "Masuk ke Akun",
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

                // Social Logins Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = AppColors.Outline)
                    Text(
                        text = "atau masuk dengan",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = AppColors.Outline)
                }

                Spacer(Modifier.height(16.dp))

                // Quick Login Buttons (Biometric & Google/Apple)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.loginAsGuest {
                                    navigator.replaceAll(MainScreen())
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = AppColors.Surface,
                        border = BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = AppColors.PrimaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Biometrik",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.onEmailChanged("google.user@spendwealthy.com")
                                viewModel.onPasswordChanged("google_pass_123")
                                viewModel.login {
                                    navigator.replaceAll(MainScreen())
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = AppColors.Surface,
                        border = BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = AppColors.TextMain,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Google / Apple",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                // Register Link Prompt
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Belum memiliki akun?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMuted
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Daftar Sekarang",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                navigator.push(RegisterScreen())
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))
            }
        }

        // Forgot Password Simple Dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = {
                    Text(
                        text = "Atur Ulang Sandi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                },
                text = {
                    Text(
                        text = "SpendWealthy berbasis Sovereign Local Vault. Jika Anda lupa PIN atau kata sandi lokal, Anda dapat masuk melalui Biometrik perangkat Anda atau membuat akun vault baru.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMuted
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text(
                            text = "Mengerti",
                            fontWeight = FontWeight.Bold,
                            color = AppColors.PrimaryDark
                        )
                    }
                },
                containerColor = AppColors.Surface,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
