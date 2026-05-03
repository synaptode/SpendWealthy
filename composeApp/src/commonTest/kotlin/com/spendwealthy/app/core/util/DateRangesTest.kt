package com.spendwealthy.app.core.util

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DateRangesTest {

    @Test
    fun monthRangeClosesAtLastDayOfRegularMonth() {
        val range = monthRange(year = 2026, month = 5)

        assertEquals(LocalDate(2026, 5, 1), range.start)
        assertEquals(LocalDate(2026, 5, 31), range.endInclusive)
    }

    @Test
    fun monthRangeHandlesLeapYearFebruary() {
        val range = monthRange(year = 2024, month = 2)

        assertEquals(LocalDate(2024, 2, 1), range.start)
        assertEquals(LocalDate(2024, 2, 29), range.endInclusive)
    }

    @Test
    fun monthRangeHandlesDecemberYearBoundary() {
        val range = monthRange(year = 2026, month = 12)

        assertEquals(LocalDate(2026, 12, 1), range.start)
        assertEquals(LocalDate(2026, 12, 31), range.endInclusive)
    }
}
