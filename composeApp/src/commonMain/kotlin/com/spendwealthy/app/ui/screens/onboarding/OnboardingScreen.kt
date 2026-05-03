@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.spendwealthy.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
        val state by viewModel.uiState.collectAsState()

        Column(
            modifier = Modifier.fillMaxSize().background(AppColors.Background),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FinanceHero(modifier = Modifier.fillMaxWidth().weight(1.12f))

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 28.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedContent(
                    targetState = state.step,
                    transitionSpec = {
                        (slideInHorizontally { it / 2 } + fadeIn()).togetherWith(slideOutHorizontally { -it / 2 } + fadeOut())
                            .using(SizeTransform(clip = false))
                    }
                ) { step ->
                    OnboardingStepContent(step, state, viewModel)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    StepIndicator(state.step)
                    Button(
                        onClick = { viewModel.nextStep() },
                        modifier = Modifier.fillMaxWidth().height(62.dp),
                        shape = RoundedCornerShape(31.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(
                            text = if (state.step < 4) "Next" else "Get Started",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextContrast
                        )
                        Spacer(Modifier.size(10.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = AppColors.TextContrast)
                    }
                }
            }
        }
    }

    @Composable
    private fun FinanceHero(modifier: Modifier) {
        Box(
            modifier = modifier.clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)).background(AppColors.PrimaryDark)
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(AppColors.CardBlue.copy(alpha = 0.42f), radius = size.width * 0.34f, center = Offset(size.width * 0.2f, size.height * 0.18f))
                drawCircle(AppColors.Accent.copy(alpha = 0.52f), radius = size.width * 0.28f, center = Offset(size.width * 0.86f, size.height * 0.28f))
                val cardWidth = size.width * 0.72f
                val cardHeight = size.height * 0.28f
                drawRoundRect(
                    color = AppColors.CardBlue,
                    topLeft = Offset(size.width * 0.14f, size.height * 0.34f),
                    size = Size(cardWidth, cardHeight),
                    cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx())
                )
                drawRoundRect(
                    color = AppColors.Accent,
                    topLeft = Offset(size.width * 0.28f, size.height * 0.58f),
                    size = Size(cardWidth * 0.82f, cardHeight * 0.76f),
                    cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
                )
            }

            Row(
                modifier = Modifier.align(Alignment.Center).padding(top = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroMetric(Icons.Default.CreditCard, "Cards")
                HeroMetric(Icons.Default.PieChart, "Budget")
                HeroMetric(Icons.AutoMirrored.Filled.TrendingUp, "Growth")
            }
        }
    }

    @Composable
    private fun HeroMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(58.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(26.dp))
            }
            Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }

    @Composable
    private fun StepIndicator(step: Int) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (index in 1..4) {
                val width by animateDpAsState(if (index == step) 24.dp else 8.dp)
                Box(
                    modifier = Modifier.size(width, 8.dp).clip(CircleShape)
                        .background(if (index == step) AppColors.Secondary else AppColors.Outline)
                )
            }
        }
    }

    @Composable
    private fun OnboardingStepContent(step: Int, state: OnboardingState, viewModel: OnboardingViewModel) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (step) {
                1 -> {
                    Text(
                        text = "All Your Finances.\nOne Powerful Platform.",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = "Send, receive, and manage money with a fast, secure daily finance workspace.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
                2 -> {
                    Text("Set Up Your Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    AppTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChanged,
                        label = "Full Name",
                        placeholder = "e.g. Alex Piter"
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf("A", "M", "R", "S").forEachIndexed { index, initial ->
                            val isSelected = state.avatarId == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isSelected) AppColors.Primary else AppColors.SurfaceRaised)
                                    .clickable { viewModel.onAvatarChanged(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initial, fontSize = 20.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.White else AppColors.TextMain)
                            }
                        }
                    }
                }
                3 -> {
                    Text("Create Main Wallet", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    AppTextField(
                        value = state.walletName,
                        onValueChange = viewModel::onWalletNameChanged,
                        label = "Main Wallet Name",
                        placeholder = "e.g. Everyday Wallet"
                    )
                    AppAmountInput(
                        value = state.initialBalance,
                        onValueChange = viewModel::onInitialBalanceChanged,
                        label = "Initial Balance"
                    )
                }
                4 -> {
                    SuccessCelebration(
                        title = "You're All Set!",
                        message = "Your finance workspace is ready for budgets, cards, bills, and transactions."
                    )
                }
            }
        }
    }
}
