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
    val updatedAt: Instant
)

@Serializable
data class Wallet(
    val id: String,
    val name: String,
    val balance: Money,
    val type: WalletType,
    val createdAt: Instant,
    val isArchived: Boolean
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
    INVOICE, WARRANTY, CONTRACT, OTHER
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
