package com.spendwealthy.app.domain.usecase

import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.repository.BillRepository
import kotlinx.coroutines.flow.firstOrNull

class RescheduleBillRemindersUseCase(
    private val billRepository: BillRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke() {
        billRepository.getBillReminders()
            .firstOrNull()
            .orEmpty()
            .filter { it.isEnabled }
            .forEach { bill ->
                notificationScheduler.scheduleBillReminder(bill)
            }
    }
}
