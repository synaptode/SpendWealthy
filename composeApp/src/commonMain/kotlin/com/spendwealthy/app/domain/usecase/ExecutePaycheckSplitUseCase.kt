package com.spendwealthy.app.domain.usecase

import com.spendwealthy.app.domain.model.ExecuteSplitRequest
import com.spendwealthy.app.domain.model.ExecuteSplitResult
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.repository.GoalRepository
import com.spendwealthy.app.domain.repository.LedgerRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ExecutePaycheckSplitUseCase(
    private val ledgerRepository: LedgerRepository,
    private val goalRepository: GoalRepository,
    private val clock: Clock = Clock.System
) {
    suspend operator fun invoke(request: ExecuteSplitRequest): ExecuteSplitResult {
        require(request.totalIncome.isPositive()) { "Nominal gaji harus lebih dari 0." }
        require(request.payrollWalletId.isNotBlank()) { "Dompet penerima gaji wajib dipilih." }

        val now = clock.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        // 1. Catat transaksi Income ke dompet payroll
        val incomeTx = Transaction(
            id = "income_paycheck_${now.toEpochMilliseconds()}",
            amount = request.totalIncome,
            type = TransactionType.INCOME,
            categoryId = null,
            walletId = request.payrollWalletId,
            targetWalletId = null,
            date = today,
            note = request.note.ifBlank { "Gaji Masuk & Alokasi Otomatis" },
            merchant = "Paycheck Splitter",
            createdAt = now,
            updatedAt = now
        )
        ledgerRepository.insertTransactionAndApplyBalance(incomeTx)

        // 2. Jika ada savings wallet terpisah, catat transfer untuk pos tabungan
        if (!request.savingsWalletId.isNullOrBlank() &&
            request.savingsWalletId != request.payrollWalletId &&
            request.breakdown.savingsAmount.amount > 0L
        ) {
            val transferTx = Transaction(
                id = "split_transfer_${now.toEpochMilliseconds() + 1}",
                amount = request.breakdown.savingsAmount,
                type = TransactionType.TRANSFER,
                categoryId = null,
                walletId = request.payrollWalletId,
                targetWalletId = request.savingsWalletId,
                date = today,
                note = "Alokasi Tabungan Gaji (${request.breakdown.strategy.title})",
                merchant = "Paycheck Splitter",
                createdAt = now,
                updatedAt = now
            )
            ledgerRepository.insertTransactionAndApplyBalance(transferTx)
        }

        // 3. Update progres setiap target goal yang dialokasikan
        var goalCount = 0
        request.breakdown.goalAllocations.forEach { allocation ->
            if (allocation.allocatedAmount.amount > 0L) {
                goalRepository.updateGoalProgress(
                    id = allocation.goal.id,
                    currentAmount = allocation.projectedAmount.amount
                )
                goalCount++
            }
        }

        return ExecuteSplitResult(
            isSuccess = true,
            totalIncome = request.totalIncome,
            allocatedToGoalsCount = goalCount,
            message = "Gaji sebesar ${request.totalIncome.format()} berhasil dialokasikan secara otomatis."
        )
    }
}
