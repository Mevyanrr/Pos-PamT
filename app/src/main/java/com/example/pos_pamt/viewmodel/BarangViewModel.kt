package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.Barang
import com.example.pos_pamt.repository.BarangRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BarangViewModel : ViewModel() {

    private val repository = BarangRepository()
    private val _barangState = MutableStateFlow<DataUiState<List<Barang>>>(DataUiState.Idle)
    val barangState: StateFlow<DataUiState<List<Barang>>> = _barangState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadBarang()
    }

    fun loadBarang() {
        viewModelScope.launch {
            try {
                _barangState.value = DataUiState.Loading

                val result = repository.getSemuaBarang()
                _barangState.value = DataUiState.Success(result)

            } catch (e: Exception) {
                _barangState.value = DataUiState.Error(
                    message = e.message ?: "Gagal memuat data barang."
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
