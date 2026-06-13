package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.LogPelanggan
import com.example.pos_pamt.data.Pelanggan
import com.example.pos_pamt.repository.PelangganRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PelangganViewModel : ViewModel() {
    private val repo = PelangganRepository()

    private val _pelangganState = MutableStateFlow<DataUiState<List<Pelanggan>>>(DataUiState.Idle)
    val pelangganState: StateFlow<DataUiState<List<Pelanggan>>> = _pelangganState

    private val _logState = MutableStateFlow<DataUiState<List<LogPelanggan>>>(DataUiState.Idle)
    val logPelangganState: StateFlow<DataUiState<List<LogPelanggan>>> = _logState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showTambah = MutableStateFlow(false)
    val showTambah: StateFlow<Boolean> = _showTambah

    private val _editTarget = MutableStateFlow<Pelanggan?>(null)
    val editTarget: StateFlow<Pelanggan?> = _editTarget

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError

    private val _actionSuccess = MutableStateFlow<String?>(null)
    val actionSuccess: StateFlow<String?> = _actionSuccess

    init { loadPelanggan(); loadLogPelanggan() }

    fun loadPelanggan() {
        viewModelScope.launch {
            _pelangganState.value = DataUiState.Loading
            try { _pelangganState.value = DataUiState.Success(repo.getSemuaPelanggan()) }
            catch (e: Exception) { _pelangganState.value = DataUiState.Error(e.message ?: "Gagal memuat") }
        }
    }

    fun loadLogPelanggan() {
        viewModelScope.launch {
            _logState.value = DataUiState.Loading
            try { _logState.value = DataUiState.Success(repo.getLogPelanggan()) }
            catch (e: Exception) { _logState.value = DataUiState.Error(e.message ?: "Gagal memuat log") }
        }
    }

    fun onSearchQueryChange(q: String) { _searchQuery.value = q }
    fun openTambah() { _showTambah.value = true }
    fun closeTambah() { _showTambah.value = false }
    fun openEdit(pel: Pelanggan) { _editTarget.value = pel }
    fun closeEdit() { _editTarget.value = null }
    fun clearMessages() { _actionError.value = null; _actionSuccess.value = null }

    fun tambahPelanggan(nama: String, noTelp: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                repo.tambahPelanggan(nama.trim(), noTelp.trim(), isActive)
                _actionSuccess.value = "Pelanggan berhasil ditambahkan"
                _showTambah.value = false
                loadPelanggan(); loadLogPelanggan()
            } catch (e: Exception) { _actionError.value = e.message ?: "Gagal menambah" }
        }
    }

    fun editPelanggan(id: String, nama: String, noTelp: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                repo.editPelanggan(id, nama.trim(), noTelp.trim(), isActive)
                _actionSuccess.value = "Data pelanggan diperbarui"
                _editTarget.value = null
                loadPelanggan(); loadLogPelanggan()
            } catch (e: Exception) { _actionError.value = e.message ?: "Gagal mengedit" }
        }
    }

    fun hapusPelanggan(id: String) {
        viewModelScope.launch {
            try {
                repo.hapusPelanggan(id)
                _actionSuccess.value = "Pelanggan berhasil dihapus"
                loadPelanggan(); loadLogPelanggan()
            } catch (e: Exception) { _actionError.value = e.message ?: "Gagal menghapus" }
        }
    }
}