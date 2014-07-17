package omoikane.formularios;

import omoikane.principal.Principal;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 12/06/13
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
public class FrostedGlassDesktopPane extends JDesktopPane {

    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    public void paint(Graphics g) {
        if(!Principal.fondoBlur) { super.paint(g); return; }

        int anchoPantalla = Principal.getEscritorio().getEscritorioFrame().getWidth();
        int altoPantalla  = Principal.getEscritorio().getEscritorioFrame().getHeight();

        Rectangle clipBounds = g.getClipBounds();
        //BufferedImage bufferImage = GraphicsUtilities.createCompatibleImage(clipBounds.width, clipBounds.height);
        BufferedImage bufferImage = getBufferImage(clipBounds.width, clipBounds.height);
        BufferedImage bufferImage2 = getBufferedImage(anchoPantalla,altoPantalla);

        Graphics bufferGraphics = bufferImage.createGraphics();
        bufferGraphics.translate(-clipBounds.x, -clipBounds.y);
        bufferGraphics.setClip(clipBounds);

        if(bufferImage2 != null) {
            Graphics bufferGraphics2 = bufferImage2.getGraphics();
            bufferGraphics2.translate(-clipBounds.x, -clipBounds.y);
            bufferGraphics2.setClip(clipBounds);
        }

        try {
            // let children paint
            super.paint(bufferGraphics);
            // pinto la pantalla completa en el bufferImage2
            if(bufferImage2 != null) {
                super.paint(bufferImage2.createGraphics());
            }

            // blit offscreen buffer to given graphics g
            g.drawImage(bufferImage, clipBounds.x, clipBounds.y,
                    null);
            getBufferImage(getWidth(), getHeight()).getGraphics().drawImage(bufferImage, clipBounds.x, clipBounds.y, null);

        } catch (RasterFormatException rfe) {

            //Error posiblemente causado por el fondo blur, se desactiva como posible solución automatizada
            Principal.fondoBlur = false;
            throw rfe;

        } catch (RuntimeException ex) {
            throw ex;
        } finally {
            bufferGraphics.dispose();
        }
    }
    private BufferedImage bufferImage;

    public BufferedImage getBufferImage(int w, int h) {
        //if(bufferImage == null) {
            bufferImage = GraphicsUtilities.createCompatibleImage(w, h);
        //}
        return bufferImage;
    }

    private Hashtable<String, BufferedImage> bufferedImagesTable;

    public BufferedImage getBufferedImage(Integer width, Integer height) {
        if(bufferedImagesTable == null) { bufferedImagesTable = new Hashtable<>(); }
        String imageSize = width+"x"+height;
        if(bufferedImagesTable.containsKey(imageSize)) {
            return bufferedImagesTable.get(imageSize);
        } else {
            //System.out.println("Nuevo caché: "+width+", "+height);
            return bufferedImagesTable.put(imageSize, GraphicsUtilities.createCompatibleImage(width, height));
        }
    }
}
