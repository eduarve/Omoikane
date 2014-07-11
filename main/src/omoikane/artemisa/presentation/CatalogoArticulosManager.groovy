package omoikane.artemisa.presentation

import omoikane.formularios.CatalogoArticulos
import omoikane.formularios.FrostedGlassDesktopPane;
import omoikane.principal.Articulos;
import omoikane.principal.Escritorio
import omoikane.sistema.Herramientas;

import javax.swing.*
import omoikane.sistema.Permisos

import javax.swing.event.InternalFrameAdapter
import javax.swing.event.InternalFrameEvent
import javax.swing.event.InternalFrameListener
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.WindowListener;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 31/07/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class CatalogoArticulosManager {
    public static CatalogoArticulos cat

    public void show() {

        if(omoikane.principal.Principal.escritorio == null) {
            omoikane.principal.Principal.escritorio = new Escritorio();
            //omoikane.principal.Principal.escritorio.frameEscritorio.setUndecorated(false)
            omoikane.principal.Principal.escritorio.frameEscritorio.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            omoikane.principal.Principal.escritorio.iniciar()
            omoikane.principal.Principal.iniciarSesion()
            omoikane.principal.Principal.escritorio.setNombreUsuario("Artemisa")
            omoikane.principal.Principal.escritorio.frameEscritorio.setTitle("Artemisa")

            cat = Articulos.lanzarCatalogo();

            cat.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE)

            cat.getBtnCerrar().removeAll();
            for(ActionListener ac : cat.getBtnCerrar().getActionListeners()) {
                cat.getBtnCerrar().removeActionListener(ac);
            }
            cat.getBtnCerrar().addActionListener(new ActionListener() {
                @Override
                void actionPerformed(ActionEvent e) {
                    omoikane.principal.Principal.escritorio.frameEscritorio.setVisible(false);
                    cat.setVisible(false)
                }
            })


            //omoikane.principal.Principal.escritorio = new EscritorioArtemisa();
            //omoikane.principal.Principal.escritorio.frameEscritorio.setUndecorated(false)
            //omoikane.principal.Principal.escritorio.frameEscritorio.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            //omoikane.principal.Principal.escritorio.iniciar()
            //omoikane.principal.Principal.iniciarSesion()
            //omoikane.principal.Principal.escritorio.setNombreUsuario("Artemisa")
            //omoikane.principal.Principal.escritorio.getFrameEscritorio().setTitle("Artemisa")
            //omoikane.principal.Principal.escritorio.getFrameEscritorio().setVisible(true)

        } else {

            omoikane.principal.Principal.escritorio.frameEscritorio.setVisible(true)
            omoikane.principal.Principal.escritorio.frameEscritorio.getContentPane().setVisible(true)
            cat.setVisible(true)

        }

        //Articulos.lanzarCatalogo();
    }
}
/*
class EscritorioArtemisa extends Escritorio {
    JFrame escritorioFrame;
    FrostedGlassDesktopPane panelEscritorio;

    public EscritorioArtemisa() {
        escritorioFrame = new JFrame();
        panelEscritorio = new FrostedGlassDesktopPane();
        escritorioFrame.setContentPane(panelEscritorio);
    }

    javax.swing.JFrame getFrameEscritorio()
    {
        return escritorioFrame
    }

    FrostedGlassDesktopPane getPanelEscritorio()
    {
        return panelEscritorio
    }
}
   */