package com.example.sp2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.model.Priority

// Displays a task's priority as a small rounded chip with a colored dot,
// following the same rounded, container-colored language as HabitStreakCard
@Composable
fun PriorityChip(
    priority: Priority
) {

    val (label, color) = when (priority) {
        Priority.NONE -> stringResource(R.string.task_no_priority) to MaterialTheme.colorScheme.onSurfaceVariant
        Priority.LOW -> stringResource(R.string.priority_low) to MaterialTheme.colorScheme.primary
        Priority.MEDIUM -> stringResource(R.string.priority_medium) to MaterialTheme.colorScheme.tertiary
        Priority.HIGH -> stringResource(R.string.priority_high) to MaterialTheme.colorScheme.error
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f)
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Small colored dot indicating the priority level
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = color, shape = CircleShape)
            )

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.size(6.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}