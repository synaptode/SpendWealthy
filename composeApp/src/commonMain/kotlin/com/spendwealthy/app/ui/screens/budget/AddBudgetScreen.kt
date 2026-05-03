package com.spendwealthy.app.ui.screens.budget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.ui.components.AppAmountInput
import com.spendwealthy.app.ui.components.AppButton
import com.spendwealthy.app.ui.theme.AppColors

class AddBudgetScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AddBudgetViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    AddBudgetEffect.NavigateBack -> navigator.pop()
                    is AddBudgetEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(containerColor = AppColors.Background, snackbarHost = { SnackbarHost(snackbarHostState) }, contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(AppColors.Background).windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                item { Header(onClose = { navigator.pop() }) }
                item {
                    FormSection("Category", Icons.Default.Category) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(state.categories) { category ->
                                CategoryChip(category, state.selectedCategory?.id == category.id) {
                                    viewModel.onEvent(AddBudgetEvent.CategorySelected(category))
                                }
                            }
                        }
                    }
                }
                item {
                    FormSection("Monthly limit", Icons.Default.Payments) {
                        AppAmountInput(
                            value = state.amount,
                            onValueChange = { viewModel.onEvent(AddBudgetEvent.AmountChanged(it)) },
                            label = "Limit"
                        )
                    }
                }
                item {
                    state.errorMessage?.let {
                        Text(it, color = AppColors.Danger, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                    }
                    AppButton(
                        text = "Save Budget",
                        onClick = { viewModel.onEvent(AddBudgetEvent.SaveClicked) },
                        isLoading = state.isLoading,
                        enabled = (state.amount.toLongOrNull() ?: 0L) > 0L && !state.isLoading
                    )
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }

    @Composable
    private fun Header(onClose: () -> Unit) {
        Row(Modifier.fillMaxWidth().padding(bottom = 18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            CircleButton(Icons.AutoMirrored.Filled.ArrowBack, onClose)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("New Budget", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text("Set monthly spending controls", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMuted)
            }
            CircleButton(Icons.Default.Close, onClose)
        }
    }

    @Composable
    private fun FormSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable () -> Unit) {
        Column(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(17.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
            }
            Surface(shape = RoundedCornerShape(22.dp), color = AppColors.Surface, shadowElevation = 6.dp) {
                Box(Modifier.fillMaxWidth().padding(16.dp)) { content() }
            }
        }
    }

    @Composable
    private fun CategoryChip(category: Category, selected: Boolean, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            color = if (selected) AppColors.Primary else AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, if (selected) AppColors.Primary else AppColors.Outline)
        ) {
            Text(category.name, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = if (selected) Color.White else AppColors.TextMain, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun CircleButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = AppColors.TextMain, modifier = Modifier.size(20.dp))
        }
    }
}
