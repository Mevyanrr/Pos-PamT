package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.UserRole
import com.example.pos_pamt.data.UserSession
import com.example.pos_pamt.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _authCheckState = MutableStateFlow<AuthCheckState>(AuthCheckState.Checking)
    val authCheckState: StateFlow<AuthCheckState> = _authCheckState

    private val _userSession = MutableStateFlow(UserSession())
    val userSession: StateFlow<UserSession> = _userSession

    init {
        observeAuthStatus()
    }

    private fun observeAuthStatus() {
        viewModelScope.launch {
            repository.sessionStatus.collect { status ->
                _authCheckState.value = when (status) {
                    is SessionStatus.Authenticated -> {
                        loadUserSession()
                        AuthCheckState.Authenticated
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _userSession.value = UserSession()
                        AuthCheckState.NotAuthenticated
                    }
                    is SessionStatus.Initializing -> AuthCheckState.Checking
                    is SessionStatus.RefreshFailure -> {
                        if (repository.isLoggedIn()) {
                            loadUserSession()
                            AuthCheckState.Authenticated
                        } else AuthCheckState.NotAuthenticated
                    }
                }
            }
        }
    }

    private fun loadUserSession() {
        viewModelScope.launch {
            val profile = repository.getCurrentProfile()
            _userSession.value = UserSession(
                nama  = profile?.username ?: "",
                role  = when (profile?.role?.lowercase()) {
                    "admin" -> UserRole.Admin
                    else    -> UserRole.Kasir
                }
            )
        }
    }

    fun onEmailChange(value: String)    { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun login() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                repository.login(email = _email.value, password = _password.value)
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Login gagal. Periksa email dan password.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _userSession.value = UserSession()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }
}