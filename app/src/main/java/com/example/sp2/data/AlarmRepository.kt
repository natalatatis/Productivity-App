package com.example.sp2.data

import com.example.sp2.data.local.dao.AlarmDao
import com.example.sp2.data.local.toAlarm
import com.example.sp2.data.local.toEntity
import com.example.sp2.data.local.toNewEntity
import com.example.sp2.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalTime

class AlarmRepository(
    private val alarmDao: AlarmDao
) {

    val alarms: Flow<List<Alarm>> = alarmDao.getAllAlarms().map { entities ->
        entities.map { it.toAlarm() }
    }

    // Returns the created alarm, id included, so the caller can
    // schedule it with AlarmManager right away
    suspend fun addAlarm(
        time: LocalTime,
        label: String,
        days: Set<DayOfWeek>,
        soundUri: String?,
        snoozeMinutes: Int,
        specificDate: java.time.LocalDate? = null
    ): Alarm {

        val entity = Alarm(
            time = time,
            label = label,
            days = days,
            soundUri = soundUri,
            snoozeMinutes = snoozeMinutes,
            specificDate = specificDate
        ).toNewEntity()

        val id = alarmDao.insertAlarm(entity)

        return entity.copy(id = id.toInt()).toAlarm()
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm.toEntity())
    }

    suspend fun toggleAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm.copy(enabled = !alarm.enabled).toEntity())
    }

    // Updates every editable field of an existing alarm
    suspend fun updateAlarmDetails(alarm: Alarm): Alarm {
        alarmDao.updateAlarm(alarm.toEntity())
        return alarm
    }
}