package com.example.tubelist.ui.screens

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubelist.app.runCatchingCancellable
import com.example.tubelist.model.auth.AuthResult
import com.example.tubelist.model.auth.IAuthManager
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

class SignInViewModel(private val auth: IAuthManager) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _pendingIntent = MutableStateFlow<PendingIntent?>(null)
    val pendingIntent = _pendingIntent.asStateFlow()

    fun signIn(context: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = AuthState.Loading
            runCatchingCancellable {
                auth.signIn(context)
            }.onSuccess { result ->
                when (result) {
                    is AuthResult.Error -> _uiState.value = AuthState.Error(result.message)
                    is AuthResult.Success -> _uiState.value = AuthState.Success(result.token)
                    is AuthResult.ResolutionRequired -> _pendingIntent.value = result.intent
                }
            }.onFailure { e ->
                _uiState.value = AuthState.Error(e.message ?: "Unexpected error")
            }
        }
    }

    fun handleAuthorizationResponse(intent: Intent?, activity: Activity) {
        auth.handleAuthorizationResult(intent, activity)
        _pendingIntent.value = null
    }
}