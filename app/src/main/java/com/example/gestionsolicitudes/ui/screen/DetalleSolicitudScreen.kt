package com.example.gestionsolicitudes.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionsolicitudes.ui.components.MetaLine
import com.example.gestionsolicitudes.ui.components.PrettyCard
import com.example.gestionsolicitudes.ui.components.ScreenHeader
import com.example.gestionsolicitudes.ui.components.SectionLabel
import com.example.gestionsolicitudes.ui.components.SoftChip
import com.example.gestionsolicitudes.ui.components.SoftScreen
import com.example.gestionsolicitudes.ui.components.colorEstado
import com.example.gestionsolicitudes.ui.components.colorPrioridad
import com.example.gestionsolicitudes.utils.codigoQr
import com.example.gestionsolicitudes.viewmodel.SolicitudViewModel

@Composable
fun DetalleSolicitudScreen(
    navController: NavController,
    solicitudViewModel: SolicitudViewModel
) {
    val estado by solicitudViewModel.estado.collectAsState()
    val solicitud = estado.solicitudes.find { it.codigo == estado.codigoSeleccionado }

    SoftScreen {
        if (solicitud == null) {
            PrettyCard { Text("No se encontró la solicitud") }
        } else {
            ScreenHeader(
                title = solicitud.codigo,
                subtitle = solicitud.titulo
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SoftChip(solicitud.estado.etiqueta, colorEstado(solicitud.estado))
                SoftChip(
                    solicitud.prioridad?.etiqueta ?: "Sin prioridad",
                    colorPrioridad(solicitud.prioridad)
                )
            }

            PrettyCard {
                MetaLine("Categoría", solicitud.categoria.etiqueta)
                MetaLine("Responsable", solicitud.responsable ?: "Sin asignar")
                MetaLine("Servicio", solicitud.servicio.ifBlank { "No indicado" })
                MetaLine("Evidencia", solicitud.evidencia ?: "Sin evidencia")
                MetaLine("Creada", solicitud.fechaCreacion)
                MetaLine("Actualizada", solicitud.fechaActualizacion)
                Text(
                    solicitud.descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            PrettyCard {
                SectionLabel("Código QR")
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val qr = remember(solicitud.codigo) { codigoQr(solicitud.codigo) }
                    Image(
                        bitmap = qr,
                        contentDescription = "Código QR de ${solicitud.codigo}",
                        modifier = Modifier.size(220.dp)
                    )
                }
                Text(
                    "Escanéalo en la app para abrir esta solicitud",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SectionLabel("Historial")
            solicitud.historial.forEach { evento ->
                PrettyCard {
                    Text(
                        "${evento.fechaHora} · ${evento.accion}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("${evento.estadoAnterior?.etiqueta ?: "—"} → ${evento.estadoNuevo.etiqueta}")
                    MetaLine("Por", "${evento.usuario} (${evento.perfil.name})")
                    Text(evento.comentario, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    evento.evidencia?.let { MetaLine("Evidencia", it) }
                }
            }
        }

        OutlinedButton(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Volver")
        }
    }
}
