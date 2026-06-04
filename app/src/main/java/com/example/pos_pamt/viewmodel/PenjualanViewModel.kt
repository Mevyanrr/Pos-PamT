package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.*
import com.example.pos_pamt.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PenjualanViewModel : ViewModel() {
    private val repo = PenjualanRepository()

    private val _penjualanState = MutableStateFlow<DataUiState<List<Penjualan>>>(DataUiState.Idle)
    val penjualanState: StateFlow<DataUiState<List<Penjualan>>> = _penjualanState

    private val _detailState = MutableStateFlow<DataUiState<List<PenjualanDetail>>>(DataUiState.Idle)
    val detailState: StateFlow<DataUiState<List<PenjualanDetail>>> = _detailState

    private val _selected = MutableStateFlow<Penjualan?>(null)
    val selectedPenjualan: StateFlow<Penjualan?> = _selected

    init { loadPenjualan() }

    fun loadPenjualan() {
        viewModelScope.launch {
            try {
                _penjualanState.value = DataUiState.Loading
                _penjualanState.value = DataUiState.Success(repo.getAll())
            } catch (e: Exception) { _penjualanState.value = DataUiState.Error(e.message ?: "Gagal memuat transaksi.") }
        }
    }

    fun selectPenjualan(p: Penjualan) {
        _selected.value = p
        viewModelScope.launch {
            try {
                _detailState.value = DataUiState.Loading
                _detailState.value = DataUiState.Success(repo.getDetail(p.id))
            } catch (e: Exception) { _detailState.value = DataUiState.Error(e.message ?: "Gagal memuat detail.") }
        }
    }
    fun clearSelected() { _selected.value = null; _detailState.value = DataUiState.Idle }
}
