package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.Transaction

interface LedgerRepository {
    suspend fun insertTransactionAndApplyBalance(transaction: Transaction)
    suspend fun deleteTransactionAndRollbackBalance(transactionId: String)
}
