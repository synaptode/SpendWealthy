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
    }
}
