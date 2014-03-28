
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import org.springframework.context.ApplicationContext;
import omoikane.producto.*;
import omoikane.repository.ProductoRepo;
import org.apache.log4j.Logger


count = 0
new SwingBuilder().edt {
  frame(title:'Etiquetor', size:[300,100], show: true) {
    borderLayout()
    txtStatus = label(text:"Impresor rápido de etiquetas. Escanee un código para imprimir etiqueta", constraints: BL.NORTH)
    txtCodigo = textField(actionPerformed: { txtCodigo.selectAll(); imprimir(getProducto(txtCodigo.text)) })
    
    button(text:'Imprimir <Enter>',
         actionPerformed: { txtCodigo.selectAll(); imprimir(getProducto(txtCodigo.text)); },
         constraints:BL.SOUTH)
  }
}

def imprimir(Articulo a) {
	
	def protocolo = omoikane.principal.Principal.puertoImpresion
	def impresora = omoikane.principal.Principal.impresoraActiva
	Logger logger = Logger.getLogger(omoikane.principal.Principal.class);

        Thread.start {
            if (impresora)
            {
            try {
                FileOutputStream os = new FileOutputStream("$protocolo");
                PrintStream ps = new PrintStream(os);
                ps.println(plantilla(a));
                ps.close();
            } catch (FileNotFoundException fnf) { logger.error("Error al imprimir al puerto $protocolo", fnf); }
            }
            else
            {
                try {
                    logger.info(plantilla(a))
                } catch (e) { logger.error("Error al mandar a la consola", e); }
            }
        }
    }

Articulo getProducto(String codigo) {
	ApplicationContext applicationContext = omoikane.principal.Principal.applicationContext;
     productoRepo = (ProductoRepo)applicationContext.getBean("productoRepo");

     try {
		Articulo articulo = productoRepo.findByCodigo(codigo).get(0);
	            
		txtStatus.text = articulo.descripcion
		return articulo;
     } catch(java.lang.IndexOutOfBoundsException e) {
     	txtStatus.text = "Artículo no encontrado"
     }
}

def plantilla(Articulo a) {
return """
* * * Prueba * * *
Producto: ${a.descripcion}
Precio: ${String.format("\$%,6.02f", a.getPrecio().getPrecio())}
"""
}

