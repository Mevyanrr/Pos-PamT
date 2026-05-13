package com.example.pos_pamt.viewmodel

sealed class DataUiState<out T> {
    object Idle : DataUiState<Nothing>()
    object Loading : DataUiState<Nothing>()
    data class Success<T>(val data: T) : DataUiState<T>()
    data class Error(val message: String) : DataUiState<Nothing>()
}
