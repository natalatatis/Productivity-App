package com.example.sp2.model

data class Habit(
    val id: Int = 0,
    val name: String,
    val type: HabitType = HabitType.CHECKOFF,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val streak: Int = 0,
    val durationType: HabitDurationType = HabitDurationType.INDEFINITE,
    val totalPeriods: Int? = null,
    val totalHits: Int = 0,
    val totalMisses: Int = 0
) {

    val isCompleted: Boolean
        get() = currentCount >= targetCount

    // How many periods (days/weeks/months) have been settled so far
    val periodsElapsed: Int
        get() = totalHits + totalMisses

    // True once a fixed-duration challenge has run its full length
    val isChallengeFinished: Boolean
        get() = durationType == HabitDurationType.FIXED &&
                totalPeriods != null &&
                periodsElapsed >= totalPeriods
}