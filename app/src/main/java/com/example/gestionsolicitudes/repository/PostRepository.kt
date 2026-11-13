package com.example.gestionsolicitudes.repository

import com.example.gestionsolicitudes.data.model.Post
import com.example.gestionsolicitudes.data.remote.RetrofitInstance

// Este repositorio se encarga de acceder a los datos usando Retrofit
open class PostRepository {

    // Función que obtiene los posts desde la API
    open suspend fun getPosts(): List<Post> {
        return RetrofitInstance.api.getPosts()
    }

    open suspend fun getPost(id: Int): Post {
        return RetrofitInstance.api.getPost(id)
    }

    open suspend fun createPost(post: Post): Post {
        return RetrofitInstance.api.createPost(post)
    }

    open suspend fun updatePost(id: Int, post: Post): Post {
        return RetrofitInstance.api.updatePost(id, post)
    }

    open suspend fun deletePost(id: Int) {
        RetrofitInstance.api.deletePost(id)
    }
}
