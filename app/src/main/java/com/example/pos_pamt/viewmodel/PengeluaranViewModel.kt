package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.*
import com.example.pos_pamt.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PengeluaranViewModel : ViewModel() {
    private val repo    = PengeluaranRepository()
    private val logRepo = LogRepository()

    private val _pengeluaranState = MutableStateFlow<DataUiState<List<Pengeluaran>>>(DataUiState.Idle)
    val pengeluaranState: StateFlow<DataUiState<List<Pengeluaran>>> = _pengeluaranState

    private val _logPengeluaranState = MutableStateFlow<DataUiState<List<LogPengeluaran>>>(DataUiState.Idle)
    val logPengeluaranState: StateFlow<DataUiState<List<LogPengeluaran>>> = _logPengeluaranState

    private val _filterStatus = MutableStateFlow("Semua")
    val filterStatus: StateFlow<String> = _filterStatus

    init { loadPengeluaran() }

    fun loadPengeluaran() {
        viewModelScope.launch {
            try {
                _pengeluaranState.value = DataUiState.Loading
                _pengeluaranState.value = DataUiState.Success(repo.getAll())
            } catch (e: Exception) { _pengeluaranState.value = DataUiState.Error(e.message ?: "Gagal memuat pengeluaran.") }
        }
    }

    fun loadLogPengeluaran() {
        viewModelScope.launch {
            try {
                _logPengeluaranState.value = DataUiState.Loading
                _logPengeluaranState.value = DataUiState.Success(logRepo.getLogPengeluaran())
            } catch (e: Exception) { _logPengeluaranState.value = DataUiState.Error(e.message ?: "Gagal memuat log.") }
        }
    }
    fun onFilterChange(s: String) { _filterStatus.value = s }
}
