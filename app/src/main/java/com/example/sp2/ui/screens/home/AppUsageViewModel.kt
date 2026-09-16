package com.example.sp2.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.data.AppUsageRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.AppUsage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppUsageViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = DatabaseProvider.getDatabase(application)
    private val repository = AppUsageRepository(database.appUsageDao())

    private val _usage = MutableStateFlow(AppUsage())
    val usage: StateFlow<AppUsage> = _usage.asStateFlow()

    // Called every time Home becomes visible (not just on app start),
    // so revisiting Home after the date changes reflects it right away
    fun refresh() {
        viewModelScope.launch {
            _usage.value = repository.recordAppOpenAndGetUsage()
        }
    }
}