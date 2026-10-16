package com.spendwealthy.app.ui.screens.vault

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.domain.model.Document
import com.spendwealthy.app.domain.model.Badge
import com.spendwealthy.app.domain.repository.DocumentRepository
import com.spendwealthy.app.domain.repository.SubscriptionRepository
import kotlinx.datetime.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VaultUiState(
    val documents: List<Document> = emptyList(),
    val expiringSoon: List<Document> = emptyList(),
    val securityScore: Int = 0,
    val currentPlan: String = "Free",
    val badges: List<Badge> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class VaultViewModel(
    private val repository: DocumentRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDocuments()
        observeSubscription()
        loadBadges()
    }

    private fun loadBadges() {
        _uiState.update { 
            it.copy(
                badges = listOf(
                    Badge("1", "Family First", "Joined a shared space", "🏠", "#10B981", Clock.System.now()),
                    Badge("2", "Goal Getter", "Finished your first goal", "🎯", "#8B5CF6", Clock.System.now()),
                    Badge("3", "Automation Pro", "Set up an auto-move", "⚡", "#F59E0B", Clock.System.now()),
                    Badge("4", "Family Power", "Pooled funds for a goal", "👑", "#FCD34D", Clock.System.now())
                )
            )
        }
    }

    private fun observeSubscription() {
        screenModelScope.launch {
            subscriptionRepository.getEntitlement().collect { entitlement ->
                _uiState.update { 
                    it.copy(currentPlan = if (entitlement.isActive) entitlement.plan.name.lowercase().replaceFirstChar { it.uppercase() } else "Free")
                }
            }
        }
    }

    private fun loadDocuments() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val now = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
            
            repository.getAllDocuments()
                .onEach { docs ->
                    val expiring = docs.filter { doc ->
                        doc.expiryDate?.let { now.daysUntil(it) in 0..30 } ?: false
                    }
                    
                    // Calculate a simple security score
                    // +20 per document, +10 if it has expiry date, capped at 100
                    val baseScore = (docs.size * 20).coerceAtMost(60)
                    val hygieneScore = (docs.count { it.expiryDate != null } * 10).coerceAtMost(40)
                    
                    _uiState.update { 
                        it.copy(
                            documents = docs,
                            expiringSoon = expiring,
                            securityScore = baseScore + hygieneScore,
                            isLoading = false
                        )
                    }
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
