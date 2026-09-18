package com.example.sp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.navigation.AppNavigation
import com.example.sp2.ui.screens.auth.AuthUiState
import com.example.sp2.ui.screens.auth.AuthViewModel
import com.example.sp2.ui.screens.auth.LoginScreen
import com.example.sp2.ui.screens.auth.VerifyEmailScreen
import com.example.sp2.ui.theme.Sp2Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Sp2Theme {

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