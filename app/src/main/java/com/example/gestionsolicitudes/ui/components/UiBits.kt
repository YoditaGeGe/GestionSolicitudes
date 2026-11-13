package com.example.gestionsolicitudes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionsolicitudes.model.EstadoSolicitud
import com.example.gestionsolicitudes.model.Prioridad
import com.example.gestionsolicitudes.ui.theme.ChipAsignada
import com.example.gestionsolicitudes.ui.theme.ChipCancelada
import com.example.gestionsolicitudes.ui.theme.ChipCerrada
import com.example.gestionsolicitudes.ui.theme.ChipPendiente
import com.example.gestionsolicitudes.ui.theme.ChipProceso
import com.example.gestionsolicitudes.ui.theme.ChipRecibida
import com.example.gestionsolicitudes.ui.theme.ChipResuelta
import com.example.gestionsolicitudes.ui.theme.ChipRevision
import com.example.gestionsolicitudes.ui.theme.CoralAccent
import com.example.gestionsolicitudes.ui.theme.SoftBackground
import com.example.gestionsolicitudes.ui.theme.TealContainer
import com.example.gestionsolicitudes.ui.theme.TealPrimary

@Composable
fun SoftScreen(
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val scroll = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(TealContainer.copy(alpha = 0.55f), SoftBackground, SoftBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (scrollable) Modifier.verticalScroll(scroll) else Modifier)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PrettyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
    )
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    if (onClick == null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            elevation = elevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = content
            )
        }
    } else {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            elevation = elevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = content
            )
        }
    }
}

@Composable
fun SoftChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun colorEstado(estado: EstadoSolicitud): Color = when (estado) {
    EstadoSolicitud.RECIBIDA -> ChipRecibida
    EstadoSolicitud.EN_REVISION -> ChipRevision
    EstadoSolicitud.PENDIENTE_INFO -> ChipPendiente
    EstadoSolicitud.ASIGNADA -> ChipAsignada
    EstadoSolicitud.EN_PROCESO -> ChipProceso
    EstadoSolicitud.RESUELTA -> ChipResuelta
    EstadoSolicitud.CERRADA -> ChipCerrada
    EstadoSolicitud.CANCELADA -> ChipCancelada
}

fun colorPrioridad(prioridad: Prioridad?): Color = when (prioridad) {
    Prioridad.BAJA -> ChipCerrada
    Prioridad.MEDIA -> ChipPendiente
    Prioridad.ALTA -> ChipRevision
    Prioridad.URGENTE -> CoralAccent
    null -> TealPrimary
}

@Composable
fun MetaLine(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SectionLabel(text: String) {
    Spacer(Modifier.height(4.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
