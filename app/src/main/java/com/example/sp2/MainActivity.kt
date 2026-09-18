package com.example.sp2

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.data.LanguageManager
import com.example.sp2.navigation.AppNavigation
import com.example.sp2.ui.screens.auth.AuthUiState
import com.example.sp2.ui.screens.auth.AuthViewModel
import com.example.sp2.ui.screens.auth.LoginScreen
import com.example.sp2.ui.screens.auth.VerifyEmailScreen
import com.example.sp2.ui.theme.Sp2Theme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Sp2Theme {

                // Reads the saved language and wraps the ENTIRE app in a
                // context locked to that locale, so every stringResource()
                // call in every screen reflects it — not just Calendar.
                // (activity.recreate() in LanguageManager makes this whole
                // block re-run fresh whenever the language changes.)
                val baseContext = LocalContext.current

                val language = remember { LanguageManager.getSavedLanguage(baseContext) }

                val localizedContext = remember(language) {
                    val config = Configuration(baseContext.resources.configuration)
                    config.setLocale(Locale(language))
                    baseContext.createConfigurationContext(config)
                }

                CompositionLocalProvider(LocalContext provides localizedContext) {

                    val authViewModel: AuthViewModel = viewModel()
                    val authState by authViewModel.uiState.collectAsState()

                    when (val state = authState) {

                        is AuthUiState.SignedOut -> {
                            LoginScreen(viewModel = authViewModel)
                        }

                        is AuthUiState.SignedIn -> {
                            if (state.emailVerified) {
                                AppNavigation(authViewModel = authViewModel)
                            } else {
                                VerifyEmailScreen(
                                    email = state.user.email,
                                    onCheckAgain = { authViewModel.refreshEmailVerified() },
                                    onResend = { authViewModel.resendVerificationEmail() },
                                    onSignOut = { authViewModel.signOut() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}