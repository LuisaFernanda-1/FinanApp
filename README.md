# FinanApp – Control de gastos personales

Aplicación móvil Android que permite registrar, consultar y administrar ingresos y gastos
personales, ver el balance financiero en tiempo real y analizar los movimientos mediante
estadísticas gráficas.

**Universidad Francisco de Paula Santander Ocaña (UFPSO)**
Ingeniería de Sistemas – Tópicos Avanzados en Ingeniería – Examen 2

**Integrantes:**
- Luisa Fernanda Ovallos Carrascal – 01992245
- Geily Nathalia Duran Torrado – 0191973

---

## 1. Descripción del proyecto

FinanApp ayuda al usuario a llevar el control de su dinero. Cada movimiento (ingreso o gasto)
se registra con descripción, valor, categoría y fecha. La aplicación calcula automáticamente
el balance disponible y muestra estadísticas para identificar en qué se gasta más.

### Funcionalidades

| Requisito | Funcionalidad | Pantalla |
|-----------|---------------|----------|
| RF01 | Dashboard con balance disponible, total de ingresos, total de gastos y últimos 5 movimientos | `DashboardScreen` |
| RF02 | Registro de ingresos y gastos con validaciones (campos obligatorios, valor mayor que cero, categoría según el tipo, selector de fecha) | `FormularioMovimientoScreen` |
| RF03 | Listado de movimientos con filtros por tipo y por categoría, más edición y eliminación con confirmación | `MovimientosScreen` / `DetalleMovimientoScreen` |
| RF04 | Cálculo automático del balance al registrar, editar o eliminar | `FinanzasViewModel` |
| RF05 | Estadísticas: resumen, gráfico de dona ingresos vs. gastos, gastos por categoría y categoría con mayor gasto | `EstadisticasScreen` |

### Categorías
- **Ingresos:** Salario, Ventas, Freelance, Inversiones, Otros ingresos.
- **Gastos:** Alimentación, Transporte, Vivienda, Servicios, Salud, Educación, Entretenimiento, Otros gastos.

---

## 2. Tecnologías utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Kotlin | 2.0.21 | Lenguaje de programación |
| Jetpack Compose + Material 3 | BOM 2024.04.01 | Interfaz de usuario declarativa |
| Navigation 3 | 1.2.1 | Navegación entre pantallas |
| Room | 2.6.1 | Persistencia local sobre SQLite |
| KSP | 2.0.21-1.0.28 | Generación de código de Room |
| Lifecycle ViewModel | 2.7.0 | Manejo del estado de la UI |
| Kotlin Coroutines + Flow | — | Operaciones asíncronas y datos reactivos |
| Kotlinx Serialization | 1.6.3 | Rutas de navegación serializables |
| Android Gradle Plugin | 8.9.1 | Compilación |

- **SDK mínimo:** 24 (Android 7.0)
- **SDK objetivo:** 35
- **Java:** 17

---

## 3. Arquitectura

El proyecto sigue el patrón **MVVM (Model–View–ViewModel)** con un **Repository**:

```
UI (Compose) ──► ViewModel ──► Repository ──► DAO (Room) ──► SQLite
     ▲                                                          │
     └──────────────── Flow (actualización automática) ◄────────┘
```

### Estructura de carpetas

```
app/src/main/java/com/ufpso/finanapp/
├── data/
│   ├── model/Movimiento.kt            # Entidad, TipoMovimiento, ResumenFinanciero, Categorias
│   ├── local/MovimientoDao.kt         # Consultas SQL (CRUD)
│   ├── local/AppDatabase.kt           # Base de datos Room (Singleton)
│   └── repository/MovimientoRepository.kt
├── ui/
│   ├── components/Componentes.kt      # Tarjetas, ítems, diálogos y gráficos (Canvas)
│   ├── navigation/Rutas.kt            # Rutas (NavKey)
│   ├── navigation/AppNavigation.kt    # NavDisplay y pila de navegación
│   ├── screens/                       # Dashboard, Formulario, Movimientos, Detalle, Estadísticas
│   ├── theme/                         # Colores y tema
│   └── viewmodel/FinanzasViewModel.kt # Lógica: balance, filtros, estadísticas, CRUD
├── util/Formato.kt                    # Formato de pesos colombianos y fechas
└── MainActivity.kt
```

### Responsabilidad de cada capa
- **Model:** define la entidad `Movimiento` (id, descripción, valor, categoría, fecha, tipo).
- **DAO:** define las consultas a la base de datos. `obtenerTodos()` devuelve un `Flow`.
- **Repository:** es el único punto de acceso a los datos y separa la UI del origen de datos.
  Decide si un movimiento se inserta (id = 0) o se actualiza.
- **ViewModel:** transforma los datos en estado para la UI (`StateFlow`): resumen, balance,
  gastos por categoría y lista filtrada. Sobrevive a rotaciones de pantalla.
- **UI:** pantallas en Compose que observan el estado con `collectAsState()`.

### Navegación
Se usa **Navigation 3**. Las rutas son objetos y clases `@Serializable` que implementan
`NavKey`. La pila de navegación (`backStack`) es una lista: navegar a una pantalla equivale a
`backStack.add(ruta)`, y volver equivale a eliminar el último elemento. Las rutas
`Formulario(id)` y `Detalle(id)` reciben el id del movimiento como parámetro.

---

## 4. Solución de persistencia

Se eligió **Room**, la librería oficial de Android Jetpack para bases de datos locales, que
funciona como una capa de abstracción sobre **SQLite**.

**¿Cómo funciona?**
1. La clase `Movimiento` está anotada con `@Entity`, y Room crea la tabla `movimientos`.
2. `MovimientoDao` define las operaciones con `@Insert`, `@Update`, `@Delete` y `@Query`.
3. `AppDatabase` crea una única instancia (patrón Singleton) del archivo `finanapp.db`.
4. Las operaciones de escritura son funciones `suspend`, de modo que se ejecutan en
   corrutinas sin bloquear la interfaz.

**¿Por qué Room?**
- **Los datos persisten:** se conservan al cerrar y volver a abrir la aplicación.
- **Es reactivo:** el DAO devuelve un `Flow`, así que cada vez que se inserta, edita o elimina
  un movimiento, Room emite la nueva lista. El ViewModel recalcula el balance y las
  estadísticas automáticamente, sin recargar la pantalla.
- **Valida en compilación:** las consultas SQL se verifican al compilar, lo que evita errores
  en tiempo de ejecución.
- **Encaja con los datos:** los movimientos tienen una estructura fija (tabla), por lo que
  una base de datos relacional es más adecuada que SharedPreferences o DataStore.

---

## 5. Instrucciones de ejecución

### Requisitos
- Android Studio (versión reciente compatible con AGP 8.9).
- JDK 17 (incluido en Android Studio).
- Emulador o dispositivo con Android 7.0 (API 24) o superior.

### Pasos
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/USUARIO/FinanApp.git
   ```
2. Abrir la carpeta del proyecto en Android Studio (**File → Open**).
3. Esperar a que Gradle sincronice las dependencias (**Sync Now**).
4. Crear o iniciar un emulador desde **Device Manager**.
5. Presionar **Run ▶** para instalar y ejecutar la aplicación.

### Instalar el APK
El APK listo para instalar está en la carpeta [`apk/FinanApp.apk`](apk/FinanApp.apk) de este
repositorio. Para generarlo de nuevo, usar
**Build → Generate App Bundles or APKs → Generate APKs**; el archivo queda en
`app/build/outputs/apk/debug/app-debug.apk`. Puede instalarse arrastrándolo al emulador o
copiándolo a un celular con instalación de orígenes desconocidos habilitada.

---

## 6. Pruebas realizadas

| # | Prueba | Resultado esperado |
|---|--------|-------------------|
| 1 | Registrar ingreso de $2.500.000 | Balance: $2.500.000 |
| 2 | Registrar gasto de $350.000 (Alimentación) | Balance: $2.150.000 |
| 3 | Registrar gasto de $150.000 (Transporte) | Balance: $2.000.000 |
| 4 | Intentar guardar con campos vacíos o valor 0 | Se muestran mensajes de error |
| 5 | Editar un movimiento | El balance se recalcula |
| 6 | Eliminar un movimiento | Se pide confirmación y el balance se actualiza |
| 7 | Filtrar por tipo y por categoría | Solo se muestran los movimientos que coinciden |
| 8 | Cerrar y volver a abrir la app | Los datos se conservan |
| 9 | Revisar estadísticas | Los totales coinciden con los movimientos registrados |

---

## 7. Video de demostración

_(agregar enlace al video)_
