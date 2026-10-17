package com.spendwealthy.app.ui.screens.health

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.FinancialBadge
import com.spendwealthy.app.domain.model.ScoreBoosterMission
import com.spendwealthy.app.domain.service.ScoreBreakdown
import com.spendwealthy.app.domain.service.WealthScore
import com.spendwealthy.app.ui.components.bouncyClickable
import com.spendwealthy.app.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WealthScoreDetailBottomSheet(
    viewModel: WealthScoreViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .animateContentSize()
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AppColors.Primary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = AppColors.PrimaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    val strings = com.spendwealthy.app.core.localization.LocalStrings.current
                    Text(
                        text = strings.scoreTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain
                    )
                }

                val strings = com.spendwealthy.app.core.localization.LocalStrings.current
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        tint = AppColors.TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Banner & Speedometer
            ScoreGaugeHeader(score = state.score)

            Spacer(modifier = Modifier.height(18.dp))

            // Tab Selector (5 Pilar, Misi, Lencana)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.SurfaceRaised, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HealthTab.entries.forEach { tab ->
                    val isSelected = state.selectedTab == tab
                    val label = when (tab) {
                        HealthTab.BREAKDOWN -> "5 Pilar"
                        HealthTab.MISSIONS -> "Misi Skor"
                        HealthTab.BADGES -> "Lencana"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) AppColors.Surface else Color.Transparent)
                            .clickable { viewModel.onTabSelected(tab) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AppColors.TextMain else AppColors.TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (state.selectedTab) {
                HealthTab.BREAKDOWN -> PillarsBreakdownView(breakdown = state.score.breakdown)
                HealthTab.MISSIONS -> MissionsListView(missions = state.missions)
                HealthTab.BADGES -> BadgesGridView(badges = state.badges)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AppColors.Outline)
            ) {
                Text("Tutup", color = AppColors.TextMuted, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ScoreGaugeHeader(score: WealthScore) {
    val tierColor = when {
        score.score >= 800 -> AppColors.PrimaryDark
        score.score >= 700 -> Color(0xFF10B981)
        score.score >= 500 -> Color(0xFFF59E0B)
        else -> AppColors.Error
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = AppColors.SurfaceRaised,
        border = BorderStroke(1.dp, AppColors.Outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = score.label,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tierColor
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${score.score}",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextMain,
                    letterSpacing = (-1.5).sp
                )
                Text(
                    text = " / 850",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF10B981).copy(alpha = 0.15f)
            ) {
                Text(
                    text = "📈 +${score.delta} poin dibanding bulan lalu",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PillarsBreakdownView(breakdown: ScoreBreakdown) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PillarItem(
            emoji = "🛡️",
            title = "Resilience (Bantalan Darurat)",
            score = breakdown.resilience,
            maxScore = 150,
            desc = "Ketahanan dana cair terhadap skenario tak terduga."
        )
        PillarItem(
            emoji = "🚀",
            title = "Momentum (Pertumbuhan Aset)",
            score = breakdown.momentum,
            maxScore = 150,
            desc = "Kecepatan akumulasi kekayaan bersih bulanan."
        )
        PillarItem(
            emoji = "🎯",
            title = "Adherence (Disiplin Anggaran)",
            score = breakdown.adherence,
            maxScore = 150,
            desc = "Kepatuhan pengeluaran terhadap batas pos anggaran."
        )
        PillarItem(
            emoji = "💧",
            title = "Liquidity (Likuiditas & Kas)",
            score = breakdown.liquidity,
            maxScore = 100,
            desc = "Kesiapan kas untuk melunasi tagihan jangka pendek."
        )
        PillarItem(
            emoji = "🏛️",
            title = "Legacy (Perencanaan Masa Depan)",
            score = breakdown.legacy,
            maxScore = 100,
            desc = "Kelengkapan vault dokumen dan sasaran keluarga."
        )
    }
}

@Composable
private fun PillarItem(
    emoji: String,
    title: String,
    score: Int,
    maxScore: Int,
    desc: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = AppColors.SurfaceRaised,
        border = BorderStroke(1.dp, AppColors.Outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextMain
                    )
                }
                Text(
                    text = "$score / $maxScore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = desc,
                fontSize = 11.sp,
                color = AppColors.TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                        .background(Color(0xFF10B981))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(AppColors.Outline)
                )
            }
        }
    }
}

@Composable
private fun MissionsListView(missions: List<ScoreBoosterMission>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        missions.forEach { mission ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AppColors.SurfaceRaised,
                border = BorderStroke(
                    1.dp,
                    if (mission.isCompleted) Color(0xFF10B981) else AppColors.Outline
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (mission.isCompleted) Icons.Default.CheckCircle else Icons.Default.Star,
                        contentDescription = null,
                        tint = if (mission.isCompleted) Color(0xFF10B981) else Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = mission.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextMain,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (mission.isCompleted) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFFFDE43).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (mission.isCompleted) "Selesai" else "+${mission.potentialPoints} pts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mission.isCompleted) Color(0xFF10B981) else Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = mission.description,
                            fontSize = 11.sp,
                            color = AppColors.TextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgesGridView(badges: List<FinancialBadge>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        badges.forEach { badge ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (badge.isUnlocked) AppColors.SurfaceRaised else AppColors.SurfaceRaised.copy(alpha = 0.6f),
                border = BorderStroke(
                    1.dp,
                    if (badge.isUnlocked) Color(0xFFFFDE43).copy(alpha = 0.6f) else AppColors.Outline
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (badge.isUnlocked) Color(0xFFFFDE43).copy(alpha = 0.2f) else AppColors.Outline.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (badge.isUnlocked) {
                            Text(text = badge.iconEmoji, fontSize = 22.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = AppColors.TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = badge.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badge.isUnlocked) AppColors.TextMain else AppColors.TextMuted
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (badge.isUnlocked) Color(0xFF10B981).copy(alpha = 0.15f) else AppColors.Outline.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    text = if (badge.isUnlocked) "Tercapai" else "Terkunci",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (badge.isUnlocked) Color(0xFF10B981) else AppColors.TextMuted,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = badge.description,
                            fontSize = 11.sp,
                            color = AppColors.TextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
