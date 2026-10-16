@file:UseSerializers(
    com.spendwealthy.app.core.util.InstantSerializer::class,
    com.spendwealthy.app.core.util.LocalDateSerializer::class
)

package com.spendwealthy.app.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

@Serializable
data class Transaction(
    val id: String,
    val amount: Money,
    val type: TransactionType,
    val categoryId: String?,
    val walletId: String,
    val targetWalletId: String?, // For transfers
    val date: LocalDate,
    val note: String,
    val merchant: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val familyId: String? = null,
    val createdById: String? = null
)

@Serializable
data class Wallet(
    val id: String,
    val name: String,
    val balance: Money,
    val type: WalletType,
    val createdAt: Instant,
    val isArchived: Boolean,
    val familyId: String? = null
)

@Serializable
enum class WalletType {
    CASH, BANK, EWALLET, CREDIT_CARD, PAYLATER, SAVINGS
}

@Serializable
data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isDefault: Boolean
)

@Serializable
data class Budget(
    val id: String,
    val categoryId: String?, // Null means total budget
    val limit: Money,
    val month: Int,
    val year: Int,
    val createdAt: Instant
)

@Serializable
enum class BillStatus {
    UPCOMING, DUE_SOON, OVERDUE, PAID, SKIPPED
}

@Serializable
data class BillReminder(
    val id: String,
    val name: String,
    val amount: Money,
    val categoryId: String?,
    val dueDate: LocalDate,
    val recurrenceType: RecurrenceType,
    val isEnabled: Boolean,
    val lastPaidAt: Instant?
)

@Serializable
enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
}

@Serializable
enum class DocumentType {
    IDENTITY, RECEIPT, INVOICE, WARRANTY, CONTRACT, OTHER
}

@Serializable
data class Document(
    val id: String,
    val name: String,
    val type: DocumentType,
    val filePath: String,
    val expiryDate: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
enum class GoalType {
    SAVINGS, SPENDING_LIMIT, DEBT_REPAYMENT
}

@Serializable
data class Goal(
    val id: String,
    val name: String,
    val targetAmount: Money,
    val currentAmount: Money,
    val type: GoalType,
    val deadline: LocalDate?,
    val createdAt: Instant
)

@Serializable
enum class FamilyRole {
    ADMIN, MEMBER, VIEWER
}

@Serializable
data class FamilyMember(
    val id: String,
    val name: String,
    val email: String,
    val role: FamilyRole,
    val avatarUrl: String?
)

@Serializable
data class Family(
    val id: String,
    val name: String,
    val members: List<FamilyMember>,
    val sharedWalletIds: List<String>,
    val createdAt: Instant
)

@Serializable
data class AutoMove(
    val id: String,
    val name: String,
    val amount: Money,
    val sourceWalletId: String,
    val targetGoalId: String? = null,
    val targetWalletId: String? = null,
    val frequency: RecurrenceType,
    val nextRunDate: LocalDate,
    val isEnabled: Boolean = true
)

@Serializable
data class AutoMoveLog(
    val id: String,
    val autoMoveId: String,
    val amount: Money,
    val status: AutoMoveStatus,
    val executedAt: Instant,
    val message: String? = null
)

@Serializable
enum class AutoMoveStatus {
    SUCCESS, FAILED, PENDING
}

@Serializable
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val colorHex: String,
    val unlockedAt: Instant
)

@Serializable
data class OptimizerRecommendation(
    val id: String,
    val title: String,
    val explanation: String,
    val impactScore: Int, // 1-100
    val actions: List<OptimizationAction>
)

@Serializable
data class OptimizationAction(
    val sourceId: String,
    val targetId: String,
    val amount: Money,
    val type: OptimizationType
)

@Serializable
enum class OptimizationType {
    GOAL_BOOST, BILL_SHIELD, IDLE_CASH_SWEEP
}

@Serializable
data class OptimizationLog(
    val id: String,
    val recommendationId: String,
    val title: String,
    val impactScore: Int,
    val executedAt: Instant
)
