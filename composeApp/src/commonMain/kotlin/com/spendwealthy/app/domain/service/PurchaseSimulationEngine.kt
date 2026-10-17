package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlin.math.roundToInt

class PurchaseSimulationEngine(
    private val safeToSpendEngine: SafeToSpendEngine = SafeToSpendEngine(),
    private val resilienceService: ResilienceService = ResilienceService()
) {

    fun simulate(
        request: PurchaseSimulationRequest,
        wallets: List<Wallet>,
        transactions: List<Transaction>,
        unpaidBills: List<BillReminder>,
        budgets: List<Budget>,
        currentSpentInBudgets: Map<String?, Long>,
        activeGoals: List<Goal> = emptyList(),
        today: LocalDate,
        payday: LocalDate
    ): PurchaseSimulationResult {
        val currency = request.price.currency
        val purchaseAmount = request.price.amount

        // 1. Check direct wallet affordability
        val targetWallet = request.selectedWalletId?.let { id -> wallets.find { it.id == id } }
            ?: wallets.maxByOrNull { it.balance.amount }
        val walletBalance = targetWallet?.balance?.amount ?: 0L
        val canAffordDirectly = walletBalance >= purchaseAmount

        val totalLiquidityAmount = wallets.sumOf { it.balance.amount }
        val totalLiquidity = Money(totalLiquidityAmount, currency)

        // 2. Current Safe-to-Spend & Daily Allowance
        val currentSafeResult = safeToSpendEngine.calculateAdvanced(
            totalLiquidity = totalLiquidity,
            unpaidBills = unpaidBills,
            budgets = budgets,
            currentSpentInBudgets = currentSpentInBudgets,
            today = today,
            payday = payday
        )
        val currentDailyAllowance = currentSafeResult.dailySafe

        // 3. Simulated Safe-to-Spend & Daily Allowance post-purchase
        val simulatedLiquidity = Money((totalLiquidityAmount - purchaseAmount).coerceAtLeast(0L), currency)
        val simulatedSafeResult = safeToSpendEngine.calculateAdvanced(
            totalLiquidity = simulatedLiquidity,
            unpaidBills = unpaidBills,
            budgets = budgets,
            currentSpentInBudgets = currentSpentInBudgets,
            today = today,
            payday = payday
        )
        val simulatedDailyAllowance = simulatedSafeResult.dailySafe

        // 4. Resilience / Runway Impact
        val currentResilience = resilienceService.calculateResilience(wallets, transactions)
        val simulatedWallets = wallets.map { w ->
            if (w.id == targetWallet?.id) {
                w.copy(balance = Money((w.balance.amount - purchaseAmount).coerceAtLeast(0L), currency))
            } else {
                w
            }
        }
        val simulatedResilience = resilienceService.calculateResilience(simulatedWallets, transactions)

        // 5. Impact on Goals (Delayed Days estimation)
        val activeSavingsGoals = activeGoals.filter { it.type == GoalType.SAVINGS }
        val daysDelayed = if (activeSavingsGoals.isNotEmpty()) {
            val estimatedMonthlySavings = currentSafeResult.breakdown.savingsReserve.amount.coerceAtLeast(500_000L)
            val delayedFraction = purchaseAmount.toDouble() / estimatedMonthlySavings.toDouble()
            (delayedFraction * 30.0).roundToInt().coerceIn(0, 365)
        } else {
            0
        }

        // 6. Verdict Determination
        val currentRunway = (currentResilience.runwayMonths * 10).roundToInt() / 10.0
        val simulatedRunway = (simulatedResilience.runwayMonths * 10).roundToInt() / 10.0

        val verdict: PurchaseVerdict
        val verdictTitle: String
        val verdictMessage: String
        val recommendation: String

        if (!canAffordDirectly) {
            verdict = PurchaseVerdict.HIGH_RISK_DEFICIT
            verdictTitle = "Saldo Tidak Cukup"
            verdictMessage = "Saldo dompet '${targetWallet?.name ?: "Utama"}' tidak mencukupi untuk transaksi ini."
            recommendation = "Pilih dompet lain yang memiliki saldo memadai atau tunda pembelian."
        } else if (simulatedRunway < 1.0 || (currentDailyAllowance.amount > 0 && simulatedDailyAllowance.amount == 0L)) {
            verdict = PurchaseVerdict.HIGH_RISK_DEFICIT
            verdictTitle = "Risiko Tinggi / Defisit"
            verdictMessage = "Pembelian ini menghabiskan jatah harianmu hingga gajian atau cadangan kas darurat tersisa < 1 bulan."
            recommendation = "Sangat disarankan menunda belanja ini dan jadikan target tabungan terlebih dahulu."
        } else if (simulatedRunway < 3.0 || (simulatedDailyAllowance.amount < currentDailyAllowance.amount * 0.5)) {
            verdict = PurchaseVerdict.PROCEED_WITH_CAUTION
            verdictTitle = "Pertimbangkan Lagi"
            val dropPct = if (currentDailyAllowance.amount > 0) {
                ((1.0 - simulatedDailyAllowance.amount.toDouble() / currentDailyAllowance.amount) * 100).toInt()
            } else 0
            verdictMessage = "Bisa dibeli, tetapi jatah harianmu turun $dropPct% hingga tanggal gajian."
            recommendation = "Pastikan tidak ada pengeluaran mendesak lain dalam waktu dekat."
        } else {
            verdict = PurchaseVerdict.SAFE_TO_BUY
            verdictTitle = "Aman Dibeli! 🎉"
            verdictMessage = "Kondisi keuanganmu sangat sehat dan cadangan kas darurat tetap aman di $simulatedRunway bulan."
            recommendation = "Pembelian ini aman dilakukan tanpa mengorbankan pos tabungan dan tagihan wajib."
        }

        return PurchaseSimulationResult(
            verdict = verdict,
            verdictTitle = verdictTitle,
            verdictMessage = verdictMessage,
            currentDailyAllowance = currentDailyAllowance,
            simulatedDailyAllowance = simulatedDailyAllowance,
            emergencyRunwayMonthsCurrent = currentRunway,
            emergencyRunwayMonthsSimulated = simulatedRunway,
            impactOnFamilyGoalDaysDelayed = daysDelayed,
            recommendation = recommendation,
            canAffordDirectly = canAffordDirectly
        )
    }
}
