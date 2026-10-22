package com.example.gestionsolicitudes.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionsolicitudes.model.EstadoSolicitud
import com.example.gestionsolicitudes.model.Prioridad
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Panel del administrador", style = MaterialTheme.typography.headlineMedium)
        Text("Bienvenido/a, ${usuario?.nombre ?: ""}")

        Button(
            onClick = { navController.navigate("escanear_qr") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Escanear código QR")
        }

        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = solicitudViewModel::onBusquedaChange,
            label = { Text("Buscar por código o título") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Estado", style = MaterialTheme.typography.titleMedium)
        OpcionFiltro("Todos", estado.filtroEstado == null) {
            solicitudViewModel.onFiltroEstado(null)
        }
        EstadoSolicitud.entries.forEach { opcion ->
            OpcionFiltro(opcion.etiqueta, estado.filtroEstado == opcion) {
                solicitudViewModel.onFiltroEstado(opcion)
            }
        }

        Text("Prioridad", style = MaterialTheme.typography.titleMedium)
        OpcionFiltro("Todas", estado.filtroPrioridad == null) {
            solicitudViewModel.onFiltroPrioridad(null)
        }
        Prioridad.entries.forEach { opcion ->
            OpcionFiltro(opcion.etiqueta, estado.filtroPrioridad == opcion) {
                solicitudViewModel.onFiltroPrioridad(opcion)
            }
        }

        Text("Cliente", style = MaterialTheme.typography.titleMedium)
        OpcionFiltro("Todos", estado.filtroCliente == null) {
            solicitudViewModel.onFiltroCliente(null)
        }
        estado.solicitudes.map { it.clienteNombre }.distinct().forEach { nombre ->
            OpcionFiltro(nombre, estado.filtroCliente == nombre) {
                solicitudViewModel.onFiltroCliente(nombre)
            }
        }

        Text("Responsable", style = MaterialTheme.typography.titleMedium)
        OpcionFiltro("Todos", estado.filtroResponsable == null) {
            solicitudViewModel.onFiltroResponsable(null)
        }
        estado.solicitudes.mapNotNull { it.responsable }.distinct().forEach { nombre ->
            OpcionFiltro(nombre, estado.filtroResponsable == nombre) {
                solicitudViewModel.onFiltroResponsable(nombre)
            }
        }

        OutlinedButton(
            onClick = { solicitudViewModel.limpiarFiltros() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Limpiar filtros")
        }

        Text("Solicitudes (${lista.size})", style = MaterialTheme.typography.titleLarge)

        if (lista.isEmpty()) {
            Text("No hay solicitudes que coincidan")
        }

        lista.forEach { solicitud ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        solicitudViewModel.seleccionarSolicitud(solicitud.codigo)
                        navController.navigate("admin_detalle")
                    }
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("${solicitud.codigo} · ${solicitud.titulo}", style = MaterialTheme.typography.titleMedium)
                    Text("Cliente: ${solicitud.clienteNombre} (${solicitud.empresa})")
                    Text("Estado: ${solicitud.estado.etiqueta}")
                    Text("Prioridad: ${solicitud.prioridad?.etiqueta ?: "Sin definir"}")
                    Text("Responsable: ${solicitud.responsable ?: "Sin asignar"}")
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (solicitud == null || admin == null) {
            Text("No se encontró la solicitud")
        } else {
            Text("Gestión de solicitud", style = MaterialTheme.typography.headlineMedium)
            Text(solicitud.codigo, style = MaterialTheme.typography.titleLarge)
            Text(solicitud.titulo, style = MaterialTheme.typography.titleMedium)

            Text("Cliente: ${solicitud.clienteNombre} (${solicitud.empresa})")
            Text("Estado: ${solicitud.estado.etiqueta}")
            Text("Categoría: ${solicitud.categoria.etiqueta}")
            Text("Prioridad: ${solicitud.prioridad?.etiqueta ?: "Sin definir"}")
            Text("Responsable: ${solicitud.responsable ?: "Sin asignar"}")
            Text("Servicio relacionado: ${solicitud.servicio.ifBlank { "No indicado" }}")
            Text("Evidencia: ${solicitud.evidencia ?: "Sin evidencia"}")
            Text("Creada: ${solicitud.fechaCreacion}")
            Text("Última actualización: ${solicitud.fechaActualizacion}")
            Text("Descripción: ${solicitud.descripcion}")
            solicitud.motivoCancelacion?.let { Text("Motivo de cancelación: $it") }

            if (solicitud.estaAbierta) {
                Text(
                    "Acciones del administrador",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (solicitud.estado == EstadoSolicitud.RECIBIDA) {
                    Button(
                        onClick = { solicitudViewModel.pasarARevision(solicitud.codigo, admin) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pasar a revisión")
                    }
                }
                if (solicitud.estado == EstadoSolicitud.ASIGNADA) {
                    Button(
                        onClick = { solicitudViewModel.pasarAProceso(solicitud.codigo, admin) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Marcar en proceso")
                    }
                }
                if (solicitud.estado == EstadoSolicitud.EN_PROCESO) {
                    Button(
                        onClick = { solicitudViewModel.marcarResuelta(solicitud.codigo, admin) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Marcar resuelta")
                    }
                }

                Text("Prioridad", style = MaterialTheme.typography.titleMedium)
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

                Text("Asignar responsable (técnico)", style = MaterialTheme.typography.titleMedium)
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

                OutlinedTextField(
                    value = estado.comentarioAdmin,
                    onValueChange = solicitudViewModel::onComentarioAdminChange,
                    label = { Text("Comentario (para pedir información o cancelar)") },
                    isError = estado.errorComentarioAdmin != null,
                    supportingText = {
                        estado.errorComentarioAdmin?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { solicitudViewModel.solicitarInformacion(solicitud.codigo, admin) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Solicitar información al cliente")
                }
                OutlinedButton(
                    onClick = { solicitudViewModel.cancelarSolicitud(solicitud.codigo, admin) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar solicitud")
                }
            } else if (solicitud.estado == EstadoSolicitud.RESUELTA) {
                Text(
                    "La solicitud está resuelta",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = { solicitudViewModel.cerrarSolicitud(solicitud.codigo, admin) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar solicitud")
                }
            } else {
                Text("Esta solicitud está ${solicitud.estado.etiqueta.lowercase()} y ya no admite cambios del administrador.")
            }

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
