package com.example.sp2.data.local

import com.example.sp2.data.local.entity.AlarmEntity
import com.example.sp2.model.Alarm
import java.time.DayOfWeek
import java.time.LocalTime

fun AlarmEntity.toAlarm(): Alarm {

    val parsedDays = if (days.isBlank()) {
        emptySet()
    } else {
        days.split(",").map { DayOfWeek.valueOf(it) }.toSet()
    }

    return Alarm(
        id = id,
        time = LocalTime.of(hour, minute),
        label = label,
        days = parsedDays,
        enabled = enabled,
        soundUri = soundUri,
        snoozeMinutes = snoozeMinutes
    )
}

fun Alarm.toNewEntity(): AlarmEntity {
    return AlarmEntity(
        id = 0,
        hour = time.hour,
        minute = time.minute,
        label = label,
        days = days.joinToString(",") { it.name },
        enabled = true,
        soundUri = soundUri,
        snoozeMinutes = snoozeMinutes
    )
}

fun Alarm.toEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        hour = time.hour,
        minute = time.minute,
        label = label,
        days = days.joinToString(",") { it.name },
        enabled = enabled,
        soundUri = soundUri,
        snoozeMinutes = snoozeMinutes
    )
}