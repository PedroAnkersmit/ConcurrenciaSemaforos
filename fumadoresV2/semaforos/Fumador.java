package fumadoresV2.semaforos;
import java.util.*;
public class Fumador extends Thread{
	private static Random r = new Random();
	
	private Mesa m;
	
	private int id;
	public Fumador(Mesa m,int id){
		this.m = m;
		this.id = id; //el identificador determina el tipo de ingrediente que tiene
	}

	//Devuelve el ingrediente que tiene ese fumador
	private String ingrediente(){
		String nombre=null;
		if (id == 0){
			nombre = "tabaco";
		}else if (id == 1){
			nombre = "papel";
		}else if (id == 2){
			nombre = "cerillas";
		}	
		return nombre;
	}
	
	public void run(){
		while (true){	
			try {
				m.quieroFumar(id); //protocolo de entrada
				System.out.println("Fumador " + id + " tiene "+ ingrediente() + " y puede fumar");
				Thread.sleep(r.nextInt(200));
				m.finFumar(id); //protocolo de salida
				System.out.println("Fumador "+id+" termina de fumar");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
