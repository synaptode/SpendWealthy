package com.spendwealthy.app.domain.model

/**
 * Domain-level typed error hierarchy as defined in the architectural blueprint.
 * Eliminates unhandled exceptions leaking across architectural layers.
 */
sealed interface DomainError {
    data object Unauthorized : DomainError
    data object NotFound : DomainError
    data class Validation(val field: String, val reason: String? = null) : DomainError
    data object Offline : DomainError
    data class LimitExceeded(val message: String) : DomainError
    data class Unexpected(val cause: Throwable) : DomainError
}
