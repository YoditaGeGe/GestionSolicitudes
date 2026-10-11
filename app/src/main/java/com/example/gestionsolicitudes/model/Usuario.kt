package com.example.gestionsolicitudes.model

// Los 3 perfiles que pide el caso
enum class Perfil { CLIENTE, ADMINISTRADOR, TECNICO }

// Usuario ficticio (el caso pide cuentas, nombres y empresas ficticias)
data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: Perfil,
    val empresa: String
)
