package com.mustafagur.marketim

import DatabaseHelper
import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

class NotificationsClass : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "MarketimChannelID"
        const val CHANNEL_NAME = "SKT Bildirimi"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNotification(context)
        } else if (intent.action == "com.mustafagur.marketim.SEND_NOTIFICATION") {
            sendNotification(context)
        }
    }

    public fun scheduleNotification(context: Context) {
        val intent = Intent(context, NotificationsClass::class.java)
        intent.action = "com.mustafagur.marketim.SEND_NOTIFICATION"
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        val currentTime = Calendar.getInstance().timeInMillis
        val selectedTime = getSelectedTime(calendar)

        if (selectedTime <= currentTime) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            selectedTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        createNotificationChannel(context)
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 23)   // Alarm saatini buradan ayarlıyon.
        now.set(Calendar.MINUTE, 51)
        now.set(Calendar.SECOND, 0)

        val alarmIntent = Intent(context, NotificationsClass::class.java)
        alarmIntent.action = "com.mustafagur.marketim.SEND_NOTIFICATION"
        val notificationPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            now.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            notificationPendingIntent
        )
    }

    private fun getSelectedTime(calendar: Calendar): Long {
        val selectedCalendar = calendar.clone() as Calendar

        selectedCalendar.set(Calendar.HOUR_OF_DAY, 23)
        selectedCalendar.set(Calendar.MINUTE, 35)
        selectedCalendar.set(Calendar.SECOND, 0)

        val currentTime = Calendar.getInstance().timeInMillis
        val selectedTime = selectedCalendar.timeInMillis

        if (selectedTime <= currentTime) {
            selectedCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return selectedCalendar.timeInMillis
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("Range")
    private fun sendNotification(context: Context) {
        val database = DatabaseHelper(context)
        val cursor = database.getAllData()
        if (cursor != null && cursor.moveToFirst()) {
            var sayac = 0
            do {
                val currentDate = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val expiryDateString = cursor.getString(cursor.getColumnIndex("urunskt"))
                val expiryDate = dateFormat.parse(expiryDateString)
                if (expiryDate != null && expiryDate.time - currentDate.time < 30L * 24L * 60L * 60L * 1000L) {
                    sayac += 1
                }
            } while (cursor.moveToNext())
            val notificationText = "Selamlar! $sayac tane ürünün son kullanma tarihi yaklaştı. Göz atmak için tıkla!"
            val notificationTitle = "SKT 'si yaklaşan ürünler var!"
            val notificationIcon = R.drawable.logo
            val notificationIntent = Intent(context, MainActivity::class.java)
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(notificationIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}
