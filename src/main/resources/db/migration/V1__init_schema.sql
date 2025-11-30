-- =====================================================
-- Script de migración inicial V1
-- Sistema Clínico Veterinario
-- Base de datos: PostgreSQL 15.x
-- =====================================================

-- =====================================================
-- 1. TABLAS DE GESTIÓN DE USUARIOS
-- =====================================================

-- Tabla de personas (clase base abstracta)
CREATE TABLE IF NOT EXISTS personas (
    id_persona BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(30),
    direccion VARCHAR(255)
);

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id_rol BIGSERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Tabla de permisos
CREATE TABLE IF NOT EXISTS permisos (
    id_permiso BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Tabla intermedia para relación muchos a muchos entre roles y permisos
CREATE TABLE IF NOT EXISTS rol_permisos (
    rol_id BIGINT NOT NULL REFERENCES roles(id_rol) ON DELETE CASCADE,
    permiso_id BIGINT NOT NULL REFERENCES permisos(id_permiso) ON DELETE CASCADE,
    PRIMARY KEY (rol_id, permiso_id)
);

-- Tabla de usuarios (hereda de personas)
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario BIGINT PRIMARY KEY REFERENCES personas(id_persona) ON DELETE CASCADE,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    ultimo_acceso TIMESTAMP WITH TIME ZONE,
    rol_id BIGINT NOT NULL REFERENCES roles(id_rol)
);

-- Tabla de clientes (hereda de usuarios)
CREATE TABLE IF NOT EXISTS clientes (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    fecha_registro TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    documento_identidad VARCHAR(50)
);

-- Tabla de usuarios veterinarios (hereda de usuarios)
CREATE TABLE IF NOT EXISTS usuarios_veterinarios (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    licencia_profesional VARCHAR(100),
    especialidad VARCHAR(100),
    disponibilidad JSONB
);

-- Tabla de secretarios (hereda de usuarios)
CREATE TABLE IF NOT EXISTS secretarios (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    extension VARCHAR(20)
);

-- Tabla de historial de acciones
CREATE TABLE IF NOT EXISTS historial_acciones (
    id_accion BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    fecha_hora TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion TEXT NOT NULL,
    metadata JSONB
);

-- =====================================================
-- 2. TABLAS DE GESTIÓN DE PACIENTES
-- =====================================================

-- Tabla de pacientes (mascotas)
CREATE TABLE IF NOT EXISTS pacientes (
    id_paciente BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(30) NOT NULL CHECK (especie IN ('perro', 'gato')),
    raza VARCHAR(80),
    fecha_nacimiento DATE,
    sexo VARCHAR(10),
    peso_kg NUMERIC(5,2) CHECK (peso_kg > 0),
    estado_salud VARCHAR(100),
    cliente_id BIGINT NOT NULL REFERENCES clientes(id_usuario) ON DELETE CASCADE,
    identificador_externo UUID
);

-- Tabla de historias clínicas
CREATE TABLE IF NOT EXISTS historias_clinicas (
    id_historia BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES pacientes(id_paciente) ON DELETE CASCADE,
    fecha_apertura TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resumen TEXT,
    metadatos JSONB
);

-- Tabla de registros médicos
CREATE TABLE IF NOT EXISTS registros_medicos (
    id_registro BIGSERIAL PRIMARY KEY,
    historia_id BIGINT NOT NULL REFERENCES historias_clinicas(id_historia) ON DELETE CASCADE,
    fecha TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo TEXT,
    diagnostico TEXT,
    signos_vitales JSONB,
    tratamiento TEXT,
    veterinario_id BIGINT REFERENCES usuarios_veterinarios(id_usuario),
    insumos_usados JSONB,
    archivos JSONB
);

-- Tabla de vacunaciones
CREATE TABLE IF NOT EXISTS vacunaciones (
    id_vacunacion BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES pacientes(id_paciente) ON DELETE CASCADE,
    tipo_vacuna VARCHAR(100) NOT NULL,
    fecha_aplicacion DATE NOT NULL,
    proxima_dosis DATE,
    veterinario_id BIGINT REFERENCES usuarios_veterinarios(id_usuario)
);

-- Tabla de desparasitaciones
CREATE TABLE IF NOT EXISTS desparasitaciones (
    id_desparasitacion BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES pacientes(id_paciente) ON DELETE CASCADE,
    producto_usado VARCHAR(150) NOT NULL,
    fecha_aplicacion DATE NOT NULL,
    proxima_aplicacion DATE
);

-- =====================================================
-- 3. TABLAS DE GESTIÓN DE INVENTARIO
-- =====================================================

-- Tabla de proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id_proveedor BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(30),
    direccion VARCHAR(255),
    correo VARCHAR(150) UNIQUE
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    id_producto BIGSERIAL PRIMARY KEY,
    sku VARCHAR(60) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    precio_unitario NUMERIC(12,2) NOT NULL CHECK (precio_unitario >= 0),
    um VARCHAR(20),
    metadatos JSONB
);

-- Índices para productos
CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre);
CREATE INDEX IF NOT EXISTS idx_productos_tipo ON productos(tipo);

-- Tabla de lotes
CREATE TABLE IF NOT EXISTS lotes (
    id_lote BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL REFERENCES productos(id_producto) ON DELETE CASCADE,
    fecha_vencimiento DATE NOT NULL CHECK (fecha_vencimiento >= CURRENT_DATE),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    numero_lote VARCHAR(100) UNIQUE
);

-- Tabla de movimientos de inventario
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id_movimiento BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL REFERENCES productos(id_producto) ON DELETE CASCADE,
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('IN', 'OUT', 'AJUSTE')),
    cantidad INTEGER NOT NULL,
    fecha TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    proveedor_id BIGINT REFERENCES proveedores(id_proveedor),
    referencia VARCHAR(100),
    usuario_id BIGINT REFERENCES usuarios(id_usuario)
);

-- Tabla de alertas de inventario
CREATE TABLE IF NOT EXISTS alertas_inventario (
    id_alerta BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL REFERENCES productos(id_producto) ON DELETE CASCADE,
    nivel_stock INTEGER NOT NULL,
    fecha_generada TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mensaje TEXT
);

-- =====================================================
-- 4. TABLAS DE PRESTACIÓN DE SERVICIOS
-- =====================================================

-- Tabla de servicios
CREATE TABLE IF NOT EXISTS servicios (
    id_servicio BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    descripcion TEXT,
    tipo VARCHAR(50),
    precio_base NUMERIC(12,2) NOT NULL CHECK (precio_base >= 0),
    duracion_min INTEGER
);

-- Tabla de citas
CREATE TABLE IF NOT EXISTS citas (
    id_cita BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES pacientes(id_paciente) ON DELETE CASCADE,
    veterinario_id BIGINT NOT NULL REFERENCES usuarios_veterinarios(id_usuario),
    fecha_hora TIMESTAMP WITH TIME ZONE NOT NULL,
    tipo_servicio VARCHAR(50),
    estado VARCHAR(30) NOT NULL DEFAULT 'PROGRAMADA' CHECK (estado IN ('PROGRAMADA', 'REALIZADA', 'CANCELADA')),
    motivo TEXT,
    triage_nivel VARCHAR(30)
);

-- Índice para evitar doble reserva de citas
CREATE UNIQUE INDEX IF NOT EXISTS idx_citas_veterinario_fecha ON citas(veterinario_id, fecha_hora) 
WHERE estado = 'PROGRAMADA';

-- Tabla de servicios prestados
CREATE TABLE IF NOT EXISTS servicios_prestados (
    id_prestado BIGSERIAL PRIMARY KEY,
    cita_id BIGINT NOT NULL REFERENCES citas(id_cita) ON DELETE CASCADE,
    servicio_id BIGINT NOT NULL REFERENCES servicios(id_servicio),
    fecha_ejecucion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    costo_total NUMERIC(12,2) NOT NULL CHECK (costo_total >= 0),
    insumos_consumidos JSONB
);

-- Tabla de facturas
CREATE TABLE IF NOT EXISTS facturas (
    id_factura BIGSERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    fecha_emision TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(14,2) NOT NULL CHECK (total >= 0),
    forma_pago VARCHAR(50),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'PAGADA', 'ANULADA')),
    cliente_id BIGINT NOT NULL REFERENCES clientes(id_usuario),
    contenido JSONB
);

-- =====================================================
-- 5. TABLAS DE NOTIFICACIONES
-- =====================================================

-- Tabla de canales de envío (clase abstracta)
CREATE TABLE IF NOT EXISTS canales_envio (
    id_canal BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    configuracion JSONB
);

-- Tabla de plantillas de mensajes
CREATE TABLE IF NOT EXISTS plantillas_mensajes (
    id_plantilla BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    asunto VARCHAR(150),
    cuerpo TEXT NOT NULL,
    variables JSONB
);

-- Tabla de notificaciones
CREATE TABLE IF NOT EXISTS notificaciones (
    id_notificacion BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_envio_programada TIMESTAMP WITH TIME ZONE,
    fecha_envio_real TIMESTAMP WITH TIME ZONE,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'ENVIADA', 'FALLIDA')),
    plantilla_id BIGINT REFERENCES plantillas_mensajes(id_plantilla),
    datos JSONB
);

-- Tabla de destinatarios
CREATE TABLE IF NOT EXISTS destinatarios (
    id_destinatario BIGSERIAL PRIMARY KEY,
    tipo_destinatario VARCHAR(30) NOT NULL,
    referencia_id BIGINT NOT NULL
);

-- Tabla intermedia para relación entre notificaciones y destinatarios
CREATE TABLE IF NOT EXISTS notificacion_destinatarios (
    notificacion_id BIGINT NOT NULL REFERENCES notificaciones(id_notificacion) ON DELETE CASCADE,
    destinatario_id BIGINT NOT NULL REFERENCES destinatarios(id_destinatario) ON DELETE CASCADE,
    PRIMARY KEY (notificacion_id, destinatario_id)
);

-- Tabla de canales de email
CREATE TABLE IF NOT EXISTS canales_email (
    id_canal BIGINT PRIMARY KEY REFERENCES canales_envio(id_canal) ON DELETE CASCADE,
    smtp_server VARCHAR(150),
    from_address VARCHAR(150)
);

-- Tabla de canales App
CREATE TABLE IF NOT EXISTS canales_app (
    id_canal BIGINT PRIMARY KEY REFERENCES canales_envio(id_canal) ON DELETE CASCADE,
    app_topic VARCHAR(150)
);

-- =====================================================
-- 6. TABLAS DE CONFIGURACIÓN DEL SISTEMA
-- =====================================================

-- Tabla de parámetros del sistema (Singleton)
CREATE TABLE IF NOT EXISTS parametros_sistema (
    id_parametro BIGSERIAL PRIMARY KEY,
    clave VARCHAR(150) NOT NULL UNIQUE,
    valor VARCHAR(500) NOT NULL,
    descripcion TEXT,
    aplicacion VARCHAR(50)
);

-- Tabla de backups del sistema
CREATE TABLE IF NOT EXISTS backups_sistema (
    id_backup BIGSERIAL PRIMARY KEY,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ruta_archivo VARCHAR(500) NOT NULL,
    metadata JSONB
);

-- Tabla de logs del sistema
CREATE TABLE IF NOT EXISTS logs_sistema (
    id_log BIGSERIAL PRIMARY KEY,
    fecha_hora TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nivel VARCHAR(20) NOT NULL,
    componente VARCHAR(100),
    mensaje TEXT NOT NULL,
    metadata JSONB
);

-- Índices para logs
CREATE INDEX IF NOT EXISTS idx_logs_nivel ON logs_sistema(nivel);
CREATE INDEX IF NOT EXISTS idx_logs_componente ON logs_sistema(componente);
CREATE INDEX IF NOT EXISTS idx_logs_fecha ON logs_sistema(fecha_hora);

-- =====================================================
-- 7. TABLAS DE REPORTES Y ESTADÍSTICAS
-- =====================================================

-- Tabla de reportes
CREATE TABLE IF NOT EXISTS reportes (
    id_reporte BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    tipo VARCHAR(50),
    fecha_generacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    generado_por BIGINT REFERENCES usuarios(id_usuario),
    parametros JSONB
);

-- Tabla de estadísticas
CREATE TABLE IF NOT EXISTS estadisticas (
    id_estadistica BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    valor NUMERIC(18,4),
    periodo_inicio DATE,
    periodo_fin DATE,
    UNIQUE (nombre, periodo_inicio, periodo_fin)
);

-- Tabla de indicadores
CREATE TABLE IF NOT EXISTS indicadores (
    id_indicador BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    descripcion TEXT,
    valor_actual NUMERIC(18,4)
);

-- Tabla de fuentes de datos
CREATE TABLE IF NOT EXISTS fuentes_datos (
    id_fuente BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    tipo VARCHAR(50),
    configuracion JSONB
);

-- =====================================================
-- 8. DATOS INICIALES
-- =====================================================

-- Insertar roles básicos
INSERT INTO roles (nombre_rol, descripcion) VALUES
    ('ADMIN', 'Administrador del sistema con acceso completo'),
    ('VETERINARIO', 'Veterinario que atiende pacientes'),
    ('SECRETARIO', 'Secretario que gestiona citas e inventario'),
    ('CLIENTE', 'Cliente que posee mascotas')
ON CONFLICT (nombre_rol) DO NOTHING;

-- Insertar permisos básicos
INSERT INTO permisos (nombre, descripcion) VALUES
    ('CREAR_USUARIO', 'Permite crear nuevos usuarios'),
    ('EDITAR_USUARIO', 'Permite editar usuarios existentes'),
    ('ELIMINAR_USUARIO', 'Permite eliminar usuarios'),
    ('VER_USUARIOS', 'Permite ver lista de usuarios'),
    ('CREAR_PACIENTE', 'Permite crear nuevos pacientes'),
    ('EDITAR_PACIENTE', 'Permite editar pacientes existentes'),
    ('VER_PACIENTES', 'Permite ver lista de pacientes'),
    ('CREAR_CITA', 'Permite crear nuevas citas'),
    ('EDITAR_CITA', 'Permite editar citas existentes'),
    ('CANCELAR_CITA', 'Permite cancelar citas'),
    ('GESTIONAR_INVENTARIO', 'Permite gestionar inventario'),
    ('VER_REPORTES', 'Permite ver reportes y estadísticas')
ON CONFLICT (nombre) DO NOTHING;

-- Asignar permisos a roles (ejemplo: ADMIN tiene todos los permisos)
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id_rol, p.id_permiso
FROM roles r, permisos p
WHERE r.nombre_rol = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Asignar permisos para rol VETERINARIO
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id_rol, p.id_permiso
FROM roles r
JOIN permisos p ON p.nombre IN ('CREAR_PACIENTE', 'EDITAR_PACIENTE', 'VER_PACIENTES', 'CREAR_CITA', 'EDITAR_CITA', 'CANCELAR_CITA', 'VER_REPORTES')
WHERE r.nombre_rol = 'VETERINARIO'
ON CONFLICT DO NOTHING;

-- Asignar permisos para rol SECRETARIO
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id_rol, p.id_permiso
FROM roles r
JOIN permisos p ON p.nombre IN ('CREAR_CITA', 'EDITAR_CITA', 'CANCELAR_CITA', 'VER_PACIENTES', 'GESTIONAR_INVENTARIO')
WHERE r.nombre_rol = 'SECRETARIO'
ON CONFLICT DO NOTHING;

-- Asignar permisos para rol CLIENTE
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id_rol, p.id_permiso
FROM roles r
JOIN permisos p ON p.nombre IN ('CREAR_CITA', 'CANCELAR_CITA', 'VER_PACIENTES')
WHERE r.nombre_rol = 'CLIENTE'
ON CONFLICT DO NOTHING;

-- Insertar parámetros del sistema iniciales
INSERT INTO parametros_sistema (clave, valor, descripcion, aplicacion) VALUES
    ('notificaciones.email.enabled', 'true', 'Habilita el envío de notificaciones por email', 'notificaciones'),
    ('inventario.stock.minimo', '10', 'Stock mínimo para generar alertas', 'inventario'),
    ('sistema.mantenimiento', 'false', 'Indica si el sistema está en mantenimiento', 'global')
ON CONFLICT (clave) DO NOTHING;

-- Insertar usuarios iniciales (contraseñas bcrypt preconfiguradas solo para desarrollo)
INSERT INTO personas (nombre, apellido, correo, telefono, direccion) VALUES
    ('Ana', 'Ramírez', 'admin@veterinaria.com', '3100000001', 'Calle 1 #2-3'),
    ('Carlos', 'Méndez', 'carlos.vet@veterinaria.com', '3100000002', 'Carrera 5 #10-20'),
    ('Laura', 'Gómez', 'laura.sec@veterinaria.com', '3100000003', 'Avenida 15 #8-40'),
    ('Diego', 'López', 'diego.cliente@email.com', '3100000004', 'Transversal 9 #6-15')
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (id_usuario, username, password_hash, activo, ultimo_acceso, rol_id)
SELECT p.id_persona, 'admin', '747d6d397e16f11268321f7fa94d08fe400c7e7cda6c22f0c44de7d16d2eafcf', TRUE, CURRENT_TIMESTAMP, r.id_rol
FROM personas p
JOIN roles r ON r.nombre_rol = 'ADMIN'
WHERE p.correo = 'admin@veterinaria.com'
ON CONFLICT (username) DO NOTHING;

INSERT INTO usuarios (id_usuario, username, password_hash, activo, ultimo_acceso, rol_id)
SELECT p.id_persona, 'vet_carlos', '$2a$10$2b2GZHTCqkfNNPJBWlAbGuY/jGj33jjXNMTvEihQ/HVBuUaz2Ys3S', TRUE, CURRENT_TIMESTAMP, r.id_rol
FROM personas p
JOIN roles r ON r.nombre_rol = 'VETERINARIO'
WHERE p.correo = 'carlos.vet@veterinaria.com'
ON CONFLICT (username) DO NOTHING;

INSERT INTO usuarios (id_usuario, username, password_hash, activo, ultimo_acceso, rol_id)
SELECT p.id_persona, 'sec_laura', '$2a$10$wN5e7/geXjr6JvLCYOtJk.nK0Os/.xLtEPPYU.GOCa/Br0gS2wPFe', TRUE, CURRENT_TIMESTAMP, r.id_rol
FROM personas p
JOIN roles r ON r.nombre_rol = 'SECRETARIO'
WHERE p.correo = 'laura.sec@veterinaria.com'
ON CONFLICT (username) DO NOTHING;

INSERT INTO usuarios (id_usuario, username, password_hash, activo, ultimo_acceso, rol_id)
SELECT p.id_persona, 'cliente_diego', '$2a$10$A8EjqCOUMIqFZ3VAb9Y/jOb0IhiHBpjz2uVH54camzatoNgtrENcO', TRUE, CURRENT_TIMESTAMP, r.id_rol
FROM personas p
JOIN roles r ON r.nombre_rol = 'CLIENTE'
WHERE p.correo = 'diego.cliente@email.com'
ON CONFLICT (username) DO NOTHING;

INSERT INTO usuarios_veterinarios (id_usuario, licencia_profesional, especialidad, disponibilidad)
SELECT u.id_usuario, 'MVZ-12345', 'Consulta General', '{"lunes":[{"inicio":"09:00","fin":"13:00"}],"miércoles":[{"inicio":"14:00","fin":"18:00"}]}'::jsonb
FROM usuarios u
WHERE u.username = 'vet_carlos'
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO secretarios (id_usuario, extension)
SELECT u.id_usuario, '101'
FROM usuarios u
WHERE u.username = 'sec_laura'
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO clientes (id_usuario, fecha_registro, documento_identidad)
SELECT u.id_usuario, CURRENT_TIMESTAMP, 'CC-100200300'
FROM usuarios u
WHERE u.username = 'cliente_diego'
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO historial_acciones (usuario_id, descripcion, metadata)
SELECT u.id_usuario, 'Usuario inicial creado', '{"ip":"127.0.0.1"}'::jsonb
FROM usuarios u
WHERE u.username IN ('admin', 'vet_carlos', 'sec_laura', 'cliente_diego')
ON CONFLICT DO NOTHING;

-- Paciente y registros clínicos iniciales
INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo)
SELECT 'Firulais', 'perro', 'Labrador', DATE '2020-05-15', 'Macho', 28.5, 'Estable', c.id_usuario, gen_random_uuid()
FROM clientes c
JOIN usuarios u ON u.id_usuario = c.id_usuario AND u.username = 'cliente_diego'
ON CONFLICT DO NOTHING;

INSERT INTO historias_clinicas (paciente_id, resumen, metadatos)
SELECT p.id_paciente, 'Historia clínica creada automáticamente para Firulais', '{"origen":"registro inicial"}'::jsonb
FROM pacientes p
WHERE p.nombre = 'Firulais'
ON CONFLICT DO NOTHING;

INSERT INTO registros_medicos (historia_id, motivo, diagnostico, signos_vitales, tratamiento, veterinario_id, insumos_usados)
SELECT h.id_historia,
       'Chequeo general de ingreso',
       'Sin hallazgos relevantes',
       '{"temperatura":38.2,"fc":95,"fr":22}'::jsonb,
       'Continuar alimentación balanceada',
       v.id_usuario,
       '{"vitaminas":1}'::jsonb
FROM historias_clinicas h
JOIN pacientes p ON p.id_paciente = h.paciente_id AND p.nombre = 'Firulais'
JOIN usuarios v ON v.username = 'vet_carlos'
ON CONFLICT DO NOTHING;

INSERT INTO vacunaciones (paciente_id, tipo_vacuna, fecha_aplicacion, proxima_dosis, veterinario_id)
SELECT p.id_paciente, 'Rabia', DATE '2025-01-10', DATE '2026-01-10', v.id_usuario
FROM pacientes p
JOIN usuarios v ON v.username = 'vet_carlos'
WHERE p.nombre = 'Firulais'
ON CONFLICT DO NOTHING;

INSERT INTO desparasitaciones (paciente_id, producto_usado, fecha_aplicacion, proxima_aplicacion)
SELECT p.id_paciente, 'Ivermectina', DATE '2025-02-05', DATE '2025-08-05'
FROM pacientes p
WHERE p.nombre = 'Firulais'
ON CONFLICT DO NOTHING;

-- Proveedores e inventario inicial
INSERT INTO proveedores (nombre, contacto, telefono, direccion, correo) VALUES
    ('Distribuidora VetMed', 'Paula Herrera', '3001234567', 'Zona Industrial 45', 'ventas@vetmed.com'),
    ('FarmAnimal SAS', 'Ricardo Díaz', '3007654321', 'Av. Central 100-20', 'contacto@farmanimal.com')
ON CONFLICT (correo) DO NOTHING;

INSERT INTO productos (sku, nombre, descripcion, tipo, stock, precio_unitario, um, metadatos) VALUES
    ('SKU-ANTIP', 'Antiparasitario interno', 'Tabletas antiparasitarias para perros medianos', 'medicamento', 35, 22000, 'tableta', '{"dosis":"1 tableta por cada 10kg"}'::jsonb),
    ('SKU-GASA', 'Gasas estériles 10x10', 'Paquete de gasas para curaciones', 'insumo', 120, 5000, 'paquete', NULL),
    ('SKU-VACRAB', 'Vacuna antirrábica', 'Vacuna para rabia canina/felina', 'biologico', 15, 58000, 'frasco', '{"fabricante":"BioVet"}'::jsonb)
ON CONFLICT (sku) DO NOTHING;

INSERT INTO lotes (producto_id, fecha_vencimiento, cantidad, numero_lote)
SELECT pr.id_producto, DATE '2026-12-31', 20, 'L-ANTIP-2025'
FROM productos pr
WHERE pr.sku = 'SKU-ANTIP'
ON CONFLICT (numero_lote) DO NOTHING;

INSERT INTO movimientos_inventario (producto_id, tipo_movimiento, cantidad, proveedor_id, referencia, usuario_id)
SELECT pr.id_producto, 'IN', 50, prov.id_proveedor, 'OC-2025-001', u.id_usuario
FROM productos pr
JOIN proveedores prov ON prov.nombre = 'Distribuidora VetMed'
JOIN usuarios u ON u.username = 'sec_laura'
WHERE pr.sku = 'SKU-ANTIP'
ON CONFLICT DO NOTHING;

INSERT INTO alertas_inventario (producto_id, nivel_stock, mensaje)
SELECT pr.id_producto, pr.stock, 'Stock bajo para vacunas antirrábicas'
FROM productos pr
WHERE pr.sku = 'SKU-VACRAB' AND pr.stock < 20
ON CONFLICT DO NOTHING;

-- Servicios, citas y facturación inicial
INSERT INTO servicios (nombre, descripcion, tipo, precio_base, duracion_min) VALUES
    ('Consulta general', 'Evaluación clínica de pacientes', 'CONSULTA', 45000, 30),
    ('Vacunación', 'Aplicación de vacunas caninas/felinas', 'VACUNACIÓN', 60000, 20),
    ('Cirugía menor', 'Procedimientos quirúrgicos ambulatorios', 'CIRUGÍA', 180000, 90)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO citas (paciente_id, veterinario_id, fecha_hora, tipo_servicio, estado, motivo, triage_nivel)
SELECT p.id_paciente, v.id_usuario, TIMESTAMP WITH TIME ZONE '2025-11-08 10:00:00-05', 'CONSULTA', 'PROGRAMADA', 'Control anual', 'BAJO'
FROM pacientes p
JOIN usuarios v ON v.username = 'vet_carlos'
WHERE p.nombre = 'Firulais'
ON CONFLICT DO NOTHING;

INSERT INTO servicios_prestados (cita_id, servicio_id, fecha_ejecucion, observaciones, costo_total, insumos_consumidos)
SELECT c.id_cita, s.id_servicio, c.fecha_hora, 'Consulta de control realizada sin complicaciones', 45000, '{"SKU-GASA":1}'::jsonb
FROM citas c
JOIN servicios s ON s.nombre = 'Consulta general'
JOIN pacientes p ON p.id_paciente = c.paciente_id AND p.nombre = 'Firulais'
WHERE c.fecha_hora = TIMESTAMP WITH TIME ZONE '2025-11-08 10:00:00-05'
ON CONFLICT DO NOTHING;

INSERT INTO facturas (numero, fecha_emision, total, forma_pago, estado, cliente_id, contenido)
SELECT 'FAC-2025-0001', CURRENT_TIMESTAMP, 45000, 'EFECTIVO', 'PAGADA', cli.id_usuario,
       '{"detalle":[{"servicio":"Consulta general","valor":45000}]}'::jsonb
FROM clientes cli
JOIN usuarios u ON u.id_usuario = cli.id_usuario AND u.username = 'cliente_diego'
ON CONFLICT (numero) DO NOTHING;

-- Configuración de notificaciones
INSERT INTO canales_envio (nombre, configuracion) VALUES
    ('EMAIL', '{"host":"smtp.gmail.com","puerto":587}'::jsonb),
    ('APP', '{"topic":"veterinaria"}'::jsonb)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO canales_email (id_canal, smtp_server, from_address)
SELECT c.id_canal, 'smtp.gmail.com', 'notificaciones@veterinaria.com'
FROM canales_envio c
WHERE c.nombre = 'EMAIL'
ON CONFLICT (id_canal) DO NOTHING;

INSERT INTO canales_app (id_canal, app_topic)
SELECT c.id_canal, 'veterinaria-recordatorios'
FROM canales_envio c
WHERE c.nombre = 'APP'
ON CONFLICT (id_canal) DO NOTHING;

INSERT INTO plantillas_mensajes (nombre, asunto, cuerpo, variables) VALUES
    ('Recordatorio Cita', 'Recordatorio de cita veterinaria', 'Hola {{nombreCliente}}, recuerda tu cita para {{nombreMascota}} el {{fecha}} a las {{hora}}.', '["nombreCliente","nombreMascota","fecha","hora"]'::jsonb),
    ('Vacunación Pendiente', 'Próxima dosis de vacuna', 'Estimado {{nombreCliente}}, la próxima dosis de {{tipoVacuna}} para {{nombreMascota}} está programada el {{fecha}}.', '["nombreCliente","nombreMascota","tipoVacuna","fecha"]'::jsonb)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO destinatarios (tipo_destinatario, referencia_id)
SELECT 'CLIENTE', cli.id_usuario
FROM clientes cli
JOIN usuarios u ON u.id_usuario = cli.id_usuario AND u.username = 'cliente_diego'
ON CONFLICT DO NOTHING;

INSERT INTO notificaciones (tipo, mensaje, fecha_envio_programada, estado, plantilla_id, datos)
SELECT 'RECORDATORIO_CITA', 'Recordatorio automático de cita', CURRENT_TIMESTAMP + INTERVAL '1 day', 'PENDIENTE', pm.id_plantilla,
       '{"nombreCliente":"Diego","nombreMascota":"Firulais","fecha":"2025-11-08","hora":"10:00"}'::jsonb
FROM plantillas_mensajes pm
WHERE pm.nombre = 'Recordatorio Cita'
ON CONFLICT DO NOTHING;

INSERT INTO notificacion_destinatarios (notificacion_id, destinatario_id)
SELECT n.id_notificacion, d.id_destinatario
FROM notificaciones n
JOIN destinatarios d ON d.tipo_destinatario = 'CLIENTE'
WHERE n.tipo = 'RECORDATORIO_CITA'
ON CONFLICT DO NOTHING;

-- Configuración adicional del sistema
INSERT INTO backups_sistema (fecha_creacion, ruta_archivo, metadata)
VALUES (CURRENT_TIMESTAMP, '/backups/backup_inicial.sql', '{"creadoPor":"script"}'::jsonb)
ON CONFLICT DO NOTHING;

INSERT INTO logs_sistema (fecha_hora, nivel, componente, mensaje, metadata)
VALUES (CURRENT_TIMESTAMP, 'INFO', 'bootstrap', 'Inicialización de datos de ejemplo completada', '{"script":"V1__init_schema.sql"}'::jsonb)
ON CONFLICT DO NOTHING;

-- Reportes e indicadores base
INSERT INTO fuentes_datos (nombre, tipo, configuracion) VALUES
    ('PostgreSQL Interno', 'DATABASE', '{"descripcion":"Fuente principal del sistema"}'::jsonb)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO estadisticas (nombre, valor, periodo_inicio, periodo_fin)
VALUES ('Ingresos Mensuales', 0, DATE_TRUNC('month', CURRENT_DATE)::date, (DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month - 1 day')::date)
ON CONFLICT (nombre, periodo_inicio, periodo_fin) DO NOTHING;

INSERT INTO indicadores (nombre, descripcion, valor_actual)
VALUES ('CitasPendientes', 'Cantidad de citas pendientes de atención', 0)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO reportes (nombre, tipo, generado_por, parametros)
SELECT 'Reporte Inicial de Citas', 'OPERATIVO', u.id_usuario, '{"descripcion":"Reporte vacío generado en la migración"}'::jsonb
FROM usuarios u
WHERE u.username = 'admin'
ON CONFLICT (nombre) DO NOTHING;

-- =====================================================
-- FIN DEL SCRIPT DE MIGRACIÓN V1
-- =====================================================

