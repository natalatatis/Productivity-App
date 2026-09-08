package com.example.sp2

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sp2.data.LanguageManager
import com.example.sp2.navigation.AppNavigation
import com.example.sp2.ui.theme.Sp2Theme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {

        // Gets the saved language before the activity is created
        val language = LanguageManager.getSavedLanguage(newBase)

        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration(
            newBase.resources.configuration
        )

        configuration.setLocale(locale)

        val localizedContext =
            newBase.createConfigurationContext(configuration)

        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Sp2Theme {
                AppNavigation()
            }
        }
    }
}