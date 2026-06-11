package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.Kas
import com.example.pos_pamt.data.LogKas
import com.example.pos_pamt.repository.KasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KasViewModel : ViewModel() {

    private val repository = KasRepository()

    private val _kasState = MutableStateFlow<DataUiState<List<Kas>>>(DataUiState.Idle)
    val kasState: StateFlow<DataUiState<List<Kas>>> = _kasState

    private val _logKasState = MutableStateFlow<DataUiState<List<LogKas>>>(DataUiState.Idle)
    val logKasState: StateFlow<DataUiState<List<LogKas>>> = _logKasState

    init {
        loadKas()
    }

    fun loadKas() {
        viewModelScope.launch {
            try {
                _kasState.value = DataUiState.Loading
                _kasState.value = DataUiState.Success(repository.getSemuaKas())
            } catch (e: Exception) {
                _kasState.value = DataUiState.Error(e.message ?: "Gagal memuat data kas.")
            }
        }
    }

    fun loadLogKas() {
        viewModelScope.launch {
            try {
                _logKasState.value = DataUiState.Loading
                _logKasState.value = DataUiState.Success(repository.getLogKas())
            } catch (e: Exception) {
                _logKasState.value = DataUiState.Error(e.message ?: "Gagal memuat log kas.")
            }
        }
    }
}