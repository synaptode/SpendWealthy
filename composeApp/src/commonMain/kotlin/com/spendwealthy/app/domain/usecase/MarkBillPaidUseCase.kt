package com.spendwealthy.app.domain.usecase

import com.spendwealthy.app.core.util.daysInMonth
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.RecurrenceType
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.repository.BillRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone

class MarkBillPaidUseCase(
    private val billRepository: BillRepository,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(billId: String, walletId: String) {
        val bill = billRepository.getBillReminderById(billId).firstOrNull()
            ?: return

        val now = Clock.System.now()
        val paidDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        addTransactionUseCase(
            Transaction(
                id = "bill_tx_${bill.id}_${now.toEpochMilliseconds()}",
                amount = Money(bill.amount.amount, bill.amount.currency),
                type = TransactionType.EXPENSE,
                categoryId = bill.categoryId,
                walletId = walletId,
                targetWalletId = null,
                date = paidDate,
                note = "Pembayaran ${bill.name}",
                merchant = bill.name,
                createdAt = now,
                updatedAt = now
            )
        )

        val nextDueDate = bill.nextDueDate(after = paidDate)
        if (nextDueDate == null) {
            billRepository.updateBillReminder(
                bill.copy(
                    isEnabled = false,
                    lastPaidAt = now
                )
            )
            notificationScheduler.cancelBillReminder(bill.id)
        } else {
            val nextBill = bill.copy(
                dueDate = nextDueDate,
                lastPaidAt = now
            )
            billRepository.updateBillReminder(nextBill)
            notificationScheduler.scheduleBillReminder(nextBill)
        }
    }

    private fun com.spendwealthy.app.domain.model.BillReminder.nextDueDate(after: LocalDate): LocalDate? {
        var next = when (recurrenceType) {
            RecurrenceType.NONE -> null
            RecurrenceType.DAILY -> dueDate.plus(DatePeriod(days = 1))
            RecurrenceType.WEEKLY -> dueDate.plus(DatePeriod(days = 7))
            RecurrenceType.MONTHLY -> dueDate.plusMonthsClamped(1)
            RecurrenceType.YEARLY -> dueDate.plusYearsClamped(1)
        }
        while (next != null && next <= after) {
            next = when (recurrenceType) {
                RecurrenceType.NONE -> null
                RecurrenceType.DAILY -> next.plus(DatePeriod(days = 1))
                RecurrenceType.WEEKLY -> next.plus(DatePeriod(days = 7))
                RecurrenceType.MONTHLY -> next.plusMonthsClamped(1)
                RecurrenceType.YEARLY -> next.plusYearsClamped(1)
            }
        }
        return next
    }

    private fun LocalDate.plusMonthsClamped(months: Int): LocalDate {
        val zeroBased = (monthNumber - 1) + months
        val year = year + zeroBased / 12
        val month = zeroBased % 12 + 1
        return LocalDate(year, month, minOf(dayOfMonth, daysInMonth(year, month)))
    }

    private fun LocalDate.plusYearsClamped(years: Int): LocalDate {
        val targetYear = year + years
        return LocalDate(targetYear, monthNumber, minOf(dayOfMonth, daysInMonth(targetYear, monthNumber)))
    }
}
