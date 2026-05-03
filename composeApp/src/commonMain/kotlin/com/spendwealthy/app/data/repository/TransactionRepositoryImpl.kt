package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.spendwealthy.app.data.mapper.toDomain
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class TransactionRepositoryImpl(
    private val db: AppDatabase
) : TransactionRepository {

    private val queries = db.appDatabaseQueries

    override fun getTransactions(): Flow<List<Transaction>> {
        return queries.selectAllTransactions()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTransactionsByWallet(walletId: String): Flow<List<Transaction>> {
        return queries.selectTransactionsByWallet(walletId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> {
        return queries.selectTransactionsByDateRange(start, end)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTransactionById(id: String): Flow<Transaction?> {
        return queries.selectTransactionById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
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
    }

    override suspend fun deleteTransaction(id: String) {
        queries.deleteTransaction(id)
    }
}
