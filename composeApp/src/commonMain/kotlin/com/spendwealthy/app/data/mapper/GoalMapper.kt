package com.spendwealthy.app.data.mapper

import com.spendwealthy.app.db.GoalEntity
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.GoalType
import com.spendwealthy.app.domain.model.Money

fun GoalEntity.toDomain(): Goal {
    return Goal(
        id = id,
        name = name,
        targetAmount = Money(targetAmount, currency),
        currentAmount = Money(currentAmount, currency),
        type = GoalType.valueOf(type),
        deadline = deadline,
        createdAt = createdAt
    )
}
