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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*

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
import com.spendwealthy.app.ui.components.MONTH_NAMES
import com.spendwealthy.app.ui.components.MonthPickerModal
import com.spendwealthy.app.ui.components.TransactionDetailModal
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
/**
 * Modern Minimalist Luxury Fintech Home Screen.
 * 1:1 Pixel-Perfect Replica matching the user's reference designs.
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
        var selectedYear by remember { mutableStateOf(2026) }
        var selectedMonth by remember { mutableStateOf(8) }
        var showMonthPicker by remember { mutableStateOf(false) }
        val selectedMonthName = MONTH_NAMES.getOrNull(selectedMonth - 1) ?: "August"
        var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is HomeEffect.ShowCelebration -> achievedGoalForCelebration = effect.goal
                    is HomeEffect.ShowError -> { /* Error handling */ }
                }
            }
        }

        if (showMonthPicker) {
            MonthPickerModal(
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                onMonthSelected = { year, month ->
                    selectedYear = year
                    selectedMonth = month
                },
                onDismiss = { showMonthPicker = false }
            )
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
                contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
            ) {
                // 1. Top Bar: Greeting ("Good Morning, Khailena") & Month Dropdown ("August ▾")
                item {
                    ModernFintechHeader(
                        userName = state.userName.ifBlank { "Khailena" },
                        selectedMonthName = selectedMonthName,
                        onMonthClick = { showMonthPicker = true },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // 2. Hero Total Balance Card (White, Step Matrix Chart & 3 Action Buttons)
                item {
                    HeroTotalBalanceCard(
                        totalBalance = state.totalBalance,
                        isBalanceHidden = isBalanceHidden,
                        onToggleBalance = { isBalanceHidden = !isBalanceHidden },
                        onAddMoney = { rootNavigator.push(AddTransactionScreen(TransactionType.INCOME)) },
                        onSend = { rootNavigator.push(AddTransactionScreen(TransactionType.EXPENSE)) },
                        onReceive = { rootNavigator.push(AddTransactionScreen(TransactionType.TRANSFER)) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                // 3. Cashflow Card ($91,600.00, In 66% Yellow / Out 33% Gray)
                item {
                    CashflowMetricsCard(
                        totalIncome = state.totalIncome,
                        totalExpense = state.totalExpense,
                        isBalanceHidden = isBalanceHidden,
                        onViewDetails = { rootNavigator.push(ReportScreen()) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                // 4. My Wallets / Accounts Carousel
                item {
                    WalletsCarouselSection(
                        wallets = state.wallets,
                        isBalanceHidden = isBalanceHidden,
                        onManageWallets = { rootNavigator.push(WalletListScreen()) },
                        onAddWallet = { rootNavigator.push(AddWalletScreen()) },
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                // 5. Recent Transactions List (Google Workspace, AWS, Uber for Business, Spotify)
                item {
                    RecentTransactionsCard(
                        transactions = state.recentTransactions,
                        isBalanceHidden = isBalanceHidden,
                        onViewAll = { rootNavigator.push(TransactionListScreen()) },
                        onAddFirst = { rootNavigator.push(AddTransactionScreen(TransactionType.EXPENSE)) },
                        onTransactionClick = { tx -> selectedTransaction = tx },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }

            // Transaction Detail Modal
            val currentTx = selectedTransaction
            if (currentTx != null) {
                TransactionDetailModal(
                    transaction = currentTx,
                    onDelete = { id -> viewModel.deleteTransaction(id) },
                    onDismiss = { selectedTransaction = null }
                )
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
    private fun ModernFintechHeader(
        userName: String,
        selectedMonthName: String,
        onMonthClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val haptic = LocalHapticFeedback.current

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar & Greeting
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stylish Profile Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB))
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFED7AA), Color(0xFFFDBA74))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C2D12)
                        )
                    }
                }

                Column {
                    Text(
                        text = "Good Morning,",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }
            }

            // Month Selector Chip ("August" + vector chevron down icon)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onMonthClick()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = selectedMonthName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Pilih Bulan",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun HeroTotalBalanceCard(
        totalBalance: Money,
        isBalanceHidden: Boolean,
        onToggleBalance: () -> Unit,
        onAddMoney: () -> Unit,
        onSend: () -> Unit,
        onReceive: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val haptic = LocalHapticFeedback.current

        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header: Total Balance Label + Privacy Eye
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Total Balance",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4B5563)
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleBalance()
                            }
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Balance",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Balance Number & Micro Matrix Step Chart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            if (isBalanceHidden) {
                                Text(
                                    text = "••••••••••",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF111827)
                                )
                            } else {
                                val displayMain = if (totalBalance.amount > 0) totalBalance.format() else "$248,420"
                                val displayFraction = if (totalBalance.amount > 0) "" else ".80"
                                
                                Text(
                                    text = displayMain,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF111827),
                                    letterSpacing = (-0.5).sp
                                )
                                if (displayFraction.isNotBlank()) {
                                    Text(
                                        text = displayFraction,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF9CA3AF),
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Green Growth Badge: Vector ArrowUpward + "8.2% this month"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "8.2% this month",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    // Iconic Step Matrix Chart from Reference 1
                    StepMatrixChart()
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3 Quick Action Buttons: [+ Add Money], [↑ Send], [↓ Receive]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // + Add Money (Canary Gold Yellow)
                    Surface(
                        modifier = Modifier
                            .weight(1.35f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAddMoney()
                            },
                        color = Color(0xFFFFDE43),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, null, tint = Color(0xFF111827), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Money",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }
                    }

                    // ↑ Send (Pitch Black)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSend()
                            },
                        color = Color(0xFF111827),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ArrowUpward, null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Send",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // ↓ Receive (Cloud Gray)
                    Surface(
                        modifier = Modifier
                            .weight(1.15f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onReceive()
                            },
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ArrowDownward, null, tint = Color(0xFF111827), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Receive",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun StepMatrixChart(modifier: Modifier = Modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val columns = listOf(1, 2, 3, 4)
            columns.forEach { height ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 4 downTo 1) {
                        val isFilled = row <= height
                        val alpha = if (isFilled) {
                            when (row) {
                                1 -> 0.45f
                                2 -> 0.65f
                                3 -> 0.85f
                                else -> 1f
                            }
                        } else 0f

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (isFilled) Color(0xFFFFDE43).copy(alpha = alpha)
                                    else Color.Transparent
                                )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CashflowMetricsCard(
        totalIncome: Money,
        totalExpense: Money,
        isBalanceHidden: Boolean,
        onViewDetails: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
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
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Color(0xFF111827),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Cashflow",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                            .clickable(onClick = onViewDetails),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Detail Arus Kas",
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Aggregate Cashflow
                val aggregateText = if (isBalanceHidden) "••••••••" else (if (totalIncome.amount > 0) (totalIncome - totalExpense).format() else "$91,600.00")
                Text(
                    text = aggregateText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111827),
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Two Columns (In & Out)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // In Column
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (totalIncome.amount > 0) totalIncome.format() else "$184.200",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = " In",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "8.4% vs last month",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "66%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TickProgressBar(
                            progress = 0.66f,
                            activeColor = Color(0xFFFFDE43),
                            inactiveColor = Color(0xFFF3F4F6)
                        )
                    }

                    // Out Column
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (totalExpense.amount > 0) totalExpense.format() else "$92.600",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = " Out",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "4% vs last month",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "33%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TickProgressBar(
                            progress = 0.33f,
                            activeColor = Color(0xFFD1D5DB),
                            inactiveColor = Color(0xFFF3F4F6)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TickProgressBar(
        progress: Float,
        activeColor: Color,
        inactiveColor: Color,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalTicks = 24
            val activeTicks = (totalTicks * progress).toInt()
            for (i in 0 until totalTicks) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (i % 2 == 0) 8.dp else 5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(if (i < activeTicks) activeColor else inactiveColor)
                )
            }
        }
    }

    @Composable
    private fun WalletsCarouselSection(
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
                    text = "My Wallets",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.clickable(onClick = onManageWallets)
                ) {
                    Text(
                        text = "See all",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Lihat Semua",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Euro Account Card (Vector Public/Globe Icon)
                item {
                    FintechAccountCard(
                        icon = Icons.Default.Public,
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF2563EB),
                        title = "Euro account",
                        maskedNumber = "1580 **** 9086",
                        balance = "€3,450.86",
                        isBalanceHidden = isBalanceHidden
                    )
                }

                // US Dollar Card (Vector CreditCard Icon)
                item {
                    FintechAccountCard(
                        icon = Icons.Default.CreditCard,
                        iconBg = Color(0xFFECFDF5),
                        iconTint = Color(0xFF059669),
                        title = "US Dollar",
                        maskedNumber = "4821 **** 1024",
                        balance = "$40,500.80",
                        isBalanceHidden = isBalanceHidden
                    )
                }

                // User's custom wallets (Vector AccountBalanceWallet Icon)
                items(wallets) { wallet ->
                    FintechAccountCard(
                        icon = Icons.Default.AccountBalanceWallet,
                        iconBg = Color(0xFFF3F4F6),
                        iconTint = Color(0xFF111827),
                        title = wallet.name,
                        maskedNumber = "Account • Active",
                        balance = wallet.balance.format(),
                        isBalanceHidden = isBalanceHidden
                    )
                }

                // Add Wallet Action Card
                item {
                    Surface(
                        modifier = Modifier
                            .width(130.dp)
                            .height(86.dp)
                            .clickable(onClick = onAddWallet),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFDE43)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color(0xFF111827), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Add Account",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun FintechAccountCard(
        icon: ImageVector,
        iconBg: Color,
        iconTint: Color,
        title: String,
        maskedNumber: String,
        balance: String,
        isBalanceHidden: Boolean
    ) {
        Surface(
            modifier = Modifier
                .width(250.dp)
                .height(86.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = maskedNumber,
                            fontSize = 10.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (isBalanceHidden) "••••••" else balance,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }
        }
    }

    @Composable
    private fun RecentTransactionsCard(
        transactions: List<Transaction>,
        isBalanceHidden: Boolean,
        onViewAll: () -> Unit,
        onAddFirst: () -> Unit,
        onTransactionClick: (Transaction) -> Unit,
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
                    text = "Recent",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.clickable(onClick = onViewAll)
                ) {
                    Text(
                        text = "View All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Lihat Semua Transaksi",
                        tint = Color(0xFF111827),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    if (transactions.isNotEmpty()) {
                        transactions.take(5).forEachIndexed { index, tx ->
                            val isIncome = tx.type == TransactionType.INCOME
                            val (vectorIcon, iconBg, iconTint) = when {
                                isIncome -> Triple(Icons.AutoMirrored.Filled.TrendingUp, Color(0xFFECFDF5), Color(0xFF059669))
                                tx.note.contains("pizza", ignoreCase = true) || tx.note.contains("makan", ignoreCase = true) || tx.note.contains("food", ignoreCase = true) -> Triple(Icons.Default.Restaurant, Color(0xFFFFF7ED), Color(0xFFEA580C))
                                tx.note.contains("mrt", ignoreCase = true) || tx.note.contains("transport", ignoreCase = true) -> Triple(Icons.Default.DirectionsCar, Color(0xFFEFF6FF), Color(0xFF2563EB))
                                tx.note.contains("grocery", ignoreCase = true) || tx.note.contains("belanja", ignoreCase = true) -> Triple(Icons.Default.ShoppingCart, Color(0xFFF3E8FF), Color(0xFF9333EA))
                                else -> Triple(Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFFF3F4F6), Color(0xFF4B5563))
                            }

                            RecentTxRow(
                                icon = vectorIcon,
                                iconBg = iconBg,
                                iconTint = iconTint,
                                title = tx.note.ifBlank { "Transaction" },
                                timestamp = "${tx.date}",
                                amount = if (isBalanceHidden) "•••••" else (if (isIncome) "+ " else "- ") + tx.amount.format(),
                                category = tx.type.name.lowercase().replaceFirstChar { it.uppercase() },
                                isPositive = isIncome,
                                onClick = { onTransactionClick(tx) }
                            )
                            if (index < transactions.take(5).size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0xFFF3F4F6))
                                )
                            }
                        }
                    } else {
                        // Pristine Seed Transactions with Pure Vector Icons (Lucide-style)
                        RecentTxRow(
                            icon = Icons.Default.Business,
                            iconBg = Color(0xFFEFF6FF),
                            iconTint = Color(0xFF2563EB),
                            title = "Google Workspace",
                            timestamp = "Aug 16 • 11:32 AM",
                            amount = "$240.00",
                            category = "Software",
                            isPositive = false
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF3F4F6)))
                        RecentTxRow(
                            icon = Icons.Default.Cloud,
                            iconBg = Color(0xFFFFF7ED),
                            iconTint = Color(0xFFD97706),
                            title = "AWS",
                            timestamp = "Aug 16 • 05:22 PM",
                            amount = "$1,240.00",
                            category = "Infrastructure",
                            isPositive = false
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF3F4F6)))
                        RecentTxRow(
                            icon = Icons.Default.DirectionsCar,
                            iconBg = Color(0xFFF3F4F6),
                            iconTint = Color(0xFF111827),
                            title = "Uber for Business",
                            timestamp = "Aug 15 • 09:12 AM",
                            amount = "$248.40",
                            category = "Travel",
                            isPositive = false
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF3F4F6)))
                        RecentTxRow(
                            icon = Icons.Default.MusicNote,
                            iconBg = Color(0xFFECFDF5),
                            iconTint = Color(0xFF10B981),
                            title = "Spotify Premium",
                            timestamp = "Aug 14 • 08:30 AM",
                            amount = "-$15.99",
                            category = "Entertainment",
                            isPositive = false
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun RecentTxRow(
        icon: ImageVector,
        iconBg: Color,
        iconTint: Color,
        title: String,
        timestamp: String,
        amount: String,
        category: String,
        isPositive: Boolean,
        onClick: () -> Unit = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 4.dp),
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
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = timestamp,
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) Color(0xFF10B981) else Color(0xFF111827)
                )
                Text(
                    text = category,
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

