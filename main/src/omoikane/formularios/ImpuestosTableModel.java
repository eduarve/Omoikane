package omoikane.formularios;

import omoikane.producto.Impuesto;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 23/01/14
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class ImpuestosTableModel extends DefaultTableModel {

    public ImpuestosTableModel(Object [][] data, String [] cols) {
        super(data, cols);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Impuesto im = (Impuesto)super.getValueAt(rowIndex, 0);

        switch(columnIndex) {
            case 0:
                return im.getDescripcion();
            case 1:
                return im.getPorcentaje().toString();
            case 2:
                return im.getImpuesto()==null?"0":im.getImpuesto().toString();
        }
        return "";
    }

    /**
     * Método que utiliza búsqueda secuencial para encontrar la existencia en el modelo del impuesto dado en el argumento,
     * para las comparaciones utiliza el ID del impuesto
     * @param im
     * @return
     */
    public Boolean exists(Impuesto im) {

        for(Object renglon : getDataVector()) {
            Impuesto imRenglon = (Impuesto) ((Vector)renglon).get(0);
            if(imRenglon.getId() == im.getId()) return true;
        }

        return false;
    }

    public List<Impuesto> getImpuestoList() {
        List<Impuesto> impuestoList = new ArrayList<>();

        for(Object renglon : getDataVector()) {
            Impuesto imRenglon = (Impuesto) ((Vector)renglon).get(0);
            impuestoList.add(imRenglon);
        }

        return impuestoList;
    }
}
