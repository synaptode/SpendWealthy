package com.spendwealthy.app.data.gateway

import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.model.BillReminder
import platform.Foundation.NSDateComponents
import platform.Foundation.NSNumber
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Native iOS Notification Scheduler using UNUserNotificationCenter.
 * Reliably schedules offline bill payment reminders with system audio alert and badge count.
 */
class IosNotificationScheduler : NotificationScheduler {

    private val center: UNUserNotificationCenter
        get() = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun scheduleBillReminder(bill: BillReminder): GatewayResult<Unit> {
        val authorized = requestAuthorization()
        if (!authorized) {
            return GatewayResult.Failure("Izin notifikasi iOS belum diberikan oleh pengguna.", recoverable = true)
        }

        return runCatching {
            val content = UNMutableNotificationContent().apply {
                setTitle("Pengingat Tagihan: ${bill.name} 🐾")
                setBody("Tagihan ${bill.amount.format()} jatuh tempo pada ${bill.dueDate}. Yuk bayar tepat waktu agar bebas denda!")
                setSound(UNNotificationSound.defaultSound())
                setBadge(NSNumber(int = 1))
            }

            // Schedule for 09:00 AM on the due date
            val components = NSDateComponents().apply {
                year = bill.dueDate.year.toLong()
                month = bill.dueDate.monthNumber.toLong()
                day = bill.dueDate.dayOfMonth.toLong()
                hour = 9
                minute = 0
            }

            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = components,
                repeats = false
            )

            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = bill.id,
                content = content,
                trigger = trigger
            )

            suspendCoroutine<GatewayResult<Unit>> { cont ->
                center.addNotificationRequest(request) { error ->
                    if (error != null) {
                        cont.resume(GatewayResult.Failure(error.localizedDescription, recoverable = true))
                    } else {
                        cont.resume(GatewayResult.Success(Unit))
                    }
                }
            }
        }.getOrElse { error ->
            GatewayResult.Failure(error.message ?: "Gagal menjadwalkan notifikasi di iOS.", recoverable = true)
        }
    }

    override suspend fun cancelBillReminder(billId: String): GatewayResult<Unit> {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(billId))
        return GatewayResult.Success(Unit)
    }

    private suspend fun requestAuthorization(): Boolean {
        return suspendCoroutine { continuation ->
            val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            center.requestAuthorizationWithOptions(options) { granted, _ ->
                continuation.resume(granted)
            }
        }
    }
}
