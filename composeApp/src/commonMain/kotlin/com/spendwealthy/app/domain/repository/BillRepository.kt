package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.BillReminder
import kotlinx.coroutines.flow.Flow

interface BillRepository {
    fun getBillReminders(): Flow<List<BillReminder>>
    fun getBillReminderById(id: String): Flow<BillReminder?>
    suspend fun insertBillReminder(bill: BillReminder)
    suspend fun updateBillReminder(bill: BillReminder)
    suspend fun deleteBillReminder(id: String)
}
