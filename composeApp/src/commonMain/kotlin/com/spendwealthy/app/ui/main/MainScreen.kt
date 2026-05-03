package com.spendwealthy.app.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.spendwealthy.app.ui.screens.home.HomeScreen
import com.spendwealthy.app.ui.screens.transaction.list.TransactionListScreen
import com.spendwealthy.app.ui.screens.budget.BudgetListScreen
import com.spendwealthy.app.ui.screens.vault.VaultScreen
import com.spendwealthy.app.ui.screens.transaction.AddTransactionScreen
import com.spendwealthy.app.ui.theme.AppColors

class MainScreen : Screen {

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        
        TabNavigator(HomeTab) { tabNavigator ->
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    FloatingBottomNav()
                },
                floatingActionButton = {
                    ScanFab(rootNavigator)
                },
                floatingActionButtonPosition = FabPosition.Center
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding).background(AppColors.Background)) {
                    AnimatedContent(
                        targetState = tabNavigator.current,
                        transitionSpec = {
                            val currentIndex = initialState.getIndex()
                            val targetIndex = targetState.getIndex()
                            
                            if (targetIndex > currentIndex) {
                                (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                            } else {
                                (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                            }.using(SizeTransform(clip = false))
                        }
                    ) { currentTab ->
                        currentTab.Content()
                    }
                }
            }
        }
    }

    @Composable
    private fun FloatingBottomNav() {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 10.dp)
                .height(72.dp),
            shape = RoundedCornerShape(36.dp),
            color = AppColors.Surface,
            shadowElevation = 18.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(HomeTab, Icons.Default.Home, "Home")
                NavItem(TransactionTab, Icons.AutoMirrored.Filled.ReceiptLong, "Transactions")
                Spacer(modifier = Modifier.width(60.dp))
                NavItem(BudgetTab, Icons.Default.CreditCard, "Cards")
                NavItem(VaultTab, Icons.Default.Person, "Profile")
            }
        }
    }

    @Composable
    private fun RowScope.NavItem(tab: Tab, icon: ImageVector, label: String) {
        val tabNavigator = LocalTabNavigator.current
        val isSelected = tabNavigator.current == tab
        
        val scale by animateFloatAsState(
            targetValue = if (isSelected) 1.08f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
        )
        
        val color by animateColorAsState(
            targetValue = if (isSelected) AppColors.Primary else AppColors.TextMuted.copy(alpha = 0.5f)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { tabNavigator.current = tab }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp).scale(scale)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    @Composable
    private fun ScanFab(rootNavigator: cafe.adriel.voyager.navigator.Navigator) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.85f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioHighBouncy)
        )

        Box(
            modifier = Modifier
                .offset(y = 42.dp)
                .size(58.dp)
                .scale(scale)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(AppColors.Accent)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { rootNavigator.push(AddTransactionScreen()) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.GridView, 
                contentDescription = "Add transaction", 
                tint = AppColors.Primary, 
                modifier = Modifier.size(28.dp)
            )
        }
    }

    private fun Tab.getIndex(): Int {
        return when (this) {
            HomeTab -> 0
            TransactionTab -> 1
            BudgetTab -> 2
            VaultTab -> 3
            else -> 0
        }
    }
}

// Tab Definitions
internal object HomeTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 0u, title = "Home", icon = null)
    @Composable override fun Content() = HomeScreen().Content()
}

internal object TransactionTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 1u, title = "Ledger", icon = null)
    @Composable override fun Content() = TransactionListScreen().Content()
}

internal object BudgetTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 2u, title = "Budget", icon = null)
    @Composable override fun Content() = BudgetListScreen().Content()
}

internal object VaultTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 3u, title = "Vault", icon = null)
    @Composable override fun Content() = VaultScreen().Content()
}
