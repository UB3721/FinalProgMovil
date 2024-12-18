package com.df.base.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.df.base.data.MangasRepository
import com.df.base.model.back.LoginRequest
import com.df.base.model.back.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MangasRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val loginRequest = LoginRequest(username, password)

                val response = repository.login(loginRequest)

                if (response.isSuccessful) {
                    _isLoggedIn.value = true
                    val userData = response.body()
                    _user.value = userData
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Login failed")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            _user.value = null
        }
    }

    fun signup(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val signupRequest = LoginRequest(username, password)
                val response = repository.signup(signupRequest)

                if (response.isSuccessful) {
                    _isLoggedIn.value = true
                    val userData = response.body()
                    _user.value = userData
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Signup failed")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}




