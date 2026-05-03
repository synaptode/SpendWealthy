package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category)
    suspend fun seedDefaultCategories()
}
