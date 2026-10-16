@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.spendwealthy.app.ui.screens.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppTextField
import com.spendwealthy.app.ui.components.SuccessCelebration
import com.spendwealthy.app.ui.theme.AppColors

class OnboardingScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<OnboardingViewModel>()
        val state = viewModel.uiState.collectAsState().value
        val haptic = LocalHapticFeedback.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FinanceHero(modifier = Modifier.fillMaxWidth().weight(1.1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                OnboardingStepContent(state.step, state, viewModel)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    StepIndicator(state.step)
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.nextStep()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(
                            text = if (state.step < 4) "Lanjutkan" else "Mulai Sekarang",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.size(10.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun FinanceHero(modifier: Modifier) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                )
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    color = AppColors.Primary.copy(alpha = 0.2f),
                    radius = size.width * 0.45f,
                    center = Offset(size.width * 0.85f, size.height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = size.width * 0.35f,
                    center = Offset(size.width * 0.15f, size.height * 0.7f)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroMetric(Icons.Default.CreditCard, "Kartu")
                HeroMetric(Icons.Default.PieChart, "Anggaran")
                HeroMetric(Icons.Default.Security, "Brankas")
                HeroMetric(Icons.AutoMirrored.Filled.TrendingUp, "Arus Kas")
            }
        }
    }

    @Composable
    private fun HeroMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    @Composable
    private fun StepIndicator(step: Int) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (index in 1..4) {
                val width by animateDpAsState(if (index == step) 28.dp else 8.dp)
                Box(
                    modifier = Modifier
                        .size(width, 6.dp)
                        .clip(CircleShape)
                        .background(if (index == step) AppColors.Primary else AppColors.Outline)
                )
            }
        }
    }

    @Composable
    private fun OnboardingStepContent(step: Int, state: OnboardingState, viewModel: OnboardingViewModel) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (step) {
                1 -> {
                    Text(
                        text = "Satu Aplikasi Finansial.\nKendali Penuh Aset Anda.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = "Kelola arus kas harian, alokasi anggaran belanja, dan brankas dokumen secara offline-first dan terenkripsi.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                2 -> {
                    Text(
                        text = "Lengkapi Profil Anda",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                    AppTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChanged,
                        label = "Nama Lengkap",
                        placeholder = "Contoh: Alex Piter"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("A", "M", "R", "S").forEachIndexed { index, initial ->
                            val isSelected = state.avatarId == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) AppColors.Primary else AppColors.SurfaceRaised)
                                    .clickable { viewModel.onAvatarChanged(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) Color.White else AppColors.TextMain
                                )
                            }
                        }
                    }
                }
                3 -> {
                    Text(
                        text = "Buat Dompet Utama",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                    AppTextField(
                        value = state.walletName,
                        onValueChange = viewModel::onWalletNameChanged,
                        label = "Nama Dompet",
                        placeholder = "Contoh: Rekening Utama, Dompet Harian"
                    )
                    AppAmountInput(
                        value = state.initialBalance,
                        onValueChange = viewModel::onInitialBalanceChanged,
                        label = "Saldo Awal (Rp)"
                    )
                }
                4 -> {
                    SuccessCelebration(
                        title = "Semua Siap!",
                        message = "Workspace finansial pribadi Anda siap digunakan untuk mencatat pengeluaran, anggaran, dan brankas dokumen."
                    )
                }
                else -> {
                    SuccessCelebration(
                        title = "Menyiapkan Data",
                        message = "Kami sedang menyelesaikan konfigurasi aplikasi Anda."
                    )
                }
            }
        }
    }
}
