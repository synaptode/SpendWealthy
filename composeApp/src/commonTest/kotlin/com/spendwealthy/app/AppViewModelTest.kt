package com.spendwealthy.app

import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.repository.BillRepository
import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.domain.service.NotificationService
import com.spendwealthy.app.domain.service.NotificationType
import com.spendwealthy.app.domain.usecase.RescheduleBillRemindersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakePreferenceRepo = FakePreferenceRepository()
    private val fakeBillRepo = FakeBillRepository()
    private val fakeNotificationScheduler = FakeNotificationScheduler()
    private val notificationService = NotificationService()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testStartupStateDefaultsToOnboardingWhenNotCompleted() = runTest(testDispatcher) {
        val useCase = RescheduleBillRemindersUseCase(fakeBillRepo, fakeNotificationScheduler)
        val viewModel = AppViewModel(fakePreferenceRepo, useCase, notificationService)

        advanceUntilIdle()
        assertEquals(AppStartupState.Onboarding, viewModel.startupState.value)
    }

    @Test
    fun testStartupStateTransitionsToDashboardWhenCompleted() = runTest(testDispatcher) {
        val useCase = RescheduleBillRemindersUseCase(fakeBillRepo, fakeNotificationScheduler)
        val viewModel = AppViewModel(fakePreferenceRepo, useCase, notificationService)

        advanceUntilIdle()
        assertEquals(AppStartupState.Onboarding, viewModel.startupState.value)

        fakePreferenceRepo.setBoolean("onboarding_completed", true)
        advanceUntilIdle()

        assertEquals(AppStartupState.Dashboard, viewModel.startupState.value)
    }

    @Test
    fun testNotificationObservationAndAutoDismiss() = runTest(testDispatcher) {
        val useCase = RescheduleBillRemindersUseCase(fakeBillRepo, fakeNotificationScheduler)
        val viewModel = AppViewModel(fakePreferenceRepo, useCase, notificationService)

        advanceUntilIdle()
        notificationService.showNotification("Peringatan", "Tagihan listrik jatuh tempo", NotificationType.WARNING)

        testScheduler.advanceTimeBy(100)
        testScheduler.runCurrent()

        assertNotNull(viewModel.notification.value)
        assertEquals("Peringatan", viewModel.notification.value?.title)
        assertEquals("Tagihan listrik jatuh tempo", viewModel.notification.value?.message)

        testScheduler.advanceTimeBy(4000)
        testScheduler.runCurrent()
        assertEquals(null, viewModel.notification.value)
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

    private class FakeBillRepository : BillRepository {
        override fun getBillReminders(): Flow<List<BillReminder>> = flowOf(emptyList())
        override fun getBillReminderById(id: String): Flow<BillReminder?> = flowOf(null)
        override suspend fun insertBillReminder(bill: BillReminder) {}
        override suspend fun updateBillReminder(bill: BillReminder) {}
        override suspend fun deleteBillReminder(id: String) {}
    }

    private class FakeNotificationScheduler : NotificationScheduler {
        val scheduledBills = mutableListOf<BillReminder>()
        override suspend fun scheduleBillReminder(bill: BillReminder): GatewayResult<Unit> {
            scheduledBills.add(bill)
            return GatewayResult.Success(Unit)
        }
        override suspend fun cancelBillReminder(billId: String): GatewayResult<Unit> {
            scheduledBills.removeAll { it.id == billId }
            return GatewayResult.Success(Unit)
        }
    }
}
