package com.spendwealthy.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.spendwealthy.app.core.architecture.DefaultDispatcherProvider
import com.spendwealthy.app.core.architecture.DispatcherProvider
import com.spendwealthy.app.db.AppDatabase
import com.spendwealthy.app.domain.model.Document
import com.spendwealthy.app.domain.model.DocumentType
import com.spendwealthy.app.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightDocumentRepository(
    private val db: AppDatabase,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : DocumentRepository {

    private val queries = db.appDatabaseQueries

    override fun getAllDocuments(): Flow<List<Document>> {
        return queries.selectAllVaultDocuments()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { entities ->
                entities.map { entity ->
                    Document(
                        id = entity.id,
                        name = entity.name,
                        type = DocumentType.entries.firstOrNull { it.name == entity.type } ?: DocumentType.OTHER,
                        filePath = entity.filePath,
                        expiryDate = entity.expiryDate,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                }
            }
    }

    override fun getDocumentById(id: String): Flow<Document?> {
        return getAllDocuments().map { docs -> docs.find { it.id == id } }
    }

    override suspend fun saveDocument(document: Document) {
        queries.insertVaultDocument(
            id = document.id,
            name = document.name,
            type = document.type.name,
            filePath = document.filePath,
            expiryDate = document.expiryDate,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt
        )
    }

    override suspend fun deleteDocument(id: String) {
        queries.deleteVaultDocument(id)
    }
}
