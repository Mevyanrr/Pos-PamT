package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.Pelanggan
import com.example.pos_pamt.repository.PelangganRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PelangganViewModel : ViewModel() {
    private val repository = PelangganRepository()
    private val _pelangganState = MutableStateFlow<DataUiState<List<Pelanggan>>>(DataUiState.Idle)
    val pelangganState: StateFlow<DataUiState<List<Pelanggan>>> = _pelangganState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadPelanggan()
    }

    fun loadPelanggan() {
        viewModelScope.launch {
            try {
                _pelangganState.value = DataUiState.Loading

                val result = repository.getSemuaPelanggan()
                _pelangganState.value = DataUiState.Success(result)

            } catch (e: Exception) {
                _pelangganState.value = DataUiState.Error(
                    message = e.message ?: "Gagal memuat data pelanggan."
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
