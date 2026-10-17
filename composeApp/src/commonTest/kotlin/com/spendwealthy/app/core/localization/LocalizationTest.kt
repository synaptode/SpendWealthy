package com.spendwealthy.app.core.localization

import com.spendwealthy.app.domain.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LocalizationTest {

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
    fun testAppLanguageCodeParsing() {
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromCode("id"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.MALAY, AppLanguage.fromCode("ms"))
        assertEquals(AppLanguage.THAI, AppLanguage.fromCode("th"))
        assertEquals(AppLanguage.CHINESE, AppLanguage.fromCode("zh"))
        assertEquals(AppLanguage.JAPANESE, AppLanguage.fromCode("ja"))
        assertEquals(AppLanguage.VIETNAMESE, AppLanguage.fromCode("vi"))
        assertEquals(AppLanguage.ARABIC, AppLanguage.fromCode("ar"))
        // Case insensitive check
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("EN"))
        assertEquals(AppLanguage.THAI, AppLanguage.fromCode("Th"))
        // Fallback for null or unknown
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromCode(null))
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromCode("invalid_code"))
    }

    @Test
    fun testAllLanguagesHaveNonEmptyStrings() {
        val languages = listOf(
            AppLanguage.INDONESIAN,
            AppLanguage.ENGLISH,
            AppLanguage.MALAY,
            AppLanguage.THAI,
            AppLanguage.CHINESE,
            AppLanguage.JAPANESE
        )

        for (lang in languages) {
            val strings = LocalizationManager.getStringsForLanguage(lang)
            assertNotNull(strings)
            assertTrue(strings.appName.isNotBlank(), "appName must not be blank for $lang")
            assertTrue(strings.tabHome.isNotBlank(), "tabHome must not be blank for $lang")
            assertTrue(strings.tabBudget.isNotBlank(), "tabBudget must not be blank for $lang")
            assertTrue(strings.quickActionSend.isNotBlank(), "quickActionSend must not be blank for $lang")
            assertTrue(strings.quickActionScan.isNotBlank(), "quickActionScan must not be blank for $lang")
            assertTrue(strings.settingsTitle.isNotBlank(), "settingsTitle must not be blank for $lang")
            assertTrue(strings.languageSectionTitle.isNotBlank(), "languageSectionTitle must not be blank for $lang")
        }
    }

    @Test
    fun testLocalizationManagerRuntimeSwitching() = runTest(testDispatcher) {
        val manager = LocalizationManager(
            preferenceRepository = fakePreferenceRepo,
            scope = backgroundScope
        )
        advanceUntilIdle()

        // Default is Indonesian
        assertEquals(AppLanguage.INDONESIAN, manager.currentLanguage.value)
        assertEquals("Beranda", manager.currentStrings.value.tabHome)

        // Switch to English
        manager.setLanguage(AppLanguage.ENGLISH)
        advanceUntilIdle()
        assertEquals(AppLanguage.ENGLISH, manager.currentLanguage.value)
        assertEquals("Home", manager.currentStrings.value.tabHome)

        // Switch to Thai
        manager.setLanguage(AppLanguage.THAI)
        advanceUntilIdle()
        assertEquals(AppLanguage.THAI, manager.currentLanguage.value)
        assertEquals("หน้าแรก", manager.currentStrings.value.tabHome)

        // Switch to Mandarin / Chinese
        manager.setLanguage(AppLanguage.CHINESE)
        advanceUntilIdle()
        assertEquals(AppLanguage.CHINESE, manager.currentLanguage.value)
        assertEquals("首页", manager.currentStrings.value.tabHome)

        // Switch to Malay
        manager.setLanguage(AppLanguage.MALAY)
        advanceUntilIdle()
        assertEquals(AppLanguage.MALAY, manager.currentLanguage.value)
        assertEquals("Utama", manager.currentStrings.value.tabHome)

        // Switch to Japanese
        manager.setLanguage(AppLanguage.JAPANESE)
        advanceUntilIdle()
        assertEquals(AppLanguage.JAPANESE, manager.currentLanguage.value)
        assertEquals("ホーム", manager.currentStrings.value.tabHome)
    }

    private class FakePreferenceRepository : PreferenceRepository {
        private val strings = mutableMapOf<String, MutableStateFlow<String?>>()
        private val booleans = mutableMapOf<String, MutableStateFlow<Boolean>>()
        private val ints = mutableMapOf<String, MutableStateFlow<Int>>()

        override fun getString(key: String): Flow<String?> {
            return strings.getOrPut(key) { MutableStateFlow(null) }
        }

        override suspend fun setString(key: String, value: String) {
            strings.getOrPut(key) { MutableStateFlow(null) }.value = value
        }

        override fun getBoolean(key: String): Flow<Boolean> {
            return booleans.getOrPut(key) { MutableStateFlow(false) }
        }

        override suspend fun setBoolean(key: String, value: Boolean) {
            booleans.getOrPut(key) { MutableStateFlow(false) }.value = value
        }

        override fun getInt(key: String): Flow<Int> {
            return ints.getOrPut(key) { MutableStateFlow(0) }
        }

        override suspend fun setInt(key: String, value: Int) {
            ints.getOrPut(key) { MutableStateFlow(0) }.value = value
        }
    }
}
