package com.spendwealthy.app.core.architecture

import androidx.compose.runtime.Composable

/**
 * Abstraction for UI strings that decouples ViewModels from Android Context / platform resources.
 */
sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
    }

    fun asRawString(): String = when (this) {
        is DynamicString -> value
    }
}
