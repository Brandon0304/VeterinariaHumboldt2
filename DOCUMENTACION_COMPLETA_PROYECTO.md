# 📚 Documentación Completa del Sistema Clínico Veterinario

## 📋 Tabla de Contenidos

1. [Introducción](#1-introducción)
2. [Arquitectura del Sistema](#2-arquitectura-del-sistema)
3. [Módulos del Sistema](#3-módulos-del-sistema)
4. [Rutas y Endpoints](#4-rutas-y-endpoints)
5. [Pruebas Unitarias y de Integración](#5-pruebas-unitarias-y-de-integración)
6. [Patrones de Diseño](#6-patrones-de-diseño)
7. [Base de Datos](#7-base-de-datos)
8. [Configuración y Tecnologías](#8-configuración-y-tecnologías)
9. [Seguridad y Autenticación](#9-seguridad-y-autenticación)
10. [Guía de Desarrollo](#10-guía-de-desarrollo)

---

## 1. Introducción

### 1.1 Descripción del Proyecto

El **Sistema Clínico Veterinario** es una aplicación backend desarrollada en **Spring Boot 3.2.0** que gestiona las operaciones de una clínica veterinaria. El sistema está diseñado como un **monolito modular**, donde cada módulo representa un dominio de negocio específico.

### 1.2 Características Principales

- ✅ **Arquitectura modular**: Separación clara de responsabilidades por módulos
- ✅ **API RESTful**: Endpoints bien estructurados y documentados
- ✅ **Autenticación JWT**: Sistema de seguridad basado en tokens
- ✅ **Base de datos PostgreSQL**: Persistencia robusta con Flyway para migraciones
- ✅ **Pruebas completas**: Unitarias y de integración
- ✅ **Documentación Swagger**: API documentada automáticamente
- ✅ **Patrones de diseño**: Implementación de patrones arquitectónicos y de diseño

### 1.3 Tecnologías Principales

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security 6.2.0**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Flyway** (Migraciones de BD)
- **JWT** (Autenticación)
- **Swagger/OpenAPI** (Documentación)
- **JUnit 5** + **Mockito** + **AssertJ** (Pruebas)
- **Testcontainers** (Pruebas de integración)

---

## 2. Arquitectura del Sistema

### 2.1 Estructura General

El sistema sigue una **arquitectura monolito modular por capas**:

```
┌─────────────────────────────────────┐
│   Controller Layer (REST API)       │
│   - Recibe requests HTTP            │
│   - Valida DTOs                     │
│   - Retorna responses               │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Service Layer (Lógica de Negocio) │
│   - Validaciones de negocio         │
│   - Coordinación de operaciones     │
│   - Mapeo DTO ↔ Entidad             │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Repository Layer (Acceso a Datos) │
│   - Operaciones CRUD                │
│   - Queries personalizadas         │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Database (PostgreSQL)             │
└─────────────────────────────────────┘
```

### 2.2 Capas por Módulo

Cada módulo sigue la estructura estándar:

```
modulo/
├── model/        # Entidades JPA
├── dto/          # Data Transfer Objects (Request/Response)
├── repository/   # Interfaces Spring Data JPA
├── service/      # Lógica de negocio
└── controller/   # Controladores REST
```

### 2.3 Principios de Diseño Aplicados

- **Separación de Responsabilidades**: Cada capa tiene una responsabilidad única
- **Inversión de Dependencias**: Dependencias inyectadas mediante Spring
- **DRY (Don't Repeat Yourself)**: Código reutilizable en módulos comunes
- **SOLID**: Principios aplicados en toda la arquitectura

---

## 3. Módulos del Sistema

### 3.1 Módulo Common

**Propósito**: Componentes compartidos entre todos los módulos

**Componentes**:
- `dto/ApiResponse`: Respuesta estándar de la API
- `exception/`: Excepciones personalizadas y manejador global
- `util/ValidationUtil`: Utilidades de validación
- `constants/AppConstants`: Constantes de la aplicación
- `event/`: Eventos del sistema (Observer pattern)

**Ubicación**: `src/main/java/com/tuorg/veterinaria/common/`

### 3.2 Módulo Config

**Propósito**: Configuraciones globales del sistema

**Componentes**:
- `SecurityConfig`: Configuración de Spring Security y JWT
- `SwaggerConfig`: Configuración de documentación OpenAPI
- `AsyncConfig`: Configuración de procesamiento asíncrono

**Ubicación**: `src/main/java/com/tuorg/veterinaria/config/`

### 3.3 Módulo Gestión de Usuarios

**Propósito**: Gestión de usuarios, autenticación y autorización

**Entidades principales**:
- `Persona` (clase base abstracta)
- `Usuario` (extiende Persona)
- `Cliente`, `UsuarioVeterinario`, `Secretario` (extienden Usuario)
- `Rol`, `Permiso`

**Funcionalidades**:
- Autenticación JWT (login, registro, refresh token)
- Gestión de usuarios y roles
- Historial de acciones

**Endpoints principales**:
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar usuario
- `GET /api/usuarios` - Listar usuarios
- `POST /api/usuarios` - Crear usuario

**Ubicación**: `src/main/java/com/tuorg/veterinaria/gestionusuarios/`

### 3.4 Módulo Gestión de Pacientes

**Propósito**: Gestión de pacientes (mascotas) y sus historias clínicas

**Entidades principales**:
- `Paciente`
- `HistoriaClinica`
- `RegistroMedico`
- `Vacunacion`
- `Desparasitacion`

**Funcionalidades**:
- Registro de pacientes
- Gestión de historias clínicas
- Control de vacunaciones y desparasitaciones
- Generación de PDF de historias clínicas

**Endpoints principales**:
- `GET /api/pacientes` - Listar pacientes
- `POST /api/pacientes` - Registrar paciente
- `GET /api/pacientes/{id}/historia-clinica` - Obtener historia clínica
- `POST /api/vacunaciones` - Registrar vacunación

**Ubicación**: `src/main/java/com/tuorg/veterinaria/gestionpacientes/`

### 3.5 Módulo Gestión de Inventario

**Propósito**: Gestión de productos, proveedores y movimientos de inventario

**Entidades principales**:
- `Producto`
- `Proveedor`
- `MovimientoInventario`
- `AlertaInventario`

**Funcionalidades**:
- Gestión de productos y stock
- Movimientos de entrada/salida
- Reversión de movimientos (Command pattern con undo)
- Alertas de stock bajo

**Endpoints principales**:
- `GET /api/productos` - Listar productos
- `POST /api/productos` - Crear producto
- `POST /api/movimientos-inventario/entrada` - Registrar entrada
- `POST /api/movimientos-inventario/{id}/revertir` - Revertir movimiento

**Ubicación**: `src/main/java/com/tuorg/veterinaria/gestioninventario/`

### 3.6 Módulo Gestión de Facturación

**Propósito**: Gestión de facturas y pagos

**Entidades principales**:
- `Factura`

**Funcionalidades**:
- Creación de facturas con número único (Factory pattern)
- Registro de pagos
- Consulta de facturas por cliente

**Endpoints principales**:
- `POST /api/facturas` - Crear factura
- `GET /api/facturas/{id}` - Obtener factura
- `POST /api/facturas/{id}/pago` - Registrar pago

**Ubicación**: `src/main/java/com/tuorg/veterinaria/gestionfacturacion/`

### 3.7 Módulo Prestación de Servicios

**Propósito**: Gestión de citas médicas y servicios prestados

**Entidades principales**:
- `Cita`
- `Servicio` (catálogo)
- `ServicioPrestado`

**Funcionalidades**:
- Programación de citas
- Registro de servicios prestados
- Estados de citas (programada, realizada, cancelada)

**Endpoints principales**:
- `POST /api/citas` - Crear cita
- `PUT /api/citas/{id}/reprogramar` - Reprogramar cita
- `POST /api/servicios-prestados` - Registrar servicio prestado

**Ubicación**: `src/main/java/com/tuorg/veterinaria/prestacioneservicios/`

### 3.8 Módulo Notificaciones

**Propósito**: Sistema de notificaciones multi-canal

**Entidades principales**:
- `CanalEnvio` (clase base)
- `CanalEmail`, `CanalSMS`, `CanalApp` (estrategias)
- `Notificacion`

**Funcionalidades**:
- Envío de notificaciones por diferentes canales (Strategy pattern)
- Programación de notificaciones
- Eventos de notificaciones (Observer pattern)

**Endpoints principales**:
- `POST /api/notificaciones/enviar` - Enviar notificación
- `POST /api/notificaciones/programar` - Programar notificación

**Ubicación**: `src/main/java/com/tuorg/veterinaria/notificaciones/`

### 3.9 Módulo Reportes

**Propósito**: Generación de reportes y estadísticas

**Entidades principales**:
- `Reporte`
- `Estadistica`
- `Indicador`

**Funcionalidades**:
- Generación de reportes (Facade pattern)
- Cálculo de estadísticas
- Evaluación de indicadores
- Eventos de reportes generados (Observer pattern)

**Endpoints principales**:
- `POST /api/reportes` - Generar reporte
- `GET /api/reportes/{id}` - Obtener reporte

**Ubicación**: `src/main/java/com/tuorg/veterinaria/reportes/`

### 3.10 Módulo Configuración

**Propósito**: Configuración del sistema y logs

**Entidades principales**:
- `ParametroSistema`
- `LogSistema`
- `BackupSistema`

**Funcionalidades**:
- Gestión de parámetros del sistema (Singleton pattern)
- Registro de logs del sistema
- Gestión de backups

**Endpoints principales**:
- `GET /api/configuracion/parametros` - Obtener parámetros
- `PUT /api/configuracion/parametros/{clave}` - Actualizar parámetro

**Ubicación**: `src/main/java/com/tuorg/veterinaria/configuracion/`

---

## 4. Rutas y Endpoints

### 4.1 Context Path

**Base URL**: `http://localhost:8080/api`

Todos los endpoints están bajo el contexto `/api` configurado en `application.yml`.

### 4.2 Autenticación

| Método | Ruta | Descripción | Autenticación |
|--------|------|------------|---------------|
| POST | `/api/auth/login` | Iniciar sesión | No requerida |
| POST | `/api/auth/register` | Registrar usuario | No requerida |
| POST | `/api/auth/refresh` | Refrescar token | No requerida |

### 4.3 Gestión de Usuarios

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/api/usuarios` | Listar usuarios | ADMIN |
| POST | `/api/usuarios` | Crear usuario | ADMIN |
| GET | `/api/usuarios/{id}` | Obtener usuario | ADMIN |
| PUT | `/api/usuarios/{id}` | Actualizar usuario | ADMIN |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario | ADMIN |
| GET | `/api/clientes` | Listar clientes | ADMIN, VETERINARIO |
| POST | `/api/clientes` | Crear cliente | ADMIN |

### 4.4 Gestión de Pacientes

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/api/pacientes` | Listar pacientes | ADMIN, VETERINARIO, CLIENTE |
| POST | `/api/pacientes` | Registrar paciente | ADMIN, VETERINARIO |
| GET | `/api/pacientes/{id}` | Obtener paciente | ADMIN, VETERINARIO, CLIENTE |
| PUT | `/api/pacientes/{id}` | Actualizar paciente | ADMIN, VETERINARIO |
| GET | `/api/pacientes/{id}/historia-clinica` | Obtener historia clínica | ADMIN, VETERINARIO |
| POST | `/api/vacunaciones` | Registrar vacunación | ADMIN, VETERINARIO |
| POST | `/api/desparasitaciones` | Registrar desparasitación | ADMIN, VETERINARIO |

### 4.5 Gestión de Inventario

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/api/productos` | Listar productos | ADMIN, VETERINARIO, SECRETARIO |
| POST | `/api/productos` | Crear producto | ADMIN |
| GET | `/api/productos/{id}` | Obtener producto | ADMIN, VETERINARIO, SECRETARIO |
| PUT | `/api/productos/{id}` | Actualizar producto | ADMIN |
| GET | `/api/proveedores` | Listar proveedores | ADMIN |
| POST | `/api/proveedores` | Crear proveedor | ADMIN |
| POST | `/api/movimientos-inventario/entrada` | Registrar entrada | ADMIN |
| POST | `/api/movimientos-inventario/salida` | Registrar salida | ADMIN |
| POST | `/api/movimientos-inventario/{id}/revertir` | Revertir movimiento | ADMIN |

### 4.6 Gestión de Facturación

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/api/facturas` | Listar facturas | ADMIN, SECRETARIO |
| POST | `/api/facturas` | Crear factura | ADMIN, SECRETARIO |
| GET | `/api/facturas/{id}` | Obtener factura | ADMIN, SECRETARIO |
| POST | `/api/facturas/{id}/pago` | Registrar pago | ADMIN, SECRETARIO |

### 4.7 Prestación de Servicios

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/api/citas` | Listar citas | ADMIN, VETERINARIO, SECRETARIO |
| POST | `/api/citas` | Crear cita | ADMIN, SECRETARIO |
| PUT | `/api/citas/{id}/reprogramar` | Reprogramar cita | ADMIN, SECRETARIO |
| PUT | `/api/citas/{id}/cancelar` | Cancelar cita | ADMIN, SECRETARIO |
| POST | `/api/servicios-prestados` | Registrar servicio prestado | ADMIN, VETERINARIO |

### 4.8 Notificaciones

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| POST | `/api/notificaciones/enviar` | Enviar notificación | ADMIN |
| POST | `/api/notificaciones/programar` | Programar notificación | ADMIN |
| GET | `/api/notificaciones` | Listar notificaciones | ADMIN |

### 4.9 Reportes

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| POST | `/api/reportes` | Generar reporte | ADMIN |
| GET | `/api/reportes/{id}` | Obtener reporte | ADMIN |

### 4.10 Configuración

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/api/configuracion/parametros` | Obtener parámetros | ADMIN |
| PUT | `/api/configuracion/parametros/{clave}` | Actualizar parámetro | ADMIN |

---

## 5. Pruebas Unitarias y de Integración

### 5.1 Estructura de Pruebas

Las pruebas están organizadas en la misma estructura que el código principal:

```
src/test/java/com/tuorg/veterinaria/
├── config/
│   ├── AbstractIntegrationTest.java  # Clase base para pruebas de integración
│   └── TestConfig.java                # Configuración de pruebas
├── gestionusuarios/
│   ├── service/
│   │   ├── AuthServiceTest.java              # Pruebas unitarias de autenticación
│   │   ├── UsuarioServiceTest.java           # Pruebas unitarias de usuarios
│   │   └── ClienteServiceTest.java           # Pruebas unitarias de clientes
│   └── controller/
│       └── AuthControllerIntegrationTest.java # Pruebas de integración de autenticación
├── gestionpacientes/
│   └── service/
│       ├── PacienteServiceTest.java          # Pruebas unitarias de pacientes
│       └── VacunacionServiceTest.java       # Pruebas unitarias de vacunaciones
├── gestioninventario/
│   └── service/
│       ├── ProductoServiceTest.java          # Pruebas unitarias de productos
│       └── MovimientoInventarioServiceTest.java # Pruebas unitarias de movimientos
├── gestionfacturacion/
│   └── service/
│       └── FacturaServiceTest.java           # Pruebas unitarias de facturas
└── prestacioneservicios/
    └── service/
        └── CitaServiceTest.java              # Pruebas unitarias de citas
```

**Ubicación exacta**: `src/test/java/com/tuorg/veterinaria/`

**Total de archivos de prueba**: 11 archivos
- **Pruebas unitarias**: 9 archivos (`*Test.java`)
- **Pruebas de integración**: 2 archivos (`*IntegrationTest.java` y `AbstractIntegrationTest.java`)

### 5.2 Pruebas Unitarias

**Descripción**: Prueban componentes individuales (servicios) de forma aislada, usando mocks para las dependencias.

**Herramientas**: 
- **JUnit 5**: Framework de pruebas
- **Mockito**: Para crear mocks de dependencias
- **AssertJ**: Para aserciones fluidas y legibles

**Ubicaciones exactas**:

1. **AuthServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestionusuarios/service/AuthServiceTest.java`
   - **Prueba**: Lógica de autenticación, generación de tokens JWT
   - **Mocks**: `UsuarioRepository`, `AuthenticationManager`, `JwtTokenProvider`

2. **UsuarioServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestionusuarios/service/UsuarioServiceTest.java`
   - **Prueba**: Gestión de usuarios, validaciones de negocio

3. **ClienteServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestionusuarios/service/ClienteServiceTest.java`
   - **Prueba**: Gestión de clientes

4. **PacienteServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestionpacientes/service/PacienteServiceTest.java`
   - **Prueba**: Gestión de pacientes, validaciones de especie, fecha de nacimiento

5. **VacunacionServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestionpacientes/service/VacunacionServiceTest.java`
   - **Prueba**: Gestión de vacunaciones

6. **ProductoServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestioninventario/service/ProductoServiceTest.java`
   - **Prueba**: Gestión de productos, validaciones de stock

7. **MovimientoInventarioServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestioninventario/service/MovimientoInventarioServiceTest.java`
   - **Prueba**: Movimientos de inventario, reversión de comandos

8. **FacturaServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestionfacturacion/service/FacturaServiceTest.java`
   - **Prueba**: Creación de facturas (Factory pattern)

9. **CitaServiceTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/prestacioneservicios/service/CitaServiceTest.java`
   - **Prueba**: Gestión de citas, estados, reprogramación

**Ejemplo completo - AuthServiceTest**:

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    @DisplayName("Login exitoso: debe generar token y actualizar último acceso")
    void loginExitoso_DeberiaGenerarTokenYActualizarUltimoAcceso() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "password");
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        
        // Act & Assert
        // ...
    }
}
```

**Características**:
- Usan `@ExtendWith(MockitoExtension.class)`
- Dependencias mockeadas con `@Mock`
- Servicio bajo prueba con `@InjectMocks`
- No requieren base de datos ni contexto Spring completo
- Ejecución rápida

### 5.3 Pruebas de Integración

**Descripción**: Prueban flujos completos con base de datos real, contexto Spring completo y endpoints REST.

**Herramientas**:
- **Spring Boot Test**: Contexto completo de Spring
- **Testcontainers**: PostgreSQL en contenedor Docker
- **MockMvc**: Para probar endpoints REST sin servidor completo

**Ubicaciones exactas**:

1. **AbstractIntegrationTest** (Clase base):
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/config/AbstractIntegrationTest.java`
   - **Líneas**: 1-44
   - **Propósito**: Configura Testcontainers con PostgreSQL para todas las pruebas de integración
   - **Configuración**: Líneas 25-36 (contenedor PostgreSQL, propiedades dinámicas)

2. **AuthControllerIntegrationTest**:
   - **Archivo**: `src/test/java/com/tuorg/veterinaria/gestionusuarios/controller/AuthControllerIntegrationTest.java`
   - **Líneas**: 1-118
   - **Prueba**: Endpoints REST de autenticación (`/api/auth/login`)
   - **Extiende**: `AbstractIntegrationTest`
   - **Métodos de prueba**: 3 métodos (líneas 70-87, 89-102, 104-117)

**Ejemplo completo - AbstractIntegrationTest**:

```18:44:src/test/java/com/tuorg/veterinaria/config/AbstractIntegrationTest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@SuppressWarnings("resource")
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("veterinaria_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @AfterAll
    static void tearDown() {
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }
}
```

**Ejemplo completo - AuthControllerIntegrationTest**:

```70:117:src/test/java/com/tuorg/veterinaria/gestionusuarios/controller/AuthControllerIntegrationTest.java
    @Test
    @DisplayName("POST /api/auth/login: credenciales correctas debe retornar 200 con token")
    void loginCredencialesCorrectas_DeberiaRetornar200ConToken() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.usuario.username").value("testuser"));
    }

    @Test
    @DisplayName("POST /api/auth/login: credenciales incorrectas debe retornar 401")
    void loginCredencialesIncorrectas_DeberiaRetornar401() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("passwordIncorrecta");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login: usuario inexistente debe retornar error")
    void loginUsuarioInexistente_DeberiaRetornarError() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("usuarioInexistente");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
```

**Características**:
- Usan `@SpringBootTest` con contexto completo
- Extienden `AbstractIntegrationTest`
- Base de datos PostgreSQL real en contenedor Docker
- `MockMvc` para probar endpoints REST
- Ejecución más lenta (requieren Docker)

### 5.3.1 Guía Paso a Paso: Cómo Ejecutar Pruebas de Integración

#### Paso 1: Verificar Requisitos Previos

**1.1 Verificar que Docker está instalado y ejecutándose**:

```bash
# Abrir terminal (PowerShell o CMD)
docker --version

# Verificar que Docker está corriendo
docker ps

# Si Docker no está corriendo, verás un error como:
# "error during connect: This error may indicate that the docker daemon is not running"
```

**Si Docker no está corriendo**:
1. Abrir **Docker Desktop**
2. Esperar a que aparezca el ícono de Docker en la bandeja del sistema
3. Verificar nuevamente con `docker ps`

**1.2 Verificar que Maven está instalado**:

```bash
mvn --version
```

**1.3 Verificar que Java 17 está instalado**:

```bash
java -version
```

#### Paso 2: Navegar al Directorio del Proyecto

```bash
# Abrir terminal y navegar al proyecto
cd "C:\Users\Lab Ingenieria 12\Downloads\proyectoVeterinaria"

# Verificar que estás en el directorio correcto
dir pom.xml
```

#### Paso 3: Ejecutar las Pruebas de Integración

**Opción A: Ejecutar solo pruebas de integración (recomendado)**:

```bash
mvn test -Dtest=*IntegrationTest
```

**Opción B: Ejecutar una clase específica de integración**:

```bash
mvn test -Dtest=AuthControllerIntegrationTest
```

**Opción C: Ejecutar todas las pruebas (unitarias + integración)**:

```bash
mvn test
```

#### Paso 4: Qué Sucede Durante la Ejecución

**Proceso automático**:

1. **Maven compila el proyecto**:
   ```
   [INFO] Compiling...
   ```

2. **Testcontainers descarga la imagen de PostgreSQL** (solo la primera vez):
   ```
   [INFO] Pulling image: postgres:15-alpine
   ```
   ⚠️ **Nota**: La primera vez puede tardar 1-2 minutos descargando la imagen.

3. **Testcontainers inicia el contenedor PostgreSQL**:
   ```
   [INFO] Starting PostgreSQL container...
   ```

4. **Spring Boot inicia el contexto completo**:
   ```
   [INFO] Starting VeterinariaApplication...
   ```

5. **Flyway ejecuta las migraciones** en la BD de prueba:
   ```
   [INFO] Flyway migration...
   ```

6. **Se ejecutan las pruebas**:
   ```
   [INFO] Running AuthControllerIntegrationTest...
   ```

7. **Testcontainers detiene y elimina el contenedor**:
   ```
   [INFO] Stopping PostgreSQL container...
   ```

#### Paso 5: Interpretar los Resultados

**Resultado exitoso**:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.tuorg.veterinaria.gestionusuarios.controller.AuthControllerIntegrationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**Resultado con fallos**:

```
[ERROR] Tests run: 3, Failures: 1, Errors: 0, Skipped: 0
[ERROR] 
[ERROR] Failed tests:
[ERROR]   loginCredencialesIncorrectas_DeberiaRetornar401
[ERROR] 
[ERROR] There was 1 failure:
[ERROR] 1) loginCredencialesIncorrectas_DeberiaRetornar401
[ERROR]    Expected status code <400> but was <200>
```

#### Paso 6: Ejecutar desde IntelliJ IDEA

**Método 1: Ejecutar la clase completa**:

1. Abrir el archivo: `src/test/java/com/tuorg/veterinaria/gestionusuarios/controller/AuthControllerIntegrationTest.java`
2. **Clic derecho** en el nombre de la clase `AuthControllerIntegrationTest`
3. Seleccionar **"Run 'AuthControllerIntegrationTest'"**
4. ⚠️ **Asegurarse de que Docker Desktop está ejecutándose**

**Método 2: Ejecutar un método específico**:

1. Abrir el archivo de prueba
2. **Clic derecho** en el método `@Test` (ej: `loginCredencialesCorrectas_DeberiaRetornar200ConToken`)
3. Seleccionar **"Run 'loginCredencialesCorrectas_DeberiaRetornar200ConToken()'"**

**Método 3: Usar el icono ▶️**:

1. Buscar el icono ▶️ verde junto a la clase o método
2. Clic en el icono
3. Seleccionar **"Run"**

**Atajo de teclado**: `Ctrl + Shift + F10` (Windows/Linux)

#### Paso 7: Ver Detalles de la Ejecución

**En IntelliJ IDEA**:

1. Abrir la ventana **"Run"** en la parte inferior
2. Ver los logs de:
   - Inicio del contenedor Docker
   - Inicio de Spring Boot
   - Ejecución de migraciones Flyway
   - Resultados de cada prueba

**En Terminal**:

Los logs se muestran directamente en la consola. Buscar líneas que contengan:
- `Starting PostgreSQL container`
- `Running AuthControllerIntegrationTest`
- `Tests run: X, Failures: Y`

### 5.3.2 Qué Prueban las Pruebas de Integración

**AuthControllerIntegrationTest** prueba:

1. **Login exitoso** (líneas 70-87):
   - ✅ Envía POST a `/api/auth/login` con credenciales correctas
   - ✅ Verifica que retorna código 200
   - ✅ Verifica que el token JWT existe en la respuesta
   - ✅ Verifica que el tipo de token es "Bearer"
   - ✅ Verifica que el username es correcto

2. **Login con credenciales incorrectas** (líneas 89-102):
   - ✅ Envía POST con contraseña incorrecta
   - ✅ Verifica que retorna código 400 (Bad Request)

3. **Login con usuario inexistente** (líneas 104-117):
   - ✅ Envía POST con username que no existe
   - ✅ Verifica que retorna código 400 (Bad Request)

**Flujo completo probado**:
```
Cliente HTTP → Controller → Service → Repository → Base de Datos PostgreSQL
                ↓
            MockMvc verifica respuesta HTTP
```

### 5.3.3 Solución de Problemas Comunes

#### Problema 1: "Docker daemon is not running"

**Error**:
```
Could not find a valid Docker environment
```

**Solución**:
1. Abrir **Docker Desktop**
2. Esperar a que aparezca "Docker Desktop is running"
3. Verificar con: `docker ps`
4. Ejecutar las pruebas nuevamente

#### Problema 2: "Image pull failed"

**Error**:
```
Failed to pull image: postgres:15-alpine
```

**Solución**:
1. Verificar conexión a internet
2. Intentar descargar manualmente: `docker pull postgres:15-alpine`
3. Verificar que Docker tiene espacio suficiente

#### Problema 3: "Port already in use"

**Error**:
```
Port 5432 is already allocated
```

**Solución**:
- Testcontainers asigna puertos automáticamente, este error es raro
- Si ocurre, cerrar otras instancias de PostgreSQL que puedan estar corriendo

#### Problema 4: "Tests timeout"

**Error**:
```
Test timed out after 30 seconds
```

**Solución**:
1. Verificar que Docker tiene recursos suficientes (RAM, CPU)
2. Cerrar otras aplicaciones que usen Docker
3. Aumentar timeout en la configuración si es necesario

#### Problema 5: "Flyway migration failed"

**Error**:
```
Migration failed
```

**Solución**:
1. Verificar que los scripts de migración están en `src/main/resources/db/migration/`
2. Verificar que no hay errores de sintaxis SQL en las migraciones

### 5.3.4 Comandos Útiles para Debugging

```bash
# Ver contenedores Docker activos
docker ps

# Ver logs de un contenedor (si está corriendo)
docker logs <container_id>

# Ver imágenes Docker descargadas
docker images

# Limpiar contenedores detenidos
docker container prune

# Ver todas las pruebas que se ejecutarán
mvn test -Dtest=*IntegrationTest -X
```

### 5.3.5 Crear una Nueva Prueba de Integración

**Ejemplo: Crear prueba de integración para PacienteController**:

1. **Crear el archivo**: `src/test/java/com/tuorg/veterinaria/gestionpacientes/controller/PacienteControllerIntegrationTest.java`

2. **Estructura básica**:

```java
package com.tuorg.veterinaria.gestionpacientes.controller;

import com.tuorg.veterinaria.config.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Pruebas de integración de PacienteController")
class PacienteControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/pacientes: debe retornar lista de pacientes")
    void obtenerPacientes_DeberiaRetornarLista() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
```

3. **Ejecutar la nueva prueba**:
```bash
mvn test -Dtest=PacienteControllerIntegrationTest
```

### 5.4 Cómo Ejecutar las Pruebas

#### Opción 1: Desde la Terminal con Maven

**Ubicación del proyecto**: `C:\Users\Lab Ingenieria 12\Downloads\proyectoVeterinaria`

**Comandos disponibles**:

```bash
# 1. Ejecutar TODAS las pruebas (unitarias + integración)
mvn test

# 2. Ejecutar solo pruebas unitarias (terminan en *Test.java)
mvn test -Dtest=*Test

# 3. Ejecutar solo pruebas de integración (terminan en *IntegrationTest.java)
mvn test -Dtest=*IntegrationTest

# 4. Ejecutar una clase de prueba específica
mvn test -Dtest=AuthServiceTest

# 5. Ejecutar un método de prueba específico
mvn test -Dtest=AuthServiceTest#loginExitoso_DeberiaGenerarTokenYActualizarUltimoAcceso

# 6. Ejecutar pruebas de un módulo específico
mvn test -Dtest=gestionusuarios.service.*

# 7. Compilar y ejecutar pruebas (sin instalar en repositorio local)
mvn clean test

# 8. Ejecutar pruebas y generar reporte de cobertura (si está configurado)
mvn clean test jacoco:report
```

**Ejemplo paso a paso**:

```bash
# 1. Abrir terminal en la carpeta del proyecto
cd "C:\Users\Lab Ingenieria 12\Downloads\proyectoVeterinaria"

# 2. Ejecutar todas las pruebas
mvn test

# 3. Ver resultados en la consola
# Las pruebas que pasan aparecen con ✓
# Las pruebas que fallan aparecen con ✗ y muestran el error
```

#### Opción 2: Desde IntelliJ IDEA

**Método 1: Ejecutar todas las pruebas del proyecto**

1. **Abrir el proyecto** en IntelliJ IDEA
2. **Clic derecho** en la carpeta `src/test/java`
3. Seleccionar **"Run 'All Tests'"** o **"Run Tests in 'com.tuorg.veterinaria'"**
4. Ver resultados en la ventana **"Run"** en la parte inferior

**Método 2: Ejecutar una clase de prueba específica**

1. **Abrir** el archivo de prueba (ej: `AuthServiceTest.java`)
2. **Clic derecho** en el nombre de la clase o en el icono ▶️ junto a la clase
3. Seleccionar **"Run 'AuthServiceTest'"**
4. Ver resultados en la ventana **"Run"**

**Método 3: Ejecutar un método de prueba específico**

1. **Abrir** el archivo de prueba
2. **Clic derecho** en el nombre del método `@Test` o en el icono ▶️ junto al método
3. Seleccionar **"Run 'nombreDelMetodo()'"**
4. Ver resultados en la ventana **"Run"**

**Método 4: Usar el atajo de teclado**

1. **Posicionar el cursor** en la clase o método de prueba
2. Presionar **`Ctrl + Shift + F10`** (Windows/Linux) o **`Ctrl + Shift + R`** (Mac)
3. La prueba se ejecuta automáticamente

**Método 5: Ejecutar desde la barra de herramientas**

1. En la barra superior, buscar el selector de ejecución (dropdown)
2. Seleccionar la clase o método de prueba
3. Clic en el botón **▶️ Run** (verde)

#### Opción 3: Desde la Terminal de IntelliJ IDEA

1. Abrir la **Terminal integrada** de IntelliJ (View → Tool Windows → Terminal)
2. Ejecutar los mismos comandos Maven que en la Opción 1:

```bash
mvn test
```

### 5.5 Requisitos para Ejecutar Pruebas

#### Pruebas Unitarias

**Requisitos**:
- ✅ Java 17 instalado
- ✅ Maven instalado (o usar el Maven wrapper)
- ✅ Dependencias descargadas (`mvn clean install` o `mvn dependency:resolve`)

**No requiere**:
- ❌ Base de datos PostgreSQL
- ❌ Docker
- ❌ Servidor ejecutándose

#### Pruebas de Integración

**Requisitos**:
- ✅ Java 17 instalado
- ✅ Maven instalado
- ✅ **Docker instalado y ejecutándose** (requerido para Testcontainers)
- ✅ Dependencias descargadas

**Verificar Docker**:
```bash
# Verificar que Docker está ejecutándose
docker ps

# Si no está ejecutándose, iniciar Docker Desktop
```

**Nota**: Si Docker no está disponible, las pruebas de integración fallarán. Las pruebas unitarias funcionarán normalmente.

### 5.6 Interpretación de Resultados

#### Resultado Exitoso

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

#### Resultado con Fallos

```
[ERROR] Tests run: 15, Failures: 2, Errors: 1, Skipped: 0
[ERROR] 
[ERROR] Failed tests:
[ERROR]   AuthServiceTest.loginUsuarioNoEncontrado_DeberiaLanzarExcepcion
[ERROR]   PacienteServiceTest.registrarPaciente_ConEspecieInvalida_DeberiaLanzarExcepcion
[ERROR] 
[ERROR] Tests in error:
[ERROR]   AuthControllerIntegrationTest.testLoginEndpoint
```

**Ver detalles del error**: Revisar el stack trace en la consola para ver qué falló.

### 5.7 Estructura Detallada de Archivos de Prueba

**Pruebas Unitarias (9 archivos)**:

| Archivo | Ubicación | Módulo | Métodos de Prueba |
|---------|-----------|--------|-------------------|
| `AuthServiceTest.java` | `gestionusuarios/service/` | Usuarios | 4+ métodos |
| `UsuarioServiceTest.java` | `gestionusuarios/service/` | Usuarios | 3+ métodos |
| `ClienteServiceTest.java` | `gestionusuarios/service/` | Usuarios | 2+ métodos |
| `PacienteServiceTest.java` | `gestionpacientes/service/` | Pacientes | 3+ métodos |
| `VacunacionServiceTest.java` | `gestionpacientes/service/` | Pacientes | 2+ métodos |
| `ProductoServiceTest.java` | `gestioninventario/service/` | Inventario | 3+ métodos |
| `MovimientoInventarioServiceTest.java` | `gestioninventario/service/` | Inventario | 4+ métodos |
| `FacturaServiceTest.java` | `gestionfacturacion/service/` | Facturación | 2+ métodos |
| `CitaServiceTest.java` | `prestacioneservicios/service/` | Servicios | 3+ métodos |

**Pruebas de Integración (2 archivos)**:

| Archivo | Ubicación | Tipo | Descripción |
|---------|-----------|------|-------------|
| `AbstractIntegrationTest.java` | `config/` | Clase base | Configura Testcontainers |
| `AuthControllerIntegrationTest.java` | `gestionusuarios/controller/` | Integración | Prueba endpoints REST de autenticación |

### 5.8 Ejemplos de Ejecución Específicos

#### Ejemplo 1: Ejecutar solo pruebas de autenticación

```bash
# Desde terminal
mvn test -Dtest=AuthServiceTest

# O desde IntelliJ
# Clic derecho en AuthServiceTest.java → Run 'AuthServiceTest'
```

#### Ejemplo 2: Ejecutar todas las pruebas de un módulo

```bash
# Pruebas del módulo de usuarios
mvn test -Dtest=gestionusuarios.service.*

# Pruebas del módulo de pacientes
mvn test -Dtest=gestionpacientes.service.*
```

#### Ejemplo 3: Ejecutar pruebas de integración (requiere Docker)

```bash
# Verificar que Docker está ejecutándose
docker ps

# Ejecutar pruebas de integración
mvn test -Dtest=*IntegrationTest
```

#### Ejemplo 4: Ejecutar pruebas en modo verbose (ver más detalles)

```bash
mvn test -X
```

### 5.9 Solución de Problemas Comunes

#### Problema 1: "Docker daemon is not running"

**Solución**: Iniciar Docker Desktop antes de ejecutar pruebas de integración.

#### Problema 2: "Tests are skipped"

**Causa**: Puede ser que las pruebas estén deshabilitadas o haya un problema de configuración.

**Solución**: Verificar que los archivos tengan la anotación `@Test` correcta.

#### Problema 3: "Cannot find symbol" en pruebas

**Causa**: El proyecto no está compilado correctamente.

**Solución**: 
```bash
mvn clean compile test-compile
```

#### Problema 4: Pruebas de integración fallan por timeout

**Causa**: Docker está lento o el contenedor tarda en iniciar.

**Solución**: Aumentar el timeout en `AbstractIntegrationTest.java` o verificar que Docker tenga recursos suficientes.

### 5.10 Cobertura de Pruebas

**Módulos con pruebas**:
- ✅ Gestión de Usuarios (AuthService, UsuarioService, ClienteService)
- ✅ Gestión de Pacientes (PacienteService, VacunacionService)
- ✅ Gestión de Inventario (ProductoService, MovimientoInventarioService)
- ✅ Gestión de Facturación (FacturaService)
- ✅ Prestación de Servicios (CitaService)
- ✅ Autenticación (Integración: AuthControllerIntegrationTest)

**Total**: 11 archivos de prueba, 30+ métodos de prueba

### 5.11 Configuración de Testcontainers

**Archivo**: `src/test/java/com/tuorg/veterinaria/config/AbstractIntegrationTest.java`

**Configuración**:
- **Imagen Docker**: `postgres:15-alpine`
- **Base de datos**: `veterinaria_test`
- **Usuario**: `test`
- **Contraseña**: `test`
- **Puerto**: Asignado automáticamente por Testcontainers

**Nota**: Testcontainers descarga automáticamente la imagen de PostgreSQL la primera vez que se ejecutan las pruebas de integración.

---

## 6. Patrones de Diseño

### 6.0 Resumen de Patrones Implementados

| # | Patrón | Estado | Ubicación Principal | Características |
|---|--------|--------|---------------------|-----------------|
| 1 | **Singleton** | ✅ | `configuracion/service/ConfigService` | Caché thread-safe, acceso global |
| 2 | **Observer/Event** | ✅ | `common/event/` | Eventos asíncronos para notificaciones y reportes |
| 3 | **Strategy** | ✅ | `notificaciones/model/CanalEnvio` | 3 estrategias: Email, SMS, App |
| 4 | **Command** | ✅ | `gestioninventario/service/MovimientoInventarioService` | Con reversión completa (undo) |
| 5 | **Factory/Builder** | ✅ | `gestionfacturacion/service/FacturaService` | Factory en servicios, Builder en DTOs |
| 6 | **Repository** | ✅ | Todos los repositorios | Spring Data JPA, 25+ repositorios |
| 7 | **Facade** | ✅ | `reportes/service/ReporteService` | Coordina múltiples servicios |
| 8 | **DTO** | ✅ | Todos los módulos (`*/dto/`) | 50+ DTOs Request/Response |
| 9 | **Dependency Injection** | ✅ | Spring Framework | 75+ componentes |
| 10 | **Utility Class** | ✅ | `common/util/ValidationUtil` | Métodos estáticos de validación |
| 11 | **Exception Handler** | ✅ | `common/exception/GlobalExceptionHandler` | Manejo centralizado de errores |
| 12 | **Herencia JOINED** | ✅ | `Persona`, `CanalEnvio` | 2 jerarquías JPA |
| 13 | **Service Layer** | ✅ | Todos los servicios | 25+ servicios con lógica de negocio |

### 6.1 Patrón Repository

**Descripción**: Abstrae el acceso a datos mediante interfaces Spring Data JPA.

**Ubicación**: Todos los módulos (`*/repository/`)

**Archivos específicos (26 repositorios)**:

1. **Gestión de Usuarios** (`gestionusuarios/repository/`):
   - `UsuarioRepository.java` - Líneas 1-20
   - `ClienteRepository.java` - Líneas 1-15
   - `UsuarioVeterinarioRepository.java` - Líneas 1-15
   - `RolRepository.java` - Líneas 1-15
   - `HistorialAccionRepository.java` - Líneas 1-30

2. **Gestión de Pacientes** (`gestionpacientes/repository/`):
   - `PacienteRepository.java` - Líneas 1-25
   - `HistoriaClinicaRepository.java` - Líneas 1-20
   - `RegistroMedicoRepository.java` - Líneas 1-15
   - `VacunacionRepository.java` - Líneas 1-20
   - `DesparasitacionRepository.java` - Líneas 1-15

3. **Gestión de Inventario** (`gestioninventario/repository/`):
   - `ProductoRepository.java` - Líneas 1-25
   - `ProveedorRepository.java` - Líneas 1-20
   - `MovimientoInventarioRepository.java` - Líneas 1-60 (incluye `existsByReferencia` para Command pattern)

4. **Gestión de Facturación** (`gestionfacturacion/repository/`):
   - `FacturaRepository.java` - Líneas 1-20

5. **Prestación de Servicios** (`prestacioneservicios/repository/`):
   - `CitaRepository.java` - Líneas 1-25
   - `ServicioRepository.java` - Líneas 1-15
   - `ServicioPrestadoRepository.java` - Líneas 1-20

6. **Notificaciones** (`notificaciones/repository/`):
   - `CanalEnvioRepository.java` - Líneas 1-15
   - `NotificacionRepository.java` - Líneas 1-20

7. **Reportes** (`reportes/repository/`):
   - `ReporteRepository.java` - Líneas 1-15
   - `EstadisticaRepository.java` - Líneas 1-15
   - `IndicadorRepository.java` - Líneas 1-15

8. **Configuración** (`configuracion/repository/`):
   - `ParametroSistemaRepository.java` - Líneas 1-20
   - `LogSistemaRepository.java` - Líneas 1-20
   - `BackupSistemaRepository.java` - Líneas 1-15

**Ejemplo completo - FacturaRepository**:

```12:20:src/main/java/com/tuorg/veterinaria/gestionfacturacion/repository/FacturaRepository.java
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumero(String numero);
    List<Factura> findByClienteId(Long clienteId);
}
```

**Características**:
- Métodos automáticos: `save()`, `findById()`, `findAll()`, `delete()`
- Query methods: `findByCampo()`, `findByCampo1AndCampo2()`
- Queries personalizadas con `@Query` y JPQL

**Uso en servicios**: Todos los servicios inyectan repositorios mediante `@Autowired` en el constructor

**Total**: 26 repositorios implementando el patrón

### 6.2 Patrón Strategy

**Descripción**: Permite diferentes estrategias de envío de notificaciones sin modificar el código cliente.

**Ubicaciones exactas**:

1. **Interfaz Strategy (Clase Base)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalEnvio.java`
   - **Líneas**: 1-66
   - **Método clave**: `enviar(Notificacion)` - Línea 62

2. **Estrategia Concreta 1 - Email**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalEmail.java`
   - **Líneas**: 1-52
   - **Implementación**: `enviar()` - Líneas 45-51
   - **Tabla BD**: `canales_email`

3. **Estrategia Concreta 2 - SMS**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalSMS.java`
   - **Líneas**: 1-46
   - **Implementación**: `enviar()` - Líneas 39-45
   - **Tabla BD**: `canales_sms`

4. **Estrategia Concreta 3 - App**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalApp.java`
   - **Líneas**: 1-46
   - **Implementación**: `enviar()` - Líneas 39-45
   - **Tabla BD**: `canales_app`

5. **Cliente que usa las estrategias**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/service/NotificacionService.java`
   - **Método**: `enviarAhora()` - Líneas 66-86
   - **Uso del patrón**: Línea 80-81 donde se llama `canal.enviar(notificacion)`

**Ejemplo de código - Clase Base**:

```54:65:src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalEnvio.java
    /**
     * Método para enviar una notificación (Strategy pattern).
     * 
     * Las clases hijas deben sobrescribir este método para proporcionar
     * su propia lógica de envío.
     * 
     * @param notificacion Notificación a enviar
     * @return true si el envío fue exitoso, false en caso contrario
     */
    public boolean enviar(Notificacion notificacion) {
        throw new UnsupportedOperationException("Este método debe ser implementado por las clases hijas");
    }
```

**Ejemplo de código - Estrategia Email**:

```45:51:src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalEmail.java
    @Override
    public boolean enviar(Notificacion notificacion) {
        // Nota: El envío real de email usando JavaMailSender
        // se implementará cuando se requiera la funcionalidad completa de notificaciones
        System.out.println("Enviando email a través de " + smtpServer + ": " + notificacion.getMensaje());
        return true;
    }
```

**Uso en NotificacionService**:

```80:81:src/main/java/com/tuorg/veterinaria/notificaciones/service/NotificacionService.java
        // Delegación al algoritmo específico (Strategy pattern)
        boolean enviado = canal.enviar(notificacion);
```

### 6.3 Patrón Command

**Descripción**: Encapsula operaciones de movimiento de inventario como comandos reversibles.

**Ubicaciones exactas**:

1. **Service (Invoker - Ejecuta comandos)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestioninventario/service/MovimientoInventarioService.java`
   - **Comando Entrada**: `registrarEntrada()` - Líneas 96-126
   - **Comando Salida**: `registrarSalida()` - Líneas 140-169
   - **Reversión (Undo)**: `revertirMovimiento()` - Líneas 211-260

2. **Modelo (Command)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestioninventario/model/MovimientoInventario.java`
   - **Entidad que representa el comando**

3. **Repository (Soporte para reversión)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestioninventario/repository/MovimientoInventarioRepository.java`
   - **Método**: `existsByReferencia(String referencia)` - Línea 60
   - **Propósito**: Prevenir doble reversión

4. **Controller (Endpoint REST)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestioninventario/controller/MovimientoInventarioController.java`
   - **Endpoint Reversión**: `POST /{movimientoId}/revertir` - Líneas 111-118

**Ejemplo de código - Comando de Entrada**:

```96:126:src/main/java/com/tuorg/veterinaria/gestioninventario/service/MovimientoInventarioService.java
    @Transactional
    public MovimientoInventarioResponse registrarEntrada(MovimientoEntradaRequest request) {
        // Validación del comando
        if (request.getCantidad() <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }

        // Obtener el producto (receptor del comando)
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductoId()));

        // Crear el comando (MovimientoInventario)
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(AppConstants.TIPO_MOVIMIENTO_ENTRADA);
        movimiento.setCantidad(request.getCantidad());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setReferencia(request.getReferencia());

        // Configurar proveedor si existe
        if (request.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", request.getProveedorId()));
            movimiento.setProveedor(proveedor);
        }

        // Ejecutar el comando: guardar movimiento y actualizar stock
        MovimientoInventario guardado = movimientoInventarioRepository.save(movimiento);
        Producto actualizado = productoService.actualizarStock(request.getProductoId(), request.getCantidad());

        return mapToResponse(guardado, actualizado.getStock());
    }
```

**Ejemplo de código - Reversión de Comando (Undo)**:

```211:260:src/main/java/com/tuorg/veterinaria/gestioninventario/service/MovimientoInventarioService.java
    @Transactional
    public MovimientoInventarioResponse revertirMovimiento(Long movimientoId, Long usuarioId) {
        MovimientoInventario movimientoOriginal = movimientoInventarioRepository.findById(movimientoId)
                .orElseThrow(() -> new ResourceNotFoundException("MovimientoInventario", "id", movimientoId));

        // Verificar que el movimiento no haya sido revertido previamente
        boolean yaRevertido = movimientoInventarioRepository.existsByReferencia("REVERSION-" + movimientoId);
        if (yaRevertido) {
            throw new BusinessException("Este movimiento ya ha sido revertido");
        }

        // Crear movimiento inverso
        MovimientoInventario movimientoReversion = new MovimientoInventario();
        movimientoReversion.setProducto(movimientoOriginal.getProducto());
        
        // Invertir el tipo de movimiento
        if (AppConstants.TIPO_MOVIMIENTO_ENTRADA.equals(movimientoOriginal.getTipoMovimiento())) {
            movimientoReversion.setTipoMovimiento(AppConstants.TIPO_MOVIMIENTO_SALIDA);
        } else if (AppConstants.TIPO_MOVIMIENTO_SALIDA.equals(movimientoOriginal.getTipoMovimiento())) {
            movimientoReversion.setTipoMovimiento(AppConstants.TIPO_MOVIMIENTO_ENTRADA);
        } else {
            throw new BusinessException("No se puede revertir un movimiento de tipo AJUSTE");
        }
        
        movimientoReversion.setCantidad(movimientoOriginal.getCantidad());
        movimientoReversion.setFecha(LocalDateTime.now());
        movimientoReversion.setReferencia("REVERSION-" + movimientoId);
        movimientoReversion.setProveedor(movimientoOriginal.getProveedor());

        if (usuarioId != null) {
            movimientoReversion.setUsuario(usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId)));
        }

        MovimientoInventario guardado = movimientoInventarioRepository.save(movimientoReversion);
        
        // Actualizar stock (invertir el cambio original)
        Integer cantidadAjuste;
        if (AppConstants.TIPO_MOVIMIENTO_ENTRADA.equals(movimientoOriginal.getTipoMovimiento())) {
            cantidadAjuste = -movimientoOriginal.getCantidad();
        } else {
            cantidadAjuste = movimientoOriginal.getCantidad();
        }
        
        Producto actualizado = productoService.actualizarStock(
                movimientoOriginal.getProducto().getIdProducto(), cantidadAjuste);
        
        return mapToResponse(guardado, actualizado.getStock());
    }
```

**Endpoint REST**:

```111:118:src/main/java/com/tuorg/veterinaria/gestioninventario/controller/MovimientoInventarioController.java
    @PostMapping("/{movimientoId}/revertir")
    public ResponseEntity<ApiResponse<MovimientoInventarioResponse>> revertirMovimiento(
            @PathVariable Long movimientoId,
            @RequestParam Long usuarioId) {
        MovimientoInventarioResponse movimientoReversion = movimientoInventarioService.revertirMovimiento(movimientoId, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movimiento revertido exitosamente", movimientoReversion));
    }
```

**Características**:
- Operaciones transaccionales (`@Transactional`)
- Validaciones antes de ejecutar
- Actualización automática de stock
- Prevención de doble reversión mediante `existsByReferencia()`

### 6.4 Patrón Factory/Builder

**Descripción**: 
- **Factory**: Encapsula la creación compleja de facturas y servicios prestados
- **Builder**: Construcción fluida de DTOs de respuesta con Lombok

#### Factory Pattern

**Ubicaciones exactas**:

1. **Factory Method 1 - FacturaService**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestionfacturacion/service/FacturaService.java`
   - **Método Factory**: `crear(FacturaRequest)` - Líneas 52-83
   - **Método Helper**: `generarNumeroFactura()` - Líneas 85-89
   - **Mapeo**: `mapToResponse(Factura)` - Líneas 157-174

2. **Factory Method 2 - ServicioPrestadoService**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/prestacioneservicios/service/ServicioPrestadoService.java`
   - **Método Factory**: `registrarEjecucion(ServicioPrestadoRequest)` - Líneas 68-117

**Ejemplo completo - Factory en FacturaService**:

```52:89:src/main/java/com/tuorg/veterinaria/gestionfacturacion/service/FacturaService.java
    @Transactional
    public FacturaResponse crear(FacturaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));

        if (!(usuario instanceof Cliente cliente)) {
            throw new BusinessException("El identificador proporcionado no corresponde a un cliente registrado");
        }

        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setTotal(request.getTotal());
        factura.setFormaPago(request.getFormaPago());
        factura.setContenido(asJsonString(request.getContenido()));

        // Generar número único
        String numeroFactura = generarNumeroFactura();
        while (facturaRepository.findByNumero(numeroFactura).isPresent()) {
            numeroFactura = generarNumeroFactura();
        }
        factura.setNumero(numeroFactura);

        if (factura.getTotal() == null || factura.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El total de la factura debe ser mayor o igual a cero");
        }

        factura.setFechaEmision(LocalDateTime.now());
        factura.setEstado(AppConstants.ESTADO_FACTURA_PENDIENTE);

        Factura guardada = facturaRepository.save(factura);
        return mapToResponse(guardada);
    }

    private String generarNumeroFactura() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String secuencia = String.format("%04d", (int) (Math.random() * 10000));
        return "FACT-" + fecha + "-" + secuencia;
    }
```

#### Builder Pattern

**Ubicaciones exactas - DTOs con @Builder (21 archivos)**:

1. **Gestión de Facturación**:
   - `gestionfacturacion/dto/FacturaResponse.java` - Líneas 1-63 (con `@Builder` en línea 15)

2. **Gestión de Pacientes**:
   - `gestionpacientes/dto/PacienteResponse.java` - Líneas 1-50
   - `gestionpacientes/dto/VacunacionResponse.java`
   - `gestionpacientes/dto/DesparasitacionResponse.java`
   - `gestionpacientes/dto/HistoriaClinicaResponse.java`
   - `gestionpacientes/dto/RegistroMedicoResponse.java`

3. **Gestión de Inventario**:
   - `gestioninventario/dto/ProductoResponse.java`
   - `gestioninventario/dto/ProveedorResponse.java`
   - `gestioninventario/dto/MovimientoInventarioResponse.java`

4. **Prestación de Servicios**:
   - `prestacioneservicios/dto/CitaResponse.java`
   - `prestacioneservicios/dto/ServicioPrestadoResponse.java` - Líneas 1-75 (con clases internas Builder)

5. **Notificaciones**:
   - `notificaciones/dto/NotificacionResponse.java`

6. **Reportes**:
   - `reportes/dto/ReporteResponse.java`
   - `reportes/dto/EstadisticaResponse.java`

7. **Gestión de Usuarios**:
   - `gestionusuarios/dto/UsuarioResponse.java`
   - `gestionusuarios/dto/ClienteResponse.java`
   - `gestionusuarios/dto/LoginResponse.java`
   - `gestionusuarios/dto/RolResponse.java`

8. **Common**:
   - `common/dto/ApiResponse.java`

**Ejemplo completo - Builder en FacturaResponse**:

```14:63:src/main/java/com/tuorg/veterinaria/gestionfacturacion/dto/FacturaResponse.java
@Data
@Builder
@Schema(name = "FacturaResponse", description = "Información visible de una factura veterinaria")
public class FacturaResponse {

    @Schema(description = "Identificador de la factura", example = "120")
    private Long idFactura;

    @Schema(description = "Número único de la factura", example = "FACT-20251110-0001")
    private String numero;

    @Schema(description = "Fecha y hora de emisión", example = "2025-11-10T14:25:00")
    private LocalDateTime fechaEmision;

    @Schema(description = "Monto total cobrado", example = "145000.50")
    private BigDecimal total;

    @Schema(description = "Forma de pago utilizada", example = "EFECTIVO")
    private String formaPago;

    @Schema(description = "Estado actual de la factura", example = "PENDIENTE")
    private String estado;

    @Schema(description = "Contenido detallado de la factura")
    private Map<String, Object> contenido;

    @Schema(description = "Resumen del cliente al que se factura")
    private ClienteSummary cliente;

    @Data
    @Builder
    @Schema(name = "FacturaClienteSummary", description = "Datos básicos del cliente")
    public static class ClienteSummary {
        private Long id;
        private String nombreCompleto;
        private String correo;
        private String telefono;
    }
}
```

**Uso del Builder en el servicio**:

```157:174:src/main/java/com/tuorg/veterinaria/gestionfacturacion/service/FacturaService.java
    private FacturaResponse mapToResponse(Factura factura) {
        Cliente cliente = factura.getCliente();
        return FacturaResponse.builder()
                .idFactura(factura.getIdFactura())
                .numero(factura.getNumero())
                .fechaEmision(factura.getFechaEmision())
                .total(factura.getTotal())
                .formaPago(factura.getFormaPago())
                .estado(factura.getEstado())
                .contenido(asMap(factura.getContenido()))
                .cliente(cliente != null ? FacturaResponse.ClienteSummary.builder()
                        .id(cliente.getIdUsuario())
                        .nombreCompleto(cliente.getNombre() + " " + cliente.getApellido())
                        .correo(cliente.getCorreo())
                        .telefono(cliente.getTelefono())
                        .build() : null)
                .build();
    }
```

**Total**: 2 Factory methods, 21 DTOs con Builder

### 6.5 Patrón Facade

**Descripción**: Proporciona una interfaz simplificada para la generación de reportes, ocultando la complejidad de múltiples servicios.

**Ubicaciones exactas**:

1. **Facade (Interfaz simplificada)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/reportes/service/ReporteService.java`
   - **Método principal**: `generar(ReporteRequest)` - Líneas 50-69
   - **Métodos adicionales**: `exportarPDF()` - Líneas 66-73, `exportarExcel()` - Líneas 78-85

2. **Subsistema 1 - EstadisticaService**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/reportes/service/EstadisticaService.java`
   - **Método usado por Facade**: `calcularEstadisticasParaReporte()` - Líneas 71-77

3. **Subsistema 2 - IndicadorService**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/reportes/service/IndicadorService.java`
   - **Método**: `evaluarTendencia()` - Líneas 45-53

4. **Subsistema 3 - ReporteRepository**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/reportes/repository/ReporteRepository.java`
   - **Persistencia de reportes**

5. **Cliente (Controller)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/reportes/controller/ReporteController.java`
   - **Endpoint**: `POST /reportes` - Líneas 42-50

**Ejemplo completo - Facade**:

```50:69:src/main/java/com/tuorg/veterinaria/reportes/service/ReporteService.java
    @Transactional
    public ReporteResponse generar(ReporteRequest request) {
        Reporte reporte = new Reporte();
        reporte.setNombre(request.getNombre());
        reporte.setTipo(request.getTipo());
        reporte.setGeneradoPor(request.getGeneradoPor());
        reporte.setFechaGeneracion(LocalDateTime.now());
        reporte.setParametros(toJson(request.getParametros()));

        List<Estadistica> estadisticas = estadisticaService.calcularEstadisticasParaReporte(
                request.getTipo(),
                request.getParametros() != null ? request.getParametros() : Collections.emptyMap()
        );

        Reporte guardado = reporteRepository.save(reporte);
        
        // Publicar evento (Observer pattern)
        eventPublisher.publishEvent(new ReporteGeneradoEvent(this, guardado, request.getTipo()));
        
        return mapToResponse(guardado, estadisticas);
    }
```

**Inyección de dependencias (subsistemas)**:

```33:45:src/main/java/com/tuorg/veterinaria/reportes/service/ReporteService.java
    @Autowired
    public ReporteService(ReporteRepository reporteRepository,
                          EstadisticaService estadisticaService,
                          ObjectMapper objectMapper,
                          ApplicationEventPublisher eventPublisher) {
        this.reporteRepository = reporteRepository;
        this.estadisticaService = estadisticaService;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }
```

**Subsistemas coordinados**:
- `EstadisticaService` - Cálculo de estadísticas (línea 59)
- `IndicadorService` - Evaluación de indicadores (disponible para uso futuro)
- `ReporteRepository` - Persistencia (línea 64)
- `ObjectMapper` - Serialización JSON (línea 57)
- `ApplicationEventPublisher` - Publicación de eventos (línea 67)

### 6.6 Patrón Singleton

**Descripción**: Garantiza una única instancia de configuración global con caché thread-safe.

**Ubicación exacta**:
- **Archivo**: `src/main/java/com/tuorg/veterinaria/configuracion/service/ConfigService.java`
- **Líneas totales**: 1-164

**Implementación específica**:

1. **Variable estática (Instancia única)**:
   - **Línea**: 38
   - **Código**: `private static ConfigService instance;`

2. **Caché thread-safe**:
   - **Línea**: 44
   - **Código**: `private final Map<String, String> cache = new ConcurrentHashMap<>();`

3. **Constructor (Asignación de instancia)**:
   - **Líneas**: 56-62
   - **Código clave**: `instance = this;` (línea 59)

4. **Método getInstance() (Acceso global)**:
   - **Líneas**: 69-74

5. **Método obtener() (Cache-Aside pattern)**:
   - **Líneas**: 86-101
   - **Lógica**: Busca en caché primero, si no existe consulta BD y actualiza caché

6. **Método actualizarValor() (Actualización de caché)**:
   - **Líneas**: 112-123

7. **Método cargarTodos() (Carga inicial)**:
   - **Líneas**: 131-139

**Ejemplo completo - Singleton**:

```29:74:src/main/java/com/tuorg/veterinaria/configuracion/service/ConfigService.java
@Service
public class ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    /**
     * Instancia única del servicio (Singleton).
     * En Spring, esto se maneja automáticamente con @Service.
     */
    private static ConfigService instance;

    /**
     * Caché local thread-safe para almacenar parámetros en memoria.
     * Utiliza ConcurrentHashMap para garantizar thread-safety.
     */
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    /**
     * Repositorio de parámetros del sistema.
     */
    private final ParametroSistemaRepository parametroSistemaRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param parametroSistemaRepository Repositorio de parámetros
     */
    @Autowired
    public ConfigService(ParametroSistemaRepository parametroSistemaRepository) {
        this.parametroSistemaRepository = parametroSistemaRepository;
        instance = this;
        // Cargar todos los parámetros al inicializar
        cargarTodos();
    }

    /**
     * Obtiene la instancia única del servicio (Singleton).
     * 
     * @return Instancia del ConfigService
     */
    public static ConfigService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConfigService no ha sido inicializado");
        }
        return instance;
    }
```

**Método obtener() con caché**:

```86:101:src/main/java/com/tuorg/veterinaria/configuracion/service/ConfigService.java
    @Transactional(readOnly = true)
    public String obtener(String clave) {
        // Buscar en caché primero
        String valor = cache.get(clave);
        if (valor != null) {
            return valor;
        }

        // Si no está en caché, buscar en base de datos
        ParametroSistema parametro = parametroSistemaRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("ParametroSistema", "clave", clave));

        // Actualizar caché
        cache.put(clave, parametro.getValor());
        return parametro.getValor();
    }
```

**Características**:
- Instancia única con `getInstance()` (línea 69)
- Caché en memoria (`ConcurrentHashMap`) (línea 44)
- Carga inicial de parámetros (línea 61)
- Actualización automática de caché (línea 99)

**Nota**: También existe Singleton implícito en Spring - todos los beans con `@Service`, `@Repository`, `@Controller` son singletons por defecto (75+ componentes).

### 6.7 Patrón Observer/Event

**Descripción**: Sistema de eventos usando `ApplicationEventPublisher` de Spring para publicar y escuchar eventos del sistema.

**Ubicaciones exactas**:

#### Eventos Personalizados

1. **NotificacionEvent**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/common/event/NotificacionEvent.java`
   - **Líneas**: 1-40
   - **Extiende**: `ApplicationEvent` (línea 15)
   - **Constructor**: Líneas 27-31
   - **Campos**: `notificacion` (línea 17), `tipoEvento` (línea 18)

2. **ReporteGeneradoEvent**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/common/event/ReporteGeneradoEvent.java`
   - **Líneas**: 1-40
   - **Extiende**: `ApplicationEvent` (línea 15)
   - **Constructor**: Líneas 27-31
   - **Campos**: `reporte` (línea 17), `tipoReporte` (línea 18)

#### Listeners (Observers)

3. **NotificacionEventListener**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/common/event/NotificacionEventListener.java`
   - **Líneas**: 1-58
   - **Anotación**: `@Component` (línea 20)
   - **Método listener**: `handleNotificacionEnviada()` - Líneas 37-57
   - **Anotaciones**: `@EventListener` (línea 37), `@Async` (línea 38)

4. **ReporteEventListener**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/common/event/ReporteEventListener.java`
   - **Líneas**: 1-60
   - **Anotación**: `@Component` (línea 20)
   - **Método listener**: `handleReporteGenerado()` - Líneas 37-59
   - **Anotaciones**: `@EventListener` (línea 37), `@Async` (línea 38)

#### Publicadores (Subjects)

5. **NotificacionService (Publica eventos)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/service/NotificacionService.java`
   - **Inyección**: `ApplicationEventPublisher eventPublisher` - Línea 39
   - **Publicación 1**: `programarEnvio()` - Línea 67
   - **Publicación 2**: `enviarAhora()` - Línea 97

6. **ReporteService (Publica eventos)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/reportes/service/ReporteService.java`
   - **Inyección**: `ApplicationEventPublisher eventPublisher` - Línea 33
   - **Publicación**: `generar()` - Línea 67

#### Configuración

7. **AsyncConfig (Habilita @Async)**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/config/AsyncConfig.java`
   - **Líneas**: 1-18
   - **Anotación**: `@EnableAsync` (línea 15)

**Ejemplo completo - Evento**:

```15:40:src/main/java/com/tuorg/veterinaria/common/event/NotificacionEvent.java
public class NotificacionEvent extends ApplicationEvent {

    private final Notificacion notificacion;
    private final String tipoEvento; // "ENVIADA", "PROGRAMADA", "FALLIDA"

    /**
     * Constructor del evento.
     * 
     * @param source Fuente del evento (normalmente el servicio que lo publica)
     * @param notificacion Notificación relacionada con el evento
     * @param tipoEvento Tipo de evento (ENVIADA, PROGRAMADA, FALLIDA)
     */
    public NotificacionEvent(Object source, Notificacion notificacion, String tipoEvento) {
        super(source);
        this.notificacion = notificacion;
        this.tipoEvento = tipoEvento;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }
}
```

**Ejemplo completo - Listener**:

```37:57:src/main/java/com/tuorg/veterinaria/common/event/NotificacionEventListener.java
    @EventListener
    @Async
    public void handleNotificacionEnviada(NotificacionEvent event) {
        logger.info("Evento de notificación recibido: {} - ID: {}", 
                event.getTipoEvento(), event.getNotificacion().getIdNotificacion());
        
        // Registrar en log del sistema
        logSistemaService.registrarEvento(
                "NotificacionService",
                "INFO",
                String.format("Notificación %s: %s (ID: %d)",
                        event.getTipoEvento(),
                        event.getNotificacion().getMensaje(),
                        event.getNotificacion().getIdNotificacion())
        );
        
        // Aquí se pueden agregar más acciones automáticas:
        // - Enviar notificaciones a administradores
        // - Actualizar métricas
        // - Trigger de alertas si hay muchas fallidas
    }
```

**Ejemplo completo - Publicación en NotificacionService**:

```97:97:src/main/java/com/tuorg/veterinaria/notificaciones/service/NotificacionService.java
        eventPublisher.publishEvent(new NotificacionEvent(this, guardada, tipoEvento));
```

**Ejemplo completo - Publicación en ReporteService**:

```67:67:src/main/java/com/tuorg/veterinaria/reportes/service/ReporteService.java
        eventPublisher.publishEvent(new ReporteGeneradoEvent(this, guardado, request.getTipo()));
```

**Configuración AsyncConfig**:

```14:18:src/main/java/com/tuorg/veterinaria/config/AsyncConfig.java
@Configuration
@EnableAsync
public class AsyncConfig {
    // Configuración por defecto de Spring para @Async
}
```

**Características**:
- Eventos asíncronos (`@Async`) para no bloquear el hilo principal
- Logging automático de eventos en `LogSistemaService`
- Extensible: fácil agregar más listeners sin modificar código existente

### 6.8 Patrón DTO

**Descripción**: Separa la capa de presentación de la capa de dominio.

**Ubicaciones exactas - Request DTOs**:

1. **Gestión de Usuarios** (`gestionusuarios/dto/`):
   - `LoginRequest.java` - Líneas 1-20
   - `RegisterRequest.java` - Líneas 1-30 (con validación `@Size(min = 8)` en password)
   - `UsuarioRequest.java` - Líneas 1-25
   - `ClienteRequest.java` - Líneas 1-30

2. **Gestión de Pacientes** (`gestionpacientes/dto/`):
   - `PacienteRequest.java` - Líneas 1-47 (con validaciones `@NotBlank`, `@NotNull`, `@Positive`)
   - `VacunacionRequest.java` - Líneas 1-30
   - `DesparasitacionRequest.java` - Líneas 1-25
   - `RegistroMedicoRequest.java` - Líneas 1-35

3. **Gestión de Inventario** (`gestioninventario/dto/`):
   - `ProductoRequest.java` - Líneas 1-40
   - `ProveedorRequest.java` - Líneas 1-30
   - `MovimientoEntradaRequest.java` - Líneas 1-25
   - `MovimientoSalidaRequest.java` - Líneas 1-20

4. **Gestión de Facturación** (`gestionfacturacion/dto/`):
   - `FacturaRequest.java` - Líneas 1-30
   - `FacturaPagoRequest.java` - Líneas 1-20

5. **Prestación de Servicios** (`prestacioneservicios/dto/`):
   - `CitaRequest.java` - Líneas 1-40
   - `ServicioPrestadoRequest.java` - Líneas 1-35

6. **Notificaciones** (`notificaciones/dto/`):
   - `NotificacionProgramarRequest.java` - Líneas 1-25
   - `NotificacionEnviarRequest.java` - Líneas 1-20

7. **Reportes** (`reportes/dto/`):
   - `ReporteRequest.java` - Líneas 1-30

**Ubicaciones exactas - Response DTOs (21 archivos)**:

1. `gestionfacturacion/dto/FacturaResponse.java` - Líneas 1-63
2. `gestionpacientes/dto/PacienteResponse.java` - Líneas 1-50
3. `gestionpacientes/dto/VacunacionResponse.java`
4. `gestionpacientes/dto/DesparasitacionResponse.java`
5. `gestionpacientes/dto/HistoriaClinicaResponse.java`
6. `gestionpacientes/dto/RegistroMedicoResponse.java`
7. `gestioninventario/dto/ProductoResponse.java`
8. `gestioninventario/dto/ProveedorResponse.java`
9. `gestioninventario/dto/MovimientoInventarioResponse.java`
10. `prestacioneservicios/dto/CitaResponse.java`
11. `prestacioneservicios/dto/ServicioPrestadoResponse.java` - Líneas 1-75
12. `notificaciones/dto/NotificacionResponse.java`
13. `reportes/dto/ReporteResponse.java`
14. `reportes/dto/EstadisticaResponse.java`
15. `gestionusuarios/dto/UsuarioResponse.java`
16. `gestionusuarios/dto/ClienteResponse.java`
17. `gestionusuarios/dto/LoginResponse.java`
18. `gestionusuarios/dto/RolResponse.java`
19. `gestionusuarios/dto/PermisoResponse.java`
20. `gestionpacientes/dto/PacienteOwnerResponse.java`
21. `common/dto/ApiResponse.java` - Respuesta estándar de la API

**Ejemplo completo - Request DTO**:

```14:47:src/main/java/com/tuorg/veterinaria/gestionpacientes/dto/PacienteRequest.java
@Data
@Schema(description = "Request para el registro de un paciente")
public class PacienteRequest {

    @NotBlank(message = "El nombre del paciente es obligatorio")
    @Schema(description = "Nombre del paciente", example = "Max")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    @Schema(description = "Especie del paciente", allowableValues = {"perro", "gato"}, example = "perro")
    private String especie;

    @Schema(description = "Raza del paciente", example = "Beagle")
    private String raza;

    @Schema(description = "Fecha de nacimiento", example = "2023-02-15")
    private LocalDate fechaNacimiento;

    @Schema(description = "Sexo del paciente", example = "Macho")
    private String sexo;

    @Positive(message = "El peso debe ser mayor a cero")
    @Schema(description = "Peso en kilogramos", example = "12.4")
    private BigDecimal pesoKg;

    @Schema(description = "Estado de salud", example = "Estable")
    private String estadoSalud;

    @NotNull(message = "Debe indicar el identificador del cliente")
    @Schema(description = "Identificador del cliente dueño", example = "4")
    private Long clienteId;

    @Schema(description = "Identificador externo opcional", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID identificadorExterno;
}
```

**Ejemplo completo - Response DTO**:

```12:50:src/main/java/com/tuorg/veterinaria/gestionpacientes/dto/PacienteResponse.java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con la información de un paciente")
public class PacienteResponse {

    @Schema(description = "Identificador del paciente", example = "1")
    private Long id;

    @Schema(description = "Nombre del paciente", example = "Firulais")
    private String nombre;

    @Schema(description = "Especie", example = "perro")
    private String especie;

    @Schema(description = "Raza", example = "Labrador")
    private String raza;

    @Schema(description = "Fecha de nacimiento", example = "2020-05-15")
    private LocalDate fechaNacimiento;

    @Schema(description = "Sexo", example = "Macho")
    private String sexo;

    @Schema(description = "Peso en kilogramos", example = "25.6")
    private BigDecimal pesoKg;

    @Schema(description = "Estado de salud", example = "Estable")
    private String estadoSalud;

    @Schema(description = "Cliente dueño")
    private PacienteOwnerResponse cliente;

    @Schema(description = "Identificador externo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID identificadorExterno;
}
```

**Mapeo DTO ↔ Entidad en servicios**: Todos los servicios tienen métodos `mapToEntity()` y `mapToResponse()`

**Total**: 30+ Request DTOs, 21 Response DTOs = 50+ DTOs totales

### 6.9 Patrón Dependency Injection

**Descripción**: Spring inyecta dependencias automáticamente mediante constructores.

**Aplicación**: Toda la aplicación

**Ubicaciones exactas - Componentes con @Autowired (75+ archivos)**:

#### Services (25+ archivos)
- `gestionusuarios/service/AuthService.java` - Líneas 50-60 (5 dependencias)
- `gestionusuarios/service/UsuarioService.java` - Líneas 40-50 (3 dependencias)
- `gestionusuarios/service/ClienteService.java` - Líneas 30-40 (2 dependencias)
- `gestionpacientes/service/PacienteService.java` - Líneas 30-40 (3 dependencias)
- `gestionpacientes/service/VacunacionService.java` - Líneas 30-40 (2 dependencias)
- `gestionpacientes/service/DesparasitacionService.java` - Líneas 30-40 (2 dependencias)
- `gestionpacientes/service/HistoriaClinicaService.java` - Líneas 40-50 (2 dependencias)
- `gestioninventario/service/ProductoService.java` - Líneas 30-40 (2 dependencias)
- `gestioninventario/service/ProveedorService.java` - Líneas 30-40 (2 dependencias)
- `gestioninventario/service/MovimientoInventarioService.java` - Líneas 60-75 (5 dependencias)
- `gestionfacturacion/service/FacturaService.java` - Líneas 30-40 (2 dependencias)
- `prestacioneservicios/service/CitaService.java` - Líneas 40-50 (3 dependencias)
- `prestacioneservicios/service/ServicioPrestadoService.java` - Líneas 40-50 (4 dependencias)
- `notificaciones/service/NotificacionService.java` - Líneas 30-40 (3 dependencias)
- `reportes/service/ReporteService.java` - Líneas 33-45 (4 dependencias)
- `reportes/service/EstadisticaService.java` - Líneas 30-40 (1 dependencia)
- `reportes/service/IndicadorService.java` - Líneas 33-36 (1 dependencia)
- `configuracion/service/ConfigService.java` - Líneas 56-62 (1 dependencia)
- `configuracion/service/LogSistemaService.java` - Líneas 30-40 (1 dependencia)
- Y más...

#### Controllers (15+ archivos)
- `gestionusuarios/controller/AuthController.java` - Líneas 25-30
- `gestionusuarios/controller/UsuarioController.java` - Líneas 25-30
- `gestionpacientes/controller/PacienteController.java` - Líneas 25-30
- `gestioninventario/controller/ProductoController.java` - Líneas 25-30
- Y más...

#### Repositories (26 archivos)
- Todos los repositorios son interfaces, Spring Data JPA crea las implementaciones automáticamente

#### Components (5+ archivos)
- `common/event/NotificacionEventListener.java` - Líneas 27-30
- `common/event/ReporteEventListener.java` - Líneas 27-30
- Y más...

**Ejemplo completo - Inyección por Constructor**:

```30:40:src/main/java/com/tuorg/veterinaria/gestionpacientes/service/PacienteService.java
    @Autowired
    public PacienteService(
            PacienteRepository pacienteRepository,
            HistoriaClinicaRepository historiaClinicaRepository,
            ClienteRepository clienteRepository) {
        this.pacienteRepository = pacienteRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.clienteRepository = clienteRepository;
    }
```

**Ejemplo - Inyección en Controller**:

```25:30:src/main/java/com/tuorg/veterinaria/gestionpacientes/controller/PacienteController.java
    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }
```

**Total**: 75+ componentes usando Dependency Injection

### 6.10 Utility Class Pattern

**Descripción**: Clase con métodos estáticos y constructor privado para prevenir instanciación.

**Ubicación exacta**:
- **Archivo**: `src/main/java/com/tuorg/veterinaria/common/util/ValidationUtil.java`
- **Líneas totales**: 1-154

**Implementación específica**:

1. **Constructor privado**:
   - **Líneas**: 34-36
   - **Propósito**: Prevenir instanciación

2. **Patrones regex compilados**:
   - **EMAIL_PATTERN**: Líneas 22-23
   - **PHONE_PATTERN**: Líneas 28-29

3. **Métodos de validación**:
   - `isValidEmail(String)` - Líneas 44-49
   - `validateEmail(String)` - Líneas 57-62
   - `isValidPhone(String)` - Líneas 70-75
   - `validatePhone(String)` - Líneas 83-88
   - `validateUsername(String)` - Líneas 96-110
   - `validatePassword(String)` - Líneas 118-125
   - `validatePositiveNumber(double, String)` - Líneas 133-140
   - `validateNonNegativeNumber(double, String)` - Líneas 148-155

**Ejemplo completo**:

```17:62:src/main/java/com/tuorg/veterinaria/common/util/ValidationUtil.java
public final class ValidationUtil {

    /**
     * Patrón para validar formato de correo electrónico.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Patrón para validar formato de teléfono (solo números, guiones y espacios).
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9\\s\\-+()]+$");

    /**
     * Constructor privado para prevenir instanciación.
     */
    private ValidationUtil() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades y no debe instanciarse");
    }

    /**
     * Valida el formato de un correo electrónico.
     * 
     * @param email Correo electrónico a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida el formato de un correo electrónico y lanza excepción si es inválido.
     * 
     * @param email Correo electrónico a validar
     * @throws BusinessException Si el formato del correo es inválido
     */
    public static void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new BusinessException("El formato del correo electrónico no es válido");
        }
    }
```

**Uso en el sistema**:
- `AuthService.register()` - Línea 161: `ValidationUtil.validatePassword(request.getPassword());`
- `ClienteService.crear()` - Validación de email
- Otros servicios que requieren validaciones

**Características**:
- Clase final (no puede ser extendida)
- Constructor privado que lanza excepción
- Métodos estáticos puros (sin estado)
- Patrones regex compilados una vez (mejor rendimiento)

### 6.11 Exception Handler Pattern

**Descripción**: Manejo centralizado de excepciones que transforma excepciones en respuestas HTTP estandarizadas.

**Ubicación exacta**:
- **Archivo**: `src/main/java/com/tuorg/veterinaria/common/exception/GlobalExceptionHandler.java`
- **Líneas totales**: 1-92

**Implementación específica**:

1. **Anotación de clase**:
   - **Línea**: 25
   - **Código**: `@RestControllerAdvice`

2. **Handler para ResourceNotFoundException**:
   - **Líneas**: 36-41
   - **Código HTTP**: 404 NOT FOUND

3. **Handler para BusinessException**:
   - **Líneas**: 49-54
   - **Código HTTP**: 400 BAD REQUEST

4. **Handler para validaciones**:
   - **Líneas**: 62-77
   - **Código HTTP**: 400 BAD REQUEST
   - **Retorna**: Map con errores por campo

5. **Handler genérico**:
   - **Líneas**: 85-91
   - **Código HTTP**: 500 INTERNAL SERVER ERROR

**Ejemplo completo**:

```25:92:src/main/java/com/tuorg/veterinaria/common/exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de recursos no encontrados.
     * 
     * @param ex Excepción de recurso no encontrado
     * @return Respuesta HTTP 404 con mensaje de error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Recurso no encontrado: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Maneja excepciones de negocio.
     * 
     * @param ex Excepción de negocio
     * @return Respuesta HTTP 400 con mensaje de error
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        logger.warn("Error de negocio: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones de validación de argumentos.
     * 
     * @param ex Excepción de validación
     * @return Respuesta HTTP 400 con detalles de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        logger.warn("Error de validación: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.error("Error de validación");
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones genéricas no previstas.
     * 
     * @param ex Excepción genérica
     * @return Respuesta HTTP 500 con mensaje de error genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Error inesperado: ", ex);
        ApiResponse<Object> response = ApiResponse.error(
            "Ocurrió un error inesperado. Por favor, contacte al administrador.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

**Excepciones personalizadas**:
- `common/exception/ResourceNotFoundException.java` - Líneas 1-70
- `common/exception/BusinessException.java` - Líneas 1-70

**Mapeo de excepciones**:
- `ResourceNotFoundException` → 404 NOT FOUND (línea 36)
- `BusinessException` → 400 BAD REQUEST (línea 49)
- `MethodArgumentNotValidException` → 400 BAD REQUEST (línea 62)
- `Exception` → 500 INTERNAL SERVER ERROR (línea 85)

### 6.12 Herencia JOINED (JPA)

**Descripción**: Mapea jerarquías de herencia a tablas separadas en la base de datos.

#### Jerarquía 1 - Personas

**Ubicaciones exactas**:

1. **Clase Base - Persona**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestionusuarios/model/Persona.java`
   - **Líneas**: 1-69
   - **Anotación**: `@Inheritance(strategy = InheritanceType.JOINED)` - Línea 23
   - **Tabla BD**: `personas`

2. **Clase Intermedia - Usuario**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestionusuarios/model/Usuario.java`
   - **Líneas**: 1-92
   - **Anotación**: `@PrimaryKeyJoinColumn(name = "id_usuario")` - Línea 22
   - **Tabla BD**: `usuarios`

3. **Clase Hija 1 - Cliente**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestionusuarios/model/Cliente.java`
   - **Líneas**: 1-43
   - **Anotación**: `@PrimaryKeyJoinColumn(name = "id_usuario")` - Línea 22
   - **Tabla BD**: `clientes`

4. **Clase Hija 2 - UsuarioVeterinario**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestionusuarios/model/UsuarioVeterinario.java`
   - **Anotación**: `@PrimaryKeyJoinColumn(name = "id_usuario")`
   - **Tabla BD**: `usuarios_veterinarios`

5. **Clase Hija 3 - Secretario**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/gestionusuarios/model/Secretario.java`
   - **Anotación**: `@PrimaryKeyJoinColumn(name = "id_usuario")`
   - **Tabla BD**: `secretarios`

**Ejemplo completo - Clase Base Persona**:

```21:69:src/main/java/com/tuorg/veterinaria/gestionusuarios/model/Persona.java
@Entity
@Table(name = "personas", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;
}
```

#### Jerarquía 2 - Canales

**Ubicaciones exactas**:

1. **Clase Base - CanalEnvio**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalEnvio.java`
   - **Líneas**: 1-66
   - **Anotación**: `@Inheritance(strategy = InheritanceType.JOINED)` - Línea 24
   - **Tabla BD**: `canales_envio`

2. **Clase Hija 1 - CanalEmail**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalEmail.java`
   - **Líneas**: 1-52
   - **Anotación**: `@PrimaryKeyJoinColumn(name = "id_canal")` - Línea 20
   - **Tabla BD**: `canales_email`

3. **Clase Hija 2 - CanalSMS**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalSMS.java`
   - **Líneas**: 1-46
   - **Anotación**: `@PrimaryKeyJoinColumn(name = "id_canal")` - Línea 20
   - **Tabla BD**: `canales_sms`

4. **Clase Hija 3 - CanalApp**:
   - **Archivo**: `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalApp.java`
   - **Líneas**: 1-46
   - **Anotación**: `@PrimaryKeyJoinColumn(name = "id_canal")` - Línea 20
   - **Tabla BD**: `canales_app`

**Estructura de tablas en BD**:
- `personas` (tabla base) → `usuarios` → `clientes` / `usuarios_veterinarios` / `secretarios`
- `canales_envio` (tabla base) → `canales_email` / `canales_sms` / `canales_app`

**Total**: 2 jerarquías, 8 clases usando herencia JOINED

### 6.13 Patrón Service Layer

**Descripción**: Encapsula lógica de negocio y coordina operaciones entre repositorios.

**Ubicaciones exactas - Servicios (25+ archivos)**:

1. **Gestión de Usuarios** (`gestionusuarios/service/`):
   - `AuthService.java` - Líneas 1-260 (autenticación, registro, JWT)
   - `UsuarioService.java` - Líneas 1-200 (gestión de usuarios)
   - `ClienteService.java` - Líneas 1-150 (gestión de clientes)
   - `CustomUserDetailsService.java` - Líneas 1-80 (Spring Security)

2. **Gestión de Pacientes** (`gestionpacientes/service/`):
   - `PacienteService.java` - Líneas 1-200 (gestión de pacientes)
   - `VacunacionService.java` - Líneas 1-150 (gestión de vacunaciones)
   - `DesparasitacionService.java` - Líneas 1-120 (gestión de desparasitaciones)
   - `HistoriaClinicaService.java` - Líneas 1-250 (gestión de historias clínicas, generación PDF)

3. **Gestión de Inventario** (`gestioninventario/service/`):
   - `ProductoService.java` - Líneas 1-200 (gestión de productos)
   - `ProveedorService.java` - Líneas 1-150 (gestión de proveedores)
   - `MovimientoInventarioService.java` - Líneas 1-260 (movimientos, Command pattern)
   - `AlertaInventarioService.java` - Líneas 1-100 (alertas de stock)

4. **Gestión de Facturación** (`gestionfacturacion/service/`):
   - `FacturaService.java` - Líneas 1-200 (Factory pattern, gestión de facturas)

5. **Prestación de Servicios** (`prestacioneservicios/service/`):
   - `CitaService.java` - Líneas 1-200 (gestión de citas)
   - `ServicioPrestadoService.java` - Líneas 1-150 (Factory pattern, servicios prestados)

6. **Notificaciones** (`notificaciones/service/`):
   - `NotificacionService.java` - Líneas 1-120 (Observer pattern, Strategy pattern)

7. **Reportes** (`reportes/service/`):
   - `ReporteService.java` - Líneas 1-130 (Facade pattern, Observer pattern)
   - `EstadisticaService.java` - Líneas 1-104 (cálculo de estadísticas)
   - `IndicadorService.java` - Líneas 1-64 (evaluación de indicadores)

8. **Configuración** (`configuracion/service/`):
   - `ConfigService.java` - Líneas 1-164 (Singleton pattern)
   - `LogSistemaService.java` - Líneas 1-107 (gestión de logs)

**Ejemplo completo - Service Layer**:

```30:80:src/main/java/com/tuorg/veterinaria/gestionpacientes/service/PacienteService.java
    @Autowired
    public PacienteService(
            PacienteRepository pacienteRepository,
            HistoriaClinicaRepository historiaClinicaRepository,
            ClienteRepository clienteRepository) {
        this.pacienteRepository = pacienteRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Registra un nuevo paciente (método de negocio completo).
     */
    @Transactional
    public PacienteResponse registrarPaciente(PacienteRequest request) {
        // 1. Validaciones de negocio
        if (request.getFechaNacimiento() != null && 
            request.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha de nacimiento no puede ser futura");
        }

        // 2. Obtener entidades relacionadas (coordinación)
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));

        // 3. Crear entidad (mapeo DTO → Entidad)
        Paciente paciente = mapToEntity(request, cliente);
        
        // 4. Persistir
        Paciente guardado = pacienteRepository.save(paciente);

        // 5. Operación relacionada: crear historia clínica
        HistoriaClinica historiaClinica = new HistoriaClinica();
        historiaClinica.setPaciente(guardado);
        historiaClinica.setFechaCreacion(LocalDateTime.now());
        historiaClinicaRepository.save(historiaClinica);

        // 6. Mapeo Entidad → DTO
        return mapToResponse(guardado);
    }
```

**Características**:
- Métodos transaccionales (`@Transactional`) en operaciones de escritura
- `@Transactional(readOnly = true)` en operaciones de lectura
- Validaciones de negocio antes de persistir
- Mapeo DTO ↔ Entidad en métodos privados
- Coordinación entre múltiples repositorios

**Total**: 25+ servicios implementando Service Layer

---

### 6.14 Resumen de Ubicaciones Exactas de Patrones

#### Tabla Completa de Ubicaciones por Patrón

| Patrón | Archivo Principal | Líneas Clave | Archivos Relacionados |
|--------|-------------------|--------------|----------------------|
| **Repository** | `gestionfacturacion/repository/FacturaRepository.java` | 1-20 | 26 repositorios en total |
| **Strategy** | `notificaciones/model/CanalEnvio.java` | 54-65 | `CanalEmail.java` (45-51), `CanalSMS.java` (39-45), `CanalApp.java` (39-45) |
| **Command** | `gestioninventario/service/MovimientoInventarioService.java` | 211-260 | `MovimientoInventarioRepository.java` (60), `MovimientoInventarioController.java` (111-118) |
| **Factory** | `gestionfacturacion/service/FacturaService.java` | 52-89 | `ServicioPrestadoService.java` (68-117) |
| **Builder** | `gestionfacturacion/dto/FacturaResponse.java` | 15 | 21 DTOs Response con `@Builder` |
| **Facade** | `reportes/service/ReporteService.java` | 50-69 | `EstadisticaService.java` (71-77), `IndicadorService.java` (45-53) |
| **Singleton** | `configuracion/service/ConfigService.java` | 38, 44, 59, 69-74 | Todos los beans Spring (75+ implícitos) |
| **Observer/Event** | `common/event/NotificacionEvent.java` | 1-40 | `NotificacionEventListener.java` (37-57), `NotificacionService.java` (97), `ReporteService.java` (67) |
| **DTO** | `gestionpacientes/dto/PacienteRequest.java` | 1-47 | 30+ Request DTOs, 21 Response DTOs |
| **Dependency Injection** | `gestionpacientes/service/PacienteService.java` | 30-40 | 75+ componentes con `@Autowired` |
| **Utility Class** | `common/util/ValidationUtil.java` | 34-36, 44-155 | Usado en `AuthService.java` (161) |
| **Exception Handler** | `common/exception/GlobalExceptionHandler.java` | 25, 36-41, 49-54, 62-77, 85-91 | `ResourceNotFoundException.java`, `BusinessException.java` |
| **Herencia JOINED** | `gestionusuarios/model/Persona.java` | 23 | `Usuario.java` (22), `Cliente.java` (22), `CanalEnvio.java` (24) |
| **Service Layer** | `gestionpacientes/service/PacienteService.java` | 30-80 | 25+ servicios en todos los módulos |

#### Lista Detallada de Archivos por Patrón

**Repository Pattern (26 archivos)**:
- `gestionusuarios/repository/UsuarioRepository.java`
- `gestionusuarios/repository/ClienteRepository.java`
- `gestionusuarios/repository/UsuarioVeterinarioRepository.java`
- `gestionusuarios/repository/RolRepository.java`
- `gestionusuarios/repository/HistorialAccionRepository.java`
- `gestionpacientes/repository/PacienteRepository.java`
- `gestionpacientes/repository/HistoriaClinicaRepository.java`
- `gestionpacientes/repository/RegistroMedicoRepository.java`
- `gestionpacientes/repository/VacunacionRepository.java`
- `gestionpacientes/repository/DesparasitacionRepository.java`
- `gestioninventario/repository/ProductoRepository.java`
- `gestioninventario/repository/ProveedorRepository.java`
- `gestioninventario/repository/MovimientoInventarioRepository.java` (incluye `existsByReferencia` línea 60)
- `gestioninventario/repository/AlertaInventarioRepository.java`
- `gestionfacturacion/repository/FacturaRepository.java`
- `prestacioneservicios/repository/CitaRepository.java`
- `prestacioneservicios/repository/ServicioRepository.java`
- `prestacioneservicios/repository/ServicioPrestadoRepository.java`
- `notificaciones/repository/CanalEnvioRepository.java`
- `notificaciones/repository/NotificacionRepository.java`
- `reportes/repository/ReporteRepository.java`
- `reportes/repository/EstadisticaRepository.java`
- `reportes/repository/IndicadorRepository.java`
- `configuracion/repository/ParametroSistemaRepository.java`
- `configuracion/repository/LogSistemaRepository.java`
- `configuracion/repository/BackupSistemaRepository.java`

**Strategy Pattern (5 archivos)**:
- `notificaciones/model/CanalEnvio.java` - Clase base (línea 54-65: método `enviar()`)
- `notificaciones/model/CanalEmail.java` - Estrategia 1 (línea 45-51: implementación `enviar()`)
- `notificaciones/model/CanalSMS.java` - Estrategia 2 (línea 39-45: implementación `enviar()`)
- `notificaciones/model/CanalApp.java` - Estrategia 3 (línea 39-45: implementación `enviar()`)
- `notificaciones/service/NotificacionService.java` - Cliente que usa estrategias (línea 80-81)

**Command Pattern (4 archivos)**:
- `gestioninventario/service/MovimientoInventarioService.java` - Invoker (líneas 96-126: `registrarEntrada()`, líneas 140-169: `registrarSalida()`, líneas 211-260: `revertirMovimiento()`)
- `gestioninventario/model/MovimientoInventario.java` - Entidad comando
- `gestioninventario/repository/MovimientoInventarioRepository.java` - Soporte (línea 60: `existsByReferencia()`)
- `gestioninventario/controller/MovimientoInventarioController.java` - Endpoint REST (líneas 111-118: `POST /{movimientoId}/revertir`)

**Factory Pattern (2 archivos)**:
- `gestionfacturacion/service/FacturaService.java` - Factory method `crear()` (líneas 52-83), helper `generarNumeroFactura()` (líneas 85-89)
- `prestacioneservicios/service/ServicioPrestadoService.java` - Factory method `registrarEjecucion()` (líneas 68-117)

**Builder Pattern (21 archivos - DTOs Response)**:
- `gestionfacturacion/dto/FacturaResponse.java` - Línea 15: `@Builder`
- `gestionpacientes/dto/PacienteResponse.java` - Línea 13: `@Builder`
- `gestionpacientes/dto/VacunacionResponse.java` - `@Builder`
- `gestionpacientes/dto/DesparasitacionResponse.java` - `@Builder`
- `gestionpacientes/dto/HistoriaClinicaResponse.java` - `@Builder`
- `gestionpacientes/dto/RegistroMedicoResponse.java` - `@Builder`
- `gestioninventario/dto/ProductoResponse.java` - `@Builder`
- `gestioninventario/dto/ProveedorResponse.java` - `@Builder`
- `gestioninventario/dto/MovimientoInventarioResponse.java` - `@Builder`
- `prestacioneservicios/dto/CitaResponse.java` - `@Builder`
- `prestacioneservicios/dto/ServicioPrestadoResponse.java` - Línea 15: `@Builder` (con clases internas Builder)
- `notificaciones/dto/NotificacionResponse.java` - `@Builder`
- `reportes/dto/ReporteResponse.java` - `@Builder`
- `reportes/dto/EstadisticaResponse.java` - `@Builder`
- `gestionusuarios/dto/UsuarioResponse.java` - `@Builder`
- `gestionusuarios/dto/ClienteResponse.java` - `@Builder`
- `gestionusuarios/dto/LoginResponse.java` - `@Builder`
- `gestionusuarios/dto/RolResponse.java` - `@Builder`
- `gestionusuarios/dto/PermisoResponse.java` - `@Builder`
- `gestionpacientes/dto/PacienteOwnerResponse.java` - `@Builder`
- `common/dto/ApiResponse.java` - Respuesta estándar

**Facade Pattern (4 archivos)**:
- `reportes/service/ReporteService.java` - Facade principal (líneas 50-69: método `generar()`)
- `reportes/service/EstadisticaService.java` - Subsistema 1 (líneas 71-77: `calcularEstadisticasParaReporte()`)
- `reportes/service/IndicadorService.java` - Subsistema 2 (líneas 45-53: `evaluarTendencia()`)
- `reportes/repository/ReporteRepository.java` - Subsistema 3 (persistencia)

**Singleton Pattern (1 explícito + 75 implícitos)**:
- `configuracion/service/ConfigService.java` - Singleton explícito (línea 38: `instance`, línea 44: `cache`, línea 59: asignación, líneas 69-74: `getInstance()`)
- Todos los beans con `@Service`, `@Repository`, `@Controller`, `@Component` (singletons implícitos de Spring)

**Observer/Event Pattern (7 archivos)**:
- `common/event/NotificacionEvent.java` - Evento 1 (líneas 1-40)
- `common/event/ReporteGeneradoEvent.java` - Evento 2 (líneas 1-40)
- `common/event/NotificacionEventListener.java` - Listener 1 (líneas 37-57: `handleNotificacionEnviada()`)
- `common/event/ReporteEventListener.java` - Listener 2 (líneas 37-59: `handleReporteGenerado()`)
- `notificaciones/service/NotificacionService.java` - Publicador (línea 39: inyección, líneas 67 y 97: publicación)
- `reportes/service/ReporteService.java` - Publicador (línea 33: inyección, línea 67: publicación)
- `config/AsyncConfig.java` - Configuración (líneas 14-18: `@EnableAsync`)

**DTO Pattern (50+ archivos)**:
- 30+ Request DTOs en `*/dto/*Request.java`
- 21 Response DTOs en `*/dto/*Response.java`
- Ejemplo principal: `gestionpacientes/dto/PacienteRequest.java` (líneas 1-47)

**Dependency Injection (75+ archivos)**:
- Todos los archivos con `@Service` (25+)
- Todos los archivos con `@Repository` (26)
- Todos los archivos con `@Controller` (15+)
- Todos los archivos con `@Component` (5+)
- Ejemplo: `gestionpacientes/service/PacienteService.java` (líneas 30-40: constructor con `@Autowired`)

**Utility Class Pattern (1 archivo)**:
- `common/util/ValidationUtil.java` - Líneas 1-154 (constructor privado línea 34-36, métodos estáticos líneas 44-155)

**Exception Handler Pattern (3 archivos)**:
- `common/exception/GlobalExceptionHandler.java` - Handler principal (líneas 1-92, `@RestControllerAdvice` línea 25)
- `common/exception/ResourceNotFoundException.java` - Excepción personalizada (líneas 1-70)
- `common/exception/BusinessException.java` - Excepción personalizada (líneas 1-70)

**Herencia JOINED Pattern (8 archivos)**:
- `gestionusuarios/model/Persona.java` - Clase base (línea 23: `@Inheritance(strategy = InheritanceType.JOINED)`)
- `gestionusuarios/model/Usuario.java` - Clase intermedia (línea 22: `@PrimaryKeyJoinColumn`)
- `gestionusuarios/model/Cliente.java` - Clase hija (línea 22: `@PrimaryKeyJoinColumn`)
- `gestionusuarios/model/UsuarioVeterinario.java` - Clase hija (línea 22: `@PrimaryKeyJoinColumn`)
- `gestionusuarios/model/Secretario.java` - Clase hija (línea 22: `@PrimaryKeyJoinColumn`)
- `notificaciones/model/CanalEnvio.java` - Clase base (línea 24: `@Inheritance(strategy = InheritanceType.JOINED)`)
- `notificaciones/model/CanalEmail.java` - Clase hija (línea 20: `@PrimaryKeyJoinColumn`)
- `notificaciones/model/CanalSMS.java` - Clase hija (línea 20: `@PrimaryKeyJoinColumn`)
- `notificaciones/model/CanalApp.java` - Clase hija (línea 20: `@PrimaryKeyJoinColumn`)

**Service Layer Pattern (25+ archivos)**:
- Todos los archivos `*/service/*Service.java`
- Ejemplos principales:
  - `gestionpacientes/service/PacienteService.java` (líneas 30-80: constructor y método de negocio)
  - `gestionusuarios/service/AuthService.java` (líneas 1-260)
  - `gestioninventario/service/MovimientoInventarioService.java` (líneas 1-260)
  - `reportes/service/ReporteService.java` (líneas 1-130)
  - `gestionfacturacion/service/FacturaService.java` (líneas 1-200)

---

### 6.15 Relaciones entre Patrones

**Flujo típico**:
```
Controller → Service Layer → Repository → Database
    ↓           ↓              ↓
  DTO      Validaciones    Entidad JPA
    ↓           ↓
Exception Handler ← Utility Class
```

**Combinaciones frecuentes**:
- **Service + Repository + DTO + Builder**: Flujo completo de datos
- **Factory + Builder**: Creación y construcción de objetos
- **Strategy + Observer**: Ejecución de algoritmo y notificación de resultado
- **Command + Repository + Transaction**: Operaciones atómicas reversibles

---

## 7. Base de Datos

### 7.1 Sistema de Gestión

- **SGBD**: PostgreSQL 15
- **Migraciones**: Flyway
- **Ubicación de Scripts**: `src/main/resources/db/migration/`

### 7.2 Esquema Principal

#### Tablas de Usuarios
- `personas`, `usuarios`, `clientes`, `usuarios_veterinarios`, `secretarios`
- `roles`, `permisos`, `roles_permisos`
- `historial_acciones`

#### Tablas de Pacientes
- `pacientes`, `historias_clinicas`, `registros_medicos`
- `vacunaciones`, `desparasitaciones`

#### Tablas de Inventario
- `proveedores`, `productos`, `movimientos_inventario`
- `lotes`, `alertas_inventario`

#### Tablas de Servicios
- `servicios`, `citas`, `servicios_prestados`

#### Tablas de Facturación
- `facturas`

#### Tablas de Notificaciones
- `canales_envio`, `canales_email`, `canales_sms`, `canales_app`
- `notificaciones`

#### Tablas de Reportes
- `reportes`, `estadisticas`, `indicadores`

#### Tablas de Configuración
- `parametros_sistema`, `logs_sistema`, `backups_sistema`

### 7.3 Características

- **JSONB**: Uso de JSONB para campos flexibles (signos vitales, contenido de facturas)
- **Constraints**: CHECK constraints para validaciones
- **Índices**: Índices únicos para evitar duplicados
- **Foreign Keys**: Relaciones con ON DELETE CASCADE
- **Timestamps**: TIMESTAMP WITH TIME ZONE para fechas

---

## 8. Configuración y Tecnologías

### 8.1 Configuración de la Aplicación

**Archivo**: `src/main/resources/application.yml`

**Base de Datos**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/veterinaria_db
    username: vet_admin
    password: Petrico123
```

**JWT**:
```yaml
app:
  jwt:
    secret: Mi_Super_Secreto_Para_JWT_Veterinaria_2024...
    expiration: 86400000  # 24 horas
```

**Servidor**:
```yaml
server:
  port: 8080
  servlet:
    context-path: /api
```

### 8.2 Dependencias Principales

- **Spring Boot Starters**: web, data-jpa, security, validation, mail
- **Base de Datos**: postgresql, flyway-core
- **Seguridad**: jjwt (JWT)
- **Utilidades**: lombok, mapstruct
- **Documentación**: springdoc-openapi (Swagger)
- **Testing**: spring-boot-starter-test, testcontainers

---

## 9. Seguridad y Autenticación

### 9.1 Autenticación JWT

**Flujo**:
1. Usuario envía credenciales → `/api/auth/login`
2. Spring Security valida credenciales
3. `JwtTokenProvider` genera token JWT
4. Se retorna token en respuesta
5. Cliente incluye token en header: `Authorization: Bearer {token}`

### 9.2 Roles del Sistema

- **ADMIN**: Acceso completo
- **VETERINARIO**: Gestión de pacientes, citas, historias clínicas
- **SECRETARIO**: Gestión de citas, facturas
- **CLIENTE**: Consulta de sus propios datos

### 9.3 Configuración de Seguridad

**Archivo**: `config/SecurityConfig.java`

**Endpoints públicos**:
- `/api/auth/**` - Autenticación

**Endpoints protegidos**: Todos los demás requieren autenticación JWT

---

## 10. Guía de Desarrollo

### 10.1 Estructura de un Nuevo Módulo

```
nuevomodulo/
├── model/
│   └── Entidad.java
├── dto/
│   ├── EntidadRequest.java
│   └── EntidadResponse.java
├── repository/
│   └── EntidadRepository.java
├── service/
│   └── EntidadService.java
└── controller/
    └── EntidadController.java
```

### 10.2 Convenciones de Código

- **Nombres**: PascalCase para clases, camelCase para métodos/variables
- **DTOs**: `*Request.java` para entrada, `*Response.java` para salida
- **Servicios**: Métodos transaccionales con `@Transactional`
- **Validaciones**: Bean Validation en DTOs, validaciones de negocio en servicios
- **Excepciones**: Usar `BusinessException` para reglas de negocio, `ResourceNotFoundException` para recursos no encontrados

### 10.3 Testing

- **Unitarias**: Mockear dependencias, probar lógica aislada
- **Integración**: Usar `AbstractIntegrationTest`, probar flujos completos
- **Cobertura**: Objetivo mínimo 70% de cobertura

### 10.4 Documentación

- **Código**: JavaDoc en todas las clases públicas
- **API**: Swagger/OpenAPI automático
- **Endpoints**: Documentar con `@Operation` y `@Schema`

---

**Fin de la Documentación**

