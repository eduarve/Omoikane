Omoikane
========

Poderoso punto de venta opensource de tecnología de vanguardia. Solución sencilla para cualquier tipo de negocio, especializado en negocios, que por sus características complejas no pueden ser tratados con puntos de venta básicos. (la mayoría en el mercado). Puede manejar desde 1 producto hasta cientos de miles sin problemas. Tiene características de inventario y almacén sencillas de uso, pero avanzadas en características que superan la media del mercado.

![Caja](http://i.imgur.com/JHh7C0f.png)

## Características
![Alt MainMenu](https://github.com/Phesus/Omoikane/raw/gh-pages/images/SS-MainMenu-2013-06-20.png)
- __Opensource__. Cualquiera puede extender nuestro código para implementar o modificar funciones en sus negocios, puede ser un excelente punto de partida para negocios que por su tamaño o especialización requiern una solución propia. Ésto siempre que respeten los términos de nuestra licencia :).
- __Diseño__ moderno que da una buena presentación a los clientes y oscuro que reduce el cansancio visual de los usuarios
- __Tuchscreen y touchless (no touch)__: No solo su diseño es perfectamente apto para terminales touchscreen, además presenta una navegación por teclado, finalmente cuidada para terminales que no tienen touchscreen, ésto es útil en negocios de mucho tráfico.
- __Seguridad__ de transacciones: Una de las características más cuidadas ha sido la protección contra robo por parte del personal que labora en caja.
- __Huela dactilar__ Autenticación de usuarios por tecnología biométrica de reconocimiento de huellas dactilares. Por el momento solo disponible para lectores U.Are.U 4000 y 4500.

![Imgur](http://i.imgur.com/08IbsmI.png)
- Autenticación estándar usando nip. ( Se recomienda usar la autenticación biométrica de ser posible ).
- __Multi-código__ de producto. Cada producto puede tener asignado un código de barras (o interno) e ilimitados códigos secundarios.
- __Paquetes__ de productos. Es posible agrupar distintos productos y en distintas cantidades bajo un mismo nombre y código de producto. Además esa agrupación de productos puede tener un precio especial que no necesariamente es la suma de sus componentes.
- __Categorización__ de productos. Es posible categorizar un producto por línea y por grupo. 
- __Descuentos__ por producto, por grupo y por línea. 
- __Robusta y confiable__ base de datos libre y gratuita MySQL. 
- __Rendimiento, Escalabilidad y capacidad__ comprobados: El rendimiento no depende del tamaño del negocio, nuestra aplicación funciona en negocios con más de dos millones de registros y consideramos que puede soportar decenas de millones sin afectar nunca el rendimiento.
- __Multi-impuesto__ Es posible agregar cualquier cantidad de impuestos a cualquier producto. Por ejemplo IVA 0%, IVA 16%, IEPS 8%, IEPS 25%, etc. Es posible agregar cualquier impuesto que sea una proporción del valor del producto.
- __Facturación y notas de crédito CFDI__ La facturación electrónica se puede realizar mediante diversos proveedores de timbres, desde 30 centavos por timbre aún comprando un solo timbre.
- __Catálogo offline__ Consulte su catálogo de artículos aún sin conexión. Útil en caso de caídas del servidor o para llevar consigo la información.

## Características agendadas:
- Agrupación por departamento. Actualmente se puede usar la agrupación por líneas como suplemento.
- Descuentos por cliente: Ésta característica se bloqueo hace poco para arreglar detalles.
- Multi-almacén. Estamos analizando de que manera implementar esta característica sin impactar la simplicidad del sistema.
- Descuentos por cantidad. Una solución en la versión actual es usar los paquetes para dar descuentos por cantidad. Es útil para negocios que venden pocos productos en diferentes cantidades.
Además se agendarán las características sugeridas por nuestros usuarios, previo análisis por nuestra parte.

## Instalación
### Requisitos mínimos
- Windows XP y superiores, MAC OSX 10.6 o superiores, Linux con Java 7 o superior.
- Java JRE 7u21
- Al menos 2 GB de RAM
- Procesador de 1 GHz o superior
### Características recomendadas del sistema
- Procesador de doble núcleo de 2 GHz o superior
- Conexión a internet
- Para el uso de huella dactilar: Windows XP o superior y lector de huellas DigitalPersona u.are.u 4000 o 4500

### Instalación de la base de datos
1. Descargar MySQL Server, desde la página oficial. [Página oficial de MySQL Server](http://dev.mysql.com/downloads/mysql/)
2. [Descargar MySQL Workbench](http://dev.mysql.com/downloads/tools/workbench/)
3. [Descargar el esquema de la base de datos](https://github.com/Phesus/Omoikane/releases/download/4.1.0/schemadb.sql)
4. Instalar MySQL con los parámetros deseados, para un negocio pequeño los parámetros que vienen por default en el instalador serán más que suficientes. Es importante recordar el password que pongamos durante la instalación.
5. Instalar MySQL Workbench, éste software facilita las operaciones con la base de datos y será de gran utilidad para administrarla.
6. Abrir workbench, tome en cuenta que la interfaz está dividida en 3 columnas: "SQL Development", "Data Modeling" y "Server Administration".
7. Cargar la base de datos de Omoikane, para ésto: haga doble click en "mysql@localhost" en la columna "Server Administration", ahora vaya a "Data export and restore", seleccione la pestaña "Import from disk", seleccione "Import from self-contained file", haga click en "..." y busque el archivo "schemadb.sql" (del paso 3). Por último haga click en "Select all tables" y "Start import".
8. ¡Felicidades! Si los pasos anteriores se han seguido con éxito, la base de datos estará lista para ser utilizada.

#### Notas
- Es posible conectarse a cualquier otro tipo de instalación de MySQL y resultaría útil para configuraciones excentricas, por ejemplo usando LAMP, TurnKey Linux, WAMP, etc.
- Para un negocio grande, digamos más de 4 cajas de cobro, simplemente utilicen la configuración "Para producción" del instalador

### Instalación del cliente
1. Descargar el último instalador de omoikane del [área de releases](https://github.com/Phesus/Omoikane/releases)
2. Ejecutar el instalador, la instalación de omoikane, se divide en 5 instaladores, lanzado uno después de otro automáticamente: Instalador del Omoikane, Java JRE 7u25, .NET Framework 2.0, DigitalPersona RTE, driver u.are.u 4500
3. Configurar usando el configurador integrado, para ello ejecutar el siguiente comando en la línea de comandos desde la carpeta donde se instaló el programa:

```
java -classpath omoikane.jar omoikane.configuracion.ConfiguratorAppManager
```

Por ejemplo, si el programa se instaló en windows, en la carpeta default (c:\Phesus\Omoikane), entonces:
```
c:
cd c:\phesus\omoikane
java -classpath omoikane.jar omoikane.configuracion.ConfiguratorAppManager
```

4. Utilizar el configurador para establecer los principales parámetros del sistema. La parte más importante de la configuración es establecer los parámetros de conexión a la base de datos, estos parámetros tienen que ser consistentes con los que se introdujeron cuando se configuró la base de datos:

## Configuración

[Leer acerca de la configuración en nuestro wiki/manual](https://github.com/Phesus/Omoikane/wiki/Configuraci%C3%B3n)

## Licencia

Licenciado bajo GNUv2. Para más detalles ver el archivo LICENSE adjunto al código en éste repositorio.


Contacto
========
- [Página de Phesus](http://phesus.com)

Instrumentación
===============
Para los preocupados por el lado técnico, Omoikane utiliza las siguientes tecnologías libres:
- Java
- MySQL
- JavaFX y swing en los componentes más viejos
- JasperReports
- Spring
- Hibernate

Descargas
=========

[Área de descargas](https://github.com/Phesus/Omoikane/releases)
