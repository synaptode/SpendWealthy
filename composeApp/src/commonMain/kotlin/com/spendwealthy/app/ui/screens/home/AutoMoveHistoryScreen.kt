package com.spendwealthy.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.domain.model.AutoMoveLog
import com.spendwealthy.app.domain.model.AutoMoveStatus
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.ui.theme.AppColors
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AutoMoveHistoryScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        // Mock data
        val logs = listOf(
            AutoMoveLog("1", "am1", Money(500000, "IDR"), AutoMoveStatus.SUCCESS, Clock.System.now(), "Tabungan Otomatis Bulanan Berhasil"),
            AutoMoveLog("2", "am1", Money(1000000, "IDR"), AutoMoveStatus.SUCCESS, Clock.System.now(), "Alokasi Dana Darurat Terlaksana"),
            AutoMoveLog("3", "am1", Money(750000, "IDR"), AutoMoveStatus.FAILED, Clock.System.now(), "Saldo dompet sumber tidak mencukupi")
        )

        Scaffold(
            containerColor = AppColors.Background,
            topBar = {
                Row(
                    Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(AppColors.SurfaceRaised).clickable { if (navigator.canPop) navigator.pop() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = AppColors.TextMain)
                    }
                    Text("Riwayat Auto-Move", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AppColors.TextMain)
                    Box(Modifier.size(44.dp))
                }
            }
        ) { padding ->
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Lacak setiap pemindahan dana otomatis yang telah dieksekusi secara privat.", style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMuted)
                }
                
                items(logs) { log ->
                    LogItem(log)
                }
            }
        }
    }

    @Composable
    private fun LogItem(log: AutoMoveLog) {
        val isSuccess = log.status == AutoMoveStatus.SUCCESS
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isSuccess) AppColors.Success.copy(alpha = 0.15f) else AppColors.Danger.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isSuccess) AppColors.Success else AppColors.Danger,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Column(Modifier.weight(1f)) {
                    Text(if (isSuccess) "Pemindahan Sukses" else "Pemindahan Gagal", fontWeight = FontWeight.Bold, color = AppColors.TextMain)
                    Text(log.message ?: "", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(log.amount.format(), fontWeight = FontWeight.Black, color = if (isSuccess) AppColors.Primary else AppColors.TextMuted)
                    Text("Baru saja", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                }
            }
        }
    }
}
