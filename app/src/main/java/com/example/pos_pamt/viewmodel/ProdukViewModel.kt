package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.*
import com.example.pos_pamt.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProdukViewModel : ViewModel() {
    private val produkRepo = ProdukRepository()
    private val logRepo    = LogRepository()

    private val _produkState = MutableStateFlow<DataUiState<List<Produk>>>(DataUiState.Idle)
    val produkState: StateFlow<DataUiState<List<Produk>>> = _produkState

    // log_produk: admin only — panggil loadLogProduk() hanya jika isAdmin = true
    private val _logProdukState = MutableStateFlow<DataUiState<List<LogProduk>>>(DataUiState.Idle)
    val logProdukState: StateFlow<DataUiState<List<LogProduk>>> = _logProdukState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init { loadProduk() }

    fun loadProduk() {
        viewModelScope.launch {
            try {
                _produkState.value = DataUiState.Loading
                _produkState.value = DataUiState.Success(produkRepo.getAll())
            } catch (e: Exception) { _produkState.value = DataUiState.Error(e.message ?: "Gagal memuat produk.") }
        }
    }

    fun loadLogProduk() {
        viewModelScope.launch {
            try {
                _logProdukState.value = DataUiState.Loading
                _logProdukState.value = DataUiState.Success(logRepo.getLogProduk())
            } catch (e: Exception) { _logProdukState.value = DataUiState.Error(e.message ?: "Gagal memuat log.") }
        }
    }

    fun onSearchChange(q: String) { _searchQuery.value = q }
}

