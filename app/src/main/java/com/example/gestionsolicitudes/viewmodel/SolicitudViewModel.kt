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

    // Room: la pantalla nunca toca la base de datos, solo este ViewModel (vía repositorio)
    private val db = AppDatabase.getDatabase(app)
    private val repositorio = SolicitudRepository(db.solicitudDao(), db.usuarioDao())

    private val _estado = MutableStateFlow(SolicitudUiState())
    val estado: StateFlow<SolicitudUiState> = _estado.asStateFlow()

    // Evidencias ficticias para elegir (el caso pide archivos de prueba)
    val evidenciasFicticias = listOf("captura_error_demo.png", "documento_demo.pdf")

    // Sirve para generar el código SOL-0001, SOL-0002, etc.
    // Se pone al día con lo que hay guardado en la base de datos
    private var contador = 0

    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    init {
        // Cada vez que cambia la tabla, el estado de la UI se actualiza solo
        viewModelScope.launch {
            repositorio.solicitudes.collect { lista ->
                contador = maxOf(contador, lista.size)
                _estado.update { it.copy(solicitudes = lista) }
            }
        }
    }

    // Actualiza el título y limpia su error
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

    // Actualiza la descripción y limpia su error
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

    // Actualiza la categoría y limpia su error
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

    // Actualiza el servicio o sitio relacionado (opcional)
    fun onServicioChange(nuevoServicio: String) {
        _estado.update { it.copy(formulario = it.formulario.copy(servicio = nuevoServicio)) }
    }

    // Actualiza la evidencia ficticia (null = sin evidencia)
    fun onEvidenciaChange(nuevaEvidencia: String?) {
        _estado.update { it.copy(formulario = it.formulario.copy(evidencia = nuevaEvidencia)) }
    }

    // Guarda cuál solicitud se va a mostrar en la pantalla de detalle
    fun seleccionarSolicitud(codigo: String) {
        _estado.update { it.copy(codigoSeleccionado = codigo) }
    }

    // Valida y crea la solicitud. Devuelve el código si salió bien, o null si hay errores
    fun enviarSolicitud(usuario: Usuario): String? {
        val form = _estado.value.formulario
        val categoria = form.categoria

        val errores = ErroresSolicitud(
            titulo = if (form.titulo.isBlank()) "Campo obligatorio" else null,
            descripcion = if (form.descripcion.trim().length < 10) "Describe la necesidad (mínimo 10 caracteres)" else null,
            categoria = if (categoria == null) "Selecciona una categoría" else null
        )

        if (categoria == null || errores.titulo != null || errores.descripcion != null) {
            _estado.update { it.copy(formulario = it.formulario.copy(errores = errores)) }
            return null
        }

        contador++
        val codigo = "SOL-" + contador.toString().padStart(4, '0')
        val ahora = LocalDateTime.now().format(formatoFecha)

        // Primer evento del historial: la solicitud nace en estado "Recibida"
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

        _estado.update {
            it.copy(
                solicitudes = it.solicitudes + nueva,
                formulario = FormularioSolicitud(),
                codigoSeleccionado = codigo
            )
        }
        // Persistir en Room (la solicitud ya se ve en pantalla; esto la deja guardada)
        viewModelScope.launch { repositorio.guardar(nueva) }
        return codigo
    }

    // ---------------- BÚSQUEDA Y FILTROS (administrador) ----------------

    fun onBusquedaChange(texto: String) {
        _estado.update { it.copy(busqueda = texto) }
    }

    // Cada botón de filtro va pasando a la siguiente opción; después de la última vuelve a "Todos"
    fun cambiarFiltroEstado() {
        _estado.update {
            it.copy(filtroEstado = siguiente(it.filtroEstado, EstadoSolicitud.entries.toList()))
        }
    }

    fun cambiarFiltroPrioridad() {
        _estado.update {
            it.copy(filtroPrioridad = siguiente(it.filtroPrioridad, Prioridad.entries.toList()))
        }
    }

    fun cambiarFiltroCliente() {
        _estado.update {
            val clientes = it.solicitudes.map { s -> s.clienteNombre }.distinct()
            it.copy(filtroCliente = siguiente(it.filtroCliente, clientes))
        }
    }

    fun cambiarFiltroResponsable() {
        _estado.update {
            val responsables = it.solicitudes.mapNotNull { s -> s.responsable }.distinct()
            it.copy(filtroResponsable = siguiente(it.filtroResponsable, responsables))
        }
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

    // Devuelve la opción que sigue a la actual. Si no hay filtro, parte por la primera; tras la última, null
    private fun <T> siguiente(actual: T?, opciones: List<T>): T? {
        if (opciones.isEmpty()) return null
        if (actual == null) return opciones.first()
        val posicion = opciones.indexOf(actual)
        return if (posicion == -1 || posicion == opciones.lastIndex) null else opciones[posicion + 1]
    }

    // ---------------- ACCIONES DEL ADMINISTRADOR ----------------

    fun onComentarioAdminChange(texto: String) {
        _estado.update { it.copy(comentarioAdmin = texto, errorComentarioAdmin = null) }
    }

    private fun buscar(codigo: String): Solicitud? {
        return _estado.value.solicitudes.find { it.codigo == codigo }
    }

    // Aplica un cambio a la solicitud y deja el evento en el historial (fecha, usuario y acción)
    private fun registrarCambio(
        codigo: String,
        usuario: Usuario,
        nuevoEstado: EstadoSolicitud,
        accion: String,
        comentario: String,
        cambio: (Solicitud) -> Solicitud = { it }
    ) {
        val ahora = LocalDateTime.now().format(formatoFecha)
        _estado.update { ui ->
            ui.copy(
                solicitudes = ui.solicitudes.map { s ->
                    if (s.codigo != codigo) {
                        s
                    } else {
                        val evento = EventoHistorial(
                            id = "${s.codigo}-E${s.historial.size + 1}",
                            estadoAnterior = s.estado,
                            estadoNuevo = nuevoEstado,
                            accion = accion,
                            comentario = comentario,
                            perfil = usuario.perfil,
                            usuario = usuario.nombre,
                            fechaHora = ahora
                        )
                        cambio(s).copy(
                            estado = nuevoEstado,
                            fechaActualizacion = ahora,
                            historial = s.historial + evento
                        )
                    }
                }
            )
        }
        // Persistir en Room la solicitud ya modificada (con su evento nuevo en el historial)
        buscar(codigo)?.let { actualizada ->
            viewModelScope.launch { repositorio.guardar(actualizada) }
        }
    }

    // Pasa la solicitud de "Recibida" a "En revisión"
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
            "Prioridad anterior: ${solicitud.prioridad?.etiqueta ?: "Sin definir"}"
        ) { it.copy(prioridad = prioridad) }
    }

    fun asignarResponsable(codigo: String, tecnico: Usuario, admin: Usuario) {
        if (admin.perfil != Perfil.ADMINISTRADOR || tecnico.perfil != Perfil.TECNICO) return
        val solicitud = buscar(codigo) ?: return
        if (!solicitud.estaAbierta || solicitud.responsable == tecnico.nombre) return

        val esReasignacion = solicitud.responsable != null
        // Si todavía no estaba asignada, pasa a "Asignada". Si ya estaba en proceso, conserva su estado
        val nuevoEstado = if (
            solicitud.estado == EstadoSolicitud.RECIBIDA ||
            solicitud.estado == EstadoSolicitud.EN_REVISION ||
            solicitud.estado == EstadoSolicitud.PENDIENTE_INFO
        ) EstadoSolicitud.ASIGNADA else solicitud.estado

        registrarCambio(
            codigo, admin, nuevoEstado,
            if (esReasignacion) "Reasignada a ${tecnico.nombre}" else "Asignada a ${tecnico.nombre}",
            "Responsable anterior: ${solicitud.responsable ?: "Sin asignar"}"
        ) { it.copy(responsable = tecnico.nombre) }
    }

    // Pide información al cliente. El comentario es obligatorio. Devuelve true si se aplicó
    fun solicitarInformacion(codigo: String, admin: Usuario): Boolean {
        if (admin.perfil != Perfil.ADMINISTRADOR) return false
        val solicitud = buscar(codigo) ?: return false
        if (!solicitud.estaAbierta) return false

        val comentario = _estado.value.comentarioAdmin.trim()
        if (comentario.isEmpty()) {
            _estado.update { it.copy(errorComentarioAdmin = "Escribe qué información necesitas") }
            return false
        }
        registrarCambio(
            codigo, admin, EstadoSolicitud.PENDIENTE_INFO,
            "Información solicitada al cliente", comentario
        )
        _estado.update { it.copy(comentarioAdmin = "", errorComentarioAdmin = null) }
        return true
    }

    // Cancela la solicitud. El motivo (el comentario) es obligatorio. Devuelve true si se aplicó
    fun cancelarSolicitud(codigo: String, admin: Usuario): Boolean {
        if (admin.perfil != Perfil.ADMINISTRADOR) return false
        val solicitud = buscar(codigo) ?: return false
        if (!solicitud.estaAbierta) return false

        val motivo = _estado.value.comentarioAdmin.trim()
        if (motivo.isEmpty()) {
            _estado.update { it.copy(errorComentarioAdmin = "Escribe el motivo de la cancelación") }
            return false
        }
        registrarCambio(
            codigo, admin, EstadoSolicitud.CANCELADA,
            "Solicitud cancelada", motivo
        ) { it.copy(motivoCancelacion = motivo) }
        _estado.update { it.copy(comentarioAdmin = "", errorComentarioAdmin = null) }
        return true
    }

    // Arma el mensaje estructurado para WhatsApp (el caso pide: código, título, categoría y referencia).
    // No incluye la descripción ni datos personales.
    fun armarMensajeWhatsApp(solicitud: Solicitud): String {
        return "Hola, registré una solicitud de soporte.\n\n" +
                "Código: ${solicitud.codigo}\n" +
                "Título: ${solicitud.titulo}\n" +
                "Categoría: ${solicitud.categoria.etiqueta}\n" +
                "Seguimiento: https://seguimiento.demo.cl/${solicitud.codigo}"
    }
}
