package com.spendwealthy.app.ui.screens.security

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spendwealthy.app.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SetPinModal(
    onDismiss: () -> Unit,
    onPinSaved: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var step by remember { mutableStateOf(1) } // 1: Enter new, 2: Confirm
    var firstPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val shakeOffset = remember { Animatable(0f) }

    val currentEnteredPin = if (step == 1) firstPin else confirmPin

    fun onDigitPress(digit: String) {
        if (currentEnteredPin.length < 6) {
            errorMessage = null
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            val updated = currentEnteredPin + digit
            if (step == 1) {
                firstPin = updated
                if (updated.length == 6) {
                    coroutineScope.launch {
                        delay(200)
                        step = 2
                    }
                }
            } else {
                confirmPin = updated
                if (updated.length == 6) {
                    if (updated == firstPin) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        coroutineScope.launch {
                            delay(150)
                            onPinSaved(updated)
                            onDismiss()
                        }
                    } else {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        errorMessage = "PIN konfirmasi tidak sama. Silakan ulangi."
                        coroutineScope.launch {
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
                            delay(250)
                            step = 1
                            firstPin = ""
                            confirmPin = ""
                        }
                    }
                }
            }
        }
    }

    fun onDeletePress() {
        if (currentEnteredPin.isNotEmpty()) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            if (step == 1) {
                firstPin = firstPin.dropLast(1)
            } else {
                confirmPin = confirmPin.dropLast(1)
            }
            errorMessage = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AppColors.CanaryYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AppColors.CharcoalBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = AppColors.TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (step == 1) "Buat PIN Baru" else "Konfirmasi PIN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (step == 1) "Masukkan 6 digit angka untuk PIN SpendWealthy" else "Ketik kembali 6 digit PIN untuk memastikan",
                    fontSize = 13.sp,
                    color = AppColors.TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Pin Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.offset(x = shakeOffset.value.dp)
                ) {
                    repeat(6) { index ->
                        val isFilled = index < currentEnteredPin.length
                        val isError = errorMessage != null

                        Box(
                            modifier = Modifier
                                .size(14.dp)
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

                Spacer(modifier = Modifier.height(14.dp))

                // Error message
                Column(
                    modifier = Modifier.height(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Keypad
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ModalPinKeypadButton("1") { onDigitPress("1") }
                        ModalPinKeypadButton("2") { onDigitPress("2") }
                        ModalPinKeypadButton("3") { onDigitPress("3") }
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ModalPinKeypadButton("4") { onDigitPress("4") }
                        ModalPinKeypadButton("5") { onDigitPress("5") }
                        ModalPinKeypadButton("6") { onDigitPress("6") }
                    }

                    // Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ModalPinKeypadButton("7") { onDigitPress("7") }
                        ModalPinKeypadButton("8") { onDigitPress("8") }
                        ModalPinKeypadButton("9") { onDigitPress("9") }
                    }

                    // Row 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(60.dp)) // empty spacer

                        ModalPinKeypadButton("0") { onDigitPress("0") }

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .clickable(enabled = currentEnteredPin.isNotEmpty()) { onDeletePress() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Hapus",
                                tint = if (currentEnteredPin.isNotEmpty()) AppColors.CharcoalBlack else AppColors.TextMuted.copy(alpha = 0.4f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalPinKeypadButton(
    digit: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = AppColors.Background,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = digit,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMain
            )
        }
    }
}
