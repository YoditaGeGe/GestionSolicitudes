package com.example.gestionsolicitudes.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QrViewModel : ViewModel() {
    private val _resultado = MutableStateFlow<String?>(null)
    val resultado: StateFlow<String?> = _resultado.asStateFlow()

    fun alDetectarQr(contenido: String) {
        _resultado.value = contenido
    }

    fun limpiarResultado() {
        _resultado.value = null
    }
}
