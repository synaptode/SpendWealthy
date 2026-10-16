package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money

data class Asset(
    val id: String,
    val name: String,
    val type: AssetType,
    val value: Money,
    val annualYield: Double,
    val riskLevel: Int // 1-10
)

enum class AssetType { CASH, STOCK, CRYPTO, REAL_ESTATE, COMMODITY }

data class PortfolioAnalysis(
    val totalValue: Money,
    val diversificationScore: Int,
    val weightedYield: Double,
    val rebalanceActions: List<String>
)

class AssetIntelligenceEngine {

    fun analyzePortfolio(assets: List<Asset>): PortfolioAnalysis {
        val totalAmount = assets.sumOf { it.value.amount }
        val currency = assets.firstOrNull()?.value?.currency ?: "IDR"
        
        val typeCount = assets.map { it.type }.distinct().size
        val diversificationScore = (typeCount * 20).coerceAtMost(100)
        
        val totalWeightedYield = if (totalAmount > 0) {
            assets.sumOf { it.value.amount.toDouble() * it.annualYield } / totalAmount.toDouble()
        } else 0.0
        
        val actions = mutableListOf<String>()
        val cashWeight = if (totalAmount > 0) assets.filter { it.type == AssetType.CASH }.sumOf { it.value.amount }.toDouble() / totalAmount.toDouble() else 0.0
        
        if (cashWeight > 0.4) {
            actions.add("Excessive Cash: Reallocate 15% to index funds for inflation protection.")
        }
        if (diversificationScore < 60) {
            actions.add("Concentration Risk: Explore Commodity or Real Estate exposure.")
        }

        return PortfolioAnalysis(
            totalValue = Money(totalAmount, currency),
            diversificationScore = diversificationScore,
            weightedYield = totalWeightedYield,
            rebalanceActions = actions
        )
    }
}
