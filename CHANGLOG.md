## Versión 4.1.1 (2014-03-09) Correcciones

- Corrección de Mepro. Sólo se podía editar, ejecutar y eliminar el último script de la lista
- Nuevo script que muestra el reporte de faltantes de inventario, también conocida como lista de resurtimiento.
- Corrección del cuadro de diálogo de mensajes de error: Los detalles del error ahora pueden verse maximizados.
- Mejora al cuadro de diálogo de mensaje de error: Botón para copiar los detalles (stacktrace) de un error al portapapeles
- Retoque de las plantillas de etiquetas
- Corrección del filtro de cortes de sucursal, no se podía filtrar por fecha
- Corrección del error que no permitía realizar cortes de caja (lanzaba un error)
- Corrección del panel de autenticación con NIP. Ahora se puede utilizar la tecla <Enter>.

## Versión 4.1.0 (2014-02-16) Catálogo de productos modo HA

- Cambie nomenclatura de versión, se elimina el intervalo de la
izquierda que siempre es 1, las versiones 1.4.x.x pasan a ser 4.x.x.
- Nuevo parámetro de configuración HA (stands for High Availability).
Por ahora activa y desactiva el origen de datos local para el catálogo
de artículos.
- Funcón HA. Catálogo de artículos ahora puede tomar datos desde una
base de datos local. La sincronización entre las BDs está desacoplada
de Omoikane y por ahora está implementada externamente con SymmetricDS.
- Mejoras en manejo de mensajes de error (tomadas de facturatron). El
mensaje default de error es el de la catarina y ahora el stacktrace se
muestra completo (incluyendo causas).
- Incluyo un ejemplo de caceso a datos usando MePro. La herramienta
para extender funcionalidad dinámicamente en tiempo de ejecución.
- Tiempo de arranque reducido, gracias a la eliminación de las
revisiones que hacía hibernate al comenzar la aplicación
- Reducción del código legado de Nadesico, varios métodos se migraron a
código nuevo.
- Ajustes al componente de compras
