-- Crear usuario secretario demo
-- NOTA: El hash se actualizará automáticamente al iniciar la aplicación si no es compatible con Spring Security
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Eliminar si existe
DELETE FROM secretarios WHERE id_usuario IN (SELECT id_usuario FROM usuarios WHERE username = 'secretario_demo');
DELETE FROM usuarios WHERE username = 'secretario_demo';
DELETE FROM personas WHERE correo = 'maria.secretaria@veterinaria.com';

-- Insertar usuario secretario
-- El hash se generará/actualizará automáticamente por UsuarioDemoInitializer al iniciar la app
WITH rol_secretario AS (
    SELECT id_rol FROM roles WHERE nombre_rol = 'SECRETARIO' LIMIT 1
),
nueva_persona AS (
    INSERT INTO personas (nombre, apellido, correo, telefono, direccion)
    VALUES ('Maria', 'Gonzalez', 'maria.secretaria@veterinaria.com', '3100000006', 'Calle 25 #10-20')
    RETURNING id_persona
),
nuevo_usuario AS (
    INSERT INTO usuarios (id_usuario, username, password_hash, activo, ultimo_acceso, rol_id)
    SELECT
        p.id_persona,
        'secretario_demo',
        crypt('Secret1234!', gen_salt('bf', 10)),  -- Hash temporal, se actualizará automáticamente al iniciar
        TRUE,
        CURRENT_TIMESTAMP,
        r.id_rol
    FROM nueva_persona p
    CROSS JOIN rol_secretario r
    RETURNING id_usuario
)
INSERT INTO secretarios (id_usuario, extension)
SELECT
    u.id_usuario,
    '101'
FROM nuevo_usuario u;

-- Credenciales: usuario=secretario_demo, contraseña=Secret1234!
-- El hash se actualizará automáticamente al reiniciar el backend
