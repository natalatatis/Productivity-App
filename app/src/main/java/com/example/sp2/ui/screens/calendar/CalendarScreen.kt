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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.data.TaskManager
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.TaskItem
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

@Composable
fun CalendarScreen(onEditTask: (Int) -> Unit = {}) {

    val currentMonth = YearMonth.now()

    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    val coroutineScope = rememberCoroutineScope()

    val visibleMonth = calendarState.firstVisibleMonth.yearMonth

    // Gets the current tasks from the TaskManager
    val tasks = TaskManager.tasks

    // Keeps track of the day the user tapped on the calendar
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
                text = (visibleMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                ) + " " + visibleMonth.year).replaceFirstChar {
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
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
            )

            weekdays.forEach { day ->

                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalCalendar(
            state = calendarState,

            dayContent = { day ->

                // Checks if this day has at least one task assigned
                val hasTask = tasks.any {
                    it.date == day.date
                }

                CalendarDayContent(
                    day = day,
                    isSelected = day.date == selectedDate,
                    hasTask = hasTask,
                    onClick = {
                        selectedDate = day.date
                    }
                )
            }
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Section below the calendar: shows tasks for the selected day,
        // or a message if no day has been selected
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {

            val currentSelectedDate = selectedDate

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

                val tasksForDay = tasks.filter {
                    it.date == currentSelectedDate
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Shows the selected date as a readable header
                    Text(
                        text = currentSelectedDate.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    if (tasksForDay.isEmpty()) {

                        EmptyState(
                            title = stringResource(
                                R.string.calendar_no_tasks_this_day
                            )
                        )

                    } else {

                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            items(
                                items = tasksForDay,
                                key = { it.id }
                            ) { task ->

                                TaskItem(
                                    task = task,
                                    onEdit = {
                                        onEditTask(task.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayContent(
    day: CalendarDay,
    isSelected: Boolean,
    hasTask: Boolean,
    onClick: () -> Unit
) {

    val isCurrentMonth = day.position == DayPosition.MonthDate

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
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // Small dot shown under days that have at least one task
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
        }
    }
}