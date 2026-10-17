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
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.repository.FamilyRepository

data class MemberContribution(
    val memberId: String,
    val memberName: String,
    val amount: Long
)

data class FamilyGoalTransaction(
    val id: String,
    val memberName: String,
    val amount: Long,
    val date: String,
    val note: String,
    val sourceWallet: String = "Rekening Bank"
)

data class FamilySavingsGoal(
    val id: String,
    val title: String,
    val targetAmount: Long,
    val currentAmount: Long,
    val targetDate: String,
    val category: String, // "Holiday", "Education", "Property", "Emergency"
    val contributions: List<MemberContribution>,
    val transactions: List<FamilyGoalTransaction> = emptyList()
)

data class FamilyState(
    val family: Family? = null,
    val sharedGoals: List<FamilySavingsGoal> = emptyList(),
    val activities: List<FamilyActivity> = emptyList(),
    val isLoading: Boolean = false,
    val showInviteDialog: Boolean = false,
    val showContributeDialog: Boolean = false,
    val showCreateGoalDialog: Boolean = false,
    val selectedGoalForContribution: FamilySavingsGoal? = null,
    val selectedGoalForDetail: FamilySavingsGoal? = null,
    val selectedTab: Int = 0, // 0: Target Bersama, 1: Anggota, 2: Aktivitas
    val totalSavingsAmount: Long = 0L,
    val totalTargetAmount: Long = 0L
) : UiState

data class FamilyActivity(
    val id: String,
    val type: ActivityType,
    val memberName: String,
    val timestamp: kotlinx.datetime.Instant,
    val details: String
)

enum class ActivityType {
    MEMBER_JOINED, TRANSACTION_ADDED, ROLE_CHANGED, GOAL_REACHED, CONTRIBUTION_ADDED
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

            val initialGoals = listOf(
                FamilySavingsGoal(
                    id = "fg_1",
                    title = "Liburan Keluarga Akhir Tahun",
                    targetAmount = 40_000_000L,
                    currentAmount = 28_000_000L,
                    targetDate = "Desember 2026",
                    category = "Holiday",
                    contributions = listOf(
                        MemberContribution("1", "Alex Pratama", 16_000_000L),
                        MemberContribution("2", "Sarah Pratama", 12_000_000L)
                    ),
                    transactions = listOf(
                        FamilyGoalTransaction("tx_1_1", "Sarah Pratama", 2_000_000L, "17 Okt 2026, 14:30", "Setoran rutin tengah bulan", "Bank Jago"),
                        FamilyGoalTransaction("tx_1_2", "Alex Pratama", 6_000_000L, "01 Okt 2026, 09:15", "Bonus kuartal Q3", "Rekening BCA"),
                        FamilyGoalTransaction("tx_1_3", "Sarah Pratama", 5_000_000L, "25 Sep 2026, 18:20", "Alokasi gaji September", "Mandiri"),
                        FamilyGoalTransaction("tx_1_4", "Alex Pratama", 10_000_000L, "01 Sep 2026, 10:00", "Setoran awal tiket pesawat & hotel", "Rekening BCA"),
                        FamilyGoalTransaction("tx_1_5", "Sarah Pratama", 5_000_000L, "15 Agu 2026, 11:45", "Inisiasi tabungan liburan akhir tahun", "Bank Jago")
                    )
                ),
                FamilySavingsGoal(
                    id = "fg_2",
                    title = "Dana Pendidikan Anak",
                    targetAmount = 75_000_000L,
                    currentAmount = 52_500_000L,
                    targetDate = "Juli 2027",
                    category = "Education",
                    contributions = listOf(
                        MemberContribution("1", "Alex Pratama", 32_500_000L),
                        MemberContribution("2", "Sarah Pratama", 20_000_000L)
                    ),
                    transactions = listOf(
                        FamilyGoalTransaction("tx_2_1", "Alex Pratama", 5_000_000L, "15 Okt 2026, 10:00", "Alokasi tabungan semester genap", "Rekening BCA"),
                        FamilyGoalTransaction("tx_2_2", "Sarah Pratama", 10_000_000L, "28 Sep 2026, 16:30", "Tabungan biaya pendaftaran sekolah", "Mandiri"),
                        FamilyGoalTransaction("tx_2_3", "Alex Pratama", 15_000_000L, "01 Sep 2026, 08:30", "Setoran dana pendidikan kuartal 3", "Rekening BCA"),
                        FamilyGoalTransaction("tx_2_4", "Sarah Pratama", 10_000_000L, "15 Agu 2026, 12:00", "Gaji Sarah alokasi pendidikan", "Mandiri"),
                        FamilyGoalTransaction("tx_2_5", "Alex Pratama", 12_500_000L, "01 Agu 2026, 09:00", "Inisiasi tabungan pendidikan anak", "Rekening BCA")
                    )
                ),
                FamilySavingsGoal(
                    id = "fg_3",
                    title = "Dana Darurat Bersama (6 Bulan)",
                    targetAmount = 60_000_000L,
                    currentAmount = 45_000_000L,
                    targetDate = "Berkelanjutan",
                    category = "Emergency",
                    contributions = listOf(
                        MemberContribution("1", "Alex Pratama", 25_000_000L),
                        MemberContribution("2", "Sarah Pratama", 20_000_000L)
                    ),
                    transactions = listOf(
                        FamilyGoalTransaction("tx_3_1", "Sarah Pratama", 10_000_000L, "05 Okt 2026, 13:10", "Top-up bantalan likuiditas keluarga", "Bank Jago"),
                        FamilyGoalTransaction("tx_3_2", "Alex Pratama", 15_000_000L, "25 Sep 2026, 10:45", "Alokasi emergency safety buffer", "Rekening BCA"),
                        FamilyGoalTransaction("tx_3_3", "Sarah Pratama", 10_000_000L, "01 Sep 2026, 15:20", "Setoran bulanan dana cadangan", "Mandiri"),
                        FamilyGoalTransaction("tx_3_4", "Alex Pratama", 10_000_000L, "10 Agu 2026, 09:30", "Inisiasi dana darurat 6 bulan", "Rekening BCA")
                    )
                )
            )

            val initialActivities = listOf(
                FamilyActivity(
                    id = "act_1",
                    type = ActivityType.CONTRIBUTION_ADDED,
                    memberName = "Sarah Pratama",
                    timestamp = Clock.System.now(),
                    details = "Menyetor Rp 2.000.000 ke 'Liburan Keluarga Akhir Tahun'"
                ),
                FamilyActivity(
                    id = "act_2",
                    type = ActivityType.CONTRIBUTION_ADDED,
                    memberName = "Alex Pratama",
                    timestamp = Clock.System.now(),
                    details = "Menyetor Rp 5.000.000 ke 'Dana Pendidikan Anak'"
                ),
                FamilyActivity(
                    id = "act_3",
                    type = ActivityType.MEMBER_JOINED,
                    memberName = "Sarah Pratama",
                    timestamp = Clock.System.now(),
                    details = "Bergabung ke Ruang Tabungan Keluarga"
                ),
                FamilyActivity(
                    id = "act_4",
                    type = ActivityType.ROLE_CHANGED,
                    memberName = "Alex Pratama",
                    timestamp = Clock.System.now(),
                    details = "Mengatur Sarah Pratama sebagai Pengelola Keuangan (Admin)"
                )
            )

            familyRepository.getFamily().collectLatest { family ->
                val totalSavings = initialGoals.sumOf { it.currentAmount }
                val totalTarget = initialGoals.sumOf { it.targetAmount }

                _uiState.update { state ->
                    state.copy(
                        family = family ?: Family(
                            id = "fam_1",
                            name = "Keluarga Pratama",
                            members = listOf(
                                FamilyMember("1", "Alex Pratama", "alex@pratama.id", FamilyRole.ADMIN, null),
                                FamilyMember("2", "Sarah Pratama", "sarah@pratama.id", FamilyRole.ADMIN, null)
                            ),
                            sharedWalletIds = listOf("w1"),
                            createdAt = Clock.System.now()
                        ),
                        sharedGoals = state.sharedGoals.ifEmpty { initialGoals },
                        activities = state.activities.ifEmpty { initialActivities },
                        totalSavingsAmount = totalSavings,
                        totalTargetAmount = totalTarget,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun onOpenContributeDialog(goal: FamilySavingsGoal? = null) {
        _uiState.update {
            it.copy(
                showContributeDialog = true,
                selectedGoalForContribution = goal ?: it.sharedGoals.firstOrNull()
            )
        }
    }

    fun onDismissContributeDialog() {
        _uiState.update { it.copy(showContributeDialog = false, selectedGoalForContribution = null) }
    }

    fun onSelectGoalForDetail(goal: FamilySavingsGoal) {
        _uiState.update { it.copy(selectedGoalForDetail = goal) }
    }

    fun onDismissGoalDetail() {
        _uiState.update { it.copy(selectedGoalForDetail = null) }
    }

    fun submitContribution(goalId: String, memberName: String, amount: Long) {
        screenModelScope.launch {
            _uiState.update { state ->
                val newTx = FamilyGoalTransaction(
                    id = "tx_${Clock.System.now().toEpochMilliseconds()}",
                    memberName = memberName,
                    amount = amount,
                    date = "Hari ini, 19:45",
                    note = "Setoran langsung ke target",
                    sourceWallet = "Rekening Tabungan"
                )

                val updatedGoals = state.sharedGoals.map { goal ->
                    if (goal.id == goalId) {
                        val newCurrent = goal.currentAmount + amount
                        val existingContribution = goal.contributions.find { it.memberName == memberName }
                        val updatedContributions = if (existingContribution != null) {
                            goal.contributions.map { c ->
                                if (c.memberName == memberName) c.copy(amount = c.amount + amount)
                                else c
                            }
                        } else {
                            goal.contributions + MemberContribution("m_${Clock.System.now().epochSeconds}", memberName, amount)
                        }
                        goal.copy(
                            currentAmount = newCurrent,
                            contributions = updatedContributions,
                            transactions = listOf(newTx) + goal.transactions
                        )
                    } else {
                        goal
                    }
                }

                val targetGoal = state.sharedGoals.find { it.id == goalId }
                val goalTitle = targetGoal?.title ?: "Target Keluarga"
                val newActivity = FamilyActivity(
                    id = "act_${Clock.System.now().toEpochMilliseconds()}",
                    type = ActivityType.CONTRIBUTION_ADDED,
                    memberName = memberName,
                    timestamp = Clock.System.now(),
                    details = "Menyetor ${Money(amount, "IDR").formatWithSymbol()} ke '$goalTitle'"
                )

                val newTotalSavings = updatedGoals.sumOf { it.currentAmount }
                val updatedDetailGoal = updatedGoals.find { it.id == state.selectedGoalForDetail?.id }

                state.copy(
                    sharedGoals = updatedGoals,
                    activities = listOf(newActivity) + state.activities,
                    totalSavingsAmount = newTotalSavings,
                    showContributeDialog = false,
                    selectedGoalForContribution = null,
                    selectedGoalForDetail = updatedDetailGoal ?: state.selectedGoalForDetail
                )
            }
        }
    }

    fun onOpenCreateGoalDialog() {
        _uiState.update { it.copy(showCreateGoalDialog = true) }
    }

    fun onDismissCreateGoalDialog() {
        _uiState.update { it.copy(showCreateGoalDialog = false) }
    }

    fun createSharedGoal(title: String, targetAmount: Long, targetDate: String, category: String) {
        screenModelScope.launch {
            _uiState.update { state ->
                val newGoal = FamilySavingsGoal(
                    id = "fg_${Clock.System.now().toEpochMilliseconds()}",
                    title = title,
                    targetAmount = targetAmount,
                    currentAmount = 0L,
                    targetDate = targetDate.ifBlank { "Akhir Tahun" },
                    category = category,
                    contributions = emptyList()
                )

                val newActivity = FamilyActivity(
                    id = "act_${Clock.System.now().toEpochMilliseconds()}",
                    type = ActivityType.GOAL_REACHED,
                    memberName = state.family?.members?.firstOrNull()?.name ?: "Alex Pratama",
                    timestamp = Clock.System.now(),
                    details = "Menambahkan target tabungan bersama baru: '$title'"
                )

                val updatedGoals = state.sharedGoals + newGoal
                state.copy(
                    sharedGoals = updatedGoals,
                    totalTargetAmount = updatedGoals.sumOf { it.targetAmount },
                    activities = listOf(newActivity) + state.activities,
                    showCreateGoalDialog = false
                )
            }
        }
    }

    fun simulateMemberJoin(name: String, email: String, role: FamilyRole = FamilyRole.MEMBER) {
        screenModelScope.launch {
            val newMember = FamilyMember(
                id = "m_${Clock.System.now().toEpochMilliseconds()}",
                name = name,
                email = email,
                role = role,
                avatarUrl = null
            )
            familyRepository.joinFamily(_uiState.value.family?.id ?: "fam_1", newMember)

            _uiState.update { state ->
                val newActivity = FamilyActivity(
                    id = "act_${Clock.System.now().toEpochMilliseconds()}",
                    type = ActivityType.MEMBER_JOINED,
                    memberName = name,
                    timestamp = Clock.System.now(),
                    details = "Bergabung ke Ruang Tabungan Keluarga sebagai ${role.name.lowercase().replaceFirstChar { it.uppercase() }}"
                )
                val updatedFamily = state.family?.copy(members = state.family.members + newMember)
                state.copy(
                    family = updatedFamily,
                    activities = listOf(newActivity) + state.activities,
                    showInviteDialog = false
                )
            }
        }
    }

    fun changeMemberRole(memberId: String, newRole: FamilyRole) {
        screenModelScope.launch {
            _uiState.update { state ->
                val targetMember = state.family?.members?.find { it.id == memberId }
                val updatedFamily = state.family?.let { fam ->
                    fam.copy(members = fam.members.map { m ->
                        if (m.id == memberId) m.copy(role = newRole)
                        else m
                    })
                }
                val newActivity = FamilyActivity(
                    id = "act_${Clock.System.now().toEpochMilliseconds()}",
                    type = ActivityType.ROLE_CHANGED,
                    memberName = "Admin",
                    timestamp = Clock.System.now(),
                    details = "Mengubah peran ${targetMember?.name ?: "Anggota"} menjadi ${newRole.name}"
                )
                state.copy(family = updatedFamily, activities = listOf(newActivity) + state.activities)
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
