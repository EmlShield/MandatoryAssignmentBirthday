package com.example.mandatoryassignment_birthday.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mandatoryassignment_birthday.data.model.User
import com.example.mandatoryassignment_birthday.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        _userState.value = repository.getCurrentUser()
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = repository.login(email, pass)
                if (user != null) {
                    _userState.value = user
                } else {
                    _error.value = "Invalid email or password"
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login process failed", e)
                _error.value = e.localizedMessage ?: "Login Failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        repository.logout()
        _userState.value = null
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = repository.signUp(email, pass)
                if (user != null) {
                    _userState.value = user
                } else {
                    _error.value = "Could not create account."
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign up process failed", e)
                _error.value = e.localizedMessage ?: "Sign Up Failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
}