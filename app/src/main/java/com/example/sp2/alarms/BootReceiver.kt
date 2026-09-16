package com.example.sp2.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.data.local.toAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Re-schedules every enabled alarm after the device restarts,
// since AlarmManager forgets everything on reboot
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = DatabaseProvider.getDatabase(context.applicationContext).alarmDao()
                val entities = dao.getAllAlarmsOnce()

                entities.filter { it.enabled }.forEach { entity ->
                    AlarmScheduler.schedule(context.applicationContext, entity.toAlarm())
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}