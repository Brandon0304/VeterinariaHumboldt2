# Sistema Clínico Veterinario - Backend

Sistema de gestión para clínica veterinaria desarrollado con arquitectura monolito-modular y estructura por capas.

## 🛠️ Stack Tecnológico

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Data JPA**: (Hibernate)
- **PostgreSQL**: 15.x
- **Flyway**: Migraciones de base de datos
- **Maven**: Gestión de dependencias
- **JWT**: Autenticación

## 📋 Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- PostgreSQL 15.x
- IntelliJ IDEA (recomendado)

## 🚀 Configuración Inicial

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd proyectoVeterinaria
```
### 2. Preparar la base de datos PostgreSQL

1. Inicia tu servidor PostgreSQL local.
2. Conéctate con un usuario administrador (por ejemplo `postgres`).
3. Crea el usuario y la base de datos que usará la aplicación:

```sql
CREATE ROLE vet_admin WITH LOGIN PASSWORD 'Petrico123';
CREATE DATABASE veterinaria_db OWNER vet_admin;
GRANT ALL PRIVILEGES ON DATABASE veterinaria_db TO vet_admin;
```

> Si prefieres otros datos, recuerda cambiarlos también en `src/main/resources/application.yml`.

### 3. Ejecutar migraciones de Flyway

Las migraciones se ejecutan automáticamente al iniciar la aplicación. También puedes ejecutarlas manualmente:

```bash
mvn flyway:migrate
```

### 4. Compilar y ejecutar la aplicación

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

O desde IntelliJ IDEA:
- Abrir el proyecto
- Ejecutar la clase `VeterinariaApplication.java`

## 📁 Estructura del Proyecto

```
veterinaria-backend/
├── src/
│   ├── main/
│   │   ├── java/com/tuorg/veterinaria/
│   │   │   ├── config/          # Configuraciones globales
│   │   │   ├── common/           # DTOs, excepciones, utilidades
│   │   │   ├── gestionusuarios/ # Módulo de gestión de usuarios
│   │   │   ├── gestionpacientes/# Módulo de gestión de pacientes
│   │   │   ├── gestioncitas/     # Módulo de gestión de citas
│   │   │   ├── gestioninventario/# Módulo de gestión de inventario
│   │   │   ├── prestacioneservicios/# Módulo de prestación de servicios
│   │   │   ├── notificaciones/  # Módulo de notificaciones
│   │   │   ├── configuracion/   # Módulo de configuración del sistema
│   │   │   └── reportes/        # Módulo de reportes y estadísticas
│   │   └── resources/
│   │       ├── application.yml  # Configuración de la aplicación
│   │       └── db/migration/     # Scripts de migración Flyway
│   └── test/                     # Pruebas unitarias e integración
├── pom.xml                       # Configuración de Maven
└── README.md                     # Este archivo
```

## 🏗️ Arquitectura

### Monolito Modular por Capas

Cada módulo sigue la estructura:

```
modulo/
├── model/        # Entidades JPA
├── repository/   # Interfaces Spring Data JPA
├── service/      # Lógica de negocio
└── controller/   # Controladores REST
```

## 🔐 Autenticación

El sistema utiliza JWT (JSON Web Tokens) para autenticación.

### Endpoints principales:

- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/refresh` - Refrescar token

## 📊 Base de Datos

### Conexión

- **Host**: localhost
- **Puerto**: 5432
- **Base de datos**: veterinaria_db
- **Usuario**: vet_admin
- **Contraseña**: Petrico123 (o la que hayas configurado)

### Migraciones

Las migraciones se encuentran en `src/main/resources/db/migration/` y se ejecutan automáticamente al iniciar la aplicación.

## 🧪 Testing

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas de integración
mvn verify
```

## 📝 Patrones de Diseño Implementados

- **Singleton**: ParametroSistema
- **Strategy**: CanalEnvio (notificaciones)
- **Observer**: Eventos del sistema
- **Command**: Movimientos de inventario
- **Factory/Builder**: Factura, ServicioPrestado
- **Repository**: Spring Data JPA
- **Facade**: Reporte

## 🔍 Calidad de Código

El proyecto cumple con las reglas de SonarQube:
- Código completamente comentado
- Nombres descriptivos
- Estructura clara y organizada
- Manejo adecuado de excepciones
- Validaciones de entrada

## 📚 Documentación API

La documentación de la API se generará automáticamente con Swagger/OpenAPI (pendiente de implementar).

## 👥 Equipo

Proyecto desarrollado por equipo de 4 integrantes (4to semestre).

## 📄 Licencia

Este proyecto es de uso académico.

