package com.mustafagur.marketim

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

class NotificationsClass : BroadcastReceiver() {

    companion object {
        public const val CHANNEL_ID = "MarketimChannelID"
        public const val CHANNEL_NAME = "Marketim Notifications"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNotification(context)
        }
    }

    private fun scheduleNotification(context: Context) {
        val intent = Intent(context, NotificationsClass::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        // Saati AM/PM formatına dönüştür
        val amPmFormat = SimpleDateFormat("a", Locale.getDefault())
        val amPm = amPmFormat.format(calendar.time)
        if (amPm == "AM") {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        calendar.set(Calendar.HOUR, 10)
        calendar.set(Calendar.MINUTE, 12)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.AM_PM, Calendar.PM)

        val currentTime = Calendar.getInstance().timeInMillis
        val selectedTime = calendar.timeInMillis

        if (selectedTime <= currentTime) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

}

