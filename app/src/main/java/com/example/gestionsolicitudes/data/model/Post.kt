package com.example.gestionsolicitudes.data.model

// Esta clase representa un post obtenido desde la API
data class Post(
    val userId: Int,   // ID del usuario que creó el post
    val id: Int,       // ID del post
    val title: String, // Título del post
    val body: String   // Cuerpo o contenido del post
)
