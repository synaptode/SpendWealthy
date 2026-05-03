package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class BalanceCalculatorTest {

    private val calculator = BalanceCalculator()
    private val now = Clock.System.now()
    private val today = LocalDate(2024, 1, 1)

    private val mockWallet = Wallet(
        id = "w1",
        name = "Cash",
        balance = Money(1000, "IDR"),
        type = WalletType.CASH,
        createdAt = now,
        isArchived = false
    )

    @Test
    fun testIncome() {
        val transaction = Transaction(
            id = "t1",
            amount = Money(500, "IDR"),
            type = TransactionType.INCOME,
            categoryId = null,
            walletId = "w1",
            targetWalletId = null,
            date = today,
            note = "",
            merchant = "",
            createdAt = now,
            updatedAt = now
        )
        val newBalance = calculator.calculateNewBalance(mockWallet, transaction)
        assertEquals(1500, newBalance.amount)
    }

    @Test
    fun testExpense() {
        val transaction = Transaction(
            id = "t1",
            amount = Money(300, "IDR"),
            type = TransactionType.EXPENSE,
            categoryId = null,
            walletId = "w1",
            targetWalletId = null,
            date = today,
            note = "",
            merchant = "",
            createdAt = now,
            updatedAt = now
        )
        val newBalance = calculator.calculateNewBalance(mockWallet, transaction)
        assertEquals(700, newBalance.amount)
    }

    @Test
    fun testTransferSource() {
        val transaction = Transaction(
            id = "t1",
            amount = Money(200, "IDR"),
            type = TransactionType.TRANSFER,
            categoryId = null,
            walletId = "w1",
            targetWalletId = "w2",
            date = today,
            note = "",
            merchant = "",
            createdAt = now,
            updatedAt = now
        )
        val newBalance = calculator.calculateNewBalance(mockWallet, transaction)
        assertEquals(800, newBalance.amount)
    }

    @Test
    fun testTransferDestination() {
        val destWallet = mockWallet.copy(id = "w2", balance = Money(0, "IDR"))
        val transaction = Transaction(
            id = "t1",
            amount = Money(200, "IDR"),
            type = TransactionType.TRANSFER,
            categoryId = null,
            walletId = "w1",
            targetWalletId = "w2",
            date = today,
            note = "",
            merchant = "",
            createdAt = now,
            updatedAt = now
        )
        val newBalance = calculator.calculateNewBalance(destWallet, transaction)
        assertEquals(200, newBalance.amount)
    }

    @Test
    fun testExpenseRollback() {
        val walletAfterExpense = mockWallet.copy(balance = Money(700, "IDR"))
        val transaction = Transaction(
            id = "t1",
            amount = Money(300, "IDR"),
            type = TransactionType.EXPENSE,
            categoryId = null,
            walletId = "w1",
            targetWalletId = null,
            date = today,
            note = "",
            merchant = "",
            createdAt = now,
            updatedAt = now
        )

        val rollbackBalance = calculator.calculateRollbackBalance(walletAfterExpense, transaction)

        assertEquals(1000, rollbackBalance.amount)
    }

    @Test
    fun testTransferRollbackForDestination() {
        val destinationAfterTransfer = mockWallet.copy(id = "w2", balance = Money(200, "IDR"))
        val transaction = Transaction(
            id = "t1",
            amount = Money(200, "IDR"),
            type = TransactionType.TRANSFER,
            categoryId = null,
            walletId = "w1",
            targetWalletId = "w2",
            date = today,
            note = "",
            merchant = "",
            createdAt = now,
            updatedAt = now
        )

        val rollbackBalance = calculator.calculateRollbackBalance(destinationAfterTransfer, transaction)

        assertEquals(0, rollbackBalance.amount)
    }
}
