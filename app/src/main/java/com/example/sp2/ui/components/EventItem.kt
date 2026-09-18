package com.example.sp2.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.sp2.model.Event
import java.time.format.DateTimeFormatter

@Composable
fun EventItem(
    event: Event,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    showDate: Boolean = false
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(text = event.title, style = MaterialTheme.typography.titleMedium)

                if (event.description.isNotBlank()) {
                    Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
                }

                val dateText = if (showDate) {
                    event.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } else {
                    ""
                }

                val timeText = event.time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""

                if (dateText.isNotBlank() || timeText.isNotBlank()) {
                    Text(
                        text = listOf(dateText, timeText)
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.task_edit))
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.task_delete))
            }
        }
    }
}