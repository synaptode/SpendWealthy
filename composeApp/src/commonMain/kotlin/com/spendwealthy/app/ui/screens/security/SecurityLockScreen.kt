package com.spendwealthy.app.ui.screens.security

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.gateway.BiometricGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SecurityLockScreen(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val biometricGateway = koinInject<BiometricGateway>()
    val preferenceRepository = koinInject<PreferenceRepository>()
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var isBiometricAvailable by remember { mutableStateOf(false) }

    val shakeOffset = remember { Animatable(0f) }

    // Check biometric availability and auto-prompt once on mount
    LaunchedEffect(Unit) {
        val available = biometricGateway.isAvailable()
        isBiometricAvailable = available
        if (available) {
            delay(300)
            val result = biometricGateway.authenticate("Buka kunci SpendWealthy")
            if (result is GatewayResult.Success && result.value) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onUnlocked()
            }
        }
    }

    fun triggerBiometric() {
        coroutineScope.launch {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            val result = biometricGateway.authenticate("Buka kunci SpendWealthy")
            if (result is GatewayResult.Success && result.value) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onUnlocked()
            } else if (result is GatewayResult.Failure) {
                errorMessage = result.message
            }
        }
    }

    fun validatePin(pin: String) {
        coroutineScope.launch {
            val savedPin = preferenceRepository.getString("user_security_pin").firstOrNull() ?: "123456"
            if (pin == savedPin) {
                errorMessage = null
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(100)
                onUnlocked()
            } else {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                errorMessage = "PIN tidak sesuai. Silakan coba lagi."
                // Trigger shake animation
                shakeOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = 350
                        0f at 0
                        -16f at 50
                        16f at 100
                        -12f at 150
                        12f at 200
                        -6f at 250
                        6f at 300
                        0f at 350
                    }
                )
                delay(200)
                enteredPin = ""
            }
        }
    }

    fun onDigitPress(digit: String) {
        if (enteredPin.length < 6) {
            errorMessage = null
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            val nextPin = enteredPin + digit
            enteredPin = nextPin
            if (nextPin.length == 6) {
                validatePin(nextPin)
            }
        }
    }

    fun onDeletePress() {
        if (enteredPin.isNotEmpty()) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AppColors.CanaryYellow.copy(alpha = 0.2f))
                        .border(2.dp, AppColors.CanaryYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security Lock",
                        tint = AppColors.CharcoalBlack,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "SpendWealthy Aman",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Masukkan 6 digit PIN akun Anda",
                    fontSize = 14.sp,
                    color = AppColors.TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6 Pin Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.offset(x = shakeOffset.value.dp)
                ) {
                    repeat(6) { index ->
                        val isFilled = index < enteredPin.length
                        val isError = errorMessage != null

                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isError -> Color(0xFFEF4444)
                                        isFilled -> AppColors.CharcoalBlack
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = 2.dp,
                                    color = when {
                                        isError -> Color(0xFFEF4444)
                                        isFilled -> AppColors.CharcoalBlack
                                        else -> AppColors.Outline
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message or Helper
                Column(
                    modifier = Modifier.height(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFEF4444),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Keypad Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PinKeypadButton("1") { onDigitPress("1") }
                    PinKeypadButton("2") { onDigitPress("2") }
                    PinKeypadButton("3") { onDigitPress("3") }
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PinKeypadButton("4") { onDigitPress("4") }
                    PinKeypadButton("5") { onDigitPress("5") }
                    PinKeypadButton("6") { onDigitPress("6") }
                }

                // Row 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PinKeypadButton("7") { onDigitPress("7") }
                    PinKeypadButton("8") { onDigitPress("8") }
                    PinKeypadButton("9") { onDigitPress("9") }
                }

                // Row 4 (Biometric, 0, Backspace)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Biometric button (if available or fallback)
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable(
                                enabled = isBiometricAvailable,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true)
                            ) {
                                triggerBiometric()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBiometricAvailable) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometrik",
                                tint = AppColors.CharcoalBlack,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    PinKeypadButton("0") { onDigitPress("0") }

                    // Backspace
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable(
                                enabled = enteredPin.isNotEmpty(),
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true)
                            ) {
                                onDeletePress()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Hapus",
                            tint = if (enteredPin.isNotEmpty()) AppColors.CharcoalBlack else AppColors.TextMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Help Button
                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showHelpDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AppColors.TextMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lupa PIN? Bantuan Masuk",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted
                    )
                }
            }
        }

        // Help / Forgot PIN Dialog
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = {
                    Text(
                        text = "Bantuan Keamanan Akun",
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "SpendWealthy mengamankan data keuangan Anda secara lokal di perangkat.",
                            fontSize = 14.sp,
                            color = AppColors.TextMuted
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.CanaryYellow.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.CanaryYellow.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 PIN bawaan demo: 123456\nAnda dapat mengganti atau mematikan PIN kapan saja melalui menu Pengaturan > Keamanan.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.CharcoalBlack,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showHelpDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.CharcoalBlack,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Saya Mengerti")
                    }
                }
            )
        }
    }
}

@Composable
private fun PinKeypadButton(
    digit: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = AppColors.Surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = digit,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMain
            )
        }
    }
}
