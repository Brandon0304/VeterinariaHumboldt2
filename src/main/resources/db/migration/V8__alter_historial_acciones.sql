-- =====================================================
-- V8: Agregar columnas tipo_accion e ip_address a historial_acciones
-- =====================================================

-- Agregar columna tipo_accion con valor por defecto temporal
ALTER TABLE historial_acciones 
ADD COLUMN IF NOT EXISTS tipo_accion VARCHAR(100);

-- Actualizar registros existentes con un valor por defecto
UPDATE historial_acciones 
SET tipo_accion = 'ACCION_GENERAL' 
WHERE tipo_accion IS NULL;

-- Ahora hacer la columna NOT NULL
ALTER TABLE historial_acciones 
ALTER COLUMN tipo_accion SET NOT NULL;

-- Agregar columna ip_address (nullable está bien)
ALTER TABLE historial_acciones 
ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);
