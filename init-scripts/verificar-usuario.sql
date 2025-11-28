-- Script para verificar que el usuario vet_demo existe y está correctamente configurado
SELECT 
    u.username,
    u.activo,
    r.nombre_rol,
    CASE 
        WHEN crypt('Vet1234!', u.password_hash) = u.password_hash THEN '✓ Contraseña válida'
        ELSE '✗ Contraseña NO válida'
    END AS validacion_password,
    CASE 
        WHEN uv.id_usuario IS NOT NULL THEN '✓ Es veterinario'
        ELSE '✗ NO es veterinario'
    END AS es_veterinario,
    uv.licencia_profesional,
    uv.especialidad
FROM usuarios u
LEFT JOIN roles r ON r.id_rol = u.rol_id
LEFT JOIN usuarios_veterinarios uv ON uv.id_usuario = u.id_usuario
WHERE u.username = 'vet_demo';
