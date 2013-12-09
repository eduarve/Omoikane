package omoikane.artemisa.reports

import omoikane.artemisa.PacienteRepo
import omoikane.artemisa.entity.Abono
import omoikane.artemisa.entity.Paciente
import omoikane.artemisa.entity.Transaccion
import omoikane.moduloreportes.Reporte

import javax.swing.*
import java.awt.Label

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 29/07/13
 * Time: 03:48 PM
 * To change this template use File | Settings | File Templates.
 */
class CuentaSimplificadaPrint {
    Reporte reporte;

    public CuentaSimplificadaPrint(List<Transaccion> a, Paciente b) {

        BigDecimal sumaAbono, sumaCargo;

        sumaAbono = new BigDecimal(0);
        sumaCargo = new BigDecimal(0);

        a.each { Transaccion transaccion ->
            sumaAbono = sumaAbono.add(transaccion.abono)
            sumaCargo = sumaCargo.add(transaccion.cargo)
        }

        def data = [
                [
                        fecha: Calendar.getInstance().time,
                        paciente: b.getNombre() ,
                        cargo: sumaCargo,
                        abono: sumaAbono,
                        saldo: sumaCargo.subtract(sumaAbono)
                ]
        ]
        reporte = new Reporte("Plantillas/artemisa_cuentaSimplificada.jrxml", data)
    }

    public show() {
        JFrame frame  = new JFrame();
        JPanel viewer = reporte.getPreviewPanel();

        viewer.setOpaque(true);
        viewer.setVisible(true);

        frame.setTitle("Recibo de abono");
        frame.add(viewer);
        frame.setSize(960,600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
