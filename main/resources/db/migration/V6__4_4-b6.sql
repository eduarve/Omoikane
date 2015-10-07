
-- A partir de beta 6
ALTER TABLE ventas_detalles_impuestos DROP COLUMN ventaDetalleImpuestos_ORDER;

-- A partir de RC4
ALTER TABLE Compra ADD fecha_pago DATETIME;