package com.spendwealthy.app.ui.screens.family

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.FamilyMember
import com.spendwealthy.app.domain.model.FamilyRole
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.components.PremiumBadge
import com.spendwealthy.app.ui.theme.AppColors

class FamilyScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<FamilyViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        var selectedMemberForAction by remember { mutableStateOf<FamilyMember?>(null) }

        Scaffold(
            topBar = {
                FamilyTopBar(onBackClick = { navigator.pop() })
            },
            containerColor = AppColors.Background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                
                FamilyOverviewCard(state.family?.name ?: "Family Space")
                
                if (state.familyGoals.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    SharedGoalMomentum(state.familyGoals)
                }

                Spacer(Modifier.height(24.dp))
                
                SpaceTabSwitcher(
                    selectedIndex = state.selectedTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
                
                Spacer(Modifier.height(24.dp))
                
                if (state.selectedTab == 0) {
                    MembersList(
                        members = state.family?.members ?: emptyList(),
                        onMemberClick = { selectedMemberForAction = it },
                        onInviteClick = { viewModel.onInviteClicked() }
                    )
                } else {
                    ActivityList(state.activities)
                }
            }

            if (state.showInviteDialog) {
                InviteMemberDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.dismissInvite() }
                )
            }

            selectedMemberForAction?.let { member ->
                MemberActionDialog(
                    member = member,
                    onDismiss = { selectedMemberForAction = null },
                    onRoleChange = { role ->
                        viewModel.changeMemberRole(member.id, role)
                        selectedMemberForAction = null
                    }
                )
            }
        }
    }

    @Composable
    private fun FamilyTopBar(onBackClick: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.TextMain)
            }
            Text("Family Coordination", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            IconButton(onClick = { }) {
                Icon(Icons.Default.Settings, null, tint = AppColors.TextMain)
            }
        }
    }

    @Composable
    private fun FamilyOverviewCard(familyName: String) {
        AppGlassCard(backgroundColor = AppColors.Primary) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(familyName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White)
                        PremiumBadge()
                    }
                    Text("Collaborative Wealth Management", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Groups, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }

    @Composable
    private fun MembersList(
        members: List<FamilyMember>,
        onMemberClick: (FamilyMember) -> Unit,
        onInviteClick: () -> Unit
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(members) { member ->
                MemberRow(member, onClick = { onMemberClick(member) })
            }
            item {
                AppButton(
                    text = "Invite New Member",
                    onClick = onInviteClick,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    icon = Icons.Default.PersonAdd
                )
            }
        }
    }

    @Composable
    private fun MemberRow(member: FamilyMember, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            color = AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(member.name.take(1), fontWeight = FontWeight.Black, color = AppColors.Primary)
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(member.name, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text(member.role.name, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                Icon(Icons.Default.ChevronRight, null, tint = AppColors.TextMuted)
            }
        }
    }

    @Composable
    private fun ActivityList(activities: List<FamilyActivity>) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(activities) { activity ->
                ActivityItem(activity)
            }
        }
    }

    @Composable
    private fun SpaceTabSwitcher(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            color = AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf("Members", "Activity").forEachIndexed { index, label ->
                    val isSelected = selectedIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AppColors.Primary else Color.Transparent)
                            .clickable { onTabSelected(index) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else AppColors.TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ActivityItem(activity: FamilyActivity) {
        val icon = when (activity.type) {
            ActivityType.MEMBER_JOINED -> Icons.Default.Person
            ActivityType.TRANSACTION_ADDED -> Icons.Default.ShoppingBag
            ActivityType.ROLE_CHANGED -> Icons.Default.Shield
            ActivityType.GOAL_REACHED -> Icons.Default.Star
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.SurfaceRaised),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
            }
            
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(activity.memberName, fontWeight = FontWeight.Bold, color = AppColors.TextMain, fontSize = 14.sp)
                    Box(Modifier.size(4.dp).clip(CircleShape).background(AppColors.TextMuted))
                    Text("Just now", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                Text(activity.details, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMain)
            }
        }
    }

    @Composable
    private fun InviteMemberDialog(viewModel: FamilyViewModel, onDismiss: () -> Unit) {
        Dialog(onDismissRequest = onDismiss) {
            AppGlassCard(backgroundColor = AppColors.Surface) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(24.dp)) {
                    Text("Invite Member", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Share this space with your family and coordinate your wealth together.", color = AppColors.TextMuted)
                    
                    Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp).background(AppColors.SurfaceRaised, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCode2, null, modifier = Modifier.size(64.dp), tint = AppColors.TextMain)
                    }
                    
                    TextButton(
                        onClick = { 
                            viewModel.simulateMemberJoin("Alex Piter", "alex@family.com")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate External Join", color = AppColors.TextMuted)
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
            AppGlassCard(backgroundColor = AppColors.Surface) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(24.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Manage Member", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.size(48.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Text(member.name.take(1), fontWeight = FontWeight.Black, color = AppColors.Primary)
                        }
                        Column {
                            Text(member.name, fontWeight = FontWeight.Bold)
                            Text(member.email, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                        }
                    }
                    
                    HorizontalDivider(color = AppColors.Outline)
                    
                    Text("Permissions & Role", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoleOption(
                            title = "Admin",
                            description = "Full access to manage wallets and members",
                            icon = Icons.Default.Shield,
                            isSelected = member.role == FamilyRole.ADMIN,
                            onClick = { onRoleChange(FamilyRole.ADMIN) }
                        )
                        RoleOption(
                            title = "Member",
                            description = "Can view and add transactions",
                            icon = Icons.Default.Person,
                            isSelected = member.role == FamilyRole.MEMBER,
                            onClick = { onRoleChange(FamilyRole.MEMBER) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun RoleOption(
        title: String,
        description: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) AppColors.Primary.copy(alpha = 0.05f) else Color.Transparent,
            border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else Color.Transparent)
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(if (isSelected) AppColors.Primary else AppColors.SurfaceRaised), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = if (isSelected) Color.White else AppColors.TextMuted, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) AppColors.Primary else AppColors.TextMain)
                    Text(description, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                if (isSelected) {
                    Icon(Icons.Default.Check, null, tint = AppColors.Primary)
                }
            }
        }
    }

    @Composable
    private fun SharedGoalMomentum(goals: List<com.spendwealthy.app.domain.model.Goal>) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Shared Goal Momentum", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = AppColors.TextMuted)
            
            goals.take(2).forEach { goal ->
                val progress = (goal.currentAmount.amount.toFloat() / goal.targetAmount.amount.toFloat()).coerceIn(0f, 1f)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = AppColors.SurfaceRaised,
                    border = BorderStroke(1.dp, AppColors.Outline)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(goal.name, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Black, color = AppColors.Primary, fontSize = 14.sp)
                        }
                        
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = AppColors.Primary,
                            trackColor = AppColors.Primary.copy(alpha = 0.1f)
                        )
                        
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Terkumpul: ${goal.currentAmount.format()}", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                            Text("Target: ${goal.targetAmount.format()}", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                        }
                    }
                }
            }
        }
    }
}
