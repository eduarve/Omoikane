 CREATE TABLE `TraspasoSaliente` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `aplicado` bit(1) DEFAULT NULL,
   `completado` bit(1) DEFAULT NULL,
   `fecha` datetime DEFAULT NULL,
   `id_usuario` bigint(20) DEFAULT NULL,
   `almacenDestino` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
   `almacenOrigen` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
   `uid` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
   `notas` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `FK802122261D916F4` (`id_usuario`),
   KEY `completadoIndex` (`completado`),
   CONSTRAINT `FK802122261D916F4` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
 ) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

CREATE TABLE `TraspasoSaliente_items` (
  `TraspasoSaliente_id` bigint(20) NOT NULL,
  `articulo_id_articulo` int(11) DEFAULT NULL,
  `codigo` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `conteo` decimal(19,2) DEFAULT NULL,
  `costoUnitario` decimal(19,2) DEFAULT NULL,
  `diferencia` decimal(19,2) DEFAULT NULL,
  `nombre` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `stockDB` decimal(19,2) DEFAULT NULL,
  `items_ORDER` int(11) NOT NULL,
  `cantidad` decimal(19,2) DEFAULT NULL,
  `precioPublico` decimal(19,2) DEFAULT NULL,
  PRIMARY KEY (`TraspasoSaliente_id`,`items_ORDER`),
  KEY `FK5D5ABB63B3E87FB` (`articulo_id_articulo`),
  KEY `FK5D5ABB63C70B00F1` (`TraspasoSaliente_id`),
  CONSTRAINT `FK5D5ABB63B3E87FB` FOREIGN KEY (`articulo_id_articulo`) REFERENCES `articulos` (`id_articulo`),
  CONSTRAINT `FK5D5ABB63C70B00F1` FOREIGN KEY (`TraspasoSaliente_id`) REFERENCES `TraspasoSaliente` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

CREATE TABLE `TraspasoEntrante` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `almacenDestino` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `almacenOrigen` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `aplicado` bit(1) DEFAULT NULL,
  `completado` bit(1) DEFAULT NULL,
  `fecha` datetime DEFAULT NULL,
  `notas` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `id_usuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1786674261D916F4` (`id_usuario`),
  KEY `completadoIndex` (`completado`),
  CONSTRAINT `FK1786674261D916F4` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

CREATE TABLE `TraspasoEntrante_items` (
  `TraspasoEntrante_id` bigint(20) NOT NULL,
  `articulo_id_articulo` int(11) DEFAULT NULL,
  `cantidad` decimal(19,2) DEFAULT NULL,
  `codigo` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `costoUnitario` decimal(19,2) DEFAULT NULL,
  `nombre` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `precioPublico` decimal(19,2) DEFAULT NULL,
  `stockDB` decimal(19,2) DEFAULT NULL,
  `items_ORDER` int(11) NOT NULL,
  PRIMARY KEY (`TraspasoEntrante_id`,`items_ORDER`),
  KEY `FKC852B883B3E87FB` (`articulo_id_articulo`),
  KEY `FKC852B883FA89FBD1` (`TraspasoEntrante_id`),
  CONSTRAINT `FKC852B883FA89FBD1` FOREIGN KEY (`TraspasoEntrante_id`) REFERENCES `TraspasoEntrante` (`id`),
  CONSTRAINT `FKC852B883B3E87FB` FOREIGN KEY (`articulo_id_articulo`) REFERENCES `articulos` (`id_articulo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;