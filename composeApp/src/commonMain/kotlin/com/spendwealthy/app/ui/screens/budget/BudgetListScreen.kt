package com.spendwealthy.app.ui.screens.budget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.components.tactilePress
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.ui.screens.family.FamilyScreen
import com.spendwealthy.app.ui.screens.settings.SettingsScreen
import com.spendwealthy.app.ui.screens.wallet.AddWalletScreen
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

class BudgetListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BudgetListViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val rootNavigator = navigator.parent ?: navigator
        val haptic = LocalHapticFeedback.current

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(bottom = 140.dp)
            ) {
                item {
                    CardsHeader(
                        onAdd = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(AddWalletScreen())
                        }
                    )
                }

                item {
                    CardCarousel(
                        state = state,
                        viewModel = viewModel,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(AddWalletScreen())
                        }
                    )
                }

                item {
                    CardControls(
                        onAddCard = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(AddWalletScreen())
                        },
                        onFreeze = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(SettingsScreen())
                        },
                        onSecurity = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(SettingsScreen())
                        }
                    )
                }

                item {
                    SectionHeader(
                        title = "Alokasi Anggaran",
                        badge = "${state.budgets.size} aktif",
                        action = "+ Tambah",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(AddBudgetScreen())
                        }
                    )
                }

                if (state.budgets.isEmpty()) {
                    item {
                        EmptyBudgetCard(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                rootNavigator.push(AddBudgetScreen())
                            }
                        )
                    }
                } else {
                    item {
                        BudgetHealthOverviewCard(viewModel = viewModel)
                    }
                    items(state.budgets, key = { it.id }) { budget ->
                        BudgetEnvelopeCard(
                            title = viewModel.getBudgetName(budget),
                            budget = budget,
                            usage = viewModel.getUsage(budget),
                            remaining = viewModel.getRemaining(budget).formatWithSymbol(),
                            viewModel = viewModel
                        )
                    }
                }

                // Savings & Financial Goals Section
                item {
                    Spacer(Modifier.height(14.dp))
                    SectionHeader(
                        title = "Target Tabungan & Impian",
                        badge = "${state.goals.size} Aktif",
                        action = "+ Target Baru",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(AddBudgetScreen())
                        }
                    )
                }

                if (state.goals.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.Surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = AppColors.Primary.copy(alpha = 0.12f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Savings,
                                            contentDescription = null,
                                            tint = AppColors.Primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "Belum Ada Target Tabungan",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextMain
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Buat target dana darurat atau impian untuk melacak kemajuan tabungan Anda.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.TextMuted,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(state.goals, key = { it.id }) { goal ->
                        val target = goal.targetAmount.amount.coerceAtLeast(1L)
                        val progress = (goal.currentAmount.amount.toFloat() / target).coerceIn(0f, 1f)
                        val pct = ((goal.currentAmount.amount.toDouble() / target) * 100).toInt()
                        val (icon, color) = when {
                            goal.name.contains("darurat", ignoreCase = true) || goal.name.contains("emergency", ignoreCase = true) -> Icons.Default.Shield to Color(0xFF10B981)
                            goal.name.contains("investasi", ignoreCase = true) || goal.name.contains("saham", ignoreCase = true) -> Icons.AutoMirrored.Filled.TrendingUp to Color(0xFFD97706)
                            else -> Icons.Default.Savings to Color(0xFF2563EB)
                        }

                        SavingsGoalCard(
                            title = goal.name,
                            currentAmount = goal.currentAmount.formatWithSymbol(),
                            targetAmount = goal.targetAmount.formatWithSymbol(),
                            progress = progress,
                            icon = icon,
                            accentColor = color,
                            badgeText = "$pct% Terkumpul",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        )
                    }
                }

                // Family Shared Savings Hub Card
                item {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                rootNavigator.push(FamilyScreen())
                            },
                        shape = RoundedCornerShape(20.dp),
                        color = AppColors.SurfaceRaised,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.Primary.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = AppColors.Primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f, fill = false),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Tabungan Keluarga",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = AppColors.TextMain,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AppColors.Primary
                                        ) {
                                            Text(
                                                text = "Bersama",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = AppColors.OnPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Nabung bareng pasangan & keluarga untuk wujudkan target impian bersama.",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.TextMuted,
                                        lineHeight = 15.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CardsHeader(onAdd: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 10.dp)
            ) {
                Text(
                    text = "Portofolio & Anggaran",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain,
                    letterSpacing = (-0.5).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Alokasi batas pos belanja & pemantauan likuiditas",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                shape = CircleShape,
                color = AppColors.Primary,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAdd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Kartu",
                        tint = AppColors.OnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun CardCarousel(
        state: BudgetListState,
        viewModel: BudgetListViewModel,
        onClick: () -> Unit
    ) {
        val primaryBudget = state.budgets.firstOrNull()
        val secondBudget = state.budgets.drop(1).firstOrNull()
        val balanceLabel = primaryBudget?.limit?.formatWithSymbol() ?: "Rp0"
        val remainingLabel = primaryBudget?.let { viewModel.getRemaining(it).formatWithSymbol() } ?: "Siap Digunakan"

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ObsidianPaymentCard(
                    label = "Plafon Anggaran",
                    cardNumber = "•••• •••• •••• 5690",
                    amount = balanceLabel,
                    badgeText = "Sisa: $remainingLabel",
                    network = "LIMIT",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    ),
                    onClick = onClick
                )
            }
            if (secondBudget != null) {
                val secondRemaining = viewModel.getRemaining(secondBudget).formatWithSymbol()
                item {
                    ObsidianPaymentCard(
                        label = viewModel.getBudgetName(secondBudget),
                        cardNumber = "•••• •••• •••• 2488",
                        amount = secondBudget.limit.formatWithSymbol(),
                        badgeText = "Sisa: $secondRemaining",
                        network = "KATEGORI",
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF064E3B), Color(0xFF022C22))
                        ),
                        onClick = onClick
                    )
                }
            }
        }
    }

    @Composable
    private fun ObsidianPaymentCard(
        label: String,
        cardNumber: String,
        amount: String,
        badgeText: String,
        network: String,
        gradient: Brush,
        onClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .width(286.dp)
                .height(168.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(gradient)
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                .bouncyClickable(
                    pressedScale = 0.96f,
                    hapticType = HapticFeedbackType.LongPress,
                    onClick = onClick
                )
                .padding(20.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = size.width * 0.45f,
                    center = Offset(size.width * 0.9f, size.height * 0.15f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = size.width * 0.35f,
                    center = Offset(size.width * 0.5f, size.height * 0.85f)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }

                    Text(
                        text = network,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Text(
                    text = cardNumber,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Limit Terpasang",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.55f)
                        )
                        Text(
                            text = amount,
                            style = AppTypography.headlineNumeric(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CardControls(
        onAddCard: () -> Unit,
        onFreeze: () -> Unit,
        onSecurity: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlActionButton(
                icon = Icons.Default.Add,
                label = "Kartu Baru",
                modifier = Modifier.weight(1f),
                onClick = onAddCard
            )
            ControlActionButton(
                icon = Icons.Default.Lock,
                label = "Kunci Kartu",
                modifier = Modifier.weight(1f),
                onClick = onFreeze
            )
            ControlActionButton(
                icon = Icons.Default.Shield,
                label = "Keamanan",
                modifier = Modifier.weight(1f),
                onClick = onSecurity
            )
        }
    }

    @Composable
    private fun ControlActionButton(
        icon: ImageVector,
        label: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .bouncyClickable(
                    pressedScale = 0.93f,
                    hapticType = HapticFeedbackType.LongPress,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(16.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppColors.SurfaceRaised),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    @Composable
    private fun SectionHeader(
        title: String,
        badge: String,
        action: String,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.SurfaceRaised
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = action,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onClick)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
    }

    @Composable
    private fun BudgetHealthOverviewCard(
        viewModel: BudgetListViewModel
    ) {
        val totalAllocated = viewModel.totalAllocated
        val totalSpent = viewModel.totalSpent
        val usage = viewModel.overallUsagePercentage
        val criticalCount = viewModel.criticalBudgetsCount
        val warningCount = viewModel.warningBudgetsCount

        val (accentColor, badgeBg, statusText) = when {
            criticalCount > 0 -> Triple(
                AppColors.Danger,
                Color(0xFFF43F5E).copy(alpha = 0.15f),
                "$criticalCount Pos Overbudget 🚨"
            )
            warningCount > 0 -> Triple(
                AppColors.Warning,
                Color(0xFFF59E0B).copy(alpha = 0.15f),
                "$warningCount Pos Waspada (80%+) ⚠️"
            )
            else -> Triple(
                AppColors.Primary,
                AppColors.Primary.copy(alpha = 0.15f),
                "Semua Anggaran Sehat ✅"
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.SurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ringkasan Plafon Bulanan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMuted
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Terpakai: ${totalSpent.formatWithSymbol()}",
                            style = AppTypography.bodyNumeric(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.TextMain
                            )
                        )
                        Text(
                            text = "Alokasi Maksimal: ${totalAllocated.formatWithSymbol()}",
                            style = AppTypography.labelNumeric(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextMuted
                            )
                        )
                    }
                    Text(
                        text = "${(usage * 100).toInt()}%",
                        style = AppTypography.headlineNumeric(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = accentColor
                        )
                    )
                }

                val progressFraction = usage.toFloat().coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.Outline)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    when {
                                        criticalCount > 0 -> listOf(Color(0xFFF59E0B), Color(0xFFF43F5E))
                                        warningCount > 0 -> listOf(Color(0xFF10B981), Color(0xFFF59E0B))
                                        else -> listOf(Color(0xFF10B981), Color(0xFF34D399), Color(0xFFDEFF37))
                                    }
                                )
                            )
                    )
                }
            }
        }
    }

    @Composable
    private fun BudgetEnvelopeCard(
        title: String,
        budget: Budget,
        usage: Double,
        remaining: String,
        viewModel: BudgetListViewModel
    ) {
        val isOverbudget = usage >= 1.0
        val isWarning = usage >= 0.8 && !isOverbudget
        val progress = usage.toFloat().coerceIn(0f, 1f)
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 650)
        )

        val percentageInt = (usage * 100).toInt()
        val (accentColor, progressBrush, statusLabel) = when {
            isOverbudget -> Triple(
                AppColors.Danger,
                Brush.horizontalGradient(listOf(Color(0xFFF43F5E), Color(0xFFBE123C))),
                "Overbudget"
            )
            isWarning -> Triple(
                AppColors.Warning,
                Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))),
                "Waspada"
            )
            else -> Triple(
                AppColors.Primary,
                Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF34D399), Color(0xFFDEFF37))),
                "Terkendali"
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .tactilePress(pressedScale = 0.97f),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isOverbudget) Color(0xFFF43F5E).copy(alpha = 0.5f) else AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(accentColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isOverbudget) Icons.Default.Warning else Icons.Default.Payments,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppColors.TextMain,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isOverbudget) "Melebihi plafon: ${viewModel.getExceeded(budget).formatWithSymbol()}" else "Sisa anggaran: $remaining",
                                style = AppTypography.labelNumeric(
                                    fontSize = 11.sp,
                                    fontWeight = if (isOverbudget) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isOverbudget) AppColors.Danger else AppColors.TextMuted
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = budget.limit.formatWithSymbol(),
                            style = AppTypography.bodyNumeric(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.TextMain
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isOverbudget) "🚨 Overbudget (+${percentageInt - 100}%)" else "$statusLabel • $percentageInt%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // High-End Gradient Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(AppColors.SurfaceRaised)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(9.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(progressBrush)
                    )
                }
            }
        }
    }

    @Composable
    private fun EmptyBudgetCard(onClick: () -> Unit) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(22.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Belum Ada Pos Anggaran",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = "Atur batas belanja per kategori agar pos keuangan tetap terkendali.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextMuted
                    )
                }
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                    tint = AppColors.TextMuted
                )
            }
        }
    }

    @Composable
    private fun SavingsGoalCard(
        title: String,
        currentAmount: String,
        targetAmount: String,
        progress: Float,
        icon: ImageVector,
        accentColor: Color,
        badgeText: String,
        onClick: () -> Unit
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 650)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(accentColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppColors.TextMain,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Target: $targetAmount",
                                style = AppTypography.labelNumeric(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextMuted
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = currentAmount,
                            style = AppTypography.bodyNumeric(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.TextMain
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // High-End Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.SurfaceRaised)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor)
                    )
                }
            }
        }
    }
}
