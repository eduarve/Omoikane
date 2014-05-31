package phesus.configuratron.model;


import java.io.*;
import java.util.Scanner;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 30/08/12
 * Time: 23:34
 */
public class TemplatesDao {

    public Templates getTemplates() throws IOException {
        Templates templates = new Templates();

        templates.setPlantillaCorte(readFile("Plantillas/FormatoCorte.txt"));
        templates.setPlantillaTicket(readFile("Plantillas/FormatoTicket.txt"));

        return templates;

    }


    private String readFile(String filename) throws IOException {
        File file = new File( filename );
        if ( !file.exists() ) {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.close();
        }
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String txt = "";
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            txt = sb.toString();
        } finally {
            br.close();
            return txt;
        }
    }

    public void save(Templates templates) throws IOException {
        writeFile( "Plantillas/FormatoCorte.txt" , templates.getPlantillaCorte().get() );
        writeFile( "Plantillas/FormatoTicket.txt", templates.getPlantillaTicket().get());
    }

    private void writeFile(String filename, String content) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        out.write( content );
        out.close();
    }
}
