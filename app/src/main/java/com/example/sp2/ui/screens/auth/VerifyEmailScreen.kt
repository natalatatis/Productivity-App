package com.example.sp2.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Blocks access to the app until the person confirms their email —
// this is what makes account creation meaningfully secure (proves
// the person actually owns the address they registered with)
@Composable
fun VerifyEmailScreen(
    email: String?,
    onCheckAgain: () -> Unit,
    onResend: () -> Unit,
    onSignOut: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.MailOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Confirma tu correo",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Te enviamos un enlace de verificación a${if (email != null) " $email" else " tu correo"}. Ábrelo y luego vuelve aquí.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onCheckAgain, modifier = Modifier.fillMaxWidth()) {
            Text("Ya lo confirmé")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = onResend, modifier = Modifier.fillMaxWidth()) {
            Text("Reenviar correo")
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onSignOut) {
            Text("Cerrar sesión")
        }
    }
}