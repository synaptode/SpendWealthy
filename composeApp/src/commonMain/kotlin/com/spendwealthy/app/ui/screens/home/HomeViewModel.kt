package com.spendwealthy.app.ui.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiEffect
import com.spendwealthy.app.core.architecture.UiEvent
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.core.util.monthRange
import com.spendwealthy.app.domain.model.*
import com.spendwealthy.app.domain.repository.*
import com.spendwealthy.app.domain.service.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.Channel
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

data class BudgetAlertInfo(
    val categoryId: String?,
    val categoryName: String,
    val limit: Money,
    val spent: Money,
    val usagePercentage: Double,
    val isOverbudget: Boolean
)

data class HomeState(
    val userName: String = "User",
    val userAvatarId: Int = 0,
    val selectedYear: Int? = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    val selectedMonth: Int? = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber,
    val totalBalance: Money = Money.zero(),
    val totalIncome: Money = Money.zero(),
    val totalExpense: Money = Money.zero(),
    val safeToSpend: Money = Money.zero(),
    val dailyAllowance: Money = Money.zero(),
    val budgetUsage: Double = 0.0,
    val criticalBudgetAlerts: List<BudgetAlertInfo> = emptyList(),
    val upcomingBills: List<BillReminder> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val daysUntilPayday: Int = 0,
    val dailyInsight: CatInsight? = null,
    val familyInsight: CatInsight? = null,
    val resilienceReport: ResilienceReport? = null,
    val goals: List<Goal> = emptyList(),
    val autoMoves: List<AutoMove> = emptyList(),
    val automationImpact: Money = Money(0, "IDR"),
    val growthProjection: Money = Money(0, "IDR"),
    val optimizerRecommendations: List<OptimizerRecommendation> = emptyList(),
    val optimizationLogs: List<OptimizationLog> = emptyList(),
    val familyMembers: List<FamilyMember> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedScenario: ResilienceScenario = ResilienceScenario.Standard,
    val healthTelemetry: FinancialHealthTelemetry? = null,
    val wealthNarrative: WealthNarrative? = null,
    val wealthProjection: WealthProjection? = null,
    val auditReport: FinancialAuditReport? = null,
    val financialContext: FinancialContext = FinancialContext.STANDARD,
    val autoPilotStatus: AutoPilotStatus? = null,
    val detectedAnomalies: List<FinancialAnomaly> = emptyList(),
    val discoveredIntents: List<DiscoveryIntent> = emptyList(),
    val pastComparison: SimulationComparison? = null,
    val activeConsensusRequests: List<ConsensusRequest> = emptyList(),
    val financialMomentum: MomentumAnalysis? = null,
    val wealthBlueprint: WealthBlueprint? = null,
    val discoveredOpportunities: List<WealthOpportunity> = emptyList(),
    val dailyChallenge: FinancialChallenge? = null,
    val wealthShield: WealthShieldStatus? = null,
    val portfolioAnalysis: PortfolioAnalysis? = null,
    val lifecycleReport: LifecycleReport? = null,
    val estateReport: EstateReport? = null,
    val sovereignWealthScore: WealthScore? = null,
    val stressTestResult: StressTestResult? = null,
    val liquidityReport: LiquidityReport? = null,
    val simulationImpact: ImpactReport? = null,
    val isPrivacyMode: Boolean = false,
    val isLoading: Boolean = false
) : UiState

sealed class HomeEvent : UiEvent {
    object Refresh : HomeEvent()
    object TogglePrivacyMode : HomeEvent()
    data class SelectPeriod(val year: Int?, val month: Int?) : HomeEvent()
    data class ContributeToGoal(val goalId: String, val walletId: String, val amount: Long) : HomeEvent()
    data class ExecuteOptimization(val recommendationId: String) : HomeEvent()
    data class ChangeResilienceScenario(val scenario: ResilienceScenario) : HomeEvent()
    data class SimulatePurchaseImpact(val amount: Long) : HomeEvent()
    data class DiscoverIntent(val query: String) : HomeEvent()
    object DismissSimulation : HomeEvent()
    object SimulateMonthlyWrapup : HomeEvent()
    data class VoteConsensus(val requestId: String, val isApproved: Boolean) : HomeEvent()
}

sealed class HomeEffect : UiEffect {
    data class ShowError(val message: String) : HomeEffect()
    data class ShowCelebration(val goal: Goal) : HomeEffect()
}

class HomeViewModel(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val preferenceRepository: PreferenceRepository,
    private val budgetRepository: BudgetRepository,
    private val billRepository: BillRepository,
    private val insightService: CatInsightService,
    private val safeToSpendEngine: SafeToSpendEngine,
    private val notificationService: NotificationService,
    private val analyticsService: AnalyticsService,
    private val optimizerEngine: WealthOptimizerEngine,
    private val familyRepository: FamilyRepository,
    private val goalRepository: GoalRepository,
    private val resilienceService: ResilienceService,
    private val telemetryService: TelemetryService,
    private val narrativeEngine: WealthNarrativeEngine,
    private val projectionEngine: WealthProjectionEngine,
    private val impactSimulator: ImpactSimulator,
    private val auditService: AuditService,
    private val contextEngine: ContextEngine,
    private val autoPilotService: AutoPilotService,
    private val anomalyEngine: AnomalyDetectionEngine,
    private val intentEngine: IntentDiscoveryEngine,
    private val historicalEngine: HistoricalSimulationEngine,
    private val consensusService: FamilyConsensusService,
    private val trendService: TrendAnalysisService,
    private val blueprintEngine: WealthBlueprintEngine,
    private val opportunityEngine: OpportunityCostEngine,
    private val coachingService: CoachingService,
    private val shieldEngine: WealthShieldEngine,
    private val assetEngine: AssetIntelligenceEngine,
    private val lifecycleEngine: LifecycleSimulationEngine,
    private val estateEngine: EstateIntelligenceEngine,
    private val liquidityEngine: LiquiditySimulationEngine,
    private val scoreEngine: WealthScoreEngine,
    private val stressEngine: StressTestEngine,
    private val categoryRepository: CategoryRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val _selectedScenario = MutableStateFlow<ResilienceScenario>(ResilienceScenario.Standard)
    private val _nowSystem = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private val _selectedPeriod = MutableStateFlow<Pair<Int?, Int?>>(
        Pair(_nowSystem.year, _nowSystem.monthNumber)
    )

    private var currentWallets: List<Wallet> = emptyList()
    private var currentTransactions: List<Transaction> = emptyList()
    private var currentBudgets: List<Budget> = emptyList()
    private var currentBills: List<BillReminder> = emptyList()
    private var currentFamily: Family? = null
    private var currentGoals: List<Goal> = emptyList()
    private var currentCategories: Map<String, Category> = emptyMap()

    init {
        observeData()
    }

    private fun observeData() {
        screenModelScope.launch {
            preferenceRepository.getString("user_name").collect { name ->
                _uiState.update { it.copy(userName = name ?: "User") }
            }
        }
        screenModelScope.launch {
            preferenceRepository.getInt("user_avatar_id").collect { avatarId ->
                _uiState.update { it.copy(userAvatarId = avatarId) }
            }
        }
        screenModelScope.launch {
            preferenceRepository.getBoolean("privacy_mode_enabled").collect { privacy ->
                _uiState.update { it.copy(isPrivacyMode = privacy) }
            }
        }
        screenModelScope.launch {
            _selectedScenario.collect {
                recomputeState()
            }
        }
        screenModelScope.launch {
            walletRepository.getWallets().collect { wallets ->
                currentWallets = wallets
                recomputeState()
            }
        }
        screenModelScope.launch {
            _selectedPeriod.collectLatest { period ->
                val (year, month) = period
                if (year != null && month != null) {
                    val range = monthRange(year, month)
                    transactionRepository.getTransactionsByDateRange(range.start, range.endInclusive).collect { transactions ->
                        currentTransactions = transactions
                        recomputeState()
                    }
                } else {
                    transactionRepository.getTransactions().collect { transactions ->
                        currentTransactions = transactions
                        recomputeState()
                    }
                }
            }
        }
        screenModelScope.launch {
            _selectedPeriod.collectLatest { period ->
                val (year, month) = period
                val now = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val targetYear = year ?: now.year
                val targetMonth = month ?: now.monthNumber
                budgetRepository.getBudgets(targetMonth, targetYear).collect { budgets ->
                    currentBudgets = budgets
                    recomputeState()
                }
            }
        }
        screenModelScope.launch {
            billRepository.getBillReminders().collect { bills ->
                currentBills = bills
                recomputeState()
            }
        }
        screenModelScope.launch {
            familyRepository.getFamily().collect { family ->
                currentFamily = family
                recomputeState()
            }
        }
        screenModelScope.launch {
            goalRepository.getGoals().collect { goals ->
                currentGoals = goals
                recomputeState()
            }
        }
        screenModelScope.launch {
            categoryRepository.getCategories().collect { categories ->
                currentCategories = categories.associateBy { it.id }
                recomputeState()
            }
        }
    }

    private fun recomputeState() {
        val now = KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val (year, month) = _selectedPeriod.value
        val range = if (year != null && month != null) {
            monthRange(year, month)
        } else {
            monthRange(now.year, now.monthNumber)
        }
        val wallets = currentWallets
        val transactions = currentTransactions
        val budgets = currentBudgets
        val bills = currentBills
        val family = currentFamily
        val goals = currentGoals
        val scenario = _selectedScenario.value

        val totalBalance = wallets.sumOf { CurrencyConverter.toIdr(it.balance) }
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.amount }
        val unpaidBills = bills
            .filter {
                if (year != null && month != null) {
                    it.isEnabled && it.dueDate >= range.start && it.dueDate <= range.endInclusive
                } else {
                    it.isEnabled
                }
            }
            .sortedBy { it.dueDate }
        val totalBudget = budgets.firstOrNull { it.categoryId == null } ?: budgets.combineAsTotal()
        val budgetUsage = if (totalBudget?.limit?.amount != null && totalBudget.limit.amount > 0L) {
            expense.toDouble() / totalBudget.limit.amount.toDouble()
        } else {
            0.0
        }
        val spentMap = transactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapValues { (_, tList) -> tList.sumOf { it.amount.amount } }

        val criticalBudgetAlerts = budgets.filter { it.categoryId != null }.mapNotNull { budget ->
            val spent = spentMap[budget.categoryId] ?: 0L
            val limit = budget.limit.amount
            if (limit > 0L) {
                val usage = spent.toDouble() / limit.toDouble()
                if (usage >= 0.8) {
                    val catName = budget.categoryId?.let { currentCategories[it]?.name } ?: "Kategori"
                    BudgetAlertInfo(
                        categoryId = budget.categoryId,
                        categoryName = catName,
                        limit = budget.limit,
                        spent = Money(spent, budget.limit.currency),
                        usagePercentage = usage,
                        isOverbudget = usage >= 1.0
                    )
                } else null
            } else null
        }.sortedByDescending { it.usagePercentage }

        val safeResult = safeToSpendEngine.calculateAdvanced(
            totalLiquidity = Money(totalBalance, "IDR"),
            unpaidBills = unpaidBills,
            budgets = budgets,
            currentSpentInBudgets = spentMap,
            today = now,
            payday = range.endInclusive
        )

        val safeToSpend = safeResult.totalSafe
        val dailyAllowance = safeResult.dailySafe

        val dailyInsight = insightService.getDailyInsight(transactions, budgets, range.endInclusive)
        val familyInsight = insightService.getFamilyInsight(transactions, budgets)
        val resilienceReport = resilienceService.calculateResilience(wallets, transactions, scenario)

        val executedOptCount = _uiState.value.optimizationLogs.size
        val recs = optimizerEngine.generateRecommendations(
            wallets = wallets,
            goals = goals,
            billReminders = bills,
            safeToSpend = safeToSpend,
            familyMembers = family?.members ?: emptyList()
        )

        val healthTelemetry = telemetryService.generateHealthTelemetry(
            wallets = wallets,
            transactions = transactions,
            executedOptimizations = executedOptCount,
            totalOptimizations = recs.size
        )

        val narrative = narrativeEngine.generateNarrative(
            telemetry = healthTelemetry,
            resilience = resilienceReport,
            recommendations = recs,
            userName = _uiState.value.userName
        )

        val projection = projectionEngine.generate12MonthProjection(
            currentLiquidity = Money(totalBalance, "IDR"),
            telemetry = healthTelemetry,
            activeGoals = goals
        )

        val auditReport = auditService.generateExecutiveAudit(
            telemetry = healthTelemetry,
            resilience = resilienceReport,
            narrative = narrative,
            projection = projection,
            recommendations = recs
        )

        val context = contextEngine.determineContext(healthTelemetry, resilienceReport)

        val autoPilotStatus = autoPilotService.getStatus(
            executedCount = executedOptCount,
            wealthCreated = 3450000L
        )

        val anomalies = anomalyEngine.detectAnomalies(
            recentTransactions = transactions,
            averageMonthlySpending = 8_000_000L
        )

        val pastComparison = historicalEngine.simulateAlternatePast(
            transactions = transactions,
            actualCurrentBalance = Money(totalBalance, wallets.firstOrNull()?.balance?.currency ?: "IDR")
        )

        val consensusRequests = consensusService.requests.filter { it.status == ConsensusStatus.PENDING }

        val mockSnapshots = listOf(
            MomentumSnapshot(
                timestamp = KtClock.System.now().minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
                healthScore = 72,
                netWorth = Money(150_000_000, "IDR"),
                savingsRate = 0.15
            )
        )
        val momentum = trendService.calculateMomentum(healthTelemetry, mockSnapshots)

        val blueprint = blueprintEngine.generateBlueprint(
            wallets = wallets,
            monthlyIncome = 15_000_000L
        )

        val opportunities = opportunityEngine.discoverOpportunities(wallets)
        val dailyChallenge = coachingService.generateDailyChallenge()

        val shieldStatus = shieldEngine.evaluateShieldStatus(
            context = context,
            anomalies = anomalies,
            totalAssets = Money(totalBalance, wallets.firstOrNull()?.balance?.currency ?: "IDR")
        )

        val portfolio = assetEngine.analyzePortfolio(
            listOf(
                Asset("a1", "Cash Reserves", AssetType.CASH, Money(totalBalance, "IDR"), 0.02, 1),
                Asset("a2", "Blue Chip Index", AssetType.STOCK, Money(50_000_000, "IDR"), 0.12, 5),
                Asset("a3", "Gold Bullion", AssetType.COMMODITY, Money(25_000_000, "IDR"), 0.05, 3)
            )
        )

        val lifecycle = lifecycleEngine.simulateLifetime(
            currentAge = 30,
            targetRetirementAge = 65,
            currentNetWorth = portfolio.totalValue,
            monthlyContribution = 5_000_000L
        )

        val estate = estateEngine.analyzeEstate(
            totalAssets = portfolio.totalValue,
            familyMembers = family?.members?.size ?: 0,
            hasVault = true
        )

        val liquidity = liquidityEngine.simulateLiquidity(
            wallets = wallets,
            bills = bills,
            projectedIncome = 15_000_000L,
            next30DaysSpending = 8_000_000L
        )

        val sovereignScore = scoreEngine.calculateScore(
            resilienceDays = (resilienceReport.runwayMonths * 30).toInt(),
            momentumScore = momentum.momentumScore.toDouble(),
            adherenceScore = blueprint.adherenceScore,
            liquidityScore = liquidity.safetyScore,
            legacyEfficiency = estate.wealthTransferEfficiency
        )

        val stressTest = stressEngine.runBlackSwanSimulation(
            totalAssets = portfolio.totalValue,
            monthlyExpenses = 10_000_000L,
            isShieldActive = shieldStatus.isActive
        )

        val sortedTransactions = transactions.sortedByDescending { it.date }
        val transactionsWithFamily = if (year == null || (year == now.year && month == now.monthNumber)) {
            sortedTransactions.toMutableList().apply {
                add(0, Transaction(
                    id = "ft1",
                    amount = Money(75000, "IDR"),
                    type = TransactionType.EXPENSE,
                    categoryId = "cat_food",
                    walletId = wallets.firstOrNull()?.id ?: "w1",
                    targetWalletId = null,
                    date = now,
                    note = "Family Pizza Night",
                    merchant = "Pizza Hut",
                    createdAt = KtClock.System.now(),
                    updatedAt = KtClock.System.now(),
                    familyId = "fam_1",
                    createdById = "2"
                ))
            }
        } else {
            sortedTransactions
        }

        _uiState.update { current ->
            current.copy(
                isLoading = false,
                selectedYear = year,
                selectedMonth = month,
                wallets = wallets,
                categories = currentCategories.values.toList(),
                totalBalance = Money(totalBalance, "IDR"),
                totalIncome = Money(income, "IDR"),
                totalExpense = Money(expense, "IDR"),
                safeToSpend = safeToSpend,
                dailyAllowance = dailyAllowance,
                budgetUsage = budgetUsage,
                criticalBudgetAlerts = criticalBudgetAlerts,
                upcomingBills = unpaidBills.take(3),
                recentTransactions = transactionsWithFamily.take(5),
                daysUntilPayday = (range.endInclusive.dayOfMonth - now.dayOfMonth).coerceAtLeast(0),
                dailyInsight = dailyInsight,
                familyInsight = familyInsight,
                resilienceReport = resilienceReport,
                healthTelemetry = healthTelemetry,
                wealthNarrative = narrative,
                wealthProjection = projection,
                auditReport = auditReport,
                financialContext = context,
                autoPilotStatus = autoPilotStatus,
                detectedAnomalies = anomalies,
                pastComparison = pastComparison,
                activeConsensusRequests = consensusRequests,
                financialMomentum = momentum,
                wealthBlueprint = blueprint,
                discoveredOpportunities = opportunities,
                dailyChallenge = dailyChallenge,
                wealthShield = shieldStatus,
                portfolioAnalysis = portfolio,
                lifecycleReport = lifecycle,
                estateReport = estate,
                liquidityReport = liquidity,
                sovereignWealthScore = sovereignScore,
                stressTestResult = stressTest,
                selectedScenario = scenario,
                familyMembers = family?.members ?: emptyList(),
                autoMoves = listOf(
                    AutoMove(
                        id = "am1",
                        name = "Auto-Save to Emergency",
                        amount = Money(500000, "IDR"),
                        sourceWalletId = wallets.firstOrNull()?.id ?: "w1",
                        targetGoalId = goals.firstOrNull()?.id ?: "g1",
                        frequency = RecurrenceType.MONTHLY,
                        nextRunDate = now
                    )
                ),
                goals = if (current.goals.isNotEmpty()) current.goals else goals.ifEmpty {
                    listOf(
                        Goal(
                            id = "g1",
                            name = "Emergency Fund",
                            targetAmount = Money(25000000, "IDR"),
                            currentAmount = Money(15600000, "IDR"),
                            type = GoalType.SAVINGS,
                            deadline = null,
                            createdAt = KtClock.System.now()
                        )
                    )
                },
                automationImpact = analyticsService.calculateMonthlyAutomationImpact(
                    listOf(
                        AutoMoveLog("1", "am1", Money(500000, "IDR"), AutoMoveStatus.SUCCESS, KtClock.System.now()),
                        AutoMoveLog("2", "am1", Money(500000, "IDR"), AutoMoveStatus.SUCCESS, KtClock.System.now())
                    )
                ),
                growthProjection = analyticsService.calculateAnnualGrowthProjection(
                    listOf(
                        AutoMove(
                            id = "am1",
                            name = "Auto-Save to Emergency",
                            amount = Money(500000, "IDR"),
                            sourceWalletId = wallets.firstOrNull()?.id ?: "w1",
                            targetGoalId = goals.firstOrNull()?.id ?: "g1",
                            frequency = RecurrenceType.MONTHLY,
                            nextRunDate = now
                        )
                    )
                ),
                optimizerRecommendations = recs
            )
        }
    }


    fun onPeriodSelected(year: Int?, month: Int?) {
        _selectedPeriod.value = Pair(year, month)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> {
                recomputeState()
            }
            HomeEvent.TogglePrivacyMode -> {
                togglePrivacyMode()
            }
            is HomeEvent.SelectPeriod -> {
                onPeriodSelected(event.year, event.month)
            }
            is HomeEvent.ChangeResilienceScenario -> {
                _selectedScenario.value = event.scenario
            }
            is HomeEvent.SimulatePurchaseImpact -> {
                simulateImpact(event.amount)
            }
            is HomeEvent.DiscoverIntent -> {
                val intents = intentEngine.discoverIntents(event.query)
                _uiState.update { it.copy(discoveredIntents = intents) }
            }
            HomeEvent.DismissSimulation -> {
                _uiState.update { it.copy(simulationImpact = null) }
            }
            is HomeEvent.ContributeToGoal -> contributeToGoal(event)
            is HomeEvent.ExecuteOptimization -> executeOptimization(event)
            HomeEvent.SimulateMonthlyWrapup -> simulateMonthlyWrapup()
            is HomeEvent.VoteConsensus -> {
                consensusService.castVote(event.requestId, event.isApproved)
                recomputeState()
            }
        }
    }

    fun togglePrivacyMode() {
        screenModelScope.launch {
            val current = _uiState.value.isPrivacyMode
            preferenceRepository.setBoolean("privacy_mode_enabled", !current)
        }
    }

    private fun simulateMonthlyWrapup() {
        screenModelScope.launch {
            val wrapup = analyticsService.generateMonthlyWrapup(
                logs = listOf(
                    AutoMoveLog("1", "am1", Money(500000, "IDR"), AutoMoveStatus.SUCCESS, KtClock.System.now()),
                    AutoMoveLog("2", "am1", Money(500000, "IDR"), AutoMoveStatus.SUCCESS, KtClock.System.now())
                ),
                optimizationLogs = _uiState.value.optimizationLogs,
                goalsReached = 1
            )
            
            notificationService.showNotification(
                title = "Monthly Growth Report 📈",
                message = wrapup,
                type = NotificationType.INFO
            )
        }
    }

    private fun executeOptimization(event: HomeEvent.ExecuteOptimization) {
        screenModelScope.launch {
            val recommendation = _uiState.value.optimizerRecommendations.find { it.id == event.recommendationId } ?: return@launch
            
            // Simulate processing
            delay(1500)
            
            _uiState.update { state ->
                // Apply actions (simulated)
                val updatedGoals = state.goals.map { g ->
                    val action = recommendation.actions.find { it.targetId == g.id }
                    if (action != null) {
                        g.copy(currentAmount = g.currentAmount.copy(amount = g.currentAmount.amount + action.amount.amount))
                    } else g
                }
                
                val newLog = OptimizationLog(
                    id = "olog_${KtClock.System.now().toEpochMilliseconds()}",
                    recommendationId = recommendation.id,
                    title = recommendation.title,
                    impactScore = recommendation.impactScore,
                    executedAt = KtClock.System.now()
                )
                
                // Achievement Logic: Unlock "Family Power" badge if it was a family pooling strategy
                if (recommendation.id.contains("pool")) {
                    screenModelScope.launch {
                        delay(2000) // Show after the initial success toast
                        notificationService.showNotification(
                            title = "Achievement Unlocked! 🏆",
                            message = "You've earned the 'Family Power' badge for collective goal surging.",
                            type = NotificationType.SUCCESS
                        )
                    }
                }
                
                state.copy(
                    optimizerRecommendations = state.optimizerRecommendations.filter { it.id != event.recommendationId },
                    optimizationLogs = listOf(newLog) + state.optimizationLogs,
                    goals = updatedGoals
                )
            }
            
            notificationService.showNotification(
                title = "Optimization Executed! ✨",
                message = "Your wealth is now working harder for you.",
                type = NotificationType.SUCCESS
            )
        }
    }

    private fun simulateImpact(amount: Long) {
        screenModelScope.launch {
            val wallets = walletRepository.getWallets().first()
            val transactions = transactionRepository.getTransactionsByDateRange(
                KtClock.System.now().minus(30, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()).date,
                KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            ).first()
            
            val report = impactSimulator.simulatePurchaseImpact(
                amount = Money(amount, "IDR"),
                currentWallets = wallets,
                recentTransactions = transactions
            )
            
            _uiState.update { it.copy(simulationImpact = report) }
        }
    }
    
    private fun contributeToGoal(event: HomeEvent.ContributeToGoal) {
        screenModelScope.launch {
            _uiState.update { state ->
                val updatedGoals = state.goals.map { g ->
                    if (g.id == event.goalId) {
                        val newAmount = g.currentAmount.amount + event.amount
                        val updatedGoal = g.copy(currentAmount = g.currentAmount.copy(amount = newAmount))
                        
                        if (newAmount >= g.targetAmount.amount) {
                            screenModelScope.launch { 
                                _effect.send(HomeEffect.ShowCelebration(updatedGoal))
                                // Also notify family space
                                familyRepository.getFamily().firstOrNull()?.let { _ ->
                                    notificationService.showNotification(
                                        title = "Goal Achieved! 🥳",
                                        message = "Your family reached the '${updatedGoal.name}' target!",
                                        type = NotificationType.FAMILY
                                    )
                                }
                            }
                        }
                        
                        updatedGoal
                    } else g
                }
                state.copy(goals = updatedGoals)
            }
        }
    }

    fun deleteTransaction(id: String) {
        screenModelScope.launch {
            transactionRepository.deleteTransaction(id)
        }
    }

    private fun List<Budget>.combineAsTotal(): Budget? {
        if (isEmpty()) return null
        val first = first()
        return first.copy(
            categoryId = null,
            limit = Money(sumOf { it.limit.amount }, first.limit.currency)
        )
    }
}
