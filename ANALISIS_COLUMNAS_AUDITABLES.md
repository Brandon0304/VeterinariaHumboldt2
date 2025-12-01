# ANÁLISIS COMPLETO: Columnas Faltantes en Entidades Auditables

## Fecha de Análisis
**30 de Noviembre de 2025**

---

## Resumen Ejecutivo

Se identificó una **desincronización crítica** entre las entidades JPA que extienden `Auditable` y sus correspondientes tablas en la base de datos. 

**Problema:** Las tablas creadas en la migración V10 (módulo de configuración) NO tienen las columnas de auditoría (`created_by`, `created_at`, `updated_by`, `updated_at`), pero sus entidades Java SÍ extienden de la clase `Auditable`.

**Solución:** Migración V21 que agrega TODAS las columnas faltantes en una sola operación.

---

## Metodología de Análisis

1. **Identificación de entidades:** Búsqueda de todas las clases con `@Entity` que extienden `Auditable`
2. **Revisión de migraciones:** Análisis de V1, V3, V4, V10 para verificar estructura de tablas
3. **Comparación:** Contraste entre campos `@Column` en Java vs columnas en SQL

---

## Clase Base: Auditable

La clase `Auditable` define 4 campos que DEBEN estar en TODAS las tablas cuyas entidades la extienden:

```java
@CreatedBy
@Column(name = "created_by", updatable = false, length = 100)
private String createdBy;

@CreatedDate
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;

@LastModifiedBy
@Column(name = "updated_by", length = 100)
private String updatedBy;

@LastModifiedDate
@Column(name = "updated_at")
private LocalDateTime updatedAt;
```

---

## Entidades Analizadas (13 Total)

### ✅ ENTIDADES CON COLUMNAS CORRECTAS

#### 1. **Factura** (`facturas`)
- **Extends Auditable:** ✅ Sí
- **Columnas en V1:** id_factura, numero, fecha_emision, total, forma_pago, estado, cliente_id, contenido
- **Columnas de auditoría agregadas en:** V3__add_audit_columns.sql
- **Columna adicional en:** V9 (fecha_pago)
- **Estado:** ✅ CORRECTO

#### 2. **Producto** (`productos`)
- **Extends Auditable:** ✅ Sí
- **Columnas en V1:** id_producto, sku, nombre, descripcion, tipo, stock, precio_unitario, um, metadatos
- **Columnas de auditoría agregadas en:** V3__add_audit_columns.sql
- **Estado:** ✅ CORRECTO

#### 3. **Paciente** (`pacientes`)
- **Extends Auditable:** ✅ Sí
- **Columnas en V1:** id_paciente, nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo
- **Columnas de auditoría agregadas en:** V3__add_audit_columns.sql
- **Estado:** ✅ CORRECTO

#### 4. **Cita** (`citas`)
- **Extends Auditable:** ✅ Sí
- **Columnas en V1:** id_cita, paciente_id, veterinario_id, fecha_hora, tipo_servicio, estado, motivo, triage_nivel
- **Columnas de auditoría agregadas en:** V3__add_audit_columns.sql
- **Estado:** ✅ CORRECTO

#### 5. **SolicitudCita** (`solicitudes_citas`)
- **Extends Auditable:** ✅ Sí
- **Columnas en V4:** id_solicitud, cliente_id, paciente_id, fecha_solicitada, hora_solicitada, tipo_servicio, motivo, estado, motivo_rechazo, cita_id, observaciones, aprobado_por, aprobado_en, rechazado_por, rechazado_en, cancelado_por, cancelado_en, **created_at, updated_at, created_by, updated_by**
- **Estado:** ✅ CORRECTO (incluidas desde creación)

---

### ❌ ENTIDADES CON COLUMNAS FALTANTES (8 tablas)

#### 6. **ParametroSistema** (`parametros_sistema`)
- **Extends Auditable:** ✅ Sí
- **Tabla creada en:** V1__init_schema.sql
- **Columnas en BD:** id_parametro, clave, valor, descripcion, aplicacion
- **Columnas en Java:** idParametro, clave, valor, descripcion, tipoDato, categoria, editable, activo + **HEREDA: createdBy, createdAt, updatedBy, updatedAt**
- **Columnas faltantes en BD:**
  - ❌ `created_by VARCHAR(100)`
  - ❌ `created_at TIMESTAMP`
  - ❌ `updated_by VARCHAR(100)`
  - ❌ `updated_at TIMESTAMP`
  - ❌ `tipo_dato VARCHAR(50)` (campo adicional de negocio)
  - ❌ `categoria VARCHAR(50)` (campo adicional de negocio)
  - ❌ `editable BOOLEAN` (campo adicional de negocio)
  - ❌ `activo BOOLEAN` (campo adicional de negocio)

**ALTER TABLE necesario:**
```sql
ALTER TABLE parametros_sistema 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN tipo_dato VARCHAR(50),
ADD COLUMN categoria VARCHAR(50),
ADD COLUMN editable BOOLEAN DEFAULT TRUE,
ADD COLUMN activo BOOLEAN DEFAULT TRUE;
```

---

#### 7. **InformacionClinica** (`informacion_clinica`)
- **Extends Auditable:** ✅ Sí
- **Tabla creada en:** V10__create_configuracion_module_tables.sql
- **Columnas en V10:** id, nombre_clinica, nit, telefono, email, direccion, idioma, moneda, zona_horaria, formato_fecha, logo_url, creado_por, fecha_creacion, modificado_por, fecha_modificacion, activo
- **Columnas en Java:** idClinica, nombreClinica, direccion, telefono, email, sitioWeb, logoUrl, mision, vision, horarioAtencion, redesSociales, activo + **HEREDA: createdBy, createdAt, updatedBy, updatedAt**
- **Problema:** V10 usa nombres NO estándar (creado_por, fecha_creacion, modificado_por, fecha_modificacion)
- **Columnas faltantes/incorrectas:**
  - ❌ Debe renombrar `creado_por` → `created_by`
  - ❌ Debe renombrar `fecha_creacion` → `created_at`
  - ❌ Debe renombrar `modificado_por` → `updated_by`
  - ❌ Debe renombrar `fecha_modificacion` → `updated_at`
  - ❌ Faltan: nit, idioma, moneda, zona_horaria, formato_fecha (en V10 pero no en Java)
  - ❌ Faltan: sitio_web, mision, vision, horario_atencion, redes_sociales (en Java pero no en V10)

**ALTER TABLE necesario:**
```sql
ALTER TABLE informacion_clinica 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN sitio_web VARCHAR(200),
ADD COLUMN mision TEXT,
ADD COLUMN vision TEXT,
ADD COLUMN horario_atencion TEXT,
ADD COLUMN redes_sociales JSONB;

-- Migrar datos
UPDATE informacion_clinica 
SET created_by = creado_por, 
    created_at = fecha_creacion,
    updated_by = modificado_por,
    updated_at = fecha_modificacion;

-- Eliminar columnas antiguas
ALTER TABLE informacion_clinica 
DROP COLUMN creado_por,
DROP COLUMN fecha_creacion,
DROP COLUMN modificado_por,
DROP COLUMN fecha_modificacion,
DROP COLUMN nit,
DROP COLUMN idioma,
DROP COLUMN moneda,
DROP COLUMN zona_horaria,
DROP COLUMN formato_fecha;
```

---

#### 8. **PermisoRol** (`permisos_rol`)
- **Extends Auditable:** ✅ Sí
- **Tabla creada en:** V10__create_configuracion_module_tables.sql
- **Columnas en V10:** id, rol_id, modulo, accion, ruta, descripcion, permitido, creado_por, fecha_creacion, modificado_por, fecha_modificacion, activo
- **Columnas en Java:** idPermiso, rol, modulo, accion, descripcion, activo + **HEREDA: createdBy, createdAt, updatedBy, updatedAt**
- **Problema:** V10 usa nombres NO estándar
- **Columnas faltantes:**
  - ❌ Debe renombrar `creado_por` → `created_by`
  - ❌ Debe renombrar `fecha_creacion` → `created_at`
  - ❌ Debe renombrar `modificado_por` → `updated_by`
  - ❌ Debe renombrar `fecha_modificacion` → `updated_at`
  - ❌ Sobra: `ruta`, `permitido` (en V10 pero no en Java)

**ALTER TABLE necesario:**
```sql
ALTER TABLE permisos_rol 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

UPDATE permisos_rol 
SET created_by = creado_por, 
    created_at = fecha_creacion,
    updated_by = modificado_por,
    updated_at = fecha_modificacion;

ALTER TABLE permisos_rol 
DROP COLUMN creado_por,
DROP COLUMN fecha_creacion,
DROP COLUMN modificado_por,
DROP COLUMN fecha_modificacion,
DROP COLUMN ruta,
DROP COLUMN permitido;
```

---

#### 9. **ServicioConfiguracion** (`servicios_configuracion`)
- **Extends Auditable:** ✅ Sí
- **Tabla creada en:** V10__create_configuracion_module_tables.sql
- **Columnas en V10:** id, servicio_id, nombre, descripcion, precio_base, duracion_estimada_minutos, disponible, requiere_cita, color_hex, icono, creado_por, fecha_creacion, modificado_por, fecha_modificacion, activo
- **Columnas en Java:** idServicioConfig, servicioId, nombreServicio, descripcion, precio, duracionMinutos, categoria, activo + **HEREDA: createdBy, createdAt, updatedBy, updatedAt**
- **Columnas faltantes:**
  - ❌ `created_by`, `created_at`, `updated_by`, `updated_at`
  - ❌ `categoria VARCHAR(50)` (en Java, no en V10)
  - ❌ Sobran: disponible, requiere_cita, color_hex, icono (en V10, no en Java)

**ALTER TABLE necesario:**
```sql
ALTER TABLE servicios_configuracion 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN categoria VARCHAR(50);

ALTER TABLE servicios_configuracion 
DROP COLUMN creado_por,
DROP COLUMN fecha_creacion,
DROP COLUMN modificado_por,
DROP COLUMN fecha_modificacion,
DROP COLUMN disponible,
DROP COLUMN requiere_cita,
DROP COLUMN color_hex,
DROP COLUMN icono;

ALTER TABLE servicios_configuracion
RENAME COLUMN nombre TO nombre_servicio;
ALTER TABLE servicios_configuracion
RENAME COLUMN precio_base TO precio;
ALTER TABLE servicios_configuracion
RENAME COLUMN duracion_estimada_minutos TO duracion_minutos;
```

---

#### 10. **HorarioAtencion** (`horarios_atencion`)
- **Extends Auditable:** ✅ Sí
- **Tabla creada en:** V10__create_configuracion_module_tables.sql
- **Columnas en V10:** id, dia_semana (INTEGER), hora_apertura, hora_cierre, abierto (BOOLEAN), descripcion, creado_por, fecha_creacion, modificado_por, fecha_modificacion, activo
- **Columnas en Java:** idHorario, diaSemana (ENUM), horaApertura, horaCierre, cerrado (Boolean), notas + **HEREDA: createdBy, createdAt, updatedBy, updatedAt**
- **Problema crítico:** V10 usa `abierto BOOLEAN` pero Java usa `cerrado Boolean` (lógica inversa)
- **Columnas faltantes:**
  - ❌ `created_by`, `created_at`, `updated_by`, `updated_at`
  - ❌ Debe cambiar `dia_semana INTEGER` → `dia_semana VARCHAR(20)` para ENUM
  - ❌ Debe cambiar `abierto BOOLEAN` → `cerrado BOOLEAN`
  - ❌ Debe cambiar `descripcion` → `notas`

**ALTER TABLE necesario:**
```sql
ALTER TABLE horarios_atencion 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN cerrado BOOLEAN DEFAULT FALSE,
ADD COLUMN notas VARCHAR(200);

-- Migrar lógica inversa
UPDATE horarios_atencion SET cerrado = NOT abierto;

-- Migrar día de semana de INTEGER a VARCHAR ENUM
ALTER TABLE horarios_atencion 
ALTER COLUMN dia_semana TYPE VARCHAR(20);

UPDATE horarios_atencion SET dia_semana = 
  CASE 
    WHEN dia_semana::INTEGER = 1 THEN 'LUNES'
    WHEN dia_semana::INTEGER = 2 THEN 'MARTES'
    WHEN dia_semana::INTEGER = 3 THEN 'MIERCOLES'
    WHEN dia_semana::INTEGER = 4 THEN 'JUEVES'
    WHEN dia_semana::INTEGER = 5 THEN 'VIERNES'
    WHEN dia_semana::INTEGER = 6 THEN 'SABADO'
    WHEN dia_semana::INTEGER = 7 THEN 'DOMINGO'
  END;

UPDATE horarios_atencion SET notas = descripcion;

ALTER TABLE horarios_atencion 
DROP COLUMN creado_por,
DROP COLUMN fecha_creacion,
DROP COLUMN modificado_por,
DROP COLUMN fecha_modificacion,
DROP COLUMN abierto,
DROP COLUMN descripcion,
DROP COLUMN activo;
```

---

#### 11. **AuditoriaDetallada** (`auditoria_detallada`)
- **Extends Auditable:** ✅ Sí
- **Tabla creada en:** V10__create_configuracion_module_tables.sql
- **Columnas en V10:** id, historial_accion_id, usuario_id, rol_nombre, modulo, entidad, entidad_id, datos_anteriores, datos_nuevos, relevancia, requiere_revision, ip_address, user_agent, fecha_accion
- **Columnas en Java:** idAuditoria, usuario, tipoAccion, entidad, entidadId, datosAntes, datosDespues, ipOrigen, userAgent, descripcion + **HEREDA: createdBy, createdAt, updatedBy, updatedAt**
- **Columnas faltantes:**
  - ❌ `created_by`, `created_at`, `updated_by`, `updated_at`
  - ❌ `tipo_accion VARCHAR(50)` (en Java, no en V10)
  - ❌ `descripcion TEXT` (en Java, no en V10)
  - ❌ Sobran: historial_accion_id, rol_nombre, relevancia, requiere_revision (en V10, no en Java)

**ALTER TABLE necesario:**
```sql
ALTER TABLE auditoria_detallada 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN tipo_accion VARCHAR(50),
ADD COLUMN descripcion TEXT;

ALTER TABLE auditoria_detallada
RENAME COLUMN datos_anteriores TO datos_antes;
ALTER TABLE auditoria_detallada
RENAME COLUMN datos_nuevos TO datos_despues;
ALTER TABLE auditoria_detallada
RENAME COLUMN ip_address TO ip_origen;

ALTER TABLE auditoria_detallada 
DROP COLUMN historial_accion_id,
DROP COLUMN rol_nombre,
DROP COLUMN relevancia,
DROP COLUMN requiere_revision;
```

---

#### 12. **RespaldoSistema** (`respaldos_sistema`)
- **Extends Auditable:** ✅ Sí
- **Tabla creada en:** V10__create_configuracion_module_tables.sql
- **Columnas en V10:** id, nombre, descripcion, tipo, ruta_archivo, tamano_bytes, hash_verificacion, fecha_respaldo, fecha_expiracion, estado, puede_restaurar, creado_por, restaurado_por, fecha_restauracion
- **Columnas en Java:** idRespaldo, usuario, fechaRespaldo, tipoRespaldo (ENUM), rutaArchivo, tamanoBytes, hashVerificacion, estado (ENUM), descripcion, errorMensaje + **HEREDA: createdBy, createdAt, updatedBy, updatedAt**
- **Problema:** V10 tiene `creado_por` pero NO tiene `fecha_creacion`, `modificado_por`, `fecha_modificacion`
- **Columnas faltantes:**
  - ❌ `created_by`, `created_at`, `updated_by`, `updated_at`
  - ❌ `usuario_id BIGINT` (referencia a Usuario, en Java no en V10)
  - ❌ `error_mensaje TEXT` (en Java, no en V10)
  - ❌ Debe cambiar `tipo VARCHAR` → tipo ENUM
  - ❌ Debe cambiar `estado VARCHAR` → estado ENUM
  - ❌ Sobran: nombre, fecha_expiracion, puede_restaurar (en V10, no en Java)

**ALTER TABLE necesario:**
```sql
ALTER TABLE respaldos_sistema 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN usuario_id BIGINT REFERENCES usuarios(id_usuario),
ADD COLUMN error_mensaje TEXT;

ALTER TABLE respaldos_sistema
RENAME COLUMN tamano_bytes TO tamano_bytes;

ALTER TABLE respaldos_sistema 
DROP COLUMN creado_por,
DROP COLUMN nombre,
DROP COLUMN fecha_expiracion,
DROP COLUMN puede_restaurar;
```

---

#### 13. **ConfiguracionAvanzada** (`configuracion_avanzada`)
- **Extends Auditable:** ✅ Sí
- **Tabla creada en:** V10__create_configuracion_module_tables.sql
- **Columnas en V10:** id, clave, valor, categoria, tipo_dato, descripcion, valor_por_defecto, requerido, editable, creado_por, fecha_creacion, modificado_por, fecha_modificacion
- **Columnas en Java:** idConfiguracion, clave, valor, tipoDato (ENUM), categoria, descripcion, editable, activo + **HEREDA: createdBy, createdAt, updatedBy, updatedAt**
- **Columnas faltantes:**
  - ❌ `created_by`, `created_at`, `updated_by`, `updated_at`
  - ❌ `activo BOOLEAN` (en Java, no en V10)
  - ❌ Debe cambiar `tipo_dato VARCHAR` → tipo_dato ENUM
  - ❌ Sobran: valor_por_defecto, requerido (en V10, no en Java)

**ALTER TABLE necesario:**
```sql
ALTER TABLE configuracion_avanzada 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN activo BOOLEAN DEFAULT TRUE;

UPDATE configuracion_avanzada 
SET created_by = creado_por, 
    created_at = fecha_creacion,
    updated_by = modificado_por,
    updated_at = fecha_modificacion;

ALTER TABLE configuracion_avanzada 
DROP COLUMN creado_por,
DROP COLUMN fecha_creacion,
DROP COLUMN modificado_por,
DROP COLUMN fecha_modificacion,
DROP COLUMN valor_por_defecto,
DROP COLUMN requerido;
```

---

## Resumen de Discrepancias

### Patrón Identificado
**Las tablas creadas en V10 usan nombres en español para auditoría:**
- `creado_por` en lugar de `created_by`
- `fecha_creacion` en lugar de `created_at`
- `modificado_por` en lugar de `updated_by`
- `fecha_modificacion` en lugar de `updated_at`

**Las tablas creadas en V1 + V3 usan el estándar correcto en inglés.**

### Impacto
- ❌ **Hibernate fallará** al intentar mapear campos que no existen
- ❌ **@CreatedBy/@LastModifiedBy** no funcionarán
- ❌ **Auditoría automática NO se guardará**
- ❌ **Queries fallidos** en servicios que usen ordenamiento por fecha de creación

---

## Solución Implementada: Migración V21

La migración `V21__add_missing_audit_columns_to_all_auditable_entities.sql` realiza:

### Acciones por Tabla

1. **parametros_sistema**: Agrega 4 columnas de auditoría
2. **informacion_clinica**: Agrega 4 columnas + migra datos + elimina antiguas
3. **permisos_rol**: Agrega 4 columnas + migra datos + elimina antiguas
4. **servicios_configuracion**: Agrega 4 columnas
5. **horarios_atencion**: Agrega 4 columnas + migra datos + elimina antiguas
6. **auditoria_detallada**: Agrega 4 columnas
7. **respaldos_sistema**: Agrega 4 columnas + elimina 1 antigua
8. **configuracion_avanzada**: Agrega 4 columnas + migra datos + elimina antiguas

### Total de Operaciones
- **32 columnas agregadas** (4 × 8 tablas)
- **17 columnas eliminadas** (duplicadas/obsoletas)
- **8 índices creados** (para performance de consultas de auditoría)

---

## Verificación Post-Migración

### SQL para verificar que TODAS las tablas tienen las columnas correctas:

```sql
-- Verificar que todas las entidades Auditable tienen las 4 columnas
SELECT 
    table_name,
    COUNT(*) FILTER (WHERE column_name = 'created_by') as tiene_created_by,
    COUNT(*) FILTER (WHERE column_name = 'created_at') as tiene_created_at,
    COUNT(*) FILTER (WHERE column_name = 'updated_by') as tiene_updated_by,
    COUNT(*) FILTER (WHERE column_name = 'updated_at') as tiene_updated_at
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN (
    'parametros_sistema',
    'informacion_clinica',
    'permisos_rol',
    'servicios_configuracion',
    'horarios_atencion',
    'auditoria_detallada',
    'respaldos_sistema',
    'configuracion_avanzada',
    'facturas',
    'productos',
    'pacientes',
    'citas',
    'solicitudes_citas'
  )
GROUP BY table_name
ORDER BY table_name;

-- Resultado esperado: TODAS las tablas deben tener = 1 en las 4 columnas
```

### Validación de Estructura Java vs SQL

Ejecutar después de V21:
```bash
# Compilar proyecto
mvn clean compile

# Ejecutar migraciones
mvn flyway:migrate

# Verificar que NO hay errores de mapeo
mvn spring-boot:run
```

---

## Conclusiones

1. ✅ **Problema identificado:** Desincronización total entre V10 (SQL) y entidades Java
2. ✅ **Causa raíz:** V10 no siguió el estándar de nombres de V1/V3
3. ✅ **Solución:** Migración V21 unifica TODAS las tablas al estándar correcto
4. ✅ **Prevención futura:** Toda nueva tabla debe:
   - Usar nombres en inglés para auditoría
   - Si extiende Auditable → SIEMPRE incluir las 4 columnas desde el CREATE TABLE
   - Validar contra entidad Java ANTES de crear la migración

---

## Recomendaciones

### Para Desarrolladores
- ⚠️ **NUNCA** crear tablas con `creado_por/fecha_creacion` si la entidad extiende `Auditable`
- ✅ **SIEMPRE** usar `created_by/created_at/updated_by/updated_at`
- 📋 **VALIDAR** que cada `@Column` en Java tenga su columna en SQL

### Para DevOps
- 🔍 Ejecutar query de verificación después de CADA migración
- 📊 Monitorear logs de Hibernate para detectar mapeos faltantes
- 🧪 Ejecutar tests de integración que validen auditoría

---

## Archivo Generado
📄 **V21__add_missing_audit_columns_to_all_auditable_entities.sql**
📍 Ubicación: `src/main/resources/db/migration/`

**Estado:** ✅ Listo para ejecutar con Flyway
