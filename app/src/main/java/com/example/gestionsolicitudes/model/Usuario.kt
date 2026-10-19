package com.example.gestionsolicitudes.model

enum class Perfil { CLIENTE, ADMINISTRADOR, TECNICO }

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: Perfil,
    val empresa: String
)
