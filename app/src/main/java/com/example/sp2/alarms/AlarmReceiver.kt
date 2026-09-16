package com.example.sp2.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.data.local.toAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Fires when an alarm's scheduled time arrives
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val alarmId = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_ID, -1)
        if (alarmId == -1) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val dao = DatabaseProvider.getDatabase(context.applicationContext).alarmDao()
                val entity = dao.getAlarmById(alarmId)

                if (entity != null && entity.enabled) {

                    val alarm = entity.toAlarm()

                    NotificationHelper.showAlarmNotification(context.applicationContext, alarm)

                    // Repeating alarms: line up the following occurrence
                    if (alarm.days.isNotEmpty()) {
                        AlarmScheduler.schedule(context.applicationContext, alarm)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}