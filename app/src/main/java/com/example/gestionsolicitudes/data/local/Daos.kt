package com.example.gestionsolicitudes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SolicitudDao {

    // Flow: la UI se actualiza sola cada vez que cambia la tabla
    @Transaction
    @Query("SELECT * FROM solicitudes ORDER BY codigo ASC")
    abstract fun observarTodas(): Flow<List<SolicitudConHistorial>>

    @Query("SELECT COUNT(*) FROM solicitudes")
    abstract suspend fun contarSolicitudes(): Int

    @Query("SELECT COUNT(*) FROM eventos_historial WHERE solicitudCodigo = :codigo")
    abstract suspend fun contarEventos(codigo: String): Int

    // @Upsert = inserta si no existe, actualiza si existe (no dispara el CASCADE del historial)
    @Upsert
    abstract suspend fun guardarSolicitud(solicitud: SolicitudEntity)

    @Insert
    abstract suspend fun insertarEventos(eventos: List<EventoHistorialEntity>)

    // Solicitud y eventos nuevos se guardan juntos o no se guarda nada
    @Transaction
    open suspend fun guardarConEventos(
        solicitud: SolicitudEntity,
        eventosNuevos: List<EventoHistorialEntity>
    ) {
        guardarSolicitud(solicitud)
        if (eventosNuevos.isNotEmpty()) insertarEventos(eventosNuevos)
    }
}

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:correo) AND clave = :clave LIMIT 1")
    suspend fun autenticar(correo: String, clave: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE perfil = 'TECNICO'")
    suspend fun tecnicos(): List<UsuarioEntity>
}
