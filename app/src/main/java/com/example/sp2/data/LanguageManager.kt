package com.example.sp2.data

import android.app.Activity
import android.content.Context

object LanguageManager {

    private const val PREFS_NAME = "language_preferences"
    private const val LANGUAGE_KEY = "selected_language"

    // Returns the language currently saved by the user
    fun getSavedLanguage(context: Context): String {

        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return preferences.getString(
            LANGUAGE_KEY,
            "en"
        ) ?: "en"
    }

    // Saves the language and reloads the Activity
    fun changeLanguage(
        activity: Activity,
        language: String
    ) {

        val preferences = activity.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        preferences.edit()
            .putString(
                LANGUAGE_KEY,
                language
            )
            .apply()

        activity.recreate()
    }
}