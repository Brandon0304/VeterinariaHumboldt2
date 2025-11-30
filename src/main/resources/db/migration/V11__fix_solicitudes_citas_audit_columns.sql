-- V11__fix_solicitudes_citas_audit_columns.sql
-- Renombra las columnas de auditoría para que coincidan con la clase Auditable

ALTER TABLE solicitudes_citas 
    RENAME COLUMN fecha_creacion TO created_at;

ALTER TABLE solicitudes_citas 
    RENAME COLUMN fecha_actualizacion TO updated_at;

ALTER TABLE solicitudes_citas 
    RENAME COLUMN creado_por TO created_by;

ALTER TABLE solicitudes_citas 
    RENAME COLUMN actualizado_por TO updated_by;

-- Comentario para descripción
COMMENT ON TABLE solicitudes_citas IS 'Tabla para almacenar solicitudes de cita del portal del cliente que requieren aprobación del secretario (columnas de auditoría alineadas con Auditable)';
