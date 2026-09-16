package com.example.sp2.data

import com.example.sp2.data.local.dao.HabitDao
import com.example.sp2.data.local.entity.HabitEntity
import com.example.sp2.data.local.toHabit
import com.example.sp2.data.local.toNewEntity
import com.example.sp2.model.Habit
import com.example.sp2.model.HabitFrequency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitsRepository(
    private val habitDao: HabitDao
) {

    val habits: Flow<List<Habit>> =
        habitDao.getAllHabits().map { entities ->
            entities.map { it.toHabit() }
        }

    suspend fun addHabit(habit: Habit) {
        habitDao.insertHabit(habit.toNewEntity())
    }

    suspend fun deleteHabit(habit: Habit) {
        val entity = habitDao.getHabitById(habit.id) ?: return
        habitDao.deleteHabit(entity)
    }

    // Settles every period that has fully elapsed since this habit's
    // progress was last touched: updates its streak (with a grace
    // window before breaking it) and its accumulated hit/miss
    // counters, then starts the current period fresh
    private suspend fun reconcile(entity: HabitEntity): HabitEntity {

        val frequency = HabitFrequency.valueOf(entity.frequency)
        val anchor = LocalDate.parse(entity.anchorDate)
        val today = LocalDate.now()

        val gapPeriods = HabitPeriod.elapsedPeriods(frequency, anchor, today)

        if (gapPeriods <= 0) {
            return entity
        }

        val wasHit = entity.currentCount >= entity.targetCount

        // Periods that passed entirely unseen, after the tracked one
        val skippedPeriods = gapPeriods - 1

        val graceThreshold = HabitPeriod.graceThreshold(frequency)

        val newConsecutiveMisses = if (wasHit) {
            skippedPeriods
        } else {
            entity.consecutiveMisses + 1 + skippedPeriods
        }

        val newStreak = if (newConsecutiveMisses >= graceThreshold) {
            0
        } else if (wasHit) {
            entity.streak + 1
        } else {
            entity.streak
        }

        val settled = entity.copy(
            anchorDate = today.toString(),
            currentCount = 0,
            streak = newStreak,
            consecutiveMisses = newConsecutiveMisses,
            totalHits = entity.totalHits + if (wasHit) 1 else 0,
            totalMisses = entity.totalMisses +
                    (if (wasHit) 0 else 1) + skippedPeriods
        )

        habitDao.updateHabit(settled)

        return settled
    }

    suspend fun reconcileAll() {
        habitDao.getAllHabitsOnce().forEach { reconcile(it) }
    }

    suspend fun incrementHabit(habitId: Int) {
        var entity = habitDao.getHabitById(habitId) ?: return
        entity = reconcile(entity)

        val newCount = (entity.currentCount + 1)
            .coerceAtMost(entity.targetCount)

        habitDao.updateHabit(entity.copy(currentCount = newCount))
    }

    suspend fun decrementHabit(habitId: Int) {
        var entity = habitDao.getHabitById(habitId) ?: return
        entity = reconcile(entity)

        val newCount = (entity.currentCount - 1).coerceAtLeast(0)

        habitDao.updateHabit(entity.copy(currentCount = newCount))
    }
}