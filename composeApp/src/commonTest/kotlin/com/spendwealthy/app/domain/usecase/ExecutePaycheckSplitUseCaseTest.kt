package com.spendwealthy.app.domain.usecase

import com.spendwealthy.app.domain.model.ExecuteSplitRequest
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.GoalSplitAllocation
import com.spendwealthy.app.domain.model.GoalType
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.PaycheckSplitBreakdown
import com.spendwealthy.app.domain.model.SplitStrategy
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.repository.GoalRepository
import com.spendwealthy.app.domain.repository.LedgerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExecutePaycheckSplitUseCaseTest {

    private val fakeLedgerRepository = FakeLedgerRepo()
    private val fakeGoalRepository = FakeGoalRepo()
    private val fakeClock = object : Clock {
        override fun now(): Instant = Instant.fromEpochMilliseconds(1773000000000)
    }

    private val useCase = ExecutePaycheckSplitUseCase(
        ledgerRepository = fakeLedgerRepository,
        goalRepository = fakeGoalRepository,
        clock = fakeClock
    )

    @Test
    fun executeSplit_insertsIncomeAndTransferAndUpdatesGoals() = runTest {
        val income = Money(10_000_000L, "IDR")
        val goal = Goal("g1", "Dana Darurat", Money(20_000_000L, "IDR"), Money(5_000_000L, "IDR"), GoalType.SAVINGS, null, fakeClock.now())

        val breakdown = PaycheckSplitBreakdown(
            totalIncome = income,
            strategy = SplitStrategy.BALANCED_50_30_20,
            needsAmount = Money(5_000_000L, "IDR"),
            needsRatio = 0.5f,
            wantsAmount = Money(3_000_000L, "IDR"),
            wantsRatio = 0.3f,
            savingsAmount = Money(2_000_000L, "IDR"),
            savingsRatio = 0.2f,
            upcomingBillsTotal = Money(1_000_000L, "IDR"),
            isNeedsSufficientForBills = true,
            billsDeficit = Money(0L, "IDR"),
            projectedDailyAllowance = Money(100_000L, "IDR"),
            goalAllocations = listOf(
                GoalSplitAllocation(
                    goal = goal,
                    allocatedAmount = Money(2_000_000L, "IDR"),
                    projectedAmount = Money(7_000_000L, "IDR"),
                    willAchieveGoal = false
                )
            )
        )

        val request = ExecuteSplitRequest(
            totalIncome = income,
            payrollWalletId = "wallet_payroll",
            savingsWalletId = "wallet_savings",
            note = "Gaji Oktober",
            breakdown = breakdown
        )

        val result = useCase(request)

        assertTrue(result.isSuccess)
        assertEquals(1, result.allocatedToGoalsCount)

        // 2 transactions: 1 INCOME to wallet_payroll, 1 TRANSFER to wallet_savings
        assertEquals(2, fakeLedgerRepository.insertedTransactions.size)
        val incomeTx = fakeLedgerRepository.insertedTransactions[0]
        assertEquals(TransactionType.INCOME, incomeTx.type)
        assertEquals("wallet_payroll", incomeTx.walletId)
        assertEquals(10_000_000L, incomeTx.amount.amount)

        val transferTx = fakeLedgerRepository.insertedTransactions[1]
        assertEquals(TransactionType.TRANSFER, transferTx.type)
        assertEquals("wallet_payroll", transferTx.walletId)
        assertEquals("wallet_savings", transferTx.targetWalletId)
        assertEquals(2_000_000L, transferTx.amount.amount)

        // Goal updated
        assertEquals(7_000_000L, fakeGoalRepository.updatedAmounts["g1"])
    }

    private class FakeLedgerRepo : LedgerRepository {
        val insertedTransactions = mutableListOf<Transaction>()

        override suspend fun insertTransactionAndApplyBalance(transaction: Transaction) {
            insertedTransactions.add(transaction)
        }

        override suspend fun deleteTransactionAndRollbackBalance(transactionId: String) {}
    }

    private class FakeGoalRepo : GoalRepository {
        val updatedAmounts = mutableMapOf<String, Long>()

        override fun getGoals(): Flow<List<Goal>> = flowOf(emptyList())
        override fun getGoalById(id: String): Flow<Goal?> = flowOf(null)
        override suspend fun insertGoal(goal: Goal) {}
        override suspend fun updateGoalProgress(id: String, currentAmount: Long) {
            updatedAmounts[id] = currentAmount
        }
        override suspend fun deleteGoal(id: String) {}
    }
}
