package com.example.gestionsolicitudes.model

// Estados mínimos que pide el caso
enum class EstadoSolicitud(val etiqueta: String) {
    RECIBIDA("Recibida"),
    EN_REVISION("En revisión"),
    PENDIENTE_INFO("Pendiente de información"),
    ASIGNADA("Asignada"),
    EN_PROCESO("En proceso"),
    RESUELTA("Resuelta"),
    CERRADA("Cerrada"),
    CANCELADA("Cancelada")
}

// Prioridades sugeridas por el caso
enum class Prioridad(val etiqueta: String) {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta"),
    URGENTE("Urgente")
}

// Categorías sugeridas por el caso
enum class Categoria(val etiqueta: String) {
    DESARROLLO_WEB("Desarrollo web"),
    MANTENIMIENTO_WEB("Mantenimiento web"),
    INCIDENTE_TECNICO("Incidente técnico"),
    HOSTING("Hosting"),
    SOPORTE_CORREO("Soporte de correo"),
    MARKETING_DIGITAL("Marketing digital"),
    SOLICITUD_CAMBIO("Solicitud de cambio"),
    CONSULTA("Consulta"),
    ACCESO_CONFIGURACION("Acceso o configuración"),
    OTRO("Otro requerimiento")
}

// Cada cambio queda registrado aquí (trazabilidad)
data class EventoHistorial(
    val id: String,
    val estadoAnterior: EstadoSolicitud?,
    val estadoNuevo: EstadoSolicitud,
    val accion: String,
    val comentario: String,
    val perfil: Perfil,
    val usuario: String,
    val fechaHora: String,
    val evidencia: String? = null
)

data class Solicitud(
    val codigo: String,
    val clienteId: String,
    val clienteNombre: String,
    val empresa: String,
    val titulo: String,
    val descripcion: String,
    val categoria: Categoria,
    val servicio: String,
    val prioridad: Prioridad? = null,
    val estado: EstadoSolicitud = EstadoSolicitud.RECIBIDA,
    val responsable: String? = null,
    val motivoCancelacion: String? = null,
    val evidencia: String? = null,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val historial: List<EventoHistorial> = emptyList()
) {
    // Mientras esté abierta, el administrador puede gestionarla
    val estaAbierta: Boolean
        get() = estado != EstadoSolicitud.RESUELTA &&
                estado != EstadoSolicitud.CERRADA &&
                estado != EstadoSolicitud.CANCELADA
}
