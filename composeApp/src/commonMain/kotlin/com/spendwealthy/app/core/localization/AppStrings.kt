package com.spendwealthy.app.core.localization

/**
 * Type-safe interface for all localized strings across SpendWealthy.
 */
data class AppStrings(
    // Common & Actions
    val appName: String,
    val tabHome: String,
    val tabFamily: String,
    val tabBudget: String,
    val tabStats: String,
    val tabVault: String,
    val close: String,
    val cancel: String,
    val save: String,
    val confirm: String,
    val delete: String,
    val edit: String,
    val done: String,
    val back: String,
    val search: String,
    val seeAll: String,
    val loading: String,

    // Home & Financial Overview
    val totalLiquidBalance: String,
    val spendingThisMonth: String,
    val netWorth: String,
    val safeToSpend: String,
    val activeBudgets: String,
    val walletsAccounts: String,
    val recentTransactions: String,
    val noTransactionsYet: String,

    // Quick Actions
    val quickActionSend: String,
    val quickActionReceive: String,
    val quickActionScan: String,
    val quickActionSplitSalary: String,
    val quickActionSimulate: String,
    val quickActionSwap: String,

    // Superpower: What-If Simulator
    val simulatorTitle: String,
    val simulatorSubtitle: String,
    val simulatorAmountPrompt: String,
    val simulatorRunButton: String,
    val simulatorSafeToBuy: String,
    val simulatorCaution: String,
    val simulatorWaitPayday: String,
    val simulatorHighRisk: String,

    // Superpower: Paycheck Splitter
    val splitterTitle: String,
    val splitterSubtitle: String,
    val splitterAmountPrompt: String,
    val splitterExecuteButton: String,
    val splitterSuccess: String,
    val splitterNeeds: String,
    val splitterWants: String,
    val splitterSavings: String,

    // Superpower: Wealth Score & Gamification
    val scoreTitle: String,
    val scoreSubtitle: String,
    val scorePillars: String,
    val scoreDailyStreak: String,
    val scoreBoosterMissions: String,
    val scoreBadges: String,

    // Superpower: Smart Receipt & SMS Parser
    val scanReceiptTitle: String,
    val scanReceiptSubtitle: String,
    val scanSmsTitle: String,
    val scanPastePrompt: String,
    val scanAutoMatchedWallet: String,
    val scanDetectedTotal: String,
    val scanDetectedTax: String,
    val scanDetectedItems: String,
    val scanSplitBillTitle: String,
    val scanApplyTransaction: String,

    // Settings & Security
    val settingsTitle: String,
    val settingsSubtitle: String,
    val languageSectionTitle: String,
    val languageSectionSubtitle: String,
    val selectLanguage: String,
    val currentLanguageLabel: String,
    val accountSectionTitle: String,
    val guestMode: String,
    val verifiedAccount: String,
    val editProfile: String,
    val logout: String,
    val dataSyncSectionTitle: String,
    val exportCsv: String,
    val backupJson: String,
    val appearanceThemeTitle: String,
    val themeModeSystem: String,
    val themeModeLight: String,
    val themeModeDark: String,
    val securitySectionTitle: String,
    val privacyMode: String,
    val appLockBiometric: String,

    // Transactions
    val addTransactionTitle: String,
    val transactionExpense: String,
    val transactionIncome: String,
    val transactionTransfer: String,
    val amount: String,
    val note: String,
    val category: String,
    val wallet: String,
    val selectWallet: String,
    val selectCategory: String
)
