package com.example.gestionsolicitudes.data.repository

import com.example.gestionsolicitudes.data.local.SolicitudDao
import com.example.gestionsolicitudes.data.local.UsuarioDao
import com.example.gestionsolicitudes.data.local.toEntity
import com.example.gestionsolicitudes.data.local.toModel
import com.example.gestionsolicitudes.model.Solicitud
import com.example.gestionsolicitudes.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SolicitudRepository(
    private val solicitudDao: SolicitudDao,
    private val usuarioDao: UsuarioDao
) {
    // Evita que dos guardados seguidos de la misma solicitud dupliquen eventos del historial
    private val candado = Mutex()

    // Siempre al día con la BD
    val solicitudes: Flow<List<Solicitud>> =
        solicitudDao.observarTodas().map { lista -> lista.map { it.toModel() } }

    // SOL-0001, SOL-0002...
    suspend fun siguienteCodigo(): String {
        val numero = solicitudDao.contarSolicitudes() + 1
        return "SOL-" + numero.toString().padStart(4, '0')
    }

    // Guarda la solicitud (nueva o modificada) y solo inserta los eventos del historial que aún no están en la BD.
    // Así tu registrarCambio() puede seguir armando la Solicitud completa como hoy.
    suspend fun guardar(solicitud: Solicitud) = candado.withLock {
        val yaGuardados = solicitudDao.contarEventos(solicitud.codigo)
        val nuevos = solicitud.historial.drop(yaGuardados).map { it.toEntity(solicitud.codigo) }
        solicitudDao.guardarConEventos(solicitud.toEntity(), nuevos)
    }

    suspend fun autenticar(correo: String, clave: String): Usuario? =
        usuarioDao.autenticar(correo.trim(), clave)?.toModel()

    suspend fun tecnicos(): List<Usuario> = usuarioDao.tecnicos().map { it.toModel() }
}
