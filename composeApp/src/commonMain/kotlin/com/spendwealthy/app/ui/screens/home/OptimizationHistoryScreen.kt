package com.spendwealthy.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.OptimizationLog
import com.spendwealthy.app.ui.components.AppGlassCard
import com.spendwealthy.app.ui.theme.AppColors
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
class OptimizationHistoryScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<HomeViewModel>()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Riwayat Optimalisasi", fontWeight = FontWeight.Black, color = AppColors.TextMain) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = AppColors.TextMain)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = AppColors.Background
                    )
                )
            },
            containerColor = AppColors.Background
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ImpactSummaryCard(state.optimizationLogs)
                }
                
                item {
                    Text("Catatan Efisiensi Finansial", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                }
                
                if (state.optimizationLogs.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada riwayat optimalisasi. Mulai terapkan rekomendasi finansial!", color = AppColors.TextMuted)
                        }
                    }
                } else {
                    items(state.optimizationLogs) { log ->
                        OptimizationHistoryRow(log)
                    }
                }
            }
        }
    }

    @Composable
    private fun ImpactSummaryCard(logs: List<OptimizationLog>) {
        val totalImpact = logs.sumOf { it.impactScore }
        AppGlassCard(backgroundColor = AppColors.CardDark) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("TOTAL SKOR EFISIENSI", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = AppColors.Primary)
                    Text("$totalImpact pts", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    Text("Dioptimasi melalui algoritma kekayaan privat", style = MaterialTheme.typography.bodySmall, color = AppColors.TextMuted)
                }
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AppColors.Primary, modifier = Modifier.size(44.dp))
            }
        }
    }

    @Composable
    private fun OptimizationHistoryRow(log: OptimizationLog) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = AppColors.SurfaceRaised,
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(log.title, fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text("Berhasil diterapkan", style = MaterialTheme.typography.labelSmall, color = AppColors.Success)
                }
                Text("+${log.impactScore}", fontWeight = FontWeight.Black, color = AppColors.Primary, fontSize = 16.sp)
            }
        }
    }
}
