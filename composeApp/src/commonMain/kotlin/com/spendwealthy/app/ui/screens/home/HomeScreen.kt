package com.spendwealthy.app.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import com.spendwealthy.app.ui.components.MotionSpecs
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.components.tactilePress
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.components.BrandLogo
import com.spendwealthy.app.ui.components.KnownBrand
import com.spendwealthy.app.ui.components.GoalCelebrationOverlay
import com.spendwealthy.app.ui.components.MONTH_NAMES
import com.spendwealthy.app.ui.components.MonthPickerModal
import com.spendwealthy.app.ui.components.TransactionDetailModal
import com.spendwealthy.app.ui.screens.bill.AddBillScreen
import com.spendwealthy.app.ui.screens.bill.BillListScreen
import com.spendwealthy.app.ui.screens.home.components.GoalContributionDialog
import com.spendwealthy.app.ui.screens.report.ReportScreen
import com.spendwealthy.app.ui.screens.transaction.AddTransactionScreen
import com.spendwealthy.app.ui.screens.transaction.list.TransactionListScreen
import com.spendwealthy.app.ui.screens.settings.SettingsScreen
import com.spendwealthy.app.domain.model.FamilyMember
import com.spendwealthy.app.ui.screens.family.FamilyScreen
import com.spendwealthy.app.ui.screens.home.components.FamilyGovernanceCard
import com.spendwealthy.app.domain.model.CurrencyConverter
import com.spendwealthy.app.ui.screens.budget.BudgetListScreen
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

        val isBalanceHidden = state.isPrivacyMode
        var selectedGoalForContribution by remember { mutableStateOf<Goal?>(null) }
        var achievedGoalForCelebration by remember { mutableStateOf<Goal?>(null) }
        val now = remember { kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()) }
        var showMonthPicker by remember { mutableStateOf(false) }
        val selectedMonth = state.selectedMonth
        val selectedYear = state.selectedYear
        val selectedMonthName = if (selectedMonth != null && selectedYear != null) {
            val name = MONTH_NAMES.getOrNull(selectedMonth - 1) ?: "Oktober"
            if (selectedYear == now.year) name else "$name $selectedYear"
        } else {
            "Semua"
        }
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
                selectedYear = state.selectedYear,
                selectedMonth = state.selectedMonth,
                onMonthSelected = { year, month ->
                    viewModel.onPeriodSelected(year, month)
                    showMonthPicker = false
                },
                onDismiss = { showMonthPicker = false },
                allowAll = true
            )
        }

        val listState = rememberLazyListState()
        val scrollOffset by remember {
            derivedStateOf {
                if (listState.firstVisibleItemIndex == 0) {
                    listState.firstVisibleItemScrollOffset.toFloat()
                } else {
                    400f
                }
            }
        }
        val headerParallaxY = (scrollOffset * 0.35f).coerceAtMost(40f)
        val headerAlpha = (1f - (scrollOffset / 240f)).coerceIn(0.2f, 1f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
            ) {
                // 1. Top Bar: Greeting ("Good Morning, Khailena") & Month Dropdown ("August ▾")
                item {
                    ModernFintechHeader(
                        userName = state.userName.ifBlank { "Alex Pratama" },
                        selectedMonthName = selectedMonthName,
                        onMonthClick = { showMonthPicker = true },
                        onProfileClick = { rootNavigator.push(SettingsScreen()) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .graphicsLayer {
                                translationY = headerParallaxY
                                alpha = headerAlpha
                            }
                    )
                }

                // 2. Hero Total Balance Card (White, Step Matrix Chart)
                item {
                    HeroTotalBalanceCard(
                        totalBalance = state.totalBalance,
                        isBalanceHidden = isBalanceHidden,
                        onToggleBalance = { viewModel.togglePrivacyMode() },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                // 2.1 Quick Actions Row (Send, Receive, Swap, Deposit)
                item {
                    QuickActionsRow(
                        onSend = { rootNavigator.push(AddTransactionScreen(TransactionType.EXPENSE)) },
                        onReceive = { rootNavigator.push(AddTransactionScreen(TransactionType.INCOME)) },
                        onSwap = { rootNavigator.push(AddTransactionScreen(TransactionType.TRANSFER)) },
                        onDeposit = { rootNavigator.push(AddTransactionScreen(TransactionType.INCOME)) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // 3. Cashflow Card ($91,600.00, In 66% Yellow / Out 33% Gray)
                item {
                    CashflowMetricsCard(
                        totalIncome = state.totalIncome,
                        totalExpense = state.totalExpense,
                        isBalanceHidden = isBalanceHidden,
                        onViewDetails = { rootNavigator.push(ReportScreen(onBackClick = { if (rootNavigator.canPop) rootNavigator.pop() })) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                // 3.5. Proactive Budget Health Alert Card
                if (state.criticalBudgetAlerts.isNotEmpty()) {
                    item {
                        BudgetHealthAlertCard(
                            alerts = state.criticalBudgetAlerts,
                            onViewBudgets = { rootNavigator.push(BudgetListScreen()) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                    }
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

                // 4.5. Upcoming Recurring Bills Section
                if (state.upcomingBills.isNotEmpty()) {
                    item {
                        UpcomingBillsCard(
                            bills = state.upcomingBills,
                            isBalanceHidden = isBalanceHidden,
                            onViewAll = { rootNavigator.push(BillListScreen()) },
                            onAddBill = { rootNavigator.push(AddBillScreen()) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                // 4.6. Family Governance & Approvals (if any pending)
                if (state.activeConsensusRequests.isNotEmpty()) {
                    item {
                        FamilyGovernanceCard(
                            requests = state.activeConsensusRequests,
                            onApprove = { id -> viewModel.onEvent(HomeEvent.VoteConsensus(id, true)) },
                            onReject = { id -> viewModel.onEvent(HomeEvent.VoteConsensus(id, false)) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                // 4.7. Family Shared Savings Hub Card
                item {
                    FamilyHubPreviewCard(
                        familyMembers = state.familyMembers,
                        onOpenFamily = { rootNavigator.push(FamilyScreen()) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
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
        onProfileClick: () -> Unit = {},
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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Stylish Profile Avatar (Clickable to Settings)
                Box(
                    modifier = Modifier
                        .bouncyClickable(
                            pressedScale = 0.90f,
                            hapticType = HapticFeedbackType.LongPress,
                            onClick = onProfileClick
                        )
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB))
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
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
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C2D12)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "Good Morning,",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = userName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                    .bouncyClickable(
                        pressedScale = 0.93f,
                        hapticType = HapticFeedbackType.TextHandleMove,
                        onClick = onMonthClick
                    )
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
                            .bouncyClickable(
                                pressedScale = 0.85f,
                                hapticType = HapticFeedbackType.TextHandleMove,
                                onClick = onToggleBalance
                            )
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
                    Column(modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)) {
                        AnimatedContent(
                            targetState = isBalanceHidden,
                            transitionSpec = {
                                if (targetState) {
                                    (slideInVertically(animationSpec = tween(220)) { height -> -height / 3 } + fadeIn(tween(220)))
                                        .togetherWith(slideOutVertically(animationSpec = tween(180)) { height -> height / 3 } + fadeOut(tween(180)))
                                } else {
                                    (slideInVertically(animationSpec = tween(220)) { height -> height / 3 } + fadeIn(tween(220)))
                                        .togetherWith(slideOutVertically(animationSpec = tween(180)) { height -> -height / 3 } + fadeOut(tween(180)))
                                }
                            },
                            label = "HeroBalancePrivacyAnimation"
                        ) { hidden ->
                            Row(verticalAlignment = Alignment.Bottom) {
                                if (hidden) {
                                    Text(
                                        text = "••••••••••",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF111827),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                } else {
                                    val displayMain = if (totalBalance.amount > 0) totalBalance.format() else "$248,420"
                                    val displayFraction = if (totalBalance.amount > 0) "" else ".80"
                                    val balanceFontSize = when {
                                        displayMain.length > 15 -> 22.sp
                                        displayMain.length > 12 -> 25.sp
                                        displayMain.length > 9 -> 28.sp
                                        else -> 32.sp
                                    }
                                    
                                    Text(
                                        text = displayMain,
                                        fontSize = balanceFontSize,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF111827),
                                        letterSpacing = (-0.5).sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (displayFraction.isNotBlank()) {
                                        Text(
                                            text = displayFraction,
                                            fontSize = (balanceFontSize.value * 0.7f).sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF9CA3AF),
                                            modifier = Modifier.padding(bottom = 2.dp),
                                            maxLines = 1
                                        )
                                    }
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
            }
        }
    }

    /**
     * 4 Circular Quick Action Buttons matching Reference 2:
     * Send (Dark), Receive (Light), Swap (Light), Deposit (Yellow).
     */
    @Composable
    private fun QuickActionsRow(
        onSend: () -> Unit,
        onReceive: () -> Unit,
        onSwap: () -> Unit,
        onDeposit: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val haptic = LocalHapticFeedback.current

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Send Action (Expense)
            QuickActionItem(
                label = "Send",
                icon = Icons.Default.ArrowUpward,
                backgroundColor = Color(0xFF111827),
                iconColor = Color.White,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSend()
                }
            )

            // 2. Receive Action (Income)
            QuickActionItem(
                label = "Receive",
                icon = Icons.Default.ArrowDownward,
                backgroundColor = Color(0xFFF3F4F6),
                iconColor = Color(0xFF111827),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onReceive()
                }
            )

            // 3. Swap Action (Transfer)
            QuickActionItem(
                label = "Swap",
                icon = Icons.Default.SwapHoriz,
                backgroundColor = Color(0xFFF3F4F6),
                iconColor = Color(0xFF111827),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSwap()
                }
            )

            // 4. Deposit Action (Income)
            QuickActionItem(
                label = "Deposit",
                icon = Icons.Default.Add,
                backgroundColor = Color(0xFFFFDE43),
                iconColor = Color(0xFF111827),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDeposit()
                }
            )
        }
    }

    @Composable
    private fun QuickActionItem(
        label: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        backgroundColor: Color,
        iconColor: Color,
        onClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.bouncyClickable(
                pressedScale = 0.90f,
                hapticType = HapticFeedbackType.LongPress,
                onClick = onClick
            )
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF111827)
            )
        }
    }

    @Composable
    private fun StepMatrixChart(modifier: Modifier = Modifier) {
        var animateWave by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            animateWave = true
        }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val columns = listOf(1, 2, 3, 4)
            columns.forEachIndexed { colIdx, height ->
                val columnProgress by animateFloatAsState(
                    targetValue = if (animateWave) 1f else 0.25f,
                    animationSpec = tween(
                        durationMillis = 650,
                        delayMillis = colIdx * 70,
                        easing = FastOutSlowInEasing
                    ),
                    label = "MatrixColumnProgress_$colIdx"
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val activeRows = (height * columnProgress).toInt().coerceAtLeast(1)
                    for (row in 4 downTo 1) {
                        val isFilled = row <= activeRows
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
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .clickable(onClick = onViewDetails),
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
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (totalIncome.amount > 0) totalIncome.format() else "$184.200",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
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
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (totalExpense.amount > 0) totalExpense.format() else "$92.600",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
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
        val animatedProgress by animateFloatAsState(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            label = "TickProgressAnimation"
        )

        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalTicks = 24
            val activeTicks = (totalTicks * animatedProgress).toInt()
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
                    modifier = Modifier.bouncyClickable(
                        pressedScale = 0.94f,
                        hapticType = HapticFeedbackType.TextHandleMove,
                        onClick = onManageWallets
                    )
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
                // 1. User's custom wallets (Priority 1)
                items(wallets) { wallet ->
                    val brand = KnownBrand.from(wallet.name)
                    val isForeign = wallet.balance.currency != "IDR"
                    val displayBalance = if (isForeign) wallet.balance.formatWithSymbol() else wallet.balance.format()
                    val subtitle = if (isForeign) CurrencyConverter.formatEstimatedIdr(wallet.balance) else null

                    FintechAccountCard(
                        icon = Icons.Default.AccountBalanceWallet,
                        brand = brand,
                        iconBg = if (brand != null) Color(0xFFF9FAFB) else Color(0xFFF3F4F6),
                        iconTint = Color(0xFF111827),
                        title = wallet.name,
                        maskedNumber = if (isForeign) "${wallet.balance.currency} • Kurs Live" else "Account • Active",
                        balance = displayBalance,
                        secondaryBalance = subtitle,
                        isBalanceHidden = isBalanceHidden
                    )
                }

                // 2. Global Multi-Currency Discovery Cards (If user has not yet created foreign accounts)
                if (wallets.none { it.balance.currency == "EUR" }) {
                    item {
                        FintechAccountCard(
                            icon = Icons.Default.Public,
                            iconBg = Color(0xFFEFF6FF),
                            iconTint = Color(0xFF2563EB),
                            title = "Euro Account",
                            maskedNumber = "EUR • Kurs Live",
                            balance = "€3.450",
                            secondaryBalance = "≈ Rp60.375.000",
                            isBalanceHidden = isBalanceHidden
                        )
                    }
                }

                if (wallets.none { it.balance.currency == "USD" }) {
                    item {
                        FintechAccountCard(
                            icon = Icons.Default.CreditCard,
                            iconBg = Color(0xFFECFDF5),
                            iconTint = Color(0xFF059669),
                            title = "US Dollar",
                            maskedNumber = "USD • Kurs Live",
                            balance = "$2,500",
                            secondaryBalance = "≈ Rp40.625.000",
                            isBalanceHidden = isBalanceHidden
                        )
                    }
                }

                // 3. Add Wallet Action Card
                item {
                    Surface(
                        modifier = Modifier
                            .width(130.dp)
                            .height(86.dp)
                            .bouncyClickable(
                                pressedScale = 0.94f,
                                hapticType = HapticFeedbackType.TextHandleMove,
                                onClick = onAddWallet
                            ),
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
        isBalanceHidden: Boolean,
        secondaryBalance: String? = null,
        brand: KnownBrand? = null
    ) {
        Surface(
            modifier = Modifier
                .width(260.dp)
                .height(86.dp)
                .tactilePress(pressedScale = 0.96f),
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
                        if (brand != null) {
                            BrandLogo(brand = brand, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
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

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isBalanceHidden) "••••••" else balance,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    if (!isBalanceHidden && secondaryBalance != null) {
                        Text(
                            text = secondaryBalance,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun BudgetHealthAlertCard(
        alerts: List<BudgetAlertInfo>,
        onViewBudgets: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val hasOverbudget = alerts.any { it.isOverbudget }
        val topAlert = alerts.firstOrNull() ?: return
        val cardBorderColor = if (hasOverbudget) Color(0xFFF43F5E).copy(alpha = 0.45f) else Color(0xFFF59E0B).copy(alpha = 0.45f)
        val badgeBg = if (hasOverbudget) Color(0xFFF43F5E).copy(alpha = 0.14f) else Color(0xFFF59E0B).copy(alpha = 0.14f)
        val accentColor = if (hasOverbudget) Color(0xFFF43F5E) else Color(0xFFF59E0B)

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .bouncyClickable(
                    pressedScale = 0.98f,
                    hapticType = HapticFeedbackType.LongPress,
                    onClick = onViewBudgets
                ),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, cardBorderColor),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(badgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hasOverbudget) Icons.Default.Warning else Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (hasOverbudget) "Peringatan Plafon Anggaran 🚨" else "Mendekati Batas Anggaran ⚠️",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = "${alerts.size} pos anggaran membutuhkan perhatian",
                                fontSize = 11.sp,
                                color = AppColors.TextMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = if (hasOverbudget) "Kritis" else "Waspada",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Progress Bar for top alert
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = topAlert.categoryName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "${(topAlert.usagePercentage * 100).toInt()}% • ${topAlert.spent.format()} / ${topAlert.limit.format()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }

                    val progressFraction = topAlert.usagePercentage.toFloat().coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(AppColors.Outline)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progressFraction)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        if (hasOverbudget) listOf(Color(0xFFF59E0B), Color(0xFFF43F5E))
                                        else listOf(Color(0xFF10B981), Color(0xFFF59E0B))
                                    )
                                )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kelola Anggaran →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                }
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
                .bouncyClickable(
                    pressedScale = 0.97f,
                    hapticType = HapticFeedbackType.TextHandleMove,
                    onClick = onClick
                )
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
                    val brand = KnownBrand.from(title)
                    if (brand != null) {
                        BrandLogo(
                            brand = brand,
                            modifier = Modifier.size(20.dp),
                            tint = if (brand.isMonochrome) iconTint else null
                        )
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
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

    @Composable
    private fun UpcomingBillsCard(
        bills: List<BillReminder>,
        isBalanceHidden: Boolean,
        onViewAll: () -> Unit,
        onAddBill: () -> Unit,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Upcoming Bills",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFDE43).copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color(0xFFFFDE43).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "${bills.size} upcoming",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.clickable(onClick = onViewAll)
                ) {
                    Text(
                        text = "Manage",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Kelola Tagihan",
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
                    bills.take(3).forEachIndexed { index, bill ->
                        val brand = KnownBrand.from(bill.name)
                        val (vectorIcon, iconBg, iconTint) = when {
                            brand == KnownBrand.NETFLIX -> Triple(Icons.Default.Movie, Color(0xFFFEE2E2), Color(0xFFE50914))
                            brand == KnownBrand.SPOTIFY -> Triple(Icons.Default.MusicNote, Color(0xFFECFDF5), Color(0xFF1ED760))
                            brand == KnownBrand.APPLE -> Triple(Icons.Default.Cloud, Color(0xFFF3F4F6), Color(0xFF111827))
                            brand == KnownBrand.GOOGLE -> Triple(Icons.Default.Subscriptions, Color(0xFFEFF6FF), Color(0xFF4285F4))
                            brand == KnownBrand.YOUTUBE -> Triple(Icons.Default.PlayArrow, Color(0xFFFEE2E2), Color(0xFFFF0000))
                            brand == KnownBrand.AMAZON -> Triple(Icons.Default.ShoppingCart, Color(0xFFFFF7ED), Color(0xFFFF9900))
                            brand == KnownBrand.CHATGPT -> Triple(Icons.Default.SmartToy, Color(0xFFECFDF5), Color(0xFF10A37F))
                            brand == KnownBrand.DISNEY_PLUS -> Triple(Icons.Default.Movie, Color(0xFFEFF6FF), Color(0xFF0063E5))
                            brand == KnownBrand.PAYPAL -> Triple(Icons.Default.Payment, Color(0xFFEFF6FF), Color(0xFF003087))
                            brand == KnownBrand.GITHUB -> Triple(Icons.Default.Code, Color(0xFFF3F4F6), Color(0xFF111827))
                            brand == KnownBrand.MICROSOFT -> Triple(Icons.Default.Window, Color(0xFFEFF6FF), Color(0xFF00A4EF))
                            bill.name.contains("listrik", ignoreCase = true) || bill.name.contains("pln", ignoreCase = true) -> Triple(Icons.Default.Bolt, Color(0xFFFEF3C7), Color(0xFFD97706))
                            bill.name.contains("wifi", ignoreCase = true) || bill.name.contains("indihome", ignoreCase = true) || bill.name.contains("biznet", ignoreCase = true) || bill.name.contains("internet", ignoreCase = true) -> Triple(Icons.Default.Wifi, Color(0xFFEFF6FF), Color(0xFF2563EB))
                            bill.name.contains("bpjs", ignoreCase = true) || bill.name.contains("sehat", ignoreCase = true) -> Triple(Icons.Default.LocalHospital, Color(0xFFECFDF5), Color(0xFF059669))
                            bill.name.contains("sewa", ignoreCase = true) || bill.name.contains("kos", ignoreCase = true) || bill.name.contains("rumah", ignoreCase = true) -> Triple(Icons.Default.Home, Color(0xFFFFF7ED), Color(0xFFEA580C))
                            else -> Triple(Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFFF3F4F6), Color(0xFF4B5563))
                        }

                        UpcomingBillRow(
                            brand = brand,
                            icon = vectorIcon,
                            iconBg = iconBg,
                            iconTint = iconTint,
                            title = bill.name,
                            dueDateText = "Due on ${bill.dueDate}",
                            amount = if (isBalanceHidden) "•••••" else bill.amount.format(),
                            onClick = onViewAll
                        )
                        if (index < bills.take(3).size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFF3F4F6))
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun UpcomingBillRow(
        brand: KnownBrand? = null,
        icon: ImageVector,
        iconBg: Color,
        iconTint: Color,
        title: String,
        dueDateText: String,
        amount: String,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (brand != null) {
                        BrandLogo(
                            brand = brand,
                            modifier = Modifier.size(20.dp),
                            tint = if (brand.isMonochrome) iconTint else null
                        )
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dueDateText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
private fun FamilyHubPreviewCard(
    familyMembers: List<FamilyMember>,
    onOpenFamily: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onOpenFamily()
            },
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF111827),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFDE43).copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color(0xFFFFDE43),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Tabungan Keluarga",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFDE43)
                        ) {
                            Text(
                                text = "KOLABORATIF",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111827),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (familyMembers.isNotEmpty()) "${familyMembers.size} Anggota • 3 Target Bersama" else "Nabung bareng keluarga & pasangan",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = "Buka Tabungan Keluarga",
                    tint = Color(0xFFFFDE43),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}


