package com.example.gestionsolicitudes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UsuarioEntity::class, SolicitudEntity::class, EventoHistorialEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestion_solicitudes.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO usuarios VALUES ('U1','Camila Cliente','cliente@demo.cl','123456','CLIENTE','Pyme Demo SpA')")
                            db.execSQL("INSERT INTO usuarios VALUES ('U2','Andrés Admin','admin@demo.cl','123456','ADMINISTRADOR','Servicios Digitales Demo')")
                            db.execSQL("INSERT INTO usuarios VALUES ('U3','Tomás Técnico','tecnico@demo.cl','123456','TECNICO','Servicios Digitales Demo')")
                            db.execSQL("INSERT INTO usuarios VALUES ('U4','Valentina Soporte','tecnico2@demo.cl','123456','TECNICO','Servicios Digitales Demo')")
                        }
                    })
                    .build()
                    .also { instancia = it }
            }
        }
    }
}
