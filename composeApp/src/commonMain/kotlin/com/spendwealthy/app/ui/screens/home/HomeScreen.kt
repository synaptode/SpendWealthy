package com.spendwealthy.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.components.GoalCelebrationOverlay
import com.spendwealthy.app.ui.screens.bill.BillListScreen
import com.spendwealthy.app.ui.screens.home.components.GoalContributionDialog
import com.spendwealthy.app.ui.screens.report.ReportScreen
import com.spendwealthy.app.ui.screens.transaction.AddTransactionScreen
import com.spendwealthy.app.ui.screens.transaction.list.TransactionListScreen
import com.spendwealthy.app.ui.screens.wallet.AddWalletScreen
import com.spendwealthy.app.ui.screens.wallet.WalletListScreen
import com.spendwealthy.app.ui.theme.AppColors

/**
 * Modern, minimalist, and contemporary Home Screen.
 * Prioritizes clarity, daily financial peace-of-mind ("Safe-to-Spend"), and frictionless actions.
 */
class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<HomeViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val rootNavigator = navigator.parent ?: navigator

        var isBalanceHidden by remember { mutableStateOf(false) }
        var selectedGoalForContribution by remember { mutableStateOf<Goal?>(null) }
        var achievedGoalForCelebration by remember { mutableStateOf<Goal?>(null) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is HomeEffect.ShowCelebration -> achievedGoalForCelebration = effect.goal
                    is HomeEffect.ShowError -> { /* Error handling */ }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
            ) {
                // 1. Top Bar: Greeting, Date & Privacy Action
                item {
                    ModernHeader(
                        userName = state.userName,
                        isBalanceHidden = isBalanceHidden,
                        onToggleBalance = { isBalanceHidden = !isBalanceHidden },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // 2. Hero Financial Card (Obsidian & Neon Lime)
                item {
                    HeroBalanceCard(
                        totalBalance = state.totalBalance,
                        dailyAllowance = state.dailyAllowance,
                        totalIncome = state.totalIncome,
                        totalExpense = state.totalExpense,
                        isBalanceHidden = isBalanceHidden,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                // 3. Quick Action Hub (4 Clean Minimalist Circles)
                item {
                    QuickActionHub(
                        onAddExpense = { rootNavigator.push(AddTransactionScreen(TransactionType.EXPENSE)) },
                        onAddIncome = { rootNavigator.push(AddTransactionScreen(TransactionType.INCOME)) },
                        onTransfer = { rootNavigator.push(AddTransactionScreen(TransactionType.TRANSFER)) },
                        onBills = { rootNavigator.push(BillListScreen()) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                // 4. Dompet & Rekening (Sleek Horizontal Carousel)
                item {
                    WalletsSection(
                        wallets = state.wallets,
                        isBalanceHidden = isBalanceHidden,
                        onManageWallets = { rootNavigator.push(WalletListScreen()) },
                        onAddWallet = { rootNavigator.push(AddWalletScreen()) },
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                // 5. Intelligent Pounce Spotlight (Single Dynamic Insight)
                item {
                    PounceSpotlightCard(
                        state = state,
                        onViewDetails = { rootNavigator.push(ReportScreen()) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                // 6. Recent Transactions Ledger
                item {
                    RecentTransactionsSection(
                        transactions = state.recentTransactions,
                        isBalanceHidden = isBalanceHidden,
                        onViewAll = { rootNavigator.push(TransactionListScreen()) },
                        onAddFirst = { rootNavigator.push(AddTransactionScreen(TransactionType.EXPENSE)) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                // 7. Pounce Wealth Lab (Minimalist 2-Column Discovery Grid)
                item {
                    WealthLabSection(
                        score = state.sovereignWealthScore?.score ?: 80,
                        goalsCount = state.goals.size,
                        onOpenAnalytics = { rootNavigator.push(ReportScreen()) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
            }

            // Dialogs & Celebrations
            selectedGoalForContribution?.let { goal ->
                GoalContributionDialog(
                    goal = goal,
                    wallets = state.wallets,
                    onDismiss = { selectedGoalForContribution = null },
                    onContribute = { wallet, amount ->
                        viewModel.onEvent(HomeEvent.ContributeToGoal(goal.id, wallet.id, amount))
                        selectedGoalForContribution = null
                    }
                )
            }


            achievedGoalForCelebration?.let { goal ->
                GoalCelebrationOverlay(
                    goal = goal,
                    onDismiss = { achievedGoalForCelebration = null }
                )
            }
        }
    }

    @Composable
    private fun ModernHeader(
        userName: String,
        isBalanceHidden: Boolean,
        onToggleBalance: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val haptic = LocalHapticFeedback.current

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Halo, $userName 🐾",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Cashflow harian terkendali",
                    fontSize = 13.sp,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Privacy Toggle Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AppColors.Surface)
                        .border(1.dp, AppColors.Outline.copy(alpha = 0.6f), CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggleBalance()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle saldo",
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun HeroBalanceCard(
        totalBalance: Money,
        dailyAllowance: Money,
        totalIncome: Money,
        totalExpense: Money,
        isBalanceHidden: Boolean,
        modifier: Modifier = Modifier
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = AppColors.CardDark,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                AppColors.CardDark,
                                Color(0xFF0F151F)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                // Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL SALDO BERSIH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.55f),
                        letterSpacing = 1.2.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.Primary.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Aktif",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Accent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Balance Number
                Text(
                    text = if (isBalanceHidden) "••••••••••" else totalBalance.format(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Safe-to-Spend ("Sisa Aman Harian") Highlight Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.07f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Accent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = AppColors.CardDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Sisa Aman Harian",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isBalanceHidden) "•••••" else "${dailyAllowance.format()} / hari",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.Accent
                                )
                            }
                        }

                        Text(
                            text = "Bebas Cemas",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Income vs Expense Mini Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(AppColors.Success.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowDownward, null, tint = AppColors.Success, modifier = Modifier.size(14.dp))
                        }
                        Column {
                            Text("Masuk", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                            Text(if (isBalanceHidden) "••••" else totalIncome.format(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(AppColors.Danger.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowUpward, null, tint = AppColors.Danger, modifier = Modifier.size(14.dp))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Keluar", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                            Text(if (isBalanceHidden) "••••" else totalExpense.format(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun QuickActionHub(
        onAddExpense: () -> Unit,
        onAddIncome: () -> Unit,
        onTransfer: () -> Unit,
        onBills: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val haptic = LocalHapticFeedback.current

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionPill(
                icon = Icons.Default.Add,
                label = "Catat",
                isPrimary = true,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddExpense()
                }
            )
            ActionPill(
                icon = Icons.Default.ArrowDownward,
                label = "Pemasukan",
                isPrimary = false,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onAddIncome()
                }
            )
            ActionPill(
                icon = Icons.Default.SwapHoriz,
                label = "Transfer",
                isPrimary = false,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onTransfer()
                }
            )
            ActionPill(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                label = "Tagihan",
                isPrimary = false,

                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onBills()
                }
            )
        }
    }

    @Composable
    private fun ActionPill(
        icon: ImageVector,
        label: String,
        isPrimary: Boolean,
        onClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isPrimary) AppColors.Primary else AppColors.Surface)
                    .border(1.dp, if (isPrimary) Color.Transparent else AppColors.Outline.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isPrimary) AppColors.Accent else AppColors.TextMain,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextMain,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    @Composable
    private fun WalletsSection(
        wallets: List<Wallet>,
        isBalanceHidden: Boolean,
        onManageWallets: () -> Unit,
        onAddWallet: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dompet & Rekening",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Kelola  >",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary,
                    modifier = Modifier.clickable(onClick = onManageWallets)
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(wallets) { wallet ->
                    WalletMiniCard(wallet = wallet, isBalanceHidden = isBalanceHidden)
                }

                // Add Wallet Minimalist Dashed Card
                item {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(115.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppColors.Surface.copy(alpha = 0.6f))
                            .border(1.dp, AppColors.Outline, RoundedCornerShape(20.dp))
                            .clickable(onClick = onAddWallet)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.PrimaryContainer.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tambah Akun",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WalletMiniCard(wallet: Wallet, isBalanceHidden: Boolean) {
        Surface(
            modifier = Modifier
                .width(180.dp)
                .height(115.dp),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            shadowElevation = 4.dp,
            border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wallet.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = "Saldo",
                        fontSize = 10.sp,
                        color = AppColors.TextMuted
                    )
                    Text(
                        text = if (isBalanceHidden) "••••••••" else wallet.balance.format(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain
                    )
                }
            }
        }
    }

    @Composable
    private fun PounceSpotlightCard(
        state: HomeState,
        onViewDetails: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val hasAnomaly = state.detectedAnomalies.isNotEmpty()
        val nearestBill = state.upcomingBills.firstOrNull()

        val headline = when {
            hasAnomaly -> "Perhatian: Bocor Halus Terdeteksi"
            nearestBill != null -> "Pengingat Tagihan Terdekat"
            else -> "Cashflow Dalam Kondisi Zen 🐾"
        }

        val description = when {
            hasAnomaly -> "Ada pengeluaran berulang melebihi rata-rata mingguanmu."
            nearestBill != null -> "${nearestBill.name} (${nearestBill.amount.format()}) jatuh tempo pada ${nearestBill.dueDate}."
            else -> "Pengeluaran harianmu berada dalam batas aman. Pertahankan ritme ini!"
        }

        val cardBg = if (hasAnomaly) AppColors.Danger.copy(alpha = 0.08f) else AppColors.PrimaryContainer.copy(alpha = 0.45f)
        val iconColor = if (hasAnomaly) AppColors.Danger else AppColors.Primary

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onViewDetails),
            shape = RoundedCornerShape(22.dp),
            color = cardBg,
            border = BorderStroke(1.dp, iconColor.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasAnomaly) Icons.Default.WarningAmber else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = headline,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = AppColors.TextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    @Composable
    private fun RecentTransactionsSection(
        transactions: List<Transaction>,
        isBalanceHidden: Boolean,
        onViewAll: () -> Unit,
        onAddFirst: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi Terakhir",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Lihat Semua  >",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary,
                    modifier = Modifier.clickable(onClick = onViewAll)
                )
            }

            if (transactions.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Belum Ada Transaksi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "Yuk catat pengeluaran pertamamu hari ini 🐾",
                            fontSize = 12.sp,
                            color = AppColors.TextMuted,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.Primary)
                                .clickable(onClick = onAddFirst)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "+ Catat Sekarang",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        transactions.take(5).forEachIndexed { index, tx ->
                            val isIncome = tx.type == TransactionType.INCOME
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isIncome) AppColors.Success.copy(alpha = 0.15f) else AppColors.Outline.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isIncome) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.ReceiptLong,
                                            contentDescription = null,
                                            tint = if (isIncome) AppColors.Success else AppColors.TextMain,
                                            modifier = Modifier.size(18.dp)
                                        )

                                    }

                                    Column {
                                        Text(
                                            text = tx.note.ifBlank { "Transaksi ${tx.type.name.lowercase().replaceFirstChar { it.uppercase() }}" },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppColors.TextMain,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${tx.date}",
                                            fontSize = 11.sp,
                                            color = AppColors.TextMuted
                                        )
                                    }
                                }

                                Text(
                                    text = if (isBalanceHidden) "•••••" else (if (isIncome) "+ " else "- ") + tx.amount.format(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isIncome) AppColors.Success else AppColors.TextMain
                                )
                            }

                            if (index < transactions.take(5).size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(AppColors.Outline.copy(alpha = 0.35f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WealthLabSection(
        score: Int,
        goalsCount: Int,
        onOpenAnalytics: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = "Pounce Lab & Analitik",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Score Mini Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenAnalytics),
                    shape = RoundedCornerShape(20.dp),
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Skor Finansial",
                            fontSize = 12.sp,
                            color = AppColors.TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$score",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.Primary
                            )
                            Text(
                                text = "/100",
                                fontSize = 12.sp,
                                color = AppColors.TextMuted,
                                modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                            )
                        }
                        Text(
                            text = "Mandiri Finansial ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Success,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Targets & Goals Mini Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenAnalytics),
                    shape = RoundedCornerShape(20.dp),
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Target Impian",
                            fontSize = 12.sp,
                            color = AppColors.TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$goalsCount Sasaran",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "Tabungan Terjadwal 🎯",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
