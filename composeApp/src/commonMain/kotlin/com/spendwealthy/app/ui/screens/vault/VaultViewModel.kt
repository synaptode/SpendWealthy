package com.spendwealthy.app.ui.screens.vault

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.domain.model.Document
import com.spendwealthy.app.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VaultUiState(
    val documents: List<Document> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class VaultViewModel(
    private val repository: DocumentRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAllDocuments()
                .onEach { docs ->
                    _uiState.update { it.copy(documents = docs, isLoading = false) }
                }
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect()
        }
    }

    fun deleteDocument(id: String) {
        screenModelScope.launch {
            repository.deleteDocument(id)
        }
    }
}
