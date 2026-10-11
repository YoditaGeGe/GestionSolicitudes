package com.example.gestionsolicitudes.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestionsolicitudes.model.LoginErrores
import com.example.gestionsolicitudes.model.LoginUiState
import com.example.gestionsolicitudes.model.Perfil
import com.example.gestionsolicitudes.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // Cuentas ficticias para la demo (después las podemos mover a Room)
    private val usuariosFicticios = listOf(
        Usuario("U1", "Camila Cliente", "cliente@demo.cl", "123456", Perfil.CLIENTE, "Pyme Demo SpA"),
        Usuario("U2", "Andrés Admin", "admin@demo.cl", "123456", Perfil.ADMINISTRADOR, "Servicios Digitales Demo"),
        Usuario("U3", "Tomás Técnico", "tecnico@demo.cl", "123456", Perfil.TECNICO, "Servicios Digitales Demo"),
        Usuario("U4", "Valentina Soporte", "tecnico2@demo.cl", "123456", Perfil.TECNICO, "Servicios Digitales Demo")
    )

    // Técnicos disponibles para que el administrador les asigne solicitudes
    val tecnicos: List<Usuario> = usuariosFicticios.filter { it.perfil == Perfil.TECNICO }

    // Actualiza el correo y limpia sus errores
    fun onCorreoChange(nuevoCorreo: String) {
        _estado.update {
            it.copy(correo = nuevoCorreo, errores = it.errores.copy(correo = null, general = null))
        }
    }

    // Actualiza la clave y limpia sus errores
    fun onClaveChange(nuevaClave: String) {
        _estado.update {
            it.copy(clave = nuevaClave, errores = it.errores.copy(clave = null, general = null))
        }
    }

    // Valida y busca al usuario. Devuelve el perfil si entró bien, o null si falló
    fun iniciarSesion(): Perfil? {
        val actual = _estado.value

        val errores = LoginErrores(
            correo = if (actual.correo.isBlank()) "Campo obligatorio" else null,
            clave = if (actual.clave.isBlank()) "Campo obligatorio" else null
        )
        if (errores.correo != null || errores.clave != null) {
            _estado.update { it.copy(errores = errores) }
            return null
        }

        val usuario = usuariosFicticios.find {
            it.correo.equals(actual.correo.trim(), ignoreCase = true) && it.clave == actual.clave
        }

        return if (usuario == null) {
            _estado.update {
                it.copy(errores = LoginErrores(general = "Correo o contraseña incorrectos"))
            }
            null
        } else {
            _estado.update { it.copy(usuarioActual = usuario, errores = LoginErrores()) }
            usuario.perfil
        }
    }

    // Cierra sesión: deja el estado como nuevo
    fun cerrarSesion() {
        _estado.value = LoginUiState()
    }
}
