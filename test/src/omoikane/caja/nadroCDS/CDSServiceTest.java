package omoikane.caja.nadroCDS;

import com.net.cds_oroDemo.*;
import junit.framework.Assert;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 10/09/14
 * Time: 17:51
 */
public class CDSServiceTest {
    @Test
    public void testLogin() throws Exception, NadroCDSException {
        CDSService cdsService = new CDSService();
        cdsService.login();
    }

    @Test
    public void testActivarTarjeta() throws Exception, NadroCDSException {
        CDSService cdsService = new CDSService();
        cdsService.login();

        Paciente p = new Paciente() {{
            setNombre("José Pruebas");
            setApellidopaterno("Negrito");
            setApellidomaterno("Martínez");
            setTelefono("2221234567");
            setSexo("M");
            setCodigopostal("72000");
        }};
        //Activamos la tarjeta
        try {
            ResponseActivateCard rap = cdsService.activarTarjeta("9981212573821", "Cajero 1", p);
            System.out.println( ToStringBuilder.reflectionToString(rap, ToStringStyle.MULTI_LINE_STYLE) );
        } catch (NadroCDSException nce) {
            //Es aceptable si el WS me indica que ya está activa, cualquier otro Exception es inaceptable
            if(!nce.getMessage().contains("Tarjeta ya se encuentra activada"))
                throw nce;
        }
    }

    @Test
    public void testGetInfoTarjeta() throws Exception, NadroCDSException {
        CDSService cdsService = new CDSService();
        cdsService.login();

        //Pruebo una tarjeta válida
        Tarjeta t = cdsService.getInfoTarjeta("9981212573784");
        System.out.println( ToStringBuilder.reflectionToString(t, ToStringStyle.MULTI_LINE_STYLE) );
        System.out.println( ToStringBuilder.reflectionToString(t.getCliente(), ToStringStyle.MULTI_LINE_STYLE) );

        //Prueba una tarjeta inválida
        try {
            cdsService.getInfoTarjeta("00");
            throw new Exception("Comportamiento inválido, no detectó tarjeta inválida");
        } catch (NadroCDSException n) {
            //Comportamiento adecuado
        }
    }

    @Test
    public void testGetBeneficios() throws Exception, NadroCDSException {
        CDSService cdsService = new CDSService();
        cdsService.login();
        ArrayOfBonusProductList productos = new ArrayOfBonusProductList();
        productos.getBonusProductList()
                .addAll(
                        Arrays.asList(

                                new BonusProductList() {{ setPiezas(5l); setSku("7506200700038"); }},
                                new BonusProductList() {{ setPiezas(10l); setSku("7501094917012"); }}
                        ))
                ;

        ArrayOfResponseBonusList beneficios = cdsService.getBeneficios("9981212573821", productos);

        debugPrint(beneficios);

        cdsService.logout();

        for(ResponseBonusList bonus : beneficios.getResponseBonusList()) {
            if(bonus.getSku().equals("7506200700038"))
                Assert.assertTrue(bonus.getPiezasGratis() == 1l);
            else if(bonus.getSku().equals("7501094917012"))
                Assert.assertTrue(bonus.getPorcentajeDescuento() == 4d);
            else
                throw new Exception("Estado inválido");
        }
    }

    private void debugPrint(Object o) {
        RecursiveToStringStyle recursiveToStringStyle = new RecursiveToStringStyle() {{
            this.setContentStart("[");
            this.setFieldSeparatorAtStart(true);
            this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
            this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
        }};

        System.out.println(ToStringBuilder.reflectionToString(o, recursiveToStringStyle));
    }

    @Test
    public void testEnviarVenta() throws Exception, NadroCDSException {
        CDSService cdsService = new CDSService();
        cdsService.login();

        Pedido pedido = new Pedido();
        pedido.setCedulaProfesionalMedico("");
        pedido.setNoTarjeta("9981212573784");
        pedido.setNoTicket("1-1-10008000");
        pedido.setUsuario("Cajero X");
        pedido.setTotal(new BigDecimal("500.00"));

        ArrayOfPedidoArticulos apa = new ArrayOfPedidoArticulos();
        PedidoArticulos pa1 = new PedidoArticulos() {{
            setPiezasPagadas(5);
            setPiezasGratis(0);
            setSku("7506200700038");
            setIVA(BigDecimal.ZERO);
            setMontoDescuento(BigDecimal.TEN);
            setPorcentajeDescuento(BigDecimal.TEN);
            setPorcentajeIVA(0d);
            setPrecio(BigDecimal.TEN);
            setPrecioFijo(BigDecimal.TEN);
            setPrecioPOS(BigDecimal.TEN);
        }};
        PedidoArticulos pa2 = new PedidoArticulos() {{
            setPiezasPagadas(10);
            setPiezasGratis(0);
            setSku("7501094917012");
            setIVA(BigDecimal.ZERO);
            setMontoDescuento(BigDecimal.TEN);
            setPorcentajeDescuento(BigDecimal.TEN);
            setPorcentajeIVA(0d);
            setPrecio(BigDecimal.TEN);
            setPrecioFijo(BigDecimal.TEN);
            setPrecioPOS(BigDecimal.TEN);
        }};
        apa.getPedidoArticulos().add(pa1);
        apa.getPedidoArticulos().add(pa2);
        ResponseSales rs = cdsService.enviarVenta(pedido, apa);

        debugPrint(rs);

        cdsService.logout();
    }

    @Test
    public void testLogout() throws Exception, NadroCDSException {
        CDSService cdsService = new CDSService();
        cdsService.login();

        cdsService.logout();
    }
}
