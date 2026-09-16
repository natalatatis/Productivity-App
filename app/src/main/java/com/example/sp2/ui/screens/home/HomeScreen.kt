package com.example.sp2.ui.screens.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onEditTask: (Int) -> Unit = {},
    onAddTask: () -> Unit = {},
    onOpenReminders: () -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    habitViewModel: HabitViewModel = viewModel(),
    appUsageViewModel: AppUsageViewModel = viewModel()
) {

    val tasks by taskViewModel.tasks.collectAsState()
    val habits by habitViewModel.habits.collectAsState()
    val usage by appUsageViewModel.usage.collectAsState()

    // Re-checks the app-usage streak every time Home becomes visible
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

    val todayTasks = tasks.filter {
        it.date == today && !it.completed
    }

    var expandedTaskId by remember {
        mutableStateOf<Int?>(null)
    }

    var showCreateHabitDialog by remember {
        mutableStateOf(false)
    }

    var habitToDelete by remember {
        mutableStateOf<Habit?>(null)
    }

    // Controls the swipeable Today/Habits pages
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // Greeting, based on the current time of day, with quick
        // access to Reminders (alarms + timer) alongside it
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = stringResource(greetingStringRes()),
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(onClick = onOpenReminders) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = stringResource(R.string.reminders_title)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App usage streak
        HabitStreakCard(usage = usage)

        Spacer(modifier = Modifier.height(20.dp))

        // Tab-like header for the two swipeable pages —
        // tappable to jump directly, or swipe left/right on the pager
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            val pageTabs = listOf(
                stringResource(R.string.home_today),
                stringResource(R.string.habits_title)
            )

            pageTabs.forEachIndexed { index, label ->

                val selected = pagerState.currentPage == index

                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Swipeable pages: Today's tasks and Habits, kept separate
        // so a long list in one doesn't push the other out of sight
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->

            when (page) {

                0 -> TodayPage(
                    tasks = todayTasks,
                    expandedTaskId = expandedTaskId,
                    onToggleExpand = { taskId ->
                        expandedTaskId = if (expandedTaskId == taskId) {
                            null
                        } else {
                            taskId
                        }
                    },
                    onEdit = onEditTask,
                    onDelete = { taskViewModel.deleteTask(it) },
                    onToggleComplete = { taskViewModel.toggleTask(it) },
                    onAddTask = onAddTask
                )

                1 -> HabitsPage(
                    habits = habits,
                    onIncrement = { habitViewModel.increment(it) },
                    onDecrement = { habitViewModel.decrement(it) },
                    onLongClick = { habitToDelete = it },
                    onAddHabit = { showCreateHabitDialog = true }
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

// The "Today" swipeable page
// The "Today" swipeable page
@Composable
private fun TodayPage(
    tasks: List<Task>,
    expandedTaskId: Int?,
    onToggleExpand: (Int) -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onAddTask: () -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (tasks.isEmpty()) {

            item {
                EmptyState(
                    title = stringResource(R.string.tasks_empty_title),
                    description = stringResource(R.string.tasks_empty_description)
                )
            }
        }

        items(
            items = tasks,
            key = { it.id }
        ) { task ->

            TaskCard(
                task = task,
                isExpanded = expandedTaskId == task.id,
                onToggleExpand = { onToggleExpand(task.id) },
                onEdit = { onEdit(task.id) },
                onDelete = { onDelete(task) },
                onToggleComplete = { onToggleComplete(task) }
            )
        }

        item {
            TextButton(onClick = onAddTask) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(text = stringResource(R.string.task_add))
            }
        }
    }
}

// The "Habits" swipeable page
@Composable
private fun HabitsPage(
    habits: List<Habit>,
    onIncrement: (Habit) -> Unit,
    onDecrement: (Habit) -> Unit,
    onLongClick: (Habit) -> Unit,
    onAddHabit: () -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (habits.isEmpty()) {

            item {
                EmptyState(
                    title = stringResource(R.string.habits_empty_title),
                    description = stringResource(R.string.habits_empty_description)
                )
            }
        }

        items(
            items = habits,
            key = { "habit_${it.id}" }
        ) { habit ->

            HabitProgressCard(
                habit = habit,
                onIncrement = { onIncrement(habit) },
                onDecrement = { onDecrement(habit) },
                onLongClick = { onLongClick(habit) }
            )
        }

        item {
            TextButton(onClick = onAddHabit) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(text = stringResource(R.string.habit_new))
            }
        }
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