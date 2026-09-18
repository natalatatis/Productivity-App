package com.example.sp2.ui.screens.reminders

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.alarms.AlarmScheduler
import com.example.sp2.data.AlarmRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.Alarm
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime

enum class TimerMode {
    COUNTDOWN,
    STOPWATCH
}

class RemindersViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        AlarmRepository(DatabaseProvider.getDatabase(application).alarmDao())

    val alarms: StateFlow<List<Alarm>> = repository.alarms.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addAlarm(
        time: LocalTime,
        label: String,
        days: Set<DayOfWeek>,
        soundUri: String?,
        snoozeMinutes: Int,
        specificDate: java.time.LocalDate? = null
    ) {
        viewModelScope.launch {
            val alarm = repository.addAlarm(time, label, days, soundUri, snoozeMinutes, specificDate)
            AlarmScheduler.schedule(getApplication(), alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            AlarmScheduler.cancel(getApplication(), alarm)
            repository.deleteAlarm(alarm)
        }
    }

    fun toggleAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.toggleAlarm(alarm)

            val updated = alarm.copy(enabled = !alarm.enabled)

            if (updated.enabled) {
                AlarmScheduler.schedule(getApplication(), updated)
            } else {
                AlarmScheduler.cancel(getApplication(), updated)
            }
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            // Cancels the old schedule first, in case the time/days changed
            AlarmScheduler.cancel(getApplication(), alarm)
            val updated = repository.updateAlarmDetails(alarm)
            AlarmScheduler.schedule(getApplication(), updated)
        }
    }

    // --- Timer / Stopwatch ---

    private var tickJob: Job? = null

    private val _timerMode = MutableStateFlow(TimerMode.COUNTDOWN)
    val timerMode: StateFlow<TimerMode> = _timerMode.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _countdownTotalSeconds = MutableStateFlow(5 * 60)
    val countdownTotalSeconds: StateFlow<Int> = _countdownTotalSeconds.asStateFlow()

    private val _countdownRemainingSeconds = MutableStateFlow(5 * 60)
    val countdownRemainingSeconds: StateFlow<Int> = _countdownRemainingSeconds.asStateFlow()

    private val _stopwatchElapsedMillis = MutableStateFlow(0L)
    val stopwatchElapsedMillis: StateFlow<Long> = _stopwatchElapsedMillis.asStateFlow()

    private var stopwatchStartRealtime = 0L
    private var stopwatchAccumulatedMillis = 0L

    // Recorded lap/split times, most recent first
    private val _laps = MutableStateFlow<List<Long>>(emptyList())
    val laps: StateFlow<List<Long>> = _laps.asStateFlow()

    fun lap() {
        if (_timerMode.value != TimerMode.STOPWATCH || !_isRunning.value) return
        _laps.value = listOf(_stopwatchElapsedMillis.value) + _laps.value
    }

    fun selectTimerMode(mode: TimerMode) {
        stopTicking()
        _isRunning.value = false
        _timerMode.value = mode
    }

    // Sets the countdown duration from a total number of seconds
    // (supports hours — any value over 3600 works fine)
    fun setCountdownSeconds(totalSeconds: Int) {
        val seconds = totalSeconds.coerceAtLeast(1)
        _countdownTotalSeconds.value = seconds
        _countdownRemainingSeconds.value = seconds
    }

    fun start() {
        if (_isRunning.value) return
        _isRunning.value = true

        when (_timerMode.value) {

            TimerMode.COUNTDOWN -> {
                tickJob = viewModelScope.launch {
                    while (true) {
                        delay(1000)
                        val next = _countdownRemainingSeconds.value - 1
                        if (next <= 0) {
                            _countdownRemainingSeconds.value = 0
                            _isRunning.value = false
                            break
                        } else {
                            _countdownRemainingSeconds.value = next
                        }
                    }
                }
            }

            TimerMode.STOPWATCH -> {
                stopwatchStartRealtime = SystemClock.elapsedRealtime()

                tickJob = viewModelScope.launch {
                    while (true) {
                        _stopwatchElapsedMillis.value = stopwatchAccumulatedMillis +
                                (SystemClock.elapsedRealtime() - stopwatchStartRealtime)
                        delay(30)
                    }
                }
            }
        }
    }

    fun pause() {
        if (_timerMode.value == TimerMode.STOPWATCH) {
            stopwatchAccumulatedMillis = _stopwatchElapsedMillis.value
        }
        stopTicking()
        _isRunning.value = false
    }

    fun reset() {
        stopTicking()
        _isRunning.value = false
        _countdownRemainingSeconds.value = _countdownTotalSeconds.value
        stopwatchAccumulatedMillis = 0L
        _stopwatchElapsedMillis.value = 0L
        _laps.value = emptyList()
    }

    private fun stopTicking() {
        tickJob?.cancel()
        tickJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTicking()
    }
}