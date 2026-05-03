package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.spendwealthy.app.data.mapper.toDomain
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.repository.BudgetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl(
    private val db: AppDatabase
) : BudgetRepository {

    private val queries = db.appDatabaseQueries

    override fun getBudgets(month: Int, year: Int): Flow<List<Budget>> {
        return queries.selectBudgetsByPeriod(month, year)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun insertBudget(budget: Budget) {
        queries.insertBudget(
            id = budget.id,
            categoryId = budget.categoryId,
            limitAmount = budget.limit.amount,
            currency = budget.limit.currency,
            month = budget.month,
            year = budget.year,
            createdAt = budget.createdAt
        )
    }

    override suspend fun deleteBudget(id: String) {
        queries.deleteBudget(id)
    }
}
