package com.example.sp2

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.sp2.data.LanguageManager
import com.example.sp2.navigation.AppNavigation
import com.example.sp2.ui.theme.Sp2Theme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Sp2Theme {

                // Reads the language currently selected in the app
                val language by LanguageManager.currentLanguage

                // The context tied to the device's real configuration
                val baseContext = LocalContext.current

                // Builds a context whose resources are locked to the
                // selected language, regardless of the device's language
                val localizedContext = remember(language) {
                    val locale = Locale(language)
                    val config = Configuration(baseContext.resources.configuration)
                    config.setLocale(locale)
                    baseContext.createConfigurationContext(config)
                }

                // Every stringResource() call below this point reads
                // from the localized context instead of the device's context
                CompositionLocalProvider(LocalContext provides localizedContext) {
                    AppNavigation()
                }
            }
        }
    }
}