package com.tuorg.veterinaria;

/**
 * DOCUMENTACIÓN COMPLETA DEL PROYECTO - SISTEMA CLÍNICO VETERINARIO
 * 
 * Esta clase documenta todo el proyecto desde su concepción inicial hasta
 * su implementación completa, incluyendo herramientas, tecnologías, base de datos,
 * arquitectura, módulos y patrones de diseño utilizados.
 * 
 * ============================================================================
 * 1. CONCEPCIÓN Y PLANIFICACIÓN DEL PROYECTO
 * ============================================================================
 * 
 * El proyecto es un Sistema de Gestión para Clínica Veterinaria desarrollado
 * como un monolito modular estructurado por capas. Fue diseñado para un equipo
 * de 4 integrantes (4to semestre) y sigue las mejores prácticas de desarrollo
 * de software empresarial.
 * 
 * Objetivo Principal:
 * - Gestionar pacientes (mascotas), usuarios, citas, inventario, servicios,
 *   facturación, notificaciones y reportes estadísticos para una clínica veterinaria.
 * 
 * ============================================================================
 * 2. STACK TECNOLÓGICO Y HERRAMIENTAS UTILIZADAS
 * ============================================================================
 * 
 * 2.1. LENGUAJE DE PROGRAMACIÓN:
 * - Java 17 (LTS - Long Term Support)
 *   - Versión estable y con soporte extendido
 *   - Características modernas: records, pattern matching, text blocks
 *   - Mejor rendimiento y seguridad
 * 
 * 2.2. FRAMEWORK PRINCIPAL:
 * - Spring Boot 3.2.0
 *   - Framework de desarrollo empresarial más popular en Java
 *   - Configuración automática (Auto-Configuration)
 *   - Inyección de dependencias (IoC Container)
 *   - Facilita el desarrollo de aplicaciones REST
 * 
 * 2.3. PERSISTENCIA DE DATOS:
 * - Spring Data JPA (Hibernate como implementación)
 *   - Abstracción sobre JDBC
 *   - Mapeo Objeto-Relacional (ORM)
 *   - Repositorios automáticos
 *   - Consultas por nombre de método
 * 
 * 2.4. BASE DE DATOS:
 * - PostgreSQL 15.x
 *   - Base de datos relacional de código abierto
 *   - Soporte para tipos avanzados (JSONB, UUID, BIGSERIAL)
 *   - ACID compliance
 *   - Excelente rendimiento y escalabilidad
 * 
 * 2.5. MIGRACIONES DE BASE DE DATOS:
 * - Flyway
 *   - Control de versiones de esquema de base de datos
 *   - Scripts SQL versionados (V1__init_schema.sql)
 *   - Migraciones automáticas al iniciar la aplicación
 *   - Historial de cambios en la base de datos
 * 
 * 2.6. SEGURIDAD:
 * - Spring Security
 *   - Framework de seguridad para aplicaciones Java
 *   - Autenticación y autorización
 *   - Protección contra ataques comunes (CSRF, XSS)
 * 
 * - JWT (JSON Web Tokens) - jjwt 0.12.3
 *   - Autenticación stateless
 *   - Tokens firmados y encriptados
 *   - Sin necesidad de sesiones en servidor
 * 
 * 2.7. VALIDACIÓN:
 * - Spring Boot Validation
 *   - Validación de datos de entrada
 *   - Anotaciones (@NotNull, @Size, @Email, etc.)
 *   - Mensajes de error personalizados
 * 
 * 2.8. HERRAMIENTAS DE DESARROLLO:
 * - Maven 3.6+
 *   - Gestión de dependencias
 *   - Compilación y empaquetado
 *   - Gestión del ciclo de vida del proyecto
 * 
 * - IntelliJ IDEA (Recomendado)
 *   - IDE profesional para desarrollo Java
 *   - Soporte completo para Spring Boot
 *   - Debugging avanzado
 * 
* 2.9. UTILIDADES:
 * - Lombok 1.18.30
 *   - Reduce código boilerplate
 *   - Anotaciones: @Getter, @Setter, @NoArgsConstructor, etc.
 *   - Código más limpio y legible
 * 
 * - MapStruct 1.5.5.Final (Opcional)
 *   - Mapeo automático entre objetos
 *   - Generación de código en tiempo de compilación
 *   - Mejor rendimiento que reflexión
 * 
* 2.10. TESTING:
 * - JUnit 5
 *   - Framework de testing unitario
 *   - Anotaciones: @Test, @BeforeEach, etc.
 * 
 * - Mockito
 *   - Framework de mocking
 *   - Simulación de dependencias
 * 
 * - Testcontainers 1.19.3
 *   - Contenedores para pruebas de integración
 *   - PostgreSQL en contenedor para tests
 * 
 * ============================================================================
 * 3. ESTRUCTURA Y ARQUITECTURA DEL PROYECTO
 * ============================================================================
 * 
 * 3.1. ARQUITECTURA GENERAL:
 * - Monolito Modular: Un solo despliegue con módulos independientes
 * - Estructura por Capas: Cada módulo organizado en capas
 * 
 * 3.2. ESTRUCTURA DE CAPAS (por módulo):
 * 
 *   ┌─────────────────────────────────────┐
 *   │      CONTROLLER (Capa de Presentación) │
 *   │  - Endpoints REST                    │
 *   │  - Manejo de peticiones HTTP         │
 *   │  - Validación de entrada             │
 *   └─────────────────────────────────────┘
 *                    ↓
 *   ┌─────────────────────────────────────┐
 *   │      SERVICE (Capa de Negocio)      │
 *   │  - Lógica de negocio                │
 *   │  - Reglas de negocio               │
 *   │  - Transaccionalidad (@Transactional)│
 *   └─────────────────────────────────────┘
 *                    ↓
 *   ┌─────────────────────────────────────┐
 *   │      REPOSITORY (Capa de Acceso a Datos)│
 *   │  - Interfaces Spring Data JPA       │
 *   │  - Consultas a base de datos        │
 *   │  - Abstracción de persistencia      │
 *   └─────────────────────────────────────┘
 *                    ↓
 *   ┌─────────────────────────────────────┐
 *   │      MODEL (Capa de Dominio)        │
 *   │  - Entidades JPA                    │
 *   │  - Anotaciones @Entity, @Table      │
 *   │  - Relaciones (@ManyToOne, etc.)   │
 *   └─────────────────────────────────────┘
 * 
 * 3.3. ESTRUCTURA DE CARPETAS:
 * 
 * veterinaria-backend/
 * ├── src/
 * │   ├── main/
 * │   │   ├── java/com/tuorg/veterinaria/
 * │   │   │   ├── VeterinariaApplication.java    # Clase principal Spring Boot
 * │   │   │   ├── common/                         # Módulo común
 * │   │   │   │   ├── dto/                        # Data Transfer Objects
 * │   │   │   │   ├── exception/                  # Excepciones personalizadas
 * │   │   │   │   ├── constants/                  # Constantes
 * │   │   │   │   └── util/                       # Utilidades
 * │   │   │   ├── config/                         # Configuraciones globales
 * │   │   │   │   └── security/                   # Configuración de seguridad
 * │   │   │   ├── configuracion/                  # Módulo de configuración
 * │   │   │   │   ├── model/
 * │   │   │   │   ├── repository/
 * │   │   │   │   ├── service/
 * │   │   │   │   └── controller/
 * │   │   │   ├── gestionusuarios/              # Módulo de usuarios
 * │   │   │   ├── gestionpacientes/            # Módulo de pacientes
 * │   │   │   ├── gestioninventario/           # Módulo de inventario
 * │   │   │   ├── prestacioneservicios/        # Módulo de servicios
 * │   │   │   ├── notificaciones/              # Módulo de notificaciones
 * │   │   │   └── reportes/                     # Módulo de reportes
 * │   │   └── resources/
 * │   │       ├── application.yml               # Configuración de la aplicación
 * │   │       └── db/migration/                 # Scripts Flyway
 * │   │           └── V1__init_schema.sql       # Migración inicial
* │   └── test/                                 # Pruebas unitarias e integración
* ├── pom.xml                                   # Configuración Maven
 * └── README.md                                 # Documentación del proyecto
 * 
 * ============================================================================
 * 4. ORDEN DE IMPLEMENTACIÓN DEL PROYECTO
 * ============================================================================
 * 
 * FASE 1: CONFIGURACIÓN INICIAL Y ESTRUCTURA BASE
 * -------------------------------------------------
 * 
 * 1.1. Creación del proyecto Maven
 *     - pom.xml con todas las dependencias necesarias
 *     - Configuración de Java 17
 *     - Plugins de Maven (compiler, Spring Boot, Flyway)
 * 
* 1.2. Configuración de PostgreSQL local
*     - Instalación de PostgreSQL 15
*     - Creación de usuario `vet_admin` con contraseña de desarrollo
*     - Creación de la base `veterinaria_db`
*     - Asignación de privilegios
 * 
 * 1.3. Configuración de application.yml
 *     - Perfiles (dev, test, prod)
 *     - Configuración de base de datos
 *     - Configuración de JPA/Hibernate
 *     - Configuración de Flyway
 *     - Configuración de logging
 *     - Configuración de JWT
 * 
 * 1.4. Clase principal Spring Boot
 *     - VeterinariaApplication.java
 *     - Anotación @SpringBootApplication
 *     - Punto de entrada de la aplicación
 * 
 * FASE 2: MÓDULO COMÚN
 * --------------------
 * 
 * 2.1. DTOs (Data Transfer Objects)
 *     - ApiResponse<T>: Respuesta estándar de la API
 *     - Formato consistente para todas las respuestas REST
 * 
 * 2.2. Excepciones Personalizadas
 *     - ResourceNotFoundException: Recurso no encontrado (404)
 *     - BusinessException: Errores de negocio (400)
 *     - GlobalExceptionHandler: Manejador global de excepciones
 * 
 * 2.3. Constantes
 *     - AppConstants: Todas las constantes del sistema
 *     - Estados, tipos, mensajes, etc.
 * 
 * 2.4. Utilidades
 *     - ValidationUtil: Validaciones comunes
 *     - Validación de email, teléfono, contraseñas, etc.
 * 
 * FASE 3: MÓDULO DE CONFIGURACIÓN DEL SISTEMA
 * --------------------------------------------
 * 
 * 3.1. Entidades
 *     - ParametroSistema: Parámetros configurables del sistema
 *     - BackupSistema: Backups del sistema
 *     - LogSistema: Logs de eventos
 * 
 * 3.2. Repositorios
 *     - ParametroSistemaRepository
 *     - BackupSistemaRepository
 *     - LogSistemaRepository
 * 
 * 3.3. Servicios
 *     - ConfigService: Implementa patrón Singleton
 *       * Caché thread-safe con ConcurrentHashMap
 *       * Acceso único a configuración en runtime
 *       * Métodos: obtener(), actualizarValor(), cargarTodos()
 *     - LogSistemaService: Gestión de logs
 * 
 * 3.4. Controlador
 *     - ConfiguracionController: Endpoints REST para configuración
 * 
 * FASE 4: MÓDULO DE GESTIÓN DE USUARIOS
 * -------------------------------------
 * 
 * 4.1. Modelo de Datos (Herencia)
 *     - Persona (clase base abstracta)
 *       * id_persona, nombre, apellido, correo, telefono, direccion
 *     - Usuario (hereda de Persona)
 *       * id_usuario, username, password_hash, activo, ultimo_acceso, rol_id
 *     - Cliente (hereda de Usuario)
 *       * fecha_registro, documento_identidad
 *     - UsuarioVeterinario (hereda de Usuario)
 *       * licencia_profesional, especialidad, disponibilidad (JSONB)
 *     - Secretario (hereda de Usuario)
 *       * extension
 *     - Rol: Roles del sistema (ADMIN, VETERINARIO, SECRETARIO, CLIENTE)
 *     - Permiso: Permisos específicos
 *     - HistorialAccion: Auditoría de acciones de usuarios
 * 
 * 4.2. Repositorios
 *     - UsuarioRepository: Búsqueda por username, email
 *     - RolRepository: Búsqueda por nombre
 *     - HistorialAccionRepository: Búsqueda por usuario, rango de fechas
 * 
 * 4.3. Seguridad JWT
 *     - SecurityConfig: Configuración de Spring Security
 *       * Filtros de seguridad
 *       * Configuración CORS
 *       * PasswordEncoder (BCrypt)
 *     - JwtTokenProvider: Generación y validación de tokens
 *     - JwtAuthenticationFilter: Filtro para validar tokens en cada petición
 *     - JwtAuthenticationEntryPoint: Manejo de errores de autenticación
 *     - CustomUserDetailsService: Carga de usuarios para Spring Security
 * 
 * 4.4. Servicios
 *     - AuthService: Autenticación y registro
 *       * login(): Autentica y genera token JWT
 *       * register(): Registra nuevos usuarios
 *     - UsuarioService: Gestión CRUD de usuarios
 * 
 * 4.5. Controladores
 *     - AuthController: /api/auth/login, /api/auth/register
 *     - UsuarioController: CRUD de usuarios
 * 
 * FASE 5: MIGRACIONES DE BASE DE DATOS (FLYWAY)
 * ---------------------------------------------
 * 
 * 5.1. Script V1__init_schema.sql
 *     - Creación de todas las tablas
 *     - Constraints (CHECK, UNIQUE, FOREIGN KEY)
 *     - Índices para optimización
 *     - Datos iniciales (roles, permisos, parámetros)
 *     - Tipos de datos PostgreSQL (BIGSERIAL, JSONB, UUID, etc.)
 * 
 * FASE 6: MÓDULO DE GESTIÓN DE PACIENTES
 * --------------------------------------
 * 
 * 6.1. Entidades
 *     - Paciente: Información de mascotas
 *       * especie (perro/gato), raza, fecha_nacimiento, peso_kg, etc.
 *     - HistoriaClinica: Historia médica del paciente
 *       * Se crea automáticamente al registrar paciente (invariante)
 *     - RegistroMedico: Registros individuales en la historia
 *       * motivo, diagnostico, tratamiento, signos_vitales (JSONB)
 *     - Vacunacion: Registro de vacunaciones
 *     - Desparasitacion: Registro de desparasitaciones
 * 
 * 6.2. Repositorios
 *     - PacienteRepository: Búsqueda por cliente, especie, nombre
 *     - HistoriaClinicaRepository: Búsqueda por paciente
 *     - RegistroMedicoRepository: Búsqueda por historia
 *     - VacunacionRepository: Búsqueda por paciente, vacunaciones pendientes
 * 
 * 6.3. Servicios
 *     - PacienteService:
 *       * registrarPaciente(): Crea paciente y su historia clínica (transaccional)
 *       * actualizarDatos(): Actualiza información del paciente
 *       * generarResumenClinico(): Genera resumen clínico
 *     - HistoriaClinicaService:
 *       * agregarRegistro(): Agrega registro médico (transaccional con consumo de insumos)
 *       * exportarPDF(): Exporta historia como PDF
 *     - VacunacionService:
 *       * registrarVacuna(): Registra vacunación
 *       * programarProximaDosis(): Programa próxima dosis
 * 
 * 6.4. Controladores
 *     - PacienteController: CRUD de pacientes
 *     - HistoriaClinicaController: Gestión de historias clínicas
 *     - VacunacionController: Gestión de vacunaciones
 * 
 * FASE 7: MÓDULO DE GESTIÓN DE INVENTARIO
 * ----------------------------------------
 * 
 * 7.1. Entidades
 *     - Producto: Productos del inventario
 *       * sku (único), nombre, stock, precio_unitario, tipo
 *       * Constraint: stock >= 0
 *     - MovimientoInventario: Movimientos de entrada/salida (Patrón Command)
 *       * tipo_movimiento: IN, OUT, AJUSTE
 *       * Transaccional: actualiza stock automáticamente
 *     - Proveedor: Proveedores de productos
 *     - AlertaInventario: Alertas de stock bajo
 *     - Lote: Lotes de productos con fecha de vencimiento
 * 
 * 7.2. Repositorios
 *     - ProductoRepository: Búsqueda por SKU, tipo, stock bajo
 *     - MovimientoInventarioRepository: Búsqueda por producto, tipo, rango de fechas
 *     - ProveedorRepository: CRUD de proveedores
 *     - AlertaInventarioRepository: Búsqueda por producto
 * 
 * 7.3. Servicios
 *     - ProductoService:
 *       * actualizarStock(): Actualiza stock (validación de no negativo)
 *       * verificarDisponibilidad(): Verifica stock disponible
 *     - MovimientoInventarioService (Patrón Command):
 *       * registrarEntrada(): Registra entrada y actualiza stock (transaccional)
 *       * registrarSalida(): Verifica stock, registra salida y actualiza stock (transaccional)
 *     - AlertaInventarioService:
 *       * generarAlerta(): Genera alerta de stock bajo
 * 
 * 7.4. Controladores
 *     - ProductoController: CRUD de productos
 *     - MovimientoInventarioController: Gestión de movimientos
 * 
 * FASE 8: MÓDULO DE PRESTACIÓN DE SERVICIOS
 * ------------------------------------------
 * 
 * 8.1. Entidades
 *     - Servicio: Servicios ofrecidos (consultas, cirugías, etc.)
 *     - Cita: Citas médicas programadas
 *       * Validación de doble reserva (índice único)
 *       * Estados: PROGRAMADA, REALIZADA, CANCELADA
 *     - ServicioPrestado: Servicio ejecutado (Patrón Factory/Builder)
 *       * Transaccional complejo: crea servicio, consume inventario, genera factura
 *     - Factura: Facturas generadas (Patrón Factory/Builder)
 *       * Número único generado automáticamente
 *       * Estados: PENDIENTE, PAGADA, ANULADA
 * 
 * 8.2. Repositorios
 *     - ServicioRepository: Búsqueda por tipo
 *     - CitaRepository: Búsqueda por paciente, veterinario, estado
 *       * Validación de doble reserva
 *     - ServicioPrestadoRepository: Búsqueda por cita
 *     - FacturaRepository: Búsqueda por número, cliente, estado
 * 
 * 8.3. Servicios
 *     - CitaService:
 *       * programar(): Programa cita (valida doble reserva)
 *       * reprogramar(): Reprograma cita
 *       * cancelar(): Cancela cita
 *       * completar(): Marca cita como realizada
 *     - ServicioPrestadoService (Transaccional complejo):
 *       * registrarEjecucion(): 
 *         - Crea servicio prestado
 *         - Consume insumos del inventario
 *         - Genera factura
 *         - Marca cita como REALIZADA
 *         - Todo en una sola transacción (@Transactional)
 *     - FacturaService (Patrón Factory):
 *       * crear(): Genera número único automáticamente
 *       * generarPDF(): Exporta factura como PDF
 *       * anular(): Anula factura
 *       * registrarPago(): Registra pago de factura
 * 
 * 8.4. Controladores
 *     - CitaController: Gestión de citas
 *     - FacturaController: Gestión de facturas
 * 
 * FASE 9: MÓDULO DE NOTIFICACIONES
 * --------------------------------
 * 
 * 9.1. Entidades (Patrón Strategy)
 *     - CanalEnvio (clase base): Canal abstracto de envío
 *       * Método abstracto: enviar(Notificacion)
 *     - CanalEmail (implementación): Envío por email
 *       * smtp_server, from_address
 *     - CanalApp (implementación): Notificaciones push en app
 *       * app_topic
 *     - Notificacion: Notificaciones del sistema
 *       * Estados: PENDIENTE, ENVIADA, FALLIDA
 *     - PlantillaMensaje: Plantillas reutilizables
 * 
 * 9.2. Repositorios
 *     - CanalEnvioRepository: CRUD de canales
 *     - NotificacionRepository: Búsqueda por estado, notificaciones pendientes
 * 
 * 9.3. Servicios (Patrón Strategy)
 *     - NotificacionService:
 *       * programarEnvio(): Programa notificación para fecha futura
 *       * enviarAhora(): Envía notificación usando canal (Strategy)
 *         - El canal concreto decide cómo enviar
 *         - Sin modificar código cliente
 * 
 * 9.4. Controladores
 *     - NotificacionController: Gestión de notificaciones
 * 
 * FASE 10: MÓDULO DE REPORTES Y ESTADÍSTICAS
 * -------------------------------------------
 * 
 * 10.1. Entidades (Patrón Facade)
 *      - Reporte: Reportes del sistema
 *      - Estadistica: Estadísticas calculadas
 *      - Indicador: Indicadores clave (KPIs)
 * 
 * 10.2. Repositorios
 *      - ReporteRepository: CRUD de reportes
 *      - EstadisticaRepository: Búsqueda por nombre
 *      - IndicadorRepository: CRUD de indicadores
 * 
 * 10.3. Servicios (Patrón Facade)
 *      - ReporteService (Facade):
 *        * generar(): Simplifica generación de reportes
 *          - Agrupa múltiples operaciones complejas
 *          - Calcula estadísticas
 *          - Obtiene datos de múltiples fuentes
 *          - Genera reporte
 *        * exportarPDF(): Exporta reporte como PDF
 *        * exportarExcel(): Exporta reporte como Excel
 *      - EstadisticaService:
 *        * calcular(): Calcula estadísticas
 *      - IndicadorService:
 *        * evaluarTendencia(): Evalúa tendencias de indicadores
 * 
 * 10.4. Controladores
 *      - ReporteController: Generación y exportación de reportes
 * 
 * ============================================================================
 * 5. PATRONES DE DISEÑO IMPLEMENTADOS
 * ============================================================================
 * 
 * 5.1. SINGLETON
 *     - ConfigService: Una única instancia para configuración del sistema
 *     - Caché thread-safe con ConcurrentHashMap
 *     - Acceso global: ConfigService.getInstance()
 * 
 * 5.2. STRATEGY
 *     - CanalEnvio y sus implementaciones (CanalEmail, CanalApp)
 *     - Permite cambiar estrategia de envío sin modificar código cliente
 *     - NotificacionService usa el canal apropiado dinámicamente
 * 
 * 5.3. COMMAND
 *     - MovimientoInventario: Encapsula operaciones de inventario
 *     - registrarEntrada() y registrarSalida() son comandos transaccionales
 *     - Permiten reversión en caso de error
 * 
 * 5.4. FACTORY/BUILDER
 *     - FacturaService.crear(): Factory para crear facturas con número único
 *     - ServicioPrestadoService.registrarEjecucion(): Builder para objetos complejos
 * 
 * 5.5. REPOSITORY
 *     - Spring Data JPA: Repositorios automáticos
 *     - Abstracción de acceso a datos
 *     - Consultas por nombre de método
 * 
 * 5.6. FACADE
 *     - ReporteService: Simplifica operaciones complejas
 *     - Agrupa múltiples servicios y cálculos
 *     - Interfaz simple para el cliente
 * 
 * ============================================================================
 * 6. BASE DE DATOS POSTGRESQL
 * ============================================================================
 * 
 * 6.1. TIPOS DE DATOS UTILIZADOS:
 *     - BIGSERIAL: IDs auto-incrementales (equivalente a BIGINT + SEQUENCE)
 *     - BIGINT: IDs de relaciones (foreign keys)
 *     - VARCHAR(n): Cadenas de longitud fija
 *     - TEXT: Cadenas de longitud variable ilimitada
 *     - NUMERIC(p,s): Números decimales precisos (precio, peso)
 *     - BOOLEAN: Valores verdadero/falso
 *     - TIMESTAMP WITH TIME ZONE: Fechas y horas con zona horaria
 *     - DATE: Solo fechas (sin hora)
 *     - JSONB: Datos JSON binarios (flexible y eficiente)
 *     - UUID: Identificadores únicos universales
 * 
 * 6.2. CONSTRAINTS IMPLEMENTADOS:
 *     - PRIMARY KEY: Claves primarias
 *     - FOREIGN KEY: Relaciones entre tablas
 *     - UNIQUE: Valores únicos (username, email, sku, numero_factura)
 *     - CHECK: Validaciones (stock >= 0, precio >= 0, especie IN ('perro','gato'))
 *     - NOT NULL: Campos obligatorios
 * 
 * 6.3. ÍNDICES:
 *     - Índices en campos de búsqueda frecuente (nombre, tipo, fecha_hora)
 *     - Índice único compuesto para evitar doble reserva de citas
 * 
 * 6.4. RELACIONES:
 *     - One-to-Many: Usuario → HistorialAccion, Paciente → Vacunacion
 *     - Many-to-One: MovimientoInventario → Producto, Cita → Paciente
 *     - Many-to-Many: Rol ↔ Permiso (tabla intermedia rol_permisos)
 *     - Herencia: Persona → Usuario → Cliente/Veterinario/Secretario (JOINED)
 * 
 * ============================================================================
 * 7. SEGURIDAD Y AUTENTICACIÓN
 * ============================================================================
 * 
 * 7.1. SPRING SECURITY:
 *     - Configuración en SecurityConfig
 *     - Filtros de seguridad personalizados
 *     - CORS configurado para frontend
 *     - Endpoints públicos: /api/auth/**
 *     - Endpoints protegidos: Requieren token JWT
 * 
 * 7.2. JWT (JSON WEB TOKENS):
 *     - Generación: JwtTokenProvider.generateToken()
 *     - Validación: JwtTokenProvider.validateToken()
 *     - Filtro: JwtAuthenticationFilter valida token en cada petición
 *     - Header: Authorization: Bearer <token>
 *     - Expiración: Configurable en application.yml
 * 
 * 7.3. ROLES Y PERMISOS:
 *     - Roles: ADMIN, VETERINARIO, SECRETARIO, CLIENTE
 *     - Permisos: Acciones específicas (CREAR_USUARIO, VER_REPORTES, etc.)
 *     - Relación Many-to-Many entre Roles y Permisos
 * 
 * 7.4. PASSWORD ENCODING:
 *     - BCrypt: Algoritmo de hash para contraseñas
 *     - Salt automático
 *     - Nunca se almacenan contraseñas en texto plano
 * 
 * ============================================================================
 * 8. TRANSACCIONALIDAD
 * ============================================================================
 * 
 * 8.1. OPERACIONES TRANSACCIONALES:
 *     - @Transactional en métodos de servicios
 *     - Rollback automático en caso de error
 *     - ACID compliance garantizado
 * 
 * 8.2. TRANSACCIONES COMPLEJAS:
 *     - registrarEjecucion(): Servicio prestado + Inventario + Factura + Cita
 *     - registrarSalida(): Verificación stock + Movimiento + Actualización stock
 *     - registrarPaciente(): Paciente + Historia clínica
 * 
 * ============================================================================
 * 9. TESTING CON SWAGGER/OPENAPI
 * ============================================================================
 * 
 * 9.1. CONFIGURACIÓN DE SWAGGER:
 *     Swagger/OpenAPI está configurado y listo para usar en el proyecto.
 *     - Dependencia agregada en pom.xml (springdoc-openapi-starter-webmvc-ui 2.2.0)
 *     - Configuración en SwaggerConfig.java
 *     - Endpoints de Swagger permitidos en SecurityConfig
 * 
 * 9.2. ACCESO A SWAGGER UI:
 *     - URL: http://localhost:8080/swagger-ui.html
 *     - Documentación automática de todos los endpoints
 *     - Interfaz gráfica para probar endpoints
 *     - Incluye esquemas de request/response
 * 
 * 9.3. ENDPOINTS DISPONIBLES PARA TESTING:
 *     
 *     AUTENTICACIÓN:
 *     - POST /api/auth/login
 *       Body: {"username": "admin", "password": "password"}
 *       Response: {"token": "eyJhbGc...", "type": "Bearer"}
 *     
 *     - POST /api/auth/register
 *       Body: {"username": "user", "password": "pass", "email": "user@email.com", ...}
 *     
 *     USUARIOS:
 *     - GET /api/usuarios
 *     - GET /api/usuarios/{id}
 *     - POST /api/usuarios
 *     - PUT /api/usuarios/{id}
 *     - DELETE /api/usuarios/{id}
 *     
 *     PACIENTES:
 *     - GET /api/pacientes
 *     - POST /api/pacientes
 *     - GET /api/pacientes/{id}
 *     - PUT /api/pacientes/{id}
 *     
 *     CITAS:
 *     - POST /api/citas
 *     - GET /api/citas/paciente/{pacienteId}
 *     - PUT /api/citas/{id}/completar
 *     
 *     INVENTARIO:
 *     - GET /api/productos
 *     - POST /api/movimientos-inventario/entrada
 *     - POST /api/movimientos-inventario/salida
 *     
 *     FACTURAS:
 *     - GET /api/facturas
 *     - POST /api/facturas
 *     - GET /api/facturas/{id}/pdf
 *     
 *     NOTIFICACIONES:
 *     - POST /api/notificaciones/enviar
 *     - GET /api/notificaciones/pendientes
 *     
 *     REPORTES:
 *     - POST /api/reportes/generar
 *     - GET /api/reportes/{id}/exportar-pdf
 * 
 * 9.4. USO DE SWAGGER PARA TESTING:
 *     1. Iniciar la aplicación
 *     2. Abrir navegador en http://localhost:8080/swagger-ui.html
 *     3. Expandir el endpoint deseado
 *     4. Hacer clic en "Try it out"
 *     5. Completar los parámetros del request
 *     6. Hacer clic en "Execute"
 *     7. Ver la respuesta del servidor
 * 
 * 9.5. AUTENTICACIÓN EN SWAGGER:
 *     - Hacer login primero en /api/auth/login
 *     - Copiar el token recibido
 *     - En Swagger UI, hacer clic en "Authorize" (botón verde)
 *     - Pegar el token en el campo "Value"
 *     - Formato: Bearer <token> o solo <token>
 *     - Ahora todos los endpoints protegidos estarán autenticados
 * 
 * ============================================================================
 * 10. FLUJO DE TRABAJO TÍPICO
 * ============================================================================
 * 
 * 10.1. FLUJO DE REGISTRO Y ATENCIÓN:
 *      1. Cliente se registra (/api/auth/register)
 *      2. Cliente inicia sesión (/api/auth/login) → Obtiene token JWT
 *      3. Cliente registra su mascota (/api/pacientes) → Se crea historia clínica automáticamente
 *      4. Cliente solicita cita (/api/citas) → Se valida disponibilidad del veterinario
 *      5. Veterinario atiende la cita
 *      6. Se registra servicio prestado (/api/servicios-prestados) → 
 *         - Consume insumos del inventario
 *         - Genera factura automáticamente
 *         - Marca cita como realizada
 *      7. Cliente paga factura (/api/facturas/{id}/pagar)
 * 
 * 10.2. FLUJO DE INVENTARIO:
 *      1. Proveedor entrega productos
 *      2. Secretario registra entrada (/api/movimientos-inventario/entrada)
 *      3. Sistema actualiza stock automáticamente
 *      4. Si stock baja del mínimo, se genera alerta automáticamente
 *      5. Al prestar servicio, se registra salida (/api/movimientos-inventario/salida)
 * 
 * 10.3. FLUJO DE NOTIFICACIONES:
 *      1. Sistema detecta evento (cita programada, vacuna pendiente, etc.)
 *      2. Se crea notificación (/api/notificaciones/programar)
 *      3. Se selecciona canal apropiado (Strategy pattern)
 *      4. Se envía notificación (/api/notificaciones/enviar)
 * 
 * ============================================================================
 * 11. MEJORES PRÁCTICAS IMPLEMENTADAS
 * ============================================================================
 * 
 * 11.1. CÓDIGO:
 *      - Comentarios completos en español
 *      - Nombres descriptivos de clases, métodos y variables
 *      - Separación de responsabilidades (SRP)
 *      - DRY (Don't Repeat Yourself)
 *      - Validaciones de entrada
 *      - Manejo de excepciones apropiado
 * 
 * 11.2. BASE DE DATOS:
 *      - Normalización (3NF)
 *      - Índices en campos de búsqueda
 *      - Constraints para integridad de datos
 *      - Migraciones versionadas (Flyway)
 * 
 * 11.3. SEGURIDAD:
 *      - Contraseñas hasheadas (BCrypt)
 *      - Tokens JWT con expiración
 *      - Validación de entrada
 *      - Protección contra SQL Injection (JPA)
 * 
 * 11.4. ARQUITECTURA:
 *      - Capas bien definidas
 *      - Módulos independientes
 *      - Bajo acoplamiento
 *      - Alta cohesión
 * 
 * ============================================================================
 * 12. COMANDOS ÚTILES
 * ============================================================================
 * 
* 12.1. PostgreSQL (local):
*      - psql -U postgres              # Abrir consola interactiva
*      - \du                           # Listar roles
*      - \l                            # Listar bases
*      - ALTER ROLE vet_admin WITH PASSWORD '...';
*      - \c veterinaria_db             # Conectarse a la base
* 
* 12.2. MAVEN:
 *      - mvn clean install             # Compilar proyecto
 *      - mvn spring-boot:run          # Ejecutar aplicación
 *      - mvn test                      # Ejecutar tests
 *      - mvn flyway:migrate           # Ejecutar migraciones manualmente
 * 
* 12.3. Flyway:
*      - mvn flyway:migrate            # Ejecutar migraciones manualmente
*      - mvn flyway:info               # Ver estado de las migraciones
 * 
 * ============================================================================
 * 13. PRÓXIMOS PASOS Y MEJORAS FUTURAS
 * ============================================================================
 * 
 * 13.1. FUNCIONALIDADES PENDIENTES:
 *      - Generación real de PDFs (iText/JasperReports)
 *      - Envío real de emails (JavaMailSender)
 *      - Notificaciones push reales
 *      - Cálculos reales de estadísticas
 *      - Dashboard en tiempo real
 * 
 * 13.2. TESTING:
 *      - Tests unitarios completos (JUnit 5)
 *      - Tests de integración (Testcontainers)
 *      - Tests end-to-end (Postman/Newman)
 *      - Cobertura de código > 80%
 * 
 * 13.3. DOCUMENTACIÓN:
 *      - Swagger/OpenAPI completo
 *      - Documentación de API
 *      - Guías de usuario
 * 
 * 13.4. DEPLOYMENT:
 *      - CI/CD con GitHub Actions
 *      - Dockerización de la aplicación
 *      - Configuración de producción
 * 
 * ============================================================================
 * FIN DE LA DOCUMENTACIÓN
 * ============================================================================
 * 
 * Este proyecto representa una implementación completa de un sistema de gestión
 * veterinaria siguiendo las mejores prácticas de desarrollo de software empresarial,
 * con arquitectura modular, patrones de diseño apropiados, seguridad robusta y
 * código completamente documentado.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 * @since 2024
 */
public class DocumentacionProyecto {
    
    /**
     * Esta clase es únicamente para documentación.
     * No contiene código ejecutable, solo comentarios de documentación.
     */
    
    // Esta clase no necesita implementación
    // Su propósito es servir como documentación completa del proyecto
}

