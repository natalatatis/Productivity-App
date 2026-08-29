package com.example.sp2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// Displays the user's current habit streak and the progress of the last seven days
@Composable
fun HabitStreakCard(
    streak: Int = 7,

    // Indicates whether the habit was completed for each of the seven days
    completedDays: List<Boolean> = listOf(
        true,
        true,
        true,
        true,
        true,
        true,
        true
    )
) {

    // Main container for the habit streak card
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // Displays the streak icon and current streak information
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Circular background for the streak icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    // Icon representing completed habits
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Habit streak",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                // Displays the number of consecutive days and a message
                Column {

                    Text(
                        text = "$streak day streak",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Keep it going!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // Displays the progress for each day of the week
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Labels for the seven days of the week
                val days = listOf(
                    "M", "T", "W", "T", "F", "S", "S"
                )

                // Creates one progress indicator for each day
                days.forEachIndexed { index, day ->

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Displays the day label
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        // Circle showing whether the habit was completed
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (completedDays.getOrElse(index) { false }) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            // Shows a checkmark when the habit was completed
                            if (completedDays.getOrElse(index) { false }) {
                                Text(
                                    text = "✓",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}