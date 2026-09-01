package com.example.sp2.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.data.TaskManager
import com.example.sp2.model.Priority

// Displays the screen for creating a new task
@Composable
fun AddTaskScreen(
    onTaskAdded: () -> Unit
) {

    // Stores the task information entered by the user
    var title by rememberSaveable {
        mutableStateOf("")
    }

    var description by rememberSaveable {
        mutableStateOf("")
    }

    var priority by rememberSaveable {
        mutableStateOf(Priority.NONE)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Screen title
        Text(
            text = stringResource(
                R.string.task_add
            )
        )

        // Field for the task title
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = {
                Text(
                    text = stringResource(
                        R.string.task_title
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Field for the task description
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text(
                    text = stringResource(
                        R.string.task_description
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Priority selection
        Text(
            text = stringResource(
                R.string.task_priority
            )
        )

        Priority.values().forEach { option ->

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                RadioButton(
                    selected = priority == option,
                    onClick = {
                        priority = option
                    }
                )

                Text(
                    text = priorityText(option),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        // Saves the task when the button is pressed
        Button(
            onClick = {

                TaskManager.addTask(
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority
                )

                onTaskAdded()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {

            Text(
                text = stringResource(
                    R.string.task_add
                )
            )
        }
    }
}

// Returns the localized text for each priority level
@Composable
private fun priorityText(
    priority: Priority
): String {

    return when (priority) {

        Priority.NONE -> stringResource(
            R.string.task_no_priority
        )

        Priority.LOW -> stringResource(
            R.string.priority_low
        )

        Priority.MEDIUM -> stringResource(
            R.string.priority_medium
        )

        Priority.HIGH -> stringResource(
            R.string.priority_high
        )
    }
}