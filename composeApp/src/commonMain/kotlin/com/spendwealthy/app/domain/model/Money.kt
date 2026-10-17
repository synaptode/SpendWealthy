package com.spendwealthy.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Value object for handling monetary amounts.
 * Uses minor units (e.g. Cents, Rupiah) stored as Long to avoid floating point precision issues.
 * For IDR, 1 unit = Rp1.
 * For currencies with cents, 100 units = 1.00.
 */
@Serializable
data class Money(
    val amount: Long,
    val currency: String
) {
    companion object {
        fun zero(currency: String = "IDR") = Money(0, currency)
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return Money(amount + other.amount, currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return Money(amount - other.amount, currency)
    }

    operator fun times(factor: Int): Money = Money(amount * factor, currency)
    operator fun div(factor: Int): Money = Money(amount / factor, currency)

    operator fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return amount.compareTo(other.amount)
    }

    fun isZero() = amount == 0L
    fun isPositive() = amount > 0L
    fun isNegative() = amount < 0L

    /**
     * Formatting for IDR and other currencies.
     */
    fun format(privacyMode: Boolean = false): String {
        if (privacyMode) return "Rp ••••••"
        return when (currency) {
            "IDR" -> {
                val isNegative = amount < 0
                val absAmount = kotlin.math.abs(amount)
                val formatted = absAmount.toString().reversed().chunked(3).joinToString(".").reversed()
                if (isNegative) "-Rp$formatted" else "Rp$formatted"
            }
            else -> "$currency $amount"
        }
    }

    /**
     * Enhanced international formatting with currency symbols ($250, €1.500, S$80, etc.).
     */
    fun formatWithSymbol(privacyMode: Boolean = false): String {
        if (privacyMode) return "Rp ••••••"
        val isNegative = amount < 0
        val absAmount = kotlin.math.abs(amount)
        val formattedThousands = absAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        val formattedComma = absAmount.toString().reversed().chunked(3).joinToString(",").reversed()
        val sign = if (isNegative) "-" else ""

        return when (currency) {
            "IDR" -> "${sign}Rp$formattedThousands"
            "USD" -> "${sign}$$formattedComma"
            "EUR" -> "${sign}€$formattedThousands"
            "SGD" -> "${sign}S$$formattedComma"
            "JPY" -> "${sign}¥$formattedComma"
            "GBP" -> "${sign}£$formattedComma"
            "AUD" -> "${sign}A$$formattedComma"
            else -> "$sign$currency $formattedThousands"
        }
    }
}

/**
 * Standard exchange rate estimates for multi-currency conversion to IDR.
 */
object CurrencyConverter {
    val ratesToIdr: Map<String, Double> = mapOf(
        "IDR" to 1.0,
        "USD" to 16250.0,
        "EUR" to 17500.0,
        "SGD" to 12400.0,
        "JPY" to 108.0,
        "GBP" to 20800.0,
        "AUD" to 10700.0
    )

    fun toIdr(money: Money): Long {
        if (money.currency == "IDR") return money.amount
        val rate = ratesToIdr[money.currency] ?: 1.0
        return (money.amount * rate).toLong()
    }

    fun formatEstimatedIdr(money: Money): String {
        if (money.currency == "IDR") return money.formatWithSymbol()
        val idrAmount = toIdr(money)
        return "≈ ${Money(idrAmount, "IDR").formatWithSymbol()}"
    }
}
