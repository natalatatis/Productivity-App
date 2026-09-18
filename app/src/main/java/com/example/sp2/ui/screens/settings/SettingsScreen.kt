package com.example.sp2.ui.screens.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sp2.R
import com.example.sp2.data.LanguageManager
import com.example.sp2.ui.screens.auth.AuthUiState
import com.example.sp2.ui.screens.auth.AuthViewModel

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel = viewModel()
) {

    val context = LocalContext.current
    val activity = context as Activity

    var notificationsEnabled by remember {
        mutableStateOf(true)
    }

    // Reads the language saved in SharedPreferences
    val selectedLanguage = LanguageManager.getSavedLanguage(context)

    val authState by authViewModel.uiState.collectAsState()
    val signedInUser = (authState as? AuthUiState.SignedIn)?.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Screen title
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium
        )

        // Profile section
        if (signedInUser != null) {

            ProfileCard(
                name = signedInUser.displayName,
                email = signedInUser.email,
                photoUrl = signedInUser.photoUrl?.toString(),
                onSignOut = { authViewModel.signOut() }
            )

            HorizontalDivider()
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Notifications section
        Text(
            text = stringResource(R.string.settings_notifications),
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = stringResource(R.string.settings_enable_notifications),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = stringResource(
                        R.string.settings_notifications_description
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                }
            )
        }

        HorizontalDivider()

        // Language section
        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selectedLanguage == "en",
                onClick = {
                    LanguageManager.changeLanguage(
                        activity = activity,
                        language = "en"
                    )
                }
            )

            Text(
                text = stringResource(R.string.settings_english),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selectedLanguage == "es",
                onClick = {
                    LanguageManager.changeLanguage(
                        activity = activity,
                        language = "es"
                    )
                }
            )

            Text(
                text = stringResource(R.string.settings_spanish),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        HorizontalDivider()

        // About section
        Text(
            text = stringResource(R.string.settings_about),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(R.string.settings_app_version),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Shows the signed-in person's photo (or initials, if no photo),
// name, email, and a way to sign out — right at the top of Settings
@Composable
private fun ProfileCard(
    name: String?,
    email: String?,
    photoUrl: String?,
    onSignOut: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (!photoUrl.isNullOrBlank()) {

                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initialsFrom(name, email),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = name?.takeIf { it.isNotBlank() }
                            ?: email?.substringBefore("@")
                            ?: "Usuario",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (!email.isNullOrBlank()) {
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onSignOut,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

// Builds a 1-2 letter avatar fallback from the person's name or email
private fun initialsFrom(name: String?, email: String?): String {
    val source = name?.takeIf { it.isNotBlank() } ?: email ?: "?"
    return source.trim()
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifBlank { "?" }
}