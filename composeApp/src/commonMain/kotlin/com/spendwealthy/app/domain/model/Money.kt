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
     * Simple formatting for IDR. 
     * In a production app, this should probably use platform-specific formatters 
     * or a more robust common-main formatting logic.
     */
    fun format(privacyMode: Boolean = false): String {
        if (privacyMode) return "Rp ••••••"
        return when (currency) {
            "IDR" -> {
                val formatted = amount.toString().reversed().chunked(3).joinToString(".").reversed()
                "Rp$formatted"
            }
            else -> "$currency $amount"
        }
    }
}
