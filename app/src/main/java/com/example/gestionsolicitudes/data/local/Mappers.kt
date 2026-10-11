package com.example.gestionsolicitudes.data.local

import com.example.gestionsolicitudes.model.EventoHistorial
import com.example.gestionsolicitudes.model.Solicitud
import com.example.gestionsolicitudes.model.Usuario

// ---- Entity -> modelo (lo que usa la UI) ----

fun UsuarioEntity.toModel() = Usuario(id, nombre, correo, clave, perfil, empresa)

fun EventoHistorialEntity.toModel() = EventoHistorial(
    id = id,
    estadoAnterior = estadoAnterior,
    estadoNuevo = estadoNuevo,
    accion = accion,
    comentario = comentario,
    perfil = perfil,
    usuario = usuario,
    fechaHora = fechaHora,
    evidencia = evidencia
)

fun SolicitudConHistorial.toModel() = Solicitud(
    codigo = solicitud.codigo,
    clienteId = solicitud.clienteId,
    clienteNombre = solicitud.clienteNombre,
    empresa = solicitud.empresa,
    titulo = solicitud.titulo,
    descripcion = solicitud.descripcion,
    categoria = solicitud.categoria,
    servicio = solicitud.servicio,
    prioridad = solicitud.prioridad,
    estado = solicitud.estado,
    responsable = solicitud.responsable,
    motivoCancelacion = solicitud.motivoCancelacion,
    evidencia = solicitud.evidencia,
    fechaCreacion = solicitud.fechaCreacion,
    fechaActualizacion = solicitud.fechaActualizacion,
    historial = eventos.sortedBy { it.pk }.map { it.toModel() }
)

// ---- modelo -> Entity (para guardar) ----

fun Solicitud.toEntity() = SolicitudEntity(
    codigo = codigo,
    clienteId = clienteId,
    clienteNombre = clienteNombre,
    empresa = empresa,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    servicio = servicio,
    prioridad = prioridad,
    estado = estado,
    responsable = responsable,
    motivoCancelacion = motivoCancelacion,
    evidencia = evidencia,
    fechaCreacion = fechaCreacion,
    fechaActualizacion = fechaActualizacion
)

fun EventoHistorial.toEntity(solicitudCodigo: String) = EventoHistorialEntity(
    id = id,
    solicitudCodigo = solicitudCodigo,
    estadoAnterior = estadoAnterior,
    estadoNuevo = estadoNuevo,
    accion = accion,
    comentario = comentario,
    perfil = perfil,
    usuario = usuario,
    fechaHora = fechaHora,
    evidencia = evidencia
)
