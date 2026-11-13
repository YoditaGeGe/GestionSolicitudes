package com.example.gestionsolicitudes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestionsolicitudes.navigation.AppNavigation
import com.example.gestionsolicitudes.ui.screens.PostScreen
import com.example.gestionsolicitudes.ui.theme.GestionSolicitudesTheme
import com.example.gestionsolicitudes.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GestionSolicitudesTheme {
                val postViewModel: PostViewModel = viewModel()
                var mostrarGestion by remember { mutableStateOf(false) }

                if (mostrarGestion) {
                    AppNavigation()
                } else {
                    PostScreen(
                        viewModel = postViewModel,
                        onIrAGestion = { mostrarGestion = true }
                    )
                }
            }
        }
    }
}
