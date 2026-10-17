package com.spendwealthy.app.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoneyTest {

    @Test
    fun testPlus() {
        val m1 = Money(1000, "IDR")
        val m2 = Money(500, "IDR")
        assertEquals(1500, (m1 + m2).amount)
    }

    @Test
    fun testMinus() {
        val m1 = Money(1000, "IDR")
        val m2 = Money(300, "IDR")
        assertEquals(700, (m1 - m2).amount)
    }

    @Test
    fun testCurrencyMismatch() {
        val m1 = Money(1000, "IDR")
        val m2 = Money(10, "USD")
        assertFailsWith<IllegalArgumentException> {
            m1 + m2
        }
    }

    @Test
    fun testFormatting() {
        val m1 = Money(1250000, "IDR")
        assertEquals("Rp1.250.000", m1.format())

        val mNegative = Money(-50000, "IDR")
        assertEquals("-Rp50.000", mNegative.format())

        val mUsd = Money(1500, "USD")
        assertEquals("USD 1500", mUsd.format())
        assertEquals("$1,500", mUsd.formatWithSymbol())

        val mEur = Money(3200, "EUR")
        assertEquals("EUR 3200", mEur.format())
        assertEquals("€3.200", mEur.formatWithSymbol())

        val mSgd = Money(850, "SGD")
        assertEquals("SGD 850", mSgd.format())
        assertEquals("S$850", mSgd.formatWithSymbol())
    }

    @Test
    fun testCurrencyConverter() {
        val usd = Money(100, "USD")
        val idrEquivalent = CurrencyConverter.toIdr(usd)
        assertEquals(1625000L, idrEquivalent)

        val idr = Money(50000, "IDR")
        assertEquals(50000L, CurrencyConverter.toIdr(idr))
    }
}
