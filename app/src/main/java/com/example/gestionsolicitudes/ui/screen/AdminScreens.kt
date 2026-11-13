package com.example.gestionsolicitudes.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionsolicitudes.model.EstadoSolicitud
import com.example.gestionsolicitudes.model.Prioridad
import com.example.gestionsolicitudes.ui.components.MetaLine
import com.example.gestionsolicitudes.ui.components.PrettyCard
import com.example.gestionsolicitudes.ui.components.ScreenHeader
import com.example.gestionsolicitudes.ui.components.SectionLabel
import com.example.gestionsolicitudes.ui.components.SoftChip
import com.example.gestionsolicitudes.ui.components.SoftScreen
import com.example.gestionsolicitudes.ui.components.colorEstado
import com.example.gestionsolicitudes.ui.components.colorPrioridad
import com.example.gestionsolicitudes.viewmodel.LoginViewModel
import com.example.gestionsolicitudes.viewmodel.SolicitudViewModel

@Composable
fun InicioAdminScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    solicitudViewModel: SolicitudViewModel
) {
    val loginEstado by loginViewModel.estado.collectAsState()
    val estado by solicitudViewModel.estado.collectAsState()
    val usuario = loginEstado.usuarioActual
    val lista = estado.solicitudesFiltradas()

    SoftScreen {
        ScreenHeader(
            title = "Panel admin",
            subtitle = "Hola ${usuario?.nombre ?: ""}, revisa y gestiona solicitudes"
        )

        Button(
            onClick = { navController.navigate("escanear_qr") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Escanear código QR", fontWeight = FontWeight.SemiBold)
        }

        PrettyCard {
            SoftFieldAdmin(
                value = estado.busqueda,
                onValueChange = solicitudViewModel::onBusquedaChange,
                label = "Buscar por código o título"
            )

            FiltroDesplegable(
                titulo = "Estado",
                valorActual = estado.filtroEstado?.etiqueta ?: "Todos"
            ) {
                OpcionFiltro("Todos", estado.filtroEstado == null) {
                    solicitudViewModel.onFiltroEstado(null)
                }
                EstadoSolicitud.entries.forEach { opcion ->
                    OpcionFiltro(opcion.etiqueta, estado.filtroEstado == opcion) {
                        solicitudViewModel.onFiltroEstado(opcion)
                    }
                }
            }

            FiltroDesplegable(
                titulo = "Prioridad",
                valorActual = estado.filtroPrioridad?.etiqueta ?: "Todas"
            ) {
                OpcionFiltro("Todas", estado.filtroPrioridad == null) {
                    solicitudViewModel.onFiltroPrioridad(null)
                }
                Prioridad.entries.forEach { opcion ->
                    OpcionFiltro(opcion.etiqueta, estado.filtroPrioridad == opcion) {
                        solicitudViewModel.onFiltroPrioridad(opcion)
                    }
                }
            }

            FiltroDesplegable(
                titulo = "Cliente",
                valorActual = estado.filtroCliente ?: "Todos"
            ) {
                OpcionFiltro("Todos", estado.filtroCliente == null) {
                    solicitudViewModel.onFiltroCliente(null)
                }
                estado.solicitudes.map { it.clienteNombre }.distinct().forEach { nombre ->
                    OpcionFiltro(nombre, estado.filtroCliente == nombre) {
                        solicitudViewModel.onFiltroCliente(nombre)
                    }
                }
            }

            FiltroDesplegable(
                titulo = "Responsable",
                valorActual = estado.filtroResponsable ?: "Todos"
            ) {
                OpcionFiltro("Todos", estado.filtroResponsable == null) {
                    solicitudViewModel.onFiltroResponsable(null)
                }
                estado.solicitudes.mapNotNull { it.responsable }.distinct().forEach { nombre ->
                    OpcionFiltro(nombre, estado.filtroResponsable == nombre) {
                        solicitudViewModel.onFiltroResponsable(nombre)
                    }
                }
            }

            OutlinedButton(
                onClick = { solicitudViewModel.limpiarFiltros() },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpiar filtros")
            }
        }

        SectionLabel("Solicitudes (${lista.size})")

        if (lista.isEmpty()) {
            PrettyCard {
                Text("No hay solicitudes que coincidan")
            }
        }

        lista.forEach { solicitud ->
            PrettyCard(
                onClick = {
                    solicitudViewModel.seleccionarSolicitud(solicitud.codigo)
                    navController.navigate("admin_detalle")
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
                MetaLine("Cliente", "${solicitud.clienteNombre} (${solicitud.empresa})")
                SoftChip(
                    solicitud.prioridad?.etiqueta ?: "Sin prioridad",
                    colorPrioridad(solicitud.prioridad)
                )
                MetaLine("Responsable", solicitud.responsable ?: "Sin asignar")
                MetaLine("Actualizada", solicitud.fechaActualizacion)
            }
        }

        OutlinedButton(
            onClick = {
                loginViewModel.cerrarSesion()
                navController.navigate("login") { popUpTo(0) }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun AdminDetalleScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    solicitudViewModel: SolicitudViewModel
) {
    val loginEstado by loginViewModel.estado.collectAsState()
    val estado by solicitudViewModel.estado.collectAsState()
    val admin = loginEstado.usuarioActual
    val solicitud = estado.solicitudes.find { it.codigo == estado.codigoSeleccionado }

    SoftScreen {
        if (solicitud == null || admin == null) {
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
                MetaLine("Cliente", "${solicitud.clienteNombre} (${solicitud.empresa})")
                MetaLine("Categoría", solicitud.categoria.etiqueta)
                MetaLine("Responsable", solicitud.responsable ?: "Sin asignar")
                MetaLine("Servicio", solicitud.servicio.ifBlank { "No indicado" })
                MetaLine("Evidencia", solicitud.evidencia ?: "Sin evidencia")
                MetaLine("Creada", solicitud.fechaCreacion)
                MetaLine("Actualizada", solicitud.fechaActualizacion)
                Text(solicitud.descripcion)
                solicitud.motivoCancelacion?.let { MetaLine("Motivo cancelación", it) }
            }

            if (solicitud.estaAbierta) {
                PrettyCard {
                    SectionLabel("Acciones")

                    if (solicitud.estado == EstadoSolicitud.RECIBIDA) {
                        Button(
                            onClick = { solicitudViewModel.pasarARevision(solicitud.codigo, admin) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Pasar a revisión") }
                    }
                    if (solicitud.estado == EstadoSolicitud.ASIGNADA) {
                        Button(
                            onClick = { solicitudViewModel.pasarAProceso(solicitud.codigo, admin) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Marcar en proceso") }
                    }
                    if (solicitud.estado == EstadoSolicitud.EN_PROCESO) {
                        Button(
                            onClick = { solicitudViewModel.marcarResuelta(solicitud.codigo, admin) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Marcar resuelta") }
                    }

                    SectionLabel("Prioridad")
                    Prioridad.entries.forEach { prioridad ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    solicitudViewModel.definirPrioridad(solicitud.codigo, prioridad, admin)
                                }
                        ) {
                            RadioButton(
                                selected = solicitud.prioridad == prioridad,
                                onClick = {
                                    solicitudViewModel.definirPrioridad(solicitud.codigo, prioridad, admin)
                                }
                            )
                            Text(prioridad.etiqueta)
                        }
                    }

                    SectionLabel("Asignar responsable")
                    loginEstado.tecnicos.forEach { tecnico ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    solicitudViewModel.asignarResponsable(solicitud.codigo, tecnico, admin)
                                }
                        ) {
                            RadioButton(
                                selected = solicitud.responsable == tecnico.nombre,
                                onClick = {
                                    solicitudViewModel.asignarResponsable(solicitud.codigo, tecnico, admin)
                                }
                            )
                            Text(tecnico.nombre)
                        }
                    }

                    SoftFieldAdmin(
                        value = estado.comentarioAdmin,
                        onValueChange = solicitudViewModel::onComentarioAdminChange,
                        label = "Comentario (pedir info o cancelar)",
                        error = estado.errorComentarioAdmin
                    )
                    Button(
                        onClick = { solicitudViewModel.solicitarInformacion(solicitud.codigo, admin) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Solicitar información") }
                    OutlinedButton(
                        onClick = { solicitudViewModel.cancelarSolicitud(solicitud.codigo, admin) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cancelar solicitud") }
                }
            } else if (solicitud.estado == EstadoSolicitud.RESUELTA) {
                PrettyCard {
                    Text("La solicitud está resuelta 🎉")
                    Button(
                        onClick = { solicitudViewModel.cerrarSolicitud(solicitud.codigo, admin) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cerrar solicitud") }
                }
            } else {
                PrettyCard {
                    Text("Esta solicitud está ${solicitud.estado.etiqueta.lowercase()} y ya no admite cambios.")
                }
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

@Composable
private fun SoftFieldAdmin(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FiltroDesplegable(
    titulo: String,
    valorActual: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    var abierto by rememberSaveable(titulo) { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { abierto = !abierto }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = valorActual,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (abierto) "▲" else "▼",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        AnimatedVisibility(visible = abierto) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                content = contenido
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    }
}

@Composable
private fun OpcionFiltro(texto: String, marcada: Boolean, alElegir: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { alElegir() }
    ) {
        RadioButton(selected = marcada, onClick = alElegir)
        Text(texto)
    }
}
