# Resumen del Flujo del Código y Manejo de Excepciones

## 📋 Tabla de Contenidos
1. [Flujo de Autenticación (Login)](#flujo-de-autenticación-login)
2. [Flujo de Registro](#flujo-de-registro)
3. [Manejo de Excepciones](#manejo-de-excepciones)
4. [Jerarquía de Excepciones](#jerarquía-de-excepciones)
5. [Validaciones](#validaciones)
6. [Respuestas Estándar](#respuestas-estándar)

---

## 🔐 Flujo de Autenticación (Login)

### Diagrama de Flujo

```
Cliente HTTP
    ↓
AuthController.login()
    ↓
@Valid LoginRequest (Validación Bean Validation)
    ↓
AuthService.login()
    ↓
1. Buscar usuario por username
    ├─ No existe → BusinessException("Usuario no encontrado")
    └─ Existe → Continuar
    ↓
2. Verificar si usuario está activo
    ├─ Inactivo → BusinessException("Usuario inactivo")
    └─ Activo → Continuar
    ↓
3. Obtener rol del usuario (con manejo de errores)
    ├─ Error → Usar "SIN_ROL" como valor por defecto
    └─ Éxito → Usar nombre del rol
    ↓
4. Autenticar con Spring Security
    ├─ Credenciales inválidas → AuthenticationException
    │   └─ Convertida a BusinessException("Credenciales inválidas")
    └─ Éxito → Continuar
    ↓
5. Obtener UserDetails del usuario
    ↓
6. Generar token JWT
    ↓
7. Actualizar último acceso del usuario
    ↓
8. Construir LoginResponse con:
    - Token JWT
    - Tipo de token ("Bearer")
    - Datos del usuario (ID, username, nombre, apellido, correo, rol)
    ↓
9. Retornar ApiResponse.success() con LoginResponse
```

### Código Clave

**AuthController.java** (Líneas 55-66):
```java
@PostMapping("/login")
public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
    logger.info("🔐 Intento de login recibido - Username: {}", loginRequest.getUsername());
    try {
        LoginResponse tokenResponse = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        logger.info("✅ Login exitoso para usuario: {}", loginRequest.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", tokenResponse));
    } catch (Exception e) {
        logger.error("❌ Error en login para usuario {}: {}", loginRequest.getUsername(), e.getMessage(), e);
        throw e; // La excepción es capturada por GlobalExceptionHandler
    }
}
```

**AuthService.java** (Líneas 98-168):
```java
@Transactional
public LoginResponse login(String username, String password) {
    try {
        // 1. Verificar existencia del usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + username));
        
        // 2. Verificar si está activo
        if (!usuario.getActivo()) {
            throw new BusinessException("Usuario inactivo: " + username);
        }
        
        // 3. Obtener rol (con manejo de errores)
        String nombreRol = "SIN_ROL";
        try {
            Rol rol = usuario.getRol();
            if (rol != null && rol.getNombreRol() != null) {
                nombreRol = rol.getNombreRol();
            }
        } catch (Exception e) {
            logger.warn("No se pudo obtener el nombre del rol para usuario {}: {}", username, e.getMessage());
        }
        
        // 4. Autenticar con Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        
        // 5. Obtener UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // 6. Generar token JWT
        String token = tokenProvider.generateToken(userDetails);
        
        // 7. Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
        usuarioRepository.flush();
        
        // 8. Construir respuesta
        LoginResponse.UsuarioLoginResponse usuarioResponse = new LoginResponse.UsuarioLoginResponse(...);
        return new LoginResponse(token, "Bearer", usuarioResponse);
        
    } catch (AuthenticationException e) {
        // Convertir excepciones de Spring Security a BusinessException
        logger.error("Error de autenticación para usuario {}: {}", username, e.getMessage());
        throw new BusinessException("Credenciales inválidas: " + e.getMessage());
    } catch (Exception e) {
        // Capturar cualquier error inesperado
        logger.error("Error inesperado en login para usuario {}: {}", username, e.getMessage(), e);
        throw new BusinessException("Error al procesar el login: " + e.getMessage());
    }
}
```

---

## 📝 Flujo de Registro

### Diagrama de Flujo

```
Cliente HTTP
    ↓
AuthController.register()
    ↓
@Valid RegisterRequest (Validación Bean Validation)
    ↓
AuthService.register()
    ↓
1. Verificar que username no exista
    ├─ Existe → BusinessException("El nombre de usuario ya está en uso")
    └─ No existe → Continuar
    ↓
2. Verificar que email no exista
    ├─ Existe → BusinessException("El correo electrónico ya está en uso")
    └─ No existe → Continuar
    ↓
3. Validar contraseña (ValidationUtil.validatePassword)
    ├─ Contraseña < 8 caracteres → BusinessException
    └─ Válida → Continuar
    ↓
4. Determinar rol (del request o "CLIENTE" por defecto)
    ↓
5. Buscar rol en base de datos
    ├─ No existe → BusinessException("El rol 'X' no está configurado")
    └─ Existe → Continuar
    ↓
6. Crear nuevo Usuario
    - Codificar contraseña con PasswordEncoder
    - Asignar datos del request
    - Asignar rol
    - Activar usuario por defecto
    ↓
7. Guardar usuario en base de datos
    ↓
8. Retornar ApiResponse.success()
```

### Código Clave

**AuthService.java** (Líneas 180-216):
```java
@Transactional
public Usuario register(RegisterRequest request) {
    String username = request.getUsername();
    String email = request.getEmail();

    // 1. Verificar username único
    if (usuarioRepository.existsByUsername(username)) {
        throw new BusinessException("El nombre de usuario ya está en uso");
    }

    // 2. Verificar email único
    if (usuarioRepository.existsByCorreo(email)) {
        throw new BusinessException("El correo electrónico ya está en uso");
    }

    // 3. Validar contraseña
    ValidationUtil.validatePassword(request.getPassword());

    // 4. Determinar rol
    String nombreRol = (request.getRol() != null && !request.getRol().trim().isEmpty()) 
            ? request.getRol().trim().toUpperCase() 
            : "CLIENTE";

    // 5. Buscar rol
    Rol rol = rolRepository.findByNombreRol(nombreRol)
            .orElseThrow(() -> new BusinessException("El rol '" + nombreRol + "' no está configurado en el sistema"));

    // 6. Crear usuario
    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    usuario.setCorreo(email);
    usuario.setNombre(request.getNombre());
    usuario.setApellido(request.getApellido());
    usuario.setActivo(true);
    usuario.setRol(rol);

    // 7. Guardar
    return usuarioRepository.save(usuario);
}
```

---

## ⚠️ Manejo de Excepciones

### Arquitectura de Manejo de Excepciones

```
Excepción lanzada en cualquier capa
    ↓
GlobalExceptionHandler (@RestControllerAdvice)
    ↓
Identifica el tipo de excepción
    ↓
    ├─ ResourceNotFoundException
    │   └─ HTTP 404 (NOT_FOUND)
    │
    ├─ BusinessException
    │   └─ HTTP 400 (BAD_REQUEST)
    │
    ├─ MethodArgumentNotValidException
    │   └─ HTTP 400 (BAD_REQUEST) + detalles de validación
    │
    └─ Exception (genérica)
        └─ HTTP 500 (INTERNAL_SERVER_ERROR)
    ↓
Construye ApiResponse con formato estándar
    ↓
Retorna ResponseEntity con código HTTP apropiado
```

### GlobalExceptionHandler

**Ubicación**: `src/main/java/com/tuorg/veterinaria/common/exception/GlobalExceptionHandler.java`

**Métodos implementados**:

1. **handleResourceNotFoundException** (Líneas 36-41)
   - Captura: `ResourceNotFoundException`
   - HTTP Status: `404 NOT_FOUND`
   - Uso: Cuando un recurso no se encuentra en la base de datos

2. **handleBusinessException** (Líneas 49-54)
   - Captura: `BusinessException`
   - HTTP Status: `400 BAD_REQUEST`
   - Uso: Errores de reglas de negocio, validaciones de dominio

3. **handleValidationExceptions** (Líneas 62-77)
   - Captura: `MethodArgumentNotValidException`
   - HTTP Status: `400 BAD_REQUEST`
   - Uso: Errores de validación de Bean Validation (@Valid, @NotNull, @Size, etc.)
   - Incluye: Mapa con detalles de errores por campo

4. **handleGenericException** (Líneas 85-91)
   - Captura: `Exception` (cualquier excepción no manejada)
   - HTTP Status: `500 INTERNAL_SERVER_ERROR`
   - Uso: Errores inesperados del sistema

### Código del GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Recurso no encontrado: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        logger.warn("Error de negocio: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Error inesperado: ", ex);
        ApiResponse<Object> response = ApiResponse.error(
                "Ocurrió un error inesperado. Por favor, contacte al administrador.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

## 🏗️ Jerarquía de Excepciones

### Excepciones Personalizadas

#### 1. BusinessException
**Ubicación**: `src/main/java/com/tuorg/veterinaria/common/exception/BusinessException.java`

**Características**:
- Extiende: `RuntimeException`
- Propósito: Errores de reglas de negocio y validaciones de dominio
- Campos opcionales:
  - `errorCode`: Código de error personalizado
  - `message`: Mensaje descriptivo del error

**Constructores**:
```java
BusinessException(String message)
BusinessException(String message, String errorCode)
BusinessException(String message, Throwable cause)
BusinessException(String message, String errorCode, Throwable cause)
```

**Ejemplos de uso**:
```java
// Usuario no encontrado
throw new BusinessException("Usuario no encontrado: " + username);

// Usuario inactivo
throw new BusinessException("Usuario inactivo: " + username);

// Credenciales inválidas
throw new BusinessException("Credenciales inválidas: " + e.getMessage());

// Username duplicado
throw new BusinessException("El nombre de usuario ya está en uso");

// Email duplicado
throw new BusinessException("El correo electrónico ya está en uso");

// Rol no configurado
throw new BusinessException("El rol 'ADMIN' no está configurado en el sistema");
```

#### 2. ResourceNotFoundException
**Ubicación**: `src/main/java/com/tuorg/veterinaria/common/exception/ResourceNotFoundException.java`

**Características**:
- Extiende: `RuntimeException`
- Propósito: Recurso no encontrado en la base de datos
- Campos:
  - `resourceName`: Nombre del recurso (ej: "Usuario", "Paciente")
  - `fieldName`: Nombre del campo usado para buscar (ej: "id", "username")
  - `fieldValue`: Valor del campo

**Constructores**:
```java
ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue)
ResourceNotFoundException(String message)
```

**Ejemplo de uso**:
```java
Usuario usuario = usuarioRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
```

---

## ✅ Validaciones

### Niveles de Validación

#### 1. Validación a Nivel de DTO (Bean Validation)

**Anotaciones utilizadas**:
- `@Valid`: Activa la validación del objeto
- `@NotNull`: Campo no puede ser nulo
- `@NotBlank`: Campo no puede estar vacío o en blanco
- `@Size(min = X, max = Y)`: Longitud del string
- `@Email`: Formato de correo electrónico
- `@Positive`: Número debe ser positivo
- `@DecimalMin`: Valor mínimo decimal

**Ejemplo - RegisterRequest**:
```java
public class RegisterRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 60, message = "El nombre de usuario debe tener entre 3 y 60 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;
    
    // ... otros campos
}
```

**Manejo**: Si la validación falla, Spring lanza `MethodArgumentNotValidException`, que es capturada por `GlobalExceptionHandler.handleValidationExceptions()`.

#### 2. Validación a Nivel de Servicio (ValidationUtil)

**Ubicación**: `src/main/java/com/tuorg/veterinaria/common/util/ValidationUtil.java`

**Métodos disponibles**:
- `validateEmail(String email)`: Valida formato de correo
- `validatePhone(String phone)`: Valida formato de teléfono
- `validateUsername(String username)`: Valida longitud de username
- `validatePassword(String password)`: Valida longitud mínima de contraseña (8 caracteres)
- `validatePositiveNumber(double value, String fieldName)`: Valida número positivo
- `validateNonNegativeNumber(double value, String fieldName)`: Valida número no negativo

**Ejemplo de uso**:
```java
// En AuthService.register()
ValidationUtil.validatePassword(request.getPassword());
// Si la contraseña es < 8 caracteres, lanza BusinessException
```

#### 3. Validación a Nivel de Base de Datos

**Constraints en PostgreSQL**:
- `UNIQUE`: Campos únicos (username, email, SKU, etc.)
- `CHECK`: Validaciones de rango (peso > 0, stock >= 0, etc.)
- `NOT NULL`: Campos obligatorios
- `FOREIGN KEY`: Integridad referencial

**Manejo**: Si falla una constraint, Spring lanza `DataIntegrityViolationException`, que puede ser capturada y convertida a `BusinessException`.

---

## 📤 Respuestas Estándar

### ApiResponse

**Ubicación**: `src/main/java/com/tuorg/veterinaria/common/dto/ApiResponse.java`

**Estructura**:
```json
{
    "success": true/false,
    "message": "Mensaje descriptivo",
    "data": { ... },  // Objeto de respuesta (puede ser null)
    "timestamp": "2025-11-21T04:23:20.854073"
}
```

**Métodos estáticos**:
```java
// Respuesta exitosa
ApiResponse.success("Mensaje de éxito", data)

// Respuesta de error
ApiResponse.error("Mensaje de error")
```

**Ejemplos de Respuestas**:

1. **Login Exitoso** (HTTP 200):
```json
{
    "success": true,
    "message": "Login exitoso",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "tokenType": "Bearer",
        "usuario": {
            "idUsuario": 1,
            "username": "testuser",
            "nombre": "Test",
            "apellido": "User",
            "correo": "test@example.com",
            "rol": "CLIENTE"
        }
    },
    "timestamp": "2025-11-21T04:23:20.854073"
}
```

2. **Error de Negocio** (HTTP 400):
```json
{
    "success": false,
    "message": "Usuario no encontrado: testuser",
    "data": null,
    "timestamp": "2025-11-21T04:23:20.854073"
}
```

3. **Error de Validación** (HTTP 400):
```json
{
    "success": false,
    "message": "Error de validación",
    "data": {
        "password": "La contraseña debe tener al menos 8 caracteres",
        "email": "El formato del correo electrónico no es válido"
    },
    "timestamp": "2025-11-21T04:23:20.854073"
}
```

4. **Recurso No Encontrado** (HTTP 404):
```json
{
    "success": false,
    "message": "Usuario no encontrado con id : '999'",
    "data": null,
    "timestamp": "2025-11-21T04:23:20.854073"
}
```

5. **Error Interno** (HTTP 500):
```json
{
    "success": false,
    "message": "Ocurrió un error inesperado. Por favor, contacte al administrador.",
    "data": null,
    "timestamp": "2025-11-21T04:23:20.854073"
}
```

---

## 🔄 Flujo Completo de una Petición

### Ejemplo: Login con Credenciales Incorrectas

```
1. Cliente envía POST /api/auth/login
   {
     "username": "testuser",
     "password": "passwordIncorrecta"
   }
   ↓
2. AuthController recibe la petición
   - @Valid valida LoginRequest (pasa)
   ↓
3. AuthService.login() es llamado
   ↓
4. Usuario existe y está activo ✓
   ↓
5. authenticationManager.authenticate() falla
   - Lanza AuthenticationException
   ↓
6. catch (AuthenticationException e) en AuthService
   - Convierte a BusinessException("Credenciales inválidas")
   ↓
7. Excepción propagada a AuthController
   - catch (Exception e) registra el error
   - throw e; propaga la excepción
   ↓
8. GlobalExceptionHandler captura BusinessException
   - handleBusinessException() es llamado
   ↓
9. Se construye ApiResponse.error()
   ↓
10. Se retorna HTTP 400 con:
    {
      "success": false,
      "message": "Credenciales inválidas: ...",
      "data": null,
      "timestamp": "..."
    }
```

---

## 📊 Resumen de Códigos HTTP

| Código | Significado | Cuándo se usa | Excepción |
|--------|-------------|---------------|-----------|
| 200 | OK | Operación exitosa | - |
| 400 | Bad Request | Error de negocio o validación | `BusinessException`, `MethodArgumentNotValidException` |
| 401 | Unauthorized | No autenticado | (Manejado por Spring Security) |
| 403 | Forbidden | Sin permisos | (Manejado por Spring Security) |
| 404 | Not Found | Recurso no encontrado | `ResourceNotFoundException` |
| 500 | Internal Server Error | Error inesperado | `Exception` (genérica) |

---

## 🎯 Buenas Prácticas Implementadas

1. **Separación de Responsabilidades**:
   - Controller: Recibe peticiones, delega a Service
   - Service: Lógica de negocio, validaciones
   - Repository: Acceso a datos
   - Exception Handler: Manejo centralizado de excepciones

2. **Logging Consistente**:
   - INFO: Operaciones exitosas
   - WARN: Errores de negocio esperados
   - ERROR: Errores inesperados con stack trace

3. **Mensajes de Error Descriptivos**:
   - No revelan información sensible
   - Son claros para el usuario final
   - Incluyen contexto suficiente para debugging

4. **Transacciones**:
   - `@Transactional` en métodos de servicio
   - Rollback automático en caso de excepción

5. **Validación en Múltiples Capas**:
   - DTO: Bean Validation
   - Service: ValidationUtil
   - Database: Constraints

---

## 📝 Notas Finales

- Todas las excepciones son capturadas y convertidas a respuestas HTTP estándar
- El formato de respuesta es consistente en toda la aplicación (`ApiResponse`)
- Los logs ayudan a rastrear errores sin exponer detalles al cliente
- Las validaciones previenen errores antes de llegar a la base de datos
- El manejo de excepciones es centralizado y fácil de mantener

