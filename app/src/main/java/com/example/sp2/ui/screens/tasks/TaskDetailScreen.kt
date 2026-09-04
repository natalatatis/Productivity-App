package com.example.sp2.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.model.Priority
import com.example.sp2.model.ReminderFrequency
import com.example.sp2.model.RepeatFrequency
import com.example.sp2.model.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// Displays the screen for editing an existing task
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    task: Task,
    onSave: (Task) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {

    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var reminder by remember { mutableStateOf(task.reminder) }
    var repeat by remember { mutableStateOf(task.repeat) }

    // Pre-fills the date picker with the task's current date, if it has one
    var selectedDateMillis by remember {
        mutableStateOf(
            task.date
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
        )
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.task_edit_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.nav_back)
                        )
                    }
                },
                actions = {
                    // Button for deleting the task directly from this screen
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.task_delete)
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.task_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.task_description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = stringResource(R.string.task_priority))

            Priority.values().forEach { option ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(
                        selected = priority == option,
                        onClick = { priority = option }
                    )
                    Text(
                        text = priorityText(option),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            Text(text = stringResource(R.string.task_date))

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {

                val selectedDate = selectedDateMillis?.let { millis ->
                    Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }

                Text(
                    text = selectedDate?.toString()
                        ?: stringResource(R.string.task_select_date)
                )
            }

            Text(text = stringResource(R.string.task_reminder))

            ReminderFrequency.values().forEach { option ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(
                        selected = reminder == option,
                        onClick = { reminder = option }
                    )
                    Text(
                        text = reminderText(option),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            Text(text = stringResource(R.string.task_repeat))

            RepeatFrequency.values().forEach { option ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(
                        selected = repeat == option,
                        onClick = { repeat = option }
                    )
                    Text(
                        text = repeatText(option),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            // Saves the changes when pressed
            Button(
                onClick = {

                    val selectedDate: LocalDate? = selectedDateMillis?.let { millis ->
                        Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }

                    onSave(
                        task.copy(
                            title = title.trim(),
                            description = description.trim(),
                            priority = priority,
                            date = selectedDate,
                            reminder = reminder,
                            repeat = repeat
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text(text = stringResource(R.string.task_save))
            }
        }
    }

    if (showDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun priorityText(priority: Priority): String {
    return when (priority) {
        Priority.NONE -> stringResource(R.string.task_no_priority)
        Priority.LOW -> stringResource(R.string.priority_low)
        Priority.MEDIUM -> stringResource(R.string.priority_medium)
        Priority.HIGH -> stringResource(R.string.priority_high)
    }
}

@Composable
private fun reminderText(reminder: ReminderFrequency): String {
    return when (reminder) {
        ReminderFrequency.NONE -> stringResource(R.string.reminder_none)
        ReminderFrequency.ONCE -> stringResource(R.string.reminder_once)
        ReminderFrequency.DAILY -> stringResource(R.string.reminder_daily)
        ReminderFrequency.WEEKLY -> stringResource(R.string.reminder_weekly)
        ReminderFrequency.MONTHLY -> stringResource(R.string.reminder_monthly)
    }
}

@Composable
private fun repeatText(repeat: RepeatFrequency): String {
    return when (repeat) {
        RepeatFrequency.NONE -> stringResource(R.string.repeat_none)
        RepeatFrequency.DAILY -> stringResource(R.string.repeat_daily)
        RepeatFrequency.WEEKLY -> stringResource(R.string.repeat_weekly)
        RepeatFrequency.MONTHLY -> stringResource(R.string.repeat_monthly)
        RepeatFrequency.YEARLY -> stringResource(R.string.repeat_yearly)
    }
}