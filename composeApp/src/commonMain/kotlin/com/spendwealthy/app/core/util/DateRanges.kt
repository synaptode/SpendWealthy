package com.spendwealthy.app.core.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

data class DateRange(
    val start: LocalDate,
    val endInclusive: LocalDate
)

fun monthRange(year: Int, month: Int): DateRange {
    val nextMonth = if (month == 12) {
        LocalDate(year + 1, 1, 1)
    } else {
        LocalDate(year, month + 1, 1)
    }
    return DateRange(
        start = LocalDate(year, month, 1),
        endInclusive = nextMonth.minus(DatePeriod(days = 1))
    )
}

fun daysInMonth(year: Int, month: Int): Int = monthRange(year, month).endInclusive.dayOfMonth
