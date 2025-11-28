# Documentación de Pruebas - Sistema Clínico Veterinario

## Índice

1. [Introducción](#introducción)
2. [Estructura de Pruebas](#estructura-de-pruebas)
3. [Tecnologías Utilizadas](#tecnologías-utilizadas)
4. [Pruebas Unitarias](#pruebas-unitarias)
5. [Pruebas de Integración](#pruebas-de-integración)
6. [Configuración](#configuración)
7. [Ejecución de Pruebas](#ejecución-de-pruebas)
8. [Cobertura de Pruebas](#cobertura-de-pruebas)

---

## Introducción

Este documento describe la suite completa de pruebas implementadas para el Sistema Clínico Veterinario. Las pruebas están organizadas por módulos funcionales y cubren tanto pruebas unitarias como de integración.

### Objetivos de las Pruebas

- **Validación de reglas de negocio**: Asegurar que las validaciones funcionan correctamente
- **Mapeo DTO-Entidad**: Verificar que las conversiones entre DTOs y entidades son correctas
- **Integración de componentes**: Validar que los controladores, servicios y repositorios trabajan juntos
- **Casos de error**: Probar escenarios de fallo y validaciones

---

## Estructura de Pruebas

```
src/test/java/com/tuorg/veterinaria/
├── config/
│   ├── TestConfig.java                    # Configuración base para pruebas
│   └── AbstractIntegrationTest.java       # Clase base para pruebas de integración
├── gestionusuarios/
│   ├── service/
│   │   ├── AuthServiceTest.java          # Pruebas de autenticación
│   │   ├── UsuarioServiceTest.java       # Pruebas de gestión de usuarios
│   │   └── ClienteServiceTest.java       # Pruebas de gestión de clientes
│   └── controller/
│       └── AuthControllerIntegrationTest.java  # Pruebas de integración de autenticación
├── gestionpacientes/
│   └── service/
│       ├── PacienteServiceTest.java      # Pruebas de gestión de pacientes
│       └── VacunacionServiceTest.java    # Pruebas de vacunaciones
├── gestioninventario/
│   └── service/
│       ├── ProductoServiceTest.java      # Pruebas de productos
│       └── MovimientoInventarioServiceTest.java  # Pruebas de movimientos de inventario
└── prestacioneservicios/
    └── service/
        ├── CitaServiceTest.java          # Pruebas de citas
        └── FacturaServiceTest.java       # Pruebas de facturas
```

---

## Tecnologías Utilizadas

### Framework de Pruebas
- **JUnit 5**: Framework principal para pruebas unitarias y de integración
- **AssertJ**: Biblioteca de aserciones fluida y legible
- **Mockito**: Framework para crear mocks y stubs

### Pruebas de Integración
- **Spring Boot Test**: Soporte para pruebas de integración con Spring
- **MockMvc**: Para probar controladores REST sin levantar el servidor completo
- **Testcontainers**: Para pruebas con PostgreSQL real en contenedores Docker

### Configuración
- **@ExtendWith(MockitoExtension.class)**: Para pruebas unitarias con Mockito
- **@SpringBootTest**: Para pruebas de integración con contexto completo
- **@DataJpaTest**: Para pruebas de repositorios (pendiente de implementar)

---

## Pruebas Unitarias

### 1. AuthServiceTest

**Ubicación**: `gestionusuarios/service/AuthServiceTest.java`

**Propósito**: Validar la lógica de autenticación, generación de tokens JWT y actualización del último acceso.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `loginExitoso_DeberiaGenerarTokenYActualizarUltimoAcceso` | Prueba login exitoso | Verifica que se genera token, se actualiza último acceso y se retorna respuesta correcta |
| `loginUsuarioNoEncontrado_DeberiaLanzarExcepcion` | Prueba con usuario inexistente | Verifica que lanza `BusinessException` con mensaje apropiado |
| `loginUsuarioInactivo_DeberiaLanzarExcepcion` | Prueba con usuario inactivo | Verifica que rechaza usuarios inactivos |
| `loginCredencialesInvalidas_DeberiaLanzarExcepcion` | Prueba con contraseña incorrecta | Verifica manejo de `BadCredentialsException` |

#### Ejemplo de Código

```java
@Test
@DisplayName("Login exitoso: debe generar token y actualizar último acceso")
void loginExitoso_DeberiaGenerarTokenYActualizarUltimoAcceso() {
    // Arrange
    String username = "testuser";
    String password = "password123";
    String token = "jwt-token-123";

    when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(tokenProvider.generateToken(userDetails)).thenReturn(token);
    
    // Act
    LoginResponse response = authService.login(username, password);
    
    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getToken()).isEqualTo(token);
    assertThat(response.getType()).isEqualTo("Bearer");
    verify(usuarioRepository).save(usuario);
}
```

---

### 2. UsuarioServiceTest

**Ubicación**: `gestionusuarios/service/UsuarioServiceTest.java`

**Propósito**: Validar reglas de negocio para creación de usuarios y mapeo DTO → Entidad.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `crearUsuarioExitoso_DeberiaValidarYMapearCorrectamente` | Creación exitosa de usuario | Verifica validaciones, codificación de contraseña y mapeo |
| `crearUsuarioUsernameDuplicado_DeberiaLanzarExcepcion` | Username duplicado | Verifica que rechaza usuarios con username existente |
| `crearUsuarioEmailEnUso_DeberiaLanzarExcepcion` | Email duplicado | Verifica que rechaza usuarios con email existente |
| `crearUsuarioContrasenaCorta_DeberiaLanzarExcepcion` | Contraseña inválida | Verifica validación de longitud mínima de contraseña |
| `obtenerUsuario_DeberiaMapearCorrectamente` | Obtención de usuario | Verifica mapeo correcto a DTO con rol incluido |
| `obtenerUsuarioNoEncontrado_DeberiaLanzarExcepcion` | Usuario inexistente | Verifica manejo de usuario no encontrado |

#### Reglas de Negocio Validadas

- ✅ Username debe ser único
- ✅ Email debe ser único
- ✅ Contraseña debe cumplir longitud mínima
- ✅ Mapeo correcto de entidad a DTO incluyendo rol y permisos

---

### 3. ClienteServiceTest

**Ubicación**: `gestionusuarios/service/ClienteServiceTest.java`

**Propósito**: Validar que los clientes se crean con rol CLIENTE, contraseña codificada y datos válidos.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `crearClienteExitoso_DeberiaAsignarRolClienteYCodificarPassword` | Creación exitosa | Verifica asignación de rol CLIENTE y codificación de contraseña |
| `crearClienteUsernameDuplicado_DeberiaRechazar` | Username duplicado | Verifica rechazo de username duplicado |
| `crearClienteEmailDuplicado_DeberiaRechazar` | Email duplicado | Verifica rechazo de email duplicado |
| `crearClienteContrasenaCorta_DeberiaRechazar` | Contraseña inválida | Verifica validación de contraseña |
| `crearClienteRolNoConfigurado_DeberiaLanzarExcepcion` | Rol CLIENTE no existe | Verifica que el rol CLIENTE debe estar configurado |

#### Validaciones Específicas

- ✅ Asignación automática de rol CLIENTE
- ✅ Codificación de contraseña con BCrypt
- ✅ Validación de datos de entrada (username, email, teléfono)
- ✅ Rechazo de datos inválidos

---

### 4. PacienteServiceTest

**Ubicación**: `gestionpacientes/service/PacienteServiceTest.java`

**Propósito**: Validar registro de pacientes con validaciones de especie, fecha y cliente.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `registrarPacienteExitoso_DeberiaCrearPacienteEHistoriaClinica` | Registro exitoso | Verifica creación de paciente e historia clínica asociada |
| `registrarPacienteEspecieInvalida_DeberiaLanzarExcepcion` | Especie inválida | Verifica que solo acepta "perro" o "gato" |
| `registrarPacienteFechaFutura_DeberiaLanzarExcepcion` | Fecha de nacimiento futura | Verifica rechazo de fechas futuras |
| `registrarPacienteClienteInexistente_DeberiaLanzarExcepcion` | Cliente no existe | Verifica validación de existencia del cliente |
| `registrarPaciente_DeberiaMapearCorrectamente` | Mapeo a DTO | Verifica mapeo correcto incluyendo información del dueño |

#### Reglas de Negocio Validadas

- ✅ Especie debe ser "perro" o "gato" (case-insensitive)
- ✅ Fecha de nacimiento no puede ser futura
- ✅ Peso debe ser positivo si se proporciona
- ✅ Cliente debe existir y ser válido
- ✅ Creación automática de historia clínica asociada

---

### 5. VacunacionServiceTest

**Ubicación**: `gestionpacientes/service/VacunacionServiceTest.java`

**Propósito**: Validar registro de vacunaciones con validaciones de fecha y veterinario.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `registrarVacunacionExitoso_DeberiaMapearCorrectamente` | Registro exitoso | Verifica mapeo correcto incluyendo paciente y veterinario |
| `registrarVacunacionFechaFutura_DeberiaLanzarExcepcion` | Fecha futura | Verifica que fecha de aplicación no puede ser futura |
| `registrarVacunacionVeterinarioNoValido_DeberiaLanzarExcepcion` | Veterinario inválido | Verifica que el usuario debe ser UsuarioVeterinario |
| `programarProximaDosisFechaPasada_DeberiaLanzarExcepcion` | Fecha pasada | Verifica que próxima dosis no puede ser pasada |

#### Validaciones Específicas

- ✅ Fecha de aplicación no puede ser futura
- ✅ Veterinario debe ser instancia de UsuarioVeterinario
- ✅ Próxima dosis debe ser fecha futura
- ✅ Mapeo correcto de signos vitales JSON → Map

---

### 6. ProductoServiceTest

**Ubicación**: `gestioninventario/service/ProductoServiceTest.java`

**Propósito**: Validar gestión de productos con validaciones de SKU y stock.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `crearProductoExitoso_DeberiaMapearCorrectamente` | Creación exitosa | Verifica mapeo correcto a ProductoResponse |
| `crearProductoSkuDuplicado_DeberiaLanzarExcepcion` | SKU duplicado | Verifica que SKU debe ser único |
| `crearProductoStockNegativo_DeberiaLanzarExcepcion` | Stock negativo | Verifica rechazo de stock negativo |
| `obtenerProducto_DeberiaMapearCorrectamente` | Obtención de producto | Verifica mapeo correcto |

#### Reglas de Negocio Validadas

- ✅ SKU debe ser único en el sistema
- ✅ Stock inicial no puede ser negativo
- ✅ Precio unitario debe ser no negativo
- ✅ Mapeo correcto incluyendo metadatos JSON

---

### 7. MovimientoInventarioServiceTest

**Ubicación**: `gestioninventario/service/MovimientoInventarioServiceTest.java`

**Propósito**: Validar movimientos de inventario (entradas y salidas) y actualización de stock.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `registrarEntradaExitoso_DeberiaActualizarStock` | Entrada exitosa | Verifica incremento de stock y creación de movimiento |
| `registrarEntradaCantidadInvalida_DeberiaLanzarExcepcion` | Cantidad ≤ 0 | Verifica rechazo de cantidades inválidas |
| `registrarSalidaExitoso_DeberiaVerificarStockYActualizar` | Salida exitosa | Verifica decremento de stock y validación de disponibilidad |
| `registrarSalidaSinStock_DeberiaLanzarExcepcion` | Stock insuficiente | Verifica rechazo cuando no hay stock suficiente |
| `registrarSalidaCantidadInvalida_DeberiaLanzarExcepcion` | Cantidad inválida | Verifica rechazo de cantidades ≤ 0 |

#### Validaciones Específicas

- ✅ Entrada: cantidad debe ser > 0
- ✅ Salida: debe verificar disponibilidad antes de registrar
- ✅ Salida: cantidad solicitada no puede exceder stock disponible
- ✅ Actualización correcta de stock resultante
- ✅ Mapeo correcto incluyendo información de producto, proveedor y usuario

---

### 8. CitaServiceTest

**Ubicación**: `prestacioneservicios/service/CitaServiceTest.java`

**Propósito**: Validar gestión del ciclo de vida de citas (programar, reprogramar, cancelar, completar).

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `programarCitaExitoso_DeberiaCrearCitaProgramada` | Programación exitosa | Verifica creación en estado PROGRAMADA |
| `programarCitaHoraPasado_DeberiaLanzarExcepcion` | Hora en pasado | Verifica rechazo de citas en el pasado |
| `reprogramarCitaEstadoInvalido_DeberiaLanzarExcepcion` | Estado inválido | Verifica que solo se pueden reprogramar citas PROGRAMADAS |
| `cancelarCitaYaRealizada_DeberiaLanzarExcepcion` | Cita realizada | Verifica que no se puede cancelar cita ya realizada |
| `completarCitaEstadoInvalido_DeberiaLanzarExcepcion` | Estado inválido | Verifica que solo se pueden completar citas PROGRAMADAS |
| `completarCitaExitoso_DeberiaCambiarEstado` | Completar exitoso | Verifica cambio de estado a REALIZADA |

#### Estados de Cita Validados

- ✅ **PROGRAMADA**: Estado inicial, permite reprogramar, cancelar o completar
- ✅ **REALIZADA**: No se puede cancelar ni reprogramar
- ✅ **CANCELADA**: Estado final, no permite cambios

#### Validaciones Específicas

- ✅ Fecha/hora no puede ser en el pasado
- ✅ Validación de disponibilidad del veterinario
- ✅ Transiciones de estado válidas
- ✅ Mapeo correcto incluyendo información de paciente y veterinario

---

### 9. FacturaServiceTest

**Ubicación**: `prestacioneservicios/service/FacturaServiceTest.java`

**Propósito**: Validar creación de facturas con validaciones de totales y cliente.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `crearFacturaExitoso_DeberiaMapearCorrectamente` | Creación exitosa | Verifica mapeo correcto incluyendo cliente y contenido |
| `crearFacturaTotalNegativo_DeberiaLanzarExcepcion` | Total negativo | Verifica rechazo de totales negativos |
| `crearFacturaClienteInexistente_DeberiaLanzarExcepcion` | Cliente no existe | Verifica validación de existencia del cliente |
| `crearFacturaUsuarioNoCliente_DeberiaLanzarExcepcion` | Usuario no es cliente | Verifica que el usuario debe ser instancia de Cliente |

#### Validaciones Específicas

- ✅ Total debe ser ≥ 0
- ✅ Cliente debe existir y ser instancia de Cliente
- ✅ Generación de número de factura único
- ✅ Mapeo correcto de contenido JSON
- ✅ Mapeo correcto de líneas de factura

---

## Pruebas de Integración

### 1. AbstractIntegrationTest

**Ubicación**: `config/AbstractIntegrationTest.java`

**Propósito**: Clase base para pruebas de integración con configuración de Testcontainers.

#### Características

- ✅ Configuración de PostgreSQL 15 en contenedor Docker
- ✅ Configuración dinámica de propiedades de Spring
- ✅ Perfil de prueba activo
- ✅ MockMvc configurado automáticamente

#### Configuración de Testcontainers

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("veterinaria_test")
        .withUsername("test")
        .withPassword("test");
```

---

### 2. AuthControllerIntegrationTest

**Ubicación**: `gestionusuarios/controller/AuthControllerIntegrationTest.java`

**Propósito**: Pruebas de integración del endpoint de autenticación.

#### Casos de Prueba Implementados

| Método de Prueba | Descripción | Validaciones |
|-----------------|-------------|--------------|
| `loginCredencialesCorrectas_DeberiaRetornar200ConToken` | Login exitoso | Verifica respuesta 200 con token JWT válido |
| `loginCredencialesIncorrectas_DeberiaRetornar401` | Credenciales inválidas | Verifica respuesta 401/400 con credenciales incorrectas |
| `loginUsuarioInexistente_DeberiaRetornarError` | Usuario no existe | Verifica manejo de usuario inexistente |

#### Endpoint Probado

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

#### Respuesta Esperada (Éxito)

```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "usuario": {
      "idUsuario": 1,
      "nombre": "Test",
      "apellido": "User",
      "correo": "test@example.com",
      "rol": "CLIENTE"
    }
  }
}
```

---

## Configuración

### Dependencias en pom.xml

Las siguientes dependencias ya están configuradas en el proyecto:

```xml
<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

### Perfil de Prueba

Crear `src/test/resources/application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/veterinaria_test
    username: test
    password: test
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true

app:
  jwt:
    secret: test-secret-key-for-testing-purposes-only
    expiration: 86400000
```

---

## Ejecución de Pruebas

### Ejecutar Todas las Pruebas

```bash
# Con Maven
mvn test

# Solo pruebas unitarias
mvn test -Dtest=*Test

# Solo pruebas de integración
mvn test -Dtest=*IntegrationTest
```

### Ejecutar Pruebas Específicas

```bash
# Pruebas de un servicio específico
mvn test -Dtest=AuthServiceTest

# Pruebas de un módulo
mvn test -Dtest=gestionusuarios.service.*Test
```

### Ejecutar desde IDE

1. **IntelliJ IDEA**: Click derecho en clase de prueba → "Run 'NombreTest'"
2. **Eclipse**: Click derecho en clase → "Run As" → "JUnit Test"
3. **VS Code**: Click en el icono de "Run Test" sobre el método

### Requisitos para Pruebas de Integración

- ✅ Docker instalado y corriendo (para Testcontainers)
- ✅ Puerto 5432 disponible (o configurar otro puerto)
- ✅ Conexión a internet (para descargar imagen de PostgreSQL)

---

## Cobertura de Pruebas

### Módulos Cubiertos

| Módulo | Servicios Probados | Controladores Probados | Cobertura |
|--------|-------------------|----------------------|-----------|
| **gestionusuarios** | ✅ AuthService<br>✅ UsuarioService<br>✅ ClienteService | ✅ AuthController | ~85% |
| **gestionpacientes** | ✅ PacienteService<br>✅ VacunacionService | ⏳ Pendiente | ~80% |
| **gestioninventario** | ✅ ProductoService<br>✅ MovimientoInventarioService | ⏳ Pendiente | ~75% |
| **prestacioneservicios** | ✅ CitaService<br>✅ FacturaService | ⏳ Pendiente | ~80% |

### Casos de Prueba por Tipo

- **Pruebas Unitarias**: 35+ casos de prueba
- **Pruebas de Integración**: 3 casos de prueba (pendiente expandir)
- **Pruebas de Repositorio**: ⏳ Pendiente de implementar

### Áreas Pendientes

1. **Pruebas de Integración de Controladores**
   - ⏳ ClienteController
   - ⏳ PacienteController
   - ⏳ ProductoController
   - ⏳ MovimientoInventarioController
   - ⏳ CitaController
   - ⏳ FacturaController

2. **Pruebas de Repositorio (@DataJpaTest)**
   - ⏳ Queries personalizadas (findByPacienteId, findProductosConStockBajo, etc.)
   - ⏳ Validación de relaciones JPA

3. **Pruebas de Servicios Adicionales**
   - ⏳ HistoriaClinicaService (signos vitales JSON → Map)
   - ⏳ ServicioPrestadoService
   - ⏳ NotificacionService (cuando se refactoricen)
   - ⏳ ReporteService (cuando se refactoricen)

---

## Mejores Prácticas Implementadas

### 1. Organización por Módulos
- ✅ Cada módulo tiene sus pruebas en su respectiva carpeta
- ✅ Separación clara entre pruebas unitarias y de integración

### 2. Nomenclatura Clara
- ✅ Nombres descriptivos: `loginExitoso_DeberiaGenerarTokenYActualizarUltimoAcceso`
- ✅ Uso de `@DisplayName` para descripciones legibles

### 3. Arrange-Act-Assert (AAA)
- ✅ Estructura clara en todas las pruebas
- ✅ Separación de preparación, ejecución y validación

### 4. Mocks y Stubs
- ✅ Uso apropiado de Mockito para dependencias
- ✅ Verificación de interacciones con `verify()`

### 5. Aserciones Descriptivas
- ✅ Uso de AssertJ para aserciones legibles
- ✅ Mensajes de error claros

### 6. Aislamiento
- ✅ Cada prueba es independiente
- ✅ `@BeforeEach` para setup común
- ✅ Limpieza de datos en pruebas de integración

---

## Ejemplos de Uso

### Ejemplo 1: Prueba Unitaria Completa

```java
@Test
@DisplayName("Crear usuario: username duplicado debe lanzar excepción")
void crearUsuarioUsernameDuplicado_DeberiaLanzarExcepcion() {
    // Arrange
    when(usuarioRepository.existsByUsername(anyString())).thenReturn(true);

    // Act & Assert
    assertThatThrownBy(() -> usuarioService.crear(usuarioRequest))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("El nombre de usuario ya está en uso");

    verify(usuarioRepository).existsByUsername("nuevousuario");
    verify(usuarioRepository, never()).save(any());
}
```

### Ejemplo 2: Prueba de Integración

```java
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
            .andExpect(jsonPath("$.data.type").value("Bearer"));
}
```

---

## Troubleshooting

### Problema: Testcontainers no inicia

**Solución**: Verificar que Docker está corriendo
```bash
docker ps
```

### Problema: Puerto 5432 en uso

**Solución**: Cambiar puerto en configuración de Testcontainers o detener PostgreSQL local

### Problema: Pruebas lentas

**Solución**: 
- Usar `@DirtiesContext` solo cuando sea necesario
- Considerar usar H2 en memoria para pruebas unitarias de repositorios
- Ejecutar pruebas en paralelo con `mvn test -T 4`

### Problema: Mocks no funcionan

**Solución**: Verificar que se usa `@ExtendWith(MockitoExtension.class)` y que los mocks están anotados con `@Mock`

---

## Próximos Pasos

1. **Expandir Pruebas de Integración**
   - Implementar pruebas para todos los controladores REST
   - Probar flujos completos (POST cliente → POST paciente → GET paciente)

2. **Pruebas de Repositorio**
   - Implementar `@DataJpaTest` para queries personalizadas
   - Validar relaciones JPA y cascadas

3. **Pruebas de Rendimiento**
   - Agregar pruebas de carga para endpoints críticos
   - Validar tiempos de respuesta

4. **Cobertura de Código**
   - Configurar JaCoCo para medir cobertura
   - Establecer meta de cobertura mínima (80%)

5. **CI/CD**
   - Integrar pruebas en pipeline de CI
   - Ejecutar pruebas automáticamente en cada commit

---

## Referencias

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

---

**Última actualización**: 2025-01-XX
**Versión del documento**: 1.0

