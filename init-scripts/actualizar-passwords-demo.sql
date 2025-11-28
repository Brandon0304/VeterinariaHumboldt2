-- ============================================
-- SCRIPT PARA ACTUALIZAR CONTRASEÑAS DE CLIENTE Y SECRETARIO
-- ============================================
-- Este script actualiza las contraseñas con hashes válidos
-- ============================================

-- Actualizar password del cliente_demo
UPDATE usuarios 
SET password_hash = '$2a$10$UlnrsohdzunjHfm8GjIFE.dXEJM/L59jvHJSZUQ4d3yXz0lDnkqzm'
WHERE username = 'cliente_demo';

-- Actualizar password del secretario_demo2
UPDATE usuarios 
SET password_hash = '$2a$10$UlnrsohdzunjHfm8GjIFE.dXEJM/L59jvHJSZUQ4d3yXz0lDnkqzm'
WHERE username = 'secretario_demo2';

-- Verificar las actualizaciones
SELECT 
    username,
    activo,
    LEFT(password_hash, 30) || '...' as password_hash_preview
FROM usuarios 
WHERE username IN ('cliente_demo', 'secretario_demo2');
