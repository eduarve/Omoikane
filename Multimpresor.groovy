import groovy.sql.*
import omoikane.sistema.*;

import omoikane.nadesicoiLegacy.Db;
import omoikane.repository.VentaRepo;
import omoikane.entities.LegacyVenta
import javax.swing.JOptionPane
import java.io.*;
import omoikane.sistema.Dialogos;
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
/*

		db = Db.connect()
		for(int i = 0; i < 98; i++) {
			db.execute("""INSERT INTO usuarios (id_usuario, nombre, fecha_hora_alta, umodificacion, nip)
			VALUES (${i}, "Cajero", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3491)""")
			}
		

		
		db.close();
return;
*/
Thread.start() {
	Impresor im = new Impresor();
	im.setDesdeTicket(2142104);
	im.setCaja(4);
	im.iniciarImpresion();
}

public class Impresor {
	def db;
	def desdeTicket;
	def pausa = false;
	def abortar = false;
	def caja;
	def textlabel;

	public Impresor(desdeTicket = 0) {
		this.desdeTicket = desdeTicket;
		gui();
	}
	def iniciarImpresion() {
		db = Db.connect()
		imprimirCaja(caja)		
		
		
		db.close();
	}
	def imprimirCaja(def idCaja) {
		def ventas = db.rows("SELECT * FROM ventas v WHERE v.fecha_hora BETWEEN ? AND ? AND v.id_venta > ? AND v.id_caja = ?", ['2012-1-22', '2012-1-23', desdeTicket, idCaja])
		//VentaRepo ventaRepo = omoikane.principal.Principal.applicationContext.getBean(VentaRepo.class);
		int i = 0;
		int j = 0;
		for(def venta : ventas) {
    			Comprobantes c = omoikane.principal.Principal.applicationContext.getBean(Comprobantes.class);
    			//LegacyVenta lv = ventaRepo.readByPrimaryKey(venta.id_venta as Long);    
    			//lv.idUsuario = 1;
    			if(abortar) { return; }
    				
    			textlabel.text = venta.id_venta;
    			c.ticketVenta(venta.id_venta as Long)
    			safePrint(c);    			
    
    			if(i++==50) { i = 0; JOptionPane.showMessageDialog(null, "Espera a que la impresora deje de imprimir. "+j); } else { Thread.sleep(5000); }
    			j++;
		}
	}
	def safePrint(Comprobantes c) {
		while(true) {
			try {
    				//c.imprimir();
    				while(true) { if(pausa) { Thread.sleep(1000) } else { break }  }    				
    				imprimir(c);
    				break;
  			} catch(java.io.FileNotFoundException fnfe) {
    				JOptionPane.showMessageDialog(null, "Error al imprimir, pulse <aceptar> para reintentar. ");
    			}
    		}
	}
	def imprimir(Comprobantes c) throws java.io.FileNotFoundException { 
	
        //Thread.start {
            def protocolo = omoikane.principal.Principal.puertoImpresion
            try {
                FileOutputStream os = new FileOutputStream("$protocolo");
                PrintStream ps = new PrintStream(os);
                ps.println(c.generado);
                ps.close();
            } catch (java.io.FileNotFoundException fnf) { JOptionPane.showMessageDialog(null, "Relanzando error filenotfoundexception"); throw fnf; }
            
        //}
    }

    public gui() {
		
		new SwingBuilder().edt {
		  frame(title:'Frame', size:[300,300], show: true) {
		    vbox {
		       textlabel = label(text:""/*, constraints: BL.NORTH*/)
		       button(text:'Pausa / Seguir',
		            actionPerformed: { pausa = !pausa; textlabel.text = "Pausa? ${pausa}"; }/*,
		            constraints:BL.SOUTH*/)
		       button(text:"Abortar impresion", actionPerformed: { abortar = true; } )
		       
		       hbox { label(text: "Desde el ticket(id): "); folioField = textField();  }
		       hbox { label(text: "De la caja: "); cajaField = textField();  }
		       button(text:"Comenzar impresión", actionPerformed: {  })
		       }
		    }
		  }

    }
    
}

