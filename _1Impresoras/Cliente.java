package _1Impresoras;

import java.util.Random;

public class Cliente extends Thread{
	private static Random rnd = new Random();
	private int id;
	private Gestor gestor;
	
	public Cliente(int id, Gestor gestor){
		this.id = id;
		this.gestor = gestor;
	}
	
	public void run(){
		//while(true){
			try {
				Thread.sleep(rnd.nextInt(100));
				gestor.cogeImpresora(id);
				System.out.println("Cliente " + id + " imprimiendo");
				Thread.sleep(rnd.nextInt(300)); 
				gestor.sueltaImpresora(id);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//}
	}
}
