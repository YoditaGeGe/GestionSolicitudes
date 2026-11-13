package com.example.gestionsolicitudes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionsolicitudes.data.model.Post
import com.example.gestionsolicitudes.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel que mantiene el estado de los datos obtenidos
class PostViewModel : ViewModel() {

    private val repository = PostRepository()

    // Flujo mutable que contiene la lista de posts
    private val _postList = MutableStateFlow<List<Post>>(value = emptyList())

    // Flujo público de solo lectura
    val postList: StateFlow<List<Post>> = _postList

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    // Se llama automáticamente al iniciar
    init {
        fetchPosts()
    }

    // Función que obtiene los datos en segundo plano
    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postList.value = repository.getPosts()
            } catch (e: Exception) {
                println("Error al obtener datos: ${e.localizedMessage}")
                _mensaje.value = "Error al obtener datos: ${e.localizedMessage}"
            }
        }
    }

    fun refrescar() {
        fetchPosts()
    }

    fun crearPostDemo() {
        viewModelScope.launch {
            try {
                val creado = repository.createPost(
                    Post(userId = 1, id = 0, title = "Post de prueba", body = "Creado desde la app")
                )
                _mensaje.value = "POST creado (simulado) con id ${creado.id}"
                _postList.value = listOf(creado) + _postList.value
            } catch (e: Exception) {
                _mensaje.value = "Error al crear: ${e.localizedMessage}"
            }
        }
    }

    fun actualizarPost(post: Post) {
        viewModelScope.launch {
            try {
                val actualizado = repository.updatePost(
                    post.id,
                    post.copy(title = "${post.title} (editado)")
                )
                _postList.value = _postList.value.map {
                    if (it.id == post.id) actualizado else it
                }
                _mensaje.value = "PUT actualizó el post ${actualizado.id}"
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar: ${e.localizedMessage}"
            }
        }
    }

    fun eliminarPost(post: Post) {
        viewModelScope.launch {
            try {
                repository.deletePost(post.id)
                _postList.value = _postList.value.filterNot { it.id == post.id }
                _mensaje.value = "DELETE eliminó el post ${post.id}"
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar: ${e.localizedMessage}"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
