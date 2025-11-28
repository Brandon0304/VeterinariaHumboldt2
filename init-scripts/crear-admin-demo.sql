-- Script para crear un usuario Administrador de prueba
-- Usuario: admin / Password: Admin123!

-- 1. Verificar si existe el rol ADMIN
SELECT id, nombre_rol FROM roles WHERE nombre_rol = 'ADMIN';

-- 2. Crear persona para el usuario admin
INSERT INTO personas (nombre, apellido, correo, telefono, direccion)
SELECT 'Administrador', 'Sistema', 'admin@veterinaria.com', '3001234567', 'Oficina Principal'
WHERE NOT EXISTS (SELECT 1 FROM personas WHERE correo = 'admin@veterinaria.com');

-- 3. Crear usuario admin si no existe
INSERT INTO usuarios (id_usuario, username, password_hash, activo, rol_id)
SELECT 
    (SELECT id_persona FROM personas WHERE correo = 'admin@veterinaria.com'),
    'admin',
    '$2a$10$8z.Ib0VLmQ5Y3zNKJ.fS5ukF8w1q7vH9yYPqJ4gLKjH8pGzPnQgYq', -- Password: Admin123!
    true,
    (SELECT id FROM roles WHERE nombre_rol = 'ADMIN')
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE username = 'admin'
);

-- 4. Verificar que el usuario fue creado
SELECT 
    u.id_usuario,
    u.username,
    p.nombre,
    p.apellido,
    p.correo,
    u.activo,
    r.nombre_rol as rol
FROM usuarios u
JOIN personas p ON u.id_usuario = p.id_persona
JOIN roles r ON u.rol_id = r.id
WHERE u.username = 'admin';

-- Credenciales para iniciar sesión:
-- Username: admin
-- Password: Admin123!
