package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.*
import com.example.pos_pamt.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PelangganViewModel : ViewModel() {
    private val pelRepo = PelangganRepository()
    private val logRepo = LogRepository()

    private val _pelangganState = MutableStateFlow<DataUiState<List<Pelanggan>>>(DataUiState.Idle)
    val pelangganState: StateFlow<DataUiState<List<Pelanggan>>> = _pelangganState

    private val _logPelangganState = MutableStateFlow<DataUiState<List<LogPelanggan>>>(DataUiState.Idle)
    val logPelangganState: StateFlow<DataUiState<List<LogPelanggan>>> = _logPelangganState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init { loadPelanggan(); loadLogPelanggan() }

    fun loadPelanggan() {
        viewModelScope.launch {
            try {
                _pelangganState.value = DataUiState.Loading
                _pelangganState.value = DataUiState.Success(pelRepo.getSemuaPelanggan())
            } catch (e: Exception) {
                _pelangganState.value = DataUiState.Error(e.message ?: "Gagal memuat pelanggan.")
            }
        }
    }

    fun loadLogPelanggan() {
        viewModelScope.launch {
            try {
                _logPelangganState.value = DataUiState.Loading
                _logPelangganState.value = DataUiState.Success(logRepo.getLogPelanggan())
            } catch (e: Exception) {
                _logPelangganState.value = DataUiState.Error(e.message ?: "Gagal memuat log.")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}