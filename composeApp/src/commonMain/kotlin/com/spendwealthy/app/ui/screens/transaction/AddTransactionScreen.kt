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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.spendwealthy.app.ui.components.CatPad
import com.spendwealthy.app.ui.theme.AppColors

class AddTransactionScreen(
    private val initialType: TransactionType = TransactionType.EXPENSE
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AddTransactionViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        var showAmountPad by remember { mutableStateOf(false) }

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
                    modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars).imePadding(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 148.dp)
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
                                title = "Category",
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
                            title = if (state.type == TransactionType.TRANSFER) "From wallet" else "Wallet",
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
                                title = "To wallet",
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

                AnimatedVisibility(
                    visible = showAmountPad,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(modifier = Modifier.navigationBarsPadding()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(36.dp).clickable { showAmountPad = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(Modifier.size(42.dp, 4.dp).clip(CircleShape).background(AppColors.TextMuted.copy(alpha = 0.28f)))
                        }
                        CatPad(
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised).border(1.dp, AppColors.Outline, CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.TextMain, modifier = Modifier.size(20.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("New Transaction", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text("Record income, expense, or transfer", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMuted)
            }
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised).border(1.dp, AppColors.Outline, CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, null, tint = AppColors.TextMain, modifier = Modifier.size(20.dp))
            }
        }
    }

    @Composable
    private fun TypeSelector(selected: TransactionType, onSelected: (TransactionType) -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            shape = RoundedCornerShape(24.dp),
            color = AppColors.Surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp).clip(RoundedCornerShape(20.dp)).background(AppColors.SurfaceMuted).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TransactionType.values().forEach { type ->
                    val isSelected = selected == type
                    val scale by animateFloatAsState(if (isSelected) 1.02f else 1f)
                    Box(
                        modifier = Modifier.weight(1f).scale(scale).clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) AppColors.Primary else Color.Transparent)
                            .clickable { onSelected(type) }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = if (isSelected) Color.White else AppColors.TextMuted,
                            fontWeight = FontWeight.Black,
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
        FormSection(title = "Amount", icon = Icons.Default.Payments) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = AppColors.Surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    ).padding(horizontal = 22.dp, vertical = 22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = amount,
                        onValueChange = { input -> onAmountChange(input.filter { it.isDigit() }) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = if (amount.isBlank()) AppColors.TextMuted else AppColors.TextMain,
                            fontSize = 36.sp,
                            lineHeight = 42.sp,
                            fontWeight = FontWeight.Black
                        ),
                        cursorBrush = SolidColor(AppColors.Primary),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (amount.isBlank()) {
                                Text("0", fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.Black, color = AppColors.TextMuted)
                            }
                            innerTextField()
                        }
                    )
                    Text("Rp", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.Primary)
                }
            }
            if (!error.isNullOrBlank()) {
                Text(error, style = MaterialTheme.typography.labelSmall, color = AppColors.Danger, modifier = Modifier.padding(start = 4.dp, top = 6.dp))
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
        FormSection(title = title, icon = icon) {
            if (values.isEmpty()) {
                EmptyPicker("No $title available")
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(values) { value ->
                        ChoiceChip(
                            text = label(value),
                            isSelected = value == selected,
                            onClick = { onSelected(value) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun NoteSection(note: String, onNoteChange: (String) -> Unit) {
        FormSection(title = "Notes", icon = Icons.AutoMirrored.Filled.Notes) {
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(22.dp),
                placeholder = { Text("What was this transaction for?") }
            )
        }
    }

    @Composable
    private fun SaveButton(state: AddTransactionState, onClick: () -> Unit) {
        val enabled = state.amount.toLongOrNull()?.let { it > 0 } == true &&
            state.selectedWallet != null &&
            (state.type != TransactionType.TRANSFER || state.selectedTargetWallet != null)

        Button(
            onClick = onClick,
            enabled = enabled && !state.isLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(58.dp),
            shape = RoundedCornerShape(29.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor = Color.White,
                disabledContainerColor = AppColors.Outline,
                disabledContentColor = AppColors.TextMuted
            )
        ) {
            Text(
                text = if (state.isLoading) "Saving..." else "Save Transaction",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }

    @Composable
    private fun FormSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(17.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
            }
            content()
        }
    }

    @Composable
    private fun ChoiceChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            color = if (isSelected) AppColors.Primary else AppColors.Surface,
            border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Outline)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp),
                color = if (isSelected) Color.White else AppColors.TextMain,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }

    @Composable
    private fun EmptyPicker(text: String) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = AppColors.Surface
        ) {
            Text(text, modifier = Modifier.padding(16.dp), color = AppColors.TextMuted, fontWeight = FontWeight.SemiBold)
        }
    }
}
