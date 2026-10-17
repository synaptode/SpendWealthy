package com.spendwealthy.app.ui.screens.family

import com.spendwealthy.app.domain.model.Family
import com.spendwealthy.app.domain.model.FamilyMember
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.repository.FamilyRepository
import com.spendwealthy.app.domain.repository.GoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FamilyViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeFamilyRepo = FakeFamilyRepository()
    private val fakeGoalRepo = FakeGoalRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoadPopulatesFamilyAndGoals() = runTest(testDispatcher) {
        val viewModel = FamilyViewModel(fakeFamilyRepo, fakeGoalRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Keluarga Pratama", state.family?.name)
        assertTrue(state.sharedGoals.isNotEmpty())
        assertTrue(state.totalSavingsAmount > 0)
        assertNull(state.achievedMilestone)
    }

    @Test
    fun testContributionCrossingMilestoneTriggersCelebrationEvent() = runTest(testDispatcher) {
        val viewModel = FamilyViewModel(fakeFamilyRepo, fakeGoalRepo)
        advanceUntilIdle()

        // Goal fg_1: target 40.000.000, current 28.000.000 (70%)
        // Menambah 2.000.000 menjadi 30.000.000 (75%) -> Menembus milestone 75%
        viewModel.submitContribution(
            goalId = "fg_1",
            memberName = "Alex Pratama",
            amount = 2_000_000L
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val milestone = state.achievedMilestone
        assertNotNull(milestone)
        assertEquals(75, milestone.milestonePercent)
        assertEquals("Liburan Keluarga Akhir Tahun", milestone.goalTitle)
        assertEquals("Alex Pratama", milestone.contributorName)
        assertEquals(30_000_000L, milestone.totalAmount)

        // Dismiss milestone
        viewModel.onDismissMilestoneCelebration()
        assertNull(viewModel.uiState.value.achievedMilestone)
    }

    @Test
    fun testContributionReaching100PercentMilestone() = runTest(testDispatcher) {
        val viewModel = FamilyViewModel(fakeFamilyRepo, fakeGoalRepo)
        advanceUntilIdle()

        // Goal fg_1: current 28.000.000, target 40.000.000
        // Setor 12.000.000 mencapai 40.000.000 (100%)
        viewModel.submitContribution(
            goalId = "fg_1",
            memberName = "Sarah Pratama",
            amount = 12_000_000L
        )
        advanceUntilIdle()

        val milestone = viewModel.uiState.value.achievedMilestone
        assertNotNull(milestone)
        assertEquals(100, milestone.milestonePercent)
        assertEquals(40_000_000L, milestone.totalAmount)
    }
}

private class FakeFamilyRepository : FamilyRepository {
    private val familyFlow = MutableStateFlow<Family?>(null)

    override fun getFamily(): Flow<Family?> = familyFlow

    override suspend fun joinFamily(familyId: String, member: FamilyMember) {}

    override suspend fun inviteMember(email: String) {}

    override suspend fun createFamily(name: String) {}
}

private class FakeGoalRepository : GoalRepository {
    override fun getGoals(): Flow<List<Goal>> = flowOf(emptyList())

    override fun getGoalById(id: String): Flow<Goal?> = flowOf(null)

    override suspend fun insertGoal(goal: Goal) {}

    override suspend fun updateGoalProgress(id: String, currentAmount: Long) {}

    override suspend fun deleteGoal(id: String) {}
}
