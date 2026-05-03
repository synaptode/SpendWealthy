package com.spendwealthy.app.ui.screens.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.ui.screens.home.HomeScreen
import com.spendwealthy.app.ui.screens.settings.SettingsScreen
import com.spendwealthy.app.ui.screens.transaction.list.TransactionListScreen
import com.spendwealthy.app.ui.screens.budget.BudgetListScreen
import com.spendwealthy.app.ui.theme.AppColors

enum class MainTab(val label: String, val icon: ImageVector) {
    POCKET("My Paws", Icons.Default.Wallet),
    GOALS("Treat Hub", Icons.Default.TrackChanges),
    VAULT("The Vault", Icons.Default.Lock),
    ACTIVITIES("Pounces", Icons.AutoMirrored.Filled.ListAlt)
}

class MainScreen : Screen {
    @Composable
    override fun Content() {
        var selectedTab by remember { mutableStateOf(MainTab.GOALS) }
        val navigator = LocalNavigator.currentOrThrow
        
        Box(modifier = Modifier.fillMaxSize().background(AppColors.BackgroundSoftBlue)) {
            // Main Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    MainTab.POCKET -> HomeScreen().Content()
                    MainTab.GOALS -> BudgetListScreen().Content()
                    MainTab.VAULT -> com.spendwealthy.app.ui.screens.vault.VaultScreen().Content()
                    MainTab.ACTIVITIES -> TransactionListScreen().Content()
                }
            }

            // Bottom Navigation with Centered FAB
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                // Background Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .align(Alignment.BottomCenter),
                    color = AppColors.DeepBlueText,
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp) 
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Tabs
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                            NavItem(MainTab.POCKET, selectedTab == MainTab.POCKET) { selectedTab = MainTab.POCKET }
                            NavItem(MainTab.GOALS, selectedTab == MainTab.GOALS) { selectedTab = MainTab.GOALS }
                        }
                        
                        // Space for FAB
                        Spacer(modifier = Modifier.width(70.dp))
                        
                        // Right Tabs
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                            NavItem(MainTab.VAULT, selectedTab == MainTab.VAULT) { selectedTab = MainTab.VAULT }
                            NavItem(MainTab.ACTIVITIES, selectedTab == MainTab.ACTIVITIES) { selectedTab = MainTab.ACTIVITIES }
                        }
                    }
                }

                // Centered FAB
                FloatingActionButton(
                    onClick = { navigator.push(com.spendwealthy.app.ui.screens.transaction.AddTransactionScreen()) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(64.dp)
                        .offset(y = 4.dp),
                    containerColor = AppColors.Primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
                }
            }
        }
    }

    @Composable
    private fun NavItem(tab: MainTab, isSelected: Boolean, onClick: () -> Unit) {
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tab.label,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                )
            )
        }
    }
}
