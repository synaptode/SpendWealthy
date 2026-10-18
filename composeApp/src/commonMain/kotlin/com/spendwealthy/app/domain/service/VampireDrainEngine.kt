package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.RecurrenceType
import kotlin.math.pow
import kotlin.math.roundToLong

enum class SubscriptionCategory(val displayName: String, val iconEmoji: String, val isEssential: Boolean) {
    ESSENTIAL_UTILITY("Utilitas Pokok (Listrik/Air/Wifi)", "⚡", true),
    ENTERTAINMENT_STREAMING("Streaming & Hiburan", "🎬", false),
    TECH_CLOUD("Cloud & AI Software", "☁️", false),
    HEALTH_FITNESS("Gym & Kebugaran", "💪", false),
    LIFESTYLE_MEMBERSHIP("Langganan Belanja & Lifestyle", "🛍️", false),
    OTHER_RECURRING("Pengeluaran Rutin Lainnya", "🔄", false)
}

data class AuditedSubscription(
    val billId: String,
    val name: String,
    val monthlyAmount: Long,
    val annualAmount: Long,
    val category: SubscriptionCategory,
    val isVampireDrain: Boolean,
    val tenYearInvestedValue: Long
)

data class VampireDrainReport(
    val totalMonthlyDrain: Long,
    val totalAnnualDrain: Long,
    val tenYearCompoundedDrain: Long,
    val twentyYearCompoundedDrain: Long,
    val activeSubscriptionsCount: Int,
    val vampireSubscriptionsCount: Int,
    val subscriptions: List<AuditedSubscription>,
    val smartInsights: List<String>,
    val actionableSavingsMonthly: Long
)

/**
 * Intelligent Subscription Audit & Vampire Drain Detector Engine.
 * Detects recurring monthly drains, categorizes them by necessity, and projects
 * the compound opportunity cost of wealth lost over 10 to 20 years.
 */
class VampireDrainEngine {

    fun auditSubscriptions(
        bills: List<BillReminder>,
        annualReturnRate: Double = 0.08 // 8% benchmark return
    ): VampireDrainReport {
        val activeBills = bills.filter { it.isEnabled }
        val auditedList = mutableListOf<AuditedSubscription>()

        val monthlyRate = annualReturnRate / 12.0
        val months10Years = 120
        val months20Years = 240

        for (bill in activeBills) {
            val rawAmount = bill.amount.amount
            val monthlyAmount = when (bill.recurrenceType) {
                RecurrenceType.DAILY -> rawAmount * 30
                RecurrenceType.WEEKLY -> rawAmount * 4
                RecurrenceType.MONTHLY -> rawAmount
                RecurrenceType.YEARLY -> (rawAmount / 12).coerceAtLeast(1L)
                RecurrenceType.NONE -> rawAmount
            }
            val annualAmount = monthlyAmount * 12

            val category = classifySubscription(bill.name)
            val isVampire = !category.isEssential

            // Future value of an annuity formula: FV = PMT * [((1 + r)^n - 1) / r]
            val fv10Factor = (((1.0 + monthlyRate).pow(months10Years) - 1.0) / monthlyRate)
            val tenYearValue = (monthlyAmount * fv10Factor).roundToLong()

            auditedList.add(
                AuditedSubscription(
                    billId = bill.id,
                    name = bill.name,
                    monthlyAmount = monthlyAmount,
                    annualAmount = annualAmount,
                    category = category,
                    isVampireDrain = isVampire,
                    tenYearInvestedValue = tenYearValue
                )
            )
        }

        val vampireItems = auditedList.filter { it.isVampireDrain }
        val totalMonthlyDrain = vampireItems.sumOf { it.monthlyAmount }
        val totalAnnualDrain = totalMonthlyDrain * 12

        val fv10Total = (((1.0 + monthlyRate).pow(months10Years) - 1.0) / monthlyRate)
        val fv20Total = (((1.0 + monthlyRate).pow(months20Years) - 1.0) / monthlyRate)

        val tenYearCompound = (totalMonthlyDrain * fv10Total).roundToLong()
        val twentyYearCompound = (totalMonthlyDrain * fv20Total).roundToLong()

        val smartInsights = generateInsights(vampireItems, totalMonthlyDrain, tenYearCompound)

        return VampireDrainReport(
            totalMonthlyDrain = totalMonthlyDrain,
            totalAnnualDrain = totalAnnualDrain,
            tenYearCompoundedDrain = tenYearCompound,
            twentyYearCompoundedDrain = twentyYearCompound,
            activeSubscriptionsCount = auditedList.size,
            vampireSubscriptionsCount = vampireItems.size,
            subscriptions = auditedList.sortedByDescending { it.monthlyAmount },
            smartInsights = smartInsights,
            actionableSavingsMonthly = totalMonthlyDrain
        )
    }

    private fun classifySubscription(name: String): SubscriptionCategory {
        val lower = name.lowercase()
        return when {
            lower.contains("listrik") || lower.contains("pln") || lower.contains("pdam") ||
            lower.contains("air") || lower.contains("bpjs") || lower.contains("indihome") ||
            lower.contains("biznet") || lower.contains("wifi") || lower.contains("internet") ||
            lower.contains("asuransi") || lower.contains("kpr") || lower.contains("cicilan") -> {
                SubscriptionCategory.ESSENTIAL_UTILITY
            }
            lower.contains("netflix") || lower.contains("spotify") || lower.contains("youtube") ||
            lower.contains("disney") || lower.contains("hbo") || lower.contains("vidio") ||
            lower.contains("prime") || lower.contains("apple music") || lower.contains("streaming") -> {
                SubscriptionCategory.ENTERTAINMENT_STREAMING
            }
            lower.contains("icloud") || lower.contains("google one") || lower.contains("chatgpt") ||
            lower.contains("canva") || lower.contains("adobe") || lower.contains("dropbox") ||
            lower.contains("notion") || lower.contains("github") || lower.contains("hosting") -> {
                SubscriptionCategory.TECH_CLOUD
            }
            lower.contains("gym") || lower.contains("fitness") || lower.contains("yoga") ||
            lower.contains("f45") || lower.contains("pilates") -> {
                SubscriptionCategory.HEALTH_FITNESS
            }
            lower.contains("grab") || lower.contains("shopee") || lower.contains("tokopedia") ||
            lower.contains("gofood") || lower.contains("plus") || lower.contains("membership") -> {
                SubscriptionCategory.LIFESTYLE_MEMBERSHIP
            }
            else -> SubscriptionCategory.OTHER_RECURRING
        }
    }

    private fun generateInsights(
        vampireItems: List<AuditedSubscription>,
        totalMonthly: Long,
        tenYearCost: Long
    ): List<String> {
        val insights = mutableListOf<String>()

        if (vampireItems.isEmpty()) {
            insights.add("🎉 Bersih! Tidak terdeteksi kebocoran langganan non-esensial aktif.")
            return insights
        }

        val streamingCount = vampireItems.count { it.category == SubscriptionCategory.ENTERTAINMENT_STREAMING }
        if (streamingCount >= 2) {
            insights.add("🎬 Terdeteksi $streamingCount langganan streaming video/musik sekaligus. Jeda salah satu layanan bisa menghemat Rp ${(totalMonthly / 3).coerceAtLeast(100_000L)}/bulan.")
        }

        if (totalMonthly >= 500_000L) {
            insights.add("⚡ Total biaya langganan silumanmu mencapai Rp ${totalMonthly / 1_000}rb/bulan. Jika dialihkan ke instrumen investasi 8%, nilainya menjadi Rp ${tenYearCost / 1_000_000} Juta dalam 10 tahun!")
        } else {
            insights.add("💡 Mengurangi Rp 150.000/bln langganan jarang pakai setara mengamankan dana darurat ekstra Rp 18 Juta dalam 5 tahun.")
        }

        return insights
    }
}
