## Versión 4.4.0 rc3 (2015-08-15)
- #125 Desactivé la tecla ESC para descartar: inventarios, compras y traspasos
- #124 Arreglé bug que impedía visualizar movimientos de almacén
- Arreglé bug que insertaba el resultado de la búsqueda rápida en la venta, en lugar del contenido del campo de captura
- Mejoré plantilla de impresión del listado de movimientos de almacén

## Versión 4.4.0 rc2 (2015-08-14)
- #121 Agregué perfil de cancelación y auto-cancelación, supervisor y gerente ya no pueden auto-cancelar
- Agregué campo de folio al formato impreso de los traspasos
- Arreglé la búsqueda de caja que había quedado dañada por la actualización de java
- #118 Arreglé el panel de etiquetas, que por actualización de java no mostraba correctamente las celdas
- #120 Las cancelaciones no eran navegables por teclado por actualización de java
- #117 No funcionaba switcheo de cambio de cliente desde java 7u40
- Detalles de artículo no abría cuando no había anotaciones
- Grupo y línea mostraban la línea en detalles de artículo

## Versión 4.4.0 rc1 (2015-08-11)
- Optimización del tiempo de apertura de la ventana de detalles/modificar artículo. 4x más rápida, 3 consultas que realizaban una conexión cada una fueron sustituidas por una que utiliza una
conexión del pool 

## Versión 4.4.0 b14 (2015-08-10)
- Se corrige bug que permitía inyectar código SQL en el catálogo de artículos y generaba un error buscando productos con apóstrofes, comillas simples, acentos sin letra.

## Versión 4.4.0 b13 (2015-08-05)
- Agregué un nuevo método "getCostoNeto" a la lógico de precios, obtiene el costo con impuestos
- Ahora los traspasos salientes muestran el costo neto (con impuestos) en lugar del costo bruto
- Corregí bug que permitía borrar partidas en traspasos
- Mejoré la plantilla de traspasos salientes, agregando espacio para las firmas y corrigiendo el formato de los números

## Versión 4.4.0 b12 (2015-08-04)
- Corregí bug que mostraba el total de un traspaso de mercancía en base al precio al público en lugar del costo
- Corregí un bug, no se marcaban las compras como completadas

## Versión 4.4.0 b11 (2015-07-26)
- Los reportes básicos ahora apuntan a la configuración general del sistema y no a su propia clase
que leía directo del archivo y no trabajaba en conjunto con el proxy de sucursales

## Versión 4.4.0 b10 (2015-07-25)
- Quité la persistencia durante la edición de Compras, ahora toda la compra se guarda una 
única ocasión y se agregan stocks todo en una misma sesión
- Corregí los reportes básicos que quedaban pendientes: costeo por grupos y líneas y artículos por línea
- Corregí un bug que ocasionaba que no se pudieran agregar códigos alternos, siempre 
marcaba "código repetido"

## Versión 4.4.0 b9 (2015-07-15)
- Cambié el límite de pacientes listados simultáneamente para artemisa de 35 a 10 000
- Agregué un flush forzado a cada operación de compras
- Quité Cascade en la relación Many-to-Many Articulos-Impuestos

## Versión 4.4.0 b8 (2015-07-12)
- Se corrige el bug que no permitía generar etiquetas debido a un lazy initialization exception de impuestos.

## Versión 4.4.0 b7
- Se corrige el enfoque después de buscar productos mediante el catálogo (F1) en inventarios y compras, el enfoque se perdía ralentizando la captura
- Se agrega el acceso a traspasos entrantes y salientes

## Versión 4.4.0 b6 (2015-07-06)
- Se corrige la relación VentaDetalles - VentaDetallesImpuesto. Ahora VDI tiene una clave primaria compuesta por el ID de VentaDetalles y el ID del impuesto.

## Versión 4.3.0 (2014-08-02)

- Se agrega reporte remoto de excepciones mediante airbrake
- Departamentos, nuevo característica que da soporte para clasificación por productos por departamentos
- CRUD de departamentos en panel de configuración
- Panel de configuración redimensionable 
- NIP de usuario ahora acepta números con longitud de 4 a 8 dígitos
- Panel de confección de etiquetas ahora acepta códigos secundarios
- Incrementé el tamaño del panel de artículos para dar más espacio a las opciones recientemente añadidas
- Capacidad para eliminar mediante desactivación los productos, ésta técnica también es llamada soft delete
- Agregué una nueva columna de opciones en el menú principal reduciendo el ancho de todos los botones
- CRUD de clientes ahora es accesible desde el menú principal
- Panel de reportes avanzados. Éste panel se conecta a un servidor Jasper Server para obtener características más avanzadas y dinámicas en los reportes.
- Nuevos settings opcionales en config.xml: URLJasperserver, loginJasperserver, passJasperserver, multiSucursal, isFlywayActive.
- Multi-sucursal. Ahora es posible conectarse a otras sucursales al iniciar el sistema, se deben realizar archivos de configuración para cada sucursal con el formato [Nombre de sucursal].config.xml y activar el parámetros opcional “<multiSucursal>true</multiSucursal>”  en el archivo de configuración principal AKA “config.xml”. Recomiendo desactivar el parámetro “isFlywayActive” para evitar problemas con versiones distintas entre sucursales.
- Corrige bug que mostraba utilidad de una lista de precios indefinida igual a la de otra lista de precios, el comportamiento correcto es utilizar la utilidad base (no la de otra lista de precios)
- Corrige bug que mostraba las primeras 5 compras en lugar de las últimas 5 en la pestaña “compras” del panel “Artículo”. Además la fecha de las compras ahora se muestra en formato SHORT
- Corrige bug que ponía comas en las utilidades de las listas de precios

## Versión 4.2.2 (2014-07-16)

- [Artemisa] Se agregó un botón en la caja clínica para reimprimir
recibos de pago
- [Artemisa] Se agregó el número de partida a cada transacción en la
caja clínica
- [Omoikane] Optimización del FrostedGlassDesktopPane, ahora es más
eficiente, además corregí un bug que distorsionaba la imagen y los
errores causados al sacar una ventana de la pantalla visible.
- Corregido bug #111 No se actualiza la fecha de modificación de un
producto

## Versión 4.2.1 (2014-07-10)

- [Artemisa] Agregué columna de partida a la “caja clínica”
- Corrección del problema de pérdida de enfoque del cursor en Caja:
Después de cancelar producto, cancelar venta, mostrar catálogo de
clientes, mostrar movimientos de caja y habilitar venta especial. En
algunos casos se perdía el enfoque al no recibir los permisos de la
respectiva acción, en otros casos al recibir permisos o en ambas
situaciones.
- [Compras] Permite filtrar por proveedor
- [Compras] Se redujo exageradamente el tiempo que tarda en abrir el
CRUD
- [Compras] Permite filtrar por fecha
- [Compras] #72 Concepto “Estado del pago de una compra” que contempla
dos estados: “Compra Pagada” y “Compra impaga”, éste status es visible
en cada compra y se puede modificar con un click.
- [Compras] Permite filtrar por “Estado del pago”
- Corrección de bug que daba un falso positivo en autenticación,
generando errores en pantallas protegidas (se recibía una autenticación
positiva pero sin usuario no era posible hacer cualquier cosa)
- Corrección de bug que generaba un error al cambiar de cliente en caja
cuando no había items
- Optimicé handler de escáner de código de barras serial.
- Corrección de los auto incrementos en el esquema inicial
- Corrección bug artemisa: Catálogo de artículos solo abre una vez por
instancia de la aplicación
- Corrección de bug #110 en ventas especiales: No se aplican descuentos
por línea o grupo a precios especiales
- Corrección de algunas pérdidas de enfoque en operaciones en caja
- Ajustes en handler de escáner de código de barras serial
- Mejora de la función “getEscritorio”, ahora reconoce más escenarios
como el escritorio de Artemisa
- Protección de la operación de cambio de cliente en Caja, para que un
cajero no pueda utilizar el cambio de cliente para aplicar cambios de
precio sin la autorización de un superior (perfil gerente)

## Versión 4.2.0 (2014-06-30)

- #105 Agregué CRUD de listas de precios. En lugar de “DELETE” el CRUD
desactiva. Además puede almacenar notas sobre cada lista de precios,
solo por si acaso.
- #106 Agregué CRUD de impuestos. Es posible agregar tantos impuestos
como se quieran, la gran limitación por ahora es que únicamente se
pueden agregar impuestos basados en factores(porcentajes en realidad) y
no en tarifas fijas. Capacidad para desactivar y para almacenar notas
por impuesto.
- Los CRUDs de impuestos y listas de precios son accesibles desde la
configuración.
- Corrección del bug #104. Almacenamiento erróneo de ítems de ventas,
en ítems agrupados.
- Workaround del bug #103
- Corrección de un bug en el mensaje “Venta registrada” sólo sucedía
cuando se lanza la caja sin el escritorio, por el momento eso solo
ocurre en tests.
- #107 Agregué un panel a la ficha “Artículo” para especificar precios
alternos/listas de precios/niveles de precios por ejemplo: precio 2,
precio 3, etc.
- #108 Agregué panel a la ficha artículo que muestra las últimas 5
compras de ese producto, cantidades, costos, nombre del proveedor y
costo promedio (el promedio de esas 5 compras)
- Ajuste de tamaño de pool de conexiones
- Corrección de comportamientos de desconexión de BD y de esquema de BD
incorrecto
- Los factores de utilidad para precios alternos (listas de precios)
pasaron de estar almacenados en tablas secundarias a metadatos en
almacenados en una columna de la tabla “precios” para optimizar las
operaciones relacionadas con precios.
- Corrección de bug que no permitía abrir Omoikane si se había mostrado
la ventaja de solución de problemas de migración del esquema de la BD
- Issues solved: #100 y #102
- Issues closed #48 #54 #68
- Correcciones #96 #97 #98 #99


## Versión 4.1.3 (2014-05-13)

- Issue #83 resuelto. Agregar definición de tablas ListaDePrecios y PrecioAlterno a esquema inicial
- Issue #86 resuelto. Captura de compra: Después de borrar partida no es posible reintroducir el código eliminado
- Issue #88 resuelto. Actualizaciones del esquema inicial
- Issue #89 resuelto. Ocultar features incompletos de la versión 4.2
- Bug solved: que mostraba incorrectamente folios de compras guardadas
- Bug solved: artículos de partidas eliminadas durante la captura de
una compra no podían volver a ser agregados (en esa misma compra)
- Centralicé los asuntos relacionados con aumento y disminución de
inventarios con el objetivo de que sean ejecutados todos los
comportamientos y respetadas las restricciones del modelo de negocio
desde cualquier parte que se requiera alterar el inventario.
- Los aumentos y reducciones de stock hechos a través de
“StockIssuesLogic” respetan las reglas de paquetes/kits
- ... Por lo tanto compras ya aumenta adecuadamente los stocks de
paquetes en sus respectivos componentes (issue #87)
- Desmarcación de formatos TXT de tickets
- Actualización de jfxrt.jar a la versión usada por el instalador (7u51)
- Mejora del instalador, incluye wrapper nativo de JRE 7u51
- Mejora del instalador, instala automáticamente MySQL y carga el
esquema inicial de la BD
- Issue #91 Instalación permite instalar únicamente cliente, únicamente servidor o ambos
- Issues resueltos: #52 #61 #63 #82 #84 #90 #92 #95

## Versión 4.2 alfa (2014-04-29)

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
- Incluyo un ejemplo de acceso a datos usando MePro. La herramienta
para extender funcionalidad dinámicamente en tiempo de ejecución.
- Tiempo de arranque reducido, gracias a la eliminación de las
revisiones que hacía hibernate al comenzar la aplicación
- Reducción del código legado de Nadesico, varios métodos se migraron a
código nuevo.
- Ajustes al componente de compras
