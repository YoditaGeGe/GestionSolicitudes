package com.example.gestionsolicitudes.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionsolicitudes.viewmodel.LoginViewModel
import com.example.gestionsolicitudes.viewmodel.SolicitudViewModel

// REEMPLAZA el InicioScreens.kt anterior.
// El cliente ve SOLO sus solicitudes y puede crear una nueva.

@Composable
fun InicioClienteScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    solicitudViewModel: SolicitudViewModel
) {
    val loginEstado by loginViewModel.estado.collectAsState()
    val estado by solicitudViewModel.estado.collectAsState()
    val usuario = loginEstado.usuarioActual

    // El cliente solo puede ver sus propias solicitudes (lo pide el caso)
    val misSolicitudes = estado.solicitudes
        .filter { it.clienteId == usuario?.id }
        .reversed()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Panel del cliente", style = MaterialTheme.typography.headlineMedium)
        Text("Bienvenido/a, ${usuario?.nombre ?: ""}")
        Text("Empresa: ${usuario?.empresa ?: ""}")

        Button(
            onClick = { navController.navigate("nueva") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nueva solicitud")
        }

        Text("Mis solicitudes (${misSolicitudes.size})", style = MaterialTheme.typography.titleLarge)

        if (misSolicitudes.isEmpty()) {
            Text("Aún no has registrado solicitudes")
        }

        misSolicitudes.forEach { solicitud ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        solicitudViewModel.seleccionarSolicitud(solicitud.codigo)
                        navController.navigate("detalle")
                    }
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(solicitud.codigo, style = MaterialTheme.typography.titleMedium)
                    Text(solicitud.titulo)
                    Text("Estado: ${solicitud.estado.etiqueta}")
                    Text("Actualizada: ${solicitud.fechaActualizacion}")
                }
            }
        }

        OutlinedButton(
            onClick = {
                loginViewModel.cerrarSesion()
                navController.navigate("login") { popUpTo(0) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

// El administrador ahora está en AdminScreens.kt. El técnico sigue igual (lo llenamos en el paso 5)

@Composable
fun InicioTecnicoScreen(navController: NavController, viewModel: LoginViewModel) {
    InicioBase("Panel del técnico", navController, viewModel)
}

@Composable
private fun InicioBase(
    titulo: String,
    navController: NavController,
    viewModel: LoginViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val usuario = estado.usuarioActual

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        Text("Bienvenido/a, ${usuario?.nombre ?: ""}")
        Text("Empresa: ${usuario?.empresa ?: ""}")

        Button(onClick = {
            viewModel.cerrarSesion()
            navController.navigate("login") { popUpTo(0) }
        }) {
            Text("Cerrar sesión")
        }
    }
}
