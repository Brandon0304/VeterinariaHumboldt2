-- V29: Añade columnas de auditoría a la tabla productos
-- Fecha: 2025-12-01
-- Descripción: Agrega created_by, created_at, updated_by, updated_at para que la entidad Producto pueda extender de Auditable

-- Agregar columnas de auditoría a productos
ALTER TABLE productos
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Establecer valores por defecto para registros existentes
UPDATE productos
SET created_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;
