package omoikane.sistema.seguridad;

import jfxtras.labs.dialogs.MonologFXBuilder;
import omoikane.entities.Usuario;
import omoikane.principal.Principal;
import omoikane.repository.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Arrays;
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
            String nip = String.valueOf(
                    new DialogPIN(null).showPlease()
            );

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

    class DialogPIN extends JDialog implements ActionListener
    {
        JPasswordField tf2;
        public DialogPIN(Frame f1) {
            super(f1, "NIP", true);
            setLayout(new FlowLayout());
            JButton btn1 = new JButton("OK");
            tf2 = new JPasswordField(10);
            btn1.addActionListener(this);
            add(new JLabel("Introduce tu NIP"));
            add(tf2);
            add(btn1);
            setSize(155,125);
            setLocationRelativeTo(null);
            getRootPane().setDefaultButton(btn1);
        }

        public void actionPerformed(ActionEvent e) {

            setVisible(false);
        }

        public char[] showPlease() {

            setVisible(true);
            return tf2.getPassword();
        }
    }

}
