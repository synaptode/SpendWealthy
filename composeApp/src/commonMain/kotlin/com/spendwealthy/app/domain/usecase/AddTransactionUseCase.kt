package com.spendwealthy.app.domain.usecase

import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.repository.LedgerRepository

class AddTransactionUseCase(
    private val ledgerRepository: LedgerRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        require(transaction.amount.isPositive()) { "Nominal transaksi harus lebih dari 0." }
        if (transaction.type == TransactionType.TRANSFER) {
            require(!transaction.targetWalletId.isNullOrBlank()) { "Transfer membutuhkan dompet tujuan." }
            require(transaction.walletId != transaction.targetWalletId) { "Dompet sumber dan tujuan transfer harus berbeda." }
        }

        ledgerRepository.insertTransactionAndApplyBalance(transaction)
    }
}
