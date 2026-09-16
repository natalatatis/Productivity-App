package com.example.sp2.ui.screens.mystuff

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.ui.screens.notes.NotesListScreen
import com.example.sp2.ui.screens.tasks.TasksScreen
import kotlinx.coroutines.launch

@Composable
fun MyStuffScreen(
    onAddTask: () -> Unit = {},
    onAddNote: () -> Unit = {},
    onEditTask: (Int) -> Unit = {},
    onOpenNote: (Int) -> Unit = {},
    onOpenTaskFolders: () -> Unit = {},
    onOpenNoteFolders: () -> Unit = {}
) {

    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {

        // Tasks and Notes tabs — tapping jumps directly,
        // swiping the content below also switches between them
        TabRow(selectedTabIndex = pagerState.currentPage) {

            Tab(
                selected = pagerState.currentPage == 0,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(0) }
                },
                text = { Text(stringResource(R.string.my_stuff_tasks)) }
            )

            Tab(
                selected = pagerState.currentPage == 1,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                },
                text = { Text(stringResource(R.string.my_stuff_notes)) }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            when (page) {

                0 -> TasksScreen(
                    onAddTask = onAddTask,
                    onEditTask = onEditTask,
                    onOpenFolders = onOpenTaskFolders
                )

                1 -> NotesListScreen(
                    onAddNote = onAddNote,
                    onOpenNote = onOpenNote,
                    onOpenFolders = onOpenNoteFolders
                )
            }
        }
    }
}