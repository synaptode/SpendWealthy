package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.spendwealthy.app.data.mapper.toDomain
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.repository.WalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WalletRepositoryImpl(
    private val db: AppDatabase
) : WalletRepository {

    private val queries = db.appDatabaseQueries

    override fun getWallets(): Flow<List<Wallet>> {
        return queries.selectAllWallets()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getWalletById(id: String): Flow<Wallet?> {
        return queries.selectWalletById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun insertWallet(wallet: Wallet) {
        queries.insertWallet(
            id = wallet.id,
            name = wallet.name,
            balanceAmount = wallet.balance.amount,
            currency = wallet.balance.currency,
            type = wallet.type.name,
            createdAt = wallet.createdAt,
            isArchived = wallet.isArchived,
            familyId = wallet.familyId
        )
    }

    override suspend fun deleteWallet(id: String) {
        // Implement soft delete or physical delete as needed
        // For now, let's just archive it as a simple implementation
        archiveWallet(id)
    }

    override suspend fun archiveWallet(id: String) {
        val wallet = queries.selectWalletById(id).executeAsOneOrNull()
        if (wallet != null) {
            queries.insertWallet(
                id = wallet.id,
                name = wallet.name,
                balanceAmount = wallet.balanceAmount,
                currency = wallet.currency,
                type = wallet.type,
                createdAt = wallet.createdAt,
                isArchived = true,
                familyId = wallet.familyId
            )
        }
    }
}
