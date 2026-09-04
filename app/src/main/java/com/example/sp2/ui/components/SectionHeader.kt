package com.example.sp2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Displays a consistent section title (and optional subtitle) across screens,
// giving Home, Tasks and Calendar a shared visual rhythm
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null
) {

    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        if (subtitle != null) {

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}