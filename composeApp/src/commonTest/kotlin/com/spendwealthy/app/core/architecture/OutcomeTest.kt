package com.spendwealthy.app.core.architecture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OutcomeTest {

    @Test
    fun testSuccessProperties() {
        val outcome: Outcome<String, Int> = Outcome.Success("hello")
        assertTrue(outcome.isSuccess)
        assertFalse(outcome.isFailure)
        assertEquals("hello", outcome.getOrNull())
        assertNull(outcome.errorOrNull())
    }

    @Test
    fun testFailureProperties() {
        val outcome: Outcome<String, Int> = Outcome.Failure(404)
        assertFalse(outcome.isSuccess)
        assertTrue(outcome.isFailure)
        assertNull(outcome.getOrNull())
        assertEquals(404, outcome.errorOrNull())
    }

    @Test
    fun testMapSuccess() {
        val outcome: Outcome<Int, String> = Outcome.Success(10)
        val mapped = outcome.map { it * 2 }
        assertEquals(20, mapped.getOrNull())
    }

    @Test
    fun testMapFailurePassThrough() {
        val outcome: Outcome<Int, String> = Outcome.Failure("error")
        val mapped = outcome.map { it * 2 }
        assertTrue(mapped.isFailure)
        assertEquals("error", mapped.errorOrNull())
    }

    @Test
    fun testFlatMapSuccess() {
        val outcome: Outcome<Int, String> = Outcome.Success(10)
        val flatMapped = outcome.flatMap { Outcome.Success("Value is $it") }
        assertEquals("Value is 10", flatMapped.getOrNull())
    }

    @Test
    fun testCallbacks() {
        var successCalled = false
        var failureCalled = false

        Outcome.Success("ok")
            .onSuccess { successCalled = true }
            .onFailure { failureCalled = true }

        assertTrue(successCalled)
        assertFalse(failureCalled)

        successCalled = false
        failureCalled = false

        Outcome.Failure("err")
            .onSuccess { successCalled = true }
            .onFailure { failureCalled = true }

        assertFalse(successCalled)
        assertTrue(failureCalled)
    }
}
