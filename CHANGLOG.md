## Versión 4.2 alfa (2014-03-29)

- Agregué soporte para resolución 1024x768
- Soporte multi-cliente en caja
- Soporta un nivel de precios por cliente en caja
- Corrige bug que agrupaba de manera incorrecta los productos, solo producía efectos negativos cuando una venta con productos agrupados se cerraba y volvía a abrir (se resumía)
- Ajustes estéticos en ventana de caja, el texto en los botones ya no se corta
- Se reduce el tiempo de espera en el buscador incorporado, de 2 segundos a 1
- En caja: Corregí el error que enviaba el enfoque al campo “Cambio” en una venta nueva en lugar del campo “Captura”
- Cargo las compras de una manera mucho más eficiente
- Corregí el mensaje de error que decía “Error al iniciar aplicación” cuando la razón no tenía nada que ver con el inicio de la aplicación
- Corregí el comportamiento no deseado de recibir enfoque en la tabla de impuestos en la ventana de “Artículo” que detenía el ciclo de enfoque al capturar un producto
- Reincorporé:
  - Entidad cliente
  - Catálogo de clientes
  - Cliente asignado a una venta
- Cambié el efecto de “Fondo borroso” de las ventanas del filtro BoxBlur a Pointillize
- Aumenté la opacidad del fondo de las ventanas semitransparentes
- Actualicé el splash de la aplicación
- Corregí el bug que impedía reimprimir cortes de caja
- Corregí el bug que impedía reimprimir tickets de venta
- Activé el registro de excepciones no manejadas
- Un producto puede tener varios niveles de precios, también llamados en el sistema “precios alternos”
- Reduje la dependencia del “Escritorio” de la aplicación, éste es un paso más que permite abrir muchas ventanas del sistema en escritorios personalizados o en una interfaz javafx
- Actualicé la vista “base_para_precios” para incluir la información de niveles de precios / precios secundarios / precios alternos o como quieran llamarlos
- Introduje información de prueba (fixtures) de las nuevas entidades “lista de precios” y “precio alterno”
- Introduje test unitario de precios alternos
- Introduje las entidades “PrecioAlterno” y “ListaDePrecios”
- Introduje tests no-unitarios (que actúan como accesos directos a pantallas del sistema): Catálogo de cliente y cliente

## Versión 4.1.2 (2014-03-27)

- Mejora sustacial del instalador, ahora además de copiar omoikane hace lo siguiente:
  - Incluye su propio JRE 7u51 independiente del que tenga (o no tenga) la PC
  - Tiene un lanzador nativo, ya no se muestra la ventana de la consola del sistema
  - Instala su propio servidor MySQL y lo inicia como servicio
  - Crea y llena el esquema inicial de la base de datos
  - Deja listo omoikane para comenzar a usarse en ese mismo momento 
- Características preliminares y ejemplos de Mepro, escritos en groovy y sin compilar, se pueden usar desde la opción "Scripting", además se pueden modificar:
  - Multi-impresor: Herramienta de impresión masiva de tickets de venta, se pueden imprimir todos los tickets a partir de x fecha.
  - Etiquetor: Herramienta para impresión rápida de "etiquetas", al pasar un producto genera al instante la etiqueta
  - GroovyFX: Ejemplo de creación de GUI usando GroovyFX en Mepro
  - PruebaFXGui: Ejemplo de creación rápida de un formulario en Mepro usando JavaFX y FXForm
- Corrección de error en las plantillas de etiquetas
- Actualización de la biblioteca FXForm
- Corrección (#78, #79 y #80) al esquema inicial ahora incluye: Cliente inicial, impuestos iniciales (IVA e IEPS) y `Concepto`
- Corrección de precios erróneos en catálogo de artículos (#76)
- Corrección de falla al reimprimir tickets (#80)
- Cambio del esquema modelo a de utf8 a latin1, por compatibilidad con SymmetricDS
- [Artemisa] Se muestra el ID de la cuenta del paciente en la pantalla "Caja clínica"		

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
