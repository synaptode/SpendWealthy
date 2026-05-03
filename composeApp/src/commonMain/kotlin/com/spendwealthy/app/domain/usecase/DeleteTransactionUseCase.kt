package com.spendwealthy.app.domain.usecase

import com.spendwealthy.app.domain.repository.LedgerRepository

class DeleteTransactionUseCase(
    private val ledgerRepository: LedgerRepository
) {
    suspend operator fun invoke(transactionId: String) {
        ledgerRepository.deleteTransactionAndRollbackBalance(transactionId)
    }
}
