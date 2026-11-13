package com.example.gestionsolicitudes.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionsolicitudes.model.Categoria
import com.example.gestionsolicitudes.ui.components.PrettyCard
import com.example.gestionsolicitudes.ui.components.ScreenHeader
import com.example.gestionsolicitudes.ui.components.SectionLabel
import com.example.gestionsolicitudes.ui.components.SoftScreen
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

    SoftScreen {
        ScreenHeader(
            title = "Nueva solicitud",
            subtitle = "Cuéntanos qué necesitas, con calma"
        )

        PrettyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SoftField(
                    value = form.titulo,
                    onValueChange = solicitudViewModel::onTituloChange,
                    label = "Título",
                    error = form.errores.titulo
                )
                SoftField(
                    value = form.descripcion,
                    onValueChange = solicitudViewModel::onDescripcionChange,
                    label = "Descripción de la necesidad",
                    error = form.errores.descripcion,
                    minLines = 3
                )
                SoftField(
                    value = form.servicio,
                    onValueChange = solicitudViewModel::onServicioChange,
                    label = "Servicio o sitio web (opcional)"
                )
            }
        }

        PrettyCard {
            SectionLabel("Categoría")
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
        }

        PrettyCard {
            SectionLabel("Evidencia (archivo ficticio)")
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
        }

        Button(
            onClick = {
                if (usuario != null) {
                    solicitudViewModel.enviarSolicitud(usuario)
                }
            },
            enabled = usuario != null && !estado.guardando,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                if (estado.guardando) "Guardando..." else "Enviar solicitud",
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
private fun SoftField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        minLines = minLines,
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
