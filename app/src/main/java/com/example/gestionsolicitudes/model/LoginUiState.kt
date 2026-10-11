package com.example.gestionsolicitudes.model
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val usuarioActual: Usuario? = null,
    val errores: LoginErrores = LoginErrores()
)

data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null,
    val general: String? = null
)
