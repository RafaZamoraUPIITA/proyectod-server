package multiservers;

import java.net.*; 
import java.io.*; 
public class ClientFullDuplex { 
	
public String Preguntar( String IP) throws IOException{
            
		Socket cliente = null; 
		PrintWriter escritor = null; 
		String DatosEnviados = null; 
		BufferedReader entrada=null;
		
		String maquina; 
		int puerto; 
		BufferedReader DatosTeclado = new BufferedReader ( new InputStreamReader (System.in)); 

		
			maquina = IP; 
			puerto = 8080; 
			System.out.println ("Establecidos valores por defecto:\nEQUIPO = localhost\nPORT = 8080"); 
		
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
		
		System.out.println("Conectado con el Servidor. Listo para enviar datos...");
                String reg= "Actualizar";
		escritor.println (reg);
                line = entrada.readLine();
	        System.out.println(line);
	
		System.out.println ("Finalizada conexion con el servidor"); 
		try{ 
			escritor.close(); 
		}catch (Exception e){}
                return line;
}
} 

