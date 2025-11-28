-- Script para actualizar el password del usuario admin
-- Password: Admin123!
-- Este hash es generado con BCrypt (strength 10) y es compatible con Spring Security

UPDATE usuarios 
SET password_hash = '$2a$10$jHLXb8YquL.DQk5kKOC8lOqR6H1DsJPYZ8k8nqNQQ5KdPeR2BVH3C',
    activo = true
WHERE username = 'admin';

-- Verificar el cambio
SELECT u.username, u.activo, p.nombre, p.apellido, p.correo, r.nombre_rol, 
       LEFT(u.password_hash, 20) || '...' as password_hash_preview
FROM usuarios u
JOIN personas p ON u.id_usuario = p.id_persona
JOIN roles r ON u.rol_id = r.id_rol
WHERE u.username = 'admin';
