package com.spendwealthy.app.data.gateway

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.spendwealthy.app.MainActivity
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.model.BillReminder
import java.util.Calendar

class AndroidNotificationScheduler(
    private val context: Context
) : NotificationScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun scheduleBillReminder(bill: BillReminder): GatewayResult<Unit> {
        if (!canPostNotifications()) {
            return GatewayResult.Failure("Izin notifikasi belum aktif.", recoverable = true)
        }

        ensureChannel()
        val pendingIntent = pendingIntentFor(bill)
        val triggerAtMillis = triggerAtMillisFor(bill)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }

        return GatewayResult.Success(Unit)
    }

    override suspend fun cancelBillReminder(billId: String): GatewayResult<Unit> {
        alarmManager.cancel(pendingIntentFor(billId))
        return GatewayResult.Success(Unit)
    }

    private fun pendingIntentFor(bill: BillReminder): PendingIntent {
        val intent = Intent(context, BillReminderReceiver::class.java).apply {
            putExtra(BillReminderReceiver.EXTRA_BILL_ID, bill.id)
            putExtra(BillReminderReceiver.EXTRA_TITLE, bill.name)
            putExtra(BillReminderReceiver.EXTRA_AMOUNT, bill.amount.format())
            putExtra(BillReminderReceiver.EXTRA_DUE_DATE, bill.dueDate.toString())
        }
        return PendingIntent.getBroadcast(
            context,
            bill.id.stableRequestCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun pendingIntentFor(billId: String): PendingIntent {
        val intent = Intent(context, BillReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            billId.stableRequestCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun triggerAtMillisFor(bill: BillReminder): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, bill.dueDate.year)
            set(Calendar.MONTH, bill.dueDate.monthNumber - 1)
            set(Calendar.DAY_OF_MONTH, bill.dueDate.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val trigger = calendar.timeInMillis
        return if (trigger > System.currentTimeMillis()) trigger else System.currentTimeMillis() + ONE_MINUTE_MS
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_BILLS,
            "Pengingat Tagihan",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifikasi jatuh tempo tagihan SpendWealthy"
        }
        manager.createNotificationChannel(channel)
    }

    private fun canPostNotifications(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    private fun String.stableRequestCode(): Int = fold(17) { acc, char -> acc * 31 + char.code }

    companion object {
        const val CHANNEL_BILLS = "spendwealthy_bill_reminders"
        private const val ONE_MINUTE_MS = 60_000L
    }
}

class BillReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty().ifBlank { "Tagihan" }
        val amount = intent.getStringExtra(EXTRA_AMOUNT).orEmpty()
        val dueDate = intent.getStringExtra(EXTRA_DUE_DATE).orEmpty()

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            title.stableRequestCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, AndroidNotificationScheduler.CHANNEL_BILLS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$title jatuh tempo")
            .setContentText("Tagihan $amount jatuh tempo $dueDate.")
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(title.stableRequestCode(), notification)
    }

    private fun String.stableRequestCode(): Int = fold(17) { acc, char -> acc * 31 + char.code }

    companion object {
        const val EXTRA_BILL_ID = "bill_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_AMOUNT = "amount"
        const val EXTRA_DUE_DATE = "due_date"
    }
}
