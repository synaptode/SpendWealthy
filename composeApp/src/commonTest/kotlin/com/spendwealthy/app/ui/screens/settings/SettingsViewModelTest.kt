package com.spendwealthy.app.ui.screens.settings

import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.SubscriptionEntitlement
import com.spendwealthy.app.domain.model.SubscriptionPlan
import com.spendwealthy.app.domain.model.ThemeMode
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.domain.repository.SubscriptionRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.service.CsvReportExporter
import com.spendwealthy.app.domain.service.DataService
import com.spendwealthy.app.domain.usecase.ExportMonthlyTransactionsCsvUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock as KtClock
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakePreferenceRepo = FakePreferenceRepository()
    private val fakeSubscriptionRepo = FakeSubscriptionRepository()
    private val fakeWalletRepo = FakeWalletRepository()
    private val fakeTransactionRepo = FakeTransactionRepository()
    private val dataService = DataService()
    private val fakeFileShareGateway = FakeFileShareGateway()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialSettingsStateLoadsFromPreferences() = runTest(testDispatcher) {
        fakePreferenceRepo.setString("user_name", "Budi Finansial")
        fakePreferenceRepo.setString("user_currency", "IDR")
        fakePreferenceRepo.setString("theme_mode", "DARK")
        fakePreferenceRepo.setBoolean("biometric_enabled", true)

        val viewModel = SettingsViewModel(
            preferenceRepository = fakePreferenceRepo,
            subscriptionRepository = fakeSubscriptionRepo,
            walletRepository = fakeWalletRepo,
            transactionRepository = fakeTransactionRepo,
            dataService = dataService,
            fileShareGateway = fakeFileShareGateway
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Budi Finansial", state.userName)
        assertEquals("IDR", state.userCurrency)
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertTrue(state.biometricEnabled)
        assertFalse(state.hasCustomPin)
    }

    @Test
    fun testSetThemeModeUpdatesPreference() = runTest(testDispatcher) {
        val viewModel = SettingsViewModel(
            preferenceRepository = fakePreferenceRepo,
            subscriptionRepository = fakeSubscriptionRepo,
            walletRepository = fakeWalletRepo,
            transactionRepository = fakeTransactionRepo,
            dataService = dataService,
            fileShareGateway = fakeFileShareGateway
        )
        advanceUntilIdle()

        viewModel.setThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()
        assertEquals(ThemeMode.LIGHT, viewModel.uiState.value.themeMode)

        viewModel.setThemeMode(ThemeMode.DARK)
        advanceUntilIdle()
        assertEquals(ThemeMode.DARK, viewModel.uiState.value.themeMode)
    }

    @Test
    fun testSetSecurityPinEnablesBiometricAndCustomPin() = runTest(testDispatcher) {
        val viewModel = SettingsViewModel(
            preferenceRepository = fakePreferenceRepo,
            subscriptionRepository = fakeSubscriptionRepo,
            walletRepository = fakeWalletRepo,
            transactionRepository = fakeTransactionRepo,
            dataService = dataService,
            fileShareGateway = fakeFileShareGateway
        )
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.hasCustomPin)

        viewModel.setSecurityPin("123456")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.hasCustomPin)
        assertTrue(viewModel.uiState.value.biometricEnabled)
    }

    @Test
    fun testBackupLocalDataJsonInvokesFileShareGateway() = runTest(testDispatcher) {
        val now = KtClock.System.now()
        fakeWalletRepo.walletsFlow.value = listOf(
            Wallet("w1", "BCA", Money(1_000_000, "IDR"), WalletType.BANK, now, false)
        )
        fakeTransactionRepo.txFlow.value = listOf(
            Transaction("t1", Money(50_000, "IDR"), TransactionType.EXPENSE, "c1", "w1", null, LocalDate(2026, 10, 17), "Lunch", "Warung", now, now)
        )

        val viewModel = SettingsViewModel(
            preferenceRepository = fakePreferenceRepo,
            subscriptionRepository = fakeSubscriptionRepo,
            walletRepository = fakeWalletRepo,
            transactionRepository = fakeTransactionRepo,
            dataService = dataService,
            fileShareGateway = fakeFileShareGateway
        )
        advanceUntilIdle()

        viewModel.backupLocalDataJson()
        advanceUntilIdle()

        assertEquals("Cadangan JSON berhasil dibagikan.", viewModel.uiState.value.exportStatus)
        assertTrue(fakeFileShareGateway.sharedFileNames.any { it.startsWith("spendwealthy-backup-") && it.endsWith(".json") })
        assertEquals("application/json", fakeFileShareGateway.lastMimeType)

        // Clear export status
        viewModel.clearExportStatus()
        assertNull(viewModel.uiState.value.exportStatus)
    }

    private class FakePreferenceRepository : PreferenceRepository {
        private val booleans = MutableStateFlow<Map<String, Boolean>>(emptyMap())
        private val strings = MutableStateFlow<Map<String, String>>(emptyMap())
        private val ints = MutableStateFlow<Map<String, Int>>(emptyMap())

        override fun getString(key: String): Flow<String?> =
            strings.map { it[key] }

        override suspend fun setString(key: String, value: String) {
            strings.value = strings.value + (key to value)
        }

        override fun getBoolean(key: String): Flow<Boolean> =
            booleans.map { it[key] ?: false }

        override suspend fun setBoolean(key: String, value: Boolean) {
            booleans.value = booleans.value + (key to value)
        }

        override fun getInt(key: String): Flow<Int> =
            ints.map { it[key] ?: 0 }

        override suspend fun setInt(key: String, value: Int) {
            ints.value = ints.value + (key to value)
        }
    }

    private class FakeSubscriptionRepository : SubscriptionRepository {
        private val entitlementFlow = MutableStateFlow(SubscriptionEntitlement(SubscriptionPlan.FREE, true))

        override fun getEntitlement(): Flow<SubscriptionEntitlement> = entitlementFlow
        override suspend fun setLocalPlan(plan: SubscriptionPlan) {
            entitlementFlow.value = SubscriptionEntitlement(plan, true)
        }
    }

    private class FakeWalletRepository : WalletRepository {
        val walletsFlow = MutableStateFlow<List<Wallet>>(emptyList())
        override fun getWallets(): Flow<List<Wallet>> = walletsFlow
        override fun getWalletById(id: String): Flow<Wallet?> = flowOf(null)
        override suspend fun insertWallet(wallet: Wallet) {}
        override suspend fun deleteWallet(id: String) {}
        override suspend fun archiveWallet(id: String) {}
    }

    private class FakeTransactionRepository : TransactionRepository {
        val txFlow = MutableStateFlow<List<Transaction>>(emptyList())
        override fun getTransactions(): Flow<List<Transaction>> = txFlow
        override fun getTransactionsByWallet(walletId: String): Flow<List<Transaction>> = txFlow
        override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> = txFlow
        override fun getTransactionById(id: String): Flow<Transaction?> = flowOf(null)
        override suspend fun insertTransaction(transaction: Transaction) {}
        override suspend fun deleteTransaction(id: String) {}
    }

    private class FakeFileShareGateway : FileShareGateway {
        val sharedFileNames = mutableListOf<String>()
        var lastMimeType: String? = null
        var lastContent: String? = null

        override suspend fun shareTextFile(
            fileName: String,
            mimeType: String,
            content: String
        ): GatewayResult<Unit> {
            sharedFileNames.add(fileName)
            lastMimeType = mimeType
            lastContent = content
            return GatewayResult.Success(Unit)
        }
    }
}
