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

    private val _logProdukState = MutableStateFlow<DataUiState<List<LogProduk>>>(DataUiState.Idle)
    val logProdukState: StateFlow<DataUiState<List<LogProduk>>> = _logProdukState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showTambah = MutableStateFlow(false)
    val showTambah: StateFlow<Boolean> = _showTambah

    private val _editTarget = MutableStateFlow<Produk?>(null)
    val editTarget: StateFlow<Produk?> = _editTarget

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError

    private val _actionSuccess = MutableStateFlow<String?>(null)
    val actionSuccess: StateFlow<String?> = _actionSuccess

    init { loadProduk() }

    fun loadProduk() {
        viewModelScope.launch {
            _produkState.value = DataUiState.Loading
            try { _produkState.value = DataUiState.Success(produkRepo.getAll()) }
            catch (e: Exception) { _produkState.value = DataUiState.Error(e.message ?: "Gagal memuat produk.") }
        }
    }

    fun loadLogProduk() {
        viewModelScope.launch {
            _logProdukState.value = DataUiState.Loading
            try { _logProdukState.value = DataUiState.Success(logRepo.getLogProduk()) }
            catch (e: Exception) { _logProdukState.value = DataUiState.Error(e.message ?: "Gagal memuat log.") }
        }
    }

    fun onSearchChange(q: String) { _searchQuery.value = q }
    fun openTambah() { _showTambah.value = true }
    fun closeTambah() { _showTambah.value = false }
    fun openEdit(p: Produk) { _editTarget.value = p }
    fun closeEdit() { _editTarget.value = null }
    fun clearMessages() { _actionError.value = null; _actionSuccess.value = null }

    fun tambah(nama: String, harga: Double, stok: Double, isActive: Boolean) {
        viewModelScope.launch {
            try {
                produkRepo.tambah(nama.trim(), harga, stok, isActive)
                _actionSuccess.value = "Produk berhasil ditambahkan"
                _showTambah.value = false
                loadProduk(); loadLogProduk()
            } catch (e: Exception) { _actionError.value = e.message ?: "Gagal menambah produk" }
        }
    }

    fun edit(id: String, nama: String, harga: Double, stok: Double, isActive: Boolean) {
        viewModelScope.launch {
            try {
                produkRepo.edit(id, nama.trim(), harga, stok, isActive)
                _actionSuccess.value = "Produk berhasil diperbarui"
                _editTarget.value = null
                loadProduk(); loadLogProduk()
            } catch (e: Exception) { _actionError.value = e.message ?: "Gagal mengedit produk" }
        }
    }

    fun hapus(id: String) {
        viewModelScope.launch {
            try {
                produkRepo.hapus(id)
                _actionSuccess.value = "Produk berhasil dihapus"
                loadProduk(); loadLogProduk()
            } catch (e: Exception) {
                _actionError.value = if (e.message?.contains("foreign key") == true)
                    "Gagal: produk masih terhubung ke transaksi"
                else e.message ?: "Gagal menghapus produk"
            }
        }
    }
}