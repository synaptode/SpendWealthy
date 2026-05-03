package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet

/**
 * Service to calculate wallet balance changes based on transactions.
 */
class BalanceCalculator {

    /**
     * Calculates the new balance of a wallet after applying a transaction.
     */
    fun calculateNewBalance(wallet: Wallet, transaction: Transaction): Money {
        return when (transaction.type) {
            TransactionType.INCOME -> wallet.balance + transaction.amount
            TransactionType.EXPENSE -> wallet.balance - transaction.amount
            TransactionType.TRANSFER -> {
                if (transaction.walletId == wallet.id) {
                    // This is the source wallet
                    wallet.balance - transaction.amount
                } else if (transaction.targetWalletId == wallet.id) {
                    // This is the destination wallet
                    wallet.balance + transaction.amount
                } else {
                    wallet.balance
                }
            }
        }
    }

    /**
     * Calculates the balance change needed when a transaction is deleted (rollback).
     */
    fun calculateRollbackBalance(wallet: Wallet, transaction: Transaction): Money {
        return when (transaction.type) {
            TransactionType.INCOME -> wallet.balance - transaction.amount
            TransactionType.EXPENSE -> wallet.balance + transaction.amount
            TransactionType.TRANSFER -> {
                if (transaction.walletId == wallet.id) {
                    wallet.balance + transaction.amount
                } else if (transaction.targetWalletId == wallet.id) {
                    wallet.balance - transaction.amount
                } else {
                    wallet.balance
                }
            }
        }
    }
}
