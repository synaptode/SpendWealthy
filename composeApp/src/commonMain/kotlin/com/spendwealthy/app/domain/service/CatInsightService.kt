package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import kotlinx.datetime.*

fun Month.daysInMonth(leapYear: Boolean): Int = when (this) {
    Month.FEBRUARY -> if (leapYear) 29 else 28
    Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
    else -> 31
}

data class CatInsight(
    val title: String,
    val message: String,
    val mood: CatMood,
    val impact: InsightImpact = InsightImpact.NEUTRAL
)

enum class CatMood {
    HAPPY, SLEEPY, GRUMPY, ALERT, PROUD, CURIOUS, NINJA
}

enum class InsightImpact {
    POSITIVE, NEGATIVE, NEUTRAL
}

interface InsightRule {
    fun evaluate(
        transactions: List<Transaction>,
        budgets: List<Budget>,
        now: LocalDate,
        payday: LocalDate
    ): CatInsight?
}

class RunRateRule : InsightRule {
    override fun evaluate(transactions: List<Transaction>, budgets: List<Budget>, now: LocalDate, payday: LocalDate): CatInsight? {
        val daysInMonth = now.month.daysInMonth(now.year % 4 == 0)
        val elapsedDays = now.dayOfMonth.coerceAtLeast(1)
        val currentMonthSpent = transactions
            .filter { it.date.monthNumber == now.monthNumber && it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.amount }
        
        val dailyRunRate = currentMonthSpent / elapsedDays
        val totalBudget = budgets.sumOf { it.limit.amount }
        
        if (totalBudget > 0) {
            val projectedSpend = dailyRunRate * daysInMonth
            if (projectedSpend.toDouble() > totalBudget * 1.2) {
                return CatInsight(
                    "Predictive Warning! 📉",
                    "At your current pace, you'll overshoot your budget by ${(projectedSpend - totalBudget) / 1000}k. Time for some financial ninja moves!",
                    CatMood.NINJA,
                    InsightImpact.NEGATIVE
                )
            }
        }
        return null
    }
}

class CategoryConcentrationRule : InsightRule {
    override fun evaluate(transactions: List<Transaction>, budgets: List<Budget>, now: LocalDate, payday: LocalDate): CatInsight? {
        val expenses = transactions.filter { it.date.monthNumber == now.monthNumber && it.type == TransactionType.EXPENSE }
        if (expenses.isEmpty()) return null
        
        val totalSpent = expenses.sumOf { it.amount.amount }
        val categoryGroups = expenses.groupBy { it.categoryId }
        val topCategory = categoryGroups.maxByOrNull { it.value.sumOf { t -> t.amount.amount } }
        
        if (topCategory != null) {
            val spentInTop = topCategory.value.sumOf { it.amount.amount }
            if (spentInTop > totalSpent * 0.5) {
                return CatInsight(
                    "Hyper-Focus Detected 🎯",
                    "Half of your stash is going into one category. Diversify your pounces to keep the hoard balanced!",
                    CatMood.CURIOUS,
                    InsightImpact.NEUTRAL
                )
            }
        }
        return null
    }
}

class CatInsightService {
    private val rules = listOf(
        RunRateRule(),
        CategoryConcentrationRule()
    )

    fun getDailyInsight(transactions: List<Transaction>, budgets: List<Budget>, payday: LocalDate = LocalDate(2024, 5, 25)): CatInsight {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // Check rules in order of priority (first non-null wins)
        rules.forEach { rule ->
            rule.evaluate(transactions, budgets, now, payday)?.let { return it }
        }

        // Default fallback logic
        val totalBudget = budgets.sumOf { it.limit.amount }
        val totalSpent = transactions.filter { it.date.monthNumber == now.monthNumber && it.type == TransactionType.EXPENSE }.sumOf { it.amount.amount }

        return when {
            totalSpent == 0L -> CatInsight("Ready to Pounce? 🐾", "Your pantry is stocked and quiet. Awaiting your first move!", CatMood.SLEEPY)
            totalSpent < totalBudget * 0.3 -> CatInsight("Master of Stealth 🥷", "Your spending is practically invisible. Excellent discipline!", CatMood.PROUD, InsightImpact.POSITIVE)
            else -> CatInsight("Steady Paws 🐈", "You're navigating the financial jungle with grace. Keep it up!", CatMood.HAPPY)
        }
    }

    fun getFamilyInsight(transactions: List<Transaction>, budgets: List<Budget>): CatInsight? {
        val familyExpenses = transactions.filter { it.familyId != null && it.type == TransactionType.EXPENSE }
        if (familyExpenses.isEmpty()) return null

        val totalFamilySpent = familyExpenses.sumOf { it.amount.amount }
        val totalFamilyBudget = budgets.sumOf { it.limit.amount } 

        if (totalFamilyBudget > 0 && totalFamilySpent > totalFamilyBudget * 0.9) {
            return CatInsight(
                "Pack Alert! 🚨",
                "The shared stash is running low! Time for a family huddle before the next pounce.",
                CatMood.ALERT,
                InsightImpact.NEGATIVE
            )
        }
        return null
    }
}
