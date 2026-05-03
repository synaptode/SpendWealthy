package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgets(month: Int, year: Int): Flow<List<Budget>>
    suspend fun insertBudget(budget: Budget)
    suspend fun deleteBudget(id: String)
}
