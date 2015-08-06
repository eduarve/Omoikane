/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package omoikane.moduloreportes

/**
 *
 * @author Octavio
 */

import java.sql.*;
import javax.swing.*
import org.apache.log4j.Logger
import omoikane.sistema.Config;
import omoikane.principal.Principal;

public class Comandos {

    static def config
    public static String login 
    public static String password 
    public static String url
    public static Logger logger = Logger.getLogger(Comandos.class);

    public static Connection Enlace(Connection conn) throws SQLException {
        try {
            defineAtributos();
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, login, password);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            logger.error("Error al conectar con la base de datos", c)
        }
        return conn;
    }

    static def defineAtributos() {
            login      = String .valueOf(Principal.loginJasper)
            password   = String .valueOf(Principal.passJasper)
            url        = String .valueOf(Principal.URLMySQL)
    }
}
	


