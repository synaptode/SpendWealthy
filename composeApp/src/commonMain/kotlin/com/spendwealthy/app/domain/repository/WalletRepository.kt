package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getWallets(): Flow<List<Wallet>>
    fun getWalletById(id: String): Flow<Wallet?>
    suspend fun insertWallet(wallet: Wallet)
    suspend fun deleteWallet(id: String)
    suspend fun archiveWallet(id: String)
}
