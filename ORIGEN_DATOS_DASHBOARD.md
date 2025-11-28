# 📊 Origen de los Datos del Dashboard Administrativo

## Resumen General

Todos los datos que ves en el dashboard provienen de la **base de datos PostgreSQL**, específicamente de las siguientes tablas:

---

## 💰 Ingresos Totales

### Origen: Tabla `facturas`

```sql
SELECT SUM(total) 
FROM facturas 
WHERE estado = 'PAGADA'
```

### Detalles:
- **Campo**: `total` (tipo: `NUMERIC(14,2)`)
- **Filtro**: Solo facturas con estado = `'PAGADA'`
- **Cálculo**: Suma de todos los valores del campo `total`
- **NO son datos quemados**: Son valores reales de la base de datos

### ¿De dónde viene el valor del campo `total` de cada factura?

El campo `total` en la tabla `facturas` se calcula cuando se crea una factura, basándose en:

1. **Servicios prestados en la cita**
2. **Productos vendidos** (medicamentos, alimentos, accesorios)
3. **Tratamientos aplicados** (vacunaciones, desparasitaciones)
4. **Impuestos y descuentos**

**Estructura de la tabla facturas:**
```sql
CREATE TABLE facturas (
    id_factura BIGSERIAL PRIMARY KEY,
    numero VARCHAR(50) UNIQUE NOT NULL,
    fecha_emision TIMESTAMP NOT NULL,
    total NUMERIC(14,2) NOT NULL CHECK (total >= 0),
    forma_pago VARCHAR(50),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'PAGADA', 'ANULADA')),
    cliente_id BIGINT NOT NULL REFERENCES usuarios(id_usuario),
    contenido JSONB -- Detalles de la factura en formato JSON
);
```

---

## 📈 Estadísticas Financieras

### 1. **Ingresos del Mes Actual**
- **Origen**: Tabla `facturas`
- **Filtro**: `fecha_emision` >= primer día del mes actual AND `estado = 'PAGADA'`
- **Cálculo**: `SUM(total)` de facturas del mes

### 2. **Ingresos del Mes Anterior**
- **Origen**: Tabla `facturas`
- **Filtro**: `fecha_emision` del mes pasado AND `estado = 'PAGADA'`
- **Cálculo**: `SUM(total)` de facturas del mes anterior

### 3. **Porcentaje de Crecimiento**
- **Cálculo**: `((IngresosMesActual - IngresosMesAnterior) / IngresosMesAnterior) * 100`

### 4. **Facturas Pendientes**
- **Origen**: Tabla `facturas`
- **Filtro**: `estado = 'PENDIENTE'`
- **Cálculo**: `COUNT(*)` y `SUM(total)`

### 5. **Promedio de Ingreso por Cita**
- **Cálculo**: `IngresosMesActual / CitasRealizadas`
- **Origen**: Dividir ingresos totales entre número de citas completadas

---

## 🏥 Métricas de Pacientes

### 1. **Total de Pacientes**
- **Origen**: Tabla `pacientes`
- **Cálculo**: `COUNT(*)`

### 2. **Citas del Mes**
- **Origen**: Tabla `citas`
- **Filtro**: `fecha_hora` del mes actual AND `estado = 'REALIZADA'`
- **Cálculo**: `COUNT(*)`

### 3. **Citas Pendientes**
- **Origen**: Tabla `citas`
- **Filtro**: `estado = 'PROGRAMADA'`
- **Cálculo**: `COUNT(*)`

---

## 📦 Estado del Inventario

### 1. **Total de Productos**
- **Origen**: Tabla `productos`
- **Cálculo**: `COUNT(*)`

### 2. **Productos con Stock Bajo**
- **Origen**: Tabla `productos`
- **Filtro**: `stock <= stock_minimo`
- **Cálculo**: `COUNT(*)`

### 3. **Productos Agotados**
- **Origen**: Tabla `productos`
- **Filtro**: `stock = 0`
- **Cálculo**: `COUNT(*)`

### 4. **Valor Total del Inventario**
- **Origen**: Tabla `productos`
- **Cálculo**: `SUM(stock * precio_unitario)`

---

## 👥 Rendimiento del Personal

### 1. **Total de Veterinarios**
- **Origen**: Tabla `usuarios` JOIN `usuarios_veterinarios`
- **Filtro**: `rol_id` corresponde a "VETERINARIO" AND `activo = true`
- **Cálculo**: `COUNT(*)`

### 2. **Veterinario más Productivo**
- **Origen**: Tabla `citas` JOIN `usuarios_veterinarios`
- **Filtro**: Contar citas realizadas por cada veterinario
- **Cálculo**: Veterinario con mayor `COUNT(*)` de citas

---

## 📊 Gráficos

### 1. **Gráfico de Ingresos Mensuales** (6 meses)
```sql
SELECT 
    EXTRACT(MONTH FROM fecha_emision) as mes,
    SUM(total) as ingresos
FROM facturas
WHERE estado = 'PAGADA'
  AND fecha_emision >= (CURRENT_DATE - INTERVAL '6 months')
GROUP BY EXTRACT(MONTH FROM fecha_emision)
ORDER BY mes
```

### 2. **Distribución de Servicios** (Gráfico Circular)
```sql
SELECT 
    tipo_servicio,
    COUNT(*) as cantidad
FROM citas
WHERE estado = 'REALIZADA'
GROUP BY tipo_servicio
```

### 3. **Tendencia de Clientes Nuevos**
```sql
SELECT 
    EXTRACT(MONTH FROM fecha_registro) as mes,
    COUNT(*) as clientes_nuevos
FROM clientes
WHERE fecha_registro >= (CURRENT_DATE - INTERVAL '6 months')
GROUP BY EXTRACT(MONTH FROM fecha_registro)
ORDER BY mes
```

---

## 🔍 Respuestas a tus Preguntas

### ❓ "¿De dónde sacas el precio que usas para los ingresos totales?"

**Respuesta**: Del campo `total` de la tabla `facturas` en PostgreSQL.

### ❓ "¿Son datos quemados?"

**Respuesta**: **NO**. Son datos **100% reales** extraídos de la base de datos mediante consultas SQL ejecutadas por el backend de Spring Boot.

### ❓ "¿Es el valor como tal de la cita?"

**Respuesta**: No exactamente. Es el valor de la **factura** que se genera después de una cita. Una factura puede incluir:
- **Costo de la consulta veterinaria**
- **Medicamentos vendidos**
- **Tratamientos aplicados** (vacunas, desparasitaciones)
- **Productos adicionales**
- **Impuestos**

---

## 🛠️ Mejoras Implementadas

### Antes (Incorrecto):
```java
// Sumaba TODAS las facturas (incluyendo PENDIENTES y ANULADAS)
BigDecimal ingresosTotales = facturaRepository.findAll().stream()
    .map(f -> f.getTotal())
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

### Después (Correcto):
```java
// Solo suma facturas PAGADAS (ingresos reales confirmados)
BigDecimal ingresosTotales = facturaRepository.findAll().stream()
    .filter(f -> "PAGADA".equals(f.getEstado()))
    .map(f -> f.getTotal() != null ? f.getTotal() : BigDecimal.ZERO)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

### Ventajas:
✅ Los ingresos reflejan solo dinero **realmente cobrado**  
✅ No se cuentan facturas pendientes como ingreso  
✅ Se excluyen facturas anuladas  
✅ Más preciso para análisis financiero  

---

## 📋 Resumen Final

| Métrica | Tabla | Campo Clave | Filtros |
|---------|-------|-------------|---------|
| **Ingresos Totales** | `facturas` | `total` | `estado = 'PAGADA'` |
| **Ingresos del Mes** | `facturas` | `total` | `fecha_emision` + `estado = 'PAGADA'` |
| **Citas Realizadas** | `citas` | - | `estado = 'REALIZADA'` |
| **Pacientes Activos** | `pacientes` | - | `COUNT(*)` |
| **Stock Bajo** | `productos` | `stock` | `stock <= stock_minimo` |
| **Veterinarios** | `usuarios` + `usuarios_veterinarios` | - | `rol = 'VETERINARIO'` |

---

## 🎯 Conclusión

**TODOS los datos son reales y provienen de la base de datos PostgreSQL.**  
El dashboard no usa datos "quemados" o ficticios. Todo se calcula dinámicamente cada vez que accedes al dashboard mediante consultas SQL ejecutadas por el servicio `AdminDashboardService.java`.

Si ves $45,000.00 en "Ingresos Totales", significa que tienes facturas PAGADAS en tu base de datos que suman exactamente esa cantidad.
