package com.spendwealthy.app.ui.screens.bill

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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.RecurrenceType
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.components.AppTextField
import com.spendwealthy.app.ui.components.BrandLogo
import com.spendwealthy.app.ui.components.KnownBrand
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

private data class BillPreset(
    val name: String,
    val suggestedAmount: Long,
    val iconVector: ImageVector,
    val recurrence: RecurrenceType = RecurrenceType.MONTHLY
)

private val BILL_PRESETS = listOf(
    BillPreset("Netflix", 186_000L, Icons.Default.Movie),
    BillPreset("Spotify Family", 86_900L, Icons.Default.MusicNote),
    BillPreset("iCloud Storage", 45_000L, Icons.Default.Cloud),
    BillPreset("YouTube Premium", 69_000L, Icons.Default.PlayCircle),
    BillPreset("ChatGPT Plus", 350_000L, Icons.Default.AutoAwesome),
    BillPreset("Google Workspace", 115_000L, Icons.Default.Cloud),
    BillPreset("Disney+ Hotstar", 65_000L, Icons.Default.Movie),
    BillPreset("Listrik PLN", 250_000L, Icons.Default.Bolt),
    BillPreset("WiFi Indihome / Biznet", 385_000L, Icons.Default.Wifi),
    BillPreset("BPJS Kesehatan", 150_000L, Icons.Default.LocalHospital),
    BillPreset("Sewa / Kos", 1_500_000L, Icons.Default.Home),
    BillPreset("Cicilan Gadget", 850_000L, Icons.Default.Smartphone)
)

class AddBillScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AddBillViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val haptic = LocalHapticFeedback.current

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    AddBillEffect.NavigateBack -> if (navigator.canPop) navigator.pop()
                    is AddBillEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
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
                        title = "Tambah Tagihan",
                        subtitle = "Pengingat otomatis jadwal recurring & kewajiban",
                        onClose = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (navigator.canPop) navigator.pop()
                        }
                    )
                }

                item {
                    PresetSection(
                        selectedName = state.name,
                        onSelectPreset = { preset ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onEvent(AddBillEvent.NameChanged(preset.name))
                            if (state.amount.isBlank() || state.amount == "0") {
                                viewModel.onEvent(AddBillEvent.AmountChanged(preset.suggestedAmount.toString()))
                            }
                            viewModel.onEvent(AddBillEvent.RecurrenceChanged(preset.recurrence))
                        }
                    )
                    Spacer(Modifier.height(14.dp))
                }

                item {
                    FormSection(title = "Nama Tagihan / Layanan", icon = Icons.AutoMirrored.Filled.ReceiptLong) {
                        AppTextField(
                            value = state.name,
                            onValueChange = { viewModel.onEvent(AddBillEvent.NameChanged(it)) },
                            label = "Nama Tagihan",
                            placeholder = "Listrik PLN, WiFi Indihome, Netflix, BPJS"
                        )
                    }
                }

                item {
                    FormSection(title = "Nominal Kewajiban", icon = Icons.Default.Payments) {
                        AppAmountInput(
                            value = state.amount,
                            onValueChange = { viewModel.onEvent(AddBillEvent.AmountChanged(it)) },
                            label = "Jumlah Tagihan (Rp)"
                        )
                    }
                }

                item {
                    FormSection(title = "Jadwal Jatuh Tempo", icon = Icons.Default.CalendarMonth) {
                        AppTextField(
                            value = state.dueDay,
                            onValueChange = { viewModel.onEvent(AddBillEvent.DueDateChanged(it)) },
                            label = "Tanggal Jatuh Tempo Tiap Bulan (1 - 31)",
                            placeholder = "Contoh: 25",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "SpendWealthy akan mengirimkan notifikasi proaktif sebelum tanggal jatuh tempo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextMuted,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "Frekuensi Pengulangan",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(RecurrenceType.entries.toList()) { type ->
                                val label = when (type) {
                                    RecurrenceType.NONE -> "Sekali Bayar"
                                    RecurrenceType.DAILY -> "Harian"
                                    RecurrenceType.WEEKLY -> "Mingguan"
                                    RecurrenceType.MONTHLY -> "Bulanan"
                                    RecurrenceType.YEARLY -> "Tahunan"
                                }
                                ChoiceChip(
                                    text = label,
                                    selected = state.recurrenceType == type,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.onEvent(AddBillEvent.RecurrenceChanged(type))
                                    }
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
                    Spacer(Modifier.height(20.dp))
                    AppButton(
                        text = "Simpan Tagihan",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onEvent(AddBillEvent.SaveClicked)
                        },
                        isLoading = state.isLoading,
                        enabled = state.name.isNotBlank() && (state.amount.toLongOrNull() ?: 0L) > 0L && !state.isLoading
                    )
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
    }

    @Composable
    private fun PresetSection(
        selectedName: String,
        onSelectPreset: (BillPreset) -> Unit
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AppColors.Accent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Preset Langganan Cepat",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(BILL_PRESETS) { preset ->
                    val isSelected = selectedName.equals(preset.name, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectPreset(preset) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) AppColors.Primary.copy(alpha = 0.2f) else AppColors.SurfaceRaised,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AppColors.Primary else AppColors.Outline
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val brand = KnownBrand.from(preset.name)
                            if (brand != null) {
                                BrandLogo(
                                    brand = brand,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (brand.isMonochrome) (if (isSelected) AppColors.Primary else AppColors.TextMain) else null
                                )
                            } else {
                                Icon(
                                    imageVector = preset.iconVector,
                                    contentDescription = null,
                                    tint = if (isSelected) AppColors.Primary else AppColors.TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = preset.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = if (isSelected) AppColors.Primary else AppColors.TextMain
                                )
                                Text(
                                    text = Money(preset.suggestedAmount, "IDR").formatWithSymbol(),
                                    style = AppTypography.labelNumeric(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) AppColors.Primary.copy(alpha = 0.85f) else AppColors.TextMuted
                                    )
                                )
                            }
                        }
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
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClose),
                shape = CircleShape,
                color = AppColors.SurfaceRaised,
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
                        .background(AppColors.Primary.copy(alpha = 0.15f)),
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
                        .padding(18.dp)
                ) {
                    content()
                }
            }
        }
    }

    @Composable
    private fun ChoiceChip(text: String, selected: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            color = if (selected) AppColors.Primary else AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, if (selected) AppColors.Primary else AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = text,
                    color = if (selected) Color.White else AppColors.TextMain,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

