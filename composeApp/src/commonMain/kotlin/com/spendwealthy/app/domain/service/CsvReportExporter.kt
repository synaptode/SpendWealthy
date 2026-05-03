package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.model.Transaction

class CsvReportExporter {

    fun exportTransactions(
        transactions: List<Transaction>,
        categories: List<Category>
    ): String {
        val categoryById = categories.associateBy { it.id }
        val rows = buildList {
            add(
                listOf(
                    "date",
                    "type",
                    "amount",
                    "currency",
                    "category",
                    "walletId",
                    "targetWalletId",
                    "merchant",
                    "note"
                )
            )
            transactions.sortedWith(compareBy<Transaction> { it.date }.thenBy { it.createdAt }).forEach { transaction ->
                add(
                    listOf(
                        transaction.date.toString(),
                        transaction.type.name,
                        transaction.amount.amount.toString(),
                        transaction.amount.currency,
                        transaction.categoryId?.let { categoryById[it]?.name } ?: "",
                        transaction.walletId,
                        transaction.targetWalletId.orEmpty(),
                        transaction.merchant,
                        transaction.note
                    )
                )
            }
        }

        return rows.joinToString(separator = "\n") { columns ->
            columns.joinToString(separator = ",") { it.escapeCsvCell() }
        }
    }

    private fun String.escapeCsvCell(): String {
        val escaped = replace("\"", "\"\"")
        return if (any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"$escaped\""
        } else {
            escaped
        }
    }
}
