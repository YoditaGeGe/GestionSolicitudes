package com.example.gestionsolicitudes.model

// Igual que UsuarioUiState de la Guía 11: campos del formulario + errores
data class FormularioSolicitud(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val servicio: String = "",
    val evidencia: String? = null,
    val errores: ErroresSolicitud = ErroresSolicitud()
)

data class ErroresSolicitud(
    val titulo: String? = null,
    val descripcion: String? = null,
    val categoria: String? = null
)

// Estado general: solicitudes, formulario, solicitud seleccionada, filtros y comentario del admin
data class SolicitudUiState(
    val solicitudes: List<Solicitud> = emptyList(),
    val formulario: FormularioSolicitud = FormularioSolicitud(),
    val codigoSeleccionado: String? = null,
    val busqueda: String = "",
    val filtroEstado: EstadoSolicitud? = null,
    val filtroPrioridad: Prioridad? = null,
    val filtroCliente: String? = null,
    val filtroResponsable: String? = null,
    val comentarioAdmin: String = "",
    val errorComentarioAdmin: String? = null
) {
    // Devuelve las solicitudes que cumplen la búsqueda y todos los filtros (las más nuevas primero)
    fun solicitudesFiltradas(): List<Solicitud> {
        val texto = busqueda.trim()
        return solicitudes.filter { s ->
            (filtroEstado == null || s.estado == filtroEstado) &&
                    (filtroPrioridad == null || s.prioridad == filtroPrioridad) &&
                    (filtroCliente == null || s.clienteNombre == filtroCliente) &&
                    (filtroResponsable == null || s.responsable == filtroResponsable) &&
                    (texto.isEmpty() ||
                            s.codigo.contains(texto, ignoreCase = true) ||
                            s.titulo.contains(texto, ignoreCase = true))
        }.reversed()
    }
}
