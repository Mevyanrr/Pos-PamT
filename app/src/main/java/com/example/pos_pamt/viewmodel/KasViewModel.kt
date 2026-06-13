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

    private val _showTambahDialog = MutableStateFlow(false)
    val showTambahDialog: StateFlow<Boolean> = _showTambahDialog

    private val _showEditDialog = MutableStateFlow<Kas?>(null)
    val showEditDialog: StateFlow<Kas?> = _showEditDialog

    private val _showHapusDialog = MutableStateFlow<Kas?>(null)
    val showHapusDialog: StateFlow<Kas?> = _showHapusDialog

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError

    private val _actionSuccess = MutableStateFlow<String?>(null)
    val actionSuccess: StateFlow<String?> = _actionSuccess

    init {
        loadKas()
        loadLogKas()
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

    fun openTambah()          { _showTambahDialog.value = true }
    fun closeTambah()         { _showTambahDialog.value = false }
    fun openEdit(kas: Kas)    { _showEditDialog.value = kas }
    fun closeEdit()           { _showEditDialog.value = null }
    fun openHapus(kas: Kas)   { _showHapusDialog.value = kas }
    fun closeHapus()          { _showHapusDialog.value = null }
    fun clearMessages()       { _actionError.value = null; _actionSuccess.value = null }

    fun tambahKas(nama: String, saldo: Double) {
        viewModelScope.launch {
            try {
                repository.tambahKas(nama, saldo)
                _actionSuccess.value = "Kas '$nama' berhasil ditambahkan"
                _showTambahDialog.value = false
                loadKas()
                loadLogKas()
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Gagal menambah kas"
            }
        }
    }

    fun editKas(id: String, nama: String, saldo: Double, isActive: Boolean) {
        viewModelScope.launch {
            try {
                repository.editKas(id, nama, saldo, isActive)
                _actionSuccess.value = "Kas '$nama' berhasil diperbarui"
                _showEditDialog.value = null
                loadKas()
                loadLogKas()
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Gagal mengedit kas"
            }
        }
    }

    fun hapusKas(kas: Kas) {
        viewModelScope.launch {
            try {
                repository.hapusKas(kas.id)
                _actionSuccess.value = "Kas '${kas.nama}' berhasil dihapus"
                _showHapusDialog.value = null
                loadKas()
                loadLogKas()
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Gagal menghapus kas"
            }
        }
    }
}