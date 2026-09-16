package com.example.sp2.data

import com.example.sp2.model.HabitFrequency
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object HabitPeriod {

    // How many full periods separate two dates, for a given frequency
    fun elapsedPeriods(
        frequency: HabitFrequency,
        from: LocalDate,
        to: LocalDate
    ): Int {
        return when (frequency) {

            HabitFrequency.DAILY ->
                ChronoUnit.DAYS.between(from, to).toInt()

            HabitFrequency.WEEKLY ->
                (ChronoUnit.DAYS.between(from, to) / 7).toInt()

            HabitFrequency.MONTHLY ->
                ChronoUnit.MONTHS.between(from, to).toInt()
        }
    }

    // How many consecutive missed periods it takes to break a streak
    // (a small grace window, so one bad day doesn't erase everything)
    fun graceThreshold(frequency: HabitFrequency): Int {
        return when (frequency) {
            HabitFrequency.DAILY -> 7
            HabitFrequency.WEEKLY -> 4
            HabitFrequency.MONTHLY -> 2
        }
    }
}