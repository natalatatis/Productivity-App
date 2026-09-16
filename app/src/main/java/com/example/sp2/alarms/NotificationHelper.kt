package com.example.sp2.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sp2.R
import com.example.sp2.model.Alarm

object NotificationHelper {

    const val CHANNEL_ID = "alarms_channel"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val manager = context.getSystemService(NotificationManager::class.java)

            if (manager.getNotificationChannel(CHANNEL_ID) == null) {

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.alarms_notification_channel),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(
                        R.string.alarms_notification_channel_description
                    )
                    enableVibration(true)
                }

                manager.createNotificationChannel(channel)
            }
        }
    }

    fun showAlarmNotification(context: Context, alarm: Alarm) {

        ensureChannel(context)

        val fullScreenIntent = Intent(context, AlarmRingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarm.id)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarm.id,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = alarm.label.ifBlank { context.getString(R.string.alarm_new) }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(alarm.time.toString())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(alarm.id, notification)
    }

    fun dismiss(context: Context, alarmId: Int) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.cancel(alarmId)
    }
}