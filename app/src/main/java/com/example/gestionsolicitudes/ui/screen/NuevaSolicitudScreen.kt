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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionsolicitudes.model.Categoria
import com.example.gestionsolicitudes.viewmodel.LoginViewModel
import com.example.gestionsolicitudes.viewmodel.SolicitudViewModel

@Composable
fun NuevaSolicitudScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    solicitudViewModel: SolicitudViewModel
) {
    val loginEstado by loginViewModel.estado.collectAsState()
    val estado by solicitudViewModel.estado.collectAsState()
    val form = estado.formulario
    val usuario = loginEstado.usuarioActual

    LaunchedEffect(estado.esperandoDetalle, estado.solicitudes) {
        val codigo = estado.codigoSeleccionado
        if (estado.esperandoDetalle && codigo != null && estado.solicitudes.any { it.codigo == codigo }) {
            solicitudViewModel.detalleAbierto()
            navController.navigate("detalle") {
                popUpTo("cliente")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nueva solicitud", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = form.titulo,
            onValueChange = solicitudViewModel::onTituloChange,
            label = { Text("Título") },
            isError = form.errores.titulo != null,
            supportingText = {
                form.errores.titulo?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.descripcion,
            onValueChange = solicitudViewModel::onDescripcionChange,
            label = { Text("Descripción de la necesidad") },
            minLines = 3,
            isError = form.errores.descripcion != null,
            supportingText = {
                form.errores.descripcion?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.servicio,
            onValueChange = solicitudViewModel::onServicioChange,
            label = { Text("Servicio o sitio web relacionado (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Categoría", style = MaterialTheme.typography.titleMedium)
        Categoria.entries.forEach { categoria ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { solicitudViewModel.onCategoriaChange(categoria) }
            ) {
                RadioButton(
                    selected = form.categoria == categoria,
                    onClick = { solicitudViewModel.onCategoriaChange(categoria) }
                )
                Text(categoria.etiqueta)
            }
        }
        form.errores.categoria?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Text("Evidencia (archivo ficticio)", style = MaterialTheme.typography.titleMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { solicitudViewModel.onEvidenciaChange(null) }
        ) {
            RadioButton(
                selected = form.evidencia == null,
                onClick = { solicitudViewModel.onEvidenciaChange(null) }
            )
            Text("Sin evidencia")
        }
        solicitudViewModel.evidenciasFicticias.forEach { archivo ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { solicitudViewModel.onEvidenciaChange(archivo) }
            ) {
                RadioButton(
                    selected = form.evidencia == archivo,
                    onClick = { solicitudViewModel.onEvidenciaChange(archivo) }
                )
                Text(archivo)
            }
        }

        Button(
            onClick = {
                if (usuario != null) {
                    solicitudViewModel.enviarSolicitud(usuario)
                }
            },
            enabled = usuario != null && !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.guardando) "Guardando..." else "Enviar solicitud")
        }

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
