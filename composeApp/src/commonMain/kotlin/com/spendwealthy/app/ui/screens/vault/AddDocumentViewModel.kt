package com.spendwealthy.app.ui.screens.vault

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiEffect
import com.spendwealthy.app.core.architecture.UiEvent
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Document
import com.spendwealthy.app.domain.model.DocumentType
import com.spendwealthy.app.domain.repository.DocumentRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class AddDocumentState(
    val name: String = "",
    val type: DocumentType = DocumentType.INVOICE,
    val filePath: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) : UiState

sealed class AddDocumentEvent : UiEvent {
    data class NameChanged(val name: String) : AddDocumentEvent()
    data class TypeChanged(val type: DocumentType) : AddDocumentEvent()
    data class FilePathChanged(val filePath: String) : AddDocumentEvent()
    object SaveClicked : AddDocumentEvent()
}

sealed class AddDocumentEffect : UiEffect {
    object NavigateBack : AddDocumentEffect()
    data class ShowError(val message: String) : AddDocumentEffect()
}

class AddDocumentViewModel(
    private val repository: DocumentRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(AddDocumentState())
    val uiState: StateFlow<AddDocumentState> = _uiState.asStateFlow()

    private val _effect = Channel<AddDocumentEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: AddDocumentEvent) {
        when (event) {
            is AddDocumentEvent.NameChanged -> _uiState.value = _uiState.value.copy(name = event.name, errorMessage = null)
            is AddDocumentEvent.TypeChanged -> _uiState.value = _uiState.value.copy(type = event.type, errorMessage = null)
            is AddDocumentEvent.FilePathChanged -> _uiState.value = _uiState.value.copy(filePath = event.filePath, errorMessage = null)
            AddDocumentEvent.SaveClicked -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            val message = "Document name is required."
            _uiState.value = state.copy(errorMessage = message)
            screenModelScope.launch { _effect.send(AddDocumentEffect.ShowError(message)) }
            return
        }

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val now = Clock.System.now()
                repository.saveDocument(
                    Document(
                        id = "doc_${now.toEpochMilliseconds()}",
                        name = state.name,
                        type = state.type,
                        filePath = state.filePath.ifBlank { "local://${state.name}" },
                        expiryDate = null,
                        createdAt = now,
                        updatedAt = now
                    )
                )
                _effect.send(AddDocumentEffect.NavigateBack)
            } catch (e: Exception) {
                val message = e.message ?: "Document failed to save."
                _uiState.value = _uiState.value.copy(errorMessage = message)
                _effect.send(AddDocumentEffect.ShowError(message))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
