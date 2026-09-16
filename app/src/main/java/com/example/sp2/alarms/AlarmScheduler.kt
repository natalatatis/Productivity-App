package com.example.sp2.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.sp2.model.Alarm
import java.time.LocalDateTime
import java.time.ZoneId

// Handles scheduling and cancelling alarms with the OS AlarmManager
object AlarmScheduler {

    const val EXTRA_ALARM_ID = "extra_alarm_id"

    private fun pendingIntent(context: Context, alarmId: Int): PendingIntent {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
        }

        return PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // Whether this app is currently allowed to schedule EXACT alarms.
    // Always true below API 31, where no special permission exists.
    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(AlarmManager::class.java)
            return manager?.canScheduleExactAlarms() ?: true
        }
        return true
    }

    fun schedule(context: Context, alarm: Alarm) {

        if (!alarm.enabled) {
            cancel(context, alarm)
            return
        }

        scheduleAt(context, alarm, nextTriggerMillis(alarm))
    }

    // Schedules a one-time ring at "now + minutesFromNow",
    // used for snoozing without touching the alarm's regular pattern
    fun scheduleSnooze(context: Context, alarm: Alarm, minutesFromNow: Int) {
        val triggerMillis = System.currentTimeMillis() + minutesFromNow * 60_000L
        scheduleAt(context, alarm, triggerMillis)
    }

    private fun scheduleAt(context: Context, alarm: Alarm, triggerMillis: Long) {

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = pendingIntent(context, alarm.id)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !manager.canScheduleExactAlarms()
            ) {
                // Falls back to an approximate alarm if the exact-alarm
                // permission hasn't been granted yet
                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pending)
            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pending)
            }
        } catch (_: SecurityException) {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pending)
        }
    }

    fun cancel(context: Context, alarm: Alarm) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent(context, alarm.id))
    }

    // Computes the next moment (epoch millis) this alarm should ring,
    // based on its time and repeat days
    fun nextTriggerMillis(alarm: Alarm): Long {

        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now(zone)

        if (alarm.days.isEmpty()) {

            var candidate = now.toLocalDate().atTime(alarm.time)
            if (!candidate.isAfter(now)) {
                candidate = candidate.plusDays(1)
            }
            return candidate.atZone(zone).toInstant().toEpochMilli()
        }

        for (offset in 0..7) {
            val date = now.toLocalDate().plusDays(offset.toLong())
            if (date.dayOfWeek in alarm.days) {
                val candidate = date.atTime(alarm.time)
                if (candidate.isAfter(now)) {
                    return candidate.atZone(zone).toInstant().toEpochMilli()
                }
            }
        }

        return now.plusDays(7).atZone(zone).toInstant().toEpochMilli()
    }
}