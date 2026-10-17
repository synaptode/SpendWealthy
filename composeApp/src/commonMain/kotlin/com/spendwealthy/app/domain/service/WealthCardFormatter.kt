package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.FinancialStreak
import com.spendwealthy.app.domain.model.ScoreBoosterMission

object WealthCardFormatter {

    fun formatExecutiveCard(
        score: WealthScore,
        streak: FinancialStreak,
        missions: List<ScoreBoosterMission>
    ): String {
        val completedCount = missions.count { it.isCompleted }
        val deltaSign = if (score.delta >= 0) "+${score.delta}" else "${score.delta}"
        return buildString {
            appendLine("══════════════════════════════════════════════")
            appendLine("   SPENDWEALTHY • SOVEREIGN WEALTH LAB")
            appendLine("   Kartu Ketahanan Finansial Eksekutif")
            appendLine("══════════════════════════════════════════════")
            appendLine()
            appendLine("SKOR KESEHATAN FINANSIAL:")
            appendLine("• Skor Total  : ${score.score} / 850 (${score.label})")
            appendLine("• Pergerakan  : $deltaSign pts bulan ini")
            appendLine("• Konsistensi : 🔥 ${streak.currentStreakDays} Hari Beruntun (Rekor: ${streak.longestStreakDays} Hari)")
            appendLine()
            appendLine("5 PILAR KESEHATAN KAS:")
            appendLine("1. Ketahanan Cadangan (Resilience) : ${score.breakdown.resilience} / 150")
            appendLine("2. Momentum Akumulasi (Momentum)   : ${score.breakdown.momentum} / 150")
            appendLine("3. Disiplin Anggaran (Adherence)   : ${score.breakdown.adherence} / 150")
            appendLine("4. Kesiapan Kas Bebas (Liquidity)  : ${score.breakdown.liquidity} / 100")
            appendLine("5. Perencanaan Masa Depan (Legacy) : ${score.breakdown.legacy} / 100")
            appendLine()
            appendLine("MISI PENINGKATAN SKOR:")
            appendLine("• Progres: $completedCount dari ${missions.size} misi aktif")
            missions.take(3).forEach { m ->
                val mark = if (m.isCompleted) "[✓]" else "[ ]"
                appendLine("  $mark ${m.title} (+${m.potentialPoints} pts)")
            }
            appendLine()
            appendLine("══════════════════════════════════════════════")
            appendLine("Kemandirian Finansial Dimulai dari Kesadaran.")
            appendLine("SpendWealthy • Autonomous Wealth Platform")
            appendLine("══════════════════════════════════════════════")
        }
    }
}
