package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoals(): Flow<List<Goal>>
    fun getGoalById(id: String): Flow<Goal?>
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoalProgress(id: String, currentAmount: Long)
    suspend fun deleteGoal(id: String)
}
