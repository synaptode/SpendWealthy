package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money

data class EstateReport(
    val wealthTransferEfficiency: Int, // 0-100%
    val taxExposureRisk: Int, // 0-100%
    val beneficiaryCoverage: Int, // 0-100%
    val legacyPillarStatus: String,
    val recommendations: List<String>
)

class EstateIntelligenceEngine {

    fun analyzeEstate(
        totalAssets: Money,
        familyMembers: Int,
        hasVault: Boolean
    ): EstateReport {
        val transferEfficiency = if (hasVault) 90 else 40
        val taxExposure = if (totalAssets.amount > 1_000_000_000L) 65 else 20
        val beneficiaryCoverage = if (familyMembers > 0) 85 else 0
        
        val status = when {
            transferEfficiency > 80 && beneficiaryCoverage > 80 -> "Sovereign Legacy"
            transferEfficiency > 50 -> "Developing Legacy"
            else -> "Vulnerable Legacy"
        }

        val recs = mutableListOf<String>()
        if (!hasVault) recs.add("Legacy at risk: Digitize and lock essential documents in the Secure Vault.")
        if (taxExposure > 50) recs.add("Tax optimization required: Diversify into tax-efficient asset classes.")
        if (beneficiaryCoverage == 0) recs.add("Governance gap: Define beneficiaries in the Family Hub.")

        return EstateReport(
            wealthTransferEfficiency = transferEfficiency,
            taxExposureRisk = taxExposure,
            beneficiaryCoverage = beneficiaryCoverage,
            legacyPillarStatus = status,
            recommendations = recs
        )
    }
}
