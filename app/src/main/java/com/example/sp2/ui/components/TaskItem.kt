package com.example.sp2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.data.TaskManager
import com.example.sp2.model.Priority
import com.example.sp2.model.Task

// Displays an individual task
@Composable
fun TaskItem(
    task: Task,
    onEdit: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        // Displays the task information and actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Marks the task as completed or incomplete
            Checkbox(
                checked = task.completed,
                onCheckedChange = {
                    TaskManager.toggleTask(task)
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {

                // Displays the task title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.completed) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )

                // Displays the description if one exists
                if (task.description.isNotBlank()) {

                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Displays the task priority
                Text(
                    text = priorityText(task.priority),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Button for editing the task
            IconButton(
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(
                        R.string.task_edit
                    )
                )
            }

            // Button for deleting the task
            IconButton(
                onClick = {
                    TaskManager.deleteTask(task)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(
                        R.string.task_delete
                    )
                )
            }
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