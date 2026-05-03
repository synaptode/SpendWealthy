package com.spendwealthy.app.ui.screens.report

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.spendwealthy.app.ui.theme.AppColors

class ReportScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<ReportViewModel>()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ReportEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(containerColor = AppColors.Background, snackbarHost = { SnackbarHost(snackbarHostState) }, contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(AppColors.Background).windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { ReportHeader(onExport = { viewModel.exportCsv() }, loading = state.exportInProgress) }
                item { OverviewCard(state) }
                item { InsightCard(state.insight) }
                item { SectionHeader("Spending breakdown") }
                if (state.categorySummaries.isEmpty()) {
                    item { EmptyBreakdown() }
                } else {
                    items(state.categorySummaries, key = { it.categoryId ?: it.categoryName }) { summary ->
                        CategoryRow(summary)
                    }
                }
            }
        }
    }

    @Composable
    private fun ReportHeader(onExport: () -> Unit, loading: Boolean) {
        Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Reports", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                Text("Monthly income and spending", style = MaterialTheme.typography.labelMedium, color = AppColors.TextMuted)
            }
            Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.Primary).clickable(enabled = !loading, onClick = onExport), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.FileDownload, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }

    @Composable
    private fun OverviewCard(state: ReportState) {
        Surface(shape = RoundedCornerShape(24.dp), color = AppColors.Accent) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Monthly overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    Text("${(state.savingRate * 100).toInt()}%", fontWeight = FontWeight.Black, color = AppColors.Primary)
                }
                MiniBars(
                    income = state.totalIncome.amount.toFloat(),
                    expense = state.totalExpense.amount.toFloat(),
                    modifier = Modifier.fillMaxWidth().height(92.dp)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricBox(Modifier.weight(1f), "Income", state.totalIncome.format(), AppColors.Success)
                    MetricBox(Modifier.weight(1f), "Spending", state.totalExpense.format(), AppColors.Danger)
                }
            }
        }
    }

    @Composable
    private fun MiniBars(income: Float, expense: Float, modifier: Modifier) {
        Canvas(modifier) {
            val max = maxOf(income, expense, 1f)
            val barWidth = size.width / 5f
            val incomeHeight = size.height * (income / max).coerceIn(0.08f, 1f)
            val expenseHeight = size.height * (expense / max).coerceIn(0.08f, 1f)
            drawRoundRect(AppColors.Primary, Offset(size.width * 0.18f, size.height - incomeHeight), Size(barWidth, incomeHeight), CornerRadius(20.dp.toPx(), 20.dp.toPx()))
            drawRoundRect(AppColors.CardDark, Offset(size.width * 0.58f, size.height - expenseHeight), Size(barWidth, expenseHeight), CornerRadius(20.dp.toPx(), 20.dp.toPx()))
        }
    }

    @Composable
    private fun MetricBox(modifier: Modifier, label: String, value: String, color: Color) {
        Column(modifier.clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.58f)).padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMain.copy(alpha = 0.64f))
            Text(value, fontWeight = FontWeight.Black, color = color)
        }
    }

    @Composable
    private fun InsightCard(text: String) {
        Surface(shape = RoundedCornerShape(22.dp), color = AppColors.PrimaryContainer) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).clip(CircleShape).background(AppColors.CardDark), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Lightbulb, null, tint = AppColors.Accent, modifier = Modifier.size(21.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(text, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMain, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    @Composable
    private fun SectionHeader(title: String) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Icon(Icons.Default.BarChart, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
        }
    }

    @Composable
    private fun CategoryRow(summary: CategorySummary) {
        Surface(shape = RoundedCornerShape(20.dp), color = AppColors.Surface, shadowElevation = 4.dp) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(summary.categoryName, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text(summary.amount.format(), fontWeight = FontWeight.Black, color = AppColors.TextMain)
                }
                Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(AppColors.SurfaceMuted)) {
                    Box(Modifier.fillMaxWidth(summary.percentage.toFloat().coerceIn(0f, 1f)).height(10.dp).clip(CircleShape).background(AppColors.Primary))
                }
            }
        }
    }

    @Composable
    private fun EmptyBreakdown() {
        Surface(shape = RoundedCornerShape(20.dp), color = AppColors.Surface) {
            Text("No spending data for this month yet.", modifier = Modifier.padding(16.dp), color = AppColors.TextMuted, fontWeight = FontWeight.SemiBold)
        }
    }
}
