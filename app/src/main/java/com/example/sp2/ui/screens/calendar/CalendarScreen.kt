package com.example.sp2.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import com.example.sp2.data.LanguageManager
import com.example.sp2.model.Event
import com.example.sp2.ui.components.CreateEventDialog
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.EventItem
import com.example.sp2.ui.components.TaskItem
import com.example.sp2.ui.screens.events.EventViewModel
import com.example.sp2.ui.screens.tasks.TaskViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.platform.LocalContext

@Composable
fun CalendarScreen(
    onEditTask: (Int) -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    eventViewModel: EventViewModel = viewModel()
) {

    val events by eventViewModel.events.collectAsState()

    var showCreateEventDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<Event?>(null) }

    val currentMonth = YearMonth.now()

    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    val coroutineScope = rememberCoroutineScope()

    val visibleMonth = calendarState.firstVisibleMonth.yearMonth

    // Reads the language currently selected in the app
    // so the month name updates when the language changes
    // Gets the language currently selected in the app
    val context = LocalContext.current
    val language = LanguageManager.getSavedLanguage(context)

    // Observes the tasks stored in Room
    // The calendar updates automatically when tasks change
    val tasks by taskViewModel.tasks.collectAsState()

    // Keeps track of the day selected by the user
    var selectedDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // Month header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Previous month
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(
                            visibleMonth.minusMonths(1)
                        )
                    }
                }
            ) {
                Text(
                    text = "‹",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            // Current visible month
            Text(
                text = (
                        visibleMonth.month.getDisplayName(
                            TextStyle.FULL,
                            Locale(language)
                        ) + " " + visibleMonth.year
                        ).replaceFirstChar {
                        it.titlecase()
                    },
                style = MaterialTheme.typography.headlineSmall
            )

            // Next month
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(
                            visibleMonth.plusMonths(1)
                        )
                    }
                }
            ) {
                Text(
                    text = "›",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Days of the week
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            val weekdays = listOf(
                stringResource(R.string.weekday_mon),
                stringResource(R.string.weekday_tue),
                stringResource(R.string.weekday_wed),
                stringResource(R.string.weekday_thu),
                stringResource(R.string.weekday_fri),
                stringResource(R.string.weekday_sat),
                stringResource(R.string.weekday_sun)
            )

            weekdays.forEach { day ->

                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Calendar
        HorizontalCalendar(
            state = calendarState,

            dayContent = { day ->

                // Checks whether this day has at least one task
                val hasTask = tasks.any {
                    it.date == day.date
                }

                val hasEvent = events.any {
                    it.date == day.date
                }

                CalendarDayContent(
                    day = day,
                    isSelected = day.date == selectedDate,
                    hasTask = hasTask,
                    hasEvent = hasEvent,
                    onClick = {
                        selectedDate = day.date
                    }
                )
            }
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Section below the calendar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {

            val currentSelectedDate = selectedDate

            // Shows a message if no day is selected
            if (currentSelectedDate == null) {

                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {

                    EmptyState(
                        title = stringResource(
                            R.string.calendar_no_date_selected
                        )
                    )
                }

            } else {

                // Gets the tasks and events assigned to the selected day
                val tasksForDay = tasks.filter {
                    it.date == currentSelectedDate
                }

                val eventsForDay = events.filter {
                    it.date == currentSelectedDate
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Selected date header
                    Text(
                        text = currentSelectedDate.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    if (tasksForDay.isEmpty() && eventsForDay.isEmpty()) {

                        EmptyState(
                            title = stringResource(
                                R.string.calendar_no_tasks_this_day
                            )
                        )
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        if (tasksForDay.isNotEmpty()) {

                            items(
                                items = tasksForDay,
                                key = { "task_${it.id}" }
                            ) { task ->

                                TaskItem(
                                    task = task,
                                    onEdit = { onEditTask(task.id) },
                                    onToggle = { taskViewModel.toggleTask(task) },
                                    onDelete = { taskViewModel.deleteTask(task) }
                                )
                            }
                        }

                        if (eventsForDay.isNotEmpty()) {

                            items(
                                items = eventsForDay,
                                key = { "event_${it.id}" }
                            ) { event ->

                                EventItem(
                                    event = event,
                                    onEdit = { eventToEdit = event },
                                    onDelete = { eventViewModel.deleteEvent(event) }
                                )
                            }
                        }

                        item {
                            TextButton(onClick = { showCreateEventDialog = true }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Text(text = stringResource(R.string.event_new))
                            }
                        }
                    }
                }
            }

            // Create/edit event dialog, pre-filled with the selected date
            if (showCreateEventDialog || eventToEdit != null) {

                CreateEventDialog(
                    initialDate = currentSelectedDate,
                    initialEvent = eventToEdit,
                    onDismiss = {
                        showCreateEventDialog = false
                        eventToEdit = null
                    },
                    onCreate = { title, description, date, time ->

                        val current = eventToEdit

                        if (current != null) {
                            eventViewModel.updateEvent(
                                current.copy(title = title, description = description, date = date, time = time)
                            )
                        } else {
                            eventViewModel.addEvent(title, description, date, time)
                        }

                        showCreateEventDialog = false
                        eventToEdit = null
                    }
                )
            }
        }
    }
}

@Composable
fun CalendarDayContent(
    day: CalendarDay,
    isSelected: Boolean,
    hasTask: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit
) {

    val isCurrentMonth =
        day.position == DayPosition.MonthDate

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                enabled = isCurrentMonth,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Calendar day number
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isSelected ->
                            MaterialTheme.colorScheme.onPrimary

                        !isCurrentMonth ->
                            MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.3f
                            )

                        else ->
                            MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // Small dots shown under days that have a task and/or an event
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {

                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(4.dp)
                        .background(
                            color = if (hasTask) {
                                MaterialTheme.colorScheme.error
                            } else {
                                androidx.compose.ui.graphics.Color.Transparent
                            },
                            shape = CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(4.dp)
                        .background(
                            color = if (hasEvent) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                androidx.compose.ui.graphics.Color.Transparent
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}