-- =====================================================
-- Migración V13: Datos de prueba - Clientes y Pacientes
-- Propósito: Poblar base de datos con datos realistas
-- Fecha: 2025-11-30
-- =====================================================

-- Primero insertar usuarios (con rol CLIENTE) si no existen
-- Password para todos: "cliente123" (hasheado con BCrypt)
INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Carlos', 'Méndez', 'carlos.mendez@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'María', 'González', 'maria.gonzalez@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Juan', 'Rodríguez', 'juan.rodriguez@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Ana', 'Martínez', 'ana.martinez@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Pedro', 'Silva', 'pedro.silva@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Laura', 'Torres', 'laura.torres@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Diego', 'Ramírez', 'diego.ramirez@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Sofia', 'López', 'sofia.lopez@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Andrés', 'Castro', 'andres.castro@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Valentina', 'Ruiz', 'valentina.ruiz@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Miguel', 'Herrera', 'miguel.herrera@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Camila', 'Vargas', 'camila.vargas@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Sebastián', 'Mora', 'sebastian.mora@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Isabella', 'Díaz', 'isabella.diaz@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, rol_id, activo, created_at, updated_at)
SELECT 'Mateo', 'Sánchez', 'mateo.sanchez@email.com', '$2a$10$YourHashedPasswordHere', r.id_rol, true, NOW(), NOW()
FROM roles r WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT (correo) DO NOTHING;

-- Insertar 15 clientes (propietarios de mascotas)
INSERT INTO clientes (usuario_id, telefono, direccion, identificacion, created_at, updated_at) VALUES
-- Cliente 1: Carlos Méndez
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'carlos.mendez@email.com'), 
 '3001234567', 'Calle 45 #23-56, Bogotá', '1234567890', NOW(), NOW()),

-- Cliente 2: María González
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'maria.gonzalez@email.com'),
 '3109876543', 'Carrera 15 #78-90, Medellín', '9876543210', NOW(), NOW()),

-- Cliente 3: Juan Rodríguez
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'juan.rodriguez@email.com'),
 '3201112233', 'Avenida 6 #12-34, Cali', '5551234567', NOW(), NOW()),

-- Cliente 4: Ana Martínez
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'ana.martinez@email.com'),
 '3152223344', 'Calle 100 #45-67, Bogotá', '7778889990', NOW(), NOW()),

-- Cliente 5: Pedro Silva
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'pedro.silva@email.com'),
 '3003334455', 'Carrera 50 #20-15, Barranquilla', '1112223334', NOW(), NOW()),

-- Cliente 6: Laura Torres
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'laura.torres@email.com'),
 '3184445566', 'Calle 70 #35-80, Bogotá', '4445556667', NOW(), NOW()),

-- Cliente 7: Diego Ramírez
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'diego.ramirez@email.com'),
 '3125556677', 'Avenida 3 #25-40, Cúcuta', '8889990001', NOW(), NOW()),

-- Cliente 8: Sofia López
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'sofia.lopez@email.com'),
 '3046667788', 'Carrera 80 #55-20, Pereira', '2223334445', NOW(), NOW()),

-- Cliente 9: Andrés Castro
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'andres.castro@email.com'),
 '3197778899', 'Calle 25 #18-30, Manizales', '6667778889', NOW(), NOW()),

-- Cliente 10: Valentina Ruiz
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'valentina.ruiz@email.com'),
 '3138889900', 'Avenida 10 #50-75, Ibagué', '3334445556', NOW(), NOW()),

-- Cliente 11: Miguel Herrera
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'miguel.herrera@email.com'),
 '3029990011', 'Calle 85 #40-22, Bogotá', '9990001112', NOW(), NOW()),

-- Cliente 12: Camila Vargas
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'camila.vargas@email.com'),
 '3170001122', 'Carrera 25 #60-45, Bucaramanga', '5556667778', NOW(), NOW()),

-- Cliente 13: Sebastián Mora
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'sebastian.mora@email.com'),
 '3111112233', 'Avenida 20 #30-55, Cartagena', '7778889991', NOW(), NOW()),

-- Cliente 14: Isabella Díaz
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'isabella.diaz@email.com'),
 '3052223344', 'Calle 50 #28-40, Santa Marta', '1112223335', NOW(), NOW()),

-- Cliente 15: Mateo Sánchez
((SELECT u.id_usuario FROM usuarios u WHERE u.correo = 'mateo.sanchez@email.com'),
 '3183334455', 'Carrera 40 #65-88, Villavicencio', '4445556668', NOW(), NOW());

-- Insertar 25 pacientes (mascotas) - Mix de perros y gatos
-- Los códigos únicos se generarán automáticamente por el trigger

-- Pacientes del Cliente 1 (Carlos Méndez)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Max', 'perro', 'Labrador Retriever', '2020-03-15', 'Macho', 28.5, 'Sano', 
 (SELECT cliente_id FROM clientes WHERE telefono = '3001234567'), 
 gen_random_uuid(), NOW(), NOW()),
 
('Luna', 'gato', 'Siamés', '2021-07-20', 'Hembra', 4.2, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3001234567'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 2 (María González)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Rocky', 'perro', 'Golden Retriever', '2019-05-10', 'Macho', 32.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3109876543'),
 gen_random_uuid(), NOW(), NOW()),
 
('Bella', 'gato', 'Persa', '2022-01-15', 'Hembra', 3.8, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3109876543'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 3 (Juan Rodríguez)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Toby', 'perro', 'Beagle', '2021-11-25', 'Macho', 12.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3201112233'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 4 (Ana Martínez)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Coco', 'gato', 'Maine Coon', '2020-08-30', 'Macho', 6.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3152223344'),
 gen_random_uuid(), NOW(), NOW()),
 
('Simba', 'gato', 'Angora', '2021-12-05', 'Macho', 5.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3152223344'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 5 (Pedro Silva)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Bruno', 'perro', 'Pastor Alemán', '2018-04-20', 'Macho', 35.0, 'Control requerido',
 (SELECT cliente_id FROM clientes WHERE telefono = '3003334455'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 6 (Laura Torres)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Mía', 'gato', 'Bengala', '2022-06-10', 'Hembra', 4.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3184445566'),
 gen_random_uuid(), NOW(), NOW()),
 
('Zeus', 'perro', 'Rottweiler', '2019-09-18', 'Macho', 42.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3184445566'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 7 (Diego Ramírez)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Lola', 'perro', 'Poodle', '2021-02-28', 'Hembra', 8.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3125556677'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 8 (Sofia López)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Oliver', 'gato', 'Ragdoll', '2020-10-12', 'Macho', 5.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3046667788'),
 gen_random_uuid(), NOW(), NOW()),
 
('Nala', 'gato', 'British Shorthair', '2021-05-22', 'Hembra', 4.8, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3046667788'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 9 (Andrés Castro)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Thor', 'perro', 'Husky Siberiano', '2020-07-08', 'Macho', 25.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3197778899'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 10 (Valentina Ruiz)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Kira', 'perro', 'Dálmata', '2021-09-30', 'Hembra', 22.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3138889900'),
 gen_random_uuid(), NOW(), NOW()),
 
('Milo', 'gato', 'Sphynx', '2022-03-18', 'Macho', 3.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3138889900'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 11 (Miguel Herrera)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Rex', 'perro', 'Bulldog Francés', '2022-11-05', 'Macho', 11.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3029990011'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 12 (Camila Vargas)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Chloe', 'gato', 'Scottish Fold', '2021-08-14', 'Hembra', 4.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3170001122'),
 gen_random_uuid(), NOW(), NOW()),
 
('Duke', 'perro', 'Boxer', '2019-12-20', 'Macho', 30.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3170001122'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 13 (Sebastián Mora)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Nina', 'perro', 'Cocker Spaniel', '2020-06-25', 'Hembra', 14.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3111112233'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 14 (Isabella Díaz)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Oreo', 'gato', 'Común Europeo', '2022-02-08', 'Macho', 4.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3052223344'),
 gen_random_uuid(), NOW(), NOW());

-- Pacientes del Cliente 15 (Mateo Sánchez)
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Canela', 'perro', 'Schnauzer', '2021-04-12', 'Hembra', 9.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3183334455'),
 gen_random_uuid(), NOW(), NOW()),
 
('Pelusa', 'gato', 'Común Europeo', '2022-09-01', 'Hembra', 3.2, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3183334455'),
 gen_random_uuid(), NOW(), NOW());

-- Agregar algunos pacientes adicionales para llegar a 25+
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_at, updated_at) VALUES
('Firulais', 'perro', 'Mestizo', '2023-01-10', 'Macho', 15.0, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3001234567'),
 gen_random_uuid(), NOW(), NOW()),
 
('Manchas', 'perro', 'Mestizo', '2023-08-22', 'Hembra', 10.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3109876543'),
 gen_random_uuid(), NOW(), NOW()),
 
('Peludo', 'gato', 'Mestizo', '2023-05-15', 'Macho', 4.5, 'Sano',
 (SELECT cliente_id FROM clientes WHERE telefono = '3201112233'),
 gen_random_uuid(), NOW(), NOW());

-- Comentario final
COMMENT ON TABLE pacientes IS 'Tabla de pacientes actualizada con seed data - 25+ mascotas registradas';
