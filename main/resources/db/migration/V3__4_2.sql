
-- Cambios en almacenamiento de precios alternos, de tabla secundaria a metadatos en una columna
ALTER TABLE precios ADD COLUMN preciosAlternos TEXT DEFAULT NULL COMMENT 'Almacena los metadatos de factores de utilidad de precios alternos';

DROP TABLE IF EXISTS articulos_PrecioAlterno;
DELETE FROM precioAlterno;
DROP TABLE IF EXISTS precioAlterno;

CREATE OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER
VIEW
	`base_para_precios` AS
		select
			`a`.`id_articulo` AS `id_articulo`,
			`a`.`descripcion` AS `descripcion`,
			`p`.`costo` AS `costo`,
			`a`.`impuestos` AS `porcentajeImpuestos`,
			`l`.`descuento` AS `porcentajeDescuentoLinea`,
			`g`.`descuento` AS `porcentajeDescuentoGrupo`,
			`p`.`descuento` AS `porcentajeDescuentoProducto`,
			`p`.`utilidad` AS `porcentajeUtilidad`,
			`p`.`preciosAlternos` AS `preciosAlternos`
		from (((`articulos` `a` join `precios` `p`) join `lineas` `l`) join `grupos` `g`)
		where
				((`a`.`id_articulo` = `p`.`id_articulo`)
			and
				(`a`.`id_linea` = `l`.`id_linea`)
			and
				(`a`.`id_grupo` = `g`.`id_grupo`));


-- Cambios en almacenamiento de impuestos, de tabla secundaria a metadatos en una columna
ALTER TABLE precios ADD COLUMN impuestos TEXT DEFAULT NULL COMMENT 'Almacena los metadatos de los impuestos';
