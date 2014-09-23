package omoikane.caja.nadroCDS;

import com.net.cds_oroDemo.*;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 09/09/14
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */
public class CDSService {

    String session;

    WSCirculodelaSaludSoap servicioWeb;

    public void login() throws NadroCDSException {
        String idCadena   = "";
        String idSucursal = "";
        String usuario    = "";
        String pass       = "";

        servicioWeb = new WSCirculodelaSalud().getWSCirculodelaSaludSoap();
        ResponseLogin responseLogin = servicioWeb.login(usuario, pass, idCadena, idSucursal);

        if(responseLogin.isHuboError())
            throw new NadroCDSException(responseLogin.getMensajeError());

        session = responseLogin.getSesion();

    }

    public Tarjeta getInfoTarjeta(String noTarjeta) throws NadroCDSException {
        if(session.length() == 0) throw new NadroCDSException("Sesión inválida con el servicio Nadro CDS Oro. COD-1");

        Tarjeta info = servicioWeb.getInfoCard(session, noTarjeta);

        if(info == null)
            throw new NadroCDSException("Falla al consultar información de la tarjeta");

        if(info.isHuboError())
            throw new NadroCDSException(info.getMensajeError());

        return info;
    }

    public ArrayOfResponseBonusList getBeneficios(String noTarjeta, ArrayOfBonusProductList productos) throws NadroCDSException {
        if(session.length() == 0) throw new NadroCDSException("Sesión inválida con el servicio Nadro CDS Oro. COD-2");

        ArrayOfResponseBonusList beneficios = servicioWeb.getBonusProductList(session, noTarjeta, productos);

        if(beneficios == null
                || beneficios.getResponseBonusList() == null
                || beneficios.getResponseBonusList().size() < productos.getBonusProductList().size())
            throw new NadroCDSException("Respuesta incorrecta por parte del WS de Nadro CDS Oro. COD-1");

        return beneficios;
    }

    /**
     * Se completa la transacción enviando al WS de Nadro CDS Oro la información de la venta
     */
    public ResponseSales enviarVenta(Pedido pedidoMaster, ArrayOfPedidoArticulos pedidoDetails) throws NadroCDSException {
        if(session.length() == 0) throw new NadroCDSException("Sesión inválida con el servicio Nadro CDS Oro. COD-3");

        ResponseSales responseSales = servicioWeb.createSales(session, pedidoMaster, pedidoDetails);

        if(responseSales == null)
            throw new NadroCDSException("Respuesta incorrecta por parte del WS de Nadro CDS Oro. COD-2.");

        if(responseSales.isHuboError())
            throw new NadroCDSException(responseSales.getMensajeError());

        return responseSales;
    }

    public ResponseActivateCard activarTarjeta(String noTarjeta, String nombreUsuario, Paciente paciente) throws NadroCDSException {
        if(session.length() == 0) throw new NadroCDSException("Sesión inválida con el servicio Nadro CDS Oro. COD-4");

        ResponseActivateCard rap = servicioWeb.activateCardUser(
                session,
                nombreUsuario,
                noTarjeta,
                paciente.getNombre(),
                paciente.getApellidopaterno(),
                paciente.getApellidomaterno(),
                paciente.getTelefono(),
                paciente.getEmail(),
                paciente.getSexo(),
                paciente.getFechanacimiento(),
                paciente.getCodigopostal()
                );

        if(rap.isHuboError())
            throw new NadroCDSException(rap.getMensajeError());

        return rap;
    }

    public void logout() {
        servicioWeb.logout(session);
    }
}

