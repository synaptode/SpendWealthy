package com.spendwealthy.app.data.mapper

import com.spendwealthy.app.db.WalletEntity
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType

fun WalletEntity.toDomain(): Wallet {
    return Wallet(
        id = id,
        name = name,
        balance = Money(balanceAmount, currency),
        type = WalletType.valueOf(type),
        createdAt = createdAt,
        isArchived = isArchived
    )
}
