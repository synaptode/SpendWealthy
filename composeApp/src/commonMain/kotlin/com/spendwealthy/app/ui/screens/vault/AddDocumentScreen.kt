package com.spendwealthy.app.ui.screens.vault

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.spendwealthy.app.domain.model.DocumentType
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.components.AppTextField
import com.spendwealthy.app.ui.components.PremiumBadge
import com.spendwealthy.app.ui.theme.AppColors

class AddDocumentScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AddDocumentViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val haptic = LocalHapticFeedback.current

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    AddDocumentEffect.NavigateBack -> if (navigator.canPop) navigator.pop()
                    is AddDocumentEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            containerColor = AppColors.Background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppColors.Background),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 680.dp)
                        .windowInsetsPadding(WindowInsets.statusBars),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                item {
                    Header(
                        title = "Tambah Dokumen",
                        subtitle = "Arsip terenkripsi aman di perangkat",
                        onClose = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (navigator.canPop) navigator.pop()
                        }
                    )
                }

                item {
                    FormSection(title = "Pindai Cerdas", icon = Icons.Default.AutoAwesome) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.onEvent(AddDocumentEvent.SmartScanClicked)
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = AppColors.Primary.copy(alpha = 0.06f),
                            border = BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.Primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF090D16),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Pindai Dokumen dengan AI",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppColors.Primary
                                        )
                                        PremiumBadge()
                                    }
                                    Text(
                                        text = "Ekstraksi otomatis teks & tanggal kedaluwarsa",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppColors.TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "PRESET DOKUMEN CEPAT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppColors.TextMuted,
                            letterSpacing = 1.sp
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(QuickDocumentPresets) { preset ->
                                val isSelected = state.name == preset.name && state.type == preset.type
                                Surface(
                                    modifier = Modifier.clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.onEvent(AddDocumentEvent.NameChanged(preset.name))
                                        viewModel.onEvent(AddDocumentEvent.TypeChanged(preset.type))
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) AppColors.Primary else AppColors.SurfaceRaised,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) AppColors.Primary else AppColors.Outline
                                    )
                                ) {
                                    Text(
                                        text = preset.name,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color(0xFF090D16) else AppColors.TextMain
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    FormSection(title = "Detail Dokumen", icon = Icons.Default.Description) {
                        AppTextField(
                            value = state.name,
                            onValueChange = { viewModel.onEvent(AddDocumentEvent.NameChanged(it)) },
                            label = "Nama Dokumen",
                            placeholder = "KTP, Polis AIA, Kartu Garansi iPad"
                        )
                        Spacer(Modifier.height(12.dp))
                        AppTextField(
                            value = state.filePath,
                            onValueChange = { viewModel.onEvent(AddDocumentEvent.FilePathChanged(it)) },
                            label = "Catatan / Nomor Referensi (Opsional)",
                            placeholder = "Nomor polis, seri nota, atau ID"
                        )
                    }
                }

                item {
                    FormSection(title = "Tipe Dokumen", icon = Icons.Default.Folder) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(DocumentType.entries.toList()) { type ->
                                val label = when (type) {
                                    DocumentType.IDENTITY -> "Identitas"
                                    DocumentType.RECEIPT -> "Struk / Nota"
                                    DocumentType.INVOICE -> "Faktur"
                                    DocumentType.WARRANTY -> "Garansi"
                                    DocumentType.CONTRACT -> "Kontrak"
                                    DocumentType.OTHER -> "Lainnya"
                                }
                                ChoiceChip(
                                    text = label,
                                    selected = state.type == type,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.onEvent(AddDocumentEvent.TypeChanged(type))
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = AppColors.SurfaceRaised,
                        border = BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Enkripsi Perangkat AES-256 GCM",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextMain
                                )
                                Text(
                                    text = "Dokumen tersimpan aman 100% offline di perangkat Anda dan tidak dibagikan ke server publik manapun.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppColors.TextMuted
                                )
                            }
                        }
                    }
                }

                item {
                    state.errorMessage?.let {
                        Text(
                            text = it,
                            color = AppColors.Danger,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    AppButton(
                        text = "Simpan ke Brankas",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onEvent(AddDocumentEvent.SaveClicked)
                        },
                        isLoading = state.isLoading,
                        enabled = state.name.isNotBlank() && !state.isLoading
                    )
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
    }

    @Composable
    private fun Header(title: String, subtitle: String, onClose: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClose),
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

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.size(42.dp))
        }
    }

    @Composable
    private fun FormSection(
        title: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AppColors.Surface,
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }

    @Composable
    private fun ChoiceChip(text: String, selected: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            color = if (selected) AppColors.Primary else AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, if (selected) AppColors.Primary else AppColors.Outline)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                color = if (selected) Color(0xFF090D16) else AppColors.TextMain,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

private data class DocumentPreset(
    val name: String,
    val type: DocumentType
)

private val QuickDocumentPresets = listOf(
    DocumentPreset("KTP Elektronik", DocumentType.IDENTITY),
    DocumentPreset("SIM (A / C)", DocumentType.IDENTITY),
    DocumentPreset("NPWP Pribadi", DocumentType.IDENTITY),
    DocumentPreset("Paspor RI", DocumentType.IDENTITY),
    DocumentPreset("Polis Asuransi", DocumentType.CONTRACT),
    DocumentPreset("Kartu Garansi", DocumentType.WARRANTY),
    DocumentPreset("Struk Pembelian", DocumentType.RECEIPT),
    DocumentPreset("Faktur Pajak", DocumentType.INVOICE)
)
