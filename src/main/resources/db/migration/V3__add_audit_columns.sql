-- Migration: Agregar columnas de auditoría a tablas principales
-- Version: V3__add_audit_columns.sql
-- Description: Agrega campos created_by, created_at, updated_by, updated_at para trazabilidad

-- ============================================================================
-- TABLAS DE GESTIÓN DE USUARIOS
-- ============================================================================

ALTER TABLE personas 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

-- ============================================================================
-- TABLAS DE GESTIÓN DE PACIENTES
-- ============================================================================

ALTER TABLE pacientes 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE historias_clinicas 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE vacunaciones 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE desparasitaciones 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

-- ============================================================================
-- TABLAS DE PRESTACIÓN DE SERVICIOS
-- ============================================================================

ALTER TABLE citas 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE servicios 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE servicios_prestados 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

-- ============================================================================
-- TABLAS DE FACTURACIÓN
-- ============================================================================

ALTER TABLE facturas 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

-- ============================================================================
-- TABLAS DE INVENTARIO
-- ============================================================================

ALTER TABLE productos 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE movimientos_inventario 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE proveedores 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE alertas_inventario 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

-- ============================================================================
-- TABLAS DE NOTIFICACIONES
-- ============================================================================

ALTER TABLE notificaciones 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN updated_by VARCHAR(100),
ADD COLUMN updated_at TIMESTAMP;

-- ============================================================================
-- INICIALIZAR VALORES EXISTENTES
-- ============================================================================
-- Para registros existentes, establecer valores por defecto

UPDATE personas SET created_by = 'system', created_at = NOW() WHERE created_at IS NULL;
UPDATE pacientes SET created_by = 'system', created_at = NOW() WHERE created_at IS NULL;
UPDATE historias_clinicas SET created_by = 'system', created_at = fecha_apertura WHERE created_at IS NULL;
UPDATE citas SET created_by = 'system', created_at = fecha_hora WHERE created_at IS NULL;
UPDATE facturas SET created_by = 'system', created_at = fecha_emision WHERE created_at IS NULL;
UPDATE productos SET created_by = 'system', created_at = NOW() WHERE created_at IS NULL;
UPDATE movimientos_inventario SET created_by = 'system', created_at = fecha WHERE created_at IS NULL;
UPDATE notificaciones SET created_by = 'system', created_at = fecha_envio_programada WHERE created_at IS NULL;

-- ============================================================================
-- ÍNDICES PARA MEJORAR PERFORMANCE EN CONSULTAS DE AUDITORÍA
-- ============================================================================

CREATE INDEX idx_personas_created_at ON personas(created_at);
CREATE INDEX idx_pacientes_created_at ON pacientes(created_at);
CREATE INDEX idx_citas_created_at ON citas(created_at);
CREATE INDEX idx_facturas_created_at ON facturas(created_at);
CREATE INDEX idx_productos_created_at ON productos(created_at);

-- ============================================================================
-- COMENTARIOS EN COLUMNAS
-- ============================================================================

COMMENT ON COLUMN personas.created_by IS 'Usuario que creó el registro';
COMMENT ON COLUMN personas.created_at IS 'Fecha y hora de creación del registro';
COMMENT ON COLUMN personas.updated_by IS 'Usuario que modificó el registro por última vez';
COMMENT ON COLUMN personas.updated_at IS 'Fecha y hora de la última modificación';
