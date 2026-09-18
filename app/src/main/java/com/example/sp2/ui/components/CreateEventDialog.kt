package com.example.sp2.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sp2.R
import com.example.sp2.model.Event
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventDialog(
    initialDate: LocalDate? = null,
    initialEvent: Event? = null,
    onDismiss: () -> Unit,
    onCreate: (title: String, description: String, date: LocalDate, time: LocalTime?) -> Unit
) {

    var title by remember { mutableStateOf(initialEvent?.title ?: "") }
    var description by remember { mutableStateOf(initialEvent?.description ?: "") }

    val startDate = initialEvent?.date ?: initialDate ?: LocalDate.now()

    var selectedDateMillis by remember {
        mutableStateOf(
            startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }

    var hasTime by remember { mutableStateOf(initialEvent?.time != null) }
    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = initialEvent?.time?.hour ?: 9,
        initialMinute = initialEvent?.time?.minute ?: 0,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {

        Surface(shape = RoundedCornerShape(24.dp)) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = if (initialEvent != null) {
                        stringResource(R.string.event_edit)
                    } else {
                        stringResource(R.string.event_new)
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(top = 12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(top = 8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.task_description)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(top = 16.dp))

                Text(text = stringResource(R.string.task_date))

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val date = Instant.ofEpochMilli(selectedDateMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
                    Text(date.toString())
                }

                Spacer(modifier = Modifier.padding(top = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(R.string.event_time))

                    TextButton(onClick = { hasTime = !hasTime }) {
                        Text(
                            if (hasTime) {
                                stringResource(R.string.event_remove_time)
                            } else {
                                stringResource(R.string.event_add_time)
                            }
                        )
                    }
                }

                if (hasTime) {

                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.action_cancel))
                    }

                    Button(
                        onClick = {
                            val date = Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()

                            val time = if (hasTime) {
                                LocalTime.of(timePickerState.hour, timePickerState.minute)
                            } else {
                                null
                            }

                            onCreate(title.trim(), description.trim(), date, time)
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text(
                            if (initialEvent != null) {
                                stringResource(R.string.task_save)
                            } else {
                                stringResource(R.string.action_create)
                            }
                        )
                    }
                }
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
                        selectedDateMillis = datePickerState.selectedDateMillis ?: selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {

        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(shape = RoundedCornerShape(24.dp)) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.padding(top = 12.dp))
                    Button(onClick = { showTimePicker = false }) {
                        Text(stringResource(R.string.action_confirm))
                    }
                }
            }
        }
    }
}