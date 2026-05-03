package com.spendwealthy.app.data.repository

import com.spendwealthy.app.domain.model.Document
import com.spendwealthy.app.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MemoryDocumentRepository : DocumentRepository {
    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    
    override fun getAllDocuments(): Flow<List<Document>> = _documents.asStateFlow()

    override fun getDocumentById(id: String): Flow<Document?> = 
        _documents.map { docs -> docs.find { it.id == id } }

    override suspend fun saveDocument(document: Document) {
        val current = _documents.value.toMutableList()
        val index = current.indexOfFirst { it.id == document.id }
        if (index >= 0) {
            current[index] = document
        } else {
            current.add(document)
        }
        _documents.value = current
    }

    override suspend fun deleteDocument(id: String) {
        _documents.value = _documents.value.filterNot { it.id == id }
    }
}
