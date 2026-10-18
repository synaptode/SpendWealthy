package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.RecurrenceType
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VampireDrainEngineTest {

    private val engine = VampireDrainEngine()
    private val date = LocalDate(2026, 10, 20)

    @Test
    fun testVampireDrainCalculationAndClassification() {
        val bills = listOf(
            BillReminder(
                id = "b1",
                name = "Listrik PLN",
                amount = Money(1_000_000L, "IDR"),
                categoryId = null,
                dueDate = date,
                recurrenceType = RecurrenceType.MONTHLY,
                isEnabled = true,
                lastPaidAt = null
            ),
            BillReminder(
                id = "b2",
                name = "Netflix Premium",
                amount = Money(186_000L, "IDR"),
                categoryId = null,
                dueDate = date,
                recurrenceType = RecurrenceType.MONTHLY,
                isEnabled = true,
                lastPaidAt = null
            ),
            BillReminder(
                id = "b3",
                name = "Spotify Family",
                amount = Money(86_000L, "IDR"),
                categoryId = null,
                dueDate = date,
                recurrenceType = RecurrenceType.MONTHLY,
                isEnabled = true,
                lastPaidAt = null
            ),
            BillReminder(
                id = "b4",
                name = "Apple iCloud 200GB",
                amount = Money(45_000L, "IDR"),
                categoryId = null,
                dueDate = date,
                recurrenceType = RecurrenceType.MONTHLY,
                isEnabled = true,
                lastPaidAt = null
            )
        )

        val report = engine.auditSubscriptions(bills)

        assertEquals(4, report.activeSubscriptionsCount)
        assertEquals(3, report.vampireSubscriptionsCount) // Netflix, Spotify, iCloud
        val expectedMonthlyDrain = 186_000L + 86_000L + 45_000L // 317_000L
        assertEquals(expectedMonthlyDrain, report.totalMonthlyDrain)
        assertEquals(expectedMonthlyDrain * 12, report.totalAnnualDrain)
        assertTrue(report.tenYearCompoundedDrain > report.totalAnnualDrain * 10)
        assertTrue(report.smartInsights.isNotEmpty())
    }

    @Test
    fun testCleanZeroVampireDrain() {
        val essentialOnly = listOf(
            BillReminder(
                id = "b_pln",
                name = "Listrik PLN Pascabayar",
                amount = Money(800_000L, "IDR"),
                categoryId = null,
                dueDate = date,
                recurrenceType = RecurrenceType.MONTHLY,
                isEnabled = true,
                lastPaidAt = null
            ),
            BillReminder(
                id = "b_wifi",
                name = "Indihome Fiber Wifi",
                amount = Money(450_000L, "IDR"),
                categoryId = null,
                dueDate = date,
                recurrenceType = RecurrenceType.MONTHLY,
                isEnabled = true,
                lastPaidAt = null
            )
        )

        val report = engine.auditSubscriptions(essentialOnly)

        assertEquals(0L, report.totalMonthlyDrain)
        assertEquals(0, report.vampireSubscriptionsCount)
        assertTrue(report.smartInsights.first().contains("Bersih"))
    }
}
