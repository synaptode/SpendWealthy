package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.spendwealthy.app.core.architecture.DefaultDispatcherProvider
import com.spendwealthy.app.core.architecture.DispatcherProvider
import com.spendwealthy.app.data.mapper.toDomain
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl(
    private val db: AppDatabase,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : BudgetRepository {

    private val queries = db.appDatabaseQueries

    override fun getBudgets(month: Int, year: Int): Flow<List<Budget>> {
        return queries.selectBudgetsByPeriod(month, year)
            .asFlow()
            .mapToList(dispatchers.io)
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
