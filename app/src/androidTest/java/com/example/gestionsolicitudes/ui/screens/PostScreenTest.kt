package com.example.gestionsolicitudes.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.gestionsolicitudes.data.model.Post
import com.example.gestionsolicitudes.viewmodel.PostViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test

class PostScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_de_post_debe_aparecer_en_pantalla() {
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Titulo 1", body = "Contenido 1"),
            Post(userId = 2, id = 2, title = "Titulo 2", body = "Contenido 2")
        )

        val fakeViewModel = object : PostViewModel() {
            override fun fetchPosts() {
                // Evita llamada real a la API durante el test de UI
            }

            override val postList: StateFlow<List<Post>> =
                MutableStateFlow(value = fakePosts)
        }

        composeRule.setContent {
            PostScreen(viewModel = fakeViewModel)
        }

        // La UI muestra "Título: Titulo X"; usamos substring para coincidir
        composeRule.onNodeWithText(text = "Titulo 1", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText(text = "Titulo 2", substring = true).assertIsDisplayed()
    }
}
