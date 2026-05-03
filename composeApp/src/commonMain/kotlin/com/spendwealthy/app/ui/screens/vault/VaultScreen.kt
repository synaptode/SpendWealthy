package com.spendwealthy.app.ui.screens.vault

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Document
import com.spendwealthy.app.domain.model.DocumentType
import com.spendwealthy.app.ui.screens.settings.SettingsScreen
import com.spendwealthy.app.ui.theme.AppColors

class VaultScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<VaultViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(bottom = 156.dp)
            ) {
                item {
                    ProfileHeader(
                        onSettings = { navigator.parent?.push(SettingsScreen()) },
                        onAddDocument = { navigator.parent?.push(AddDocumentScreen()) }
                    )
                }
                item { ProfileCard(documents = state.documents.size) }
                item { SecurityPanel(onClick = { navigator.parent?.push(SettingsScreen()) }) }
                item { SectionTitle("Document vault", "Add", onClick = { navigator.parent?.push(AddDocumentScreen()) }) }
                if (state.documents.isEmpty()) {
                    item { EmptyDocumentState() }
                } else {
                    items(state.documents, key = { it.id }) { document ->
                        DocumentRow(document)
                    }
                }
                item { SectionTitle("Preferences", "Open", onClick = { navigator.parent?.push(SettingsScreen()) }) }
                item { PreferenceRow(Icons.Default.Notifications, "Transaction alerts", "Push notifications for bills and card activity", true, onClick = { navigator.parent?.push(SettingsScreen()) }) }
                item { PreferenceRow(Icons.Default.Fingerprint, "Biometric lock", "Protect profile and documents", true, onClick = { navigator.parent?.push(SettingsScreen()) }) }
            }
        }
    }

    @Composable
    private fun ProfileHeader(onSettings: () -> Unit, onAddDocument: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text("Account, security, and private files", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMuted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(AppColors.Primary).clickable(onClick = onAddDocument),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised).border(1.dp, AppColors.Outline, CircleShape).clickable(onClick = onSettings),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Settings, null, tint = AppColors.TextMain, modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    @Composable
    private fun ProfileCard(documents: Int) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = AppColors.Primary
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(62.dp).clip(CircleShape).background(AppColors.Accent), contentAlignment = Alignment.Center) {
                        Text("R", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Rakha", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White)
                        Text("Premium money workspace", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.72f))
                    }
                    Icon(Icons.Default.VerifiedUser, null, tint = AppColors.Accent, modifier = Modifier.size(28.dp))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileMetric(Modifier.weight(1f), "Documents", documents.toString())
                    ProfileMetric(Modifier.weight(1f), "Security", "Strong")
                    ProfileMetric(Modifier.weight(1f), "Plan", "Plus")
                }
            }
        }
    }

    @Composable
    private fun ProfileMetric(modifier: Modifier, label: String, value: String) {
        Column(
            modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.14f)).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Black, color = Color.White)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.72f))
        }
    }

    @Composable
    private fun SecurityPanel(onClick: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.PrimaryContainer
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(AppColors.CardDark), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Security, null, tint = AppColors.Accent)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Account protection", fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text("Biometric lock and private vault are enabled", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.68f))
                }
                Icon(Icons.Default.ChevronRight, null, tint = AppColors.TextMain)
            }
        }
    }

    @Composable
    private fun SectionTitle(title: String, action: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
            Text(action, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = AppColors.Secondary, modifier = Modifier.clickable(onClick = onClick))
        }
    }

    @Composable
    private fun DocumentRow(document: Document) {
        val icon = when (document.type) {
            DocumentType.INVOICE -> Icons.Default.Receipt
            DocumentType.WARRANTY -> Icons.Default.VerifiedUser
            DocumentType.CONTRACT -> Icons.Default.EditNote
            DocumentType.OTHER -> Icons.Default.Description
        }
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 7.dp),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceMuted), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(21.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(document.name, fontWeight = FontWeight.SemiBold, color = AppColors.TextMain)
                    Text(document.type.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                Icon(Icons.Default.ChevronRight, null, tint = AppColors.TextMuted)
            }
        }
    }

    @Composable
    private fun EmptyDocumentState() {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 7.dp),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.Surface
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceMuted), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Description, null, tint = AppColors.Primary)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("No private files yet", fontWeight = FontWeight.SemiBold, color = AppColors.TextMain)
                    Text("Invoices, contracts, and warranties will appear here.", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
            }
        }
    }

    @Composable
    private fun PreferenceRow(icon: ImageVector, title: String, subtitle: String, enabled: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 7.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceMuted), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(21.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.SemiBold, color = AppColors.TextMain)
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = {},
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.Primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = AppColors.SurfaceMuted
                    )
                )
            }
        }
    }
}
