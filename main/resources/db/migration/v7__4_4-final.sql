-- Cambiar el nombre de la columna folioOrigen a folio_origen en tabla Compras.
alter table Compra change folioOrigen folio_origen varchar(255);

-- 	Agrandar las notas de:
alter table TraspasoSaliente change column notas notas longtext;
alter table TraspasoEntrante change column notas notas longtext;

-- 	Estandarizar los nombres de tablas de Compra a snake_case:
rename table Compra to compra;
rename table Compra_items to compra_items;

-- Estandarizado de los nombres de cojlumnas de compra y compra_items a snake_case
alter table compra change column estadoPago estado_pago int(11);
alter table compra_items change column costoUnitario costo_unitario decimal(19,2);