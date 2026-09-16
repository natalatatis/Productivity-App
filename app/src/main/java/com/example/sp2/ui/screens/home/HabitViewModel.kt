package com.example.sp2.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.data.HabitsRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.Habit
import com.example.sp2.model.HabitDurationType
import com.example.sp2.model.HabitFrequency
import com.example.sp2.model.HabitType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        DatabaseProvider.getDatabase(application)

    private val repository =
        HabitsRepository(database.habitDao())

    val habits: StateFlow<List<Habit>> =
        repository.habits.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Settles any habits whose period ended while the app was closed
        viewModelScope.launch {
            repository.reconcileAll()
        }
    }

    fun addHabit(
        name: String,
        type: HabitType,
        frequency: HabitFrequency,
        targetCount: Int,
        durationType: HabitDurationType,
        totalPeriods: Int?
    ) {
        viewModelScope.launch {
            repository.addHabit(
                Habit(
                    name = name,
                    type = type,
                    frequency = frequency,
                    targetCount = targetCount,
                    durationType = durationType,
                    totalPeriods = totalPeriods
                )
            )
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun increment(habit: Habit) {
        if (habit.isChallengeFinished) return
        viewModelScope.launch {
            repository.incrementHabit(habit.id)
        }
    }

    fun decrement(habit: Habit) {
        if (habit.isChallengeFinished) return
        viewModelScope.launch {
            repository.decrementHabit(habit.id)
        }
    }
}