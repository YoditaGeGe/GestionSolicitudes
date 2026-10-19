package com.example.gestionsolicitudes.model

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
    val errorComentarioAdmin: String? = null,
    val guardando: Boolean = false,
    val esperandoDetalle: Boolean = false
) {
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
