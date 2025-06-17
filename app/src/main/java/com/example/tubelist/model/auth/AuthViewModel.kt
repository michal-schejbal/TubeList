package com.example.tubelist.model.auth

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data class Success(val token: String) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel(private val auth: IAuthManager) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _pendingIntent = MutableStateFlow<PendingIntent?>(null)
    val pendingIntent = _pendingIntent.asStateFlow()

    fun signIn(context: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = AuthState.Loading
            val result = auth.signIn(context)

            when (result) {
                is AuthResult.Error -> _uiState.value = AuthState.Error(result.message)
                is AuthResult.Success -> _uiState.value = AuthState.Success(result.token)
                is AuthResult.ResolutionRequired -> _pendingIntent.value = result.intent
            }
        }
    }

    fun handleAuthorizationResponse(intent: Intent?, activity: Activity) {
        // We've received the result from the UI, process it.
        auth.handleAuthorizationResult(intent, activity)

        // Clear the pending intent so it's not re-launched on config change
        _pendingIntent.value = null
    }
}