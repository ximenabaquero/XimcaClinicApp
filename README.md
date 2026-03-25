# XimcaClinicApp 🏥

Aplicación Android nativa para gestión de pacientes en una clínica médica.
Desarrollada en Kotlin con arquitectura MVVM, Room y Material Design 3.

---

## ¿Cómo compilar y correr el proyecto?

### Requisitos previos
| Herramienta | Versión mínima |
|---|---|
| Android Studio | Hedgehog 2023.1.1 o superior |
| Kotlin | 1.9+ |
| Compile SDK | 34 (Android 14) |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 |
| JDK | 1.8 |

### Pasos para ejecutar

1. Clona o descarga el repositorio:
   ```
   git clone https://github.com/ximenabaquero/XimcaClinicApp.git
   ```
2. Abre Android Studio → **File → Open** → selecciona la carpeta `XimcaClinicApp`.
3. Espera a que Gradle sincronice las dependencias (barra de progreso abajo).
4. Conecta un dispositivo Android o inicia un emulador (API 24+).
5. Presiona **Run ▶** o usa el atajo `Shift + F10`.

> **Nota:** La carpeta `/build` está excluida del repositorio para reducir el tamaño del ZIP.

---

## Descripción del proyecto

**Dominio elegido:** Clínica médica — gestión de pacientes.

La app permite a un médico registrarse, iniciar sesión y gestionar la lista completa de sus pacientes (crear, ver, editar y eliminar), con cálculo automático del IMC y clasificación del estado de salud.

---

## Cumplimiento de requisitos del proyecto

### Vistas implementadas (6 vistas — mínimo requerido: 4)

| # | Vista | Activity | Descripción |
|---|---|---|---|
| 1 | **Login** | `LoginActivity` | Formulario con validación de email y contraseña |
| 2 | **Registro** | `RegisterActivity` | Crear cuenta de médico con validaciones |
| 3 | **Dashboard** | `MainActivity` | Pantalla principal post-login con datos del médico y logout |
| 4 | **Lista de pacientes** | `PacienteListActivity` | RecyclerView con todos los pacientes, botón eliminar y FAB para crear |
| 5 | **Detalle del paciente** | `PacienteDetailActivity` | Todos los datos del paciente, botones editar y eliminar |
| 6 | **Formulario crear/editar** | `PacienteFormActivity` | Formulario con validación, cálculo automático de IMC, doble modo (crear y editar) |

---

### Autenticación ✅

| Requisito del profesor | Implementación |
|---|---|
| Registro local con validación | `RegisterActivity`: nombre obligatorio, email debe contener `@`, contraseña ≥ 6 caracteres |
| Login local con validación | `LoginActivity`: verifica campos vacíos y formato de email antes de consultar la BD |
| Persistencia de sesión | `SharedPreferences` guarda `userId`, `userName`, `userEmail`, `userRol` al iniciar sesión |
| Sesión activa al reabrir | `LoginActivity` revisa si ya hay sesión guardada y redirige al dashboard automáticamente |
| Cerrar sesión | `MainActivity` limpia `SharedPreferences` con `.clear()` y redirige al login |

---

### CRUD completo sobre `Paciente` ✅

| Operación | Dónde ocurre |
|---|---|
| **Crear** | `PacienteFormActivity` → `viewModel.insert()` → `PacienteRepository` → `PacienteDao.insertPaciente()` |
| **Leer (listar)** | `PacienteListActivity` observa `viewModel.allPacientes` (LiveData que viene de un Flow de Room) |
| **Leer (detalle)** | `PacienteDetailActivity` recibe los datos por Intent extras |
| **Actualizar** | `PacienteFormActivity` en modo edición → `viewModel.update()` → `PacienteDao.updatePaciente()` |
| **Eliminar** | Desde `PacienteListActivity` o `PacienteDetailActivity` → `viewModel.delete()` con AlertDialog de confirmación |
| **Actualización reactiva** | Room retorna un `Flow<List<Paciente>>` convertido a `LiveData`; la UI se actualiza sola sin reiniciar la app |

---

### Navegación e interacción ✅

| Requisito | Implementación |
|---|---|
| Paso de datos entre vistas | `Intent.putExtra()` / `intent.getExtra()` entre List → Detail → Form |
| Botón "volver" | `supportActionBar?.setDisplayHomeAsUpEnabled(true)` + `onSupportNavigateUp()` en todas las vistas CRUD |
| Back-stack coherente | `finish()` en el momento correcto para que el stack no acumule pantallas innecesarias |
| Mensajes de error en campos | `TextInputLayout.error` muestra el error justo debajo del campo que falla |
| Mensajes de éxito | `Toast` al guardar, actualizar o registrar exitosamente |
| Confirmación al eliminar | `AlertDialog` pregunta "¿Seguro que deseas eliminar?" antes de borrar |

---

## Arquitectura: MVVM

```
┌─────────────────────────────────────────────────────────────┐
│                         UI LAYER                            │
│   LoginActivity  RegisterActivity  MainActivity             │
│   PacienteListActivity  PacienteDetailActivity              │
│   PacienteFormActivity  PacienteAdapter                     │
└───────────────────────┬─────────────────────────────────────┘
                        │  observa LiveData / llama funciones
┌───────────────────────▼─────────────────────────────────────┐
│                     VIEWMODEL LAYER                         │
│   PacienteViewModel                                         │
│   - allPacientes: LiveData<List<Paciente>>                  │
│   - insert() / update() / delete()  (viewModelScope)        │
└───────────────────────┬─────────────────────────────────────┘
                        │  delega operaciones
┌───────────────────────▼─────────────────────────────────────┐
│                    REPOSITORY LAYER                         │
│   PacienteRepository                                        │
│   - allPacientes: Flow<List<Paciente>>                      │
│   - insert() / update() / delete() / getById()             │
└───────────────────────┬─────────────────────────────────────┘
                        │  consulta la BD
┌───────────────────────▼─────────────────────────────────────┐
│                      DATA LAYER                             │
│   AppDatabase (Room Singleton)                              │
│   PacienteDao  ─────────────────  Paciente (@Entity)        │
│   UsuarioDao   ─────────────────  Usuario  (@Entity)        │
└─────────────────────────────────────────────────────────────┘
```

### Justificación de MVVM

Se eligió **MVVM (Model-View-ViewModel)** porque:

- **Separación de responsabilidades:** la Activity solo maneja la UI; el ViewModel maneja la lógica de presentación; el Repository maneja el acceso a datos.
- **Supervivencia a cambios de configuración:** el ViewModel sobrevive a rotaciones de pantalla, evitando pérdida de datos.
- **Reactividad:** `Flow` + `LiveData` actualizan la lista automáticamente sin necesidad de recargar manualmente.
- **Testabilidad:** el ViewModel y el Repository se pueden probar de forma independiente.
- **Es el patrón recomendado** por Android Jetpack para aplicaciones modernas en Kotlin.

---

## Esquema de datos

### Tabla `usuarios`
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | INT (PK, autoincrement) | Identificador único |
| `nombre` | TEXT | Nombre completo del médico |
| `email` | TEXT | Correo electrónico (usado para login) |
| `password` | TEXT | Contraseña (mínimo 6 caracteres) |
| `rol` | TEXT | Rol del usuario (default: `"MÉDICO"`) |

### Tabla `pacientes`
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | INT (PK, autoincrement) | Identificador único |
| `nombre` | TEXT | Nombre del paciente |
| `apellido` | TEXT | Apellido del paciente |
| `fechaNacimiento` | TEXT | Fecha en formato `dd/mm/aaaa` |
| `peso` | REAL | Peso en kilogramos |
| `estatura` | REAL | Estatura en metros |
| `imc` | REAL | Índice de Masa Corporal (calculado automáticamente) |
| `antecedentes` | TEXT | Historial médico (opcional) |
| `estado` | TEXT | Estado: `EN_ESPERA`, `EN_CONSULTA` o `ALTA` |

---

## Validaciones implementadas

### Registro (`RegisterActivity`)
- Nombre no puede estar vacío
- Email debe contener `@`
- Contraseña debe tener al menos **6 caracteres**
- Los errores se muestran debajo del campo con `TextInputLayout.error`

### Login (`LoginActivity`)
- Email no puede estar vacío
- Email debe contener `@`
- Contraseña no puede estar vacía

### Formulario de paciente (`PacienteFormActivity`)
- Nombre y apellido obligatorios
- Fecha de nacimiento obligatoria
- Peso debe ser un número positivo
- Estatura debe ser un número positivo (en metros, ej: `1.70`)
- IMC se calcula automáticamente con `TextWatcher` (no editable)

---

## Flujo de la aplicación

```
[App abre]
    │
    ▼
¿Hay sesión guardada?
    │
    ├── SÍ → MainActivity (dashboard)
    │             │
    │             ├── "Ver Pacientes" → PacienteListActivity
    │             │         │
    │             │         ├── Tocar tarjeta → PacienteDetailActivity
    │             │         │         ├── "Editar" → PacienteFormActivity (modo edición)
    │             │         │         └── "Eliminar" → AlertDialog → regresa a lista
    │             │         │
    │             │         └── FAB "+" → PacienteFormActivity (modo creación)
    │             │
    │             └── "Cerrar Sesión" → LoginActivity
    │
    └── NO → LoginActivity
                  │
                  ├── Login correcto → MainActivity
                  └── "Regístrate" → RegisterActivity → LoginActivity
```

---

## Dependencias principales

```kotlin
// Base de datos local
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")       // Flow + coroutines
kapt("androidx.room:room-compiler:2.6.1")

// ViewModel + LiveData (arquitectura MVVM)
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

// UI
implementation("com.google.android.material:material:...")  // Material Design 3
implementation("androidx.constraintlayout:constraintlayout:...")
```

---

## Estructura del proyecto

```
app/src/main/
├── java/com/example/ximcaclinicapp/
│   ├── data/
│   │   ├── Paciente.kt           ← @Entity tabla pacientes
│   │   ├── PacienteDao.kt        ← CRUD + Flow para Room
│   │   ├── PacienteRepository.kt ← intermediario ViewModel ↔ DAO
│   │   ├── Usuario.kt            ← @Entity tabla usuarios
│   │   ├── UsuarioDao.kt         ← login + registro
│   │   └── AppDatabase.kt        ← Singleton RoomDatabase
│   ├── utils/
│   │   └── CalculoMedico.kt      ← cálculo IMC y clasificación OMS
│   ├── PacienteViewModel.kt      ← LiveData + operaciones CRUD
│   ├── PacienteAdapter.kt        ← RecyclerView ListAdapter
│   ├── LoginActivity.kt          ← vista 1: autenticación
│   ├── RegisterActivity.kt       ← vista 2: registro
│   ├── MainActivity.kt           ← vista 3: dashboard
│   ├── PacienteListActivity.kt   ← vista 4: lista RecyclerView
│   ├── PacienteDetailActivity.kt ← vista 5: detalle
│   └── PacienteFormActivity.kt   ← vista 6: crear/editar
└── res/
    ├── layout/
    │   ├── activity_login.xml
    │   ├── activity_register.xml
    │   ├── activity_main.xml
    │   ├── activity_paciente_list.xml
    │   ├── activity_paciente_detail.xml
    │   ├── activity_paciente_form.xml
    │   └── item_paciente.xml     ← tarjeta del RecyclerView
    └── values/
        ├── colors.xml
        ├── strings.xml
        └── themes.xml
```
