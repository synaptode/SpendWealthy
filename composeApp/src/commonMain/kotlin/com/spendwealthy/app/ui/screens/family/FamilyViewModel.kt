package com.spendwealthy.app.ui.screens.family

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.Family
import com.spendwealthy.app.domain.model.FamilyMember
import com.spendwealthy.app.domain.model.FamilyRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import com.spendwealthy.app.domain.repository.FamilyRepository

data class FamilyState(
    val family: Family? = null,
    val activities: List<FamilyActivity> = emptyList(),
    val familyGoals: List<com.spendwealthy.app.domain.model.Goal> = emptyList(),
    val isLoading: Boolean = false,
    val showInviteDialog: Boolean = false,
    val selectedTab: Int = 0
) : UiState

data class FamilyActivity(
    val id: String,
    val type: ActivityType,
    val memberName: String,
    val timestamp: kotlinx.datetime.Instant,
    val details: String
)

enum class ActivityType {
    MEMBER_JOINED, TRANSACTION_ADDED, ROLE_CHANGED, GOAL_REACHED
}

class FamilyViewModel(
    private val familyRepository: com.spendwealthy.app.domain.repository.FamilyRepository,
    private val goalRepository: com.spendwealthy.app.domain.repository.GoalRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(FamilyState())
    val uiState: StateFlow<FamilyState> = _uiState.asStateFlow()

    init {
        loadFamily()
    }

    private fun loadFamily() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            launch {
                goalRepository.getGoals().collectLatest { goals ->
                    _uiState.update { it.copy(familyGoals = goals.filter { g -> g.targetAmount.amount > 10_000_000L }) } // Assume large goals are family-related for now
                }
            }

            familyRepository.getFamily().collectLatest { family ->
                _uiState.update { state ->
                    state.copy(
                        family = family,
                        activities = listOf(
                            FamilyActivity("1", ActivityType.MEMBER_JOINED, "Sarah", Clock.System.now(), "Joined the family space"),
                            FamilyActivity("2", ActivityType.TRANSACTION_ADDED, "Sarah", Clock.System.now(), "Added 'Family Pizza Night' (Rp 75.000)"),
                            FamilyActivity("3", ActivityType.ROLE_CHANGED, "Rakha", Clock.System.now(), "Promoted Sarah to Admin")
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun simulateMemberJoin(name: String, email: String) {
        screenModelScope.launch {
            val newMember = FamilyMember(
                id = "m_${Clock.System.now().toEpochMilliseconds()}",
                name = name,
                email = email,
                role = FamilyRole.MEMBER,
                avatarUrl = null
            )
            familyRepository.joinFamily(_uiState.value.family?.id ?: "fam_1", newMember)
        }
    }

    fun simulateGoalReached(goalName: String) {
        screenModelScope.launch {
            _uiState.update { state ->
                val newActivity = FamilyActivity(
                    id = "ga_${Clock.System.now().toEpochMilliseconds()}",
                    type = ActivityType.GOAL_REACHED,
                    memberName = "System",
                    timestamp = Clock.System.now(),
                    details = "Shared Goal '$goalName' has been achieved! 🥳"
                )
                state.copy(activities = listOf(newActivity) + state.activities)
            }
        }
    }

    fun changeMemberRole(memberId: String, newRole: FamilyRole) {
        screenModelScope.launch {
            _uiState.update { state ->
                val updatedFamily = state.family?.let { fam ->
                    fam.copy(members = fam.members.map { m ->
                        if (m.id == memberId) m.copy(role = newRole)
                        else m
                    })
                }
                state.copy(family = updatedFamily)
            }
        }
    }

    fun onInviteClicked() {
        _uiState.update { it.copy(showInviteDialog = true) }
    }

    fun dismissInvite() {
        _uiState.update { it.copy(showInviteDialog = false) }
    }
}
