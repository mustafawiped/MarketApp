package com.mustafagur.marketim

import DatabaseHelper
import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
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
import android.content.Context as Context1

class NotificationsClass : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "MarketimChannelID"
        const val CHANNEL_NAME = "MarketimNotifications"
    }

    override fun onReceive(context: Context1, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNotification(context)
        }
    }

    private fun scheduleNotification(context: Context1) {
        val intent = Intent(context, NotificationsClass::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context1.ALARM_SERVICE) as AlarmManager
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
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        createNotificationChannel(context)
        sendNotification(context)
    }

    private fun getSelectedTime(calendar: Calendar): Long {
        val selectedCalendar = calendar.clone() as Calendar

        selectedCalendar.set(Calendar.HOUR_OF_DAY, 19)
        selectedCalendar.set(Calendar.MINUTE, 35)
        selectedCalendar.set(Calendar.SECOND, 0)

        val currentTime = Calendar.getInstance().timeInMillis
        val selectedTime = selectedCalendar.timeInMillis

        if (selectedTime <= currentTime) {
            selectedCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return selectedCalendar.timeInMillis
    }

    private fun createNotificationChannel(context: Context1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            val notificationManager =
                context.getSystemService(Context1.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("Range")
    private fun sendNotification(context: Context1) {
        val database = DatabaseHelper(context)
        val cursor = database.getAllData()
        if (cursor != null) {
            do {
                val currentDate = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val expiryDateString = cursor.getString(cursor.getColumnIndex("urunskt"))
                val expiryDate = dateFormat.parse(expiryDateString)
                if (expiryDate != null && expiryDate.time - currentDate.time < 30L * 24L * 60L * 60L * 1000L) {
                    val urunadi = cursor.getString(cursor.getColumnIndex("urunadi"))
                    val daysRemaining = ((expiryDate.time - currentDate.time) / (24L * 60L * 60L * 1000L)).toString()
                    val notificationText = "Selamlar! $urunadi isimli ürününün son kullanma tarihine $daysRemaining Gün kaldı."
                    val notificationTitle = "$urunadi'ın SKT'i Yaklaştı!"
                    val notificationIcon = R.drawable.logo
                    val notificationIntent = Intent(context, MainActivity::class.java)
                    notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(notificationIcon)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    val notificationManager = NotificationManagerCompat.from(context)
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    notificationManager.notify(0, notificationBuilder.build())
                }
            } while (cursor.moveToNext())
        }
    }
}
