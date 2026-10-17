package com.spendwealthy.app.ui.screens.auth

import com.spendwealthy.app.domain.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakePreferenceRepo = FakePreferenceRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoginValidationFailsWhenEmpty() = runTest(testDispatcher) {
        val viewModel = AuthViewModel(fakePreferenceRepo)

        var successCalled = false
        viewModel.login { successCalled = true }
        advanceUntilIdle()

        assertFalse(successCalled)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Email atau username wajib diisi"))
    }

    @Test
    fun testLoginValidationFailsWhenInvalidEmail() = runTest(testDispatcher) {
        val viewModel = AuthViewModel(fakePreferenceRepo)
        viewModel.onEmailChanged("invalidemail")
        viewModel.onPasswordChanged("password123")

        var successCalled = false
        viewModel.login { successCalled = true }
        advanceUntilIdle()

        assertFalse(successCalled)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Format email tidak valid"))
    }

    @Test
    fun testLoginSucceedsWithValidCredentials() = runTest(testDispatcher) {
        val viewModel = AuthViewModel(fakePreferenceRepo)
        viewModel.onEmailChanged("alex@example.com")
        viewModel.onPasswordChanged("Secret123!")

        var successCalled = false
        viewModel.login { successCalled = true }
        advanceUntilIdle()

        assertTrue(successCalled)
        assertNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.isSuccess)

        val storedLoggedIn = fakePreferenceRepo.getBoolean("is_logged_in").first()
        val storedEmail = fakePreferenceRepo.getString("user_email").first()
        val storedOnboarding = fakePreferenceRepo.getBoolean("onboarding_completed").first()
        assertTrue(storedLoggedIn)
        assertTrue(storedOnboarding)
        assertEquals("alex@example.com", storedEmail)
    }

    @Test
    fun testRegisterValidationRequiresTermsAndMatchPassword() = runTest(testDispatcher) {
        val viewModel = AuthViewModel(fakePreferenceRepo)
        viewModel.onNameChanged("Alex Pratama")
        viewModel.onEmailChanged("alex@example.com")
        viewModel.onPasswordChanged("Secret123!")
        viewModel.onConfirmPasswordChanged("DifferentPassword!")

        var successCalled = false
        viewModel.register { successCalled = true }
        advanceUntilIdle()

        assertFalse(successCalled)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Konfirmasi kata sandi tidak cocok"))

        // Now fix confirm password but leave terms unchecked (agreeTerms is false by default)
        viewModel.onConfirmPasswordChanged("Secret123!")
        viewModel.register { successCalled = true }
        advanceUntilIdle()

        assertFalse(successCalled)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Harap setujui Syarat & Kebijakan Privasi"))
    }

    @Test
    fun testRegisterSucceedsAndPersistsState() = runTest(testDispatcher) {
        val viewModel = AuthViewModel(fakePreferenceRepo)
        viewModel.onNameChanged("Budi Santoso")
        viewModel.onEmailChanged("budi@example.com")
        viewModel.onCurrencyChanged("USD")
        viewModel.onPasswordChanged("StrongPass123!")
        viewModel.onConfirmPasswordChanged("StrongPass123!")
        viewModel.toggleAgreeTerms() // Agree to terms

        var successCalled = false
        viewModel.register { successCalled = true }
        advanceUntilIdle()

        assertTrue(successCalled)
        assertNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.isSuccess)

        assertEquals("Budi Santoso", fakePreferenceRepo.getString("user_name").first())
        assertEquals("budi@example.com", fakePreferenceRepo.getString("user_email").first())
        assertEquals("USD", fakePreferenceRepo.getString("user_currency").first())
        assertTrue(fakePreferenceRepo.getBoolean("is_logged_in").first())
        assertTrue(fakePreferenceRepo.getBoolean("onboarding_completed").first())
    }

    @Test
    fun testLoginAsGuestCompletesQuickSetup() = runTest(testDispatcher) {
        val viewModel = AuthViewModel(fakePreferenceRepo)

        var successCalled = false
        viewModel.loginAsGuest { successCalled = true }
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals("Tamu Sovereign", fakePreferenceRepo.getString("user_name").first())
        assertTrue(fakePreferenceRepo.getBoolean("onboarding_completed").first())
    }

    @Test
    fun testTogglesInViewModel() {
        val viewModel = AuthViewModel(fakePreferenceRepo)

        assertFalse(viewModel.uiState.value.isPasswordVisible)
        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.isPasswordVisible)

        assertTrue(viewModel.uiState.value.rememberMe)
        viewModel.toggleRememberMe()
        assertFalse(viewModel.uiState.value.rememberMe)

        assertFalse(viewModel.uiState.value.agreeTerms)
        viewModel.toggleAgreeTerms()
        assertTrue(viewModel.uiState.value.agreeTerms)

        viewModel.onNameChanged("Test")
        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorMessage)
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
}
