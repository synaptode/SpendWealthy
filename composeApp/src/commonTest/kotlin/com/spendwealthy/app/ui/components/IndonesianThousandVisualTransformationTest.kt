package com.spendwealthy.app.ui.components

import androidx.compose.ui.text.AnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals

class IndonesianThousandVisualTransformationTest {

    private val transformation = IndonesianThousandVisualTransformation()

    @Test
    fun testEmptyInput() {
        val result = transformation.filter(AnnotatedString(""))
        assertEquals("", result.text.text)
    }

    @Test
    fun testLessThanThousandInput() {
        val result = transformation.filter(AnnotatedString("500"))
        assertEquals("500", result.text.text)
    }

    @Test
    fun testThousandsInput() {
        val result = transformation.filter(AnnotatedString("5000"))
        assertEquals("5.000", result.text.text)
    }

    @Test
    fun testMillionsInput() {
        val result = transformation.filter(AnnotatedString("1500000"))
        assertEquals("1.500.000", result.text.text)
    }

    @Test
    fun testOffsetMappingBidirectional() {
        val input = "1500000" // Formatted: "1.500.000" (original len 7, formatted len 9)
        val result = transformation.filter(AnnotatedString(input))
        val mapping = result.offsetMapping

        // Original 0 -> Transformed 0
        assertEquals(0, mapping.originalToTransformed(0))
        // Original 1 ('1') -> Transformed 2 ('1.')
        assertEquals(2, mapping.originalToTransformed(1))
        // Original 4 ('1500') -> Transformed 6 ('1.500.')
        assertEquals(6, mapping.originalToTransformed(4))
        // Original 7 (end) -> Transformed 9 (end)
        assertEquals(9, mapping.originalToTransformed(7))

        // Transformed 0 -> Original 0
        assertEquals(0, mapping.transformedToOriginal(0))
        // Transformed 1 ('1') -> Original 1
        assertEquals(1, mapping.transformedToOriginal(1))
        // Transformed 2 ('1.') -> Original 1
        assertEquals(1, mapping.transformedToOriginal(2))
        // Transformed 3 ('1.5') -> Original 2
        assertEquals(2, mapping.transformedToOriginal(3))
        // Transformed 9 (end) -> Original 7
        assertEquals(7, mapping.transformedToOriginal(9))
    }
}
