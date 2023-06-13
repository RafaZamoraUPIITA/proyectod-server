/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiservers;

import java.net.*; 
import java.io.*; 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Multiservers extends Thread{
   static int NoClients=0; 
   static int NoRegistro=1;
   
   public void run(){ 
       DB DataBase = new DB();
       try {
           ResultSet rs;
           rs =DataBase.runSql("select * from registros");
           if (rs!=null){
            int count = 0;
      while (rs.next()) {
        count++;
      } 
      NoRegistro=count +1;
           }
       } catch (SQLException ex) {
           Logger.getLogger(Multiservers.class.getName()).log(Level.SEVERE, null, ex);
       }
	ServerSocket socketServidor = null; 
	Socket socketCliente = null;
                            String IP = null;
            try 
{
	IP = InetAddress.getLocalHost().getHostAddress();
	
}
catch(Exception e) 
{
	e.printStackTrace();
}
            System.out.println("Direccion: " + IP);

	try{ 
	   socketServidor = new ServerSocket (12345); 
	}catch (Exception e){ 
	   System.out.println ("Error : "+ e.toString()); 
	   System.exit (0); 
	} 
		
	System.out.println ("Server started... (Socket TCP) **Tracker en red**");	
	int enproceso=1;
	while(enproceso==1){ 
		try{ 
	   		socketCliente = socketServidor.accept();
			MultiServerThread controlThread=new MultiServerThread(socketCliente);
			controlThread.start();
	   	}catch (Exception e){ 
	    	System.out.println ("Error : " + e.toString()); 
                    try { 
                        socketServidor.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Multiservers.class.getName()).log(Level.SEVERE, null, ex);
                    }
			System.exit (0); 
	   	} 
	}
	System.out.println("Finalizando Servidor...");

   } 
} 
