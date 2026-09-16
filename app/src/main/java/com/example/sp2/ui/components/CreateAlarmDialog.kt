package com.example.sp2.ui.components

import android.media.RingtoneManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sp2.R
import com.example.sp2.model.Alarm
import java.time.DayOfWeek
import java.time.LocalTime
import androidx.compose.ui.platform.LocalContext

private val SolidBrushTransparent = SolidColor(Color.Transparent)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlarmDialog(
    initialAlarm: Alarm? = null,
    onDismiss: () -> Unit,
    onCreate: (
        time: LocalTime,
        label: String,
        days: Set<DayOfWeek>,
        soundUri: String?,
        snoozeMinutes: Int
    ) -> Unit
) {

    val context = LocalContext.current
    val now = LocalTime.now()

    val timePickerState = rememberTimePickerState(
        initialHour = initialAlarm?.time?.hour ?: now.hour,
        initialMinute = initialAlarm?.time?.minute ?: now.minute,
        is24Hour = true
    )

    var manualEntry by remember { mutableStateOf(false) }

    // Digit buffer for manual entry, phone-dialer style: typing
    // shifts digits in from the right, so "5" then "4" then "5"
    // builds "05:45" — and backspace naturally cascades across
    // the hour/minute boundary since it's a single buffer.
    // Starts empty (shows as 00:00) every time manual mode opens.
    var typedDigits by remember { mutableStateOf("") }

    val manualFocusRequester = remember { FocusRequester() }

    var label by remember { mutableStateOf(initialAlarm?.label ?: "") }
    var selectedDays by remember { mutableStateOf(initialAlarm?.days ?: setOf()) }
    var snoozeText by remember {
        mutableStateOf((initialAlarm?.snoozeMinutes ?: 10).toString())
    }

    val sounds = remember {
        val list = mutableListOf<Pair<String, String>>()
        val manager = RingtoneManager(context)
        manager.setType(RingtoneManager.TYPE_ALARM)
        val cursor = manager.cursor
        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = manager.getRingtoneUri(cursor.position).toString()
            list.add(title to uri)
        }
        list
    }

    val defaultSoundUri = remember {
        initialAlarm?.soundUri
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString()
    }

    var selectedSoundUri by remember { mutableStateOf(defaultSoundUri) }
    var showSoundMenu by remember { mutableStateOf(false) }

    val selectedSoundTitle = sounds.firstOrNull { it.second == selectedSoundUri }?.first
        ?: stringResource(R.string.alarm_sound_default)

    // Reads the currently intended time, whichever mode is active
    fun currentTime(): LocalTime {
        return if (manualEntry) {
            val padded = typedDigits.padStart(4, '0').takeLast(4)
            val hh = padded.substring(0, 2).toIntOrNull()?.coerceIn(0, 23) ?: 0
            val mm = padded.substring(2, 4).toIntOrNull()?.coerceIn(0, 59) ?: 0
            LocalTime.of(hh, mm)
        } else {
            LocalTime.of(timePickerState.hour, timePickerState.minute)
        }
    }

    Dialog(onDismissRequest = onDismiss) {

        Surface(shape = RoundedCornerShape(24.dp)) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = if (initialAlarm != null) {
                        stringResource(R.string.alarm_edit)
                    } else {
                        stringResource(R.string.alarm_new)
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(top = 12.dp))

                if (manualEntry) {

                    val padded = typedDigits.padStart(4, '0').takeLast(4)
                    val hh = padded.substring(0, 2)
                    val mm = padded.substring(2, 4)

                    LaunchedEffect(Unit) {
                        manualFocusRequester.requestFocus()
                    }

                    // A transparent BasicTextField captures the real
                    // keyboard input (so backspace/typing behave
                    // exactly like a normal text field, cascading
                    // across HH/MM automatically since it's one
                    // buffer) — the formatted "HH:MM" Text drawn on
                    // top is purely visual and never itself editable
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        BasicTextField(
                            value = typedDigits,
                            onValueChange = { input ->
                                typedDigits = input.filter { it.isDigit() }.takeLast(4)
                            },
                            textStyle = MaterialTheme.typography.displayMedium.copy(
                                color = Color.Transparent
                            ),
                            cursorBrush = SolidBrushTransparent,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .width(180.dp)
                                .height(64.dp)
                                .focusRequester(manualFocusRequester)
                        )

                        Text(
                            text = "$hh:$mm",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }

                } else {

                    TimePicker(state = timePickerState)
                }

                Spacer(modifier = Modifier.padding(top = 4.dp))

                // The only way to switch modes now — a plain button,
                // no gestures involved
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {

                            if (!manualEntry) {
                                // Entering manual mode always starts
                                // blank (00:00), as requested
                                typedDigits = ""
                            } else {
                                // Leaving manual mode: carry over
                                // whatever was typed into the dial
                                val time = currentTime()
                                timePickerState.hour = time.hour
                                timePickerState.minute = time.minute
                            }

                            manualEntry = !manualEntry
                        }
                    ) {
                        Text(
                            text = if (manualEntry) {
                                stringResource(R.string.alarm_use_dial)
                            } else {
                                stringResource(R.string.alarm_use_manual)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(top = 16.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(stringResource(R.string.alarm_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(top = 16.dp))

                Text(text = stringResource(R.string.alarm_repeat))

                Spacer(modifier = Modifier.padding(top = 4.dp))

                val days = listOf(
                    DayOfWeek.MONDAY to stringResource(R.string.weekday_mon_letter),
                    DayOfWeek.TUESDAY to stringResource(R.string.weekday_tue_letter),
                    DayOfWeek.WEDNESDAY to stringResource(R.string.weekday_wed_letter),
                    DayOfWeek.THURSDAY to stringResource(R.string.weekday_thu_letter),
                    DayOfWeek.FRIDAY to stringResource(R.string.weekday_fri_letter),
                    DayOfWeek.SATURDAY to stringResource(R.string.weekday_sat_letter),
                    DayOfWeek.SUNDAY to stringResource(R.string.weekday_sun_letter)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    days.forEach { (day, dayLabel) ->

                        val selected = day in selectedDays

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .clickable {
                                    selectedDays = if (selected) {
                                        selectedDays - day
                                    } else {
                                        selectedDays + day
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayLabel,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 6.dp))

                Text(
                    text = if (selectedDays.isEmpty()) {
                        stringResource(R.string.alarm_once)
                    } else {
                        days.filter { it.first in selectedDays }
                            .joinToString(", ") { it.second }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.padding(top = 16.dp))

                Text(text = stringResource(R.string.alarm_sound))

                Spacer(modifier = Modifier.padding(top = 4.dp))

                Column {

                    OutlinedButton(
                        onClick = { showSoundMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedSoundTitle)
                    }

                    DropdownMenu(
                        expanded = showSoundMenu,
                        onDismissRequest = { showSoundMenu = false }
                    ) {
                        sounds.forEach { (title, uri) ->
                            DropdownMenuItem(
                                text = { Text(title) },
                                onClick = {
                                    selectedSoundUri = uri
                                    showSoundMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 16.dp))

                OutlinedTextField(
                    value = snoozeText,
                    onValueChange = { input ->
                        snoozeText = input.filter { it.isDigit() }
                    },
                    label = { Text(stringResource(R.string.alarm_snooze_minutes)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

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
                            onCreate(
                                currentTime(),
                                label.trim(),
                                selectedDays,
                                selectedSoundUri,
                                snoozeText.toIntOrNull()?.coerceAtLeast(1) ?: 10
                            )
                        }
                    ) {
                        Text(
                            text = if (initialAlarm != null) {
                                stringResource(R.string.alarm_save)
                            } else {
                                stringResource(R.string.action_create)
                            }
                        )
                    }
                }
            }
        }
    }
}