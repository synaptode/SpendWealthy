package com.spendwealthy.app.core.db

import app.cash.sqldelight.db.SqlDriver
import com.spendwealthy.app.db.*

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(
        driver = driver,
        billReminderEntityAdapter = BillReminderEntity.Adapter(
            dueDateAdapter = localDateAdapter,
            lastPaidAtAdapter = instantAdapter
        ),
        budgetEntityAdapter = BudgetEntity.Adapter(
            monthAdapter = intAdapter,
            yearAdapter = intAdapter,
            createdAtAdapter = instantAdapter
        ),
        familyEntityAdapter = FamilyEntity.Adapter(
            createdAtAdapter = instantAdapter
        ),
        goalEntityAdapter = GoalEntity.Adapter(
            deadlineAdapter = localDateAdapter,
            createdAtAdapter = instantAdapter
        ),
        transactionEntityAdapter = TransactionEntity.Adapter(
            dateAdapter = localDateAdapter,
            createdAtAdapter = instantAdapter,
            updatedAtAdapter = instantAdapter
        ),
        vaultDocumentEntityAdapter = VaultDocumentEntity.Adapter(
            expiryDateAdapter = localDateAdapter,
            createdAtAdapter = instantAdapter,
            updatedAtAdapter = instantAdapter
        ),
        walletEntityAdapter = WalletEntity.Adapter(
            createdAtAdapter = instantAdapter
        )
    )
}
