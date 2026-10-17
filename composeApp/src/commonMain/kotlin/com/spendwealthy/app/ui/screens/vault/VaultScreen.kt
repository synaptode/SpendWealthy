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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Badge
import com.spendwealthy.app.domain.model.Document
import com.spendwealthy.app.domain.model.DocumentType
import com.spendwealthy.app.ui.screens.premium.PaywallScreen
import com.spendwealthy.app.ui.screens.settings.SettingsScreen
import com.spendwealthy.app.ui.theme.AppColors

class VaultScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<VaultViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val rootNavigator = navigator.parent ?: navigator
        val haptic = LocalHapticFeedback.current

        Box(
            modifier = Modifier.fillMaxSize().background(AppColors.Background),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(bottom = 140.dp)
            ) {
                item {
                    VaultHeader(
                        onSettings = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(SettingsScreen())
                        },
                        onAddDocument = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(AddDocumentScreen())
                        }
                    )
                }

                item {
                    ProfileSecurityCard(
                        documentsCount = state.documents.size,
                        plan = state.currentPlan,
                        securityScore = state.securityScore,
                        onUpgrade = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(PaywallScreen())
                        }
                    )
                }

                if (state.expiringSoon.isNotEmpty()) {
                    item {
                        SectionTitle(
                            title = "Perlu Perhatian Segera",
                            action = "${state.expiringSoon.size} dokumen"
                        )
                    }
                    items(state.expiringSoon, key = { "warn-${it.id}" }) { doc ->
                        DocumentVaultCard(
                            document = doc,
                            isWarning = true,
                            onDelete = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.deleteDocument(doc.id)
                            }
                        )
                    }
                }

                item {
                    SectionTitle(
                        title = "Arsip & Dokumen Terenkripsi",
                        action = "+ Tambah",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            rootNavigator.push(AddDocumentScreen())
                        }
                    )
                }

                if (state.documents.isEmpty()) {
                    item { EmptyVaultState() }
                } else {
                    items(state.documents, key = { it.id }) { document ->
                        DocumentVaultCard(
                            document = document,
                            isWarning = false,
                            onDelete = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.deleteDocument(document.id)
                            }
                        )
                    }
                }

                if (state.badges.isNotEmpty()) {
                    item {
                        SectionTitle(
                            title = "Pencapaian Finansial",
                            action = "${state.badges.size} lencana"
                        )
                    }
                    item {
                        BadgeGallery(state.badges)
                    }
                }
            }
        }
    }

    @Composable
    private fun VaultHeader(onSettings: () -> Unit, onAddDocument: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onSettings),
                shape = CircleShape,
                color = AppColors.Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan",
                        tint = AppColors.TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Brankas Finansial",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Enkripsi AES-256 lokal",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.Primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAddDocument),
                shape = CircleShape,
                color = AppColors.Primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Dokumen",
                        tint = Color(0xFF090D16),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun ProfileSecurityCard(
        documentsCount: Int,
        plan: String,
        securityScore: Int,
        onUpgrade: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            color = AppColors.SurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Sovereign Vault Shield",
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AppColors.TextMain
                                )
                                Text(
                                    text = "Enkripsi AES-256 GCM • Tier $plan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppColors.TextMuted
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AppColors.Primary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Skor: $securityScore%",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = AppColors.Primary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatPill("Dokumen", "$documentsCount arsip")
                        StatPill("Protokol", "E2E Enkripsi")
                        StatPill("Status Keamanan", if (securityScore >= 70) "Sangat Kuat" else "Perlu Tinjauan")
                    }

                    if (plan.equals("Free", ignoreCase = true)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(onClick = onUpgrade),
                            shape = RoundedCornerShape(14.dp),
                            color = AppColors.Primary
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF090D16),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Upgrade ke SpendWealthy Sovereign",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF090D16)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun StatPill(label: String, value: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextMain
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextMuted
            )
        }
    }

    @Composable
    private fun SectionTitle(
        title: String,
        action: String,
        onClick: (() -> Unit)? = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextMain
            )
            Text(
                text = action,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (onClick != null) AppColors.Primary else AppColors.TextMuted,
                modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
        }
    }

    @Composable
    private fun DocumentVaultCard(
        document: Document,
        isWarning: Boolean,
        onDelete: () -> Unit
    ) {
        val icon = when (document.type) {
            DocumentType.IDENTITY -> Icons.Default.VerifiedUser
            DocumentType.RECEIPT -> Icons.Default.Receipt
            else -> Icons.Default.Description
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 5.dp),
            shape = RoundedCornerShape(18.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isWarning) AppColors.Danger.copy(alpha = 0.4f) else AppColors.Outline
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isWarning) AppColors.Danger.copy(alpha = 0.12f)
                            else AppColors.Primary.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWarning) Icons.Default.Warning else icon,
                        contentDescription = null,
                        tint = if (isWarning) AppColors.Danger else AppColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = document.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = "${document.type.name} • ${document.expiryDate?.let { "Kedaluwarsa: $it" } ?: "Permanen"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isWarning) AppColors.Danger else AppColors.TextMuted
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDelete),
                    shape = CircleShape,
                    color = AppColors.SurfaceRaised
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus Dokumen",
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun EmptyVaultState() {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AppColors.SurfaceRaised),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Brankas Masih Kosong",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Simpan KTP, SIM, polis asuransi, atau kartu garansi secara aman di penyimpanan offline terenkripsi.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun BadgeGallery(badges: List<Badge>) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(badges, key = { it.id }) { badge ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.Surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(badge.iconEmoji, fontSize = 20.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = badge.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = badge.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
