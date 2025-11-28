-- Crear usuario veterinario demo
-- NOTA: El hash se actualizará automáticamente al iniciar la aplicación si no es compatible con Spring Security
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Eliminar si existe
DELETE FROM usuarios_veterinarios WHERE id_usuario IN (SELECT id_usuario FROM usuarios WHERE username = 'vet_demo');
DELETE FROM usuarios WHERE username = 'vet_demo';
DELETE FROM personas WHERE correo = 'lucia.vet@veterinaria.com';

-- Insertar usuario veterinario
-- El hash se generará/actualizará automáticamente por UsuarioDemoInitializer al iniciar la app
WITH rol_veterinario AS (
    SELECT id_rol FROM roles WHERE nombre_rol = 'VETERINARIO' LIMIT 1
),
nueva_persona AS (
    INSERT INTO personas (nombre, apellido, correo, telefono, direccion)
    VALUES ('Lucia', 'Fernandez', 'lucia.vet@veterinaria.com', '3100000005', 'Calle 20 #15-30')
    RETURNING id_persona
),
nuevo_usuario AS (
    INSERT INTO usuarios (id_usuario, username, password_hash, activo, ultimo_acceso, rol_id)
    SELECT 
        p.id_persona,
        'vet_demo',
        crypt('Vet1234!', gen_salt('bf', 10)),  -- Hash temporal, se actualizará automáticamente al iniciar
        TRUE,
        CURRENT_TIMESTAMP,
        r.id_rol
    FROM nueva_persona p
    CROSS JOIN rol_veterinario r
    RETURNING id_usuario
)
INSERT INTO usuarios_veterinarios (id_usuario, licencia_profesional, especialidad, disponibilidad)
SELECT 
    u.id_usuario,
    'MVZ-55789',
    'Medicina General',
    '{"lunes": [{"inicio": "08:00", "fin": "12:00"}], "miercoles": [{"inicio": "14:00", "fin": "18:00"}], "viernes": [{"inicio": "09:00", "fin": "13:00"}]}'::jsonb
FROM nuevo_usuario u;

-- Credenciales: usuario=vet_demo, contraseña=Vet1234!
-- El hash se actualizará automáticamente al reiniciar el backend

