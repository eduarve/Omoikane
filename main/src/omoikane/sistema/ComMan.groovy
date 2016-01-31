 /* Author Phesus        //////////////////////////////
 *  ORC,ACR             /////////////
 *                     /////////////
 *                    /////////////
 *                   /////////////
 * //////////////////////////////                   */

package omoikane.sistema

import gnu.io.*

 import java.util.regex.Pattern

 class ComMan implements SerialPortEventListener {

    private CommPortIdentifier m_PortIdPrinter;
    private SerialPort serialScale;

    private String m_sPortScale;
    private OutputStream m_out;
    private InputStream m_in;

    private static final int SCALE_READY = 0;
    private static final int SCALE_READING = 1;
    private static final int SCALE_READINGDECIMALS = 2;

    private double m_dWeightBuffer;
    private double m_dWeightDecimals;
    private int m_iStatusScale;
    def buffer = "", tempBuffer = ""
    def miniDriver = [:]
    Pattern myMask

     /** Creates a new instance of ScaleComm */
    public def ComMan(String sPortPrinter) {
        m_sPortScale = sPortPrinter;
        m_out = null;
        m_in = null;

        m_iStatusScale = SCALE_READY;
    }

    public def readWeight(command, miniDriver) {
        try {
            def rawWeight = _readWeight(command, miniDriver);
            def weight;

            //Si existe una máscara la aplica, si no, aplica una máscara default
            if (!miniDriver.mask != null && !miniDriver.mask.isEmpty()) {
                myMask = Pattern.compile(miniDriver.mask);
            }
            if (myMask == null)
                myMask = /[ ]{0,6}(?<peso>[0-9]*?.[0-9]*?)[^0-9\.]([A-Z0-9]*)/;


            weight = maskWeight(rawWeight, myMask);

            return weight;
        } catch (Exception e) {
            close();
            e.printStackTrace();
            return "0.860517";
        }
    }

    /**
     * Extraé los digitos del peso de la cadena proveniente de una báscula.
     * La expresión regular para extraer sólo los digitos del peso debe incluir el named group "peso".
     * La sintáxis phyton del named group sería "(?P<peso>___expresión del group___)", mientras que para groovy
     * sería: "(?<peso>___expresión del group___)", es decir sin la letra "P". Por ejemplo: (?<peso>.*)
     * @param rawWeight Cadena devuelta por la báscula
     * @param mask Expresión regular
     * @return
     */
    public def maskWeight(rawWeight, mask) {
        def matcher = rawWeight =~ mask;
        if(matcher.find()) {
            def weight = matcher.group("peso");
            return weight;
        }
        println("No se pudo aplicar máscara a lectura de la báscula"); // TODO Mover esto a un logger
        return rawWeight;
    }

    private def _readWeight(command, miniDriver) {

        try {
        this.miniDriver = miniDriver
        synchronized(this) {

            if (m_iStatusScale != SCALE_READY) {
                try {
                    wait(100);
                } catch (InterruptedException e) {
                }
                if (m_iStatusScale != SCALE_READY) {
                    m_iStatusScale = SCALE_READY;
                }
            }
            buffer = ""
            write(command.getBytes()); // $
            flush();
            m_iStatusScale = SCALE_READING;

            int waits = 0;
            try {
                while(m_iStatusScale == SCALE_READING) {
                    waits++;
                    wait(100);
                    println "esperando en método pesar (${buffer.size()})"
                    if(waits>3) { println "Tiempo excedido de método pesar, abortado."; throw new Exception("Abortar peso. Tiempo excedido"); }
                }
                //wait(500)
            } catch (InterruptedException e) {
            }

            if (m_iStatusScale == SCALE_READY) {
                println "buffer->"+buffer
                
                def retorno = buffer;

                return retorno;
            } else {
                m_iStatusScale = SCALE_READY;
                return "0.0"
            }
        }
        } catch(e) { Dialogos.error("Error al leer peso desde báscula", e) }
    }

    private void flush() {
        try {
            m_out.flush();
        } catch (IOException e) {
        }
    }

    private void write(byte[] data) {
        try {
            if (m_out == null) {
                m_PortIdPrinter = CommPortIdentifier.getPortIdentifier(miniDriver.port); 
                serialScale = (SerialPort) m_PortIdPrinter.open("PORTID", 2000);

                m_out = serialScale.getOutputStream(); 
                m_in = serialScale.getInputStream();

                serialScale.addEventListener(this);
                serialScale.notifyOnDataAvailable(true);

                def bits, stopBits, parity
                switch(miniDriver.bits) {
                    case "5": bits = SerialPort.DATABITS_5; break
                    case "6": bits = SerialPort.DATABITS_6; break
                    case "7": bits = SerialPort.DATABITS_7; break
                    case "8": bits = SerialPort.DATABITS_8; break
                }
                switch(miniDriver.stopBits) {
                    case "1"  : stopBits = SerialPort.STOPBITS_1  ; break
                    case "1.5": stopBits = SerialPort.STOPBITS_1_5; break
                    case "2"  : stopBits = SerialPort.STOPBITS_2  ; break
                }
                switch(miniDriver.parity) {
                    case "None" : parity  = SerialPort.PARITY_NONE; break
                    case "Odd"  : parity  = SerialPort.PARITY_ODD;  break
                    case "Even" : parity  = SerialPort.PARITY_EVEN; break
                    case "Mark" : parity  = SerialPort.PARITY_MARK; break
                    case "Space": parity  = SerialPort.PARITY_SPACE;break
                }

                serialScale.setSerialPortParams(miniDriver.baud, bits, stopBits, parity);
                serialScale.setDTR(false);
                serialScale.setRTS(false);
            }
            m_out.write(data);
        } catch (NoSuchPortException e) {
            Dialogos.lanzarAlerta("Puerto de báscula inválido")
        } catch (PortInUseException e) {
            Dialogos.lanzarAlerta("Puerto de báscula en uso por otra aplicación o proceso")
        } catch (UnsupportedCommOperationException e) {
            Dialogos.error("Excepción al leer báscula ${e.getMessage()}", e);
        } catch (TooManyListenersException e) {
            Dialogos.error("Excepción al leer báscula ${e.getMessage()}", e);
        } catch (IOException e) {
            Dialogos.error("Excepción al leer báscula ${e.getMessage()}", e);
        }
    }

    public void serialEvent(SerialPortEvent e) {

        int data;
        def in7 = m_in

        try
        {
            m_iStatusScale = SCALE_READING;
            println "comienza try de recopilar letras del puerto serial"
            int len = 0;
            while ( ( data = in7.read()) > -1 )
            {
                println "comienza while de recopilar letras del puerto serial"
                //println "a "+data
                if ( data as char == (miniDriver.stopChar as char) ) {
                    println "salto de línea, termina la recolección de letras del puerto serial"
                    break;
                }
                println "se va a agregar una letra a la recolección"
                buffer += data as char;
                println "se agregó una letra a la recolección"
            }

            println "terminó la llamada al handler"
        }
        catch ( Exception ex2 )
        {
            if(ex2.getMessage() == "No error in readByte") return;
            println "se encontró una excepción"
            ex2.printStackTrace();
            Dialogos.error("Error al leer desde el puerto serial", ex2)
            //System.exit(-1);
        } finally {
            println("Llamada a finally para poner SCALE_READY");
            m_iStatusScale = SCALE_READY;
        }
    }

    public void close() {
        if(serialScale == null) return ;
        m_in.close();
        m_out.close();
        serialScale.close();
    }

}


