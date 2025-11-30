# Consultas Avanzadas PostgreSQL - Sistema Veterinaria

Este documento contiene 10 consultas SQL avanzadas con subconsultas anidadas para validar el funcionamiento de la base de datos del sistema veterinario.

---

## 1. Clientes con más citas que el promedio

**Descripción**: Obtiene los clientes que tienen más citas programadas que el promedio de citas por cliente.

```sql
SELECT 
    c.id_cliente,
    p.nombre || ' ' || p.apellido AS nombre_completo,
    p.correo,
    COUNT(ci.id_cita) AS total_citas
FROM clientes c
INNER JOIN personas p ON c.id_cliente = p.id_persona
INNER JOIN pacientes pac ON pac.cliente_id = c.id_cliente
INNER JOIN citas ci ON ci.paciente_id = pac.id_paciente
GROUP BY c.id_cliente, p.nombre, p.apellido, p.correo
HAVING COUNT(ci.id_cita) > (
    SELECT AVG(citas_por_cliente)
    FROM (
        SELECT COUNT(ci2.id_cita) AS citas_por_cliente
        FROM clientes c2
        LEFT JOIN pacientes pac2 ON pac2.cliente_id = c2.id_cliente
        LEFT JOIN citas ci2 ON ci2.paciente_id = pac2.id_paciente
        GROUP BY c2.id_cliente
    ) AS subconsulta
)
ORDER BY total_citas DESC;
```

---

## 2. Veterinarios con mayor carga de trabajo en el último mes

**Descripción**: Identifica veterinarios que tienen más consultas registradas que el promedio en los últimos 30 días.

```sql
SELECT 
    uv.id_usuario_veterinario,
    p.nombre || ' ' || p.apellido AS veterinario,
    uv.especialidad,
    COUNT(con.id_registro_consulta) AS consultas_realizadas,
    (
        SELECT AVG(consultas_count)
        FROM (
            SELECT COUNT(con2.id_registro_consulta) AS consultas_count
            FROM usuarios_veterinarios uv2
            LEFT JOIN citas ci2 ON ci2.veterinario_id = uv2.id_usuario_veterinario
            LEFT JOIN registros_consultas con2 ON con2.cita_id = ci2.id_cita
            WHERE con2.fecha_consulta >= CURRENT_DATE - INTERVAL '30 days'
            GROUP BY uv2.id_usuario_veterinario
        ) AS avg_consultas
    ) AS promedio_consultas
FROM usuarios_veterinarios uv
INNER JOIN usuarios u ON u.id_usuario = uv.id_usuario_veterinario
INNER JOIN personas p ON p.id_persona = u.id_usuario
LEFT JOIN citas ci ON ci.veterinario_id = uv.id_usuario_veterinario
LEFT JOIN registros_consultas con ON con.cita_id = ci.id_cita
WHERE con.fecha_consulta >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY uv.id_usuario_veterinario, p.nombre, p.apellido, uv.especialidad
HAVING COUNT(con.id_registro_consulta) > (
    SELECT AVG(consultas_count)
    FROM (
        SELECT COUNT(con3.id_registro_consulta) AS consultas_count
        FROM usuarios_veterinarios uv3
        LEFT JOIN citas ci3 ON ci3.veterinario_id = uv3.id_usuario_veterinario
        LEFT JOIN registros_consultas con3 ON con3.cita_id = ci3.id_cita
        WHERE con3.fecha_consulta >= CURRENT_DATE - INTERVAL '30 days'
        GROUP BY uv3.id_usuario_veterinario
    ) AS avg_calc
)
ORDER BY consultas_realizadas DESC;
```

---

## 3. Pacientes con más vacunas aplicadas que otros de su misma especie

**Descripción**: Lista pacientes que tienen más vacunas que el promedio de su especie. FUNCIONA

```sql
SELECT 
    pac.id_paciente,
    pac.nombre AS nombre_paciente,
    pac.especie,
    pac.raza,
    COUNT(vac.id_vacunacion) AS total_vacunas,
    (
        SELECT AVG(vacunas_count)
        FROM (
            SELECT COUNT(v2.id_vacunacion) AS vacunas_count
            FROM pacientes p2
            LEFT JOIN vacunaciones v2 ON v2.paciente_id = p2.id_paciente
            WHERE p2.especie = pac.especie
            GROUP BY p2.id_paciente
        ) AS especies_promedio
    ) AS promedio_especie
FROM pacientes pac
LEFT JOIN vacunaciones vac ON vac.paciente_id = pac.id_paciente
GROUP BY pac.id_paciente, pac.nombre, pac.especie, pac.raza
HAVING COUNT(vac.id_vacunacion) > (
    SELECT AVG(vacunas_count)
    FROM (
        SELECT COUNT(v3.id_vacunacion) AS vacunas_count
        FROM pacientes p3
        LEFT JOIN vacunaciones v3 ON v3.paciente_id = p3.id_paciente
        WHERE p3.especie = pac.especie
        GROUP BY p3.id_paciente
    ) AS calc_promedio
)
ORDER BY total_vacunas DESC;
```

---

## 4. Productos de inventario con stock crítico y próximas a vencer

**Descripción**: Identifica productos con stock por debajo del promedio y que vencen en menos de 60 días.

```sql
SELECT 
    pi.id_producto,
    pi.nombre_producto,
    pi.categoria,
    pi.stock_actual,
    pi.fecha_vencimiento,
    (pi.fecha_vencimiento - CURRENT_DATE) AS dias_para_vencer,
    (
        SELECT AVG(stock_actual)
        FROM productos_inventario
        WHERE categoria = pi.categoria
    ) AS promedio_stock_categoria
FROM productos_inventario pi
WHERE pi.stock_actual < (
    SELECT AVG(stock_actual)
    FROM productos_inventario
    WHERE categoria = pi.categoria
)
AND pi.fecha_vencimiento <= CURRENT_DATE + INTERVAL '60 days'
AND pi.fecha_vencimiento IS NOT NULL
AND pi.stock_actual > 0
ORDER BY dias_para_vencer ASC, pi.stock_actual ASC;
```

---

## 5. Clientes con mascotas que necesitan desparasitación urgente

**Descripción**: Encuentra clientes cuyas mascotas tienen desparasitaciones vencidas o próximas a vencer comparado con el intervalo promedio de desparasitación.

```sql
SELECT 
    c.id_cliente,
    p.nombre || ' ' || p.apellido AS cliente,
    pac.nombre AS paciente,
    pac.especie,
    MAX(desp.fecha_desparasitacion) AS ultima_desparasitacion,
    CURRENT_DATE - MAX(desp.fecha_desparasitacion) AS dias_desde_ultima,
    (
        SELECT AVG(EXTRACT(DAY FROM (fecha_proxima - fecha_desparasitacion)))
        FROM desparasitaciones
        WHERE fecha_proxima IS NOT NULL
    ) AS intervalo_promedio_dias
FROM clientes c
INNER JOIN personas p ON p.id_persona = c.id_cliente
INNER JOIN pacientes pac ON pac.cliente_id = c.id_cliente
INNER JOIN desparasitaciones desp ON desp.paciente_id = pac.id_paciente
GROUP BY c.id_cliente, p.nombre, p.apellido, pac.id_paciente, pac.nombre, pac.especie
HAVING MAX(desp.fecha_desparasitacion) < CURRENT_DATE - INTERVAL '1 day' * (
    SELECT AVG(EXTRACT(DAY FROM (fecha_proxima - fecha_desparasitacion)))
    FROM desparasitaciones
    WHERE fecha_proxima IS NOT NULL
)
ORDER BY dias_desde_ultima DESC;
```

---

## 6. Facturas con monto superior al promedio mensual del cliente

**Descripción**: Identifica facturas que superan el promedio de gastos mensuales de cada cliente.

```sql
SELECT 
    f.id_factura,
    f.numero_factura,
    c.id_cliente,
    p.nombre || ' ' || p.apellido AS cliente,
    f.monto_total,
    f.fecha_emision,
    (
        SELECT AVG(f2.monto_total)
        FROM facturas f2
        WHERE f2.cliente_id = f.cliente_id
        AND f2.fecha_emision >= DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '6 months'
    ) AS promedio_mensual
FROM facturas f
INNER JOIN clientes c ON c.id_cliente = f.cliente_id
INNER JOIN personas p ON p.id_persona = c.id_cliente
WHERE f.monto_total > (
    SELECT AVG(f3.monto_total)
    FROM facturas f3
    WHERE f3.cliente_id = f.cliente_id
    AND f3.fecha_emision >= DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '6 months'
)
AND f.estado = 'PAGADA'
ORDER BY f.monto_total DESC;
```

---

## 7. Citas con nivel de triage más urgente por veterinario

**Descripción**: Lista citas con nivel de triage superior al promedio del veterinario asignado.

```sql
SELECT 
    ci.id_cita,
    ci.fecha_hora,
    ci.estado,
    ci.triage_nivel,
    pac.nombre AS paciente,
    p.nombre || ' ' || p.apellido AS veterinario,
    (
        SELECT AVG(CASE 
            WHEN triage_nivel = 'CRITICO' THEN 5
            WHEN triage_nivel = 'URGENTE' THEN 4
            WHEN triage_nivel = 'PRIORITARIO' THEN 3
            WHEN triage_nivel = 'NORMAL' THEN 2
            WHEN triage_nivel = 'NO_URGENTE' THEN 1
            ELSE 0
        END)
        FROM citas ci2
        WHERE ci2.veterinario_id = ci.veterinario_id
        AND ci2.estado IN ('PROGRAMADA', 'EN_CONSULTA')
    ) AS nivel_promedio_vet
FROM citas ci
INNER JOIN pacientes pac ON pac.id_paciente = ci.paciente_id
INNER JOIN usuarios_veterinarios uv ON uv.id_usuario_veterinario = ci.veterinario_id
INNER JOIN usuarios u ON u.id_usuario = uv.id_usuario_veterinario
INNER JOIN personas p ON p.id_persona = u.id_usuario
WHERE (CASE 
    WHEN ci.triage_nivel = 'CRITICO' THEN 5
    WHEN ci.triage_nivel = 'URGENTE' THEN 4
    WHEN ci.triage_nivel = 'PRIORITARIO' THEN 3
    WHEN ci.triage_nivel = 'NORMAL' THEN 2
    WHEN ci.triage_nivel = 'NO_URGENTE' THEN 1
    ELSE 0
END) > (
    SELECT AVG(CASE 
        WHEN triage_nivel = 'CRITICO' THEN 5
        WHEN triage_nivel = 'URGENTE' THEN 4
        WHEN triage_nivel = 'PRIORITARIO' THEN 3
        WHEN triage_nivel = 'NORMAL' THEN 2
        WHEN triage_nivel = 'NO_URGENTE' THEN 1
        ELSE 0
    END)
    FROM citas ci3
    WHERE ci3.veterinario_id = ci.veterinario_id
)
AND ci.estado IN ('PROGRAMADA', 'EN_CONSULTA')
ORDER BY ci.fecha_hora ASC;
```

---

## 8. Historial de seguimientos de pacientes con más registros que el promedio

**Descripción**: Pacientes con más seguimientos médicos registrados que el promedio general.

```sql
SELECT 
    pac.id_paciente,
    pac.nombre AS paciente,
    pac.especie,
    pac.raza,
    COUNT(seg.id_seguimiento) AS total_seguimientos,
    (
        SELECT AVG(seguimientos_count)
        FROM (
            SELECT COUNT(s2.id_seguimiento) AS seguimientos_count
            FROM pacientes p2
            LEFT JOIN registros_consultas rc2 ON rc2.paciente_id = p2.id_paciente
            LEFT JOIN seguimientos s2 ON s2.registro_consulta_id = rc2.id_registro_consulta
            GROUP BY p2.id_paciente
        ) AS avg_seguimientos
    ) AS promedio_seguimientos,
    STRING_AGG(DISTINCT seg.tipo_seguimiento, ', ') AS tipos_seguimiento
FROM pacientes pac
LEFT JOIN registros_consultas rc ON rc.paciente_id = pac.id_paciente
LEFT JOIN seguimientos seg ON seg.registro_consulta_id = rc.id_registro_consulta
GROUP BY pac.id_paciente, pac.nombre, pac.especie, pac.raza
HAVING COUNT(seg.id_seguimiento) > (
    SELECT AVG(seguimientos_count)
    FROM (
        SELECT COUNT(s3.id_seguimiento) AS seguimientos_count
        FROM pacientes p3
        LEFT JOIN registros_consultas rc3 ON rc3.paciente_id = p3.id_paciente
        LEFT JOIN seguimientos s3 ON s3.registro_consulta_id = rc3.id_registro_consulta
        GROUP BY p3.id_paciente
    ) AS calc_avg
)
ORDER BY total_seguimientos DESC;
```

---

## 9. Solicitudes de citas pendientes con tiempo de espera superior al promedio

**Descripción**: Identifica solicitudes de citas pendientes que llevan más tiempo esperando que el promedio.

```sql
SELECT 
    sc.id_solicitud,
    sc.estado,
    sc.motivo,
    sc.fecha_hora_solicitada,
    pac.nombre AS paciente,
    p.nombre || ' ' || p.apellido AS cliente,
    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - sc.created_at))/3600 AS horas_esperando,
    (
        SELECT AVG(EXTRACT(EPOCH FROM (COALESCE(updated_at, CURRENT_TIMESTAMP) - created_at))/3600)
        FROM solicitudes_citas
        WHERE estado IN ('PENDIENTE', 'APROBADA')
    ) AS promedio_horas_espera
FROM solicitudes_citas sc
INNER JOIN pacientes pac ON pac.id_paciente = sc.paciente_id
INNER JOIN clientes c ON c.id_cliente = pac.cliente_id
INNER JOIN personas p ON p.id_persona = c.id_cliente
WHERE sc.estado = 'PENDIENTE'
AND EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - sc.created_at))/3600 > (
    SELECT AVG(EXTRACT(EPOCH FROM (COALESCE(updated_at, CURRENT_TIMESTAMP) - created_at))/3600)
    FROM solicitudes_citas
    WHERE estado IN ('PENDIENTE', 'APROBADA')
)
ORDER BY horas_esperando DESC;
```

---

## 10. Reporte consolidado de clientes VIP con actividad superior al promedio

**Descripción**: Consulta compleja que identifica clientes con alta actividad (citas, consultas, facturas) comparado con múltiples promedios.

```sql
WITH cliente_stats AS (
    SELECT 
        c.id_cliente,
        COUNT(DISTINCT ci.id_cita) AS total_citas,
        COUNT(DISTINCT rc.id_registro_consulta) AS total_consultas,
        COUNT(DISTINCT f.id_factura) AS total_facturas,
        COALESCE(SUM(f.monto_total), 0) AS gasto_total,
        COUNT(DISTINCT pac.id_paciente) AS total_mascotas
    FROM clientes c
    LEFT JOIN pacientes pac ON pac.cliente_id = c.id_cliente
    LEFT JOIN citas ci ON ci.paciente_id = pac.id_paciente
    LEFT JOIN registros_consultas rc ON rc.cita_id = ci.id_cita
    LEFT JOIN facturas f ON f.cliente_id = c.id_cliente AND f.estado = 'PAGADA'
    GROUP BY c.id_cliente
),
promedios AS (
    SELECT 
        AVG(total_citas) AS avg_citas,
        AVG(total_consultas) AS avg_consultas,
        AVG(total_facturas) AS avg_facturas,
        AVG(gasto_total) AS avg_gasto
    FROM cliente_stats
)
SELECT 
    p.nombre || ' ' || p.apellido AS cliente_vip,
    p.correo,
    p.telefono,
    cs.total_citas,
    cs.total_consultas,
    cs.total_facturas,
    cs.gasto_total,
    cs.total_mascotas,
    ROUND((cs.total_citas::NUMERIC / NULLIF(pr.avg_citas, 0)) * 100, 2) AS porcentaje_vs_promedio_citas,
    ROUND((cs.gasto_total::NUMERIC / NULLIF(pr.avg_gasto, 0)) * 100, 2) AS porcentaje_vs_promedio_gasto,
    (
        SELECT STRING_AGG(pac.nombre || ' (' || pac.especie || ')', ', ')
        FROM pacientes pac
        WHERE pac.cliente_id = cs.id_cliente
    ) AS mascotas
FROM cliente_stats cs
CROSS JOIN promedios pr
INNER JOIN clientes c ON c.id_cliente = cs.id_cliente
INNER JOIN personas p ON p.id_persona = c.id_cliente
WHERE cs.total_citas > pr.avg_citas
AND cs.total_consultas > pr.avg_consultas
AND cs.gasto_total > pr.avg_gasto
ORDER BY cs.gasto_total DESC, cs.total_citas DESC
LIMIT 20;
```

---

## Instrucciones de Uso

1. **Conexión a PostgreSQL**:
   ```bash
   psql -U postgres -d veterinaria_db
   ```

2. **Ejecutar consultas**:
   - Copie y pegue cada consulta SQL en el cliente PostgreSQL
   - Las consultas están optimizadas para la estructura actual de la base de datos
   - Algunas pueden no devolver resultados si no hay suficientes datos de prueba

3. **Validación de Resultados**:
   - Cada consulta incluye subconsultas anidadas para cálculos complejos
   - Compare los promedios calculados con los valores individuales
   - Verifique que las condiciones HAVING funcionen correctamente

4. **Consideraciones**:
   - Estas consultas asumen que existen datos en las tablas correspondientes
   - Algunas consultas pueden tardar según el volumen de datos
   - Los índices en las tablas mejorarán significativamente el rendimiento

---

## Índices Recomendados para Optimización

```sql
-- Índices para mejorar el rendimiento de estas consultas
CREATE INDEX IF NOT EXISTS idx_citas_paciente_veterinario ON citas(paciente_id, veterinario_id, estado);
CREATE INDEX IF NOT EXISTS idx_citas_fecha_hora ON citas(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_consultas_cita_paciente ON registros_consultas(cita_id, paciente_id);
CREATE INDEX IF NOT EXISTS idx_vacunaciones_paciente ON vacunaciones(paciente_id, fecha_aplicacion);
CREATE INDEX IF NOT EXISTS idx_desparasitaciones_paciente ON desparasitaciones(paciente_id, fecha_desparasitacion);
CREATE INDEX IF NOT EXISTS idx_facturas_cliente_estado ON facturas(cliente_id, estado, fecha_emision);
CREATE INDEX IF NOT EXISTS idx_seguimientos_consulta ON seguimientos(registro_consulta_id);
CREATE INDEX IF NOT EXISTS idx_solicitudes_estado ON solicitudes_citas(estado, created_at);
CREATE INDEX IF NOT EXISTS idx_inventario_categoria ON productos_inventario(categoria, stock_actual);
CREATE INDEX IF NOT EXISTS idx_pacientes_cliente_especie ON pacientes(cliente_id, especie);
```

---

**Fecha de Creación**: 28 de Noviembre de 2025  
**Sistema**: Veterinaria Humboldt v1.0.0  
**Base de Datos**: PostgreSQL 18.0+
