# Gestión de Solicitudes

App Android en **Kotlin + Jetpack Compose** para registrar, seguir y gestionar solicitudes de soporte.

Hecha con un look amable (teal + coral), Room local, lectura de QR y consumo de API REST.

---

## Qué puedes hacer

| Perfil | Acciones |
|--------|----------|
| **Cliente** | Crear solicitudes, ver historial y escanear QR |
| **Admin** | Filtrar, asignar, cambiar estado y prioridad |
| **Técnico** | Abrir solicitudes escaneando el código QR |

También incluye un listado de **Posts** desde [JSONPlaceholder](https://jsonplaceholder.typicode.com/) (Guía 14) con GET / POST / PUT / DELETE.

---

## Cuentas de prueba

Clave para todas: `123456`

- `cliente@demo.cl`
- `admin@demo.cl`
- `tecnico@demo.cl`

---

## Cómo correrla

1. Abre el proyecto en **Android Studio**
2. Sync Gradle
3. Elige un emulador o celular
4. Run ▶

La app inicia en la pantalla de **Posts**. Toca **Gestión** para entrar al flujo de solicitudes.

---

## Estructura (MVVM)

```
app/src/main/java/.../gestionsolicitudes/
├── data/
│   ├── local/          # Room
│   ├── model/          # Post (API)
│   ├── remote/         # Retrofit
│   └── repository/
├── repository/         # PostRepository
├── model/              # Solicitud, Usuario, estados
├── viewmodel/
├── ui/
│   ├── components/     # Cards, chips, pantallas suaves
│   ├── screen/         # Login, paneles, formularios
│   ├── screens/        # PostScreen (API)
│   └── theme/
└── utils/              # QR
```

---

## Testing (Guía 15)

- Unit tests con **Kotest + MockK**
  - `PostViewModelTest`
  - `PostRepositoryTest`
- UI test con Compose
  - `PostScreenTest`

```bash
./gradlew :app:testDebugUnitTest
```

---

## Stack

- Jetpack Compose · Material 3
- Room · Navigation
- Retrofit · Gson
- CameraX · ML Kit (QR)
- Kotest · MockK · Compose UI Test

---

## Guías aplicadas

- **Guía 14** — Consumo de API REST con Retrofit  
- **Guía 15** — Pruebas unitarias y de UI  

Repo: [YoditaGeGe/GestionSolicitudes](https://github.com/YoditaGeGe/GestionSolicitudes)
