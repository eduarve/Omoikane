ALTER TABLE articulos ADD COLUMN activo BIT(1) DEFAULT 1 NOT NULL;
ALTER TABLE articulos ADD INDEX (`activo`);

CREATE TABLE `Departamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT 1 NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `notas` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

ALTER TABLE articulos ADD COLUMN departamento_id BIGINT(20) DEFAULT NULL;
ALTER TABLE articulos ADD CONSTRAINT FK_Departamento_ID FOREIGN KEY(departamento_id) REFERENCES Departamento(id);
