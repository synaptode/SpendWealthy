package com.spendwealthy.app.ui.screens.health

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.domain.model.FinancialBadge
import com.spendwealthy.app.domain.model.FinancialStreak
import com.spendwealthy.app.domain.model.ScoreBoosterMission
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.domain.repository.BillRepository
import com.spendwealthy.app.domain.repository.GoalRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.FinancialGamificationService
import com.spendwealthy.app.domain.service.ScoreBreakdown
import com.spendwealthy.app.domain.service.WealthScore
import com.spendwealthy.app.domain.service.WealthScoreEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class HealthTab {
    BREAKDOWN, MISSIONS, BADGES
}

data class WealthScoreUiState(
    val score: WealthScore = WealthScore(
        score = 742,
        label = "Wealth Professional",
        breakdown = ScoreBreakdown(115, 110, 125, 92, 75),
        delta = 12
    ),
    val streak: FinancialStreak = FinancialStreak(
        currentStreakDays = 7,
        longestStreakDays = 14,
        isStreakMaintainedToday = true,
        message = "🔥 Hebat! 7 hari konsisten menjaga kesadaran finansial!"
    ),
    val missions: List<ScoreBoosterMission> = emptyList(),
    val badges: List<FinancialBadge> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: HealthTab = HealthTab.BREAKDOWN
)

class WealthScoreViewModel(
    private val walletRepository: WalletRepository,
    private val billRepository: BillRepository,
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository,
    private val scoreEngine: WealthScoreEngine,
    private val gamificationService: FinancialGamificationService,
    private val clock: Clock = Clock.System
) : ScreenModel {

    private val _uiState = MutableStateFlow(WealthScoreUiState(isLoading = true))
    val uiState: StateFlow<WealthScoreUiState> = _uiState.asStateFlow()

    init {
        loadHealthData()
    }

    private fun loadHealthData() {
        screenModelScope.launch {
            combine(
                walletRepository.getWallets(),
                billRepository.getBillReminders(),
                goalRepository.getGoals(),
                transactionRepository.getTransactions()
            ) { wallets, bills, goals, transactions ->
                val today = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val streak = gamificationService.calculateStreak(transactions, today)

                val activeWallets = wallets.filterNot { it.isArchived }
                val liquidCash = activeWallets
                    .filter { it.type != WalletType.CREDIT_CARD && it.type != WalletType.PAYLATER }
                    .sumOf { it.balance.amount }

                val monthlyExpenses = 5_000_000L // Baseline benchmark
                val runwayDays = if (monthlyExpenses > 0) ((liquidCash.toDouble() / monthlyExpenses) * 30).toInt() else 180

                val calculatedScore = scoreEngine.calculateScore(
                    resilienceDays = runwayDays,
                    momentumScore = 1.35,
                    adherenceScore = 85,
                    liquidityScore = 90,
                    legacyEfficiency = 80
                )

                val missions = gamificationService.evaluateMissions(
                    wallets = activeWallets,
                    bills = bills,
                    goals = goals,
                    monthlyExpensesEstimate = monthlyExpenses
                )

                val badges = gamificationService.evaluateBadges(
                    wallets = activeWallets,
                    goals = goals,
                    streakDays = streak.currentStreakDays,
                    score = calculatedScore.score,
                    hasRecordedTransaction = transactions.isNotEmpty()
                )

                WealthScoreUiState(
                    score = calculatedScore,
                    streak = streak,
                    missions = missions,
                    badges = badges,
                    isLoading = false,
                    selectedTab = _uiState.value.selectedTab
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onTabSelected(tab: HealthTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}
