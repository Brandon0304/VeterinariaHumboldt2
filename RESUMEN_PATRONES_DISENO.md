# Resumen de Patrones de Diseño - Ubicaciones

## 📍 Patrones Implementados

### 1. **Singleton**
- **Módulo**: `configuracion`
- **Clase**: `ConfigService.java`
- **Ubicación**: `src/main/java/com/tuorg/veterinaria/configuracion/service/ConfigService.java`
- **Nota**: Spring también implementa Singleton por defecto en todos los `@Service`, `@Component`, `@Repository`

---

### 2. **Observer / Event**
- **Módulo**: `notificaciones` y `reportes`
- **Clases**:
  - `NotificacionService.java` - Publica eventos
  - `NotificacionEventListener.java` - Escucha eventos de notificaciones
  - `ReporteService.java` - Publica eventos
  - `ReporteEventListener.java` - Escucha eventos de reportes
- **Ubicaciones**:
  - `src/main/java/com/tuorg/veterinaria/notificaciones/service/NotificacionService.java`
  - `src/main/java/com/tuorg/veterinaria/common/event/NotificacionEventListener.java`
  - `src/main/java/com/tuorg/veterinaria/reportes/service/ReporteService.java`
  - `src/main/java/com/tuorg/veterinaria/common/event/ReporteEventListener.java`

---

### 3. **Strategy**
- **Módulo**: `notificaciones`
- **Clase**: `NotificacionService.java`
- **Implementaciones**:
  - `CanalEnvio.java` (interfaz)
  - `CanalEmail.java`
  - `CanalSMS.java`
  - `CanalApp.java`
- **Ubicaciones**:
  - `src/main/java/com/tuorg/veterinaria/notificaciones/service/NotificacionService.java`
  - `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalEnvio.java`
  - `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalEmail.java`
  - `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalSMS.java`
  - `src/main/java/com/tuorg/veterinaria/notificaciones/model/CanalApp.java`

---

### 4. **Command**
- **Módulo**: `gestioninventario`
- **Clase**: `MovimientoInventarioService.java`
- **Método clave**: `revertirMovimiento()` - Implementa "undo"
- **Ubicación**: `src/main/java/com/tuorg/veterinaria/gestioninventario/service/MovimientoInventarioService.java`

---

### 5. **Factory / Builder**
- **Módulo**: `gestionfacturacion` y `prestacioneservicios`
- **Clases**:
  - `FacturaService.java` - Factory para crear facturas
  - `Factura.java` - Usa Builder (Lombok `@Builder`)
  - `ServicioPrestado.java` - Usa Builder (Lombok `@Builder`)
- **Ubicaciones**:
  - `src/main/java/com/tuorg/veterinaria/gestionfacturacion/service/FacturaService.java`
  - `src/main/java/com/tuorg/veterinaria/gestionfacturacion/model/Factura.java`
  - `src/main/java/com/tuorg/veterinaria/prestacioneservicios/model/ServicioPrestado.java`

---

### 6. **Repository**
- **Módulo**: Todos los módulos
- **Aplicación**: Spring Data JPA
- **Ejemplos**:
  - `UsuarioRepository.java` - `gestionusuarios/repository/`
  - `PacienteRepository.java` - `gestionpacientes/repository/`
  - `ProductoRepository.java` - `gestioninventario/repository/`
  - `FacturaRepository.java` - `gestionfacturacion/repository/`
  - Y 20+ repositorios más
- **Ubicación**: Todos extienden `JpaRepository<T, ID>` en sus respectivos módulos

---

### 7. **Facade**
- **Módulo**: `reportes`
- **Clase**: `ReporteService.java`
- **Ubicación**: `src/main/java/com/tuorg/veterinaria/reportes/service/ReporteService.java`
- **Descripción**: Simplifica la generación de reportes complejos agrupando múltiples consultas

---

## 📋 Patrones Adicionales

### 8. **DTO (Data Transfer Object)**
- **Módulo**: Todos los módulos
- **Ubicación**: Cada módulo tiene carpeta `dto/`
- **Ejemplos**:
  - `gestionusuarios/dto/LoginRequest.java`, `LoginResponse.java`
  - `gestionpacientes/dto/PacienteRequest.java`, `PacienteResponse.java`
  - `gestioninventario/dto/ProductoRequest.java`, `ProductoResponse.java`
  - Y 50+ DTOs más

---

### 9. **Dependency Injection**
- **Módulo**: Toda la aplicación
- **Aplicación**: Spring Framework
- **Ubicación**: Todos los servicios, controladores y componentes usan `@Autowired` en constructores

---

### 10. **Utility Class**
- **Módulo**: `common`
- **Clase**: `ValidationUtil.java`
- **Ubicación**: `src/main/java/com/tuorg/veterinaria/common/util/ValidationUtil.java`

---

### 11. **Exception Handler**
- **Módulo**: `common`
- **Clase**: `GlobalExceptionHandler.java`
- **Ubicación**: `src/main/java/com/tuorg/veterinaria/common/exception/GlobalExceptionHandler.java`

---

### 12. **Service Layer**
- **Módulo**: Todos los módulos
- **Aplicación**: Separación Controller → Service → Repository
- **Ubicación**: Cada módulo tiene carpeta `service/` con servicios de negocio

---

### 13. **Herencia JOINED (JPA)**
- **Módulo**: `gestionusuarios`
- **Clases**:
  - `Persona.java` (clase base)
  - `Usuario.java` (hereda de Persona)
  - `Cliente.java` (hereda de Usuario)
  - `UsuarioVeterinario.java` (hereda de Usuario)
  - `Secretario.java` (hereda de Usuario)
- **Ubicación**: `src/main/java/com/tuorg/veterinaria/gestionusuarios/model/`

---

## 📊 Resumen Visual

| Patrón | Módulo | Clase Principal |
|--------|--------|-----------------|
| Singleton | `configuracion` | `ConfigService` |
| Observer/Event | `notificaciones`, `reportes` | `NotificacionService`, `ReporteService` |
| Strategy | `notificaciones` | `NotificacionService` + `CanalEnvio` |
| Command | `gestioninventario` | `MovimientoInventarioService` |
| Factory/Builder | `gestionfacturacion` | `FacturaService`, `Factura` |
| Repository | Todos | `*Repository` (Spring Data JPA) |
| Facade | `reportes` | `ReporteService` |
| DTO | Todos | `*Request`, `*Response` |
| Dependency Injection | Todos | Todos los `@Service`, `@Controller` |
| Utility Class | `common` | `ValidationUtil` |
| Exception Handler | `common` | `GlobalExceptionHandler` |
| Service Layer | Todos | `*Service` |
| Herencia JOINED | `gestionusuarios` | `Persona`, `Usuario`, `Cliente` |

