package com.example.tubelist.ui.screens

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubelist.model.auth.IAuthManager
import com.example.tubelist.model.user.IUserRepository
import com.example.tubelist.model.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: IUserRepository, private val auth: IAuthManager) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.value = repository.getUser()
        }
    }

    fun logout(context: Activity) {
        auth.signOut(context)
    }
}