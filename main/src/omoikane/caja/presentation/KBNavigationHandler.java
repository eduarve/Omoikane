package omoikane.caja.presentation;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import jfxtras.labs.internal.scene.control.skin.BigDecimalFieldSkin;
import omoikane.caja.handlers.*;

public class KBNavigationHandler implements EventHandler<KeyEvent> {

        CajaController cc;

        public KBNavigationHandler(CajaController controller) {
            cc = controller;
        }

        @Override
        public void handle(KeyEvent event) {
            Object target = event.getTarget();
            if (isControl(target)) {
                Control control = (Control) target;
                enterKeyNavigationRules(event.getCode(), control, event);
            }
        }

        private boolean isControl(Object o) {
            boolean isControl = Control.class.isAssignableFrom(o.getClass());
            if (isControl && BigDecimalFieldSkin.NumberTextField.class.isAssignableFrom(o.getClass()))
                ((Control) o).setId("efectivoBigDecimalField");
            return isControl;
        }

        /**
         * Las reglas para pasar el enfoque en el campo "capturaTextField":
         * 1. Que haya al menos un producto en la venta
         * 2. Que no haya texto en el campo de captura
         * @param keyCode
         * @param control
         * @param event
         */
        private void enterKeyNavigationRules(KeyCode keyCode, Control control, Event event) {
            if (keyCode.equals(KeyCode.ENTER) && control.getId() != null)
                switch (control.getId()) {
                    case "capturaTextField":
                        String text = cc.getCapturaTextField().getText();
                        if (cc.getModel().getVenta().size() > 0 && (text == null || text.equals(""))) {
                            //cc.getBtnEfectivo().requestFocus();
                            cc.getEfectivoTextField().requestFocus();
                        }
                        break;
                    case "btnEfectivo":
                    case "btnCheque":
                    case "btnTarjeta":
                    case "btnVale":
                        cc.getEfectivoTextField().setFocusTraversable(false);
                        cc.getEfectivoTextField().requestFocus();

                        break;
                    case "efectivoTextField":
                    case "efectivoBigDecimalField":
                        cc.getCambioTextField().requestFocus();
                        break;
                    case "cambioTextField":
                        cc.getBtnCobrar().requestFocus();
                        break;
                    case "btnCobrar":
                        cc.getBtnCobrar().fire();
                        break;
                }
            if (keyCode.equals(KeyCode.F1))
                new MostrarCatalogoHandler(cc).handle(event);
            if (keyCode.equals(KeyCode.F8))
                cc.getCerrarCajaSwingHandler().handle(event);
            if (keyCode.equals(KeyCode.F3))
                cc.getCapturaTextField().requestFocus();
            if (keyCode.equals(KeyCode.F4))
                cc.ventaEspecialHandler.handle(event);
            if (keyCode.equals(KeyCode.F5))
                new MovimientosDeCaja(cc).handle(event);
            if (keyCode.equals(KeyCode.F6))
                new AbrirCajon(cc).handle(event);
            if (keyCode.equals(KeyCode.F7))
                new CancelarProducto(cc).handle(event);
            if (keyCode.equals(KeyCode.F12))
                new CancelarVenta(cc).handle(event);
            /* Handler para el botón PLM. Lo quité por falta de uso
            if (new KeyCodeCombination(KeyCode.P, KeyCodeCombination.ALT_DOWN).match((KeyEvent) event))
                new PlmHandler(cc).handle(event); */
            if (new KeyCodeCombination(KeyCode.C, KeyCodeCombination.ALT_DOWN).match((KeyEvent) event))
                new MostrarCatalogoClientesHandler(cc).handle(event);
        }
    }