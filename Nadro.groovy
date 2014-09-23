import com.net.cds_oroDemo.*;


//1.- Login
         CDS_Oro valor = new CDS_Oro();
       String session = valor.Login();
       if(session.length()>0)
            System.out.println(session);      // 3b6f18487a003feaf1c28d801e66553fac208320
       else
           System.out.println(valor.getError());                                                                                        
       
       
       
       //2.- Validar tarjeta
       if(session.length()>0)
       {
           String card= valor.GetInfoCard(session, "9981299000012");
           System.out.println(card);
           // ALICIA,VALDES,ENRIQUES,2013-08-22T00:00:00
           // Nombre,paterno, materno, vigencia
       }//1.- Login
        valor = new CDS_Oro();
       session = valor.Login();
       if(session.length()>0)
            System.out.println(session);      // 3b6f18487a003feaf1c28d801e66553fac208320
       else
           System.out.println(valor.getError());                                                                                        
       
       
       
       //2.- Validar tarjeta
       if(session.length()>0)
       {
           String card= valor.GetInfoCard(session, "9981299000012");
           System.out.println(card);
           // ALICIA,VALDES,ENRIQUES,2013-08-22T00:00:00
           // Nombre,paterno, materno, vigencia
       }
       
       if(session.length()>0)
       {
         String Bonus= valor.GetBonusProductList(session, "9981299000012","8904091147304,3:7506200700052,2");
                                                            // Tarjeta     // Producto (sku,piezas: _ _ _ :N) 
           System.out.println(Bonus);  
       }