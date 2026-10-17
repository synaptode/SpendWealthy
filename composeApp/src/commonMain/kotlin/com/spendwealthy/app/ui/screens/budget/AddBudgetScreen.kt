package com.spendwealthy.app.ui.screens.budget

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.ui.components.AddCategoryModal
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

class AddBudgetScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AddBudgetViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val haptic = LocalHapticFeedback.current
        var showAddCategoryModal by remember { mutableStateOf(false) }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    AddBudgetEffect.NavigateBack -> if (navigator.canPop) navigator.pop()
                    is AddBudgetEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            containerColor = AppColors.Background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppColors.Background)
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                item {
                    Header(
                        title = "Atur Anggaran",
                        subtitle = "Batas pengeluaran per kategori",
                        onClose = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (navigator.canPop) navigator.pop()
                        }
                    )
                }

                item {
                    FormSection(title = "Pilih Kategori", icon = Icons.Default.Category) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.categories) { category ->
                                CategoryChip(
                                    category = category,
                                    selected = state.selectedCategory?.id == category.id,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.onEvent(AddBudgetEvent.CategorySelected(category))
                                    }
                                )
                            }
                            item {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            showAddCategoryModal = true
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = AppColors.Surface,
                                    border = BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = AppColors.Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Kategori Baru",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = AppColors.Primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    FormSection(title = "Batas Pengeluaran Bulanan", icon = Icons.Default.Payments) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppAmountInput(
                                value = state.amount,
                                onValueChange = { viewModel.onEvent(AddBudgetEvent.AmountChanged(it)) },
                                label = "Limit Maksimal (Rp)",
                                showQuickPills = false
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(QuickLimitPresets) { (amountStr, label) ->
                                    val isSelected = state.amount == amountStr
                                    Surface(
                                        modifier = Modifier.clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.onEvent(AddBudgetEvent.AmountChanged(amountStr))
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) AppColors.Primary.copy(alpha = 0.2f) else AppColors.Background,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) AppColors.Primary else AppColors.Outline.copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Text(
                                            text = label,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            style = AppTypography.labelNumeric(
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                color = if (isSelected) AppColors.Primary else AppColors.TextMuted
                                            )
                                        )
                                    }
                                }
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
                        color = AppColors.Primary.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Rekomendasi Alokasi 50/30/20",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextMain
                                )
                                Text(
                                    text = "Batasi pengeluaran kategori ini agar rasio tabungan bulanan tetap optimal di atas 20%.",
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
                        text = "Simpan Anggaran",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onEvent(AddBudgetEvent.SaveClicked)
                        },
                        isLoading = state.isLoading,
                        enabled = (state.amount.toLongOrNull() ?: 0L) > 0L && !state.isLoading
                    )
                    Spacer(Modifier.height(40.dp))
                }
            }
        }

        if (showAddCategoryModal) {
            AddCategoryModal(
                onDismiss = { showAddCategoryModal = false },
                onCategoryCreated = { newCategory ->
                    viewModel.onEvent(AddBudgetEvent.AddCustomCategory(newCategory))
                    showAddCategoryModal = false
                }
            )
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextMuted
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
    private fun CategoryChip(category: Category, selected: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            color = if (selected) AppColors.Primary else AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, if (selected) AppColors.Primary else AppColors.Outline)
        ) {
            Text(
                text = category.name,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                color = if (selected) Color(0xFF090D16) else AppColors.TextMain,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

private val QuickLimitPresets = listOf(
    "500000" to "Rp 500rb",
    "1000000" to "Rp 1 jt",
    "2000000" to "Rp 2 jt",
    "3500000" to "Rp 3,5 jt",
    "5000000" to "Rp 5 jt",
    "10000000" to "Rp 10 jt"
)
