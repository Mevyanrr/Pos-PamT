package com.example.pos_pamt.viewmodel

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

sealed class AuthCheckState {
    object Checking : AuthCheckState()
    object Authenticated : AuthCheckState()
    object NotAuthenticated : AuthCheckState()
}

