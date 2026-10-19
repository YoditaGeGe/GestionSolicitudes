package com.example.gestionsolicitudes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionsolicitudes.data.local.AppDatabase
import com.example.gestionsolicitudes.data.repository.SolicitudRepository
import com.example.gestionsolicitudes.model.LoginErrores
import com.example.gestionsolicitudes.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getDatabase(app)
    private val repositorio = SolicitudRepository(db.solicitudDao(), db.usuarioDao())

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val lista = repositorio.tecnicos()
            _estado.update { it.copy(tecnicos = lista) }
        }
    }

    fun onCorreoChange(nuevoCorreo: String) {
        _estado.update {
            it.copy(correo = nuevoCorreo, errores = it.errores.copy(correo = null, general = null))
        }
    }

    fun onClaveChange(nuevaClave: String) {
        _estado.update {
            it.copy(clave = nuevaClave, errores = it.errores.copy(clave = null, general = null))
        }
    }

    fun iniciarSesion() {
        val actual = _estado.value

        val errores = LoginErrores(
            correo = if (actual.correo.isBlank()) "Campo obligatorio" else null,
            clave = if (actual.clave.isBlank()) "Campo obligatorio" else null
        )
        if (errores.correo != null || errores.clave != null) {
            _estado.update { it.copy(errores = errores) }
            return
        }

        viewModelScope.launch {
            val usuario = repositorio.autenticar(actual.correo, actual.clave)
            if (usuario == null) {
                _estado.update {
                    it.copy(errores = LoginErrores(general = "Correo o contraseña incorrectos"))
                }
            } else {
                _estado.update { it.copy(usuarioActual = usuario, errores = LoginErrores()) }
            }
        }
    }

    fun cerrarSesion() {
        _estado.update { LoginUiState(tecnicos = it.tecnicos) }
    }
}
