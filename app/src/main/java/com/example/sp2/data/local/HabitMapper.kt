package com.example.sp2.data.local

import com.example.sp2.data.local.entity.HabitEntity
import com.example.sp2.model.Habit
import com.example.sp2.model.HabitDurationType
import com.example.sp2.model.HabitFrequency
import com.example.sp2.model.HabitType
import java.time.LocalDate

fun HabitEntity.toHabit(): Habit {
    return Habit(
        id = id,
        name = name,
        type = HabitType.valueOf(type),
        frequency = HabitFrequency.valueOf(frequency),
        targetCount = targetCount,
        currentCount = currentCount,
        streak = streak,
        durationType = HabitDurationType.valueOf(durationType),
        totalPeriods = totalPeriods,
        totalHits = totalHits,
        totalMisses = totalMisses
    )
}

fun Habit.toNewEntity(): HabitEntity {
    return HabitEntity(
        id = 0,
        name = name,
        type = type.name,
        frequency = frequency.name,
        targetCount = targetCount,
        anchorDate = LocalDate.now().toString(),
        currentCount = 0,
        streak = 0,
        consecutiveMisses = 0,
        durationType = durationType.name,
        totalPeriods = totalPeriods,
        totalHits = 0,
        totalMisses = 0
    )
}