package com.spendwealthy.app.ui.main

import androidx.compose.animation.AnimatedContent
import com.spendwealthy.app.ui.components.MotionSpecs
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.components.tactilePress

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
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
import androidx.compose.ui.graphics.Brush
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
import com.spendwealthy.app.ui.screens.family.FamilyScreen
import com.spendwealthy.app.ui.screens.home.HomeScreen
import com.spendwealthy.app.ui.screens.report.ReportScreen
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.Background),
                contentAlignment = Alignment.TopCenter
            ) {
                // Content scrolls freely across the full screen height behind the floating dock
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 680.dp)
                ) {
                    val tabNavigator = LocalTabNavigator.current
                    AnimatedContent(
                        targetState = tabNavigator.current,
                        transitionSpec = { MotionSpecs.TabTransition },
                        label = "TabTransitionAnimation"
                    ) { targetTab ->
                        targetTab.Content()
                    }
                }

                // Floating glass dock overlaid at the bottom
                FloatingBottomDock(
                    rootNavigator = rootNavigator,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    @Composable
    private fun FloatingBottomDock(
        rootNavigator: Navigator,
        modifier: Modifier = Modifier
    ) {
        val haptic = LocalHapticFeedback.current
        val tabNavigator = LocalTabNavigator.current

        Box(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color(0xFF111827).copy(alpha = 0.78f),
                shadowElevation = 16.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DockPillItem(HomeTab, Icons.Default.Home, "Home")
                    DockPillItem(FamilyTab, Icons.Default.Groups, "Keluarga")
                    
                    // Quick Action Button (+ Add) with tactile bounce
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFDE43))
                            .bouncyClickable(
                                pressedScale = 0.88f,
                                hapticType = HapticFeedbackType.LongPress
                            ) {
                                rootNavigator.push(AddTransactionScreen())
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Catat Transaksi",
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DockPillItem(BudgetTab, Icons.Default.AccountBalanceWallet, "Budget")
                    DockPillItem(ReportTab, Icons.Default.PieChart, "Stats")
                }
            }
        }
    }

    @Composable
    private fun DockPillItem(tab: Tab, icon: ImageVector, label: String) {
        val tabNavigator = LocalTabNavigator.current
        val isSelected = tabNavigator.current == tab
        val haptic = LocalHapticFeedback.current

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(if (isSelected) Color.White else Color.Transparent)
                .bouncyClickable(
                    pressedScale = 0.92f,
                    hapticType = null
                ) {
                    if (!isSelected) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        tabNavigator.current = tab
                    }
                }
                .animateContentSize(spring(stiffness = Spring.StiffnessMediumLow))
                .padding(horizontal = if (isSelected) 14.dp else 12.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) Color(0xFF111827) else Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.size(20.dp)
                )
                if (isSelected) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Tab Definitions with Clean Labels
internal object HomeTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 0u, title = "Beranda", icon = null)
    @Composable override fun Content() = HomeScreen().Content()
}

internal object FamilyTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 1u, title = "Tabungan Keluarga", icon = null)
    @Composable override fun Content() = FamilyScreen().Content()
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

internal object ReportTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 4u, title = "Statistics", icon = null)
    @Composable override fun Content() = ReportScreen().Content()
}
