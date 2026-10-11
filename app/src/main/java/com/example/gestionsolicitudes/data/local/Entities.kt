package com.example.gestionsolicitudes.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.gestionsolicitudes.model.Categoria
import com.example.gestionsolicitudes.model.EstadoSolicitud
import com.example.gestionsolicitudes.model.Perfil
import com.example.gestionsolicitudes.model.Prioridad

// Room guarda los enum directamente (como texto), por eso no hacen falta TypeConverters.

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: Perfil,
    val empresa: String
)

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey val codigo: String,
    val clienteId: String,
    val clienteNombre: String,
    val empresa: String,
    val titulo: String,
    val descripcion: String,
    val categoria: Categoria,
    val servicio: String,
    val prioridad: Prioridad?,
    val estado: EstadoSolicitud,
    val responsable: String?,
    val motivoCancelacion: String?,
    val evidencia: String?,
    val fechaCreacion: String,
    val fechaActualizacion: String
)

// Una fila por cada cambio. Si se borra la solicitud, se borra su historial (CASCADE).
@Entity(
    tableName = "eventos_historial",
    foreignKeys = [
        ForeignKey(
            entity = SolicitudEntity::class,
            parentColumns = ["codigo"],
            childColumns = ["solicitudCodigo"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("solicitudCodigo")]
)
data class EventoHistorialEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long = 0, // sirve para mantener el orden cronológico
    val id: String,                                    // "SOL-0001-E1", como en tu modelo
    val solicitudCodigo: String,
    val estadoAnterior: EstadoSolicitud?,
    val estadoNuevo: EstadoSolicitud,
    val accion: String,
    val comentario: String,
    val perfil: Perfil,
    val usuario: String,
    val fechaHora: String,
    val evidencia: String?
)

// Solicitud + todos sus eventos (relación 1 a muchos)
data class SolicitudConHistorial(
    @Embedded val solicitud: SolicitudEntity,
    @Relation(parentColumn = "codigo", entityColumn = "solicitudCodigo")
    val eventos: List<EventoHistorialEntity>
)
