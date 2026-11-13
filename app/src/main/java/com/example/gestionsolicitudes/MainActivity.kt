package com.example.gestionsolicitudes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestionsolicitudes.navigation.AppNavigation
import com.example.gestionsolicitudes.ui.screens.PostScreen
import com.example.gestionsolicitudes.ui.theme.GestionSolicitudesTheme
import com.example.gestionsolicitudes.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Permite que la app dibuje contenido debajo de las barras del sistema
        enableEdgeToEdge()

        // Aquí inicia Jetpack Compose
        setContent {
            // Aplicamos el tema Material 3
            GestionSolicitudesTheme {
                // Inyectamos el ViewModel
                val postViewModel: PostViewModel = viewModel()
                var mostrarGestion by remember { mutableStateOf(false) }

                if (mostrarGestion) {
                    Scaffold { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AppNavigation()
                        }
                    }
                } else {
                    // La app se inicia en la pantalla de posts (Guía 14)
                    PostScreen(
                        viewModel = postViewModel,
                        onIrAGestion = { mostrarGestion = true }
                    )
                }
            }
        }
    }
}
