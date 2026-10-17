package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.FinancialStreak
import com.spendwealthy.app.domain.model.ScoreBoosterMission
import kotlin.test.Test
import kotlin.test.assertTrue

class WealthCardFormatterTest {

    @Test
    fun testFormatExecutiveCard_containsAllKeyMetrics() {
        val score = WealthScore(
            score = 780,
            label = "Wealth Professional",
            breakdown = ScoreBreakdown(130, 120, 140, 95, 85),
            delta = 15
        )
        val streak = FinancialStreak(
            currentStreakDays = 10,
            longestStreakDays = 21,
            isStreakMaintainedToday = true,
            message = "Konsistensi luar biasa!"
        )
        val missions = listOf(
            ScoreBoosterMission(
                id = "m1",
                title = "Simpan Dana Darurat",
                description = "Capai 3 bulan pengeluaran",
                potentialPoints = 50,
                category = com.spendwealthy.app.domain.model.BoosterCategory.EMERGENCY_FUND,
                isCompleted = true
            ),
            ScoreBoosterMission(
                id = "m2",
                title = "Pangkas Belanja Kafe",
                description = "Hemat Rp100.000",
                potentialPoints = 25,
                category = com.spendwealthy.app.domain.model.BoosterCategory.BUDGET_DISCIPLINE,
                isCompleted = false
            )
        )

        val card = WealthCardFormatter.formatExecutiveCard(score, streak, missions)

        assertTrue(card.contains("SPENDWEALTHY • SOVEREIGN WEALTH LAB"))
        assertTrue(card.contains("780 / 850 (Wealth Professional)"))
        assertTrue(card.contains("+15 pts"))
        assertTrue(card.contains("10 Hari Beruntun"))
        assertTrue(card.contains("Ketahanan Cadangan (Resilience) : 130 / 150"))
        assertTrue(card.contains("Momentum Akumulasi (Momentum)   : 120 / 150"))
        assertTrue(card.contains("Disiplin Anggaran (Adherence)   : 140 / 150"))
        assertTrue(card.contains("Kesiapan Kas Bebas (Liquidity)  : 95 / 100"))
        assertTrue(card.contains("Perencanaan Masa Depan (Legacy) : 85 / 100"))
        assertTrue(card.contains("[✓] Simpan Dana Darurat (+50 pts)"))
        assertTrue(card.contains("[ ] Pangkas Belanja Kafe (+25 pts)"))
    }
}
