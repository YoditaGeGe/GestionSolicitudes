package com.example.gestionsolicitudes.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionsolicitudes.utils.codigoQr
import com.example.gestionsolicitudes.viewmodel.SolicitudViewModel

@Composable
fun DetalleSolicitudScreen(
    navController: NavController,
    solicitudViewModel: SolicitudViewModel
) {
    val estado by solicitudViewModel.estado.collectAsState()
    val solicitud = estado.solicitudes.find { it.codigo == estado.codigoSeleccionado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (solicitud == null) {
            Text("No se encontró la solicitud")
        } else {
            Text("Detalle de solicitud", style = MaterialTheme.typography.headlineMedium)
            Text(solicitud.codigo, style = MaterialTheme.typography.titleLarge)
            Text(solicitud.titulo, style = MaterialTheme.typography.titleMedium)

            Text("Estado: ${solicitud.estado.etiqueta}")
            Text("Categoría: ${solicitud.categoria.etiqueta}")
            Text("Prioridad: ${solicitud.prioridad?.etiqueta ?: "Sin definir"}")
            Text("Responsable: ${solicitud.responsable ?: "Sin asignar"}")
            Text("Servicio relacionado: ${solicitud.servicio.ifBlank { "No indicado" }}")
            Text("Evidencia: ${solicitud.evidencia ?: "Sin evidencia"}")
            Text("Creada: ${solicitud.fechaCreacion}")
            Text("Última actualización: ${solicitud.fechaActualizacion}")
            Text("Descripción: ${solicitud.descripcion}")

            val qr = remember(solicitud.codigo) { codigoQr(solicitud.codigo) }
            Text(
                "Código QR de la solicitud",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    bitmap = qr,
                    contentDescription = "Código QR de ${solicitud.codigo}",
                    modifier = Modifier.size(220.dp)
                )
            }
            Text(
                "Escanéalo en la app para abrir esta solicitud",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Historial",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            solicitud.historial.forEach { evento ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${evento.fechaHora} · ${evento.accion}", style = MaterialTheme.typography.titleSmall)
                        Text("${evento.estadoAnterior?.etiqueta ?: "—"} → ${evento.estadoNuevo.etiqueta}")
                        Text("Por: ${evento.usuario} (${evento.perfil.name})")
                        Text("Comentario: ${evento.comentario}")
                        evento.evidencia?.let { Text("Evidencia: $it") }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
