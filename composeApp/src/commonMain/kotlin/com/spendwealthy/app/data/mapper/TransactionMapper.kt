package com.spendwealthy.app.data.mapper

import com.spendwealthy.app.db.TransactionEntity
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = Money(amount, currency),
        type = TransactionType.valueOf(type),
        categoryId = categoryId,
        walletId = walletId,
        targetWalletId = targetWalletId,
        date = date,
        note = note,
        merchant = merchant,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
