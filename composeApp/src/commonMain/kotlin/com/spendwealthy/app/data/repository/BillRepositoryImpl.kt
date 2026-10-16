package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.spendwealthy.app.core.architecture.DefaultDispatcherProvider
import com.spendwealthy.app.core.architecture.DispatcherProvider
import com.spendwealthy.app.data.mapper.toDomain
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.repository.BillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BillRepositoryImpl(
    private val db: AppDatabase,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : BillRepository {

    private val queries = db.appDatabaseQueries

    override fun getBillReminders(): Flow<List<BillReminder>> {
        return queries.selectAllBillReminders()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getBillReminderById(id: String): Flow<BillReminder?> {
        return queries.selectBillReminderById(id)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .map { it?.toDomain() }
    }

    override suspend fun insertBillReminder(bill: BillReminder) {
        queries.insertBillReminder(
            id = bill.id,
            name = bill.name,
            amount = bill.amount.amount,
            currency = bill.amount.currency,
            categoryId = bill.categoryId,
            dueDate = bill.dueDate,
            recurrenceType = bill.recurrenceType.name,
            isEnabled = bill.isEnabled,
            lastPaidAt = bill.lastPaidAt
        )
    }

    override suspend fun updateBillReminder(bill: BillReminder) {
        queries.updateBillReminder(
            id = bill.id,
            name = bill.name,
            amount = bill.amount.amount,
            currency = bill.amount.currency,
            categoryId = bill.categoryId,
            dueDate = bill.dueDate,
            recurrenceType = bill.recurrenceType.name,
            isEnabled = bill.isEnabled,
            lastPaidAt = bill.lastPaidAt
        )
    }

    override suspend fun deleteBillReminder(id: String) {
        queries.deleteBillReminder(id)
    }
}
