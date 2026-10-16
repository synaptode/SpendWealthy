package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.spendwealthy.app.core.architecture.DefaultDispatcherProvider
import com.spendwealthy.app.core.architecture.DispatcherProvider
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceRepositoryImpl(
    private val db: AppDatabase,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : PreferenceRepository {

    private val queries = db.appDatabaseQueries

    override fun getString(key: String): Flow<String?> {
        return queries.selectPreference(key)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
    }

    override suspend fun setString(key: String, value: String) {
        queries.insertPreference(key, value)
    }

    override fun getBoolean(key: String): Flow<Boolean> {
        return getString(key).map { it == "true" }
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        setString(key, value.toString())
    }

    override fun getInt(key: String): Flow<Int> {
        return getString(key).map { it?.toIntOrNull() ?: 0 }
    }

    override suspend fun setInt(key: String, value: Int) {
        setString(key, value.toString())
    }
}
