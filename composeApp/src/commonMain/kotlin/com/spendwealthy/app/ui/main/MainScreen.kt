package com.spendwealthy.app.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.spendwealthy.app.ui.screens.budget.BudgetListScreen
import com.spendwealthy.app.ui.screens.home.HomeScreen
import com.spendwealthy.app.ui.screens.transaction.AddTransactionScreen
import com.spendwealthy.app.ui.screens.transaction.list.TransactionListScreen
import com.spendwealthy.app.ui.screens.vault.VaultScreen
import com.spendwealthy.app.ui.theme.AppColors

/**
 * Modern, minimalist main application shell.
 * Features a refined floating dock navigation with tactile micro-interactions and quick entry FAB.
 */
class MainScreen : Screen {

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow

        TabNavigator(HomeTab) {
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    FloatingBottomDock(rootNavigator)
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(AppColors.Background)
                ) {
                    CurrentTab()
                }
            }
        }
    }

    @Composable
    private fun FloatingBottomDock(rootNavigator: Navigator) {
        val haptic = LocalHapticFeedback.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                shape = RoundedCornerShape(34.dp),
                color = AppColors.Surface.copy(alpha = 0.96f),
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DockItem(HomeTab, Icons.Default.Home, "Beranda")
                    DockItem(TransactionTab, Icons.AutoMirrored.Filled.ReceiptLong, "Transaksi")

                    // Integrated Center Quick Action (Catat Cepat)
                    CenterFab(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(AddTransactionScreen())
                        }
                    )

                    DockItem(BudgetTab, Icons.Default.AccountBalanceWallet, "Anggaran")
                    DockItem(VaultTab, Icons.Default.Lock, "Brankas")
                }
            }
        }
    }

    @Composable
    private fun RowScope.DockItem(tab: Tab, icon: ImageVector, label: String) {
        val tabNavigator = LocalTabNavigator.current
        val isSelected = tabNavigator.current == tab
        val haptic = LocalHapticFeedback.current

        val scale by animateFloatAsState(
            targetValue = if (isSelected) 1.05f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
        )

        val tintColor by animateColorAsState(
            targetValue = if (isSelected) AppColors.Primary else AppColors.TextMuted
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (!isSelected) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            tabNavigator.current = tab
                        }
                    }
                )
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier
                    .size(22.dp)
                    .scale(scale)
            )

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = tintColor,
                modifier = Modifier.padding(top = 3.dp)
            )

            // Sleek active micro-indicator dot
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AppColors.Primary else Color.Transparent)
            )
        }
    }

    @Composable
    private fun CenterFab(onClick: () -> Unit) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.88f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioHighBouncy)
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .size(50.dp)
                .scale(scale)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(AppColors.CardDark)
                .border(1.5.dp, AppColors.Accent.copy(alpha = 0.8f), CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Catat Transaksi",
                tint = AppColors.Accent,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

// Tab Definitions with Clean Labels
internal object HomeTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 0u, title = "Beranda", icon = null)
    @Composable override fun Content() = HomeScreen().Content()
}

internal object TransactionTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 1u, title = "Transaksi", icon = null)
    @Composable override fun Content() = TransactionListScreen().Content()
}

internal object BudgetTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 2u, title = "Anggaran", icon = null)
    @Composable override fun Content() = BudgetListScreen().Content()
}

internal object VaultTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 3u, title = "Brankas", icon = null)
    @Composable override fun Content() = VaultScreen().Content()
}
