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

    private val _showTambahForm = MutableStateFlow(false)
    val showTambahForm: StateFlow<Boolean> = _showTambahForm

    private val _showEditForm = MutableStateFlow(false)
    val showEditForm: StateFlow<Boolean> = _showEditForm

    private val _editTarget = MutableStateFlow<Pengeluaran?>(null)
    val editTarget: StateFlow<Pengeluaran?> = _editTarget

    private val _hapusTarget = MutableStateFlow<Pengeluaran?>(null)
    val hapusTarget: StateFlow<Pengeluaran?> = _hapusTarget

    private val _formDeskripsi = MutableStateFlow("")
    val formDeskripsi: StateFlow<String> = _formDeskripsi

    private val _formTotal = MutableStateFlow("")
    val formTotal: StateFlow<String> = _formTotal

    private val _formTanggal = MutableStateFlow("")
    val formTanggal: StateFlow<String> = _formTanggal

    private val _formStatus = MutableStateFlow("aktif")
    val formStatus: StateFlow<String> = _formStatus

    private val _formKasId = MutableStateFlow("")
    val formKasId: StateFlow<String> = _formKasId

    private val _kasList = MutableStateFlow<List<Kas>>(emptyList())
    val kasList: StateFlow<List<Kas>> = _kasList

    private val _formError = MutableStateFlow<String?>(null)
    val formError: StateFlow<String?> = _formError

    private val _formLoading = MutableStateFlow(false)
    val formLoading: StateFlow<Boolean> = _formLoading

    init {
        loadPengeluaran()
        loadKasList()
        loadLogPengeluaran()
    }

    fun loadPengeluaran() {
        viewModelScope.launch {
            try {
                _pengeluaranState.value = DataUiState.Loading
                _pengeluaranState.value = DataUiState.Success(repo.getAll())
            } catch (e: Exception) {
                _pengeluaranState.value = DataUiState.Error(e.message ?: "Gagal memuat pengeluaran.")
            }
        }
    }

    fun loadLogPengeluaran() {
        viewModelScope.launch {
            try {
                _logPengeluaranState.value = DataUiState.Loading
                _logPengeluaranState.value = DataUiState.Success(logRepo.getLogPengeluaran())
            } catch (e: Exception) {
                _logPengeluaranState.value = DataUiState.Error(e.message ?: "Gagal memuat log.")
            }
        }
    }

    private fun loadKasList() {
        viewModelScope.launch {
            try {
                val list = repo.getKasList()
                _kasList.value = list
                if (list.isNotEmpty()) _formKasId.value = list.first().id
            } catch (_: Exception) {}
        }
    }

    fun onFilterChange(s: String) { _filterStatus.value = s }

    fun openTambahForm() {
        _formDeskripsi.value  = ""
        _formTotal.value      = ""
        _formTanggal.value    = java.time.LocalDate.now().toString()
        _formStatus.value     = "aktif"
        if (_kasList.value.isNotEmpty()) _formKasId.value = _kasList.value.first().id
        _formError.value      = null
        _showTambahForm.value = true
    }

    fun openEditForm(p: Pengeluaran) {
        _editTarget.value    = p
        _formDeskripsi.value = p.deskripsi
        _formTotal.value     = p.total.toLong().toString()
        _formTanggal.value   = p.tanggal.take(10)
        _formStatus.value    = p.status
        _formKasId.value     = p.kasId
        _formError.value     = null
        _showEditForm.value  = true
    }

    fun closeTambahForm() { _showTambahForm.value = false }
    fun closeEditForm()   { _showEditForm.value = false; _editTarget.value = null }

    fun onDeskripsiChange(v: String) { _formDeskripsi.value = v }
    fun onTotalChange(v: String)     { _formTotal.value = v }
    fun onTanggalChange(v: String)   { _formTanggal.value = v }
    fun onStatusChange(v: String)    { _formStatus.value = v }
    fun onKasIdChange(v: String)     { _formKasId.value = v }

    fun submitTambah() {
        val total = _formTotal.value.toDoubleOrNull()
        if (_formDeskripsi.value.isBlank()) { _formError.value = "Deskripsi wajib diisi."; return }
        if (total == null || total <= 0)    { _formError.value = "Total harus lebih dari 0."; return }
        if (_formKasId.value.isBlank())     { _formError.value = "Pilih kas terlebih dahulu."; return }

        viewModelScope.launch {
            try {
                _formLoading.value = true
                _formError.value   = null
                repo.tambah(
                    kasId     = _formKasId.value,
                    deskripsi = _formDeskripsi.value.trim(),
                    total     = total,
                    tanggal   = _formTanggal.value,
                    status    = _formStatus.value
                )
                _showTambahForm.value = false
                loadPengeluaran()
                loadLogPengeluaran()
            } catch (e: Exception) {
                _formError.value = e.message ?: "Gagal menyimpan pengeluaran."
            } finally {
                _formLoading.value = false
            }
        }
    }

    fun submitEdit() {
        val id    = _editTarget.value?.id ?: return
        val total = _formTotal.value.toDoubleOrNull()
        if (_formDeskripsi.value.isBlank()) { _formError.value = "Deskripsi wajib diisi."; return }
        if (total == null || total <= 0)    { _formError.value = "Total harus lebih dari 0."; return }

        viewModelScope.launch {
            try {
                _formLoading.value = true
                _formError.value   = null
                repo.update(
                    id        = id,
                    kasId     = _formKasId.value,
                    deskripsi = _formDeskripsi.value.trim(),
                    total     = total,
                    tanggal   = _formTanggal.value,
                    status    = _formStatus.value
                )
                _showEditForm.value = false
                _editTarget.value   = null
                loadPengeluaran()
                loadLogPengeluaran()
            } catch (e: Exception) {
                _formError.value = e.message ?: "Gagal memperbarui pengeluaran."
            } finally {
                _formLoading.value = false
            }
        }
    }

    fun konfirmasiHapus(p: Pengeluaran) { _hapusTarget.value = p }
    fun batalKonfirmasiHapus()          { _hapusTarget.value = null }

    fun hapusPengeluaran() {
        val id = _hapusTarget.value?.id ?: return
        viewModelScope.launch {
            try {
                repo.hapus(id)
                _hapusTarget.value = null
                loadPengeluaran()
                loadLogPengeluaran()
            } catch (_: Exception) {}
        }
    }
}