package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.Document
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getAllDocuments(): Flow<List<Document>>
    fun getDocumentById(id: String): Flow<Document?>
    suspend fun saveDocument(document: Document)
    suspend fun deleteDocument(id: String)
}
