-- ============================================
-- SCRIPT PARA CREAR CLIENTE Y SECRETARIO DE PRUEBA
-- ============================================
-- Usuario: cliente_demo
-- Password: Cliente123!
-- Usuario: secretario_demo2
-- Password: Secretario123!
-- ============================================

-- Obtener IDs de roles
DO $$
DECLARE
    v_rol_cliente_id BIGINT;
    v_rol_secretario_id BIGINT;
    v_persona_cliente_id BIGINT;
    v_persona_secretario_id BIGINT;
    v_usuario_cliente_id BIGINT;
    v_usuario_secretario_id BIGINT;
BEGIN
    -- Obtener ID del rol CLIENTE
    SELECT id_rol INTO v_rol_cliente_id FROM roles WHERE nombre_rol = 'CLIENTE';
    
    -- Obtener ID del rol SECRETARIO
    SELECT id_rol INTO v_rol_secretario_id FROM roles WHERE nombre_rol = 'SECRETARIO';

    -- ============================================
    -- CREAR CLIENTE DE PRUEBA
    -- ============================================
    
    -- Verificar si el cliente ya existe
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'cliente_demo') THEN
        -- 1. Crear persona para el cliente
        INSERT INTO personas (nombre, apellido, correo, telefono, direccion)
        VALUES ('María', 'González', 'maria.gonzalez@email.com', '3001234567', 'Calle 20 #15-30')
        RETURNING id_persona INTO v_persona_cliente_id;

        -- 2. Crear usuario cliente con hash de "Cliente123!"
        INSERT INTO usuarios (id_usuario, username, password_hash, rol_id, activo, ultimo_acceso)
        VALUES (
            v_persona_cliente_id,
            'cliente_demo',
            '$2a$10$UlnrsohdzunjHfm8GjIFE.dXEJM/L59jvHJSZUQ4d3yXz0lDnkqzm', -- Hash de "Cliente123!"
            v_rol_cliente_id,
            true,
            CURRENT_TIMESTAMP
        )
        RETURNING id_usuario INTO v_usuario_cliente_id;

        -- 3. Crear entrada en tabla clientes
        INSERT INTO clientes (id_usuario, documento_identidad, fecha_registro)
        VALUES (
            v_usuario_cliente_id,
            '1098765432',
            CURRENT_DATE
        );

        RAISE NOTICE '✅ Cliente creado exitosamente';
        RAISE NOTICE '   Username: cliente_demo';
        RAISE NOTICE '   Password: Cliente123!';
        RAISE NOTICE '   Email: maria.gonzalez@email.com';
    ELSE
        RAISE NOTICE '⚠️ El cliente cliente_demo ya existe, omitiendo creación';
    END IF;

    -- ============================================
    -- CREAR SECRETARIO DE PRUEBA
    -- ============================================
    
    -- Verificar si el secretario ya existe
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'secretario_demo2') THEN
        -- 1. Crear persona para el secretario
        INSERT INTO personas (nombre, apellido, correo, telefono, direccion)
        VALUES ('Carlos', 'Méndez', 'carlos.mendez@veterinaria.com', '3109876543', 'Carrera 10 #25-40')
        RETURNING id_persona INTO v_persona_secretario_id;

        -- 2. Crear usuario secretario con hash de "Secretario123!"
        INSERT INTO usuarios (id_usuario, username, password_hash, rol_id, activo, ultimo_acceso)
        VALUES (
            v_persona_secretario_id,
            'secretario_demo2',
            '$2a$10$UlnrsohdzunjHfm8GjIFE.dXEJM/L59jvHJSZUQ4d3yXz0lDnkqzm', -- Hash de "Secretario123!"
            v_rol_secretario_id,
            true,
            CURRENT_TIMESTAMP
        )
        RETURNING id_usuario INTO v_usuario_secretario_id;

        -- 3. Crear entrada en tabla secretarios
        INSERT INTO secretarios (id_usuario, extension)
        VALUES (
            v_usuario_secretario_id,
            '101'
        );

        RAISE NOTICE '✅ Secretario creado exitosamente';
        RAISE NOTICE '   Username: secretario_demo2';
        RAISE NOTICE '   Password: Secretario123!';
        RAISE NOTICE '   Email: carlos.mendez@veterinaria.com';
        RAISE NOTICE '   Extensión: 101';
    ELSE
        RAISE NOTICE '⚠️ El secretario secretario_demo2 ya existe, omitiendo creación';
    END IF;

END $$;

-- ============================================
-- VERIFICACIÓN
-- ============================================
SELECT 
    u.username,
    p.nombre,
    p.apellido,
    p.correo,
    r.nombre_rol as rol,
    u.activo,
    CASE 
        WHEN c.id_usuario IS NOT NULL THEN 'CLIENTE'
        WHEN s.id_usuario IS NOT NULL THEN 'SECRETARIO'
        ELSE 'OTRO'
    END as tipo_usuario
FROM usuarios u
JOIN personas p ON u.id_usuario = p.id_persona
JOIN roles r ON u.rol_id = r.id_rol
LEFT JOIN clientes c ON u.id_usuario = c.id_usuario
LEFT JOIN secretarios s ON u.id_usuario = s.id_usuario
WHERE u.username IN ('cliente_demo', 'secretario_demo2')
ORDER BY u.username;
