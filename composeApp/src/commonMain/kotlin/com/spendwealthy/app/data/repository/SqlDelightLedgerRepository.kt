package com.spendwealthy.app.data.repository

import com.spendwealthy.app.data.mapper.toDomain
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.repository.LedgerRepository
import com.spendwealthy.app.domain.service.BalanceCalculator

class SqlDelightLedgerRepository(
    private val db: AppDatabase,
    private val balanceCalculator: BalanceCalculator
) : LedgerRepository {

    private val queries = db.appDatabaseQueries

    override suspend fun insertTransactionAndApplyBalance(transaction: Transaction) {
        queries.transaction {
            queries.insertTransaction(
                id = transaction.id,
                amount = transaction.amount.amount,
                currency = transaction.amount.currency,
                type = transaction.type.name,
                categoryId = transaction.categoryId,
                walletId = transaction.walletId,
                targetWalletId = transaction.targetWalletId,
                date = transaction.date,
                note = transaction.note,
                merchant = transaction.merchant,
                createdAt = transaction.createdAt,
                updatedAt = transaction.updatedAt
            )

            val sourceWallet = queries.selectWalletById(transaction.walletId).executeAsOneOrNull()
                ?.toDomain()
                ?: error("Dompet sumber tidak ditemukan.")
            queries.updateWalletBalance(
                balanceAmount = balanceCalculator.calculateNewBalance(sourceWallet, transaction).amount,
                id = sourceWallet.id
            )

            if (transaction.type == TransactionType.TRANSFER && transaction.targetWalletId != null) {
                val targetWallet = queries.selectWalletById(transaction.targetWalletId).executeAsOneOrNull()
                    ?.toDomain()
                    ?: error("Dompet tujuan tidak ditemukan.")
                queries.updateWalletBalance(
                    balanceAmount = balanceCalculator.calculateNewBalance(targetWallet, transaction).amount,
                    id = targetWallet.id
                )
            }
        }
    }

    override suspend fun deleteTransactionAndRollbackBalance(transactionId: String) {
        queries.transaction {
            val transaction = queries.selectTransactionById(transactionId).executeAsOneOrNull()
                ?.toDomain()
                ?: return@transaction

            val sourceWallet = queries.selectWalletById(transaction.walletId).executeAsOneOrNull()
                ?.toDomain()
            if (sourceWallet != null) {
                queries.updateWalletBalance(
                    balanceAmount = balanceCalculator.calculateRollbackBalance(sourceWallet, transaction).amount,
                    id = sourceWallet.id
                )
            }

            if (transaction.type == TransactionType.TRANSFER && transaction.targetWalletId != null) {
                val targetWallet = queries.selectWalletById(transaction.targetWalletId).executeAsOneOrNull()
                    ?.toDomain()
                if (targetWallet != null) {
                    queries.updateWalletBalance(
                        balanceAmount = balanceCalculator.calculateRollbackBalance(targetWallet, transaction).amount,
                        id = targetWallet.id
                    )
                }
            }

            queries.deleteTransaction(transactionId)
        }
    }
}
