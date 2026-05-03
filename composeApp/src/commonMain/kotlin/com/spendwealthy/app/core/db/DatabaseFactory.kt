package com.spendwealthy.app.core.db

import app.cash.sqldelight.db.SqlDriver
import com.spendwealthy.app.db.*
import com.spendwealthy.app.db.AppDatabase

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(
        driver = driver,
        walletEntityAdapter = WalletEntity.Adapter(
            createdAtAdapter = instantAdapter
        ),
        transactionEntityAdapter = TransactionEntity.Adapter(
            dateAdapter = localDateAdapter,
            createdAtAdapter = instantAdapter,
            updatedAtAdapter = instantAdapter
        ),
        budgetEntityAdapter = BudgetEntity.Adapter(
            monthAdapter = intAdapter,
            yearAdapter = intAdapter,
            createdAtAdapter = instantAdapter
        ),
        billReminderEntityAdapter = BillReminderEntity.Adapter(
            dueDateAdapter = localDateAdapter,
            lastPaidAtAdapter = instantAdapter
        ),
        vaultDocumentEntityAdapter = VaultDocumentEntity.Adapter(
            expiryDateAdapter = localDateAdapter,
            createdAtAdapter = instantAdapter,
            updatedAtAdapter = instantAdapter
        )
    )
}
