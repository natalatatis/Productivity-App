package com.example.sp2.ui.screens.reminders

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import com.example.sp2.alarms.AlarmScheduler
import com.example.sp2.model.Alarm
import com.example.sp2.ui.components.CreateAlarmDialog
import com.example.sp2.ui.components.EmptyState
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

private enum class RemindersTab { ALARMS, TIMER }

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onBack: () -> Unit,
    viewModel: RemindersViewModel = viewModel()
) {

    var tab by remember { mutableStateOf(RemindersTab.ALARMS) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reminders_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.nav_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            PermissionsBanner()

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                FilterChip(
                    selected = tab == RemindersTab.ALARMS,
                    onClick = { tab = RemindersTab.ALARMS },
                    label = { Text(stringResource(R.string.alarms_tab)) }
                )

                FilterChip(
                    selected = tab == RemindersTab.TIMER,
                    onClick = { tab = RemindersTab.TIMER },
                    label = { Text(stringResource(R.string.timer_tab)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (tab) {
                RemindersTab.ALARMS -> AlarmsTab(viewModel)
                RemindersTab.TIMER -> TimerTab(viewModel)
            }
        }
    }
}

@Composable
private fun AlarmsTab(
    viewModel: RemindersViewModel
) {

    val alarms by viewModel.alarms.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingAlarm by remember { mutableStateOf<Alarm?>(null) }
    var alarmToDelete by remember { mutableStateOf<Alarm?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (alarms.isEmpty()) {
            item {
                EmptyState(
                    title = stringResource(R.string.alarms_empty_title),
                    description = stringResource(R.string.alarms_empty_description)
                )
            }
        }

        items(
            items = alarms,
            key = { it.id }
        ) { alarm ->
            AlarmRow(
                alarm = alarm,
                onEdit = { editingAlarm = alarm },
                onToggle = { viewModel.toggleAlarm(alarm) },
                onDelete = { alarmToDelete = alarm }
            )
        }

        item {
            TextButton(onClick = { showCreateDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(text = stringResource(R.string.alarm_new))
            }
        }
    }

    if (showCreateDialog || editingAlarm != null) {

        CreateAlarmDialog(
            initialAlarm = editingAlarm,
            onDismiss = {
                showCreateDialog = false
                editingAlarm = null
            },
            onCreate = { time, label, days, soundUri, snoozeMinutes ->

                val current = editingAlarm

                if (current != null) {
                    viewModel.updateAlarm(
                        current.copy(
                            time = time,
                            label = label,
                            days = days,
                            soundUri = soundUri,
                            snoozeMinutes = snoozeMinutes
                        )
                    )
                } else {
                    viewModel.addAlarm(time, label, days, soundUri, snoozeMinutes)
                }

                showCreateDialog = false
                editingAlarm = null
            }
        )
    }

    alarmToDelete?.let { alarm ->
        AlertDialog(
            onDismissRequest = { alarmToDelete = null },
            title = { Text(stringResource(R.string.alarm_delete_title)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAlarm(alarm)
                        alarmToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.task_delete_list_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { alarmToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun AlarmRow(
    alarm: Alarm,
    onEdit: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = alarm.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.headlineSmall
                )

                if (alarm.label.isNotBlank()) {
                    Text(
                        text = alarm.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = if (alarm.specificDate != null) {
                        alarm.specificDate.format(
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )
                    } else {
                        daysLabel(alarm.days)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(checked = alarm.enabled, onCheckedChange = { onToggle() })

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.task_delete)
                )
            }
        }
    }
}

@Composable
private fun daysLabel(days: Set<DayOfWeek>): String {

    if (days.isEmpty()) {
        return stringResource(R.string.alarm_once)
    }

    val order = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )

    val labels = mapOf(
        DayOfWeek.MONDAY to stringResource(R.string.weekday_mon_letter),
        DayOfWeek.TUESDAY to stringResource(R.string.weekday_tue_letter),
        DayOfWeek.WEDNESDAY to stringResource(R.string.weekday_wed_letter),
        DayOfWeek.THURSDAY to stringResource(R.string.weekday_thu_letter),
        DayOfWeek.FRIDAY to stringResource(R.string.weekday_fri_letter),
        DayOfWeek.SATURDAY to stringResource(R.string.weekday_sat_letter),
        DayOfWeek.SUNDAY to stringResource(R.string.weekday_sun_letter)
    )

    return order.filter { it in days }.joinToString(", ") { labels[it] ?: "" }
}

@Composable
private fun TimerTab(
    viewModel: RemindersViewModel
) {

    val mode by viewModel.timerMode.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val countdownRemaining by viewModel.countdownRemainingSeconds.collectAsState()
    val stopwatchElapsedMillis by viewModel.stopwatchElapsedMillis.collectAsState()

    // Digits typed for the countdown, phone-dialer style:
    // typing shifts digits in from the right (e.g. "5" then "0" then
    // "0" builds 00:05:00)
    var typedDigits by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            FilterChip(
                selected = mode == TimerMode.COUNTDOWN,
                onClick = { viewModel.selectTimerMode(TimerMode.COUNTDOWN) },
                label = { Text(stringResource(R.string.timer_countdown)) }
            )

            FilterChip(
                selected = mode == TimerMode.STOPWATCH,
                onClick = { viewModel.selectTimerMode(TimerMode.STOPWATCH) },
                label = { Text(stringResource(R.string.timer_stopwatch)) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (mode == TimerMode.COUNTDOWN) {

            val hours = countdownRemaining / 3600
            val minutes = (countdownRemaining % 3600) / 60
            val seconds = countdownRemaining % 60

            Text(
                text = if (hours > 0) {
                    "%02d:%02d:%02d".format(hours, minutes, seconds)
                } else {
                    "%02d:%02d".format(minutes, seconds)
                },
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isRunning) {

                val padded = typedDigits.padStart(6, '0').takeLast(6)
                val hh = padded.substring(0, 2)
                val mm = padded.substring(2, 4)
                val ss = padded.substring(4, 6)

                Text(
                    text = "$hh:$mm:$ss",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("0", "⌫")
                )

                keypadRows.forEach { row ->

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        row.forEach { key ->

                            OutlinedButton(
                                onClick = {
                                    typedDigits = if (key == "⌫") {
                                        typedDigits.dropLast(1)
                                    } else {
                                        (typedDigits + key).takeLast(6)
                                    }
                                },
                                modifier = Modifier.width(72.dp)
                            ) {
                                Text(key)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        } else {

            val hours = (stopwatchElapsedMillis / 3_600_000).toInt()
            val minutes = ((stopwatchElapsedMillis % 3_600_000) / 60_000).toInt()
            val seconds = ((stopwatchElapsedMillis % 60_000) / 1000).toInt()
            val centis = ((stopwatchElapsedMillis % 1000) / 10).toInt()

            Text(
                text = if (hours > 0) {
                    "%02d:%02d:%02d.%02d".format(hours, minutes, seconds, centis)
                } else {
                    "%02d:%02d.%02d".format(minutes, seconds, centis)
                },
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            Button(
                onClick = {
                    if (isRunning) {
                        viewModel.pause()
                    } else {
                        if (mode == TimerMode.COUNTDOWN && typedDigits.isNotEmpty()) {
                            val padded = typedDigits.padStart(6, '0').takeLast(6)
                            val hh = padded.substring(0, 2).toInt()
                            val mm = padded.substring(2, 4).toInt()
                            val ss = padded.substring(4, 6).toInt()
                            viewModel.setCountdownSeconds(hh * 3600 + mm * 60 + ss)
                        }
                        viewModel.start()
                    }
                }
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRunning) {
                        stringResource(R.string.timer_pause)
                    } else {
                        stringResource(R.string.timer_start)
                    }
                )
            }

            // Lap/split button, only relevant while the stopwatch runs
            if (mode == TimerMode.STOPWATCH) {

                OutlinedButton(
                    onClick = { viewModel.lap() },
                    enabled = isRunning
                ) {
                    Text(stringResource(R.string.timer_lap))
                }
            }

            OutlinedButton(
                onClick = {
                    viewModel.reset()
                    typedDigits = ""
                }
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.timer_reset))
            }
        }

        // List of recorded laps, most recent on top
        if (mode == TimerMode.STOPWATCH) {

            val laps by viewModel.laps.collectAsState()

            if (laps.isNotEmpty()) {

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    itemsIndexed(laps) { index, lapMillis ->

                        val lapNumber = laps.size - index
                        val h = (lapMillis / 3_600_000).toInt()
                        val m = ((lapMillis % 3_600_000) / 60_000).toInt()
                        val s = ((lapMillis % 60_000) / 1000).toInt()
                        val c = ((lapMillis % 1000) / 10).toInt()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = stringResource(R.string.timer_lap_number, lapNumber),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = if (h > 0) {
                                    "%02d:%02d:%02d.%02d".format(h, m, s, c)
                                } else {
                                    "%02d:%02d.%02d".format(m, s, c)
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionsBanner() {

    val context = LocalContext.current
    var refreshKey by remember { mutableStateOf(0) }

    // Re-checks permissions every time this screen resumes,
    // e.g. after coming back from the Settings screen
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshKey++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationsGranted = remember(refreshKey) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    val exactAlarmsAllowed = remember(refreshKey) {
        AlarmScheduler.canScheduleExactAlarms(context)
    }

    val fullScreenAllowed = remember(refreshKey) {
        if (Build.VERSION.SDK_INT >= 34) {
            androidx.core.app.NotificationManagerCompat.from(context).canUseFullScreenIntent()
        } else {
            true
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        refreshKey++
    }

    if (!notificationsGranted) {

        PermissionCard(
            title = stringResource(R.string.alarm_permission_notifications_title),
            description = stringResource(R.string.alarm_permission_notifications_description),
            buttonText = stringResource(R.string.alarm_permission_enable),
            onClick = {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (!exactAlarmsAllowed) {

        PermissionCard(
            title = stringResource(R.string.alarm_permission_exact_title),
            description = stringResource(R.string.alarm_permission_exact_description),
            buttonText = stringResource(R.string.alarm_permission_enable),
            onClick = {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (!fullScreenAllowed) {

        PermissionCard(
            title = stringResource(R.string.alarm_permission_fullscreen_title),
            description = stringResource(R.string.alarm_permission_fullscreen_description),
            buttonText = stringResource(R.string.alarm_permission_enable),
            onClick = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
                ).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = title, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onClick) {
                Text(buttonText)
            }
        }
    }
}