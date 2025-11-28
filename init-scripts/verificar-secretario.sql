-- Script para verificar que el usuario secretario existe y está correctamente configurado
SELECT 
    u.id_usuario,
    u.username,
    u.activo,
    p.nombre,
    p.apellido,
    p.correo,
    r.nombre_rol,
    s.extension,
    CASE 
        WHEN u.password_hash LIKE '$2a$%' OR u.password_hash LIKE '$2b$%' THEN 'Compatible con Spring Security'
        ELSE 'Requiere actualización'
    END as estado_hash
FROM usuarios u
JOIN personas p ON p.id_persona = u.id_usuario
JOIN roles r ON r.id_rol = u.rol_id
LEFT JOIN secretarios s ON s.id_usuario = u.id_usuario
WHERE u.username = 'secretario_demo';

-- Verificar permisos del rol SECRETARIO
SELECT 
    r.nombre_rol,
    p.nombre as permiso
FROM roles r
JOIN rol_permisos rp ON rp.rol_id = r.id_rol
JOIN permisos p ON p.id_permiso = rp.permiso_id
WHERE r.nombre_rol = 'SECRETARIO';
