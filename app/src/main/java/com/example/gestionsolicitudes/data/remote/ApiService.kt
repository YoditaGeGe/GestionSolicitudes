package com.example.gestionsolicitudes.data.remote

import com.example.gestionsolicitudes.data.model.Post
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Esta interfaz define los endpoints HTTP
interface ApiService {

    // Define una solicitud GET al endpoint /posts
    @GET("/posts")
    suspend fun getPosts(): List<Post>

    // Obtiene un post específico por su id
    @GET("/posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Post

    // Crea un nuevo post (JSONPlaceholder simula la creación)
    @POST("/posts")
    suspend fun createPost(@Body post: Post): Post

    // Actualiza un post existente
    @PUT("/posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post

    // Elimina un post
    @DELETE("/posts/{id}")
    suspend fun deletePost(@Path("id") id: Int)
}
