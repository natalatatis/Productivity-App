package com.example.sp2.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.sp2.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private enum class AuthMode { LOGIN, SIGN_UP }

@Composable
fun LoginScreen(
    viewModel: AuthViewModel
) {

    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotSent by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Basic client-side checks — Firebase re-validates everything
    // server-side regardless, this is just for immediate feedback
    val passwordsMatch = mode == AuthMode.LOGIN || password == confirmPassword
    val passwordLongEnough = password.length >= 8
    val canSubmit = email.isNotBlank() && passwordLongEnough && passwordsMatch

    fun launchGoogleSignIn() {
        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetSignInWithGoogleOption
                    .Builder(context.getString(R.string.default_web_client_id))
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context as Activity,
                    request = request
                )

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    viewModel.signInWithGoogle(googleCredential.idToken)
                }
            } catch (e: GetCredentialException) {
                // The person cancelled or no Google account is available —
                // not treated as an error, just no-op
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Orbi",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (mode == AuthMode.LOGIN) {
                "Inicia sesión para continuar"
            } else {
                "Crea tu cuenta"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña (mínimo 8 caracteres)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            isError = password.isNotEmpty() && !passwordLongEnough,
            modifier = Modifier.fillMaxWidth()
        )

        if (mode == AuthMode.SIGN_UP) {

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmPassword.isNotEmpty() && !passwordsMatch,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (mode == AuthMode.LOGIN) {
            TextButton(
                onClick = {
                    forgotEmail = email
                    forgotSent = false
                    showForgotDialog = true
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("¿Olvidaste tu contraseña?")
            }
        }

        errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = msg, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.clearError()
                if (mode == AuthMode.LOGIN) {
                    viewModel.signIn(email, password)
                } else {
                    viewModel.signUp(email, password)
                }
            },
            enabled = canSubmit && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp))
            } else {
                Text(if (mode == AuthMode.LOGIN) "Iniciar sesión" else "Crear cuenta")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  o  ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = { launchGoogleSignIn() },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar con Google")
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = {
                viewModel.clearError()
                mode = if (mode == AuthMode.LOGIN) AuthMode.SIGN_UP else AuthMode.LOGIN
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                if (mode == AuthMode.LOGIN) {
                    "¿No tienes cuenta? Crea una"
                } else {
                    "¿Ya tienes cuenta? Inicia sesión"
                }
            )
        }
    }

    if (showForgotDialog) {

        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Recuperar contraseña") },
            text = {
                Column {
                    if (forgotSent) {
                        Text("Si existe una cuenta con ese correo, te enviamos un enlace para restablecer tu contraseña.")
                    } else {
                        OutlinedTextField(
                            value = forgotEmail,
                            onValueChange = { forgotEmail = it },
                            label = { Text("Correo electrónico") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (!forgotSent) {
                    Button(
                        onClick = {
                            viewModel.sendPasswordReset(forgotEmail) { success ->
                                if (success) forgotSent = true
                            }
                        },
                        enabled = forgotEmail.isNotBlank()
                    ) {
                        Text("Enviar enlace")
                    }
                } else {
                    Button(onClick = { showForgotDialog = false }) {
                        Text("Listo")
                    }
                }
            },
            dismissButton = {
                if (!forgotSent) {
                    TextButton(onClick = { showForgotDialog = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }
}