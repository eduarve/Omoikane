package omoikane.sistema.seguridad;

import jfxtras.labs.dialogs.MonologFXBuilder;
import omoikane.entities.Usuario;
import omoikane.principal.Principal;
import omoikane.repository.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 2/08/13
 * Time: 04:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class NipAuthProvider implements AuthProvider {

    UsuarioRepo usuarioRepo;
    Integer intentosPermitidos = 5;

    @Override
    public Usuario authenticate() throws AuthException {
        usuarioRepo = (UsuarioRepo) Principal.getContext().getBean("usuarioRepo");

        for(int i = 0; i < intentosPermitidos; i++) {

            if(i>0) JOptionPane.showMessageDialog(null, "Clave errónea. "+(intentosPermitidos-i)+" intentos disponibles.");

            String nip = nipDialog();
            if(nip == null || nip.isEmpty()) continue;

            Integer nipInt;
            try { nipInt = Integer.valueOf(nip); } catch(NumberFormatException n) { continue; }


            List<Usuario> usuarios = usuarioRepo.readAll();
            for(Usuario u : usuarios) {
                if( nipInt.equals( u.getNip() ) ) return u;
            }
        }
        return null;

    }

    public String nipDialog() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Digite su NIP");
        JPasswordField pass = new JPasswordField(10);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK"};
        int option = JOptionPane.showOptionDialog(null, panel, "NIP",
                JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, pass);
        if(option == 0) // pressing OK button
        {
            char[] password = pass.getPassword();
            return new String(password);
        }
        return "";
    }
}
