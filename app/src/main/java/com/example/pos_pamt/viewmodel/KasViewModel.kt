package com.example.pos_pamt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_pamt.data.Kas
import com.example.pos_pamt.repository.KasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KasViewModel : ViewModel() {

    private val repository = KasRepository()
    private val _kasState = MutableStateFlow<DataUiState<List<Kas>>>(DataUiState.Idle)
    val kasState: StateFlow<DataUiState<List<Kas>>> = _kasState

    init {
        loadKas()
    }

    fun loadKas() {
        viewModelScope.launch {
            try {
                _kasState.value = DataUiState.Loading

                val result = repository.getSemuaKas()
                _kasState.value = DataUiState.Success(result)

            } catch (e: Exception) {
                _kasState.value = DataUiState.Error(
                    message = e.message ?: "Gagal memuat data kas."
                )
            }
        }
    }
}
