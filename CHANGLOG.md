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
