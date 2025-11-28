-- ============================================
-- Script para configurar el canal de Email con Gmail SMTP
-- ============================================
-- Este script configura o actualiza el canal de email en la base de datos
-- para poder enviar notificaciones por correo electrónico.

-- 1. Verificar si existe un canal de tipo EMAIL
SELECT * FROM canales_envio WHERE tipo = 'EMAIL';

-- 2. Si no existe, insertar un nuevo canal de EMAIL
INSERT INTO canales_envio (tipo, activo)
SELECT 'EMAIL', true
WHERE NOT EXISTS (
    SELECT 1 FROM canales_envio WHERE tipo = 'EMAIL'
);

-- 3. Obtener el ID del canal EMAIL
DO $$
DECLARE
    email_canal_id BIGINT;
BEGIN
    -- Buscar el ID del canal EMAIL
    SELECT id_canal INTO email_canal_id 
    FROM canales_envio 
    WHERE tipo = 'EMAIL' 
    LIMIT 1;

    -- Si no se encontró, mostrar error
    IF email_canal_id IS NULL THEN
        RAISE EXCEPTION 'No se encontró el canal EMAIL. Ejecuta primero el INSERT de canales_envio.';
    END IF;

    -- Insertar o actualizar la configuración del canal EMAIL
    INSERT INTO canales_email (id_canal, smtp_server, from_address)
    VALUES (
        email_canal_id,
        'smtp.gmail.com',
        'tu-email@gmail.com'  -- 🔴 CAMBIAR POR TU EMAIL REAL
    )
    ON CONFLICT (id_canal) 
    DO UPDATE SET
        smtp_server = 'smtp.gmail.com',
        from_address = 'tu-email@gmail.com';  -- 🔴 CAMBIAR POR TU EMAIL REAL

    RAISE NOTICE 'Canal de email configurado exitosamente con ID: %', email_canal_id;
END $$;

-- 4. Verificar la configuración
SELECT 
    ce.id_canal,
    ce.tipo,
    ce.activo,
    cem.smtp_server,
    cem.from_address
FROM canales_envio ce
LEFT JOIN canales_email cem ON ce.id_canal = cem.id_canal
WHERE ce.tipo = 'EMAIL';

-- 5. Resultado esperado:
-- id_canal | tipo  | activo | smtp_server      | from_address
-- ---------+-------+--------+------------------+------------------------
--        1 | EMAIL | true   | smtp.gmail.com   | tu-email@gmail.com
