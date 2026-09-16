package com.example.sp2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.model.HabitDurationType
import com.example.sp2.model.HabitFrequency
import com.example.sp2.model.HabitType

@Composable
fun CreateHabitDialog(
    onDismiss: () -> Unit,
    onCreate: (
        name: String,
        type: HabitType,
        frequency: HabitFrequency,
        targetCount: Int,
        durationType: HabitDurationType,
        totalPeriods: Int?
    ) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(HabitType.CHECKOFF) }
    var frequency by remember { mutableStateOf(HabitFrequency.DAILY) }
    var targetText by remember { mutableStateOf("8") }
    var durationType by remember { mutableStateOf(HabitDurationType.INDEFINITE) }
    var totalPeriodsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.habit_new))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(text = stringResource(R.string.habit_name))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.habit_type),
                    modifier = Modifier.padding(top = 12.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == HabitType.CHECKOFF,
                        onClick = { type = HabitType.CHECKOFF }
                    )
                    Text(text = stringResource(R.string.habit_type_checkoff))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == HabitType.COUNTER,
                        onClick = { type = HabitType.COUNTER }
                    )
                    Text(text = stringResource(R.string.habit_type_counter))
                }

                if (type == HabitType.COUNTER) {

                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { input ->
                            targetText = input.filter { it.isDigit() }
                        },
                        label = {
                            Text(text = stringResource(R.string.habit_target))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.habit_frequency),
                    modifier = Modifier.padding(top = 12.dp)
                )

                HabitFrequency.entries.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = frequency == option,
                            onClick = { frequency = option }
                        )
                        Text(text = frequencyLabel(option))
                    }
                }

                Text(
                    text = stringResource(R.string.habit_duration),
                    modifier = Modifier.padding(top = 12.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = durationType == HabitDurationType.INDEFINITE,
                        onClick = { durationType = HabitDurationType.INDEFINITE }
                    )
                    Text(text = stringResource(R.string.habit_duration_indefinite))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = durationType == HabitDurationType.FIXED,
                        onClick = { durationType = HabitDurationType.FIXED }
                    )
                    Text(text = stringResource(R.string.habit_duration_fixed))
                }

                if (durationType == HabitDurationType.FIXED) {

                    val periodsLabel = when (frequency) {
                        HabitFrequency.DAILY -> stringResource(R.string.habit_duration_days)
                        HabitFrequency.WEEKLY -> stringResource(R.string.habit_duration_weeks)
                        HabitFrequency.MONTHLY -> stringResource(R.string.habit_duration_months)
                    }

                    OutlinedTextField(
                        value = totalPeriodsText,
                        onValueChange = { input ->
                            totalPeriodsText = input.filter { it.isDigit() }
                        },
                        label = {
                            Text(text = periodsLabel)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {

                    val target = if (type == HabitType.COUNTER) {
                        targetText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    } else {
                        1
                    }

                    val totalPeriods = if (durationType == HabitDurationType.FIXED) {
                        totalPeriodsText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    } else {
                        null
                    }

                    onCreate(
                        name.trim(),
                        type,
                        frequency,
                        target,
                        durationType,
                        totalPeriods
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text(text = stringResource(R.string.action_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun frequencyLabel(frequency: HabitFrequency): String {
    return when (frequency) {
        HabitFrequency.DAILY -> stringResource(R.string.habit_frequency_daily)
        HabitFrequency.WEEKLY -> stringResource(R.string.habit_frequency_weekly)
        HabitFrequency.MONTHLY -> stringResource(R.string.habit_frequency_monthly)
    }
}