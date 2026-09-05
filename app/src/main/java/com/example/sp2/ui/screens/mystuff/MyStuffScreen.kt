package com.example.sp2.ui.screens.mystuff

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.ui.screens.notes.NotesListScreen
import com.example.sp2.ui.screens.tasks.TasksScreen

@Composable
fun MyStuffScreen(
    onAddTask: () -> Unit = {},
    onAddNote: () -> Unit = {},
    onEditTask: (Int) -> Unit = {}
) {
    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Screen title
        Text(
            text = stringResource(R.string.my_stuff_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
        )

        // Tasks and Notes tabs
        TabRow(
            selectedTabIndex = selectedTab
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(stringResource(R.string.my_stuff_tasks))
                }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(stringResource(R.string.my_stuff_notes))
                }
            )
        }

        // Selected content
        when (selectedTab) {
            0 -> TasksScreen(
                onAddTask = onAddTask,
                onEditTask = onEditTask
            )

            1 -> NotesListScreen(
                onAddNote = onAddNote
            )
        }
    }
}