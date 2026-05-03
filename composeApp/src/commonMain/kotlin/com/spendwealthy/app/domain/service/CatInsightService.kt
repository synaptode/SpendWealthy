package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Transaction
import kotlin.random.Random

data class CatInsight(
    val title: String,
    val message: String,
    val mood: CatMood
)

enum class CatMood {
    HAPPY, SLEEPY, GRUMPY, ALERT, PROUD
}

class CatInsightService {
    private val genericInsights = listOf(
        CatInsight("Morning Stretch! 🐾", "Time to stretch those financial muscles. Have you checked your treat stash today?", CatMood.SLEEPY),
        CatInsight("Purr-fect Timing! ✨", "No big pounces detected today. Your pantry is looking safe!", CatMood.HAPPY),
        CatInsight("Sharp Claws! 💎", "You've been avoiding impulsive pounces. Your savings are growing sharp!", CatMood.PROUD),
        CatInsight("Nervous Tail... 🐈", "I see some big fish being bought lately. Keep an eye on the budget!", CatMood.ALERT),
        CatInsight("Grumpy Paws 😾", "Spending too much on 'luxury salmon'? Remember your long-term goals!", CatMood.GRUMPY)
    )

    fun getDailyInsight(transactions: List<Transaction>, budgets: List<Budget>): CatInsight {
        // Logic to analyze spending and return appropriate insight
        val totalSpent = transactions.filter { it.date.monthNumber == 5 }.sumOf { it.amount.amount } // Simplification
        
        return if (totalSpent > 1000000) {
            genericInsights.find { it.mood == CatMood.GRUMPY } ?: genericInsights.random()
        } else if (transactions.isEmpty()) {
            genericInsights.find { it.mood == CatMood.SLEEPY } ?: genericInsights.random()
        } else {
            genericInsights.random()
        }
    }
}
