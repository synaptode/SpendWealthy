package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.Wallet
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class FamilyDataDump(
    val wallets: List<Wallet>,
    val transactions: List<Transaction>,
    val version: Int = 1,
    val exportedAt: Long = 0
)

class DataService {
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun exportToJson(wallets: List<Wallet>, transactions: List<Transaction>): String {
        val dump = FamilyDataDump(
            wallets = wallets,
            transactions = transactions,
            exportedAt = 0 // Should be current time
        )
        return json.encodeToString(dump)
    }

    fun importFromJson(jsonString: String): FamilyDataDump {
        return json.decodeFromString(jsonString)
    }
    
    fun exportToCsv(transactions: List<Transaction>): String {
        val header = "Date,Amount,Type,Category,Merchant,Note\n"
        val rows = transactions.joinToString("\n") { 
            "${it.date},${it.amount.amount},${it.type},${it.categoryId ?: ""},${it.merchant},${it.note}"
        }
        return header + rows
    }
}
