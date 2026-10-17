package com.spendwealthy.app.ui.screens.insight

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.spendwealthy.app.core.localization.LocalStrings
import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.model.FinancialBadge
import com.spendwealthy.app.domain.model.FinancialStreak
import com.spendwealthy.app.domain.model.ScoreBoosterMission
import com.spendwealthy.app.domain.service.ScoreBreakdown
import com.spendwealthy.app.domain.service.WealthCardFormatter
import com.spendwealthy.app.domain.service.WealthScore
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.screens.health.WealthScoreViewModel
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class PrideStatusScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()
        val strings = LocalStrings.current
        val haptic = LocalHapticFeedback.current
        val coroutineScope = rememberCoroutineScope()

        val viewModel = koinScreenModel<WealthScoreViewModel>()
        val state by viewModel.uiState.collectAsState()
        val fileShareGateway = koinInject<FileShareGateway>()

        var isSharing by remember { mutableStateOf(false) }

        val onShareCard = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            coroutineScope.launch {
                isSharing = true
                try {
                    val summaryText = WealthCardFormatter.formatExecutiveCard(
                        score = state.score,
                        streak = state.streak,
                        missions = state.missions
                    )
                    fileShareGateway.shareTextFile(
                        fileName = "spendwealthy-sovereign-card.txt",
                        mimeType = "text/plain",
                        content = summaryText
                    )
                } finally {
                    isSharing = false
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (navigator.canPop) navigator.pop() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AppColors.Surface)
                            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back,
                            tint = AppColors.TextMain,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sovereign Wealth Lab",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = strings.scoreSubtitle,
                            fontSize = 11.sp,
                            color = AppColors.TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { onShareCard() },
                        enabled = !isSharing,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AppColors.Surface)
                            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Bagikan",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Live Health Speedometer / Gauge
                    WealthHealthGauge(
                        score = state.score.score,
                        label = state.score.label,
                        delta = state.score.delta
                    )

                    // Financial Streak & Consistency Banner
                    FinancialStreakCard(streak = state.streak)

                    // 5 Pillars Breakdown
                    PillarsCard(breakdown = state.score.breakdown)

                    // Dynamic Executive Insights
                    DynamicInsightsSection(
                        score = state.score,
                        streak = state.streak
                    )

                    // Missions & Badges Preview
                    MissionsAndBadgesSummary(
                        missions = state.missions,
                        badges = state.badges
                    )

                    // Share Wealth Card Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .bouncyClickable { onShareCard() },
                        shape = RoundedCornerShape(16.dp),
                        color = AppColors.Primary,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Bagikan Kartu Ketahanan Finansial",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    @Composable
    private fun WealthHealthGauge(score: Int, label: String, delta: Int) {
        val animationProgress = remember { Animatable(0f) }
        LaunchedEffect(score) {
            animationProgress.animateTo(
                targetValue = (score.toFloat() / 850f).coerceIn(0f, 1f),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                // Background Track
                drawArc(
                    color = Color(0xFF1E293B),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                )

                // Progress Gauge
                drawArc(
                    brush = Brush.sweepGradient(
                        0f to Color(0xFF10B981),
                        0.5f to Color(0xFF38BDF8),
                        1f to Color(0xFFDEFF37)
                    ),
                    startAngle = 135f,
                    sweepAngle = 270f * animationProgress.value,
                    useCenter = false,
                    style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(animationProgress.value * 850).toInt()}",
                    style = AppTypography.displayNumeric(
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextMain
                    )
                )
                Text(
                    text = "Skor Ketahanan / 850",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (delta != 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (delta > 0) Color(0xFF10B981).copy(alpha = 0.15f) else AppColors.Error.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (delta > 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if (delta > 0) Color(0xFF10B981) else AppColors.Error,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    text = if (delta > 0) "+$delta" else "$delta",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (delta > 0) Color(0xFF10B981) else AppColors.Error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun FinancialStreakCard(streak: FinancialStreak) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.35f)),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔥", fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${streak.currentStreakDays} Hari Konsisten",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextMain
                        )
                        Text(
                            text = "Rekor: ${streak.longestStreakDays}h",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF59E0B)
                        )
                    }
                    Text(
                        text = streak.message,
                        fontSize = 11.sp,
                        color = AppColors.TextMuted,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun PillarsCard(breakdown: ScoreBreakdown) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "5 Pilar Kesehatan Finansial",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = "Bobot 650 Pts",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted
                    )
                }

                PillarProgressRow(
                    emoji = "🛡️",
                    name = "Ketahanan Darurat",
                    score = breakdown.resilience,
                    maxScore = 150,
                    color = Color(0xFF10B981)
                )
                PillarProgressRow(
                    emoji = "🚀",
                    name = "Momentum Tabungan",
                    score = breakdown.momentum,
                    maxScore = 150,
                    color = Color(0xFF38BDF8)
                )
                PillarProgressRow(
                    emoji = "🎯",
                    name = "Disiplin Anggaran",
                    score = breakdown.adherence,
                    maxScore = 150,
                    color = Color(0xFFDEFF37)
                )
                PillarProgressRow(
                    emoji = "💧",
                    name = "Likuiditas Kas",
                    score = breakdown.liquidity,
                    maxScore = 100,
                    color = Color(0xFFA78BFA)
                )
                PillarProgressRow(
                    emoji = "🏛️",
                    name = "Perencanaan Masa Depan",
                    score = breakdown.legacy,
                    maxScore = 100,
                    color = Color(0xFFF472B6)
                )
            }
        }
    }

    @Composable
    private fun PillarProgressRow(
        emoji: String,
        name: String,
        score: Int,
        maxScore: Int,
        color: Color
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = emoji, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMain
                    )
                }
                Text(
                    text = "$score / $maxScore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val progress = (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .background(color)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFF1E293B))
                )
            }
        }
    }

    @Composable
    private fun DynamicInsightsSection(score: WealthScore, streak: FinancialStreak) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Analisis Eksekutif Mandiri",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            val resilienceMsg = if (score.breakdown.resilience >= 100) {
                "Cadangan likuiditas Anda kokoh, mampu menopang kebutuhan rumah tangga dengan aman tanpa kecemasan."
            } else {
                "Tingkatkan alokasi pos tabungan darurat hingga mencapai minimal 3 bulan pengeluaran operasional."
            }
            InsightCard(
                title = if (score.breakdown.resilience >= 100) "Bantalan Darurat Tangguh ✦" else "Peluang Penguatan Bantalan Kas",
                message = resilienceMsg,
                icon = Icons.Default.Shield,
                color = if (score.breakdown.resilience >= 100) AppColors.Primary else Color(0xFFF59E0B)
            )

            val adherenceMsg = if (score.breakdown.adherence >= 100) {
                "Pengeluaran operasional terkendali prima di bawah batas plafon bulanan Anda."
            } else {
                "Beberapa pos belanja sekunder mendekati limit. Pertahankan porsi hemat untuk sisa bulan ini."
            }
            InsightCard(
                title = if (score.breakdown.adherence >= 100) "Disiplin Anggaran Konsisten" else "Evaluasi Pos Belanja Sekunder",
                message = adherenceMsg,
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                color = AppColors.Accent
            )

            val streakMsg = if (streak.currentStreakDays >= 7) {
                "Kebiasaan mencatat transaksi secara real-time memberikan transparansi finansial 100% tanpa celah."
            } else {
                "Catat setiap pengeluaran sekecil apapun setiap hari untuk membangun rekor konsistensi finansial."
            }
            InsightCard(
                title = "Momentum & Kebiasaan Finansial",
                message = streakMsg,
                icon = Icons.Default.Savings,
                color = Color(0xFF38BDF8)
            )
        }
    }

    @Composable
    private fun MissionsAndBadgesSummary(
        missions: List<ScoreBoosterMission>,
        badges: List<FinancialBadge>
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Misi Skor & Lencana",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = "${missions.count { it.isCompleted }}/${missions.size} Misi • ${badges.count { it.isUnlocked }}/${badges.size} Lencana",
                        fontSize = 11.sp,
                        color = AppColors.TextMuted
                    )
                }

                // Show up to 2 active missions
                missions.take(2).forEach { mission ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (mission.isCompleted) Icons.Default.CheckCircle else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (mission.isCompleted) Color(0xFF10B981) else Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mission.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.TextMain
                            )
                            Text(
                                text = mission.description,
                                fontSize = 10.sp,
                                color = AppColors.TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AppColors.Primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "+${mission.potentialPoints} pts",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InsightCard(title: String, message: String, icon: ImageVector, color: Color) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f))
                        .border(1.dp, color.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                    Text(
                        text = message,
                        fontSize = 11.sp,
                        color = AppColors.TextMuted,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
