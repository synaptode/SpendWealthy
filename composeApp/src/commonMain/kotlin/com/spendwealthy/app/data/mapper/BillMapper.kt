package com.spendwealthy.app.data.mapper

import com.spendwealthy.app.db.BillReminderEntity
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.RecurrenceType

fun BillReminderEntity.toDomain(): BillReminder {
    return BillReminder(
        id = id,
        name = name,
        amount = Money(amount, currency),
        categoryId = categoryId,
        dueDate = dueDate,
        recurrenceType = RecurrenceType.valueOf(recurrenceType),
        isEnabled = isEnabled,
        lastPaidAt = lastPaidAt
    )
}
