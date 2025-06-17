package com.example.tubelist.model.auth

import android.app.Activity
import android.content.Intent

interface IAuthManager {
    suspend fun signIn(context: Activity): AuthResult
    fun signOut(context: Activity)
    fun handleAuthorizationResult(intent: Intent?, context: Activity): AuthResult
}