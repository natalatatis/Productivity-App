package com.example.sp2.data

import androidx.compose.runtime.mutableStateOf

// Holds the language currently selected inside the app ("en" or "es")
// This is independent from the device's system language
object LanguageManager {
    val currentLanguage = mutableStateOf("en")
}