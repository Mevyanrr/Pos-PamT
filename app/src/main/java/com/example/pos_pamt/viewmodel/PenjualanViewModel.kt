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

    private val _showTambah = MutableStateFlow(false)
    val showTambah: StateFlow<Boolean> = _showTambah

    // Data untuk dropdown form
    private val _pelangganList = MutableStateFlow<List<Pelanggan>>(emptyList())
    val pelangganList: StateFlow<List<Pelanggan>> = _pelangganList

    private val _kasList = MutableStateFlow<List<Kas>>(emptyList())
    val kasList: StateFlow<List<Kas>> = _kasList

    private val _produkList = MutableStateFlow<List<Produk>>(emptyList())
    val produkList: StateFlow<List<Produk>> = _produkList

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError

    private val _actionSuccess = MutableStateFlow<String?>(null)
    val actionSuccess: StateFlow<String?> = _actionSuccess

    init { loadPenjualan() }

    fun loadPenjualan() {
        viewModelScope.launch {
            _penjualanState.value = DataUiState.Loading
            try { _penjualanState.value = DataUiState.Success(repo.getAll()) }
            catch (e: Exception) { _penjualanState.value = DataUiState.Error(e.message ?: "Gagal memuat transaksi.") }
        }
    }

    fun selectPenjualan(p: Penjualan) {
        _selected.value = p
        viewModelScope.launch {
            _detailState.value = DataUiState.Loading
            try { _detailState.value = DataUiState.Success(repo.getDetail(p.id)) }
            catch (e: Exception) { _detailState.value = DataUiState.Error(e.message ?: "Gagal memuat detail.") }
        }
    }

    fun clearSelected() { _selected.value = null; _detailState.value = DataUiState.Idle }

    fun openTambah() {
        _showTambah.value = true
        viewModelScope.launch {
            try {
                _pelangganList.value = repo.getPelanggan()
                _kasList.value = repo.getKas()
                _produkList.value = repo.getProduk()
            } catch (e: Exception) { _actionError.value = "Gagal memuat data form: ${e.message}" }
        }
    }

    fun closeTambah() { _showTambah.value = false }
    fun clearMessages() { _actionError.value = null; _actionSuccess.value = null }

    fun simpanTransaksi(
        kasirId: String,
        pelangganId: String,
        kasId: String,
        produkId: String,
        hargaSatuan: Double,
        qty: Double,
        jumlahBayar: Double
    ) {
        viewModelScope.launch {
            try {
                repo.simpanTransaksi(pelangganId, kasId, kasirId, produkId, hargaSatuan, qty, jumlahBayar)
                _actionSuccess.value = "Transaksi berhasil disimpan"
                _showTambah.value = false
                loadPenjualan()
            } catch (e: Exception) { _actionError.value = e.message ?: "Gagal menyimpan transaksi" }
        }
    }

    fun hapusPenjualan(id: String) {
        viewModelScope.launch {
            try {
                repo.hapusPenjualan(id)
                _actionSuccess.value = "Transaksi berhasil dihapus"
                _selected.value = null
                _detailState.value = DataUiState.Idle
                loadPenjualan()
            } catch (e: Exception) { _actionError.value = e.message ?: "Gagal menghapus transaksi" }
        }
    }
}