package com.example.sp2.ui.screens.orbi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.ui.res.painterResource
import com.example.sp2.R
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.model.Priority
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun OrbiDialog(
    onDismiss: () -> Unit,
    onConfirmTask: (
        title: String,
        description: String,
        date: LocalDate?,
        time: LocalTime?,
        priority: Priority
    ) -> Unit,
    viewModel: OrbiViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startListening()
    }

    Dialog(onDismissRequest = onDismiss) {

        Surface(shape = RoundedCornerShape(24.dp)) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.orbi_logo),
                    contentDescription = "Orbi",
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                when (val current = state) {

                    is OrbiState.Listening -> {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Escuchando...", style = MaterialTheme.typography.titleMedium)
                    }

                    is OrbiState.Thinking -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Pensando...", style = MaterialTheme.typography.titleMedium)
                    }

                    is OrbiState.Error -> {
                        Text(current.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            TextButton(onClick = onDismiss) { Text("Cancelar") }
                            TextButton(onClick = { viewModel.startListening() }) { Text("Reintentar") }
                        }
                    }

                    is OrbiState.Ready -> {

                        var title by remember(current) { mutableStateOf(current.title) }
                        var description by remember(current) { mutableStateOf(current.description) }
                        var priority by remember(current) { mutableStateOf(current.priority) }
                        var dateText by remember(current) {
                            mutableStateOf(
                                current.date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
                            )
                        }
                        var timeText by remember(current) {
                            mutableStateOf(
                                current.time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
                            )
                        }

                        Text("Confirma tu tarea", style = MaterialTheme.typography.titleMedium)

                        if (current.usedAi) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Interpretado con IA local",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Prioridad", style = MaterialTheme.typography.labelMedium)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Priority.values().forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = priority == option,
                                        onClick = { priority = option }
                                    )
                                    Text(option.name, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = dateText,
                            onValueChange = { dateText = it },
                            label = { Text("Fecha (dd/MM/yyyy, opcional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = timeText,
                            onValueChange = { timeText = it },
                            label = { Text("Hora (HH:mm, opcional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) { Text("Cancelar") }

                            Button(
                                onClick = {
                                    val parsedDate = runCatching {
                                        LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                    }.getOrNull()

                                    val parsedTime = runCatching {
                                        LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"))
                                    }.getOrNull()

                                    onConfirmTask(title.trim(), description.trim(), parsedDate, parsedTime, priority)
                                },
                                enabled = title.isNotBlank()
                            ) {
                                Text("Crear tarea")
                            }
                        }
                    }

                    is OrbiState.Idle -> {}
                }
            }
        }
    }
}