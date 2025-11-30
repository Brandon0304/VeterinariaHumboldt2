-- =====================================================
-- Migración V12: Agregar código único a pacientes
-- Propósito: Identificador único legible para humanos
-- Fecha: 2025-11-30
-- =====================================================

-- Agregar columna codigo_unico con constraint UNIQUE
ALTER TABLE pacientes 
ADD COLUMN IF NOT EXISTS codigo_unico VARCHAR(20) UNIQUE;

-- Crear índice para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_pacientes_codigo_unico ON pacientes(codigo_unico);

-- Agregar comentario a la columna
COMMENT ON COLUMN pacientes.codigo_unico IS 'Código único legible para humanos (ej: PAC-00001)';

-- Generar códigos únicos para pacientes existentes (si los hay)
-- Formato: PAC-{ID con padding de 5 dígitos}
UPDATE pacientes 
SET codigo_unico = 'PAC-' || LPAD(id_paciente::TEXT, 5, '0')
WHERE codigo_unico IS NULL;

-- Hacer la columna NOT NULL después de rellenar datos existentes
ALTER TABLE pacientes 
ALTER COLUMN codigo_unico SET NOT NULL;

-- Crear secuencia para generar códigos automáticamente (opcional, para uso futuro)
CREATE SEQUENCE IF NOT EXISTS seq_paciente_codigo START 1 INCREMENT 1;

-- Trigger para generar código único automáticamente antes de insertar
-- Este trigger asegura que cada paciente tenga un código único generado automáticamente
CREATE OR REPLACE FUNCTION generar_codigo_paciente()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.codigo_unico IS NULL THEN
        -- Obtener el siguiente número de la secuencia
        NEW.codigo_unico := 'PAC-' || LPAD(nextval('seq_paciente_codigo')::TEXT, 5, '0');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear trigger si no existe
DROP TRIGGER IF EXISTS trigger_generar_codigo_paciente ON pacientes;
CREATE TRIGGER trigger_generar_codigo_paciente
    BEFORE INSERT ON pacientes
    FOR EACH ROW
    EXECUTE FUNCTION generar_codigo_paciente();

-- Actualizar la secuencia al máximo ID actual para evitar duplicados
SELECT setval('seq_paciente_codigo', COALESCE((SELECT MAX(id_paciente) FROM pacientes), 0) + 1, false);
