package com.example.sp2.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.model.Habit
import com.example.sp2.model.HabitDurationType
import com.example.sp2.model.HabitFrequency
import com.example.sp2.model.HabitType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitProgressCard(
    habit: Habit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onLongClick: () -> Unit = {}
) {

    // Counter habits: % of the current period's goal.
    // Fixed-duration Yes/No habits: % of the challenge elapsed.
    // Indefinite Yes/No habits: no percentage at all, just the streak.
    val percentage: Int? = when {

        habit.type == HabitType.COUNTER -> {
            if (habit.targetCount > 0) {
                ((habit.currentCount * 100) / habit.targetCount)
                    .coerceAtMost(100)
            } else {
                0
            }
        }

        habit.type == HabitType.CHECKOFF &&
                habit.durationType == HabitDurationType.FIXED &&
                habit.totalPeriods != null -> {

            if (habit.totalPeriods > 0) {
                ((habit.periodsElapsed * 100) / habit.totalPeriods)
                    .coerceAtMost(100)
            } else {
                0
            }
        }

        else -> null
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Percentage only makes sense for counter-based
                    // habits — a yes/no habit is either done or not
                    if (percentage != null) {
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (habit.streak > 0) {
                        Text(
                            text = "\uD83D\uDD25 ${habit.streak}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = statusLabel(habit),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Accumulated hits/misses counter
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text = "✓ ${habit.totalHits}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "✕ ${habit.totalMisses}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Interaction area
            if (habit.isChallengeFinished) {

                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(
                        R.string.habit_challenge_finished
                    ),
                    tint = MaterialTheme.colorScheme.primary
                )

            } else if (habit.type == HabitType.CHECKOFF) {

                IconButton(
                    onClick = {
                        if (habit.isCompleted) {
                            onDecrement()
                        } else {
                            onIncrement()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (habit.isCompleted) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Outlined.CheckCircle
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            } else {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    IconButton(onClick = onDecrement) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null
                        )
                    }

                    Text(
                        text = "${habit.currentCount}/${habit.targetCount}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(onClick = onIncrement) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun statusLabel(habit: Habit): String {

    val frequency = when (habit.frequency) {
        HabitFrequency.DAILY -> stringResource(R.string.habit_frequency_daily)
        HabitFrequency.WEEKLY -> stringResource(R.string.habit_frequency_weekly)
        HabitFrequency.MONTHLY -> stringResource(R.string.habit_frequency_monthly)
    }

    return if (
        habit.durationType == HabitDurationType.FIXED &&
        habit.totalPeriods != null
    ) {
        "$frequency · ${habit.periodsElapsed}/${habit.totalPeriods}"
    } else {
        frequency
    }
}