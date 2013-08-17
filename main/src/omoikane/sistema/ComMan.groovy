 /* Author Phesus        //////////////////////////////
 *  ORC,ACR             /////////////
 *                     /////////////
 *                    /////////////
 *                   /////////////
 * //////////////////////////////                   */

package omoikane.sistema

import gnu.io.*

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

    /** Creates a new instance of ScaleComm */
    public def ComMan(String sPortPrinter) {
        m_sPortScale = sPortPrinter;
        m_out = null;
        m_in = null;

        m_iStatusScale = SCALE_READY;
    }

    public def readWeight(command, miniDriver) {

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

            int waits = 0;
            try {
                while(buffer.size() < 13) {
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
                
                def retorno = (buffer =~ /[ ]{0,6}([0-9]*?.[0-9]*?)[^0-9\.]([A-Z0-9]*)/)
                

                println "2->"+retorno
                return retorno[0][1]
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

        /*
        // Determine type of event.
        switch (e.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                synchronized(this) {
                try {
                    tempBuffer = ""
                    println "borrando buffer"

                    for (int i = 0; i < 13; i++) {
                        for(int j = 0; m_in.available() == 0 && j < 10; j++) {
                            try { wait(100); println "esperando en el evento..." } catch (InterruptedException exc) { }
                        }
                        //println "$i .. "
                        //println "Dat Disponible"
                        int b = m_in.read();
                        //println "leído: $b"
                        println "leído [$i] -> $b "
                        
                        //if (b == ((miniDriver.stopChar[0] as int) as char)) { // CR ASCII
                        //    // Fin de lectura
                        //    synchronized (this) {
                        //        m_iStatusScale = SCALE_READY;
                        //        println "scale ready"
                        //        buffer = tempBuffer
                        //        notifyAll();
                        //    }
                        //
                        //} else {
                        //    synchronized(this) {
                        //    tempBuffer += (b as char)
                        //
                        //    }
                        //}
                        
                        //*******synchronized(this) {
                            tempBuffer += (b as char)
                        //*******}
                    }

                    println ("dat dispo: ${m_in.available()}")
                    m_in.read()
                    println "asignando tempbuffer a buffer"
                    buffer = tempBuffer

                } catch (IOException eIO) { Dialogos.error("Excepción al pesar${eIO.getMessage()}", eIO) }
                }

                break;

        } 
        */
        //if(e.getEventType() == SerialPortEvent.
        int data;
        def in7 = m_in

        try
        {
            println "comienza try de recopilar letras del puerto serial"
            int len = 0;
            while ( ( data = in7.read()) > -1 )
            {
                println "comienza while de recopilar letras del puerto serial"
                //println "a "+data
                if ( data == (13 as char) ) {
                    println "salto de línea, termina la recolección de letras del puerto serial"
                    break;
                }
                println "se va a agregar una letra a la recolección"
                buffer += data as char;
                println "se agregó una letra a la recolección"
            }
            //println "se llamará al handler de la cadena completa del escáner"
            //println new String(buffer,0,len)
            println "terminó la llamada al handler"
        }
        catch ( Exception ex2 )
        {
            if(ex2.getMessage() == "No error in readByte") return;
            println "se encontró una excepción"
            ex2.printStackTrace();
            Dialogos.error("Error al leer desde el puerto serial", ex2)
            //System.exit(-1);
        }
    }

    public void close() {
        if(serialScale == null) return ;
        m_in.close();
        m_out.close();
        serialScale.close();
    }

}


