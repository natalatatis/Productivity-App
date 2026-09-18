package com.example.sp2.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.data.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    data class SignedIn(val user: FirebaseUser, val emailVerified: Boolean) : AuthUiState()
    object SignedOut : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(currentState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun currentState(): AuthUiState {
        val user = repository.currentUser
        return if (user != null) {
            AuthUiState.SignedIn(user, user.isEmailVerified)
        } else {
            AuthUiState.SignedOut
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun signUp(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.signUpWithEmail(email.trim(), password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.SignedIn(user, user.isEmailVerified)
                }
                .onFailure { _errorMessage.value = friendlyError(it) }
            _isLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.signInWithEmail(email.trim(), password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.SignedIn(user, user.isEmailVerified)
                }
                .onFailure { _errorMessage.value = friendlyError(it) }
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.signInWithGoogleIdToken(idToken)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.SignedIn(user, user.isEmailVerified)
                }
                .onFailure { _errorMessage.value = friendlyError(it) }
            _isLoading.value = false
        }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.sendPasswordReset(email.trim())
                .onSuccess { onResult(true) }
                .onFailure {
                    _errorMessage.value = friendlyError(it)
                    onResult(false)
                }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            repository.resendVerificationEmail()
        }
    }

    fun refreshEmailVerified() {
        viewModelScope.launch {
            val user = repository.reloadUser()
            if (user != null) {
                _uiState.value = AuthUiState.SignedIn(user, user.isEmailVerified)
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState.SignedOut
    }

    // Translates Firebase's error messages without ever revealing
    // whether a specific email is registered (avoids account enumeration)
    private fun friendlyError(e: Throwable): String {
        val msg = e.message ?: return "Ocurrió un error. Intenta de nuevo."
        return when {
            msg.contains("badly formatted", true) -> "El correo no tiene un formato válido."
            msg.contains("email address is already in use", true) -> "Ya existe una cuenta con ese correo."
            msg.contains("invalid", true) || msg.contains("credential", true) -> "Correo o contraseña incorrectos."
            msg.contains("weak", true) -> "La contraseña es demasiado débil (mínimo 6 caracteres)."
            msg.contains("network", true) -> "Sin conexión a internet."
            msg.contains("too many", true) || msg.contains("blocked", true) -> "Demasiados intentos. Espera unos minutos e intenta de nuevo."
            else -> "Ocurrió un error. Intenta de nuevo."
        }
    }
}