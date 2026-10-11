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

// Pantalla principal del administrador: ve TODAS las solicitudes, con búsqueda y filtros.
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

        // Búsqueda por código o título
        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = solicitudViewModel::onBusquedaChange,
            label = { Text("Buscar por código o título") },
            modifier = Modifier.fillMaxWidth()
        )

        // Filtros: cada botón pasa a la siguiente opción al tocarlo
        Text("Filtros (toca para cambiar)", style = MaterialTheme.typography.titleMedium)
        OutlinedButton(
            onClick = { solicitudViewModel.cambiarFiltroEstado() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Estado: ${estado.filtroEstado?.etiqueta ?: "Todos"}")
        }
        OutlinedButton(
            onClick = { solicitudViewModel.cambiarFiltroPrioridad() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Prioridad: ${estado.filtroPrioridad?.etiqueta ?: "Todas"}")
        }
        OutlinedButton(
            onClick = { solicitudViewModel.cambiarFiltroCliente() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cliente: ${estado.filtroCliente ?: "Todos"}")
        }
        OutlinedButton(
            onClick = { solicitudViewModel.cambiarFiltroResponsable() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Responsable: ${estado.filtroResponsable ?: "Todos"}")
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

// Detalle para el administrador: puede definir prioridad, asignar técnico, pedir información o cancelar.
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

                // Pasar a revisión (solo si recién llegó)
                if (solicitud.estado == EstadoSolicitud.RECIBIDA) {
                    Button(
                        onClick = { solicitudViewModel.pasarARevision(solicitud.codigo, admin) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pasar a revisión")
                    }
                }

                // Prioridad: una opción marcada a la vez
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

                // Responsable: asignar o reasignar a un técnico
                Text("Asignar responsable (técnico)", style = MaterialTheme.typography.titleMedium)
                loginViewModel.tecnicos.forEach { tecnico ->
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

                // Comentario: sirve para pedir información o para el motivo de cancelación
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
