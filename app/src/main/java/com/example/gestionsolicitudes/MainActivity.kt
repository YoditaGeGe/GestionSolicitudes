package com.example.gestionsolicitudes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestionsolicitudes.navigation.AppNavigation
import com.example.gestionsolicitudes.ui.theme.GestionSolicitudesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GestionSolicitudesTheme {
                AppNavigation()
            }
        }
    }
}
