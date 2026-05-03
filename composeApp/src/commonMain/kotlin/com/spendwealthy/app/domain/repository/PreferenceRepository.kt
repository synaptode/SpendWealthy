package com.spendwealthy.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun getString(key: String): Flow<String?>
    suspend fun setString(key: String, value: String)
    fun getBoolean(key: String): Flow<Boolean>
    suspend fun setBoolean(key: String, value: Boolean)
    fun getInt(key: String): Flow<Int>
    suspend fun setInt(key: String, value: Int)
}
