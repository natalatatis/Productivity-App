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

@Composable
fun HomeScreen() {

    val todayTasks = sampleTasks.filter {
        it.date == "Today"
    }

    val upcomingTasks = sampleTasks.filter {
        it.date != "Today"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

            Column {

                Text(
                    text = "Good afternoon",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Thursday, August 28",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {

            Text(
                text = "Today",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(todayTasks) { task ->

            TaskCard(task)
        }

        item {

            Text(
                text = "Upcoming",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(upcomingTasks) { task ->

            TaskCard(task)
        }
    }
}

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

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )

            if (task.description.isNotEmpty()) {

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${task.date} · ${task.time}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = task.priority.name,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}