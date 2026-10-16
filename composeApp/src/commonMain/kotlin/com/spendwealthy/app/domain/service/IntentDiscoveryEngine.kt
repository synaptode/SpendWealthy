package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money

data class DiscoveryIntent(
    val id: String,
    val query: String,
    val category: IntentCategory,
    val actionLabel: String,
    val predictedResult: String? = null
)

enum class IntentCategory {
    SIMULATION,
    PROJECTION,
    FAMILY,
    SECURITY,
    OPTIMIZATION
}

class IntentDiscoveryEngine {

    fun discoverIntents(query: String): List<DiscoveryIntent> {
        val intents = mutableListOf<DiscoveryIntent>()
        val q = query.lowercase()

        if (q.contains("buy") || q.contains("spend") || q.contains("purchase")) {
            intents.add(DiscoveryIntent(
                id = "intent_sim",
                query = query,
                category = IntentCategory.SIMULATION,
                actionLabel = "Simulate Purchase Impact",
                predictedResult = "Analyzing how this affects your 6-month runway..."
            ))
        }

        if (q.contains("when") || q.contains("hit") || q.contains("reach") || q.contains("goal")) {
            intents.add(DiscoveryIntent(
                id = "intent_proj",
                query = query,
                category = IntentCategory.PROJECTION,
                actionLabel = "View Growth Projection",
                predictedResult = "Estimating milestone achievement dates..."
            ))
        }

        if (q.contains("family") || q.contains("wife") || q.contains("husband") || q.contains("kids")) {
            intents.add(DiscoveryIntent(
                id = "intent_fam",
                query = query,
                category = IntentCategory.FAMILY,
                actionLabel = "Check Family Synergy",
                predictedResult = "Coordinating shared goals and liquidity pools..."
            ))
        }

        if (q.contains("safe") || q.contains("secure") || q.contains("vault") || q.contains("pass")) {
            intents.add(DiscoveryIntent(
                id = "intent_sec",
                query = query,
                category = IntentCategory.SECURITY,
                actionLabel = "Open Secure Vault",
                predictedResult = "Accessing encrypted assets and documents..."
            ))
        }

        return intents
    }
}
