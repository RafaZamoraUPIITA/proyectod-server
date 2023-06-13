/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiservers;

import java.net.*;
import java.io.*;
import java.sql.ResultSet;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiServerThread extends Thread {
   private Socket socket = null;
   Socket cliente = null; 
   PrintWriter escritor = null; 
   String DatosEnviados = null; 
   BufferedReader entrada=null;
   
   

   public MultiServerThread(Socket socket) {
      super("MultiServerThread");
      this.socket = socket;
      Multiservers.NoClients++;
   }
   
   public String conexion(String maquina, int puerto, String peticion) throws IOException{
     
       try{ 
			cliente = new Socket (maquina,puerto); 
		}catch (Exception e){ 
			System.out.println ("Fallo : "+ e.toString()); 
			System.exit (0); 
		}
       try{ 
			escritor = new PrintWriter(cliente.getOutputStream(), true);
			entrada=new BufferedReader(new InputStreamReader(cliente.getInputStream()));
 
		}catch (Exception e){ 
			System.out.println ("Fallo : "+ e.toString()); 
			cliente.close(); 
			System.exit (0); 
		} 
       String line;
       DatosEnviados = peticion; 
			escritor.println (DatosEnviados); 
			line = entrada.readLine();
                        try{ 
			escritor.close(); 
		}catch (Exception e){}
       System.out.println("Respuesta obtenida");
       return line;
   }
   

   public void run() {

      try {
         PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         String lineIn, lineOut;
		
	     while((lineIn = entrada.readLine()) != null){
            System.out.println("Received: "+lineIn);
            escritor.flush();
            if(lineIn.equals("FIN")){
               Multiservers.NoClients--;
			      break;
			   }
            else if(lineIn.equals("#")){
               escritor.println("Numero de Clientes conectados "+ Multiservers.NoClients);
               escritor.flush(); 
            }
            else if(lineIn.startsWith("Busqueda")){
              String sq="select IP from registros where Archivo = ";
              String IPs = "";
              String[] parts = lineIn.split("/");
              sq = sq + "'" + parts[1] + "'" + " && Porcentaje >= 50";
              DB DataBase= new DB();
              ResultSet rs;
              rs = DataBase.runSql(sq);
              while(rs.next()){
                  IPs = IPs + rs.getString(1) + " ";
              }
              if(IPs.equals("")){
                  IPs="No existe el archivo en red";
              }
              DataBase.finalize();
                
              escritor.println(IPs);
              escritor.flush(); 
            }
            else if(lineIn.startsWith("Tabla")){ 
              String Archivos= "";
              DB DataBase= new DB();
              ResultSet rs;
              rs = DataBase.runSql("select Archivo from registros where porcentaje >= 50 group by Archivo");
             
              while(rs.next()){
                  Archivos = Archivos + rs.getString(1) + " ";
              }
              if(Archivos.equals("")){
                  Archivos="No hay Archivos en red";
              }
              
              DataBase.finalize();
              escritor.println(Archivos);
              escritor.flush(); 
            }
            else if (lineIn.startsWith("Registro")) {
              String sq="insert into peers (IP, Host_name) values(";
              String sq2= "select IP from peers where IP = ";
              String x="";
              String[] parts = lineIn.split("/");
              sq = sq + "'" + parts[1] + "', " + "'" + parts[2] + "')";
              sq2 = sq2 + "'" + parts[1] + "'";
              DB DataBase= new DB();
              ResultSet rs;
              rs = DataBase.runSql(sq2);
              while(rs.next()){
                  x=x + rs.getString(1);
              }
              if (x.equals("")){
              DataBase.exe(sq); 
              DataBase.finalize();
              escritor.println("Registro exitoso");
              escritor.flush();
              }else{
              DataBase.finalize();
              escritor.println("El ususario ya existe");
              escritor.flush();     
              }
                }
            else if(lineIn.startsWith("RegAr")){
              String sq="insert into registros (no_registro, IP, Archivo, Porcentaje)values( ";  
              String[] parts = lineIn.split("/");
              sq = sq + "'"+ Multiservers.NoRegistro+ "', "+"'" + parts[1] + "', " + "'" + parts[2] +"', "+parts[3] +")";
               DB DataBase= new DB();
               DataBase.exe(sq); 
               DataBase.finalize();
               Multiservers.NoRegistro++;
               escritor.println("Registro Exitoso");
               escritor.flush();
            }
            else{
               escritor.println(lineIn);
               escritor.flush();
            }
         } 
         try{		
            entrada.close();
            escritor.close();
            socket.close();
         }catch(Exception e){ 
            System.out.println ("Error : " + e.toString()); 
            socket.close();
            System.exit (0); 
   	   } 
      }catch (IOException e) {
         e.printStackTrace();
      } catch (Throwable ex) {
           Logger.getLogger(MultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
} 

