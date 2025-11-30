-- Migration: Mejorar control de concurrencia en citas
-- Version: V4__improve_cita_concurrency.sql
-- Description: Agrega índice único parcial para prevenir citas duplicadas y mejora el control de concurrencia

-- ============================================================================
-- ÍNDICE ÚNICO PARA PREVENIR CITAS DUPLICADAS
-- ============================================================================

-- Crear índice único que previene que un veterinario tenga dos citas 
-- programadas en la misma fecha/hora
-- Solo aplica a citas en estado PROGRAMADA (índice parcial)
CREATE UNIQUE INDEX IF NOT EXISTS idx_citas_veterinario_fecha_unique 
ON citas(veterinario_id, fecha_hora) 
WHERE estado = 'PROGRAMADA';

-- ============================================================================
-- COMENTARIOS EXPLICATIVOS
-- ============================================================================

COMMENT ON INDEX idx_citas_veterinario_fecha_unique IS 
'Previene race conditions al agendar citas. Garantiza que un veterinario no tenga dos citas programadas en la misma fecha/hora.';

-- ============================================================================
-- ÍNDICES ADICIONALES PARA OPTIMIZAR CONSULTAS FRECUENTES
-- ============================================================================

-- Índice para buscar citas por estado (frecuente en queries de filtrado)
CREATE INDEX IF NOT EXISTS idx_citas_estado ON citas(estado);

-- Índice compuesto para buscar citas de un paciente por estado
CREATE INDEX IF NOT EXISTS idx_citas_paciente_estado ON citas(paciente_id, estado);

-- Índice compuesto para buscar citas de un veterinario por estado y fecha
CREATE INDEX IF NOT EXISTS idx_citas_vet_estado_fecha ON citas(veterinario_id, estado, fecha_hora DESC);

-- Índice para búsquedas por rango de fechas
CREATE INDEX IF NOT EXISTS idx_citas_fecha_hora ON citas(fecha_hora DESC);

-- ============================================================================
-- NOTAS IMPORTANTES
-- ============================================================================

-- 1. El índice único parcial (con WHERE estado = 'PROGRAMADA') permite que:
--    - Un veterinario NO pueda tener dos citas PROGRAMADAS en la misma hora
--    - Un veterinario SÍ pueda tener citas CANCELADAS o REALIZADAS en la misma hora
--    - Esto previene race conditions sin bloquear la reutilización de horarios cancelados
--
-- 2. Si dos transacciones intentan crear citas simultáneamente para el mismo 
--    veterinario/hora, una fallará con error de violación de constraint único
--
-- 3. Los índices adicionales mejoran el rendimiento de:
--    - Listar citas programadas de un veterinario (agenda)
--    - Listar citas de un paciente (historial)
--    - Buscar citas por fecha (reportes)
