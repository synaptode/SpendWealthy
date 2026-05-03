package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
    fun getTransactionsByWallet(walletId: String): Flow<List<Transaction>>
    fun getTransactionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>>
    fun getTransactionById(id: String): Flow<Transaction?>
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)
}
