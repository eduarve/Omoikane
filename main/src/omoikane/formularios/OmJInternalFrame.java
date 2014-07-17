package omoikane.formularios;

import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.PointillizeFilter;
import omoikane.principal.Principal;
import org.jdesktop.swingx.image.GaussianBlurFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 2/07/13
 * Time: 04:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class OmJInternalFrame extends javax.swing.JInternalFrame {
    public BufferedImage cacheFondo;
    private BufferedImage fondo;

    public OmJInternalFrame() {
        ((JPanel)this.getContentPane()).setOpaque(false);
        this.getLayeredPane().setOpaque(false);
        this.getRootPane().setOpaque(false);
    }

    public void paintComponent(Graphics g)
    {
        if(!Principal.fondoBlur) { g.drawImage(this.fondo, 0, 0, null); return; }

        int anchoPantalla = Principal.getEscritorio().getEscritorioFrame().getWidth();
        int altoPantalla  = Principal.getEscritorio().getEscritorioFrame().getHeight();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        //BufferedImage fondo = Principal.getEscritorio().getPanelEscritorio().getBufferImage(1024, 720);
        BufferedImage fondo = Principal.getEscritorio().getPanelEscritorio().getBufferedImage(
                anchoPantalla,
                altoPantalla);

        if(cacheFondo == null) {
            //BoxBlurFilter filter = new BoxBlurFilter();

            cacheFondo = copyImage(fondo);

            cacheFondo = new GaussianBlurFilter(6).filter(fondo, null);
            //filter.setIterations(2);
            //filter.setRadius(5);

            /*
            PointillizeFilter pointillizeFilter = new PointillizeFilter();
            pointillizeFilter.setEdgeColor(6);
            pointillizeFilter.setEdgeThickness(1);
            pointillizeFilter.setFadeEdges(true);
            pointillizeFilter.setFuzziness(6);
            pointillizeFilter.filter(cacheFondo, cacheFondo); */

            //filter.filter(cacheFondo, cacheFondo);

            Graphics2D graphics2D = (Graphics2D) cacheFondo.getGraphics();
            graphics2D.setColor(new Color(5,5,5,105));
            graphics2D.fillRect(0,0,cacheFondo.getWidth(),cacheFondo.getHeight());

        }

        int anchoFrame = getWidth();
        int altoFrame  = getHeight();
        int x          = getX();
        int y          = getY();
        int frameX     = 0;
        int frameY     = 0;

        if(getX()+anchoFrame > anchoPantalla) anchoFrame = anchoFrame - ((getX()+anchoFrame) - anchoPantalla);
        if(getY()+altoFrame  > altoPantalla ) altoFrame  = altoFrame - ((getY()+altoFrame ) - altoPantalla);
        if(x < 0) { frameX -= x; x = 0; }
        if(y < 0) { frameY -= y; y = 0; }

        BufferedImage fondoVentana = cacheFondo.getSubimage(x, y, anchoFrame, altoFrame);
        g.drawImage(fondoVentana, frameX, frameY, null);
        if(Principal.DEBUG) {
            g.drawString("X:" + x + ",Y:" + y + ",W:" + anchoFrame + ", H:" + altoFrame, 15, 15);
            g.drawString("X:" + getX() + ",Y:" + getY() + ",W:" + getWidth() + ", H:" + getHeight(), 15, 30);
            g.drawString("W:" + cacheFondo.getWidth() + ", H:" + cacheFondo.getHeight(), 15, 45);
        }


    }

    static BufferedImage copyImage(BufferedImage bi) {
        //BufferedImage copyOfImage =
        //        new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage copyOfImage = gc.createCompatibleImage(bi.getWidth(), bi.getHeight(),BufferedImage.TRANSLUCENT);
        Graphics g = copyOfImage.getGraphics();
        g.drawImage(bi, 0, 0, null);
        return copyOfImage;
    }

    public void generarFondo()
    {
        Rectangle areaDibujo = this.getBounds();
        BufferedImage tmp;
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        tmp = gc.createCompatibleImage(areaDibujo.width, areaDibujo.height,BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) tmp.getGraphics();
        g2d.setColor(new Color(0,0,0, 205));
        g2d.fillRect(0,0,areaDibujo.width,areaDibujo.height);
        fondo = tmp;
    }

}
