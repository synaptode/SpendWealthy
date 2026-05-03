package com.spendwealthy.app.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.SubscriptionPlan
import com.spendwealthy.app.ui.components.*
import com.spendwealthy.app.ui.theme.AppColors

class SettingsScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().background(AppColors.BackgroundSoftBlue)) {
            // Grid Background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSize = 32.dp.toPx()
                val color = AppColors.Primary.copy(alpha = 0.1f)
                for (x in 0..size.width.toInt() step gridSize.toInt()) {
                    drawLine(color, androidx.compose.ui.geometry.Offset(x.toFloat(), 0f), androidx.compose.ui.geometry.Offset(x.toFloat(), size.height))
                }
                for (y in 0..size.height.toInt() step gridSize.toInt()) {
                    drawLine(color, androidx.compose.ui.geometry.Offset(0f, y.toFloat()), androidx.compose.ui.geometry.Offset(size.width, y.toFloat()))
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                AppTopBar(
                    title = "Settings",
                    subtitle = "Manage account, security, and exports.",
                    avatarId = state.avatarId,
                    actions = {
                        IconButton(
                            onClick = { navigator.pop() },
                            modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                )

                // Main Content
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        // Profile Section
                        ProfileOverview(state.userName, state.avatarId)

                        // Section: Family & Data
                        SettingSection(title = "Data & Export", icon = Icons.Default.Sync) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                state.exportStatus?.let { status ->
                                    CatBubble(text = status, modifier = Modifier.fillMaxWidth())
                                }

                                AppCard(hasCatEars = true) {
                                    Column {
                                        SettingsActionItem(
                                            title = "Sync with Family",
                                            subtitle = "Share your workspace data.",
                                            icon = Icons.Default.Groups,
                                            onClick = { viewModel.exportFamilyData() }
                                        )
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AppColors.BackgroundSoftBlue)
                                        SettingsActionItem(
                                            title = "Export to CSV",
                                            subtitle = "Download a spreadsheet report.",
                                            icon = Icons.Default.FileDownload,
                                            onClick = { viewModel.exportFamilyData() }
                                        )
                                    }
                                }
                            }
                        }

                        // Section: Preferences
                        SettingSection(title = "Security & Alerts 🔔", icon = Icons.Default.Security) {
                            AppCard(hasCatEars = true) {
                                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                    BouncySwitchItem(
                                        title = "Biometric Lock",
                                        subtitle = "Secure with FaceID/TouchID",
                                        checked = state.biometricEnabled,
                                        onCheckedChange = viewModel::setBiometricEnabled,
                                        icon = Icons.Default.Fingerprint
                                    )
                                    HorizontalDivider(color = AppColors.BackgroundSoftBlue)
                                    BouncySwitchItem(
                                        title = "Bill Reminders",
                                        subtitle = "Notify before due dates",
                                        checked = state.notificationsEnabled,
                                        onCheckedChange = viewModel::setNotificationsEnabled,
                                        icon = Icons.Default.Notifications
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun ProfileOverview(name: String, avatarId: Int) {
        val avatar = name.take(1).ifBlank { "U" }.uppercase()
        
        val infiniteTransition = rememberInfiniteTransition()
        val wiggle by infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        AppCard(
            backgroundColor = AppColors.BackgroundSoftBlue,
            hasCatEars = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .graphicsLayer { rotationZ = wiggle },
                    contentAlignment = Alignment.Center
                ) {
                    Text(avatar, fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = AppColors.DeepBlueText
                    )
                    Text(
                        text = "Premium member",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.Primary,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }

    @Composable
    private fun SettingSection(
        title: String,
        icon: ImageVector,
        content: @Composable () -> Unit
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.1f))) {
                    Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(16.dp).align(Alignment.Center))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.DeepBlueText)
            }
            content()
        }
    }

    @Composable
    private fun BouncySwitchItem(
        title: String,
        subtitle: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        icon: ImageVector
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(icon, null, tint = AppColors.TextMuted, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Black, color = AppColors.DeepBlueText)
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted, fontWeight = FontWeight.Bold)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppColors.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = AppColors.BackgroundSoftBlue
                )
            )
        }
    }

    @Composable
    private fun SettingsActionItem(
        title: String,
        subtitle: String,
        icon: ImageVector,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.BackgroundSoftBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Black, color = AppColors.DeepBlueText)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.ChevronRight, null, tint = AppColors.TextMuted)
        }
    }
}
