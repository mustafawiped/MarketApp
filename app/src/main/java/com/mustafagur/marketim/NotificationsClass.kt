package com.mustafagur.marketim

import DatabaseHelper
import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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

    @SuppressLint("Range")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNotification(context)
        } else if (intent.action == "android.intent.action.MY_PACKAGE_REPLACED" ||
            intent.action == "com.mustafagur.marketim.SEND_NOTIFICATION"
        ) {
            val currentTime = Calendar.getInstance()
            val targetTime = Calendar.getInstance()
            targetTime.set(Calendar.HOUR_OF_DAY, 21)
            targetTime.set(Calendar.MINUTE, 30)
            targetTime.set(Calendar.SECOND, 0)
            targetTime.set(Calendar.MILLISECOND, 0)
            if (currentTime.timeInMillis >= targetTime.timeInMillis) {
                targetTime.add(Calendar.DAY_OF_MONTH, 1)
            }
            val alarmIntent = Intent(context, NotificationsClass::class.java)
            alarmIntent.action = "com.mustafagur.marketim.SEND_NOTIFICATION"
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                targetTime.timeInMillis,
                pendingIntent
            )
            scheduleNotification(context)
        }
    }


    fun kalanGunuHesapla(startDate: Calendar, endDate: Date): Int {
        val baslangic = startDate.clone() as Calendar
        val son = Calendar.getInstance()
        son.time = endDate
        son.set(Calendar.HOUR_OF_DAY, 0)
        son.set(Calendar.MINUTE, 0)
        son.set(Calendar.SECOND, 0)
        son.set(Calendar.MILLISECOND, 0)
        val milisaniye = 24 * 60 * 60 * 1000L
        val diff = son.timeInMillis - baslangic.timeInMillis
        return (diff / milisaniye).toInt()
    }

    @SuppressLint("Range")
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

        val now = Calendar.getInstance()
        createNotificationChannel(context)
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val durum = sharedPreferences.getBoolean("gunlukbildirimdurum", false)
        if (durum) {
            val alarm = sharedPreferences.getString("gunlukbildirimsaat", "21:30")
            val split = alarm?.split(":")
            if (split?.size == 2) {
                now.set(Calendar.HOUR_OF_DAY, split[0].toInt())
                now.set(Calendar.MINUTE, split[1].toInt())
                now.set(Calendar.SECOND, 0)
            } else {
                now.set(Calendar.HOUR_OF_DAY, 21)
                now.set(Calendar.MINUTE, 30)
                now.set(Calendar.SECOND, 0)
            }
        } else {
            now.set(Calendar.HOUR_OF_DAY, 21)
            now.set(Calendar.MINUTE, 30)
            now.set(Calendar.SECOND, 0)
        }

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

        // Bildirim gönderme işlemlerini buraya ekleyin
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
    private fun sendNotification(context: Context, notificationText: String, notificationTitle: String, notificationId: Int,notificationIntent: Intent) {
            val notificationIcon = R.drawable.logo
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
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
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }
