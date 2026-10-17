package com.spendwealthy.app.ui.screens.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.components.AppTextField
import com.spendwealthy.app.ui.components.QuickEntryKeypad
import com.spendwealthy.app.ui.theme.AppColors

/**
 * Modern, frictionless transaction recording screen ("3-Tap Quick Entry").
 * Opens with immediate tactile keypad access, category shortcuts, and live currency formatting.
 */
class AddTransactionScreen(
    private val initialType: TransactionType = TransactionType.EXPENSE
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AddTransactionViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        var showAmountPad by remember { mutableStateOf(true) }

        LaunchedEffect(initialType) {
            viewModel.onEvent(AddTransactionEvent.TypeChanged(initialType))
        }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    AddTransactionEffect.NavigateBack -> navigator.pop()
                    is AddTransactionEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            containerColor = AppColors.Background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(AppColors.Background)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .imePadding(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 260.dp)
                ) {
                    item { AddTransactionHeader(onClose = { navigator.pop() }) }
                    item { TypeSelector(state.type) { viewModel.onEvent(AddTransactionEvent.TypeChanged(it)) } }
                    item {
                        AmountSection(
                            amount = state.amount,
                            error = state.errorMessage,
                            onClick = { showAmountPad = true },
                            onAmountChange = { viewModel.onEvent(AddTransactionEvent.AmountChanged(it)) }
                        )
                    }
                    if (state.type != TransactionType.TRANSFER) {
                        item {
                            PickerSection(
                                title = "Kategori",
                                icon = Icons.Default.Category,
                                values = state.categories,
                                selected = state.selectedCategory,
                                label = { it.name },
                                onSelected = { viewModel.onEvent(AddTransactionEvent.CategorySelected(it)) }
                            )
                        }
                    }
                    item {
                        PickerSection(
                            title = if (state.type == TransactionType.TRANSFER) "Dari Dompet" else "Dompet",
                            icon = Icons.Default.AccountBalanceWallet,
                            values = state.wallets,
                            selected = state.selectedWallet,
                            label = { it.name },
                            onSelected = { viewModel.onEvent(AddTransactionEvent.WalletSelected(it)) }
                        )
                    }
                    if (state.type == TransactionType.TRANSFER) {
                        item {
                            PickerSection(
                                title = "Ke Dompet",
                                icon = Icons.Default.SwapHoriz,
                                values = state.wallets.filter { it.id != state.selectedWallet?.id },
                                selected = state.selectedTargetWallet,
                                label = { it.name },
                                onSelected = { viewModel.onEvent(AddTransactionEvent.TargetWalletSelected(it)) }
                            )
                        }
                    }
                    item {
                        NoteSection(
                            note = state.note,
                            onNoteChange = { viewModel.onEvent(AddTransactionEvent.NoteChanged(it)) }
                        )
                    }
                    item {
                        SaveButton(
                            state = state,
                            onClick = { viewModel.onEvent(AddTransactionEvent.SaveClicked) }
                        )
                    }
                }

                // Interactive Bottom Keypad Drawer
                AnimatedVisibility(
                    visible = showAmountPad,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .fillMaxWidth()
                            .background(Color(0xFF090D16))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clickable { showAmountPad = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                Modifier
                                    .size(38.dp, 4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f))
                            )
                        }
                        QuickEntryKeypad(
                            currentValue = state.amount,
                            onValueChange = { viewModel.onEvent(AddTransactionEvent.AmountChanged(it)) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AddTransactionHeader(onClose: () -> Unit) {
        val haptic = LocalHapticFeedback.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.Outline.copy(alpha = 0.6f), CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClose()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = AppColors.TextMain, modifier = Modifier.size(18.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Catat Transaksi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
                Text(
                    text = "Kelola arus uang masuk & keluar",
                    fontSize = 12.sp,
                    color = AppColors.TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.Outline.copy(alpha = 0.6f), CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClose()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, "Tutup", tint = AppColors.TextMain, modifier = Modifier.size(18.dp))
            }
        }
    }

    @Composable
    private fun TypeSelector(selected: TransactionType, onSelected: (TransactionType) -> Unit) {
        val haptic = LocalHapticFeedback.current

        val types = listOf(
            TransactionType.EXPENSE to "Pengeluaran",
            TransactionType.INCOME to "Pemasukan",
            TransactionType.TRANSFER to "Transfer"
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            shape = RoundedCornerShape(22.dp),
            color = AppColors.Surface,
            shadowElevation = 4.dp,
            border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(AppColors.SurfaceMuted)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                types.forEach { (type, label) ->
                    val isSelected = selected == type
                    val scale by animateFloatAsState(if (isSelected) 1.02f else 1f)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) AppColors.Primary else Color.Transparent)
                            .clickable {
                                if (!isSelected) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelected(type)
                                }
                            }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFF090D16) else AppColors.TextMuted,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AmountSection(
        amount: String,
        error: String?,
        onClick: () -> Unit,
        onAmountChange: (String) -> Unit
    ) {
        val haptic = LocalHapticFeedback.current
        val quickAmounts = listOf(
            "+10rb" to 10_000L,
            "+50rb" to 50_000L,
            "+100rb" to 100_000L,
            "+250rb" to 250_000L,
            "+500rb" to 500_000L
        )

        FormSection(title = "Nominal", icon = Icons.Default.Payments) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick
                        ),
                    shape = RoundedCornerShape(22.dp),
                    color = AppColors.Surface,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, if (error != null) AppColors.Danger else AppColors.Outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rp",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        BasicTextField(
                            value = amount,
                            onValueChange = { input -> onAmountChange(input.filter { it.isDigit() }) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = if (amount.isBlank()) AppColors.TextMuted else AppColors.TextMain,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            ),
                            cursorBrush = SolidColor(AppColors.Primary),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (amount.isBlank()) {
                                    Text("0", fontSize = 32.sp, fontWeight = FontWeight.Black, color = AppColors.TextMuted.copy(alpha = 0.4f))
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                // Quick Increment Shelf
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickAmounts) { (label, increment) ->
                        Surface(
                            modifier = Modifier.clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val currentVal = amount.toLongOrNull() ?: 0L
                                onAmountChange((currentVal + increment).toString())
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = AppColors.SurfaceRaised,
                            border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        }
                    }
                }
            }
            if (!error.isNullOrBlank()) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.Danger,
                    modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                )
            }
        }
    }

    @Composable
    private fun <T> PickerSection(
        title: String,
        icon: ImageVector,
        values: List<T>,
        selected: T?,
        label: (T) -> String,
        onSelected: (T) -> Unit
    ) {
        val haptic = LocalHapticFeedback.current

        FormSection(title = title, icon = icon) {
            if (values.isEmpty()) {
                EmptyPicker("Belum ada data $title")
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(values) { value ->
                        ChoiceChip(
                            text = label(value),
                            isSelected = value == selected,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelected(value)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun NoteSection(note: String, onNoteChange: (String) -> Unit) {
        FormSection(title = "Catatan", icon = Icons.AutoMirrored.Filled.Notes) {
            AppTextField(
                value = note,
                onValueChange = onNoteChange,
                label = "Keterangan",
                placeholder = "Misal: Makan siang, Kopi santai, Tagihan wifi",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun SaveButton(state: AddTransactionState, onClick: () -> Unit) {
        val haptic = LocalHapticFeedback.current
        val enabled = state.amount.toLongOrNull()?.let { it > 0 } == true &&
            state.selectedWallet != null &&
            (state.type != TransactionType.TRANSFER || state.selectedTargetWallet != null)

        AppButton(
            text = if (state.isLoading) "Menyimpan..." else "Simpan Transaksi",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            enabled = enabled && !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }

    @Composable
    private fun FormSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
            }
            content()
        }
    }

    @Composable
    private fun ChoiceChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) AppColors.Primary else AppColors.Surface,
            border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Outline.copy(alpha = 0.6f))
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = if (isSelected) Color(0xFF090D16) else AppColors.TextMain,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }

    @Composable
    private fun EmptyPicker(text: String) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = AppColors.Surface,
            border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.5f))
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(14.dp),
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
