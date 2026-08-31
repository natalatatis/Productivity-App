package com.example.sp2.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sp2.data.sampleTasks
import com.example.sp2.model.Task
import com.example.sp2.ui.components.HabitStreakCard
import androidx.compose.ui.res.stringResource
import com.example.sp2.R

// Displays the main home screen of the application
@Composable
fun HomeScreen() {

    // Separates today's tasks from the upcoming tasks
    val todayTasks = sampleTasks.filter {
        it.date == "Today"
    }

    val upcomingTasks = sampleTasks.filter {
        it.date != "Today"
    }

    // Creates a vertically scrollable list for the home screen
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Displays the greeting and current date
        item {

            Column {

                Text(
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Thursday, August 28",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Displays the user's current habit streak
        item {
            HabitStreakCard(
                streak = 7
            )
        }

        // Section title for today's tasks
        item {
            Text(
                text = stringResource(R.string.home_today),
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Displays each task scheduled for today
        items(todayTasks) { task ->

            TaskCard(task)
        }

        // Section title for upcoming tasks
        item {

            Text(
                text = stringResource(R.string.home_upcoming),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Displays tasks scheduled for future dates
        items(upcomingTasks) { task ->

            TaskCard(task)
        }
    }
}

// Displays the information of an individual task
@Composable
private fun TaskCard(
    task: Task
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            // Displays the task title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )

            // Displays the description only if one exists
            if (task.description.isNotEmpty()) {

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Displays the date and time of the task
            Text(
                text = "${task.date} · ${task.time}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Displays the priority assigned to the task
            Text(
                text = task.priority.name,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}