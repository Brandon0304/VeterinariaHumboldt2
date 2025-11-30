-- =====================================================
-- Migración V9: Agregar campo fecha_pago a facturas
-- =====================================================
-- Descripción: Agrega el campo fecha_pago para registrar
--             cuándo se realizó el pago de la factura.
-- Autor: Sistema
-- Fecha: 2025-11-29
-- =====================================================

-- Agregar columna fecha_pago (opcional - solo para facturas pagadas)
ALTER TABLE facturas 
ADD COLUMN IF NOT EXISTS fecha_pago TIMESTAMP;

-- Agregar comentario descriptivo
COMMENT ON COLUMN facturas.fecha_pago IS 'Fecha y hora en que se registró el pago de la factura';

-- Actualizar facturas ya pagadas con fecha de modificación como fecha de pago aproximada
UPDATE facturas 
SET fecha_pago = updated_at 
WHERE estado = 'PAGADA' 
  AND fecha_pago IS NULL 
  AND updated_at IS NOT NULL;
