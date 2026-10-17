package com.spendwealthy.app.core.di

import com.spendwealthy.app.core.db.createDatabase
import com.spendwealthy.app.data.repository.*
import com.spendwealthy.app.domain.repository.*
import com.spendwealthy.app.domain.service.*
import com.spendwealthy.app.domain.usecase.*
import com.spendwealthy.app.domain.model.FreePlanLimits
import com.spendwealthy.app.AppViewModel
import com.spendwealthy.app.ui.screens.auth.AuthViewModel
import com.spendwealthy.app.ui.screens.onboarding.OnboardingViewModel
import com.spendwealthy.app.ui.screens.home.HomeViewModel
import com.spendwealthy.app.ui.screens.transaction.AddTransactionViewModel
import com.spendwealthy.app.ui.screens.transaction.list.TransactionListViewModel
import com.spendwealthy.app.ui.screens.wallet.WalletListViewModel
import com.spendwealthy.app.ui.screens.wallet.WalletDetailViewModel
import com.spendwealthy.app.ui.screens.wallet.AddWalletViewModel
import com.spendwealthy.app.ui.screens.budget.BudgetListViewModel
import com.spendwealthy.app.ui.screens.budget.AddBudgetViewModel
import com.spendwealthy.app.ui.screens.bill.BillListViewModel
import com.spendwealthy.app.ui.screens.bill.AddBillViewModel
import com.spendwealthy.app.ui.screens.report.ReportViewModel
import com.spendwealthy.app.ui.screens.settings.SettingsViewModel
import com.spendwealthy.app.ui.screens.vault.VaultViewModel
import com.spendwealthy.app.ui.screens.vault.AddDocumentViewModel
import com.spendwealthy.app.ui.screens.family.FamilyViewModel
import com.spendwealthy.app.core.architecture.DefaultDispatcherProvider
import com.spendwealthy.app.core.architecture.DispatcherProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule = module {
    // Core Architecture & Concurrency
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    // Database & Repositories
    single { createDatabase(get()) }

    single<WalletRepository> { WalletRepositoryImpl(get(), get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get()) }
    single<BudgetRepository> { BudgetRepositoryImpl(get(), get()) }
    single<BillRepository> { BillRepositoryImpl(get(), get()) }
    single<PreferenceRepository> { PreferenceRepositoryImpl(get(), get()) }
    single<SubscriptionRepository> { LocalSubscriptionRepository(get()) }
    single<DocumentRepository> { SqlDelightDocumentRepository(get(), get()) }
    single<FamilyRepository> { MemoryFamilyRepository() }
    single<GoalRepository> { SqlDelightGoalRepository(get(), get()) }

    // Domain Services
    single { BalanceCalculator() }
    single { BudgetEngine() }
    single { CsvReportExporter() }
    single { DataService() }
    single { CatInsightService() }
    single { SafeToSpendEngine() }
    single { NotificationService() }
    single { AnalyticsService() }
    single { WealthOptimizerEngine(get()) }
    single { ResilienceService() }
    single { TelemetryService() }
    single { WealthNarrativeEngine() }
    single { WealthProjectionEngine() }
    single { ImpactSimulator(get()) }
    single { AuditService() }
    single { ContextEngine() }
    single { AutoPilotService() }
    single { AnomalyDetectionEngine() }
    single { IntentDiscoveryEngine() }
    single { DecisionAuditService() }
    single { HistoricalSimulationEngine() }
    single { FamilyConsensusService() }
    single { TrendAnalysisService() }
    single { WealthBlueprintEngine() }
    single { OpportunityCostEngine() }
    single { CoachingService() }
    single { WealthShieldEngine() }
    single { AssetIntelligenceEngine() }
    single { LifecycleSimulationEngine() }
    single { EstateIntelligenceEngine() }
    single { LiquiditySimulationEngine() }
    single { WealthScoreEngine() }
    single { StressTestEngine() }
    single { SubscriptionPolicy(FreePlanLimits()) }
    single<LedgerRepository> { SqlDelightLedgerRepository(get(), get()) }
    
    // Use Cases
    factory { AddTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { MarkBillPaidUseCase(get(), get(), get()) }
    factory { RescheduleBillRemindersUseCase(get(), get()) }
    factory { ExportMonthlyTransactionsCsvUseCase(get(), get(), get(), get()) }

    // App-level State / Screen Models
    single { AppViewModel(get(), get(), get()) }
    factory { AuthViewModel(get()) }
    factory { OnboardingViewModel(get(), get(), get(), get(), get()) }
    factory {
        HomeViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
            ,
            get()
            ,
            get(),
            get(),
            get()
        )
    }
    factory { AddTransactionViewModel(get(), get(), get(), get(), get(), get()) }
    factory { TransactionListViewModel(get(), get(), get()) }
    factory { WalletListViewModel(get()) }
    factory { (walletId: String) -> WalletDetailViewModel(walletId, get(), get()) }
    factory { AddWalletViewModel(get(), get(), get()) }
    factory { BudgetListViewModel(get(), get(), get(), get()) }
    factory { AddBudgetViewModel(get(), get(), get(), get()) }
    factory { BillListViewModel(get(), get(), get()) }
    factory { AddBillViewModel(get(), get(), get(), get()) }
    factory { ReportViewModel(get(), get(), get()) }
    factory { SettingsViewModel(get(), get(), get(), get(), get()) }
    factory { VaultViewModel(get(), get()) }
    factory { AddDocumentViewModel(get()) }
    factory { FamilyViewModel(get(), get()) }
}

fun initKoin(config: (org.koin.dsl.KoinAppDeclaration)? = null) = org.koin.core.context.startKoin {
    config?.invoke(this)
    modules(commonModule, platformModule())
}

expect fun platformModule(): Module
