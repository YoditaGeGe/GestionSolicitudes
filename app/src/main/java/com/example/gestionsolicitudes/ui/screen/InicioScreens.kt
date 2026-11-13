package com.example.gestionsolicitudes.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionsolicitudes.ui.components.MetaLine
import com.example.gestionsolicitudes.ui.components.PrettyCard
import com.example.gestionsolicitudes.ui.components.ScreenHeader
import com.example.gestionsolicitudes.ui.components.SectionLabel
import com.example.gestionsolicitudes.ui.components.SoftChip
import com.example.gestionsolicitudes.ui.components.SoftScreen
import com.example.gestionsolicitudes.ui.components.colorEstado
import com.example.gestionsolicitudes.viewmodel.LoginViewModel
import com.example.gestionsolicitudes.viewmodel.SolicitudViewModel

@Composable
fun InicioClienteScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    solicitudViewModel: SolicitudViewModel
) {
    val loginEstado by loginViewModel.estado.collectAsState()
    val estado by solicitudViewModel.estado.collectAsState()
    val usuario = loginEstado.usuarioActual

    val misSolicitudes = estado.solicitudes
        .filter { it.clienteId == usuario?.id }
        .reversed()

    SoftScreen {
        ScreenHeader(
            title = "Tu panel",
            subtitle = "Hola ${usuario?.nombre ?: ""}, aquí están tus solicitudes"
        )

        PrettyCard {
            MetaLine("Empresa", usuario?.empresa ?: "—")
            Text(
                "Cuando necesites algo, crea una solicitud o escanea un código QR.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = { navController.navigate("nueva") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Nueva solicitud", fontWeight = FontWeight.SemiBold)
        }

        OutlinedButton(
            onClick = { navController.navigate("escanear_qr") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Escanear código QR")
        }

        SectionLabel("Mis solicitudes (${misSolicitudes.size})")

        if (misSolicitudes.isEmpty()) {
            PrettyCard {
                Text("Todavía no tienes solicitudes ✨")
                Text(
                    "Cuando envíes la primera, aparecerá aquí.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        misSolicitudes.forEach { solicitud ->
            PrettyCard(
                onClick = {
                    solicitudViewModel.seleccionarSolicitud(solicitud.codigo)
                    navController.navigate("detalle")
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(solicitud.codigo, style = MaterialTheme.typography.titleMedium)
                    SoftChip(solicitud.estado.etiqueta, colorEstado(solicitud.estado))
                }
                Text(solicitud.titulo, fontWeight = FontWeight.Medium)
                MetaLine("Actualizada", solicitud.fechaActualizacion)
            }
        }

        OutlinedButton(
            onClick = {
                loginViewModel.cerrarSesion()
                navController.navigate("login") { popUpTo(0) }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun InicioTecnicoScreen(navController: NavController, viewModel: LoginViewModel) {
    val estado by viewModel.estado.collectAsState()
    val usuario = estado.usuarioActual

    SoftScreen {
        ScreenHeader(
            title = "Panel técnico",
            subtitle = "Bienvenido/a, ${usuario?.nombre ?: ""}"
        )
        PrettyCard {
            MetaLine("Empresa", usuario?.empresa ?: "—")
            Text(
                "Escanea el QR de una solicitud para abrirla rápido.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Button(
            onClick = { navController.navigate("escanear_qr") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Escanear código QR", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(
            onClick = {
                viewModel.cerrarSesion()
                navController.navigate("login") { popUpTo(0) }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
