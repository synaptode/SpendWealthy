package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.spendwealthy.app.data.mapper.toDomain
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.repository.GoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightGoalRepository(
    private val db: AppDatabase
) : GoalRepository {

    private val queries = db.appDatabaseQueries

    override fun getGoals(): Flow<List<Goal>> {
        return queries.selectAllGoals()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getGoalById(id: String): Flow<Goal?> {
        return queries.selectGoalById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun insertGoal(goal: Goal) {
        queries.insertGoal(
            id = goal.id,
            name = goal.name,
            targetAmount = goal.targetAmount.amount,
            currentAmount = goal.currentAmount.amount,
            currency = goal.targetAmount.currency,
            type = goal.type.name,
            deadline = goal.deadline,
            createdAt = goal.createdAt
        )
    }

    override suspend fun updateGoalProgress(id: String, currentAmount: Long) {
        queries.updateGoalProgress(currentAmount, id)
    }

    override suspend fun deleteGoal(id: String) {
        queries.deleteGoal(id)
    }
}
