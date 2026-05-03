package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.spendwealthy.app.data.mapper.toDomain
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val db: AppDatabase
) : CategoryRepository {

    private val queries = db.appDatabaseQueries

    override fun getCategories(): Flow<List<Category>> {
        return queries.selectAllCategories()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun insertCategory(category: Category) {
        queries.insertCategory(
            id = category.id,
            name = category.name,
            iconName = category.iconName,
            colorHex = category.colorHex,
            isDefault = category.isDefault
        )
    }

    override suspend fun seedDefaultCategories() {
        val defaults = listOf(
            Category("cat_1", "Makanan", "fastfood", "#FF5722", true),
            Category("cat_2", "Transportasi", "directions_bus", "#2196F3", true),
            Category("cat_3", "Belanja", "shopping_cart", "#E91E63", true),
            Category("cat_4", "Hiburan", "movie", "#9C27B0", true),
            Category("cat_5", "Kesehatan", "health_and_safety", "#4CAF50", true)
        )
        defaults.forEach { insertCategory(it) }
    }
}
