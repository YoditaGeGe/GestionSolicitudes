# Gestión de Solicitudes

App Android en **Kotlin + Jetpack Compose (Material 3)** para registrar, seguir y gestionar solicitudes de soporte.

Incluye:

- Almacenamiento local con **Room**
- Lectura / generación de **códigos QR**
- Consumo de **API REST** con Retrofit (Guía 14)
- **Tests** unitarios y de UI (Guía 15)
- Interfaz amable (paleta teal + coral, cards redondeadas, chips de estado)

Repo: [YoditaGeGe/GestionSolicitudes](https://github.com/YoditaGeGe/GestionSolicitudes)

---

## Cuentas de prueba

Clave para **todas**: `123456`

| Correo | Perfil |
|--------|--------|
| `cliente@demo.cl` | Cliente |
| `admin@demo.cl` | Administrador |
| `tecnico@demo.cl` | Técnico |

---

## Cómo correrla

1. Abre el proyecto en **Android Studio**
2. Sync Gradle
3. Elige un emulador o celular (con internet si vas a probar la API)
4. Run ▶

La app **inicia en el login**.

---

## Roles y funciones

### Cliente
- Crear solicitudes (título, descripción, categoría, evidencia ficticia)
- Ver listado de sus solicitudes con chips de estado
- Ver detalle + historial + **QR** de la solicitud
- Escanear un QR para abrir una solicitud

### Administrador
- Ver todas las solicitudes
- Buscar por código o título
- Filtros **desplegables**: Estado, Prioridad, Cliente, Responsable
- Asignar técnico, definir prioridad, cambiar estados
- Pedir información al cliente o cancelar
- Generar / escanear QR
- Abrir la **demo de API REST** (solo admin)

### Técnico
- Escanear QR para abrir solicitudes
- Cerrar sesión

---

## Guía 14 — API REST (demo)

Se consume la API pública **[JSONPlaceholder](https://jsonplaceholder.typicode.com/)**.

La lista **no son solicitudes**: son **posts de prueba** (título + cuerpo) para demostrar conexión a internet.

### Cómo abrirla
1. Inicia sesión como **admin** (`admin@demo.cl`)
2. En el panel, tarjeta **API de prueba (Guía 14)**
3. Botón **Ver lista de posts (API demo)**

### Métodos HTTP en pantalla
| Botón | Qué hace |
|-------|----------|
| **GET** | Vuelve a cargar la lista desde internet |
| **POST** | Crea un post de prueba (simulado por la API) |
| **PUT** | Edita el título del post (agrega “editado”) |
| **DELETE** | Elimina el post de la lista local |

### Archivos de la capa remote
- `data/model/Post.kt`
- `data/remote/ApiService.kt`
- `data/remote/RetrofitInstance.kt`
- `repository/PostRepository.kt`
- `viewmodel/PostViewModel.kt`
- `ui/screens/PostScreen.kt`

Base URL: `https://jsonplaceholder.typicode.com`

---

## Guía 15 — Testing

Estrategia híbrida:

| Tipo | Librerías | Archivos |
|------|-----------|----------|
| Unitario | Kotest + MockK + coroutines-test | `PostViewModelTest`, `PostRepositoryTest` |
| UI | Compose UI Test + JUnit | `PostScreenTest` |

### Correr unit tests

```bash
./gradlew :app:testDebugUnitTest
```

Si ves `BUILD SUCCESSFUL`, los tests unitarios pasaron.  
Los `WARNING` de Gradle suelen ser normales y no indican fallo.

El test de UI (`PostScreenTest`) se corre en emulador/dispositivo desde Android Studio.

---

## Flujo de estados de una solicitud

`Recibida` → `En revisión` → `Asignada` → `En proceso` → `Resuelta` → `Cerrada`

También existen: `Pendiente de información` y `Cancelada`.

Prioridades: Baja · Media · Alta · Urgente

---

## Estructura del proyecto (MVVM)

```
app/src/main/java/com/example/gestionsolicitudes/
├── MainActivity.kt
├── navigation/
│   └── AppNavigation.kt          # Rutas: login, paneles, posts, QR...
├── data/
│   ├── local/                    # Room (DB, DAOs, entities, mappers)
│   ├── model/Post.kt             # Modelo API
│   ├── remote/                   # ApiService + RetrofitInstance
│   └── repository/SolicitudRepository.kt
├── repository/PostRepository.kt
├── model/                        # Solicitud, Usuario, UI state
├── viewmodel/                    # Login, Solicitud, Post, Qr
├── ui/
│   ├── components/UiBits.kt      # SoftScreen, PrettyCard, SoftChip...
│   ├── screen/                   # Login, Cliente, Admin, Detalle, QR...
│   ├── screens/PostScreen.kt     # Pantalla demo API
│   └── theme/                    # Colores, tipografía, tema
└── utils/                        # Generar / escanear QR
```

Tests:

```
app/src/test/.../viewmodel/PostViewModelTest.kt
app/src/test/.../repository/PostRepositoryTest.kt
app/src/androidTest/.../ui/screens/PostScreenTest.kt
```

---

## Stack técnico

- Kotlin · Jetpack Compose · Material 3
- Navigation Compose
- Room (+ KSP)
- Retrofit 2.11 · Gson Converter
- Kotlin Coroutines · StateFlow
- CameraX · ML Kit Barcode · ZXing (QR)
- Kotest · MockK · JUnit 5 · kotlinx-coroutines-test
- Compose UI Test

---

## Interfaz

- Paleta **teal + coral** (sin el púrpura por defecto de Material)
- Fondos con degradado suave
- Cards redondeadas
- Chips de color según estado / prioridad
- Filtros del admin en **acordeón** (se abren al tocar)

---

## Guías Duoc UC aplicadas

| Guía | Tema | Estado |
|------|------|--------|
| **14** | Consumir API REST con Retrofit + MVVM + Compose | Implementada |
| **15** | Tests unitarios (Kotest/MockK) y UI (Compose) | Implementada |

---

## Notas

- La demo de posts es **solo datos de prueba** de internet; el negocio real de la app son las **solicitudes** guardadas en Room.
- Se necesita permiso `INTERNET` (ya declarado en el manifest).
- Para Trello / capturas de consola de tests: parte colaborativa de las guías (manual).
