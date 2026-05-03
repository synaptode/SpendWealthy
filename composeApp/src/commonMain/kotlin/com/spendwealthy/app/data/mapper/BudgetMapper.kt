package com.spendwealthy.app.data.mapper

import com.spendwealthy.app.db.BudgetEntity
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Money

fun BudgetEntity.toDomain(): Budget {
    return Budget(
        id = id,
        categoryId = categoryId,
        limit = Money(limitAmount, currency),
        month = month,
        year = year,
        createdAt = createdAt
    )
}
