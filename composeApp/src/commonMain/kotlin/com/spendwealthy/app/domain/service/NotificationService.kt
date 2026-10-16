package com.spendwealthy.app.domain.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class AppNotification(
    val title: String,
    val message: String,
    val type: NotificationType = NotificationType.INFO
)

enum class NotificationType {
    INFO, SUCCESS, WARNING, ERROR, FAMILY
}

class NotificationService {
    private val _notifications = MutableSharedFlow<AppNotification>()
    val notifications: SharedFlow<AppNotification> = _notifications.asSharedFlow()

    suspend fun showNotification(title: String, message: String, type: NotificationType = NotificationType.INFO) {
        _notifications.emit(AppNotification(title, message, type))
    }
}
