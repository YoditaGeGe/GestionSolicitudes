package com.example.gestionsolicitudes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionsolicitudes.data.local.AppDatabase
import com.example.gestionsolicitudes.data.repository.SolicitudRepository
import com.example.gestionsolicitudes.model.Categoria
import com.example.gestionsolicitudes.model.ErroresSolicitud
import com.example.gestionsolicitudes.model.EstadoSolicitud
import com.example.gestionsolicitudes.model.EventoHistorial
import com.example.gestionsolicitudes.model.FormularioSolicitud
import com.example.gestionsolicitudes.model.Perfil
import com.example.gestionsolicitudes.model.Prioridad
import com.example.gestionsolicitudes.model.Solicitud
import com.example.gestionsolicitudes.model.SolicitudUiState
import com.example.gestionsolicitudes.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SolicitudViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getDatabase(app)
    private val repositorio = SolicitudRepository(db.solicitudDao(), db.usuarioDao())

    private val _estado = MutableStateFlow(SolicitudUiState())
    val estado: StateFlow<SolicitudUiState> = _estado.asStateFlow()

    val evidenciasFicticias = listOf("captura_error_demo.png", "documento_demo.pdf")

    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    init {
        viewModelScope.launch {
            repositorio.solicitudes.collect { lista ->
                _estado.update { it.copy(solicitudes = lista) }
            }
        }
    }

    fun onTituloChange(nuevoTitulo: String) {
        _estado.update {
            it.copy(
                formulario = it.formulario.copy(
                    titulo = nuevoTitulo,
                    errores = it.formulario.errores.copy(titulo = null)
                )
            )
        }
    }

    fun onDescripcionChange(nuevaDescripcion: String) {
        _estado.update {
            it.copy(
                formulario = it.formulario.copy(
                    descripcion = nuevaDescripcion,
                    errores = it.formulario.errores.copy(descripcion = null)
                )
            )
        }
    }

    fun onCategoriaChange(nuevaCategoria: Categoria) {
        _estado.update {
            it.copy(
                formulario = it.formulario.copy(
                    categoria = nuevaCategoria,
                    errores = it.formulario.errores.copy(categoria = null)
                )
            )
        }
    }

    fun onServicioChange(nuevoServicio: String) {
        _estado.update { it.copy(formulario = it.formulario.copy(servicio = nuevoServicio)) }
    }

    fun onEvidenciaChange(nuevaEvidencia: String?) {
        _estado.update { it.copy(formulario = it.formulario.copy(evidencia = nuevaEvidencia)) }
    }

    fun seleccionarSolicitud(codigo: String) {
        _estado.update { it.copy(codigoSeleccionado = codigo) }
    }

    fun detalleAbierto() {
        _estado.update { it.copy(esperandoDetalle = false) }
    }

    fun enviarSolicitud(usuario: Usuario) {
        if (_estado.value.guardando) return

        val form = _estado.value.formulario
        val categoria = form.categoria

        val errores = ErroresSolicitud(
            titulo = if (form.titulo.isBlank()) "Campo obligatorio" else null,
            descripcion = if (form.descripcion.trim().length < 10) "Describe la necesidad (mínimo 10 caracteres)" else null,
            categoria = if (categoria == null) "Selecciona una categoría" else null
        )

        if (categoria == null || errores.titulo != null || errores.descripcion != null) {
            _estado.update { it.copy(formulario = it.formulario.copy(errores = errores)) }
            return
        }

        _estado.update { it.copy(guardando = true) }
        viewModelScope.launch {
            try {
                val codigo = repositorio.siguienteCodigo()
                val ahora = LocalDateTime.now().format(formatoFecha)

                val primerEvento = EventoHistorial(
                    id = "$codigo-E1",
                    estadoAnterior = null,
                    estadoNuevo = EstadoSolicitud.RECIBIDA,
                    accion = "Solicitud creada",
                    comentario = "El cliente registró la solicitud",
                    perfil = usuario.perfil,
                    usuario = usuario.nombre,
                    fechaHora = ahora,
                    evidencia = form.evidencia
                )

                val nueva = Solicitud(
                    codigo = codigo,
                    clienteId = usuario.id,
                    clienteNombre = usuario.nombre,
                    empresa = usuario.empresa,
                    titulo = form.titulo.trim(),
                    descripcion = form.descripcion.trim(),
                    categoria = categoria,
                    servicio = form.servicio.trim(),
                    estado = EstadoSolicitud.RECIBIDA,
                    evidencia = form.evidencia,
                    fechaCreacion = ahora,
                    fechaActualizacion = ahora,
                    historial = listOf(primerEvento)
                )

                repositorio.guardar(nueva)
                _estado.update {
                    it.copy(
                        formulario = FormularioSolicitud(),
                        codigoSeleccionado = codigo,
                        esperandoDetalle = true
                    )
                }
            } finally {
                _estado.update { it.copy(guardando = false) }
            }
        }
    }

    fun onBusquedaChange(texto: String) {
        _estado.update { it.copy(busqueda = texto) }
    }

    fun onFiltroEstado(estado: EstadoSolicitud?) {
        _estado.update { it.copy(filtroEstado = estado) }
    }

    fun onFiltroPrioridad(prioridad: Prioridad?) {
        _estado.update { it.copy(filtroPrioridad = prioridad) }
    }

    fun onFiltroCliente(cliente: String?) {
        _estado.update { it.copy(filtroCliente = cliente) }
    }

    fun onFiltroResponsable(responsable: String?) {
        _estado.update { it.copy(filtroResponsable = responsable) }
    }

    fun limpiarFiltros() {
        _estado.update {
            it.copy(
                busqueda = "",
                filtroEstado = null,
                filtroPrioridad = null,
                filtroCliente = null,
                filtroResponsable = null
            )
        }
    }

    fun onComentarioAdminChange(texto: String) {
        _estado.update { it.copy(comentarioAdmin = texto, errorComentarioAdmin = null) }
    }

    private fun buscar(codigo: String): Solicitud? {
        return _estado.value.solicitudes.find { it.codigo == codigo }
    }

    private fun registrarCambio(
        codigo: String,
        usuario: Usuario,
        nuevoEstado: EstadoSolicitud,
        accion: String,
        comentario: String,
        prioridad: Prioridad? = null,
        responsable: String? = null,
        motivoCancelacion: String? = null
    ): Boolean {
        if (_estado.value.guardando) return false
        val solicitud = buscar(codigo) ?: return false

        val ahora = LocalDateTime.now().format(formatoFecha)
        val evento = EventoHistorial(
            id = "${solicitud.codigo}-E${solicitud.historial.size + 1}",
            estadoAnterior = solicitud.estado,
            estadoNuevo = nuevoEstado,
            accion = accion,
            comentario = comentario,
            perfil = usuario.perfil,
            usuario = usuario.nombre,
            fechaHora = ahora
        )

        val actualizada = solicitud.copy(
            estado = nuevoEstado,
            fechaActualizacion = ahora,
            historial = solicitud.historial + evento,
            prioridad = if (prioridad != null) prioridad else solicitud.prioridad,
            responsable = if (responsable != null) responsable else solicitud.responsable,
            motivoCancelacion = if (motivoCancelacion != null) motivoCancelacion else solicitud.motivoCancelacion
        )

        _estado.update { it.copy(guardando = true) }
        viewModelScope.launch {
            try {
                repositorio.guardar(actualizada)
            } finally {
                _estado.update { it.copy(guardando = false) }
            }
        }
        return true
    }

    fun pasarARevision(codigo: String, admin: Usuario) {
        if (admin.perfil != Perfil.ADMINISTRADOR) return
        val solicitud = buscar(codigo) ?: return
        if (solicitud.estado != EstadoSolicitud.RECIBIDA) return
        registrarCambio(
            codigo, admin, EstadoSolicitud.EN_REVISION,
            "Pasó a revisión", "El administrador revisa los antecedentes"
        )
    }

    fun definirPrioridad(codigo: String, prioridad: Prioridad, admin: Usuario) {
        if (admin.perfil != Perfil.ADMINISTRADOR) return
        val solicitud = buscar(codigo) ?: return
        if (!solicitud.estaAbierta || solicitud.prioridad == prioridad) return
        registrarCambio(
            codigo, admin, solicitud.estado,
            "Prioridad definida: ${prioridad.etiqueta}",
            "Prioridad anterior: ${solicitud.prioridad?.etiqueta ?: "Sin definir"}",
            prioridad = prioridad
        )
    }

    fun asignarResponsable(codigo: String, tecnico: Usuario, admin: Usuario) {
        if (admin.perfil != Perfil.ADMINISTRADOR || tecnico.perfil != Perfil.TECNICO) return
        val solicitud = buscar(codigo) ?: return
        if (!solicitud.estaAbierta || solicitud.responsable == tecnico.nombre) return

        val esReasignacion = solicitud.responsable != null
        val nuevoEstado = if (
            solicitud.estado == EstadoSolicitud.RECIBIDA ||
            solicitud.estado == EstadoSolicitud.EN_REVISION ||
            solicitud.estado == EstadoSolicitud.PENDIENTE_INFO
        ) EstadoSolicitud.ASIGNADA else solicitud.estado

        val accion = if (esReasignacion) {
            "Reasignada a ${tecnico.nombre}"
        } else {
            "Asignada a ${tecnico.nombre}"
        }

        registrarCambio(
            codigo, admin, nuevoEstado,
            accion,
            "Responsable anterior: ${solicitud.responsable ?: "Sin asignar"}",
            responsable = tecnico.nombre
        )
    }

    fun solicitarInformacion(codigo: String, admin: Usuario): Boolean {
        if (admin.perfil != Perfil.ADMINISTRADOR) return false
        val solicitud = buscar(codigo) ?: return false
        if (!solicitud.estaAbierta) return false

        val comentario = _estado.value.comentarioAdmin.trim()
        if (comentario.isEmpty()) {
            _estado.update { it.copy(errorComentarioAdmin = "Escribe qué información necesitas") }
            return false
        }

        val aplico = registrarCambio(
            codigo, admin, EstadoSolicitud.PENDIENTE_INFO,
            "Información solicitada al cliente", comentario
        )
        if (!aplico) return false

        _estado.update { it.copy(comentarioAdmin = "", errorComentarioAdmin = null) }
        return true
    }

    fun cancelarSolicitud(codigo: String, admin: Usuario): Boolean {
        if (admin.perfil != Perfil.ADMINISTRADOR) return false
        val solicitud = buscar(codigo) ?: return false
        if (!solicitud.estaAbierta) return false

        val motivo = _estado.value.comentarioAdmin.trim()
        if (motivo.isEmpty()) {
            _estado.update { it.copy(errorComentarioAdmin = "Escribe el motivo de la cancelación") }
            return false
        }

        val aplico = registrarCambio(
            codigo, admin, EstadoSolicitud.CANCELADA,
            "Solicitud cancelada", motivo,
            motivoCancelacion = motivo
        )
        if (!aplico) return false

        _estado.update { it.copy(comentarioAdmin = "", errorComentarioAdmin = null) }
        return true
    }

    fun armarMensajeWhatsApp(solicitud: Solicitud): String {
        return "Hola, registré una solicitud de soporte.\n\n" +
                "Código: ${solicitud.codigo}\n" +
                "Título: ${solicitud.titulo}\n" +
                "Categoría: ${solicitud.categoria.etiqueta}\n" +
                "Seguimiento: https://seguimiento.demo.cl/${solicitud.codigo}"
    }
}
