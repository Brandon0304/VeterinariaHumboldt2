-- V6__add_audit_trail_solicitudes.sql
-- Agregar campos de audit trail a la tabla solicitudes_citas

-- Agregar columnas de audit trail
ALTER TABLE public.solicitudes_citas 
ADD COLUMN IF NOT EXISTS aprobado_por BIGINT,
ADD COLUMN IF NOT EXISTS aprobado_en TIMESTAMP,
ADD COLUMN IF NOT EXISTS rechazado_por BIGINT,
ADD COLUMN IF NOT EXISTS rechazado_en TIMESTAMP,
ADD COLUMN IF NOT EXISTS cancelado_por BIGINT,
ADD COLUMN IF NOT EXISTS cancelado_en TIMESTAMP;

-- Agregar comentarios de documentación
COMMENT ON COLUMN public.solicitudes_citas.aprobado_por IS 'ID del usuario (secretario) que aprobó la solicitud';
COMMENT ON COLUMN public.solicitudes_citas.aprobado_en IS 'Fecha y hora cuando fue aprobada la solicitud';
COMMENT ON COLUMN public.solicitudes_citas.rechazado_por IS 'ID del usuario (secretario) que rechazó la solicitud';
COMMENT ON COLUMN public.solicitudes_citas.rechazado_en IS 'Fecha y hora cuando fue rechazada la solicitud';
COMMENT ON COLUMN public.solicitudes_citas.cancelado_por IS 'ID del usuario que canceló la solicitud';
COMMENT ON COLUMN public.solicitudes_citas.cancelado_en IS 'Fecha y hora cuando fue cancelada la solicitud';
