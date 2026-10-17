package com.spendwealthy.app.ui

import com.spendwealthy.app.domain.model.Money
import kotlin.test.Test
import kotlin.test.assertEquals

class SecurityAndPrivacyTest {

    @Test
    fun testMoneyFormattingStandardVsPrivacy() {
        val money = Money(1500000, "IDR")
        assertEquals("Rp1.500.000", money.format())
        assertEquals("Rp ••••••", money.format(privacyMode = true))
    }

    @Test
    fun testZeroMoneyFormattingStandardVsPrivacy() {
        val zero = Money.zero("IDR")
        assertEquals("Rp0", zero.format())
        assertEquals("Rp ••••••", zero.format(privacyMode = true))
    }

    @Test
    fun testForeignCurrencyFormattingStandardVsPrivacy() {
        val usd = Money(250, "USD")
        assertEquals("USD 250", usd.format())
        assertEquals("Rp ••••••", usd.format(privacyMode = true))
    }
}
