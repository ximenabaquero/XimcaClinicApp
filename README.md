# Ximka — Sistema de Gestión Clínica

Aplicación Android nativa para la gestión de pacientes de un consultorio de cirugía plástica.
Permite registrar médicos, iniciar sesión y administrar pacientes con CRUD completo, notas clínicas, cálculo automático de IMC y clasificación quirúrgica.

---

## Cómo compilar y ejecutar

| Parámetro | Valor |
|---|---|
| Android Studio | Hedgehog 2023.1.1 o superior |
| compileSdk | 36 |
| targetSdk | 35 |
| minSdk | 24 (Android 7.0) |
| Lenguaje | Kotlin |
| JDK | 17 |

**Pasos:**
1. Clonar o descomprimir el proyecto
2. Abrir Android Studio → *Open an existing project* → seleccionar la carpeta `XimcaClinicApp`
3. Esperar que Gradle sincronice (barra de progreso en la parte inferior)
4. Conectar un dispositivo o iniciar un emulador API 24+
5. Presionar **Run ▶** (`Shift + F10`)

> La carpeta `/build` está excluida del repositorio para reducir el tamaño del ZIP.

---

## Dominio elegido

**Clínica de cirugía plástica.** La entidad principal es `Paciente`, que almacena datos clínicos relevantes para el contexto quirúrgico: peso, estatura, IMC calculado, clasificación de riesgo y notas médicas.

---

## Cumplimiento de requisitos funcionales

### 1. Login y Registro ✅

**Pantallas:** `LoginActivity` y `RegisterActivity`

El médico puede crear su cuenta desde la pantalla de registro y luego iniciar sesión. La app valida los datos antes de consultar la base de datos.

**Validaciones implementadas:**

| Campo | Regla |
|---|---|
| Nombre (registro) | No puede estar vacío |
| Correo | No vacío + debe contener `@` |
| Contraseña | Mínimo 6 caracteres |

- Los errores se muestran directamente debajo de cada campo con `TextInputLayout.error`
- La contraseña se almacena con hash **SHA-256** (nunca en texto plano)
- Al iniciar sesión correctamente, los datos del médico se guardan en `SharedPreferences` (`userId`, `userName`, `userEmail`, `userRol`)
- Si ya hay sesión activa, la app salta el login automáticamente
- El botón "Salir" en el dashboard limpia la sesión y regresa al login
- `ProgressBar` visible mientras se procesa la solicitud; botón deshabilitado para evitar doble envío

---

### 2. CRUD completo sobre Paciente ✅

**Entidad:** `Paciente` — nombre, apellido, fecha de nacimiento, teléfono, peso, estatura, IMC, antecedentes, estado

| Operación | Cómo se hace |
|---|---|
| **Crear** | FAB `+` en la lista → `PacienteFormActivity` en modo "Nuevo Paciente" → `viewModel.insert()` |
| **Leer** | `PacienteListActivity` con `RecyclerView` que observa `allPacientes` (LiveData) |
| **Actualizar** | Botón "Editar" en el detalle → mismo formulario en modo "Editar Paciente" → `viewModel.update()` |
| **Eliminar** | Botón "✕" en la tarjeta o "Eliminar" en el detalle → `AlertDialog` de confirmación → `viewModel.delete()` |

La lista se **actualiza sola** al crear, editar o eliminar gracias a `Room Flow → LiveData → RecyclerView`. No es necesario recargar la app.

**Extras implementados:**
- IMC calculado automáticamente mientras se escribe peso/estatura (TextWatcher en tiempo real)
- Clasificación quirúrgica del IMC con nota clínica para el médico
- Selector de fecha con calendario visual (`MaterialDatePicker`)
- Edad calculada automáticamente desde la fecha de nacimiento
- Búsqueda de pacientes por nombre o apellido en tiempo real
- Módulo de notas clínicas por paciente con fecha y hora automática (`NotasActivity`)
- Estados del paciente: En Espera / En Consulta / Alta (con badge de color)
- Estadísticas en tiempo real en el dashboard (total, por estado)

---

### 3. Vistas con interacción observable ✅

La app cuenta con **7 pantallas**, superando el mínimo de 4 requeridas:

| # | Activity | Descripción |
|---|---|---|
| 1 | `LoginActivity` | Inicio de sesión con validación |
| 2 | `RegisterActivity` | Registro de nuevo médico |
| 3 | `MainActivity` | Dashboard: datos del médico y estadísticas |
| 4 | `PacienteListActivity` | Lista con RecyclerView, búsqueda y FAB |
| 5 | `PacienteDetailActivity` | Detalle completo del paciente y acciones |
| 6 | `PacienteFormActivity` | Formulario de crear/editar con validaciones |
| 7 | `NotasActivity` | Notas clínicas del paciente |

---

### 4. Navegación e interacción ✅

- Datos enviados entre pantallas con `Intent.putExtra()` / `intent.getExtra()`
- Botón de regreso funcional (`onSupportNavigateUp`) en todas las pantallas CRUD
- Back-stack coherente: al editar, se cierra el detalle para evitar acumulación de pantallas
- Animaciones de transición (`slide_in_left`, `fade_in`)
- `Toast` para confirmar acciones exitosas
- `AlertDialog` antes de eliminar cualquier registro
- Estado vacío visible cuando no hay pacientes ni notas

---

## Arquitectura: MVVM

Se utilizó el patrón **MVVM (Model – View – ViewModel)** con Repository, recomendado por Android Jetpack para aplicaciones nativas en Kotlin.

```
Activity / Layout  →  observa LiveData
        ↕
    ViewModel      →  ejecuta lógica en viewModelScope (coroutines)
        ↕
   Repository      →  abstrae la fuente de datos
        ↕
      DAO           →  consultas SQL con Room
        ↕
  Base de datos SQLite (Room)
```

**¿Por qué MVVM?**

- Las `Activities` no tocan la base de datos. Solo observan datos y reaccionan a cambios.
- El `ViewModel` sobrevive a rotaciones de pantalla, evitando perder el estado.
- El `Repository` separa la fuente de datos de la lógica de presentación.
- `LiveData` garantiza que la UI siempre muestre el estado actual sin consultas manuales.
- Las operaciones de base de datos se ejecutan en segundo plano con `viewModelScope.launch`, sin bloquear la pantalla.

| Clase | Capa | Responsabilidad |
|---|---|---|
| `LoginActivity`, `PacienteListActivity`, etc. | View | UI y eventos del usuario |
| `PacienteViewModel`, `NotaViewModel` | ViewModel | LiveData, lógica de presentación, coroutines |
| `PacienteRepository` | Repository | Intermediario ViewModel ↔ DAO |
| `PacienteDao`, `NotaDao`, `UsuarioDao` | DAO | Consultas SQL con Room |
| `AppDatabase` | Model | Configuración de Room y migraciones |

---

## Persistencia local: Room

Se usa **Room** como capa de abstracción sobre SQLite, con tres entidades:

### Tabla `usuarios`
| Campo | Tipo | Notas |
|---|---|---|
| id | INTEGER PK | Autogenerado |
| nombre | TEXT | Nombre del médico |
| email | TEXT | Correo de acceso |
| password | TEXT | Hash SHA-256 |
| rol | TEXT | "MÉDICO" por defecto |

### Tabla `pacientes`
| Campo | Tipo | Notas |
|---|---|---|
| id | INTEGER PK | Autogenerado |
| nombre | TEXT | — |
| apellido | TEXT | — |
| fechaNacimiento | TEXT | dd/MM/yyyy |
| telefono | TEXT | Opcional |
| peso | REAL | En kg |
| estatura | REAL | En metros |
| imc | REAL | Calculado: peso / estatura² |
| antecedentes | TEXT | Opcional |
| estado | TEXT | EN_ESPERA / EN_CONSULTA / ALTA |

### Tabla `notas`
| Campo | Tipo | Notas |
|---|---|---|
| id | INTEGER PK | Autogenerado |
| pacienteId | INTEGER | Referencia al paciente |
| texto | TEXT | Contenido de la nota |
| fechaCreacion | TEXT | dd/MM/yyyy · HH:mm |

**Migraciones implementadas** (los datos no se pierden al actualizar):
- v1 → v2: creación de la tabla `notas`
- v2 → v3: columna `telefono` agregada a `pacientes` con `ALTER TABLE`

---

## UI/UX y accesibilidad

- Diseño con **Material Design 3** (paleta azul médico + verde clínico)
- Todos los campos con `TextInputLayout` con bordes redondeados y hints flotantes
- Botones con altura mínima de 52dp (mayor a los 40dp requeridos para accesibilidad táctil)
- Badges de color por estado del paciente (ámbar / azul / verde)
- Contraste adecuado: textos oscuros sobre fondos blancos/claros
- Estados vacíos con mensaje orientador cuando no hay datos
- FAB visible en lista de pacientes y notas
- Campos de solo lectura no confundibles con editables (IMC, fecha)
- Selector de fecha visual con `MaterialDatePicker` (evita errores de formato manual)

---

## Flujo a demostrar

1. Abrir la app → pantalla de **Login**
2. Tocar "¿No tienes cuenta?" → **Registro** → crear médico → volver al login
3. Iniciar sesión → **Dashboard** con estadísticas
4. Tocar "Ver Pacientes" → lista vacía
5. FAB `+` → **Formulario nuevo paciente** → llenar datos → guardar → aparece en la lista
6. Tocar la tarjeta → **Detalle** → ver datos completos, IMC y clasificación quirúrgica
7. Tocar "Editar" → modificar un dato → guardar → el cambio se refleja en lista y detalle
8. Tocar "Ver notas" → **Notas** → agregar nota → aparece con fecha y hora
9. Volver al detalle → "Eliminar" → confirmar → la lista se actualiza
10. Volver al dashboard → "Salir" → sesión cerrada → regresa al login

---

## Estructura del proyecto

```
app/src/main/
├── java/com/example/ximcaclinicapp/
│   ├── data/
│   │   ├── Paciente.kt              @Entity - tabla pacientes
│   │   ├── PacienteDao.kt           CRUD + Flow
│   │   ├── PacienteRepository.kt    capa intermedia ViewModel ↔ DAO
│   │   ├── Nota.kt                  @Entity - tabla notas
│   │   ├── NotaDao.kt               insert/delete/query notas
│   │   ├── Usuario.kt               @Entity - tabla usuarios
│   │   ├── UsuarioDao.kt            login + registro
│   │   └── AppDatabase.kt           Singleton Room + migraciones
│   ├── utils/
│   │   ├── CalculoMedico.kt         IMC, edad, clasificación quirúrgica
│   │   ├── PacienteValidator.kt     validaciones del formulario
│   │   └── PasswordUtils.kt         hash SHA-256
│   ├── PacienteViewModel.kt         LiveData + CRUD en coroutines
│   ├── NotaViewModel.kt             LiveData + CRUD de notas
│   ├── PacienteAdapter.kt           RecyclerView ListAdapter
│   ├── NotaAdapter.kt               RecyclerView notas
│   ├── LoginActivity.kt             vista 1
│   ├── RegisterActivity.kt          vista 2
│   ├── MainActivity.kt              vista 3 - dashboard
│   ├── PacienteListActivity.kt      vista 4 - lista + búsqueda
│   ├── PacienteDetailActivity.kt    vista 5 - detalle
│   ├── PacienteFormActivity.kt      vista 6 - crear/editar
│   └── NotasActivity.kt             vista 7 - notas clínicas
└── res/
    ├── layout/                      7 layouts de Activity + items + dialog
    ├── drawable/                    gradientes, badges, chips, iconos
    └── values/
        ├── colors.xml               paleta M3 completa
        ├── themes.xml               estilos y componentes personalizados
        └── strings.xml
```
