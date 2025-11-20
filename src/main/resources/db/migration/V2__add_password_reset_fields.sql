-- =====================================================
-- Migración V2: Agregar campos para recuperación de contraseña
-- =====================================================

-- Agregar campos para token de recuperación de contraseña
ALTER TABLE usuarios
ADD COLUMN IF NOT EXISTS password_reset_token VARCHAR(255),
ADD COLUMN IF NOT EXISTS password_reset_token_expiry TIMESTAMP WITH TIME ZONE;

-- Crear índice para búsquedas rápidas por token
CREATE INDEX IF NOT EXISTS idx_usuarios_password_reset_token ON usuarios(password_reset_token) WHERE password_reset_token IS NOT NULL;

