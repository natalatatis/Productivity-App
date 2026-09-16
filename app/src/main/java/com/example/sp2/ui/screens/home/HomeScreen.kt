package com.example.sp2.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import com.example.sp2.model.Habit
import com.example.sp2.model.Task
import com.example.sp2.ui.components.CreateHabitDialog
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.HabitProgressCard
import com.example.sp2.ui.components.HabitStreakCard
import com.example.sp2.ui.components.PriorityChip
import com.example.sp2.ui.components.SectionHeader
import com.example.sp2.ui.screens.tasks.TaskViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onEditTask: (Int) -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    habitViewModel: HabitViewModel = viewModel(),
    appUsageViewModel: AppUsageViewModel = viewModel()
) {

    // Observes the tasks stored in Room
    val tasks by taskViewModel.tasks.collectAsState()

    // Observes the habits stored in Room
    val habits by habitViewModel.habits.collectAsState()

    // Observes the app-usage streak
    val usage by appUsageViewModel.usage.collectAsState()

    // Re-checks the app-usage streak every time Home becomes the
    // visible destination — fires on every visit, not just the first
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                appUsageViewModel.refresh()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val today = LocalDate.now()

    // Only displays today's incomplete tasks
    val todayTasks = tasks.filter {
        it.date == today && !it.completed
    }

    var expandedTaskId by remember {
        mutableStateOf<Int?>(null)
    }

    // Controls the "new habit" dialog
    var showCreateHabitDialog by remember {
        mutableStateOf(false)
    }

    // Habit selected for deletion
    var habitToDelete by remember {
        mutableStateOf<Habit?>(null)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Greeting, based on the current time of day
        item {
            Text(
                text = stringResource(greetingStringRes()),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // App usage streak
        item {
            HabitStreakCard(usage = usage)
        }

        // Habits section
        item {
            SectionHeader(
                title = stringResource(R.string.habits_title)
            )
        }

        // Real, persisted habits
        items(
            items = habits,
            key = { "habit_${it.id}" }
        ) { habit ->

            HabitProgressCard(
                habit = habit,
                onIncrement = {
                    habitViewModel.increment(habit)
                },
                onDecrement = {
                    habitViewModel.decrement(habit)
                },
                onLongClick = {
                    habitToDelete = habit
                }
            )
        }

        // Button to create a new habit
        item {
            TextButton(
                onClick = {
                    showCreateHabitDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(text = stringResource(R.string.habit_new))
            }
        }

        // Today's tasks section
        item {
            SectionHeader(
                title = stringResource(R.string.home_today)
            )
        }

        if (todayTasks.isEmpty()) {

            item {
                EmptyState(
                    title = stringResource(R.string.tasks_empty_title),
                    description = stringResource(R.string.tasks_empty_description)
                )
            }

        } else {

            items(
                items = todayTasks,
                key = { it.id }
            ) { task ->

                TaskCard(
                    task = task,
                    isExpanded = expandedTaskId == task.id,
                    onToggleExpand = {
                        expandedTaskId = if (expandedTaskId == task.id) {
                            null
                        } else {
                            task.id
                        }
                    },
                    onEdit = {
                        onEditTask(task.id)
                    },
                    onDelete = {
                        taskViewModel.deleteTask(task)
                    },
                    onToggleComplete = {
                        taskViewModel.toggleTask(task)
                    }
                )
            }
        }
    }

    // Create habit dialog
    if (showCreateHabitDialog) {

        CreateHabitDialog(
            onDismiss = {
                showCreateHabitDialog = false
            },
            onCreate = { name, type, frequency, targetCount, durationType, totalPeriods ->

                habitViewModel.addHabit(
                    name = name,
                    type = type,
                    frequency = frequency,
                    targetCount = targetCount,
                    durationType = durationType,
                    totalPeriods = totalPeriods
                )

                showCreateHabitDialog = false
            }
        )
    }

    // Confirms deletion of a habit
    habitToDelete?.let { habit ->

        AlertDialog(
            onDismissRequest = {
                habitToDelete = null
            },
            title = {
                Text(text = stringResource(R.string.habit_delete_title))
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.habit_delete_message,
                        habit.name
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        habitViewModel.deleteHabit(habit)
                        habitToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(
                            R.string.task_delete_list_confirm
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        habitToDelete = null
                    }
                ) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskCard(
    task: Task,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {

    var showMenu by remember {
        mutableStateOf(false)
    }

    Box {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onToggleExpand,
                    onLongClick = {
                        showMenu = true
                    }
                ),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.4f
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = onToggleComplete) {
                        Icon(
                            imageVector = if (task.completed) {
                                Icons.Filled.CheckCircle
                            } else {
                                Icons.Outlined.CheckCircle
                            },
                            contentDescription = stringResource(
                                R.string.task_mark_done
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.padding(start = 48.dp)) {
                    PriorityChip(priority = task.priority)
                }

                if (isExpanded) {

                    Column(
                        modifier = Modifier.padding(start = 48.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        if (task.description.isNotEmpty()) {
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (task.date != null || task.time != null) {

                            val dateText = task.date?.format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            ) ?: ""

                            val timeText = task.time?.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            ) ?: ""

                            Text(
                                text = "$dateText $timeText".trim(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = {
                showMenu = false
            }
        ) {

            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.task_edit))
                },
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null)
                },
                onClick = {
                    showMenu = false
                    onEdit()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.task_delete))
                },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = null)
                },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}

// Picks the right greeting based on the current time of day
private fun greetingStringRes(): Int {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 5..11 -> R.string.home_greeting_morning
        in 12..18 -> R.string.home_greeting_afternoon
        else -> R.string.home_greeting_night
    }
}