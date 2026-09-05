package com.example.sp2.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight

@Composable
fun NotesScreen(
    onNoteSaved: () -> Unit,
    viewModel: NotesViewModel = viewModel()
) {
    var title by remember {
        mutableStateOf("")
    }

    var content by remember {
        mutableStateOf("")
    }

    val currentDate = remember {
        SimpleDateFormat(
            "MMMM d, yyyy",
            Locale.getDefault()
        ).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Screen title
        Text(
            text = stringResource(R.string.notes_title),
            style = MaterialTheme.typography.headlineMedium
        )

        // Note title
        TextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.note_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // Displays the current date
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Divider between title and note content
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Note content
        TextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text(
                    text = stringResource(R.string.note_content_placeholder)
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // Saves the note
        Button(
            onClick = {
                viewModel.saveNote(
                    title = title,
                    content = content
                )

                onNoteSaved()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() || content.isNotBlank()
        ) {
            Text(
                text = stringResource(R.string.note_save)
            )
        }
    }
}