# Gestión de Solicitudes

App Android con Kotlin y Jetpack Compose para manejar solicitudes.

También tiene una demo de API REST (Guía 14) y tests (Guía 15).

---

## Cuentas de prueba

Clave: `123456`

- `cliente@demo.cl`
- `admin@demo.cl`
- `tecnico@demo.cl`

---

## Cómo abrirla

1. Abre el proyecto en Android Studio  
2. Sync Gradle  
3. Dale Run ▶  

Empieza en el **login**.

---

## Qué hace cada perfil

**Cliente**
- Crear solicitudes
- Ver las suyas
- Escanear QR

**Admin**
- Ver todas
- Filtrar / asignar / cambiar estado
- Escanear QR
- Abrir la demo de API (botón coral: `API demo · Posts`)

**Técnico**
- Escanear QR

---

## Guía 14 — API de prueba

Usa datos falsos de internet: [JSONPlaceholder](https://jsonplaceholder.typicode.com/)  
(No son solicitudes reales, son posts de ejemplo.)

Cómo entrar:
1. Login como admin  
2. Toca **API demo · Posts (GET/POST/PUT/DELETE)**  

Botones:
- **GET** → carga la lista  
- **POST** → crea uno  
- **PUT** → edita  
- **DELETE** → borra  

---

## Guía 15 — Tests

Correr unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Si sale `BUILD SUCCESSFUL`, está bien.

Tests:
- `PostViewModelTest`
- `PostRepositoryTest`
- `PostScreenTest` (este en emulador)

---

## Carpetas importantes

```
data/local      → Room (solicitudes)
data/remote     → Retrofit (API)
repository/     → PostRepository
viewmodel/      → lógica
ui/screen/      → pantallas de la app
ui/screens/     → PostScreen (API)
```

---

## Comandos útiles

```bash
# tests unitarios
./gradlew :app:testDebugUnitTest

# compilar
./gradlew :app:assembleDebug

# limpiar build
./gradlew clean
```

---

## Stack rápido

Compose · Room · Retrofit · QR (CameraX + ML Kit) · Kotest · MockK

Repo: https://github.com/YoditaGeGe/GestionSolicitudes
