package com.spendwealthy.app.ui.screens.family

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.FamilyMember
import com.spendwealthy.app.domain.model.FamilyRole
import com.spendwealthy.app.ui.theme.AppColors

class FamilyScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<FamilyViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current

        var selectedMemberForAction by remember { mutableStateOf<FamilyMember?>(null) }
        var copyNotificationVisible by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                FamilyTopBar(
                    onBackClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        navigator.pop()
                    },
                    onAddGoalClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.onOpenCreateGoalDialog()
                    }
                )
            },
            containerColor = AppColors.Background
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
            ) {
                // 1. Hero Family Total Vault Card
                item {
                    FamilyVaultHeroCard(
                        familyName = state.family?.name ?: "Keluarga Pratama",
                        totalSavings = state.totalSavingsAmount,
                        totalTarget = state.totalTargetAmount,
                        memberCount = state.family?.members?.size ?: 2,
                        onContribute = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onOpenContributeDialog()
                        },
                        onInvite = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onInviteClicked()
                        },
                        onAddGoal = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onOpenCreateGoalDialog()
                        }
                    )
                }

                // 2. Tab Switcher
                item {
                    FamilyTabSwitcher(
                        selectedIndex = state.selectedTab,
                        goalsCount = state.sharedGoals.size,
                        membersCount = state.family?.members?.size ?: 0,
                        onTabSelected = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.selectTab(it)
                        }
                    )
                }

                // 3. Tab Contents
                when (state.selectedTab) {
                    0 -> {
                        // Shared Savings Goals Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Target Tabungan Bersama",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = AppColors.TextMain
                                )
                                Text(
                                    text = "+ Target Baru",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF111827),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFDE43))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.onOpenCreateGoalDialog()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        items(state.sharedGoals, key = { it.id }) { goal ->
                            SharedGoalCard(
                                goal = goal,
                                onContributeClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.onOpenContributeDialog(goal)
                                }
                            )
                        }
                    }

                    1 -> {
                        // Family Members Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Anggota & Hak Akses",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = AppColors.TextMain
                                )
                                Text(
                                    text = "+ Undang",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF111827),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFDE43))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.onInviteClicked()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        items(state.family?.members ?: emptyList(), key = { it.id }) { member ->
                            FamilyMemberCard(
                                member = member,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    selectedMemberForAction = member
                                }
                            )
                        }
                    }

                    2 -> {
                        // Activity Timeline Tab
                        item {
                            Text(
                                text = "Riwayat Setoran & Aktivitas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AppColors.TextMain
                            )
                        }

                        items(state.activities, key = { it.id }) { activity ->
                            FamilyActivityItem(activity)
                        }
                    }
                }
            }

            // Dialogs
            if (state.showContributeDialog) {
                ContributeToGoalDialog(
                    goals = state.sharedGoals,
                    members = state.family?.members ?: emptyList(),
                    initialSelectedGoal = state.selectedGoalForContribution,
                    onDismiss = { viewModel.onDismissContributeDialog() },
                    onConfirm = { goalId, memberName, amount ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.submitContribution(goalId, memberName, amount)
                    }
                )
            }

            if (state.showCreateGoalDialog) {
                CreateSharedGoalDialog(
                    onDismiss = { viewModel.onDismissCreateGoalDialog() },
                    onConfirm = { title, targetAmount, date, category ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.createSharedGoal(title, targetAmount, date, category)
                    }
                )
            }

            if (state.showInviteDialog) {
                InviteMemberDialog(
                    familyName = state.family?.name ?: "Keluarga Pratama",
                    onDismiss = { viewModel.dismissInvite() },
                    onSimulateJoin = { name, email, role ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.simulateMemberJoin(name, email, role)
                    }
                )
            }

            selectedMemberForAction?.let { member ->
                MemberActionDialog(
                    member = member,
                    onDismiss = { selectedMemberForAction = null },
                    onRoleChange = { role ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.changeMemberRole(member.id, role)
                        selectedMemberForAction = null
                    }
                )
            }
        }
    }

    @Composable
    private fun FamilyTopBar(
        onBackClick: () -> Unit,
        onAddGoalClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBackClick),
                shape = CircleShape,
                color = AppColors.Surface,
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Tabungan Keluarga",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Menabung Bersama & Kolaboratif",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextMuted
                )
            }

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAddGoalClick),
                shape = CircleShape,
                color = AppColors.Surface,
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Target",
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun FamilyVaultHeroCard(
        familyName: String,
        totalSavings: Long,
        totalTarget: Long,
        memberCount: Int,
        onContribute: () -> Unit,
        onInvite: () -> Unit,
        onAddGoal: () -> Unit
    ) {
        val percentage = if (totalTarget > 0) ((totalSavings.toFloat() / totalTarget.toFloat()) * 100).toInt() else 0
        val formattedSavings = "Rp " + totalSavings.toString().reversed().chunked(3).joinToString(".").reversed()
        val formattedTarget = "Rp " + totalTarget.toString().reversed().chunked(3).joinToString(".").reversed()

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Row
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
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFDE43).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = Color(0xFF111827),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = familyName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = "$memberCount Anggota Terhubung",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextMuted
                            )
                        }
                    }

                    // Status Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "+14.8% bln ini",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }

                // Balance Display
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Total Tabungan Terkumpul",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted
                    )
                    Text(
                        text = formattedSavings,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.TextMain,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "dari target total $formattedTarget ($percentage% tercapai)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )
                }

                // Overall Progress Bar
                val animatedProgress by animateFloatAsState(
                    targetValue = (percentage / 100f).coerceIn(0f, 1f),
                    animationSpec = tween(600)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF3F4F6))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFDE43))
                    )
                }

                // 3 Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // + Setor Tabungan (Primary)
                    Surface(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onContribute),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFDE43)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF111827),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Setor Tabungan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }
                    }

                    // Undang Anggota
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onInvite),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF111827)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Undang",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // + Target
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onAddGoal),
                        shape = RoundedCornerShape(14.dp),
                        color = AppColors.Surface,
                        border = BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = AppColors.TextMain,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Target",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun FamilyTabSwitcher(
        selectedIndex: Int,
        goalsCount: Int,
        membersCount: Int,
        onTabSelected: (Int) -> Unit
    ) {
        val tabs = listOf("Target ($goalsCount)", "Anggota ($membersCount)", "Aktivitas")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF3F4F6)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { onTabSelected(index) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AppColors.TextMain else AppColors.TextMuted
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SharedGoalCard(
        goal: FamilySavingsGoal,
        onContributeClick: () -> Unit
    ) {
        val progress = if (goal.targetAmount > 0) {
            (goal.currentAmount.toFloat() / goal.targetAmount.toFloat()).coerceIn(0f, 1f)
        } else 0f
        val percentage = (progress * 100).toInt()

        val categoryIcon = when (goal.category) {
            "Holiday" -> Icons.Default.FlightTakeoff
            "Education" -> Icons.Default.School
            "Emergency" -> Icons.Default.Shield
            "Property" -> Icons.Default.Home
            else -> Icons.Default.Savings
        }

        val categoryColor = when (goal.category) {
            "Holiday" -> Color(0xFF3B82F6)
            "Education" -> Color(0xFF8B5CF6)
            "Emergency" -> Color(0xFF10B981)
            "Property" -> Color(0xFFF59E0B)
            else -> Color(0xFF111827)
        }

        val formattedCurrent = "Rp " + goal.currentAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        val formattedTarget = "Rp " + goal.targetAmount.toString().reversed().chunked(3).joinToString(".").reversed()

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(categoryColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = goal.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = "Target: ${goal.targetDate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFDE43).copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "$percentage%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Progress Bar
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(500)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFF3F4F6))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(categoryColor)
                    )
                }

                // Amounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Terkumpul",
                            fontSize = 11.sp,
                            color = AppColors.TextMuted
                        )
                        Text(
                            text = formattedCurrent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Target Akhir",
                            fontSize = 11.sp,
                            color = AppColors.TextMuted
                        )
                        Text(
                            text = formattedTarget,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                    }
                }

                // Contributors Breakdown
                if (goal.contributions.isNotEmpty()) {
                    HorizontalDivider(color = AppColors.Outline)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Kontribusi Anggota",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMuted
                        )

                        goal.contributions.forEach { c ->
                            val cPercent = if (goal.currentAmount > 0) ((c.amount.toFloat() / goal.currentAmount.toFloat()) * 100).toInt() else 0
                            val cFormatted = "Rp " + c.amount.toString().reversed().chunked(3).joinToString(".").reversed()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(AppColors.SurfaceRaised),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = c.memberName.take(1),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.TextMain
                                        )
                                    }
                                    Text(
                                        text = c.memberName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.TextMain
                                    )
                                }

                                Text(
                                    text = "$cFormatted ($cPercent%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.TextMuted
                                )
                            }
                        }
                    }
                }

                // Setor Button for this goal
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onContributeClick),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Setor ke Target Ini",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun FamilyMemberCard(
        member: FamilyMember,
        onClick: () -> Unit
    ) {
        val roleLabel = when (member.role) {
            FamilyRole.ADMIN -> "Kepala Keluarga / Pengelola (Admin)"
            FamilyRole.MEMBER -> "Anggota Tabungan"
            FamilyRole.VIEWER -> "Pemantau (Viewer)"
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFDE43).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.name.take(1).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = member.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = member.email,
                            fontSize = 11.sp,
                            color = AppColors.TextMuted
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (member.role == FamilyRole.ADMIN) Color(0xFFFFDE43).copy(alpha = 0.35f) else Color(0xFFF3F4F6)
                        ) {
                            Text(
                                text = roleLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    @Composable
    private fun FamilyActivityItem(activity: FamilyActivity) {
        val (icon, iconColor) = when (activity.type) {
            ActivityType.CONTRIBUTION_ADDED -> Pair(Icons.Default.Savings, Color(0xFF10B981))
            ActivityType.MEMBER_JOINED -> Pair(Icons.Default.PersonAdd, Color(0xFF3B82F6))
            ActivityType.ROLE_CHANGED -> Pair(Icons.Default.Security, Color(0xFFF59E0B))
            ActivityType.GOAL_REACHED -> Pair(Icons.Default.EmojiEvents, Color(0xFFFFDE43))
            ActivityType.TRANSACTION_ADDED -> Pair(Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFF6B7280))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (iconColor == Color(0xFFFFDE43)) Color(0xFF111827) else iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activity.memberName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "Baru saja",
                            fontSize = 10.sp,
                            color = AppColors.TextMuted
                        )
                    }

                    Text(
                        text = activity.details,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun ContributeToGoalDialog(
        goals: List<FamilySavingsGoal>,
        members: List<FamilyMember>,
        initialSelectedGoal: FamilySavingsGoal?,
        onDismiss: () -> Unit,
        onConfirm: (goalId: String, memberName: String, amount: Long) -> Unit
    ) {
        var selectedGoalId by remember { mutableStateOf(initialSelectedGoal?.id ?: goals.firstOrNull()?.id ?: "") }
        var selectedMemberName by remember { mutableStateOf(members.firstOrNull()?.name ?: "Alex Pratama") }
        var amountText by remember { mutableStateOf("1000000") }

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = AppColors.Surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Setor Tabungan Bersama",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onDismiss),
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = AppColors.TextMain,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Pilih Target
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Pilih Target Impian",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMuted
                        )

                        goals.forEach { g ->
                            val isSelected = g.id == selectedGoalId
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedGoalId = g.id },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFFFDE43).copy(alpha = 0.2f) else Color(0xFFF9FAFB),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFFFFDE43) else AppColors.Outline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = g.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = AppColors.TextMain
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF111827),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Pilih Anggota Penyetor
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Penyetor",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMuted
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            members.forEach { m ->
                                val isSelected = m.name == selectedMemberName
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedMemberName = m.name },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF111827) else Color(0xFFF3F4F6)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = m.name.split(" ").firstOrNull() ?: m.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else AppColors.TextMain
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Input Nominal
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Nominal Setoran (Rp)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMuted
                        )

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Quick Nominal Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(500_000L, 1_000_000L, 2_500_000L, 5_000_000L).forEach { pillAmount ->
                                val label = if (pillAmount >= 1_000_000L) "${pillAmount / 1_000_000L} Jt" else "${pillAmount / 1_000L} Rb"
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { amountText = pillAmount.toString() },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF3F4F6)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.TextMain
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Confirm Button
                    val amountLong = amountText.toLongOrNull() ?: 0L
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = amountLong > 0 && selectedGoalId.isNotBlank()) {
                                onConfirm(selectedGoalId, selectedMemberName, amountLong)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = if (amountLong > 0 && selectedGoalId.isNotBlank()) Color(0xFFFFDE43) else Color(0xFFE5E7EB)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Konfirmasi Setor",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (amountLong > 0 && selectedGoalId.isNotBlank()) Color(0xFF111827) else Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CreateSharedGoalDialog(
        onDismiss: () -> Unit,
        onConfirm: (title: String, targetAmount: Long, date: String, category: String) -> Unit
    ) {
        var title by remember { mutableStateOf("") }
        var targetAmountText by remember { mutableStateOf("50000000") }
        var targetDate by remember { mutableStateOf("Desember 2026") }
        var selectedCategory by remember { mutableStateOf("Holiday") }

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = AppColors.Surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Baru Keluarga",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onDismiss),
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = AppColors.TextMain,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Nama Target
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Nama Target", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Contoh: Renovasi Rumah Impian") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Kategori
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Kategori", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Pair("Holiday", "Liburan"),
                                Pair("Education", "Pendidikan"),
                                Pair("Property", "Properti"),
                                Pair("Emergency", "Darurat")
                            ).forEach { (catKey, catLabel) ->
                                val isSelected = selectedCategory == catKey
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedCategory = catKey },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF111827) else Color(0xFFF3F4F6)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = catLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else AppColors.TextMain
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Target Nominal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Target Nominal (Rp)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
                        OutlinedTextField(
                            value = targetAmountText,
                            onValueChange = { targetAmountText = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Target Waktu
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Target Waktu", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
                        OutlinedTextField(
                            value = targetDate,
                            onValueChange = { targetDate = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Submit Button
                    val targetLong = targetAmountText.toLongOrNull() ?: 0L
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = title.isNotBlank() && targetLong > 0) {
                                onConfirm(title, targetLong, targetDate, selectedCategory)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = if (title.isNotBlank() && targetLong > 0) Color(0xFFFFDE43) else Color(0xFFE5E7EB)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Simpan Target Bersama",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (title.isNotBlank() && targetLong > 0) Color(0xFF111827) else Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InviteMemberDialog(
        familyName: String,
        onDismiss: () -> Unit,
        onSimulateJoin: (name: String, email: String, role: FamilyRole) -> Unit
    ) {
        var copied by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = AppColors.Surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Undang Anggota Keluarga",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onDismiss),
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = AppColors.TextMain,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Bagikan kode unik di bawah ini ke pasangan atau anggota keluarga untuk mulai menabung bersama.",
                        fontSize = 12.sp,
                        color = AppColors.TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    // Code Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF111827)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "KODE RUANG KELUARGA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "FAM-PRATAMA-889",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFDE43),
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // Copy Link Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { copied = true },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3F4F6)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = if (copied) Color(0xFF10B981) else Color(0xFF111827),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (copied) "Tautan Tersalin ke Clipboard!" else "Salin Tautan Undangan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (copied) Color(0xFF10B981) else Color(0xFF111827)
                            )
                        }
                    }

                    HorizontalDivider(color = AppColors.Outline)

                    // Quick Simulate Button for Testing / Demo
                    Text(
                        text = "Mode Pratinjau (Simulasi Backend):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onSimulateJoin("Dewi Pratama", "dewi@pratama.id", FamilyRole.MEMBER)
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFDE43).copy(alpha = 0.25f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+ Tambah Ibu/Anak",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onSimulateJoin("Kakek Hadi", "hadi@pratama.id", FamilyRole.VIEWER)
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF3F4F6)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+ Tambah Pemantau",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextMain
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MemberActionDialog(
        member: FamilyMember,
        onDismiss: () -> Unit,
        onRoleChange: (FamilyRole) -> Unit
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = AppColors.Surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kelola Hak Akses",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onDismiss),
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = AppColors.TextMain,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Member Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFDE43).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.name.take(1).uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }

                        Column {
                            Text(
                                text = member.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = member.email,
                                fontSize = 11.sp,
                                color = AppColors.TextMuted
                            )
                        }
                    }

                    HorizontalDivider(color = AppColors.Outline)

                    Text(
                        text = "Pilih Tingkat Akses:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMuted
                    )

                    // Role 1: Admin
                    RoleOptionRow(
                        title = "Pengelola Keuangan (Admin)",
                        description = "Bisa menambah target, mengundang anggota, dan menyetor dana.",
                        icon = Icons.Default.Shield,
                        isSelected = member.role == FamilyRole.ADMIN,
                        onClick = { onRoleChange(FamilyRole.ADMIN) }
                    )

                    // Role 2: Member
                    RoleOptionRow(
                        title = "Anggota Tabungan (Member)",
                        description = "Bisa menyetor tabungan dan melihat progres target bersama.",
                        icon = Icons.Default.Person,
                        isSelected = member.role == FamilyRole.MEMBER,
                        onClick = { onRoleChange(FamilyRole.MEMBER) }
                    )

                    // Role 3: Viewer
                    RoleOptionRow(
                        title = "Pemantau Saja (Viewer)",
                        description = "Hanya dapat memantau progres tanpa akses mengubah target.",
                        icon = Icons.Default.Visibility,
                        isSelected = member.role == FamilyRole.VIEWER,
                        onClick = { onRoleChange(FamilyRole.VIEWER) }
                    )
                }
            }
        }
    }

    @Composable
    private fun RoleOptionRow(
        title: String,
        description: String,
        icon: ImageVector,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) Color(0xFFFFDE43).copy(alpha = 0.2f) else Color(0xFFF9FAFB),
            border = BorderStroke(1.dp, if (isSelected) Color(0xFFFFDE43) else AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF111827) else Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFFFFDE43) else AppColors.TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = AppColors.TextMuted,
                        lineHeight = 14.sp
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF111827),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
