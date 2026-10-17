package com.spendwealthy.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class SplitStrategy(
    val title: String,
    val description: String,
    val needsRatio: Float,
    val wantsRatio: Float,
    val savingsRatio: Float
) {
    BALANCED_50_30_20(
        title = "50 / 30 / 20",
        description = "50% Kebutuhan, 30% Lifestyle, 20% Tabungan",
        needsRatio = 0.50f,
        wantsRatio = 0.30f,
        savingsRatio = 0.20f
    ),
    NEEDS_FOCUSED_60_20_20(
        title = "60 / 20 / 20",
        description = "60% Kebutuhan, 20% Lifestyle, 20% Tabungan",
        needsRatio = 0.60f,
        wantsRatio = 0.20f,
        savingsRatio = 0.20f
    ),
    AGGRESSIVE_SAVER_40_20_40(
        title = "40 / 20 / 40",
        description = "40% Kebutuhan, 20% Lifestyle, 40% Tabungan",
        needsRatio = 0.40f,
        wantsRatio = 0.20f,
        savingsRatio = 0.40f
    ),
    CUSTOM(
        title = "Kustom",
        description = "Atur persentase sesuai kebutuhanmu",
        needsRatio = 0.50f,
        wantsRatio = 0.30f,
        savingsRatio = 0.20f
    )
}

data class GoalSplitAllocation(
    val goal: Goal,
    val allocatedAmount: Money,
    val projectedAmount: Money,
    val willAchieveGoal: Boolean
)

data class PaycheckSplitBreakdown(
    val totalIncome: Money,
    val strategy: SplitStrategy,
    val needsAmount: Money,
    val needsRatio: Float,
    val wantsAmount: Money,
    val wantsRatio: Float,
    val savingsAmount: Money,
    val savingsRatio: Float,
    val upcomingBillsTotal: Money,
    val isNeedsSufficientForBills: Boolean,
    val billsDeficit: Money,
    val projectedDailyAllowance: Money,
    val goalAllocations: List<GoalSplitAllocation>
)

data class ExecuteSplitRequest(
    val totalIncome: Money,
    val payrollWalletId: String,
    val savingsWalletId: String? = null,
    val note: String = "Gaji Masuk & Alokasi Otomatis",
    val breakdown: PaycheckSplitBreakdown
)

data class ExecuteSplitResult(
    val isSuccess: Boolean,
    val totalIncome: Money,
    val allocatedToGoalsCount: Int,
    val message: String
)
