package com.example.sp2.data

import com.example.sp2.data.local.dao.AppUsageDao
import com.example.sp2.data.local.entity.AppUsageLogEntity
import com.example.sp2.data.local.entity.AppUsageStateEntity
import com.example.sp2.model.AppUsage
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AppUsageRepository(
    private val dao: AppUsageDao
) {

    suspend fun recordAppOpenAndGetUsage(): AppUsage {

        val today = LocalDate.now()

        dao.logDay(AppUsageLogEntity(date = today.toString()))

        val state = dao.getState()

        val newState = if (state == null) {

            AppUsageStateEntity(
                anchorDate = today.toString(),
                streak = 1,
                consecutiveMisses = 0
            )

        } else {

            val anchor = LocalDate.parse(state.anchorDate)
            val gapDays = ChronoUnit.DAYS.between(anchor, today).toInt()

            when {

                gapDays <= 0 -> state

                gapDays == 1 -> state.copy(
                    anchorDate = today.toString(),
                    streak = state.streak + 1,
                    consecutiveMisses = 0
                )

                else -> {

                    val missedDays = gapDays - 1
                    val newMisses = state.consecutiveMisses + missedDays

                    if (newMisses >= 7) {

                        state.copy(
                            anchorDate = today.toString(),
                            streak = 1,
                            consecutiveMisses = 0
                        )

                    } else {

                        state.copy(
                            anchorDate = today.toString(),
                            streak = state.streak + 1,
                            consecutiveMisses = newMisses
                        )
                    }
                }
            }
        }

        dao.upsertState(newState)

        val totalDaysUsed = dao.getTotalDaysUsed()

        // The current calendar week, Monday through Sunday
        val monday = today.with(DayOfWeek.MONDAY)
        val weekDates = (0..6).map { monday.plusDays(it.toLong()) }
        val weekDateStrings = weekDates.map { it.toString() }

        val usedDates = dao.getUsedDates(weekDateStrings).toSet()

        val weekUsage: List<Boolean?> = weekDates.map { date ->
            if (date.isAfter(today)) {
                null
            } else {
                date.toString() in usedDates
            }
        }

        return AppUsage(
            streak = newState.streak,
            totalDaysUsed = totalDaysUsed,
            weekUsage = weekUsage
        )
    }
}